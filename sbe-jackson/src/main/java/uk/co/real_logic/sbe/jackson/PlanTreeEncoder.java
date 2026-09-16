/*
 * Copyright 2013-2025 Real Logic Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.real_logic.sbe.jackson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.agrona.MutableDirectBuffer;
import uk.co.real_logic.sbe.PrimitiveType;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.util.Iterator;
import java.util.Map;

/**
 * One encode (or sizing) pass of a {@link JsonNode} tree over a {@link MessagePlan}. Walks the plan, never the
 * JSON: {@code obj.get(name)} per field, {@code ArrayNode.size()} for counts, no iterators on the happy path.
 * Unknown properties are detected per {@code ObjectNode} by comparing the recognised count with
 * {@code size()}; the slow path that names the offending key runs only on mismatch.
 * <p>
 * The same walk runs in sizing mode (no destination, limit {@code Integer.MAX_VALUE}) for
 * {@link SbeJsonEncoder#encodedLength}; every position is still bounds checked so a message that would exceed
 * the supported {@code int} size is rejected with {@link ErrorCode#DESTINATION_OVERFLOW} instead of wrapping.
 * Var-data lengths are validated before any payload is materialised.
 * <p>
 * One instance is retained per thread-confined {@link SbeJsonEncoder} (via {@link WalkContext#treeEncoder}).
 */
final class PlanTreeEncoder
{
    private static final BigInteger TWO_POW_64 = BigInteger.ONE.shiftLeft(64);
    private static final BigInteger MAX_UINT64 = TWO_POW_64.subtract(BigInteger.ONE);

    private final MessagePlan plan;
    private final SbeJson config;
    private final Limits limits;
    private final WalkContext ctx;
    private MutableDirectBuffer dst;
    private int limit;
    private boolean sizing;

    PlanTreeEncoder(final MessagePlan plan, final SbeJson config, final WalkContext ctx)
    {
        this.plan = plan;
        this.config = config;
        this.limits = config.limits();
        this.ctx = ctx;
    }

    MessagePlan plan()
    {
        return plan;
    }

    int encode(final JsonNode body, final MutableDirectBuffer dst, final int offset, final int available)
    {
        if (offset < 0 || available < 0 || available > dst.capacity() - offset)
        {
            throw error(ErrorCode.DESTINATION_OVERFLOW, null, offset,
                "destination [" + offset + ", " + offset + " + " + available + ") outside buffer capacity " +
                dst.capacity());
        }
        this.dst = dst;
        this.limit = offset + available;
        this.sizing = false;

        return walk(body, offset) - offset;
    }

    int encodedLength(final JsonNode body)
    {
        this.dst = null;
        this.limit = Integer.MAX_VALUE;
        this.sizing = true;

        return walk(body, 0);
    }

    private int walk(final JsonNode body, final int offset)
    {
        ctx.reset();
        final ObjectNode root = requireObject(body, null, offset);
        final HeaderLayout header = config.headerLayout();
        final int headerLength = header.encodedLength();
        ensure(offset, headerLength, null);
        if (!sizing)
        {
            header.write(dst, offset, plan.blockLength, plan.templateId, plan.schemaId, plan.schemaVersion);
        }

        return encodeEntry(plan.rootStart, plan.rootEnd, root, offset + headerLength, plan.blockLength);
    }

    private int encodeEntry(
        final int childStart, final int childEnd, final ObjectNode obj, final int entryBase, final int blockLength)
    {
        ensure(entryBase, blockLength, null);
        if (!sizing)
        {
            dst.setMemory(entryBase, blockLength, (byte)0);
        }

        final FieldPlan[] fields = plan.fields;
        int recognised = 0;
        int cursor = entryBase + blockLength;

        for (int i = childStart; i < childEnd; i++)
        {
            final FieldPlan f = fields[i];
            final JsonNode node = obj.get(f.name);
            if (null != node)
            {
                recognised++;
            }

            switch (f.kind)
            {
                case FieldPlan.KIND_GROUP:
                    cursor = encodeGroup(f, node, cursor);
                    break;

                case FieldPlan.KIND_VAR_DATA:
                    cursor = encodeVarData(f, node, cursor);
                    break;

                default:
                    encodeBlockField(f, node, entryBase);
                    break;
            }
        }

        checkUnknownProperties(obj, recognised, childStart, childEnd, entryBase);

        return cursor;
    }

