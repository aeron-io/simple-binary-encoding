/**
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

import org.agrona.collections.Object2IntHashMap;
import uk.co.real_logic.sbe.PrimitiveType;
import uk.co.real_logic.sbe.PrimitiveValue;
import uk.co.real_logic.sbe.ir.Encoding;
import uk.co.real_logic.sbe.ir.HeaderStructure;
import uk.co.real_logic.sbe.ir.Ir;
import uk.co.real_logic.sbe.ir.Signal;
import uk.co.real_logic.sbe.ir.Token;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

/**
 * Compiles the IR token list of a message into a {@link MessagePlan}. Mirrors the token walk of
 * {@link uk.co.real_logic.sbe.otf.OtfMessageDecoder} (block fields, then groups, then var-data, recursively) but
 * records offsets, sizes and tables instead of dispatching. Carries no Jackson types.
 */
final class PlanCompiler
{
    private static final int MISSING = -1;

    private final Ir ir;
    private final List<FieldPlan.Builder> out = new ArrayList<>();
    private final TreeSet<Integer> versions = new TreeSet<>();
    private int maxGroupDepth;

    private PlanCompiler(final Ir ir)
    {
        this.ir = ir;
    }

    /**
     * Compile the plan for one message.
     *
     * @param ir        the IR the message belongs to.
     * @param msgTokens tokens for the message as returned by {@link Ir#getMessage(long)}.
     * @return the compiled plan.
     */
    static MessagePlan compile(final Ir ir, final List<Token> msgTokens)
    {
        return new PlanCompiler(ir).compileMessage(msgTokens);
    }

    private MessagePlan compileMessage(final List<Token> tokens)
    {
        final Token msgToken = tokens.get(0);
        if (Signal.BEGIN_MESSAGE != msgToken.signal())
        {
            throw new IllegalArgumentException("expected BEGIN_MESSAGE but found " + msgToken.signal());
        }

        final long range = compileScope(tokens, 1, tokens.size() - 1, 0);

        final FieldPlan[] fields = new FieldPlan[out.size()];
        for (int i = 0; i < fields.length; i++)
        {
            final FieldPlan.Builder b = out.get(i);
            b.index = i;
            fields[i] = b.build();
        }

        final int[] thresholds = new int[versions.size()];
        int t = 0;
        for (final Integer version : versions)
        {
            thresholds[t++] = version;
        }

        return new MessagePlan(
            msgToken.id(),
            msgToken.name(),
            msgToken.encodedLength(),
            ir.id(),
            ir.version(),
            fields,
            rangeStart(range),
            rangeEnd(range),
            thresholds,
            maxGroupDepth);
    }

    /*
     * Compile the direct children of a block scope (message root or group body) in {@code [from, to)}, then the
     * descendants of every container child, so that each scope's children are contiguous.
     */
    private long compileScope(final List<Token> tokens, final int from, final int to, final int depth)
    {
        maxGroupDepth = Math.max(maxGroupDepth, depth);
        final int start = out.size();
        final List<int[]> containers = new ArrayList<>();

        int i = from;
        while (i < to)
        {
            final Token token = tokens.get(i);
            switch (token.signal())
            {
                case BEGIN_FIELD:
                    compileField(tokens, i, containers);
                    break;

                case BEGIN_GROUP:
                    compileGroup(tokens, i, containers);
                    break;

                case BEGIN_VAR_DATA:
                    compileVarData(tokens, i);
                    break;

                default:
                    throw new IllegalStateException("unexpected token in block scope: " + token);
            }

            i += token.componentTokenCount();
        }

        final int end = out.size();

        for (final int[] container : containers)
        {
            final FieldPlan.Builder b = out.get(container[0]);
            final long range = b.kind == FieldPlan.KIND_GROUP ?
                compileScope(tokens, container[1], container[2], depth + 1) :
                compileCompositeMembers(tokens, container[1], container[2], b.offset, b.sinceVersion);
            b.childStart = rangeStart(range);
            b.childEnd = rangeEnd(range);
        }

        return pack(start, end);
    }

