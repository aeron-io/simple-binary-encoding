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
package uk.co.real_logic.sbe.generation.php;

import uk.co.real_logic.sbe.PrimitiveType;
import uk.co.real_logic.sbe.SbeTool;
import uk.co.real_logic.sbe.ValidationUtil;

import java.util.EnumMap;
import java.util.Map;

/**
 * Utilities for mapping between IR and the PHP language.
 */
public class PHPUtil
{
    private static final Map<PrimitiveType, String> PRIMITIVE_TYPE_STRING_ENUM_MAP = new EnumMap<>(PrimitiveType.class);

    static
    {
        PRIMITIVE_TYPE_STRING_ENUM_MAP.put(PrimitiveType.CHAR, "int");
        PRIMITIVE_TYPE_STRING_ENUM_MAP.put(PrimitiveType.INT8, "int");
        PRIMITIVE_TYPE_STRING_ENUM_MAP.put(PrimitiveType.INT16, "int");
        PRIMITIVE_TYPE_STRING_ENUM_MAP.put(PrimitiveType.INT32, "int");
        PRIMITIVE_TYPE_STRING_ENUM_MAP.put(PrimitiveType.INT64, "int");
        PRIMITIVE_TYPE_STRING_ENUM_MAP.put(PrimitiveType.UINT8, "int");
        PRIMITIVE_TYPE_STRING_ENUM_MAP.put(PrimitiveType.UINT16, "int");
        PRIMITIVE_TYPE_STRING_ENUM_MAP.put(PrimitiveType.UINT32, "int");
        PRIMITIVE_TYPE_STRING_ENUM_MAP.put(PrimitiveType.UINT64, "int");
        PRIMITIVE_TYPE_STRING_ENUM_MAP.put(PrimitiveType.FLOAT, "float");
        PRIMITIVE_TYPE_STRING_ENUM_MAP.put(PrimitiveType.DOUBLE, "float");
    }

    /**
     * Map the name of a {@link uk.co.real_logic.sbe.PrimitiveType} to a PHP type name.
     *
     * @param primitiveType to map.
     * @return the name of the PHP type that most closely maps.
     */
    public static String phpTypeName(final PrimitiveType primitiveType)
    {
        return PRIMITIVE_TYPE_STRING_ENUM_MAP.get(primitiveType);
    }

    private static final Map<PrimitiveType, String> PACK_FORMAT_BY_PRIMITIVE_TYPE_MAP =
        new EnumMap<>(PrimitiveType.class);

    static
    {
        PACK_FORMAT_BY_PRIMITIVE_TYPE_MAP.put(PrimitiveType.CHAR, "C");
        PACK_FORMAT_BY_PRIMITIVE_TYPE_MAP.put(PrimitiveType.INT8, "c");
        PACK_FORMAT_BY_PRIMITIVE_TYPE_MAP.put(PrimitiveType.INT16, "s");
        PACK_FORMAT_BY_PRIMITIVE_TYPE_MAP.put(PrimitiveType.INT32, "l");
        PACK_FORMAT_BY_PRIMITIVE_TYPE_MAP.put(PrimitiveType.INT64, "q");
        PACK_FORMAT_BY_PRIMITIVE_TYPE_MAP.put(PrimitiveType.UINT8, "C");
        PACK_FORMAT_BY_PRIMITIVE_TYPE_MAP.put(PrimitiveType.UINT16, "v");
        PACK_FORMAT_BY_PRIMITIVE_TYPE_MAP.put(PrimitiveType.UINT32, "V");
        PACK_FORMAT_BY_PRIMITIVE_TYPE_MAP.put(PrimitiveType.UINT64, "P");
        PACK_FORMAT_BY_PRIMITIVE_TYPE_MAP.put(PrimitiveType.FLOAT, "f");
        PACK_FORMAT_BY_PRIMITIVE_TYPE_MAP.put(PrimitiveType.DOUBLE, "d");
    }

    /**
     * Map the name of a {@link uk.co.real_logic.sbe.PrimitiveType} to a PHP pack() format character.
     *
     * @param primitiveType to map.
     * @return the pack format character.
     */
    public static String phpPackFormat(final PrimitiveType primitiveType)
    {
        return PACK_FORMAT_BY_PRIMITIVE_TYPE_MAP.get(primitiveType);
    }

    /**
     * Get the size in bytes of a primitive type.
     *
     * @param primitiveType to get size for.
     * @return size in bytes.
     */
    public static int primitiveTypeSize(final PrimitiveType primitiveType)
    {
        switch (primitiveType)
        {
            case CHAR:
            case INT8:
            case UINT8:
                return 1;
            case INT16:
            case UINT16:
                return 2;
            case INT32:
            case UINT32:
            case FLOAT:
                return 4;
            case INT64:
            case UINT64:
            case DOUBLE:
                return 8;
            default:
                throw new IllegalArgumentException("Unknown primitive type: " + primitiveType);
        }
    }

    /**
     * Format a String as a property name (camelCase for PHP).
     *
     * @param value to be formatted.
     * @return the string formatted as a property name.
     */
    public static String formatPropertyName(final String value)
    {
        String formattedValue = value;

        if (ValidationUtil.isPhpKeyword(formattedValue))
        {
            final String keywordAppendToken = System.getProperty(SbeTool.KEYWORD_APPEND_TOKEN);
            if (null == keywordAppendToken)
            {
                throw new IllegalStateException(
                    "Invalid property name='" + formattedValue +
                    "' please correct the schema or consider setting system property: " + SbeTool.KEYWORD_APPEND_TOKEN);
            }

            formattedValue += keywordAppendToken;
        }

        return formattedValue;
    }

    /**
     * Format a String as a class name (PascalCase for PHP).
     *
     * @param value to be formatted.
     * @return the string formatted as a class name.
     */
    public static String formatClassName(final String value)
    {
        if (value == null || value.isEmpty())
        {
            return value;
        }
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }
}