    private void checkUnknownProperties(
        final ObjectNode obj, final int recognised, final int childStart, final int childEnd, final int offset)
    {
        if (UnknownProperties.ERROR != config.unknownProperties() || recognised == obj.size())
        {
            return;
        }

        final FieldPlan[] fields = plan.fields;
        final Iterator<String> names = obj.fieldNames();
        while (names.hasNext())
        {
            final String name = names.next();
            boolean known = false;
            for (int i = childStart; i < childEnd; i++)
            {
                if (fields[i].name.equals(name))
                {
                    known = true;
                    break;
                }
            }
            if (!known)
            {
                throw error(ErrorCode.UNKNOWN_PROPERTY, null, offset, "unknown property '" + name + "'");
            }
        }
    }

    private void encodeBlockField(final FieldPlan f, final JsonNode node, final int entryBase)
    {
        final int index = entryBase + f.offset;
        if (f.constant)
        {
            // Any supplied value, including an explicit null, must match; only omission is exempt.
            if (null != node)
            {
                validateConstant(f, node, index);
            }
            return;
        }

        if (null == node || node.isNull())
        {
            if (!f.optional)
            {
                throw error(ErrorCode.MISSING_REQUIRED, f, index, "required field is missing or null");
            }
            writeNull(f, index);
            return;
        }

        switch (f.kind)
        {
            case FieldPlan.KIND_INT:
                putLong(f, index, signedValue(f, node, index));
                break;

            case FieldPlan.KIND_UINT64:
                putLong(f, index, uint64Value(f, node, index));
                break;

            case FieldPlan.KIND_FLOAT:
            case FieldPlan.KIND_DOUBLE:
            {
                final double value = floatingValue(f, node, index);
                if (!sizing)
                {
                    putNumeric(f, index, 0, value);
                }
                break;
            }

            case FieldPlan.KIND_CHAR:
                encodeChar(f, node, index);
                break;

            case FieldPlan.KIND_CHAR_ARRAY:
                encodeCharArray(f, node, index);
                break;

            case FieldPlan.KIND_NUMERIC_ARRAY:
                encodeNumericArray(f, node, index);
                break;

            case FieldPlan.KIND_ENUM:
                putLong(f, index, enumValue(f, node, index));
                break;

            case FieldPlan.KIND_BIT_SET:
                putLong(f, index, bitSetValue(f, node, index));
                break;

            case FieldPlan.KIND_COMPOSITE:
                encodeComposite(f, node, entryBase);
                break;

            default:
                throw new IllegalStateException("unexpected kind " + f.kind + " for " + f);
        }
    }

    private void encodeComposite(final FieldPlan composite, final JsonNode node, final int entryBase)
    {
        final ObjectNode obj = requireObject(node, composite, entryBase + composite.offset);
        final FieldPlan[] fields = plan.fields;
        int recognised = 0;
        ctx.push(composite.index);
        for (int i = composite.childStart; i < composite.childEnd; i++)
        {
            final FieldPlan member = fields[i];
            final JsonNode memberNode = obj.get(member.name);
            if (null != memberNode)
            {
                recognised++;
            }
            encodeBlockField(member, memberNode, entryBase);
        }
        checkUnknownProperties(
            obj, recognised, composite.childStart, composite.childEnd, entryBase + composite.offset);
        ctx.pop();
    }

