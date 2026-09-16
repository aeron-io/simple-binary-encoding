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

import org.agrona.collections.Object2IntHashMap;
import uk.co.real_logic.sbe.PrimitiveType;
import uk.co.real_logic.sbe.ir.Encoding;

import java.nio.ByteOrder;
import java.nio.charset.Charset;

/**
 * One flat record per field, composite member, group or var-data section of a message. Produced once by
 * {@link PlanCompiler}; read by the codecs in a {@code switch (kind)} loop. Carries no Jackson types.
 * <p>
 * Offsets are scope-relative: relative to the block start for root fields and to the entry start for group
 * fields. Composite members carry their composite's offset added in, so one read per leaf.
 */
final class FieldPlan
{
    /**
     * int8 / int16 / int32 / int64 / uint8 / uint16 / uint32 scalar read as a {@code long}.
     */
    static final byte KIND_INT = 0;

    /**
     * uint64 scalar; the raw {@code long} is interpreted unsigned.
     */
    static final byte KIND_UINT64 = 1;

    /**
     * 32-bit float scalar.
     */
    static final byte KIND_FLOAT = 2;

    /**
     * 64-bit double scalar.
     */
    static final byte KIND_DOUBLE = 3;

    /**
     * Single {@code char}.
     */
    static final byte KIND_CHAR = 4;

    /**
     * {@code char[N]} decoded as a string.
     */
    static final byte KIND_CHAR_ARRAY = 5;

    /**
     * Fixed length array of a numeric primitive.
     */
    static final byte KIND_NUMERIC_ARRAY = 6;

    /**
     * Enum; {@link #enumValues} / {@link #enumNames} hold the table.
     */
    static final byte KIND_ENUM = 7;

    /**
     * Bit set; {@link #choiceNames} / {@link #choiceBits} hold the table.
     */
    static final byte KIND_BIT_SET = 8;

    /**
     * Composite; children in {@code [childStart, childEnd)}.
     */
    static final byte KIND_COMPOSITE = 9;

    /**
     * Repeating group; children in {@code [childStart, childEnd)}: fields, then groups, then var-data.
     */
    static final byte KIND_GROUP = 10;

    /**
     * Variable length data section.
     */
    static final byte KIND_VAR_DATA = 11;

    /**
     * Character encoding tag: ASCII or unspecified on a {@code char} field.
     */
    static final byte ENC_ASCII = 0;

    /**
     * Character encoding tag: UTF-8.
     */
    static final byte ENC_UTF8 = 1;

    /**
     * Character encoding tag: binary var-data (no character encoding).
     */
    static final byte ENC_BINARY = 2;

    /**
     * Character encoding tag: any other charset, resolved into {@link #charset}.
     */
    static final byte ENC_OTHER = 3;

    final byte kind;
    final String name;
    final int index;
    final int offset;
    final PrimitiveType primitiveType;
    final ByteOrder byteOrder;
    final int arrayLength;
    final int encodedLength;
    final int sinceVersion;
    final int deprecated;
    final Encoding.Presence presence;
    final boolean constant;
    final boolean optional;
    final long nullValueLong;
    final double nullValueDouble;
    final long minValueLong;
    final long maxValueLong;
    final double minValueDouble;
    final double maxValueDouble;
    final long constLong;
    final double constDouble;
    final String constString;
    final int childStart;
    final int childEnd;
    final long[] enumValues;
    final String[] enumNames;
    final Object2IntHashMap<String> enumNameToIndex;
    final String[] choiceNames;
    final int[] choiceBits;
    final long knownMask;
    final Object2IntHashMap<String> choiceNameToBit;
    final byte characterEncodingTag;
    final String characterEncoding;
    final Charset charset;
    final int blockLength;
    final PrimitiveType blockLengthType;
    final int blockLengthOffset;
    final PrimitiveType numInGroupType;
    final int numInGroupOffset;
    final ByteOrder numInGroupByteOrder;
    final long numInGroupMin;
    final long numInGroupMax;
    final int dimensionSize;
    final PrimitiveType lengthType;
    final int lengthOffset;
    final long lengthMax;
    final int dataOffset;

