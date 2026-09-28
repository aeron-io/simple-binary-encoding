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
package uk.co.real_logic.sbe.generation.cpp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.co.real_logic.sbe.PrimitiveType.DOUBLE;
import static uk.co.real_logic.sbe.PrimitiveType.FLOAT;
import static uk.co.real_logic.sbe.PrimitiveType.INT32;
import static uk.co.real_logic.sbe.generation.cpp.CppUtil.generateLiteral;

class CppUtilTest
{
    @Test
    void shouldGenerateFloatingPointInfinityLiterals()
    {
        assertEquals("SBE_FLOAT_INFINITY", generateLiteral(FLOAT, "Infinity"));
        assertEquals("-SBE_FLOAT_INFINITY", generateLiteral(FLOAT, "-Infinity"));
        assertEquals("SBE_DOUBLE_INFINITY", generateLiteral(DOUBLE, "Infinity"));
        assertEquals("-SBE_DOUBLE_INFINITY", generateLiteral(DOUBLE, "-Infinity"));
    }

    @Test
    void shouldPreserveExistingFloatingPointAndIntegerLiterals()
    {
        assertEquals("SBE_FLOAT_NAN", generateLiteral(FLOAT, "NaN"));
        assertEquals("SBE_DOUBLE_NAN", generateLiteral(DOUBLE, "NaN"));
        assertEquals("1.5f", generateLiteral(FLOAT, "1.5"));
        assertEquals("-1.5f", generateLiteral(FLOAT, "-1.5"));
        assertEquals("-0.0f", generateLiteral(FLOAT, "-0.0"));
        assertEquals("1.5", generateLiteral(DOUBLE, "1.5"));
        assertEquals("-1.5", generateLiteral(DOUBLE, "-1.5"));
        assertEquals("-0.0", generateLiteral(DOUBLE, "-0.0"));
        assertEquals("INT32_C(1)", generateLiteral(INT32, "1"));
    }
}