    private void writeNull(final FieldPlan f, final int index)
    {
        if (sizing)
        {
            return;
        }

        switch (f.kind)
        {
            case FieldPlan.KIND_INT:
            case FieldPlan.KIND_UINT64:
            case FieldPlan.KIND_ENUM:
            case FieldPlan.KIND_CHAR:
                WireTypes.putLong(dst, index, f.primitiveType, f.byteOrder, f.nullValueLong);
                break;

            case FieldPlan.KIND_FLOAT:
                dst.putFloat(index, (float)f.nullValueDouble, f.byteOrder);
                break;

            case FieldPlan.KIND_DOUBLE:
                dst.putDouble(index, f.nullValueDouble, f.byteOrder);
                break;

            case FieldPlan.KIND_NUMERIC_ARRAY:
                for (int i = 0; i < f.arrayLength; i++)
                {
                    putNumeric(f, index + i * f.primitiveType.size(), f.nullValueLong, f.nullValueDouble);
                }
                break;

            default:
                // char arrays and bit sets: the block was zero-filled already.
                break;
        }
    }

    private void putNumeric(final FieldPlan f, final int index, final long longValue, final double doubleValue)
    {
        switch (f.primitiveType)
        {
            case FLOAT:
                dst.putFloat(index, (float)doubleValue, f.byteOrder);
                break;

            case DOUBLE:
                dst.putDouble(index, doubleValue, f.byteOrder);
                break;

            default:
                WireTypes.putLong(dst, index, f.primitiveType, f.byteOrder, longValue);
                break;
        }
    }

    private void putLong(final FieldPlan f, final int index, final long value)
    {
        if (!sizing)
        {
            WireTypes.putLong(dst, index, f.primitiveType, f.byteOrder, value);
        }
    }

    /*
     * Signed integer value for an int / uint8..uint32 field: integral node within the schema range, or the null
     * sentinel of an optional field (so decoded sentinels round-trip).
     */
    private long signedValue(final FieldPlan f, final JsonNode node, final int index)
    {
        if (!node.isIntegralNumber())
        {
            throw error(ErrorCode.TYPE_MISMATCH, f, index, "expected an integral number but found " + describe(node));
        }
        if (!node.canConvertToLong())
        {
            throw error(ErrorCode.OUT_OF_RANGE, f, index, "value " + node + " does not fit a 64-bit integer");
        }

        final long value = node.longValue();
        if (f.optional && value == f.nullValueLong)
        {
            return value;
        }
        if (value < f.minValueLong || value > f.maxValueLong)
        {
            throw error(ErrorCode.OUT_OF_RANGE, f, index,
                "value " + value + " outside [" + f.minValueLong + ", " + f.maxValueLong + "]");
        }

        return value;
    }

    /*
     * Raw value for a uint64 field: unsigned within the schema range, or the null sentinel of an optional field.
     */
    private long uint64Value(final FieldPlan f, final JsonNode node, final int index)
    {
        final long raw = unsignedRaw(f, node, index);
        if (f.optional && raw == f.nullValueLong)
        {
            return raw;
        }
        if (Long.compareUnsigned(raw, f.minValueLong) < 0 || Long.compareUnsigned(raw, f.maxValueLong) > 0)
        {
            throw error(ErrorCode.OUT_OF_RANGE, f, index,
                "value " + Long.toUnsignedString(raw) + " outside [" + Long.toUnsignedString(f.minValueLong) +
                ", " + Long.toUnsignedString(f.maxValueLong) + "]");
        }

        return raw;
    }

