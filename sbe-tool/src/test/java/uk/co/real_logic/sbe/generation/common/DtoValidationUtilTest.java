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
package uk.co.real_logic.sbe.generation.common;

import uk.co.real_logic.sbe.PrimitiveType;
import uk.co.real_logic.sbe.PrimitiveValue;
import uk.co.real_logic.sbe.ir.Encoding;
import uk.co.real_logic.sbe.ir.Token;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.co.real_logic.sbe.generation.common.DtoValidationUtil.nativeTypeRepresentsValuesGreaterThanValidRange;
import static uk.co.real_logic.sbe.generation.common.DtoValidationUtil.nativeTypeRepresentsValuesLessThanValidRange;

public class DtoValidationUtilTest
{
    @ParameterizedTest
    @CsvSource({
        "int8,SIGNED_AND_UNSIGNED,false,-128,-127,127,true,false",
        "int8,SIGNED_AND_UNSIGNED,true,-128,-127,127,false,false",
        "int8,SIGNED_ONLY,false,-128,-127,127,true,false",
        "int8,SIGNED_ONLY,true,-128,-127,127,false,false",

        "int8,SIGNED_AND_UNSIGNED,false,127,-128,126,false,true",
        "int8,SIGNED_AND_UNSIGNED,true,127,-128,126,false,false",
        "int8,SIGNED_ONLY,false,127,-128,126,false,true",
        "int8,SIGNED_ONLY,true,127,-128,126,false,false",

        "int8,SIGNED_ONLY,true,-128,-100,127,true,false",
        "int8,SIGNED_ONLY,true,127,-128,100,false,true",

        "int8,SIGNED_ONLY,true,0,-128,127,false,false",
        "int8,SIGNED_ONLY,true,0,-127,127,true,false",
        "int8,SIGNED_ONLY,true,0,-128,126,false,true",
        "int8,SIGNED_ONLY,true,0,-127,126,true,true",

        "uint8,SIGNED_AND_UNSIGNED,false,255,0,254,false,true",
        "uint8,SIGNED_AND_UNSIGNED,true,255,0,254,false,false",
        "uint8,SIGNED_ONLY,false,255,0,254,true,true",
        "uint8,SIGNED_ONLY,true,255,0,254,true,true",

        "float,SIGNED_AND_UNSIGNED,false,-2,-1,1,true,true",
        "float,SIGNED_AND_UNSIGNED,true,-2,-1,1,true,true",
        "float,SIGNED_ONLY,false,-2,-1,1,true,true",
        "float,SIGNED_ONLY,true,-2,-1,1,true,true",

        "uint64,SIGNED_AND_UNSIGNED,true,18446744073709551615,0,18446744073709551614,false,false",
        "uint64,SIGNED_AND_UNSIGNED,true,18446744073709551615,1,18446744073709551614,true,false",
        "uint64,SIGNED_AND_UNSIGNED,true,18446744073709551615,0,18446744073709551613,false,true",
        "uint64,SIGNED_AND_UNSIGNED,true,18446744073709551615,1,18446744073709551613,true,true",
        "uint64,SIGNED_ONLY,true,18446744073709551615,0,18446744073709551614,false,false",
        "uint64,SIGNED_ONLY,true,18446744073709551615,1,18446744073709551614,true,false",
        "uint64,SIGNED_ONLY,true,18446744073709551615,0,18446744073709551613,false,true",
        "uint64,SIGNED_ONLY,true,18446744073709551615,1,18446744073709551613,true,true",
    })
    void shouldGenerateValidationBasedOnNativeRangeVersusSbeTypeRange(
        final String type,
        final DtoValidationUtil.NativeIntegerSupport integerSupport,
        final boolean isOptional,
        final String nullValue,
        final String minValue,
        final String maxValue,
        final boolean shouldValidateBelow,
        final boolean shouldValidateAbove)
    {
        final Token fieldToken = mock(Token.class);
        when(fieldToken.isOptionalEncoding()).thenReturn(isOptional);
        final Encoding encoding = mock(Encoding.class);
        final PrimitiveType primitiveType = PrimitiveType.get(type);
        when(encoding.primitiveType()).thenReturn(primitiveType);
        when(encoding.applicableNullValue()).thenReturn(PrimitiveValue.parse(nullValue, primitiveType));
        when(encoding.applicableMinValue()).thenReturn(PrimitiveValue.parse(minValue, primitiveType));
        when(encoding.applicableMaxValue()).thenReturn(PrimitiveValue.parse(maxValue, primitiveType));

        final boolean validatesBelow =
            nativeTypeRepresentsValuesLessThanValidRange(fieldToken, encoding, integerSupport);

        final boolean validatesAbove =
            nativeTypeRepresentsValuesGreaterThanValidRange(fieldToken, encoding, integerSupport);

        assertEquals(
            shouldValidateBelow, validatesBelow,
            shouldValidateBelow ? "should" : "should not" + " validate below");

        assertEquals(
            shouldValidateAbove, validatesAbove,
            shouldValidateAbove ? "should" : "should not" + " validate above");
    }
}
