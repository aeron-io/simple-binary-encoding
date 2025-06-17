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

/**
 * Helpers for generating value validation code.
 */
public final class DtoValidationUtil
{
    private DtoValidationUtil()
    {
    }

    /**
     * What support the target language has for native integer types.
     */
    public enum NativeIntegerSupport
    {
        /**
         * The target language supports both signed and unsigned integers natively and the generated code uses
         * these to represent the SBE types.
         */
        SIGNED_AND_UNSIGNED,

        /**
         * The target language only supports signed integers natively and the generated code uses the next biggest
         * signed integer type to represent the unsigned SBE types, except for UINT64 which is always represented
         * as a signed long.
         */
        SIGNED_ONLY
    }

    /**
     * Checks if the native type can represent values less than the valid range of the SBE type.
     *
     * @param fieldToken     the field token to check if it is optional.
     * @param encoding       the encoding of the field to check the applicable minimum and null values.
     * @param integerSupport the support for native integer types in the target language.
     * @return true if the native type can represent values less than the valid range of the SBE type,
     * false otherwise.
     */
    public static boolean nativeTypeRepresentsValuesLessThanValidRange(
        final Token fieldToken,
        final Encoding encoding,
        final NativeIntegerSupport integerSupport)
    {
        final PrimitiveType primitiveType = encoding.primitiveType();
        final PrimitiveValue minValue = encoding.applicableMinValue();

        switch (minValue.representation())
        {
            case LONG:
                final long nativeMinValue = nativeTypeMinValue(primitiveType, integerSupport);
                final PrimitiveValue nullValue = encoding.applicableNullValue();
                final boolean gapBefore = minValue.longValue() > nativeMinValue;
                final boolean nullFillsGap = fieldToken.isOptionalEncoding() &&
                    nullValue.longValue() == nativeMinValue &&
                    minValue.longValue() == nativeMinValue + 1L;
                return gapBefore && !nullFillsGap;

            case DOUBLE:
                switch (primitiveType)
                {
                    case FLOAT:
                        return minValue.doubleValue() > -Float.MAX_VALUE;
                    case DOUBLE:
                        return minValue.doubleValue() > -Double.MAX_VALUE;
                    default:
                        throw new IllegalArgumentException(
                            "Type did not have a double representation: " + primitiveType);
                }

            default:
                throw new IllegalArgumentException(
                    "Cannot understand the range of a type with representation: " + minValue.representation());
        }
    }

    /**
     * Checks if the native type can represent values greater than the valid range of the SBE type.
     *
     * @param fieldToken     the field token to check if it is optional.
     * @param encoding       the encoding of the field to check the applicable maximum and null values.
     * @param integerSupport the support for native integer types in the target language.
     * @return true if the native type can represent values greater than the valid range of the SBE type,
     * false otherwise.
     */
    public static boolean nativeTypeRepresentsValuesGreaterThanValidRange(
        final Token fieldToken,
        final Encoding encoding,
        final NativeIntegerSupport integerSupport)
    {
        final PrimitiveType primitiveType = encoding.primitiveType();
        final PrimitiveValue maxValue = encoding.applicableMaxValue();

        switch (maxValue.representation())
        {
            case LONG:
                final long nativeMaxValue = nativeTypeMaxValue(primitiveType, integerSupport);
                final PrimitiveValue nullValue = encoding.applicableNullValue();
                final boolean gapAfter = maxValue.longValue() < nativeMaxValue;
                final boolean nullFillsGap = fieldToken.isOptionalEncoding() &&
                    nullValue.longValue() == nativeMaxValue &&
                    maxValue.longValue() + 1L == nativeMaxValue;
                return gapAfter && !nullFillsGap;

            case DOUBLE:
                switch (primitiveType)
                {
                    case FLOAT:
                        return maxValue.doubleValue() < Float.MAX_VALUE;
                    case DOUBLE:
                        return maxValue.doubleValue() < Double.MAX_VALUE;
                    default:
                        throw new IllegalArgumentException(
                            "Type did not have a double representation: " + primitiveType);
                }

            default:
                throw new IllegalArgumentException(
                    "Cannot understand the range of a type with representation: " + maxValue.representation());
        }
    }

    private static long nativeTypeMinValue(
        final PrimitiveType primitiveType,
        final NativeIntegerSupport integerSupport)
    {
        switch (primitiveType)
        {
            case CHAR:
                return Character.MIN_VALUE;
            case INT8:
                return Byte.MIN_VALUE;
            case INT16:
                return Short.MIN_VALUE;
            case INT32:
                return Integer.MIN_VALUE;
            case INT64:
                return Long.MIN_VALUE;
            case UINT8:
                if (integerSupport == NativeIntegerSupport.SIGNED_ONLY)
                {
                    return Short.MIN_VALUE;
                }
                return 0L;
            case UINT16:
                if (integerSupport == NativeIntegerSupport.SIGNED_ONLY)
                {
                    return Integer.MIN_VALUE;
                }
                return 0L;
            case UINT32:
                if (integerSupport == NativeIntegerSupport.SIGNED_ONLY)
                {
                    return Long.MIN_VALUE;
                }
                return 0L;
            case UINT64:
                return 0L;
            default:
                throw new IllegalArgumentException("Type did not have a long representation: " + primitiveType);
        }
    }

    private static long nativeTypeMaxValue(
        final PrimitiveType primitiveType,
        final NativeIntegerSupport integerSupport)
    {
        switch (primitiveType)
        {
            case CHAR:
                return Character.MAX_VALUE;
            case INT8:
                return Byte.MAX_VALUE;
            case INT16:
                return Short.MAX_VALUE;
            case INT32:
                return Integer.MAX_VALUE;
            case INT64:
                return Long.MAX_VALUE;
            case UINT8:
                if (integerSupport == NativeIntegerSupport.SIGNED_ONLY)
                {
                    return Short.MAX_VALUE;
                }
                return 0xFFL;
            case UINT16:
                if (integerSupport == NativeIntegerSupport.SIGNED_ONLY)
                {
                    return Integer.MAX_VALUE;
                }
                return 0xFFFFL;
            case UINT32:
                if (integerSupport == NativeIntegerSupport.SIGNED_ONLY)
                {
                    return Long.MAX_VALUE;
                }
                return 0xFFFFFFFFL;
            case UINT64:
                return 0xFFFFFFFFFFFFFFFFL;
            default:
                throw new IllegalArgumentException("Type did not have a long representation: " + primitiveType);
        }
    }
}