    /*
     * Parse an unsigned 64-bit value from an integral node (including BigIntegerNode) or a decimal string.
     * Negative values and values above 2^64 - 1 are OUT_OF_RANGE.
     */
    private long unsignedRaw(final FieldPlan f, final JsonNode node, final int index)
    {
        final BigInteger value;
        if (node.isIntegralNumber())
        {
            if (node.canConvertToLong())
            {
                final long v = node.longValue();
                if (v < 0)
                {
                    throw error(ErrorCode.OUT_OF_RANGE, f, index, "negative value " + v + " into an unsigned field");
                }
                return v;
            }
            value = node.bigIntegerValue();
        }
        else if (node.isTextual())
        {
            try
            {
                value = new BigInteger(node.textValue().trim());
            }
            catch (final NumberFormatException ex)
            {
                throw error(ErrorCode.TYPE_MISMATCH, f, index,
                    "expected an unsigned decimal string but found '" + node.textValue() + "'");
            }
        }
        else
        {
            throw error(ErrorCode.TYPE_MISMATCH, f, index,
                "expected an integral number or decimal string but found " + describe(node));
        }

        if (value.signum() < 0 || value.compareTo(MAX_UINT64) > 0)
        {
            throw error(ErrorCode.OUT_OF_RANGE, f, index, "value " + value + " outside [0, " + MAX_UINT64 + "]");
        }

        return value.longValue();
    }

    private double floatingValue(final FieldPlan f, final JsonNode node, final int index)
    {
        final double value;
        if (node.isNumber())
        {
            value = node.doubleValue();
        }
        else if (node.isTextual())
        {
            switch (node.textValue())
            {
                case "NaN":
                    value = Double.NaN;
                    break;

                case "Infinity":
                case "+Infinity":
                    value = Double.POSITIVE_INFINITY;
                    break;

                case "-Infinity":
                    value = Double.NEGATIVE_INFINITY;
                    break;

                default:
                    throw error(ErrorCode.TYPE_MISMATCH, f, index,
                        "expected a number or \"NaN\" / \"Infinity\" / \"-Infinity\" but found '" +
                        node.textValue() + "'");
            }
        }
        else
        {
            throw error(ErrorCode.TYPE_MISMATCH, f, index, "expected a number but found " + describe(node));
        }

        if (Double.isFinite(value) && (value < f.minValueDouble || value > f.maxValueDouble))
        {
            throw error(ErrorCode.OUT_OF_RANGE, f, index,
                "value " + value + " outside [" + f.minValueDouble + ", " + f.maxValueDouble + "]");
        }

        return value;
    }

    private void encodeChar(final FieldPlan f, final JsonNode node, final int index)
    {
        final String text = requireText(f, node, index);
        if (1 != text.length())
        {
            throw error(ErrorCode.OUT_OF_RANGE, f, index, "expected exactly one character but found " + text.length());
        }
        final char c = text.charAt(0);
        if (c > 0xFF || (FieldPlan.ENC_ASCII == f.characterEncodingTag && c > 0x7F))
        {
            throw error(ErrorCode.TYPE_MISMATCH, f, index, "character U+" + Integer.toHexString(c) +
                " cannot be encoded in a single byte char field");
        }
        putLong(f, index, c);
    }

    private void encodeCharArray(final FieldPlan f, final JsonNode node, final int index)
    {
        final String text = requireText(f, node, index);
        if (FieldPlan.ENC_ASCII == f.characterEncodingTag)
        {
            final int length = text.length();
            if (length > f.arrayLength)
            {
                throw error(ErrorCode.OUT_OF_RANGE, f, index,
                    "string of " + length + " characters exceeds char[" + f.arrayLength + "]");
            }
            for (int i = 0; i < length; i++)
            {
                final char c = text.charAt(i);
                if (c > 0x7F)
                {
                    throw error(ErrorCode.TYPE_MISMATCH, f, index,
                        "character U+" + Integer.toHexString(c) + " is not ASCII");
                }
                if (!sizing)
                {
                    dst.putByte(index + i, (byte)c);
                }
            }
        }
        else
        {
            final byte[] bytes = boundedBytes(f, text, index, f.arrayLength, ErrorCode.OUT_OF_RANGE);
            if (!sizing)
            {
                dst.putBytes(index, bytes);
            }
        }
    }

