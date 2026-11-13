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
package uk.co.real_logic.sbe.codec.java;

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.real_logic.sbe.PrimitiveType;
import uk.co.real_logic.sbe.PrimitiveValue;
import uk.co.real_logic.sbe.ir.Encoding;
import uk.co.real_logic.sbe.otf.Types;

import java.nio.ByteOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TypesTest
{
    private static final ByteOrder BYTE_ORDER = ByteOrder.nativeOrder();
    private static final int BUFFER_CAPACITY = 64;

    private final MutableDirectBuffer buffer = new UnsafeBuffer(new byte[BUFFER_CAPACITY]);

    @Test
    void shouldTestBitInByte()
    {
        final byte bits = (byte)0b1000_0000;
        final int bufferIndex = 8;
        final int bitIndex = 7;
        buffer.putByte(bufferIndex, bits);

        for (int i = 0; i < 8; i++)
        {
            final boolean result = 0 != (buffer.getByte(bufferIndex) & (1 << i));
            if (bitIndex == i)
            {
                assertTrue(result);
            }
            else
            {
                assertFalse(result, "bit set i = " + i);
            }
        }
    }

    @Test
    void shouldSetBitInByte()
    {
        final int bufferIndex = 8;

        int total = 0;
        for (int i = 0; i < 8; i++)
        {
            byte bits = buffer.getByte(bufferIndex);
            bits = (byte)(bits | (1 << i));
            buffer.putByte(bufferIndex, bits);
            total += (1 << i);
            assertEquals((byte)total, buffer.getByte(bufferIndex));
        }
    }

    @Test
    void shouldTestBitInShort()
    {
        final short bits = (short)0b0000_0000_0000_0100;
        final int bufferIndex = 8;
        final int bitIndex = 2;
        buffer.putShort(bufferIndex, bits, BYTE_ORDER);

        for (int i = 0; i < 16; i++)
        {
            final boolean result = 0 != (buffer.getShort(bufferIndex, BYTE_ORDER) & (1 << i));
            if (bitIndex == i)
            {
                assertTrue(result);
            }
            else
            {
                assertFalse(result, "bit set i = " + i);
            }
        }
    }

    @Test
    void shouldSetBitInShort()
    {
        final int bufferIndex = 8;

        int total = 0;
        for (int i = 0; i < 16; i++)
        {
            short bits = buffer.getShort(bufferIndex, BYTE_ORDER);
            bits = (short)(bits | (1 << i));
            buffer.putShort(bufferIndex, bits, BYTE_ORDER);
            total += (1 << i);
            assertEquals((short)total, buffer.getShort(bufferIndex, BYTE_ORDER));
        }
    }

    @Test
    void shouldTestBitInInt()
    {
        final int bits = 0b0000_0000_0000_0000_0000_0000_0000_0100;
        final int bufferIndex = 8;
        final int bitIndex = 2;
        buffer.putInt(bufferIndex, bits, BYTE_ORDER);

        for (int i = 0; i < 32; i++)
        {
            final boolean result = 0 != (buffer.getInt(bufferIndex, BYTE_ORDER) & (1 << i));
            if (bitIndex == i)
            {
                assertTrue(result);
            }
            else
            {
                assertFalse(result, "bit set i = " + i);
            }
        }
    }

    @Test
    void shouldSetBitInInt()
    {
        final int bufferIndex = 8;
        long total = 0;

        for (int i = 0; i < 32; i++)
        {
            int bits = buffer.getInt(bufferIndex, BYTE_ORDER);
            bits = bits | (1 << i);
            buffer.putInt(bufferIndex, bits, BYTE_ORDER);
            total += (1 << i);
            assertEquals((int)total, buffer.getInt(bufferIndex, BYTE_ORDER));
        }
    }

    @ParameterizedTest
    @ValueSource(bytes = {Byte.MIN_VALUE, -1, 0, 1, Byte.MAX_VALUE})
    void shouldAppendUInt8AsString(final byte value)
    {
        final StringBuilder out = new StringBuilder();
        final Encoding encoding = new Encoding.Builder().primitiveType(PrimitiveType.UINT8).build();
        final PrimitiveValue primitiveValue = new PrimitiveValue(Byte.toUnsignedInt(value), 1);

        final MutableDirectBuffer buffer = new UnsafeBuffer(new byte[1]);
        buffer.putByte(0, value);

        final String expected = Integer.toString(Byte.toUnsignedInt(value));

        Types.appendAsString(out, buffer, 0, encoding);
        assertEquals(expected, out.toString());

        out.setLength(0);
        Types.appendAsJsonString(out, buffer, 0, encoding);
        assertEquals(expected, out.toString());

        out.setLength(0);
        Types.appendAsJsonString(out, primitiveValue, encoding);
        assertEquals(expected, out.toString());
    }

    @ParameterizedTest
    @ValueSource(bytes = {Byte.MIN_VALUE, -1, 0, 1, Byte.MAX_VALUE})
    void shouldAppendInt8AsString(final byte value)
    {
        final StringBuilder out = new StringBuilder();
        final Encoding encoding = new Encoding.Builder().primitiveType(PrimitiveType.INT8).build();
        final PrimitiveValue primitiveValue = new PrimitiveValue(value, 1);

        final MutableDirectBuffer buffer = new UnsafeBuffer(new byte[1]);
        buffer.putByte(0, value);

        final String expected = Byte.toString(value);

        Types.appendAsString(out, buffer, 0, encoding);
        assertEquals(expected, out.toString());

        out.setLength(0);
        Types.appendAsJsonString(out, buffer, 0, encoding);
        assertEquals(expected, out.toString());

        out.setLength(0);
        Types.appendAsJsonString(out, primitiveValue, encoding);
        assertEquals(expected, out.toString());
    }

    @ParameterizedTest
    @ValueSource(shorts = {Short.MIN_VALUE, -1, 0, 1, Short.MAX_VALUE})
    void shouldAppendUInt16AsString(final short value)
    {
        final StringBuilder out = new StringBuilder();
        final Encoding encoding = new Encoding.Builder().primitiveType(PrimitiveType.UINT16).build();
        final PrimitiveValue primitiveValue = new PrimitiveValue(Short.toUnsignedInt(value), 2);

        final MutableDirectBuffer buffer = new UnsafeBuffer(new byte[2]);
        buffer.putShort(0, value);

        final String expected = Integer.toString(Short.toUnsignedInt(value));

        Types.appendAsString(out, buffer, 0, encoding);
        assertEquals(expected, out.toString());

        out.setLength(0);
        Types.appendAsJsonString(out, buffer, 0, encoding);
        assertEquals(expected, out.toString());

        out.setLength(0);
        Types.appendAsJsonString(out, primitiveValue, encoding);
        assertEquals(expected, out.toString());
    }

    @ParameterizedTest
    @ValueSource(shorts = {Short.MIN_VALUE, -1, 0, 1, Short.MAX_VALUE})
    void shouldAppendInt16AsString(final short value)
    {
        final StringBuilder out = new StringBuilder();
        final Encoding encoding = new Encoding.Builder().primitiveType(PrimitiveType.INT16).build();
        final PrimitiveValue primitiveValue = new PrimitiveValue(value, 2);

        final MutableDirectBuffer buffer = new UnsafeBuffer(new byte[2]);
        buffer.putShort(0, value);

        final String expected = Short.toString(value);

        Types.appendAsString(out, buffer, 0, encoding);
        assertEquals(expected, out.toString());

        out.setLength(0);
        Types.appendAsJsonString(out, buffer, 0, encoding);
        assertEquals(expected, out.toString());

        out.setLength(0);
        Types.appendAsJsonString(out, primitiveValue, encoding);
        assertEquals(expected, out.toString());
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE})
    void shouldAppendUInt32AsString(final int value)
    {
        final StringBuilder out = new StringBuilder();
        final Encoding encoding = new Encoding.Builder().primitiveType(PrimitiveType.UINT32).build();
        final PrimitiveValue primitiveValue = new PrimitiveValue(Integer.toUnsignedLong(value), 4);

        final MutableDirectBuffer buffer = new UnsafeBuffer(new byte[4]);
        buffer.putInt(0, value);

        final String expected = Integer.toUnsignedString(value);

        Types.appendAsString(out, buffer, 0, encoding);
        assertEquals(expected, out.toString());

        out.setLength(0);
        Types.appendAsJsonString(out, buffer, 0, encoding);
        assertEquals(expected, out.toString());

        out.setLength(0);
        Types.appendAsJsonString(out, primitiveValue, encoding);
        assertEquals(expected, out.toString());
    }

    @ParameterizedTest
    @ValueSource(ints = {Integer.MIN_VALUE, -1, 0, 1, Integer.MAX_VALUE})
    void shouldAppendInt32AsString(final int value)
    {
        final StringBuilder out = new StringBuilder();
        final Encoding encoding = new Encoding.Builder().primitiveType(PrimitiveType.INT32).build();
        final PrimitiveValue primitiveValue = new PrimitiveValue(value, 4);

        final MutableDirectBuffer buffer = new UnsafeBuffer(new byte[4]);
        buffer.putInt(0, value);

        final String expected = Integer.toString(value);

        Types.appendAsString(out, buffer, 0, encoding);
        assertEquals(expected, out.toString());

        out.setLength(0);
        Types.appendAsJsonString(out, buffer, 0, encoding);
        assertEquals(expected, out.toString());

        out.setLength(0);
        Types.appendAsJsonString(out, primitiveValue, encoding);
        assertEquals(expected, out.toString());
    }

    @ParameterizedTest
    @ValueSource(longs = {Long.MIN_VALUE, -1L, 0L, 1L, Long.MAX_VALUE})
    void shouldAppendUInt64AsString(final long value)
    {
        final StringBuilder out = new StringBuilder();
        final Encoding encoding = new Encoding.Builder().primitiveType(PrimitiveType.UINT64).build();
        final PrimitiveValue primitiveValue = new PrimitiveValue(value, 1);

        final MutableDirectBuffer buffer = new UnsafeBuffer(new byte[8]);
        buffer.putLong(0, value);

        final String expected = Long.toUnsignedString(value);

        Types.appendAsString(out, buffer, 0, encoding);
        assertEquals(expected, out.toString());

        out.setLength(0);
        Types.appendAsJsonString(out, buffer, 0, encoding);
        assertEquals(expected, out.toString());

        out.setLength(0);
        Types.appendAsJsonString(out, primitiveValue, encoding);
        assertEquals(expected, out.toString());
    }

    @ParameterizedTest
    @ValueSource(longs = {Long.MIN_VALUE, -1L, 0L, 1L, Long.MAX_VALUE})
    void shouldAppendInt64AsString(final long value)
    {
        final StringBuilder out = new StringBuilder();
        final Encoding encoding = new Encoding.Builder().primitiveType(PrimitiveType.INT64).build();
        final PrimitiveValue primitiveValue = new PrimitiveValue(value, 1);

        final MutableDirectBuffer buffer = new UnsafeBuffer(new byte[8]);
        buffer.putLong(0, value);

        final String expected = Long.toString(value);

        Types.appendAsString(out, buffer, 0, encoding);
        assertEquals(expected, out.toString());

        out.setLength(0);
        Types.appendAsJsonString(out, buffer, 0, encoding);
        assertEquals(expected, out.toString());

        out.setLength(0);
        Types.appendAsJsonString(out, primitiveValue, encoding);
        assertEquals(expected, out.toString());
    }

    @ParameterizedTest
    @ValueSource(floats = {Float.MIN_VALUE, -1.0f, 0L, 1.0f, Float.MAX_VALUE, Float.MAX_EXPONENT,
        Float.MIN_EXPONENT, Float.MIN_NORMAL})
    void shouldAppendFloatAsString(final float value)
    {
        final StringBuilder out = new StringBuilder();
        final Encoding encoding = new Encoding.Builder().primitiveType(PrimitiveType.FLOAT).build();
        final PrimitiveValue primitiveValue = new PrimitiveValue(value, 4);

        final MutableDirectBuffer buffer = new UnsafeBuffer(new byte[8]);
        buffer.putFloat(0, value);

        final String expected = Float.toString(value);

        Types.appendAsString(out, buffer, 0, encoding);
        assertEquals(expected, out.toString());

        out.setLength(0);
        Types.appendAsJsonString(out, buffer, 0, encoding);
        assertEquals(expected, out.toString());

        out.setLength(0);
        Types.appendAsJsonString(out, primitiveValue, encoding);
        assertEquals(expected, out.toString());
    }

    @ParameterizedTest
    @ValueSource(doubles = {Double.MIN_VALUE, -1.0, 0.0, 1.0, Double.MAX_VALUE, Double.MAX_EXPONENT,
        Double.MIN_EXPONENT, Double.MIN_NORMAL})
    void shouldAppendDoubleAsString(final double value)
    {
        final StringBuilder out = new StringBuilder();
        final Encoding encoding = new Encoding.Builder().primitiveType(PrimitiveType.DOUBLE).build();
        final PrimitiveValue primitiveValue = new PrimitiveValue(value, 8);

        final MutableDirectBuffer buffer = new UnsafeBuffer(new byte[8]);
        buffer.putDouble(0, value);

        final String expected = Double.toString(value);

        Types.appendAsString(out, buffer, 0, encoding);
        assertEquals(expected, out.toString());

        out.setLength(0);
        Types.appendAsJsonString(out, buffer, 0, encoding);
        assertEquals(expected, out.toString());

        out.setLength(0);
        Types.appendAsJsonString(out, primitiveValue, encoding);
        assertEquals(expected, out.toString());
    }

    @ParameterizedTest
    @ValueSource(floats = {Float.MIN_VALUE, 0.0f, Float.MAX_VALUE, Float.MIN_EXPONENT, Float.MAX_EXPONENT,
        Float.MIN_NORMAL})
    void shouldNotExpandJsonFloatToDouble(final float value)
    {
        final StringBuilder sb = new StringBuilder();
        final Encoding encoding = new Encoding.Builder().primitiveType(PrimitiveType.FLOAT).build();

        final MutableDirectBuffer buffer = new UnsafeBuffer(new byte[4]);
        buffer.putFloat(0, value);

        final String expected = Float.toString(value);

        Types.appendAsJsonString(sb, buffer, 0, encoding);
        assertEquals(expected, sb.toString());

        sb.setLength(0);
        Types.appendAsJsonString(sb, new PrimitiveValue(value, 4), encoding);
        assertEquals(expected, sb.toString());
    }
}
