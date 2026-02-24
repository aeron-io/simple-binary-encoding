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
package uk.co.real_logic.sbe.generation.typescript;

import uk.co.real_logic.sbe.PrimitiveType;
import uk.co.real_logic.sbe.generation.Generators;
import uk.co.real_logic.sbe.ir.Token;

import java.util.EnumMap;
import java.util.Map;

/**
 * Utilities for mapping between IR and the TypeScript language.
 */
public class TypeScriptUtil
{
    private static final Map<PrimitiveType, String> PRIMITIVE_TYPE_MAP = new EnumMap<>(PrimitiveType.class);
    private static final Map<PrimitiveType, String> DATAVIEW_METHOD_MAP = new EnumMap<>(PrimitiveType.class);

    static
    {
        // TypeScript type mappings
        PRIMITIVE_TYPE_MAP.put(PrimitiveType.CHAR, "number");
        PRIMITIVE_TYPE_MAP.put(PrimitiveType.INT8, "number");
        PRIMITIVE_TYPE_MAP.put(PrimitiveType.INT16, "number");
        PRIMITIVE_TYPE_MAP.put(PrimitiveType.INT32, "number");
        PRIMITIVE_TYPE_MAP.put(PrimitiveType.INT64, "bigint");
        PRIMITIVE_TYPE_MAP.put(PrimitiveType.UINT8, "number");
        PRIMITIVE_TYPE_MAP.put(PrimitiveType.UINT16, "number");
        PRIMITIVE_TYPE_MAP.put(PrimitiveType.UINT32, "number");
        PRIMITIVE_TYPE_MAP.put(PrimitiveType.UINT64, "bigint");
        PRIMITIVE_TYPE_MAP.put(PrimitiveType.FLOAT, "number");
        PRIMITIVE_TYPE_MAP.put(PrimitiveType.DOUBLE, "number");

        // DataView method mappings
        DATAVIEW_METHOD_MAP.put(PrimitiveType.CHAR, "getUint8");
        DATAVIEW_METHOD_MAP.put(PrimitiveType.INT8, "getInt8");
        DATAVIEW_METHOD_MAP.put(PrimitiveType.INT16, "getInt16");
        DATAVIEW_METHOD_MAP.put(PrimitiveType.INT32, "getInt32");
        DATAVIEW_METHOD_MAP.put(PrimitiveType.INT64, "getBigInt64");
        DATAVIEW_METHOD_MAP.put(PrimitiveType.UINT8, "getUint8");
        DATAVIEW_METHOD_MAP.put(PrimitiveType.UINT16, "getUint16");
        DATAVIEW_METHOD_MAP.put(PrimitiveType.UINT32, "getUint32");
        DATAVIEW_METHOD_MAP.put(PrimitiveType.UINT64, "getBigUint64");
        DATAVIEW_METHOD_MAP.put(PrimitiveType.FLOAT, "getFloat32");
        DATAVIEW_METHOD_MAP.put(PrimitiveType.DOUBLE, "getFloat64");
    }

    /**
     * Map an SBE {@link PrimitiveType} to a TypeScript primitive type name.
     *
     * @param primitiveType to map.
     * @return the TypeScript type name.
     */
    public static String typeScriptTypeName(final PrimitiveType primitiveType)
    {
        return PRIMITIVE_TYPE_MAP.get(primitiveType);
    }

    /**
     * Map an SBE {@link PrimitiveType} to a DataView method name for reading.
     *
     * @param primitiveType to map.
     * @return the DataView method name.
     */
    public static String dataViewMethod(final PrimitiveType primitiveType)
    {
        return DATAVIEW_METHOD_MAP.get(primitiveType);
    }

    /**
     * Check if a DataView method requires the littleEndian parameter.
     *
     * @param primitiveType to check.
     * @return true if the method requires the littleEndian parameter.
     */
    public static boolean needsEndianness(final PrimitiveType primitiveType)
    {
        // INT8, UINT8 don't need endianness parameter
        return primitiveType != PrimitiveType.INT8 && primitiveType != PrimitiveType.UINT8 &&
            primitiveType != PrimitiveType.CHAR;
    }