    private void encodeNumericArray(final FieldPlan f, final JsonNode node, final int index)
    {
        if (!node.isArray())
        {
            throw error(ErrorCode.TYPE_MISMATCH, f, index, "expected an array but found " + describe(node));
        }
        if (node.size() != f.arrayLength)
        {
            throw error(ErrorCode.OUT_OF_RANGE, f, index,
                "expected " + f.arrayLength + " elements but found " + node.size());
        }

        final int size = f.primitiveType.size();
        for (int i = 0; i < f.arrayLength; i++)
        {
            final JsonNode element = node.get(i);
            final int elementIndex = index + i * size;
            switch (f.primitiveType)
            {
                case FLOAT:
                case DOUBLE:
                {
                    final double value = floatingValue(f, element, elementIndex);
                    if (!sizing)
                    {
                        putNumeric(f, elementIndex, 0, value);
                    }
                    break;
                }

                case UINT64:
                    putLong(f, elementIndex, uint64Value(f, element, elementIndex));
                    break;

                default:
                    putLong(f, elementIndex, signedValue(f, element, elementIndex));
                    break;
            }
        }
    }

    private long enumValue(final FieldPlan f, final JsonNode node, final int index)
    {
        if (node.isTextual())
        {
            final int valueIndex = f.enumNameToIndex.getValue(node.textValue());
            if (valueIndex < 0)
            {
                throw error(ErrorCode.UNKNOWN_ENUM, f, index, "unknown enum name '" + node.textValue() + "'");
            }
            return f.enumValues[valueIndex];
        }
        if (node.isIntegralNumber())
        {
            final PrimitiveType type = f.primitiveType;
            if (PrimitiveType.UINT64 == type)
            {
                return unsignedRaw(f, node, index);
            }
            if (!node.canConvertToLong())
            {
                throw error(ErrorCode.OUT_OF_RANGE, f, index, "enum value " + node + " does not fit " + type);
            }
            final long raw = node.longValue();
            final long min = PrimitiveType.CHAR == type ? 0 : type.minValue().longValue();
            final long max = PrimitiveType.CHAR == type ? 0xFF : type.maxValue().longValue();
            if (raw < min || raw > max)
            {
                throw error(ErrorCode.OUT_OF_RANGE, f, index, "enum value " + raw + " does not fit " + type);
            }
            return raw;
        }

        throw error(ErrorCode.TYPE_MISMATCH, f, index, "expected an enum name or number but found " + describe(node));
    }

    private long bitSetValue(final FieldPlan f, final JsonNode node, final int index)
    {
        if (node.isIntegralNumber())
        {
            if (PrimitiveType.UINT64 == f.primitiveType)
            {
                return unsignedRaw(f, node, index);
            }
            if (!node.canConvertToLong())
            {
                throw error(ErrorCode.OUT_OF_RANGE, f, index, "mask " + node + " does not fit " + f.primitiveType);
            }
            final long mask = node.longValue();
            if (mask < 0 || mask > f.maxValueLong)
            {
                throw error(ErrorCode.OUT_OF_RANGE, f, index,
                    "mask " + mask + " does not fit " + f.primitiveType);
            }
            return mask;
        }
        if (node.isObject())
        {
            long mask = 0;
            final Iterator<Map.Entry<String, JsonNode>> entries = node.fields();
            while (entries.hasNext())
            {
                final Map.Entry<String, JsonNode> entry = entries.next();
                final int bit = f.choiceNameToBit.getValue(entry.getKey());
                if (bit < 0)
                {
                    throw error(ErrorCode.UNKNOWN_CHOICE, f, index, "unknown choice '" + entry.getKey() + "'");
                }
                if (!entry.getValue().isBoolean())
                {
                    throw error(ErrorCode.TYPE_MISMATCH, f, index,
                        "choice '" + entry.getKey() + "' expected a boolean but found " + describe(entry.getValue()));
                }
                if (entry.getValue().booleanValue())
                {
                    mask |= 1L << bit;
                }
            }
            return mask;
        }

        throw error(ErrorCode.TYPE_MISMATCH, f, index,
            "expected an integer mask or an object of booleans but found " + describe(node));
    }

