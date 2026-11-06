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

import dto_test.ExtendedCarDecoder;
import dto_test.ExtendedCarDto;
import dto_test.ExtendedCarEncoder;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.nio.ByteOrder;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DtoVarDataLengthTest
{
    private static final int MAX_FITTING_LENGTH = 8;
    // added5 (uint32 length) and varCharData (uint16 length) follow activationCode, both left empty.
    private static final int TRAILING_HEADERS_LENGTH = 4 + 2;

    // ExtendedCar with empty groups and strings, whose activationCode length prefix is followed by
    // MAX_FITTING_LENGTH bytes and the empty trailing var data.
    private static UnsafeBuffer carWithActivationCodeLength(final int length)
    {
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[256]);
        final ExtendedCarEncoder encoder = new ExtendedCarEncoder().wrap(buffer, 0);
        encoder.fuelFiguresCount(0);
        encoder.performanceFiguresCount(0);
        encoder.manufacturer("");
        encoder.model("");
        final int activationCodeOffset = encoder.limit();

        final UnsafeBuffer message = new UnsafeBuffer(
            new byte[activationCodeOffset + 4 + MAX_FITTING_LENGTH + TRAILING_HEADERS_LENGTH]);
        message.putBytes(0, buffer, 0, activationCodeOffset);
        message.putInt(activationCodeOffset, length, ByteOrder.LITTLE_ENDIAN);
        return message;
    }

    @ParameterizedTest
    @ValueSource(ints = { Integer.MAX_VALUE, -1, MAX_FITTING_LENGTH + TRAILING_HEADERS_LENGTH + 1 })
    void shouldRejectByteArrayLengthBeyondBufferBeforeAllocating(final int length)
    {
        final UnsafeBuffer buffer = carWithActivationCodeLength(length);

        assertThrows(
            IndexOutOfBoundsException.class,
            () -> ExtendedCarDto.decodeFrom(
                buffer, 0, ExtendedCarDecoder.BLOCK_LENGTH, ExtendedCarDecoder.SCHEMA_VERSION));
    }

    @ParameterizedTest
    @ValueSource(ints = { 0, 3, MAX_FITTING_LENGTH })
    void shouldDecodeByteArrayThatFitsInBuffer(final int length)
    {
        final UnsafeBuffer buffer = carWithActivationCodeLength(length);
        final byte[] expected = new byte[length];
        for (int i = 0; i < length; i++)
        {
            expected[i] = (byte)(i + 1);
        }
        buffer.putBytes(buffer.capacity() - TRAILING_HEADERS_LENGTH - MAX_FITTING_LENGTH, expected);

        final ExtendedCarDto dto = ExtendedCarDto.decodeFrom(
            buffer, 0, ExtendedCarDecoder.BLOCK_LENGTH, ExtendedCarDecoder.SCHEMA_VERSION);

        assertArrayEquals(expected, dto.activationCode());
    }
}
