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
     * Escape schema supplied text so that it can be embedded between double quotes in a string literal of a
     * generated source file. Quotes, backslashes, control characters and line separators are escaped; other
     * characters are copied through unchanged.
     *
     * @param value        to be escaped.
     * @param braceUnicode true to write unicode escapes as {@code \\u{XXXX}} (Rust), false for {@code \\uXXXX}.
     * @return the escaped text.
     */
    public static String escapeStringLiteral(final String value, final boolean braceUnicode)
    {
        final StringBuilder sb = new StringBuilder(value.length() + 8);
        for (int i = 0, length = value.length(); i < length; i++)
        {
            final char c = value.charAt(i);
            switch (c)
            {
                case '"':
                    sb.append("\\\"");
                    break;

                case '\\':
                    sb.append("\\\\");
                    break;

                case '\n':
                    sb.append("\\n");
                    break;

                case '\r':
                    sb.append("\\r");
                    break;

                default:
                    if (c < ' ' || (c >= 0x7f && c <= 0x9f) || 0x2028 == c || 0x2029 == c)
                    {
                        sb.append(braceUnicode ? String.format("\\u{%x}", (int)c) : String.format("\\u%04x", (int)c));
                    }
                    else
                    {
                        sb.append(c);
                    }
                    break;
            }
        }

        return sb.toString();
    }

    /**
     * Collapse line terminators in schema supplied text so it cannot escape from a single line comment.
     *
     * @param value to be collapsed.
     * @return the text with each line terminator replaced by a space.
     */
    public static String toSingleLine(final String value)
    {
        final StringBuilder sb = new StringBuilder(value.length());
        for (int i = 0, length = value.length(); i < length; i++)
        {
            final char c = value.charAt(i);
            sb.append('\n' == c || '\r' == c || 0x85 == c || 0x2028 == c || 0x2029 == c ? ' ' : c);
        }

        return sb.toString();
    }
}