    private void validateConstant(final FieldPlan f, final JsonNode node, final int index)
    {
        final boolean matches;
        switch (f.kind)
        {
            case FieldPlan.KIND_INT:
                matches = node.isIntegralNumber() && node.canConvertToLong() && node.longValue() == f.constLong;
                break;

            case FieldPlan.KIND_UINT64:
                matches = node.isIntegralNumber() && node.bigIntegerValue().equals(unsigned(f.constLong));
                break;

            case FieldPlan.KIND_FLOAT:
                matches = node.isNumber() && (float)node.doubleValue() == (float)f.constDouble;
                break;

            case FieldPlan.KIND_DOUBLE:
                matches = node.isNumber() && node.doubleValue() == f.constDouble;
                break;

            case FieldPlan.KIND_ENUM:
                if (node.isTextual())
                {
                    matches = node.textValue().equals(f.constString);
                }
                else if (PrimitiveType.UINT64 == f.primitiveType)
                {
                    matches = node.isIntegralNumber() && node.bigIntegerValue().equals(unsigned(f.constLong));
                }
                else
                {
                    matches = node.isIntegralNumber() && node.canConvertToLong() && node.longValue() == f.constLong;
                }
                break;

            default:
                matches = node.isTextual() && node.textValue().equals(f.constString);
                break;
        }

        if (!matches)
        {
            throw error(ErrorCode.CONSTANT_MISMATCH, f, index,
                "supplied " + node + " but the schema constant is " +
                (null != f.constString ? "'" + f.constString + "'" : constantDescription(f)));
        }
    }

    private static String constantDescription(final FieldPlan f)
    {
        switch (f.kind)
        {
            case FieldPlan.KIND_UINT64:
                return Long.toUnsignedString(f.constLong);

            case FieldPlan.KIND_FLOAT:
            case FieldPlan.KIND_DOUBLE:
                return String.valueOf(f.constDouble);

            default:
                return String.valueOf(f.constLong);
        }
    }

    private int encodeGroup(final FieldPlan g, final JsonNode node, final int dimensionOffset)
    {
        final int count;
        if (null == node || node.isNull())
        {
            count = 0;
        }
        else if (node.isArray())
        {
            count = node.size();
        }
        else
        {
            throw error(ErrorCode.TYPE_MISMATCH, g, dimensionOffset, "expected an array but found " + describe(node));
        }

        if (count < g.numInGroupMin || count > g.numInGroupMax)
        {
            throw error(ErrorCode.OUT_OF_RANGE, g, dimensionOffset,
                "numInGroup " + count + " outside [" + g.numInGroupMin + ", " + g.numInGroupMax + "]");
        }
        if (ctx.addGroupEntries(count) > limits.maxGroupEntries())
        {
            throw error(ErrorCode.LIMIT_EXCEEDED, g, dimensionOffset,
                "total group entries exceed maxGroupEntries " + limits.maxGroupEntries());
        }
        if (ctx.depth() + 1 > limits.maxDepth())
        {
            throw error(ErrorCode.LIMIT_EXCEEDED, g, dimensionOffset,
                "group nesting depth " + (ctx.depth() + 1) + " exceeds maxDepth " + limits.maxDepth());
        }

        ensure(dimensionOffset, g.dimensionSize, g);
        if (!sizing)
        {
            dst.setMemory(dimensionOffset, g.dimensionSize, (byte)0);
            WireTypes.putLong(
                dst, dimensionOffset + g.blockLengthOffset, g.blockLengthType, g.byteOrder, g.blockLength);
            WireTypes.putLong(
                dst, dimensionOffset + g.numInGroupOffset, g.numInGroupType, g.numInGroupByteOrder, count);
        }

        int cursor = dimensionOffset + g.dimensionSize;
        ctx.push(g.index);
        for (int i = 0; i < count; i++)
        {
            ctx.element(i);
            final ObjectNode entry = requireObject(node.get(i), null, cursor);
            cursor = encodeEntry(g.childStart, g.childEnd, entry, cursor, g.blockLength);
        }
        ctx.pop();

        return cursor;
    }

