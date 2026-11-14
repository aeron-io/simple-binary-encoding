/*
 * Copyright 2026 Real Logic Limited.
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
package uk.co.real_logic.sbe.generation.java;

import extension.CarDecoder;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.ByteOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * The extension Car schema has uint32 length prefixes on manufacturer, model and activationCode.
 */
class VarDataUint32LengthTest
{
    private static final int GROUP_HEADER_LENGTH = 4;
    private static final int MANUFACTURER_OFFSET = CarDecoder.BLOCK_LENGTH + 2 * GROUP_HEADER_LENGTH;
    private static final int MODEL_OFFSET = MANUFACTURER_OFFSET + 4;
    private static final int ACTIVATION_CODE_OFFSET = MODEL_OFFSET + 4;
    private static final int PAYLOAD_LENGTH = 12;
    private static final int CAPACITY = ACTIVATION_CODE_OFFSET + 4 + PAYLOAD_LENGTH;
    private static final int MANUFACTURER_BYTES_AVAILABLE = CAPACITY - MANUFACTURER_OFFSET - 4;
    private static final int ACTIVATION_CODE_BYTES_AVAILABLE = PAYLOAD_LENGTH;

    private interface Accessor
    {
        void access(CarDecoder decoder);
    }

    private static UnsafeBuffer buffer(final int manufacturerLength, final int activationCodeLength)
    {
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[CAPACITY]);
        buffer.putInt(MANUFACTURER_OFFSET, manufacturerLength, ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(ACTIVATION_CODE_OFFSET, activationCodeLength, ByteOrder.LITTLE_ENDIAN);
        return buffer;
    }

    private static CarDecoder decoderAtManufacturer(final UnsafeBuffer buffer)
    {
        final CarDecoder decoder = new CarDecoder();
        decoder.wrap(buffer, 0, CarDecoder.BLOCK_LENGTH, CarDecoder.SCHEMA_VERSION);
        assertEquals(0, decoder.fuelFigures().count());
        assertEquals(0, decoder.performanceFigures().count());
        assertEquals(MANUFACTURER_OFFSET, decoder.limit());
        return decoder;
    }

    private static CarDecoder decoderAtActivationCode(final UnsafeBuffer buffer)
    {
        final CarDecoder decoder = decoderAtManufacturer(buffer);
        decoder.skipManufacturer();
        decoder.skipModel();
        assertEquals(ACTIVATION_CODE_OFFSET, decoder.limit());
        return decoder;
    }

    private static void assertRejectedAtManufacturer(final int length, final Accessor accessor)
    {
        final CarDecoder decoder = decoderAtManufacturer(buffer(length, 0));

        assertThrows(IndexOutOfBoundsException.class, () -> accessor.access(decoder));
        assertEquals(MANUFACTURER_OFFSET, decoder.limit());
    }

    @ParameterizedTest
    @ValueSource(ints = { Integer.MIN_VALUE, -1, MANUFACTURER_BYTES_AVAILABLE + 1 })
    void shouldRejectSkipWithLengthBeyondBuffer(final int length)
    {
        assertRejectedAtManufacturer(length, CarDecoder::skipManufacturer);
    }

    @ParameterizedTest
    @ValueSource(ints = { Integer.MIN_VALUE, -1, MANUFACTURER_BYTES_AVAILABLE + 1 })
    void shouldRejectWrapWithLengthBeyondBuffer(final int length)
    {
        assertRejectedAtManufacturer(length, (decoder) -> decoder.wrapManufacturer(new UnsafeBuffer()));
    }

    @ParameterizedTest
    @ValueSource(ints = { Integer.MIN_VALUE, -1, MANUFACTURER_BYTES_AVAILABLE + 1 })
    void shouldRejectGetIntoMutableDirectBufferWithLengthBeyondBuffer(final int length)
    {
        assertRejectedAtManufacturer(
            length, (decoder) -> decoder.getManufacturer(new UnsafeBuffer(new byte[4]), 0, 4));
    }

    @ParameterizedTest
    @ValueSource(ints = { Integer.MIN_VALUE, -1, MANUFACTURER_BYTES_AVAILABLE + 1 })
    void shouldRejectGetIntoByteArrayWithLengthBeyondBuffer(final int length)
    {
        assertRejectedAtManufacturer(length, (decoder) -> decoder.getManufacturer(new byte[4], 0, 4));
    }

    @ParameterizedTest
    @ValueSource(ints = { Integer.MIN_VALUE, -1, ACTIVATION_CODE_BYTES_AVAILABLE + 1 })
    void shouldRejectGetIntoAppendableWithLengthBeyondBuffer(final int length)
    {
        final CarDecoder decoder = decoderAtActivationCode(buffer(0, length));

        assertThrows(IndexOutOfBoundsException.class, () -> decoder.getActivationCode(new StringBuilder()));
        assertEquals(ACTIVATION_CODE_OFFSET, decoder.limit());
    }

    @Test
    void shouldAdvanceLimitPastVarDataThatFitsInBuffer()
    {
        final UnsafeBuffer buffer = buffer(MANUFACTURER_BYTES_AVAILABLE, 0);
        final CarDecoder decoder = decoderAtManufacturer(buffer);

        assertEquals(MANUFACTURER_BYTES_AVAILABLE, decoder.skipManufacturer());
        assertEquals(CAPACITY, decoder.limit());
    }

    @Test
    void shouldDecodeVarDataThatExactlyFillsBuffer()
    {
        final UnsafeBuffer buffer = buffer(0, PAYLOAD_LENGTH);
        buffer.putStringWithoutLengthAscii(ACTIVATION_CODE_OFFSET + 4, "abcdefghijkl");
        final CarDecoder decoder = decoderAtActivationCode(buffer);
        final StringBuilder sb = new StringBuilder();

        assertEquals(PAYLOAD_LENGTH, decoder.getActivationCode(sb));
        assertEquals("abcdefghijkl", sb.toString());
        assertEquals(buffer.capacity(), decoder.limit());
    }
}