    private void compileField(final List<Token> tokens, final int fieldIdx, final List<int[]> containers)
    {
        final Token fieldToken = tokens.get(fieldIdx);
        final Token typeToken = tokens.get(fieldIdx + 1);
        final FieldPlan.Builder b = newBuilder(fieldToken.name(), fieldToken.version(), fieldToken.deprecated());
        final int typeIdx = fieldIdx + 1;

        switch (typeToken.signal())
        {
            case ENCODING:
                fillEncoding(b, typeToken, 0);
                break;

            case BEGIN_COMPOSITE:
                fillComposite(b, typeToken, 0);
                containers.add(new int[]{ out.size(), typeIdx + 1, typeIdx + typeToken.componentTokenCount() - 1 });
                break;

            case BEGIN_ENUM:
                fillEnum(b, tokens, typeIdx, 0, fieldToken);
                break;

            case BEGIN_SET:
                fillSet(b, tokens, typeIdx, 0);
                break;

            default:
                throw new IllegalStateException("unexpected field type token: " + typeToken);
        }

        out.add(b);
    }

    /*
     * Compile the members of a composite in {@code [from, to)}; offsets are relative to the enclosing scope so the
     * composite's own scope-relative offset is added to every member.
     */
    private long compileCompositeMembers(
        final List<Token> tokens, final int from, final int to, final int compositeOffset, final int parentVersion)
    {
        final int start = out.size();
        final List<int[]> containers = new ArrayList<>();

        int i = from;
        while (i < to)
        {
            final Token token = tokens.get(i);
            final FieldPlan.Builder b = newBuilder(
                token.name(), Math.max(parentVersion, token.version()), token.deprecated());

            switch (token.signal())
            {
                case ENCODING:
                    fillEncoding(b, token, compositeOffset);
                    break;

                case BEGIN_COMPOSITE:
                    fillComposite(b, token, compositeOffset);
                    containers.add(new int[]{ out.size(), i + 1, i + token.componentTokenCount() - 1 });
                    break;

                case BEGIN_ENUM:
                    fillEnum(b, tokens, i, compositeOffset, null);
                    break;

                case BEGIN_SET:
                    fillSet(b, tokens, i, compositeOffset);
                    break;

                default:
                    throw new IllegalStateException("unexpected composite member token: " + token);
            }

            out.add(b);
            i += token.componentTokenCount();
        }

        final int end = out.size();

        for (final int[] container : containers)
        {
            final FieldPlan.Builder b = out.get(container[0]);
            final long range = compileCompositeMembers(
                tokens, container[1], container[2], b.offset, b.sinceVersion);
            b.childStart = rangeStart(range);
            b.childEnd = rangeEnd(range);
        }

        return pack(start, end);
    }

    private void compileGroup(final List<Token> tokens, final int groupIdx, final List<int[]> containers)
    {
        final Token groupToken = tokens.get(groupIdx);
        final Token dimensionToken = tokens.get(groupIdx + 1);
        final FieldPlan.Builder b = newBuilder(groupToken.name(), groupToken.version(), groupToken.deprecated());
        b.kind = FieldPlan.KIND_GROUP;
        b.blockLength = groupToken.encodedLength();
        b.dimensionSize = dimensionToken.encodedLength();
        b.encodedLength = dimensionToken.encodedLength();

        final int dimensionEnd = groupIdx + 1 + dimensionToken.componentTokenCount();
        final Token blockLengthToken = findMember(
            tokens, groupIdx + 2, dimensionEnd, HeaderStructure.BLOCK_LENGTH, groupIdx + 2);
        final Token numInGroupToken = findMember(tokens, groupIdx + 2, dimensionEnd, "numInGroup", groupIdx + 3);

        b.blockLengthType = blockLengthToken.encoding().primitiveType();
        b.blockLengthOffset = blockLengthToken.offset();
        b.byteOrder = blockLengthToken.encoding().byteOrder();
        b.numInGroupType = numInGroupToken.encoding().primitiveType();
        b.numInGroupOffset = numInGroupToken.offset();
        b.numInGroupMin = numInGroupToken.encoding().applicableMinValue().longValue();
        b.numInGroupMax = numInGroupToken.encoding().applicableMaxValue().longValue();

        containers.add(new int[]{ out.size(), dimensionEnd, groupIdx + groupToken.componentTokenCount() - 1 });
        out.add(b);
    }