    /*
     * Var-data: the payload length is validated against the length type maximum, the remaining maxVarDataBytes
     * budget and the destination before any payload bytes are materialised.
     */
    private int encodeVarData(final FieldPlan v, final JsonNode node, final int lengthOffset)
    {
        ensure(lengthOffset, v.dataOffset, v);
        final int dataIndex = lengthOffset + v.dataOffset;

        final int length;
        if (null == node || node.isNull())
        {
            length = 0;
        }
        else if (FieldPlan.ENC_BINARY == v.characterEncodingTag)
        {
            length = encodeBinaryVarData(v, node, lengthOffset, dataIndex);
        }
        else if (FieldPlan.ENC_UTF8 == v.characterEncodingTag)
        {
            final String text = requireText(v, node, dataIndex);
            length = checkVarDataLength(v, Utf8.encodedLength(text), lengthOffset);
            if (!sizing)
            {
                Utf8.encode(text, dst, dataIndex);
            }
        }
        else if (FieldPlan.ENC_ASCII == v.characterEncodingTag)
        {
            final String text = requireText(v, node, dataIndex);
            if (!Utf8.isAscii(text))
            {
                throw error(ErrorCode.TYPE_MISMATCH, v, dataIndex, "string contains non-ASCII characters");
            }
            length = checkVarDataLength(v, text.length(), lengthOffset);
            if (!sizing)
            {
                for (int i = 0; i < length; i++)
                {
                    dst.putByte(dataIndex + i, (byte)text.charAt(i));
                }
            }
        }
        else
        {
            final String text = requireText(v, node, dataIndex);
            final long budget = Math.min(
                v.lengthMax, Math.min(limits.maxVarDataBytes() - ctx.varDataBytes(), limit - dataIndex));
            final byte[] bytes = boundedBytes(v, text, lengthOffset, budget, null);
            length = checkVarDataLength(v, bytes.length, lengthOffset);
            if (!sizing)
            {
                dst.putBytes(dataIndex, bytes);
            }
        }

        ctx.addVarDataBytes(length);
        if (!sizing)
        {
            dst.setMemory(lengthOffset, v.dataOffset, (byte)0);
            WireTypes.putLong(dst, lengthOffset + v.lengthOffset, v.lengthType, v.byteOrder, length);
        }

        return dataIndex + length;
    }

    private int encodeBinaryVarData(
        final FieldPlan v, final JsonNode node, final int lengthOffset, final int dataIndex)
    {
        final byte[] bytes;
        if (node.isBinary())
        {
            bytes = binaryValue(v, node, dataIndex);
        }
        else if (node.isTextual())
        {
            // Count payload bytes without padding or whitespace; binaryValue validates the base64 syntax.
            final String text = node.textValue();
            long decodedLength = 0;
            int unitIndex = 0;
            for (int i = 0; i < text.length(); i++)
            {
                final char c = text.charAt(i);
                if (c > ' ')
                {
                    if (unitIndex > 0 && '=' != c)
                    {
                        decodedLength++;
                    }
                    unitIndex = (unitIndex + 1) & 3;
                }
            }
            checkVarDataLength(v, decodedLength, lengthOffset);
            bytes = binaryValue(v, node, dataIndex);
        }
        else
        {
            throw error(ErrorCode.TYPE_MISMATCH, v, dataIndex, "expected base64 binary but found " + describe(node));
        }

        final int length = checkVarDataLength(v, bytes.length, lengthOffset);
        if (!sizing)
        {
            dst.putBytes(dataIndex, bytes);
        }

        return length;
    }

