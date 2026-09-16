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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import uk.co.real_logic.sbe.PrimitiveType;

/**
 * Interpreter backend: {@code switch (kind)} loops over the {@link FieldPlan} array of one template. The only
 * production backend in release 1. Immutable and shared; all per-walk state lives in the {@link WalkContext}.
 */
final class PlanMessageCodec implements MessageCodec
{
    private final MessagePlan plan;
    private final JacksonCaches caches;
    private final SbeJson config;
    private final Limits limits;

    PlanMessageCodec(final MessagePlan plan, final JacksonCaches caches, final SbeJson config)
    {
        this.plan = plan;
        this.caches = caches;
        this.config = config;
        this.limits = config.limits();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessagePlan plan()
    {
        return plan;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ObjectNode decodeCopy(
        final DirectBuffer buffer,
        final int bodyOffset,
        final int frameEnd,
        final int actingBlockLength,
        final int actingVersion,
        final WalkContext ctx)
    {
        ctx.reset();
        final ObjectNode root = ctx.factory.objectNode();
        decodeEntry(
            plan.rootStart, plan.rootEnd, buffer, bodyOffset, actingBlockLength, actingVersion, frameEnd, root, ctx);

        return root;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int encode(
        final JsonNode body,
        final MutableDirectBuffer dst,
        final int offset,
        final int available,
        final WalkContext ctx)
    {
        return treeEncoder(ctx).encode(body, dst, offset, available);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int encodedLength(final JsonNode body, final WalkContext ctx)
    {
        return treeEncoder(ctx).encodedLength(body);
    }

    /*
     * The tree encoder is retained on the thread-confined context so that one instance serves every call of
     * the owning SbeJsonEncoder.
     */
    private PlanTreeEncoder treeEncoder(final WalkContext ctx)
    {
        PlanTreeEncoder encoder = ctx.treeEncoder;
        if (null == encoder || encoder.plan() != plan)
        {
            encoder = new PlanTreeEncoder(plan, config, ctx);
            ctx.treeEncoder = encoder;
        }

        return encoder;
    }

    private int decodeEntry(
        final int childStart,
        final int childEnd,
        final DirectBuffer buffer,
        final int entryBase,
        final int actingBlockLength,
        final int actingVersion,
        final int frameEnd,
        final ObjectNode target,
        final WalkContext ctx)
    {
        if (actingBlockLength > frameEnd - entryBase)
        {
            throw ctx.error(plan, ErrorCode.FRAME_OVERFLOW, null, entryBase,
                "block length " + actingBlockLength + " exceeds frame by " +
                (actingBlockLength - (frameEnd - entryBase)) + " bytes");
        }

        final FieldPlan[] fields = plan.fields;
        int cursor = entryBase + actingBlockLength;

        for (int i = childStart; i < childEnd; i++)
        {
            final FieldPlan f = fields[i];
            if (f.sinceVersion > actingVersion)
            {
                continue;
            }

            switch (f.kind)
            {
                case FieldPlan.KIND_GROUP:
                    cursor = decodeGroup(f, buffer, cursor, actingVersion, frameEnd, target, ctx);
                    break;

                case FieldPlan.KIND_VAR_DATA:
                    cursor = decodeVarData(f, buffer, cursor, frameEnd, target, ctx);
                    break;

                case FieldPlan.KIND_COMPOSITE:
                    target.set(f.name, decodeComposite(f, buffer, entryBase, actingBlockLength, actingVersion, ctx));
                    break;

                default:
                    if (f.constant)
                    {
                        target.set(f.name, caches.constant(f.index));
                    }
                    else
                    {
                        checkInsideBlock(f, entryBase, actingBlockLength, ctx);
                        target.set(f.name, decodeLeaf(f, buffer, entryBase + f.offset, ctx.factory));
                    }
                    break;
            }
        }

        return cursor;
    }

    /*
     * Every present, non-constant leaf must fit inside the acting block of its scope. Composites are not checked
     * as a whole: a composite whose newer members are absent in the acting version is legitimately shorter.
     */
    private void checkInsideBlock(
        final FieldPlan f, final int entryBase, final int actingBlockLength, final WalkContext ctx)
    {
        if (f.offset + f.encodedLength > actingBlockLength)
        {
            throw ctx.error(plan, ErrorCode.FIELD_OUTSIDE_BLOCK, f, entryBase + f.offset,
                "field ends at " + (f.offset + f.encodedLength) + " but acting block length is " +
                actingBlockLength);
        }
    }

    private ObjectNode decodeComposite(
        final FieldPlan composite,
        final DirectBuffer buffer,
        final int entryBase,
        final int actingBlockLength,
        final int actingVersion,
        final WalkContext ctx)
    {
        final ObjectNode node = ctx.factory.objectNode();
        final FieldPlan[] fields = plan.fields;
        ctx.push(composite.index);
        for (int i = composite.childStart; i < composite.childEnd; i++)
        {
            final FieldPlan member = fields[i];
            if (member.sinceVersion > actingVersion)
            {
                continue;
            }
            if (member.constant)
            {
                node.set(member.name, caches.constant(member.index));
            }
            else if (FieldPlan.KIND_COMPOSITE == member.kind)
            {
                node.set(
                    member.name, decodeComposite(member, buffer, entryBase, actingBlockLength, actingVersion, ctx));
            }
            else
            {
                checkInsideBlock(member, entryBase, actingBlockLength, ctx);
                node.set(member.name, decodeLeaf(member, buffer, entryBase + member.offset, ctx.factory));
            }
        }
        ctx.pop();

        return node;
    }

    private JsonNode decodeLeaf(
        final FieldPlan f, final DirectBuffer buffer, final int index, final JsonNodeFactory factory)
    {
        switch (f.kind)
        {
            case FieldPlan.KIND_INT:
            {
                final long value = WireTypes.getLong(buffer, index, f.primitiveType, f.byteOrder);
                if (f.optional && value == f.nullValueLong)
                {
                    return factory.nullNode();
                }
                return WireTypes.fitsInt(f.primitiveType) ? factory.numberNode((int)value) : factory.numberNode(value);
            }

            case FieldPlan.KIND_UINT64:
            {
                final long raw = buffer.getLong(index, f.byteOrder);
                if (f.optional && raw == f.nullValueLong)
                {
                    return factory.nullNode();
                }
                return JacksonCaches.unsignedLongNode(factory, raw);
            }

            case FieldPlan.KIND_FLOAT:
            {
                final float value = buffer.getFloat(index, f.byteOrder);
                if (f.optional && isNull(value, f.nullValueDouble))
                {
                    return factory.nullNode();
                }
                return factory.numberNode(value);
            }

            case FieldPlan.KIND_DOUBLE:
            {
                final double value = buffer.getDouble(index, f.byteOrder);
                if (f.optional && isNull(value, f.nullValueDouble))
                {
                    return factory.nullNode();
                }
                return factory.numberNode(value);
            }

            case FieldPlan.KIND_CHAR:
            {
                final byte value = buffer.getByte(index);
                if (f.optional && value == f.nullValueLong)
                {
                    return factory.nullNode();
                }
                return factory.textNode(String.valueOf((char)(value & 0xFF)));
            }

            case FieldPlan.KIND_CHAR_ARRAY:
                return factory.textNode(decodeCharArray(f, buffer, index));

            case FieldPlan.KIND_NUMERIC_ARRAY:
                return decodeNumericArray(f, buffer, index, factory);

            case FieldPlan.KIND_ENUM:
                return decodeEnum(f, buffer, index, factory);

            case FieldPlan.KIND_BIT_SET:
                return decodeBitSet(f, buffer, index, factory);

            default:
                throw new IllegalStateException("unexpected kind " + f.kind + " for " + f);
        }
    }

    /*
     * ASCII and UTF-8 arrays are terminated at the first zero byte (NUL is a single byte in both). Other charsets
     * are decoded in full and terminated at the first NUL character, since their encoded forms may contain zero
     * bytes inside ordinary characters.
     */
    private String decodeCharArray(final FieldPlan f, final DirectBuffer buffer, final int index)
    {
        final boolean nulTerminated = CharArrayStyle.NUL_TERMINATED == config.charArrayStyle();

        if (FieldPlan.ENC_ASCII == f.characterEncodingTag)
        {
            int length = f.arrayLength;
            final char[] chars = new char[length];
            for (int i = 0; i < length; i++)
            {
                final byte b = buffer.getByte(index + i);
                if (nulTerminated && 0 == b)
                {
                    length = i;
                    break;
                }
                chars[i] = (char)(b & 0xFF);
            }
            return new String(chars, 0, length);
        }

        int length = f.arrayLength;
        if (nulTerminated && FieldPlan.ENC_UTF8 == f.characterEncodingTag)
        {
            for (int i = 0; i < f.arrayLength; i++)
            {
                if (0 == buffer.getByte(index + i))
                {
                    length = i;
                    break;
                }
            }
        }

        final byte[] bytes = new byte[length];
        buffer.getBytes(index, bytes, 0, length);
        final String decoded = new String(bytes, f.charset);
        if (nulTerminated && FieldPlan.ENC_UTF8 != f.characterEncodingTag)
        {
            final int nul = decoded.indexOf('\0');
            return nul < 0 ? decoded : decoded.substring(0, nul);
        }

        return decoded;
    }

    private static ArrayNode decodeNumericArray(
        final FieldPlan f, final DirectBuffer buffer, final int index, final JsonNodeFactory factory)
    {
        final ArrayNode array = factory.arrayNode(f.arrayLength);
        final PrimitiveType type = f.primitiveType;
        final int size = type.size();
        for (int i = 0; i < f.arrayLength; i++)
        {
            final int elementIndex = index + i * size;
            switch (type)
            {
                case FLOAT:
                    array.add(buffer.getFloat(elementIndex, f.byteOrder));
                    break;

                case DOUBLE:
                    array.add(buffer.getDouble(elementIndex, f.byteOrder));
                    break;

                case UINT64:
                    array.add(JacksonCaches.unsignedLongNode(factory, buffer.getLong(elementIndex, f.byteOrder)));
                    break;

                default:
                    final long value = WireTypes.getLong(buffer, elementIndex, type, f.byteOrder);
                    if (WireTypes.fitsInt(type))
                    {
                        array.add((int)value);
                    }
                    else
                    {
                        array.add(value);
                    }
                    break;
            }
        }

        return array;
    }

    private JsonNode decodeEnum(
        final FieldPlan f, final DirectBuffer buffer, final int index, final JsonNodeFactory factory)
    {
        final long raw = WireTypes.getLong(buffer, index, f.primitiveType, f.byteOrder);
        if (f.optional && raw == f.nullValueLong)
        {
            return factory.nullNode();
        }

        if (EnumStyle.NAME == config.enumStyle())
        {
            final int valueIndex = f.enumIndexOf(raw);
            if (valueIndex >= 0)
            {
                return caches.enumName(f.index, valueIndex);
            }
        }

        return numericNode(f.primitiveType, raw, factory);
    }

    private JsonNode decodeBitSet(
        final FieldPlan f, final DirectBuffer buffer, final int index, final JsonNodeFactory factory)
    {
        final long raw = WireTypes.getLong(buffer, index, f.primitiveType, f.byteOrder);
        if (BitSetStyle.MASK == config.bitSetStyle())
        {
            return numericNode(f.primitiveType, raw, factory);
        }

        final ObjectNode node = factory.objectNode();
        for (int i = 0; i < f.choiceNames.length; i++)
        {
            node.set(f.choiceNames[i], factory.booleanNode(0 != ((raw >>> f.choiceBits[i]) & 1L)));
        }

        return node;
    }

    /**
     * Stock number node for a raw value of an integer encoding type: unsigned ({@code BigIntegerNode} when the
     * high bit is set) for uint64, {@code IntNode} for types that fit an int (including the byte of a
     * {@code char} encoded enum), {@code LongNode} otherwise.
     *
     * @param type    encoding type of the value.
     * @param raw     raw value as read from the wire (or the constant).
     * @param factory node factory.
     * @return the number node.
     */
    static JsonNode numericNode(final PrimitiveType type, final long raw, final JsonNodeFactory factory)
    {
        if (PrimitiveType.UINT64 == type)
        {
            return JacksonCaches.unsignedLongNode(factory, raw);
        }

        return WireTypes.fitsInt(type) || PrimitiveType.CHAR == type ?
            factory.numberNode((int)raw) : factory.numberNode(raw);
    }

    private int decodeGroup(
        final FieldPlan g,
        final DirectBuffer buffer,
        final int dimensionOffset,
        final int actingVersion,
        final int frameEnd,
        final ObjectNode target,
        final WalkContext ctx)
    {
        if (ctx.depth() + 1 > limits.maxDepth())
        {
            throw ctx.error(plan, ErrorCode.LIMIT_EXCEEDED, g, dimensionOffset,
                "group nesting depth " + (ctx.depth() + 1) + " exceeds maxDepth " + limits.maxDepth());
        }
        if (g.dimensionSize > frameEnd - dimensionOffset)
        {
            throw ctx.error(plan, ErrorCode.FRAME_OVERFLOW, g, dimensionOffset,
                "group dimensions of " + g.dimensionSize + " bytes do not fit in the frame");
        }

        final long blockLength = WireTypes.getLong(
            buffer, dimensionOffset + g.blockLengthOffset, g.blockLengthType, g.byteOrder);
        final long numInGroup = WireTypes.getLong(
            buffer, dimensionOffset + g.numInGroupOffset, g.numInGroupType, g.numInGroupByteOrder);

        if (numInGroup < g.numInGroupMin || numInGroup > g.numInGroupMax)
        {
            throw ctx.error(plan, ErrorCode.OUT_OF_RANGE, g, dimensionOffset + g.numInGroupOffset,
                "numInGroup " + numInGroup + " outside [" + g.numInGroupMin + ", " + g.numInGroupMax + "]");
        }
        if (ctx.addGroupEntries(numInGroup) > limits.maxGroupEntries())
        {
            throw ctx.error(plan, ErrorCode.LIMIT_EXCEEDED, g, dimensionOffset + g.numInGroupOffset,
                "total group entries exceed maxGroupEntries " + limits.maxGroupEntries());
        }

        int cursor = dimensionOffset + g.dimensionSize;
        final int count = (int)numInGroup;
        // An empty group consumes only its dimensions; each present entry is checked against the frame below.
        if (count > 0 && blockLength > frameEnd - cursor)
        {
            throw ctx.error(plan, ErrorCode.FRAME_OVERFLOW, g, dimensionOffset + g.blockLengthOffset,
                "group block length " + blockLength + " exceeds the frame");
        }

        final ArrayNode array = ctx.factory.arrayNode(count);
        ctx.push(g.index);
        for (int i = 0; i < count; i++)
        {
            ctx.element(i);
            final ObjectNode entry = ctx.factory.objectNode();
            cursor = decodeEntry(
                g.childStart, g.childEnd, buffer, cursor, (int)blockLength, actingVersion, frameEnd, entry, ctx);
            array.add(entry);
        }
        ctx.pop();
        target.set(g.name, array);

        return cursor;
    }

    private int decodeVarData(
        final FieldPlan v,
        final DirectBuffer buffer,
        final int lengthOffset,
        final int frameEnd,
        final ObjectNode target,
        final WalkContext ctx)
    {
        if (v.dataOffset > frameEnd - lengthOffset)
        {
            throw ctx.error(plan, ErrorCode.FRAME_OVERFLOW, v, lengthOffset,
                "var-data length prefix does not fit in the frame");
        }

        final long length = WireTypes.getLong(buffer, lengthOffset + v.lengthOffset, v.lengthType, v.byteOrder);
        if (length > v.lengthMax)
        {
            throw ctx.error(plan, ErrorCode.OUT_OF_RANGE, v, lengthOffset + v.lengthOffset,
                "var-data length " + length + " exceeds the length type maximum " + v.lengthMax);
        }
        if (ctx.addVarDataBytes(length) > limits.maxVarDataBytes())
        {
            throw ctx.error(plan, ErrorCode.LIMIT_EXCEEDED, v, lengthOffset + v.lengthOffset,
                "total var-data bytes exceed maxVarDataBytes " + limits.maxVarDataBytes());
        }

        final int dataIndex = lengthOffset + v.dataOffset;
        if (length > frameEnd - dataIndex)
        {
            throw ctx.error(plan, ErrorCode.FRAME_OVERFLOW, v, dataIndex,
                "var-data of " + length + " bytes exceeds the frame by " + (length - (frameEnd - dataIndex)));
        }

        final int len = (int)length;
        final JsonNode node;
        switch (v.characterEncodingTag)
        {
            case FieldPlan.ENC_BINARY:
            {
                final byte[] bytes = new byte[len];
                buffer.getBytes(dataIndex, bytes, 0, len);
                node = ctx.factory.binaryNode(bytes);
                break;
            }

            case FieldPlan.ENC_UTF8:
                node = ctx.factory.textNode(buffer.getStringWithoutLengthUtf8(dataIndex, len));
                break;

            case FieldPlan.ENC_ASCII:
                node = ctx.factory.textNode(buffer.getStringWithoutLengthAscii(dataIndex, len));
                break;

            default:
            {
                final byte[] bytes = new byte[len];
                buffer.getBytes(dataIndex, bytes, 0, len);
                node = ctx.factory.textNode(new String(bytes, v.charset));
                break;
            }
        }
        target.set(v.name, node);

        return dataIndex + len;
    }

    private static boolean isNull(final double value, final double nullValue)
    {
        return Double.isNaN(nullValue) ? Double.isNaN(value) : value == nullValue;
    }
}
