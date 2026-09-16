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

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SbeJsonExceptionTest
{
    @Test
    void shouldFormatMessageWithAllParts()
    {
        final SbeJsonException ex = new SbeJsonException(
            ErrorCode.OUT_OF_RANGE, 1, 42, "Car.engine.capacity", "value 70000 > 65534");

        assertEquals(ErrorCode.OUT_OF_RANGE, ex.code());
        assertEquals(1, ex.templateId());
        assertEquals(42, ex.byteOffset());
        assertEquals("Car.engine.capacity", ex.path());
        assertEquals("OUT_OF_RANGE templateId=1 path=Car.engine.capacity offset=42: value 70000 > 65534",
            ex.getMessage());
    }

    @Test
    void shouldOmitAbsentParts()
    {
        final SbeJsonException ex = new SbeJsonException(
            ErrorCode.FRAME_OVERFLOW, SbeJsonException.NO_TEMPLATE_ID, SbeJsonException.NO_OFFSET, null, null);

        assertEquals("FRAME_OVERFLOW", ex.getMessage());
        assertNull(ex.path());
    }

    @Test
    void shouldSuppressStackTraceWhenRequested()
    {
        final SbeJsonException ex = new SbeJsonException(
            ErrorCode.LIMIT_EXCEEDED, 1, SbeJsonException.NO_OFFSET, "Car.fuelFigures", "too many", false);

        assertEquals(0, ex.getStackTrace().length);
        assertFalse(ex.getMessage().isEmpty());
    }

    @Test
    void limitsBuilderRejectsNegativeValues()
    {
        assertThrows(IllegalArgumentException.class, () -> Limits.builder().maxDepth(-1));
        assertEquals(Limits.DEFAULT_MAX_DEPTH, Limits.defaults().maxDepth());
    }
}