    FieldPlan(final Builder b)
    {
        kind = b.kind;
        name = b.name.intern();
        index = b.index;
        offset = b.offset;
        primitiveType = b.primitiveType;
        byteOrder = b.byteOrder;
        arrayLength = b.arrayLength;
        encodedLength = b.encodedLength;
        sinceVersion = b.sinceVersion;
        deprecated = b.deprecated;
        presence = b.presence;
        constant = b.presence == Encoding.Presence.CONSTANT;
        optional = b.presence == Encoding.Presence.OPTIONAL;
        nullValueLong = b.nullValueLong;
        nullValueDouble = b.nullValueDouble;
        minValueLong = b.minValueLong;
        maxValueLong = b.maxValueLong;
        minValueDouble = b.minValueDouble;
        maxValueDouble = b.maxValueDouble;
        constLong = b.constLong;
        constDouble = b.constDouble;
        constString = b.constString;
        childStart = b.childStart;
        childEnd = b.childEnd;
        enumValues = b.enumValues;
        enumNames = b.enumNames;
        enumNameToIndex = b.enumNameToIndex;
        choiceNames = b.choiceNames;
        choiceBits = b.choiceBits;
        knownMask = b.knownMask;
        choiceNameToBit = b.choiceNameToBit;
        characterEncodingTag = b.characterEncodingTag;
        characterEncoding = b.characterEncoding;
        charset = b.charset;
        blockLength = b.blockLength;
        blockLengthType = b.blockLengthType;
        blockLengthOffset = b.blockLengthOffset;
        numInGroupType = b.numInGroupType;
        numInGroupOffset = b.numInGroupOffset;
        numInGroupByteOrder = b.numInGroupByteOrder;
        numInGroupMin = b.numInGroupMin;
        numInGroupMax = b.numInGroupMax;
        dimensionSize = b.dimensionSize;
        lengthType = b.lengthType;
        lengthOffset = b.lengthOffset;
        lengthMax = b.lengthMax;
        dataOffset = b.dataOffset;
    }

    /**
     * Whether this plan describes a container whose children are in {@code [childStart, childEnd)}.
     *
     * @return true for composites and groups.
     */
    boolean isContainer()
    {
        return kind == KIND_COMPOSITE || kind == KIND_GROUP;
    }

    /**
     * Whether this plan describes a fixed-length field in the block (as opposed to a group or var-data).
     *
     * @return true for everything except groups and var-data.
     */
    boolean isBlockField()
    {
        return kind < KIND_GROUP;
    }

    /**
     * Index of the enum value equal to {@code raw}, or -1.
     *
     * @param raw encoded value.
     * @return index into {@link #enumValues} / {@link #enumNames}, or -1 when unknown.
     */
    int enumIndexOf(final long raw)
    {
        final long[] values = enumValues;
        int low = 0;
        int high = values.length - 1;
        while (low <= high)
        {
            final int mid = (low + high) >>> 1;
            final long midVal = values[mid];
            if (midVal < raw)
            {
                low = mid + 1;
            }
            else if (midVal > raw)
            {
                high = mid - 1;
            }
            else
            {
                return mid;
            }
        }

        return -1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "FieldPlan{" +
            "kind=" + kind +
            ", name='" + name + '\'' +
            ", index=" + index +
            ", offset=" + offset +
            ", primitiveType=" + primitiveType +
            ", arrayLength=" + arrayLength +
            ", encodedLength=" + encodedLength +
            ", sinceVersion=" + sinceVersion +
            ", presence=" + presence +
            ", childStart=" + childStart +
            ", childEnd=" + childEnd +
            '}';
    }

    /**
     * Mutable holder used only during compilation.
     */
    static final class Builder
    {
        byte kind;
        String name;
        int index;
        int offset;
        PrimitiveType primitiveType;
        ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
        int arrayLength = 1;
        int encodedLength;
        int sinceVersion;
        int deprecated;
        Encoding.Presence presence = Encoding.Presence.REQUIRED;
        long nullValueLong;
        double nullValueDouble = Double.NaN;
        long minValueLong = Long.MIN_VALUE;
        long maxValueLong = Long.MAX_VALUE;
        double minValueDouble = -Double.MAX_VALUE;
        double maxValueDouble = Double.MAX_VALUE;
        long constLong;
        double constDouble;
        String constString;
        int childStart;
        int childEnd;
        long[] enumValues;
        String[] enumNames;
        Object2IntHashMap<String> enumNameToIndex;
        String[] choiceNames;
        int[] choiceBits;
        long knownMask;
        Object2IntHashMap<String> choiceNameToBit;
        byte characterEncodingTag = ENC_ASCII;
        String characterEncoding;
        Charset charset;
        int blockLength;
        PrimitiveType blockLengthType;
        int blockLengthOffset;
        PrimitiveType numInGroupType;
        int numInGroupOffset;
        ByteOrder numInGroupByteOrder = ByteOrder.LITTLE_ENDIAN;
        long numInGroupMin;
        long numInGroupMax;
        int dimensionSize;
        PrimitiveType lengthType;
        int lengthOffset;
        long lengthMax;
        int dataOffset;

        FieldPlan build()
        {
            return new FieldPlan(this);
        }
    }
}