    /**
     * Generate a TypeScript literal value for a given primitive type and value.
     *
     * @param type  the primitive type.
     * @param value the value as a string.
     * @return the TypeScript literal.
     */
    public static String generateLiteral(final PrimitiveType type, final String value)
    {
        switch (type)
        {
            case CHAR:
            case INT8:
            case UINT8:
            case INT16:
            case UINT16:
            case INT32:
            case UINT32:
                return value;

            case INT64:
            case UINT64:
                // BigInt literals need 'n' suffix
                return value + "n";

            case FLOAT:
                if (value.endsWith("NaN"))
                {
                    return "NaN";
                }
                return value;

            case DOUBLE:
                if (value.endsWith("NaN"))
                {
                    return "NaN";
                }
                return value;

            default:
                return value;
        }
    }

    /**
     * Generate a TypeScript null value literal for a given primitive type.
     *
     * @param type the primitive type.
     * @return the TypeScript null value literal.
     */
    public static String generateNullValueLiteral(final PrimitiveType type)
    {
        switch (type)
        {
            case CHAR:
            case UINT8:
                return "255";

            case INT8:
                return "-128";

            case INT16:
                return "-32768";

            case UINT16:
                return "65535";

            case INT32:
                return "-2147483648";

            case UINT32:
                return "4294967295";

            case INT64:
                return "-9223372036854775808n";

            case UINT64:
                return "18446744073709551615n";

            case FLOAT:
                return "NaN";

            case DOUBLE:
                return "NaN";

            default:
                return "null";
        }
    }

    /**
     * Format a class/type name in PascalCase.
     *
     * @param value the string to format.
     * @return the formatted string.
     */
    public static String formatTypeName(final String value)
    {
        return Generators.toUpperFirstChar(value);
    }

    /**
     * Format a field/property name in camelCase.
     *
     * @param value the string to format.
     * @return the formatted string.
     */
    public static String formatFieldName(final String value)
    {
        return Generators.toLowerFirstChar(value);
    }

    /**
     * Format a constant name in UPPER_SNAKE_CASE.
     *
     * @param value the string to format.
     * @return the formatted string.
     */
    public static String formatConstantName(final String value)
    {
        return value.toUpperCase().replaceAll("([a-z])([A-Z])", "$1_$2").replace(' ', '_');
    }

    /**
     * Generate file header with auto-generated comment.
     *
     * @param packageName the package/namespace name.
     * @return the file header string.
     */
    public static String generateFileHeader(final String packageName)
    {
        return "/**\n" +
            " * Auto-generated by SBE (Simple Binary Encoding)\n" +
            " * Schema: " + packageName + "\n" +
            " * DO NOT EDIT\n" +
            " */\n\n";
    }

    /**
     * Generate JSDoc documentation from token description.
     *
     * @param indent the indentation string.
     * @param token  the token containing description.
     * @return the JSDoc string or empty if no description.
     */
    public static String generateDocumentation(final String indent, final Token token)
    {
        final String description = token.description();
        if (null == description || description.isEmpty())
        {
            return "";
        }

        return indent + "/**\n" +
            indent + " * " + description + "\n" +
            indent + " */\n";
    }

    /**
     * Check if a name is a TypeScript reserved keyword.
     *
     * @param name the name to check.
     * @return true if the name is a reserved keyword.
     */
    public static boolean isReservedKeyword(final String name)
    {
        switch (name)
        {
            case "break":
            case "case":
            case "catch":
            case "class":
            case "const":
            case "continue":
            case "debugger":
            case "default":
            case "delete":
            case "do":
            case "else":
            case "enum":
            case "export":
            case "extends":
            case "false":
            case "finally":
            case "for":
            case "function":
            case "if":
            case "import":
            case "in":
            case "instanceof":
            case "new":
            case "null":
            case "return":
            case "super":
            case "switch":
            case "this":
            case "throw":
            case "true":
            case "try":
            case "typeof":
            case "var":
            case "void":
            case "while":
            case "with":
            case "as":
            case "implements":
            case "interface":
            case "let":
            case "package":
            case "private":
            case "protected":
            case "public":
            case "static":
            case "yield":
            case "any":
            case "boolean":
            case "constructor":
            case "declare":
            case "get":
            case "module":
            case "require":
            case "number":
            case "set":
            case "string":
            case "symbol":
            case "type":
            case "from":
            case "of":
                return true;

            default:
                return false;
        }
    }

    /**
     * Escape a name if it's a TypeScript reserved keyword.
     *
     * @param name the name to escape.
     * @return the escaped name with underscore prefix if needed.
     */
    public static String escapeName(final String name)
    {
        return isReservedKeyword(name) ? "_" + name : name;
    }
}