    /*
     * Encode text in a non ASCII / UTF-8 charset without materialising more than budget + 1 bytes. When the
     * result would exceed the budget, overflowCode is raised if given, otherwise the var-data budget checks
     * decide the code.
     */
    private byte[] boundedBytes(
        final FieldPlan f, final String text, final int index, final long budget, final ErrorCode overflowCode)
    {
        final CharsetEncoder encoder = f.charset.newEncoder();
        final long upperBound = (long)Math.ceil(text.length() * (double)encoder.maxBytesPerChar());
        final long scratch = Math.min(upperBound, Math.min(budget, Integer.MAX_VALUE - 1) + 1);
        final ByteBuffer out = ByteBuffer.allocate((int)Math.max(0, scratch));
        CoderResult result = encoder.encode(CharBuffer.wrap(text), out, true);
        if (result.isUnderflow())
        {
            result = encoder.flush(out);
        }
        if (result.isOverflow() || out.position() > budget)
        {
            if (null != overflowCode)
            {
                throw error(overflowCode, f, index, "string encodes to more than " + budget + " bytes");
            }
            checkVarDataLength(f, budget + 1, index);
        }
        if (result.isError())
        {
            throw error(ErrorCode.TYPE_MISMATCH, f, index, "string cannot be encoded in " + f.charset);
        }

        final byte[] bytes = new byte[out.position()];
        out.flip();
        out.get(bytes);

        return bytes;
    }

    /*
     * Check a var-data payload length against the length type maximum, the remaining var-data budget and the
     * destination, in that order, without consuming the budget.
     */
    private int checkVarDataLength(final FieldPlan v, final long length, final int lengthOffset)
    {
        if (length > v.lengthMax)
        {
            throw error(ErrorCode.OUT_OF_RANGE, v, lengthOffset,
                "var-data of " + length + " bytes exceeds the length type maximum " + v.lengthMax);
        }
        if (length > limits.maxVarDataBytes() - ctx.varDataBytes())
        {
            throw error(ErrorCode.LIMIT_EXCEEDED, v, lengthOffset,
                "total var-data bytes exceed maxVarDataBytes " + limits.maxVarDataBytes());
        }
        ensure(lengthOffset + v.dataOffset, length, v);

        return (int)length;
    }

    private byte[] binaryValue(final FieldPlan v, final JsonNode node, final int index)
    {
        try
        {
            return node.binaryValue();
        }
        catch (final IOException ex)
        {
            throw error(ErrorCode.TYPE_MISMATCH, v, index, "invalid base64: " + ex.getMessage());
        }
    }

    private ObjectNode requireObject(final JsonNode node, final FieldPlan f, final int index)
    {
        if (null == node || node.isNull())
        {
            throw error(ErrorCode.MISSING_REQUIRED, f, index, "required object is missing or null");
        }
        if (!node.isObject())
        {
            throw error(ErrorCode.TYPE_MISMATCH, f, index, "expected an object but found " + describe(node));
        }

        return (ObjectNode)node;
    }

    private String requireText(final FieldPlan f, final JsonNode node, final int index)
    {
        if (!node.isTextual())
        {
            throw error(ErrorCode.TYPE_MISMATCH, f, index, "expected a string but found " + describe(node));
        }

        return node.textValue();
    }

    /*
     * Check that length bytes at index fit before the limit. In sizing mode the limit is Integer.MAX_VALUE, so
     * this also rejects messages beyond the supported size instead of overflowing int arithmetic.
     */
    private void ensure(final int index, final long length, final FieldPlan f)
    {
        if (length > limit - index)
        {
            throw error(ErrorCode.DESTINATION_OVERFLOW, f, index,
                "need " + length + " bytes at " + index + " but only " + Math.max(0, limit - index) +
                (sizing ? " remain below the supported message size" : " available"));
        }
    }

    private SbeJsonException error(final ErrorCode code, final FieldPlan f, final int index, final String detail)
    {
        return ctx.error(plan, code, f, index, detail);
    }

    private static BigInteger unsigned(final long raw)
    {
        return raw >= 0 ? BigInteger.valueOf(raw) : BigInteger.valueOf(raw).add(TWO_POW_64);
    }

    private static String describe(final JsonNode node)
    {
        return node.getNodeType().name().toLowerCase() + " " + node;
    }
}
