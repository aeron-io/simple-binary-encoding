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
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class VarDataStringLengthTest
{
    private static final int GROUP_HEADER_LENGTH = 4;
    private static final int MANUFACTURER_OFFSET = CarDecoder.BLOCK_LENGTH + 2 * GROUP_HEADER_LENGTH;
    private static final int MAX_FITTING_LENGTH = 12;

    // Car with empty groups and a manufacturer length prefix followed by MAX_FITTING_LENGTH bytes.
    private static UnsafeBuffer carWithManufacturerLength(final int length)
    {
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[MANUFACTURER_OFFSET + 4 + MAX_FITTING_LENGTH]);
        buffer.putInt(MANUFACTURER_OFFSET, length, ByteOrder.LITTLE_ENDIAN);
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

    @ParameterizedTest
    @ValueSource(ints = { Integer.MAX_VALUE, -1, MAX_FITTING_LENGTH + 1 })
    void shouldRejectStringLengthBeyondBufferBeforeAllocating(final int length)
    {
        final CarDecoder decoder = decoderAtManufacturer(carWithManufacturerLength(length));

        assertThrows(IndexOutOfBoundsException.class, decoder::manufacturer);
        assertEquals(MANUFACTURER_OFFSET, decoder.limit());
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 6, MAX_FITTING_LENGTH })
    void shouldDecodeStringThatFitsInBuffer(final int length)
    {
        final String manufacturer = "abcdefghijkl".substring(0, length);
        final UnsafeBuffer buffer = carWithManufacturerLength(length);
        buffer.putBytes(MANUFACTURER_OFFSET + 4, manufacturer.getBytes(StandardCharsets.UTF_8));

        assertEquals(manufacturer, decoderAtManufacturer(buffer).manufacturer());
    }
}