    private void compileVarData(final List<Token> tokens, final int varDataIdx)
    {
        final Token varDataToken = tokens.get(varDataIdx);
        final Token compositeToken = tokens.get(varDataIdx + 1);
        final int compositeEnd = varDataIdx + 1 + compositeToken.componentTokenCount();
        final Token lengthToken = findMember(tokens, varDataIdx + 2, compositeEnd, "length", varDataIdx + 2);
        final Token dataToken = findMember(tokens, varDataIdx + 2, compositeEnd, "varData", varDataIdx + 3);

        final FieldPlan.Builder b = newBuilder(
            varDataToken.name(), varDataToken.version(), varDataToken.deprecated());
        b.kind = FieldPlan.KIND_VAR_DATA;
        b.lengthType = lengthToken.encoding().primitiveType();
        b.lengthOffset = lengthToken.offset();
        b.lengthMax = lengthToken.encoding().applicableMaxValue().longValue();
        b.byteOrder = lengthToken.encoding().byteOrder();
        b.dataOffset = dataToken.offset();
        b.primitiveType = dataToken.encoding().primitiveType();
        b.presence = varDataToken.encoding().presence();

        final String characterEncoding = dataToken.encoding().characterEncoding();
        b.characterEncoding = characterEncoding;
        if (null == characterEncoding)
        {
            b.characterEncodingTag = FieldPlan.ENC_BINARY;
        }
        else
        {
            applyCharacterEncoding(b, characterEncoding);
        }

        out.add(b);
    }

    private void fillEncoding(final FieldPlan.Builder b, final Token typeToken, final int baseOffset)
    {
        final Encoding encoding = typeToken.encoding();
        final PrimitiveType primitiveType = encoding.primitiveType();
        b.primitiveType = primitiveType;
        b.byteOrder = encoding.byteOrder();
        b.offset = baseOffset + typeToken.offset();
        b.presence = encoding.presence();
        b.encodedLength = typeToken.encodedLength();
        b.arrayLength = Math.max(1, typeToken.arrayLength());
        b.characterEncoding = encoding.characterEncoding();
        if (null != encoding.characterEncoding())
        {
            applyCharacterEncoding(b, encoding.characterEncoding());
        }

        if (PrimitiveType.FLOAT == primitiveType || PrimitiveType.DOUBLE == primitiveType)
        {
            b.minValueDouble = encoding.applicableMinValue().doubleValue();
            b.maxValueDouble = encoding.applicableMaxValue().doubleValue();
            b.nullValueDouble = encoding.applicableNullValue().doubleValue();
        }
        else
        {
            b.minValueLong = encoding.applicableMinValue().longValue();
            b.maxValueLong = encoding.applicableMaxValue().longValue();
            b.nullValueLong = encoding.applicableNullValue().longValue();
        }

        if (Encoding.Presence.CONSTANT == encoding.presence())
        {
            fillConstant(b, encoding.constValue(), primitiveType);
        }
        else
        {
            b.kind = scalarKind(primitiveType, b.arrayLength);
        }
    }

