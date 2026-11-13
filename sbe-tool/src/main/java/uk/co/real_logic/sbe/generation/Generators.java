/*
 * Copyright 2013-2026 Real Logic Limited.
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
package uk.co.real_logic.sbe.generation;

import uk.co.real_logic.sbe.ir.Signal;
import uk.co.real_logic.sbe.ir.Token;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.BiConsumer;

/**
 * Common operations available to all {@link CodeGenerator}s.
 */
public class Generators
{
    /**
     * For each field found in a list of field {@link Token}s take the field token and following type token to
     * a {@link BiConsumer}.
     *
     * @param tokens   to be iterated over.
     * @param consumer to for the field and encoding token pair.
     */
    public static void forEachField(final List<Token> tokens, final BiConsumer<Token, Token> consumer)
    {
        for (int i = 0, size = tokens.size(); i < size;)
        {
            final Token fieldToken = tokens.get(i);
            if (fieldToken.signal() == Signal.BEGIN_FIELD)
            {
                final Token typeToken = tokens.get(i + 1);
                consumer.accept(fieldToken, typeToken);
                i += fieldToken.componentTokenCount();
            }
            else
            {
                ++i;
            }
        }
    }

    /**
     * Uppercase the first character of a given String.
     *
     * @param s to have the first character upper-cased.
     * @return a new String with the first character in uppercase.
     */
    public static String toUpperFirstChar(final String s)
    {
        if (Character.isUpperCase(s.charAt(0)))
        {
            return s;
        }

        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    /**
     * Lowercase the first character of a given String.
     *
     * @param s to have the first character upper-cased.
     * @return a new String with the first character in uppercase.
     */
    public static String toLowerFirstChar(final String s)
    {
        if (Character.isLowerCase(s.charAt(0)))
        {
            return s;
        }

        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    /**
     * Find the first token with a given name from an index inclusive.
     *
     * @param name   to search for.
     * @param tokens to search.
     * @param index  from which to search.
     * @return first found {@link Token} or throw a {@link IllegalStateException} if not found.
     */
    public static Token findFirst(final String name, final List<Token> tokens, final int index)
    {
        for (int i = index, size = tokens.size(); i < size; i++)
        {
            final Token token = tokens.get(i);
            if (token.name().equals(name))
            {
                return token;
            }
        }

        throw new IllegalStateException("name not found: " + name);
    }

    /**
     * Generate a C-family expression for the minimum block length a decoder must accept for a given acting version,
     * i.e. the extent of the fixed fields present at that version. Fields are only appended in later schema versions,
     * so a block encoded by an older version can be shorter than the current block but never shorter than the fields
     * which existed in that version. Fields newer than the acting version are not read by the generated accessors.
     *
     * @param fields            of the message or group block.
     * @param actingVersionName the name of the variable holding the acting version.
     * @return the expression or null if no fixed fields occupy the block.
     */
    public static String minBlockLengthExpression(final List<Token> fields, final String actingVersionName)
    {
        final TreeMap<Integer, Integer> extentByVersion = new TreeMap<>();
        for (int i = 0, size = fields.size(); i < size; )
        {
            final Token fieldToken = fields.get(i);
            if (Signal.BEGIN_FIELD != fieldToken.signal())
            {
                i++;
                continue;
            }

            if (!fieldToken.isConstantEncoding())
            {
                addExtentByVersion(fields, i + 1, fieldToken.version(), 0, extentByVersion);
            }

            i += fieldToken.componentTokenCount();
        }

        int extent = 0;
        String expression = "0";
        for (final Map.Entry<Integer, Integer> entry : extentByVersion.entrySet())
        {
            final int version = entry.getKey();
            if (entry.getValue() > extent)
            {
                extent = entry.getValue();
                expression = 0 == version ?
                    Integer.toString(extent) :
                    "(" + actingVersionName + " >= " + version + "u ? " + extent + "u : " + expression + ")";
            }
        }

        return 0 == extent ? null : expression;
    }

    // A composite member added in a later version than its field only counts from that version on, since a block
    // encoded by an older version ends before it.
    private static void addExtentByVersion(
        final List<Token> tokens,
        final int typeIndex,
        final int sinceVersion,
        final int baseOffset,
        final TreeMap<Integer, Integer> extentByVersion)
    {
        final Token typeToken = tokens.get(typeIndex);
        if (typeToken.isConstantEncoding())
        {
            return;
        }

        final int version = Math.max(sinceVersion, typeToken.version());
        final int offset = baseOffset + typeToken.offset();
        if (Signal.BEGIN_COMPOSITE == typeToken.signal())
        {
            final int endIndex = typeIndex + typeToken.componentTokenCount() - 1;
            for (int i = typeIndex + 1; i < endIndex; )
            {
                addExtentByVersion(tokens, i, version, offset, extentByVersion);
                i += tokens.get(i).componentTokenCount();
            }
        }
        else
        {
            extentByVersion.merge(version, offset + typeToken.encodedLength(), Math::max);
        }
    }
}
