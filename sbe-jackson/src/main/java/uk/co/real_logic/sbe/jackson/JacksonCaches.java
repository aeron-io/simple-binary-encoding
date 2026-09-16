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

import com.fasterxml.jackson.core.io.SerializedString;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.TextNode;

import java.math.BigInteger;

/**
 * Prebuilt immutable Jackson objects for one {@link MessagePlan}: constant value nodes, enum name nodes and
 * serialized field names. The only place plan metadata meets Jackson objects; built once per {@link SbeJson}.
 */
final class JacksonCaches
{
    private static final BigInteger TWO_POW_64 = BigInteger.ONE.shiftLeft(64);

    private final JsonNode[] constants;
    private final TextNode[][] enumNames;
    private final SerializedString[] fieldNames;

    JacksonCaches(final MessagePlan plan, final EnumStyle enumStyle)
    {
        final JsonNodeFactory factory = JsonNodeFactory.instance;
        final FieldPlan[] fields = plan.fields;
        constants = new JsonNode[fields.length];
        enumNames = new TextNode[fields.length][];
        fieldNames = new SerializedString[fields.length];

        for (int i = 0; i < fields.length; i++)
        {
            final FieldPlan f = fields[i];
            fieldNames[i] = new SerializedString(f.name);

            if (FieldPlan.KIND_ENUM == f.kind)
            {
                final TextNode[] names = new TextNode[f.enumNames.length];
                for (int n = 0; n < names.length; n++)
                {
                    names[n] = factory.textNode(f.enumNames[n]);
                }
                enumNames[i] = names;
            }

            if (f.constant)
            {
                constants[i] = constantNode(f, enumStyle, factory);
            }
        }
    }

    JsonNode constant(final int fieldIndex)
    {
        return constants[fieldIndex];
    }

    TextNode enumName(final int fieldIndex, final int valueIndex)
    {
        return enumNames[fieldIndex][valueIndex];
    }

    SerializedString fieldName(final int fieldIndex)
    {
        return fieldNames[fieldIndex];
    }

    /**
     * Stock node for a uint64 raw value interpreted unsigned.
     *
     * @param factory node factory.
     * @param raw     raw 64 bits.
     * @return {@code LongNode} when non-negative, otherwise a {@code BigIntegerNode}.
     */
    static JsonNode unsignedLongNode(final JsonNodeFactory factory, final long raw)
    {
        if (raw >= 0)
        {
            return factory.numberNode(raw);
        }

        return factory.numberNode(BigInteger.valueOf(raw).add(TWO_POW_64));
    }

    private static JsonNode constantNode(final FieldPlan f, final EnumStyle enumStyle, final JsonNodeFactory factory)
    {
        switch (f.kind)
        {
            case FieldPlan.KIND_INT:
                return WireTypes.fitsInt(f.primitiveType) ?
                    factory.numberNode((int)f.constLong) : factory.numberNode(f.constLong);

            case FieldPlan.KIND_UINT64:
                return unsignedLongNode(factory, f.constLong);

            case FieldPlan.KIND_FLOAT:
                return factory.numberNode((float)f.constDouble);

            case FieldPlan.KIND_DOUBLE:
                return factory.numberNode(f.constDouble);

            case FieldPlan.KIND_CHAR:
            case FieldPlan.KIND_CHAR_ARRAY:
                return factory.textNode(f.constString);

            case FieldPlan.KIND_ENUM:
                return EnumStyle.NAME == enumStyle ?
                    factory.textNode(f.constString) :
                    PlanMessageCodec.numericNode(f.primitiveType, f.constLong, factory);

            default:
                throw new IllegalStateException("constant not supported for kind " + f.kind + ": " + f);
        }
    }
}