    private static void fillConstant(
        final FieldPlan.Builder b, final PrimitiveValue constValue, final PrimitiveType primitiveType)
    {
        switch (constValue.representation())
        {
            case LONG:
                if (PrimitiveType.CHAR == primitiveType)
                {
                    b.kind = FieldPlan.KIND_CHAR;
                    b.constString = String.valueOf((char)constValue.longValue());
                }
                else
                {
                    b.kind = scalarKind(primitiveType, 1);
                    b.constLong = constValue.longValue();
                    b.constDouble = constValue.longValue();
                }
                break;

            case DOUBLE:
                b.kind = scalarKind(primitiveType, 1);
                b.constDouble = constValue.doubleValue();
                break;

            case BYTE_ARRAY:
                b.kind = FieldPlan.KIND_CHAR_ARRAY;
                b.constString = new String(
                    constValue.byteArrayValue(), charsetFor(constValue.characterEncoding()));
                b.arrayLength = b.constString.length();
                break;

            default:
                throw new IllegalStateException("unsupported constant representation: " + constValue);
        }
    }

    private static byte scalarKind(final PrimitiveType primitiveType, final int arrayLength)
    {
        switch (primitiveType)
        {
            case CHAR:
                return arrayLength > 1 ? FieldPlan.KIND_CHAR_ARRAY : FieldPlan.KIND_CHAR;

            case FLOAT:
                return arrayLength > 1 ? FieldPlan.KIND_NUMERIC_ARRAY : FieldPlan.KIND_FLOAT;

            case DOUBLE:
                return arrayLength > 1 ? FieldPlan.KIND_NUMERIC_ARRAY : FieldPlan.KIND_DOUBLE;

            case UINT64:
                return arrayLength > 1 ? FieldPlan.KIND_NUMERIC_ARRAY : FieldPlan.KIND_UINT64;

            default:
                return arrayLength > 1 ? FieldPlan.KIND_NUMERIC_ARRAY : FieldPlan.KIND_INT;
        }
    }

    private static void fillComposite(final FieldPlan.Builder b, final Token compositeToken, final int baseOffset)
    {
        b.kind = FieldPlan.KIND_COMPOSITE;
        b.offset = baseOffset + compositeToken.offset();
        b.encodedLength = compositeToken.encodedLength();
        b.presence = Encoding.Presence.REQUIRED;
    }

    private void fillEnum(
        final FieldPlan.Builder b,
        final List<Token> tokens,
        final int enumIdx,
        final int baseOffset,
        final Token fieldToken)
    {
        final Token enumToken = tokens.get(enumIdx);
        final Encoding encoding = enumToken.encoding();
        b.kind = FieldPlan.KIND_ENUM;
        b.primitiveType = encoding.primitiveType();
        b.byteOrder = encoding.byteOrder();
        b.offset = baseOffset + enumToken.offset();
        b.encodedLength = enumToken.encodedLength();
        b.nullValueLong = encoding.applicableNullValue().longValue();
        b.presence = null != fieldToken ? fieldToken.encoding().presence() : Encoding.Presence.REQUIRED;

        final int count = enumToken.componentTokenCount() - 2;
        final long[] values = new long[count];
        final String[] names = new String[count];
        final Integer[] order = new Integer[count];
        for (int i = 0; i < count; i++)
        {
            final Token valueToken = tokens.get(enumIdx + 1 + i);
            values[i] = valueToken.encoding().constValue().longValue();
            names[i] = valueToken.name().intern();
            order[i] = i;
        }
        Arrays.sort(order, (x, y) -> Long.compare(values[x], values[y]));

        b.enumValues = new long[count];
        b.enumNames = new String[count];
        b.enumNameToIndex = new Object2IntHashMap<>(MISSING);
        for (int i = 0; i < count; i++)
        {
            b.enumValues[i] = values[order[i]];
            b.enumNames[i] = names[order[i]];
            b.enumNameToIndex.put(b.enumNames[i], i);
        }

        if (Encoding.Presence.CONSTANT == b.presence)
        {
            final PrimitiveValue constValue = fieldToken.encoding().constValue();
            final String valueRef = new String(constValue.byteArrayValue(), StandardCharsets.UTF_8);
            final int dot = valueRef.indexOf('.');
            final String valueName = -1 == dot ? valueRef : valueRef.substring(dot + 1);
            final int index = b.enumNameToIndex.getValue(valueName);
            if (MISSING == index)
            {
                throw new IllegalStateException("constant enum value not found: " + valueRef);
            }
            b.constString = b.enumNames[index];
            b.constLong = b.enumValues[index];
        }
    }

