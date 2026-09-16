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
package uk.co.real_logic.sbe.benchmarks;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.agrona.DirectBuffer;
import uk.co.real_logic.sbe.PrimitiveType;
import uk.co.real_logic.sbe.ir.Encoding;
import uk.co.real_logic.sbe.ir.Token;
import uk.co.real_logic.sbe.otf.AbstractTokenListener;
import uk.co.real_logic.sbe.otf.Types;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Deliberately simple stock-node baseline for this car.xml corpus, not a general SBE adapter.
 * Re-walks tokens and enum values on every decode; no compiled field plan or cached value nodes.
 * Optional fields, version transitions, binary data and uint64 are outside this corpus.
 */
final class NaiveCarTokenListener extends AbstractTokenListener
{
    private static final JsonNodeFactory FACTORY = JsonNodeFactory.instance;
    private final ObjectNode[] objects = new ObjectNode[8];
    private final ArrayNode[] groups = new ArrayNode[8];
    private int depth;
    private int compositeDepth;

    ObjectNode root()
    {
        return objects[0];
    }

    @Override
    public void onBeginMessage(final Token token)
    {
        depth = 0;
        compositeDepth = 0;
        objects[0] = FACTORY.objectNode();
    }

    @Override
    public void onEncoding(
        final Token fieldToken, final DirectBuffer buffer, final int bufferIndex,
        final Token typeToken, final int actingVersion)
    {
        final Encoding encoding = typeToken.encoding();
        final String name = compositeDepth > 0 ? typeToken.name() : fieldToken.name();
        final PrimitiveType type = encoding.primitiveType();
        final JsonNode value;
        if (typeToken.isConstantEncoding())
        {
            value = PrimitiveType.CHAR == type ? FACTORY.textNode(encoding.constValue().toString()) :
                integerNode(type, encoding.constValue().longValue());
        }
        else if (PrimitiveType.CHAR == type)
        {
            final byte[] bytes = new byte[typeToken.arrayLength()];
            buffer.getBytes(bufferIndex, bytes);
            int length = 0;
            while (length < bytes.length && bytes[length] != 0)
            {
                length++;
            }
            value = FACTORY.textNode(new String(bytes, 0, length, StandardCharsets.US_ASCII));
        }
        else if (typeToken.arrayLength() > 1)
        {
            final ArrayNode array = FACTORY.arrayNode();
            for (int i = 0; i < typeToken.arrayLength(); i++)
            {
                array.add(primitive(buffer, bufferIndex + i * type.size(), encoding));
            }
            value = array;
        }
        else
        {
            value = primitive(buffer, bufferIndex, encoding);
        }
        objects[depth].set(name, value);
    }

    @Override
    public void onEnum(
        final Token fieldToken, final DirectBuffer buffer, final int bufferIndex,
        final List<Token> tokens, final int fromIndex, final int toIndex, final int actingVersion)
    {
        final long raw = Types.getLong(buffer, bufferIndex, tokens.get(fromIndex + 1).encoding());
        for (int i = fromIndex + 1; i < toIndex; i++)
        {
            final Token value = tokens.get(i);
            if (raw == value.encoding().constValue().longValue())
            {
                objects[depth].put(fieldToken.name(), value.name());
                return;
            }
        }
        objects[depth].set(fieldToken.name(), integerNode(tokens.get(fromIndex + 1).encoding().primitiveType(), raw));
    }

    @Override
    public void onBitSet(
        final Token fieldToken, final DirectBuffer buffer, final int bufferIndex,
        final List<Token> tokens, final int fromIndex, final int toIndex, final int actingVersion)
    {
        final Encoding encoding = tokens.get(fromIndex + 1).encoding();
        objects[depth].set(fieldToken.name(), integerNode(encoding.primitiveType(),
            Types.getLong(buffer, bufferIndex, encoding)));
    }

    @Override
    public void onBeginComposite(
        final Token fieldToken, final List<Token> tokens, final int fromIndex, final int toIndex)
    {
        final ObjectNode child = FACTORY.objectNode();
        objects[depth].set(fieldToken.name(), child);
        objects[++depth] = child;
        compositeDepth++;
    }

    @Override
    public void onEndComposite(
        final Token fieldToken, final List<Token> tokens, final int fromIndex, final int toIndex)
    {
        objects[depth--] = null;
        compositeDepth--;
    }

    @Override
    public void onGroupHeader(final Token token, final int numInGroup)
    {
        final ArrayNode group = FACTORY.arrayNode();
        objects[depth].set(token.name(), group);
        groups[depth] = numInGroup == 0 ? null : group;
    }

    @Override
    public void onBeginGroup(final Token token, final int groupIndex, final int numInGroup)
    {
        final ObjectNode entry = FACTORY.objectNode();
        groups[depth].add(entry);
        objects[++depth] = entry;
    }

    @Override
    public void onEndGroup(final Token token, final int groupIndex, final int numInGroup)
    {
        objects[depth--] = null;
        if (groupIndex + 1 == numInGroup)
        {
            groups[depth] = null;
        }
    }

    @Override
    public void onVarData(
        final Token fieldToken, final DirectBuffer buffer, final int bufferIndex,
        final int length, final Token typeToken)
    {
        final byte[] bytes = new byte[length];
        buffer.getBytes(bufferIndex, bytes);
        objects[depth].put(fieldToken.name(),
            new String(bytes, Charset.forName(typeToken.encoding().characterEncoding())));
    }

    private static JsonNode primitive(final DirectBuffer buffer, final int index, final Encoding encoding)
    {
        switch (encoding.primitiveType())
        {
            case FLOAT:
                return FACTORY.numberNode(buffer.getFloat(index, encoding.byteOrder()));

            case DOUBLE:
                return FACTORY.numberNode(buffer.getDouble(index, encoding.byteOrder()));

            default:
                return integerNode(encoding.primitiveType(), Types.getLong(buffer, index, encoding));
        }
    }

    private static JsonNode integerNode(final PrimitiveType type, final long value)
    {
        return PrimitiveType.UINT32 == type || PrimitiveType.INT64 == type ?
            FACTORY.numberNode(value) : FACTORY.numberNode((int)value);
    }
}
