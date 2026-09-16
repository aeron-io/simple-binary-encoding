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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.fail;

/**
 * Semantic comparison of stock Jackson trees for tests. Stock {@code equals} is class based ({@code IntNode}
 * never equals {@code LongNode}, {@code FloatNode} never equals {@code DoubleNode}), so numbers are compared by
 * value: integral by {@code bigIntegerValue()}, floating point by {@code double} with {@code float} widening when
 * either side is a {@code FloatNode}, NaN equal to NaN.
 */
final class JsonNodes
{
    static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonNodes()
    {
    }

    static void assertSemanticEquals(final JsonNode expected, final JsonNode actual)
    {
        final String diff = firstDifference(expected, actual, "$");
        if (null != diff)
        {
            fail(diff + "\nexpected: " + expected + "\nactual:   " + actual);
        }
    }

    static boolean semanticEquals(final JsonNode a, final JsonNode b)
    {
        return null == firstDifference(a, b, "$");
    }

    static String firstDifference(final JsonNode a, final JsonNode b, final String path)
    {
        if (a == null || b == null)
        {
            return a == b ? null : path + ": one side is missing";
        }
        if (a.isNumber() && b.isNumber())
        {
            return numbersEqual(a, b) ? null : path + ": " + a + " != " + b;
        }
        if (a.getNodeType() != b.getNodeType())
        {
            return path + ": node type " + a.getNodeType() + " != " + b.getNodeType();
        }

        switch (a.getNodeType())
        {
            case OBJECT:
            {
                if (a.size() != b.size())
                {
                    return path + ": object size " + a.size() + " != " + b.size() + " (" + fieldNames(a) + " vs " +
                        fieldNames(b) + ")";
                }
                final Iterator<String> names = a.fieldNames();
                while (names.hasNext())
                {
                    final String name = names.next();
                    if (!b.has(name))
                    {
                        return path + "." + name + ": missing on the right";
                    }
                    final String diff = firstDifference(a.get(name), b.get(name), path + "." + name);
                    if (null != diff)
                    {
                        return diff;
                    }
                }
                return null;
            }

            case ARRAY:
            {
                if (a.size() != b.size())
                {
                    return path + ": array size " + a.size() + " != " + b.size();
                }
                for (int i = 0; i < a.size(); i++)
                {
                    final String diff = firstDifference(a.get(i), b.get(i), path + "[" + i + "]");
                    if (null != diff)
                    {
                        return diff;
                    }
                }
                return null;
            }

            case BINARY:
            {
                try
                {
                    return java.util.Arrays.equals(a.binaryValue(), b.binaryValue()) ? null : path + ": binary differs";
                }
                catch (final java.io.IOException ex)
                {
                    return path + ": " + ex;
                }
            }

            default:
                return a.equals(b) ? null : path + ": " + a + " != " + b;
        }
    }

    private static boolean numbersEqual(final JsonNode a, final JsonNode b)
    {
        if (a.isIntegralNumber() && b.isIntegralNumber())
        {
            return a.bigIntegerValue().equals(b.bigIntegerValue());
        }
        if (a.isFloat() || b.isFloat())
        {
            final float x = (float)a.doubleValue();
            final float y = (float)b.doubleValue();
            return Float.compare(x, y) == 0 || x == y;
        }
        final double x = a.doubleValue();
        final double y = b.doubleValue();

        return Double.compare(x, y) == 0 || x == y;
    }

    private static String fieldNames(final JsonNode node)
    {
        final StringBuilder sb = new StringBuilder("[");
        final Iterator<String> names = node.fieldNames();
        while (names.hasNext())
        {
            sb.append(names.next());
            if (names.hasNext())
            {
                sb.append(", ");
            }
        }

        return sb.append(']').toString();
    }
}