    private static void fillSet(
        final FieldPlan.Builder b, final List<Token> tokens, final int setIdx, final int baseOffset)
    {
        final Token setToken = tokens.get(setIdx);
        final Encoding encoding = setToken.encoding();
        b.kind = FieldPlan.KIND_BIT_SET;
        b.primitiveType = encoding.primitiveType();
        b.byteOrder = encoding.byteOrder();
        b.offset = baseOffset + setToken.offset();
        b.encodedLength = setToken.encodedLength();
        b.presence = Encoding.Presence.REQUIRED;
        b.minValueLong = 0;
        b.maxValueLong = 8 == encoding.primitiveType().size() ? -1L : (1L << (8 * encoding.primitiveType().size())) - 1;

        final int count = setToken.componentTokenCount() - 2;
        b.choiceNames = new String[count];
        b.choiceBits = new int[count];
        b.choiceNameToBit = new Object2IntHashMap<>(MISSING);
        long knownMask = 0;
        for (int i = 0; i < count; i++)
        {
            final Token choiceToken = tokens.get(setIdx + 1 + i);
            final int bit = (int)choiceToken.encoding().constValue().longValue();
            b.choiceNames[i] = choiceToken.name().intern();
            b.choiceBits[i] = bit;
            b.choiceNameToBit.put(b.choiceNames[i], bit);
            knownMask |= 1L << bit;
        }
        b.knownMask = knownMask;
    }

    private static void applyCharacterEncoding(final FieldPlan.Builder b, final String characterEncoding)
    {
        final Charset charset = charsetFor(characterEncoding);
        b.charset = charset;
        if (StandardCharsets.US_ASCII.equals(charset))
        {
            b.characterEncodingTag = FieldPlan.ENC_ASCII;
        }
        else if (StandardCharsets.UTF_8.equals(charset))
        {
            b.characterEncodingTag = FieldPlan.ENC_UTF8;
        }
        else
        {
            b.characterEncodingTag = FieldPlan.ENC_OTHER;
        }
    }

    /**
     * Resolve a schema {@code characterEncoding} to a charset; {@code null} means ASCII for character fields.
     *
     * @param characterEncoding schema value or {@code null}.
     * @return the charset.
     */
    static Charset charsetFor(final String characterEncoding)
    {
        if (null == characterEncoding || "ASCII".equalsIgnoreCase(characterEncoding))
        {
            return StandardCharsets.US_ASCII;
        }

        return Charset.forName(characterEncoding);
    }

    private FieldPlan.Builder newBuilder(final String name, final int sinceVersion, final int deprecated)
    {
        final FieldPlan.Builder b = new FieldPlan.Builder();
        b.name = name;
        b.sinceVersion = sinceVersion;
        b.deprecated = deprecated;
        versions.add(sinceVersion);

        return b;
    }

    private static Token findMember(
        final List<Token> tokens, final int from, final int to, final String name, final int fallbackIdx)
    {
        for (int i = from; i < to; i++)
        {
            final Token token = tokens.get(i);
            if (Signal.ENCODING == token.signal() && name.equals(token.name()))
            {
                return token;
            }
        }

        return tokens.get(fallbackIdx);
    }

    private static long pack(final int start, final int end)
    {
        return ((long)start << 32) | end;
    }

    private static int rangeStart(final long range)
    {
        return (int)(range >>> 32);
    }

    private static int rangeEnd(final long range)
    {
        return (int)range;
    }
}
