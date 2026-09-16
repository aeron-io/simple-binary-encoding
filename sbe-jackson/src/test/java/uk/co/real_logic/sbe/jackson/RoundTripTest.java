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
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.real_logic.sbe.ir.Ir;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.ToIntBiFunction;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Generated-encoder bytes to {@code decodeCopy} to {@code encode} must reproduce the bytes (padding is zero in
 * both), and {@code decodeCopy} to {@code encode} to {@code decodeCopy} must be a semantic fixed point, under
 * every combination of presentation policies.
 */
class RoundTripTest
{
    private static final int CAPACITY = 4096;

    static Stream<Arguments> samples()
    {
        final List<Arguments> arguments = new ArrayList<>();
        final Object[][] messages = {
            { TestMessages.BASELINE_SCHEMA, (ToIntBiFunction<UnsafeBuffer, Integer>)TestMessages::encodeBaselineCar },
            { TestMessages.EXTENSION_SCHEMA, (ToIntBiFunction<UnsafeBuffer, Integer>)TestMessages::encodeExtensionCar },
            {
                TestMessages.COMPOSITE_ELEMENTS_SCHEMA,
                (ToIntBiFunction<UnsafeBuffer, Integer>)TestMessages::encodeCompositeElements
            },
            {
                TestMessages.GROUP_WITH_DATA_SCHEMA,
                (ToIntBiFunction<UnsafeBuffer, Integer>)TestMessages::encodeGroupWithData
            },
            {
                TestMessages.NESTED_GROUP_SCHEMA,
                (ToIntBiFunction<UnsafeBuffer, Integer>)TestMessages::encodeNestedGroups
            },
        };

        for (final Object[] message : messages)
        {
            for (final EnumStyle enumStyle : EnumStyle.values())
            {
                for (final BitSetStyle bitSetStyle : BitSetStyle.values())
                {
                    for (final CharArrayStyle charArrayStyle : CharArrayStyle.values())
                    {
                        arguments.add(Arguments.of(message[0], message[1], enumStyle, bitSetStyle, charArrayStyle));
                    }
                }
            }
        }

        return arguments.stream();
    }

    @ParameterizedTest(name = "{0} {2} {3} {4}")
    @MethodSource("samples")
    void bytesSurviveDecodeEncode(
        final String schema,
        final ToIntBiFunction<UnsafeBuffer, Integer> oracle,
        final EnumStyle enumStyle,
        final BitSetStyle bitSetStyle,
        final CharArrayStyle charArrayStyle)
    {
        final Ir ir = TestMessages.ir(schema);
        final SbeJson sbeJson = SbeJson.builder(ir)
            .enumStyle(enumStyle)
            .bitSetStyle(bitSetStyle)
            .charArrayStyle(charArrayStyle)
            .build();

        final UnsafeBuffer original = TestMessages.newBuffer(CAPACITY);
        final int length = oracle.applyAsInt(original, 0);
        final SbeJsonDecoder decoder = sbeJson.newDecoder();
        final ObjectNode tree = decoder.decodeCopy(original, 0, length);
        final SbeJsonEncoder encoder = sbeJson.newEncoder(decoder.lastHeader().templateId());

        final UnsafeBuffer reencoded = TestMessages.newBuffer(CAPACITY);
        final int written = encoder.encode(tree, reencoded, 0, CAPACITY);

        assertEquals(length, written);
        assertEquals(length, encoder.encodedLength(tree));
        assertArrayEquals(
            Arrays.copyOf(original.byteArray(), length), Arrays.copyOf(reencoded.byteArray(), written));

        final ObjectNode again = sbeJson.newDecoder().decodeCopy(reencoded, 0, written);
        JsonNodes.assertSemanticEquals(tree, again);
        assertEquals(tree, again);
    }

    @ParameterizedTest(name = "{0} {2} {3} {4}")
    @MethodSource("samples")
    void propertyOrderDoesNotChangeBytes(
        final String schema,
        final ToIntBiFunction<UnsafeBuffer, Integer> oracle,
        final EnumStyle enumStyle,
        final BitSetStyle bitSetStyle,
        final CharArrayStyle charArrayStyle)
    {
        final Ir ir = TestMessages.ir(schema);
        final SbeJson sbeJson = SbeJson.builder(ir)
            .enumStyle(enumStyle)
            .bitSetStyle(bitSetStyle)
            .charArrayStyle(charArrayStyle)
            .build();

        final UnsafeBuffer original = TestMessages.newBuffer(CAPACITY);
        final int length = oracle.applyAsInt(original, 0);
        final SbeJsonDecoder decoder = sbeJson.newDecoder();
        final ObjectNode tree = decoder.decodeCopy(original, 0, length);
        final ObjectNode shuffled = shuffle(tree);

        final UnsafeBuffer reencoded = TestMessages.newBuffer(CAPACITY);
        final int written = sbeJson.newEncoder(decoder.lastHeader().templateId())
            .encode(shuffled, reencoded, 0, CAPACITY);

        assertArrayEquals(
            Arrays.copyOf(original.byteArray(), length), Arrays.copyOf(reencoded.byteArray(), written));
    }

    @ParameterizedTest(name = "numInGroup {0}")
    @ValueSource(ints = { 0, 1, 254 })
    void groupCountBoundariesRoundTrip(final int count)
    {
        final Ir ir = TestMessages.ir(TestMessages.BASELINE_SCHEMA);
        final SbeJson sbeJson = SbeJson.builder(ir).build();
        final UnsafeBuffer original = TestMessages.newBuffer(CAPACITY);
        final int originalLength = TestMessages.encodeBaselineCar(original, 0);
        final ObjectNode car = sbeJson.newDecoder().decodeCopy(original, 0, originalLength);

        final com.fasterxml.jackson.databind.node.ArrayNode fuelFigures = car.putArray("fuelFigures");
        for (int i = 0; i < count; i++)
        {
            fuelFigures.addObject().put("speed", i).put("mpg", i * 0.5f);
        }
        car.putArray("performanceFigures");

        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        final SbeJsonEncoder encoder = sbeJson.newEncoder("Car");
        final int length = encoder.encode(car, buffer, 0, CAPACITY);
        assertEquals(length, encoder.encodedLength(car));
        // fuelFigures dimensions follow the 62 byte root block: uint16 blockLength then uint8 numInGroup.
        assertEquals(count, buffer.getByte(8 + 62 + 2) & 0xFF);

        final ObjectNode decoded = sbeJson.newDecoder().decodeCopy(buffer, 0, length);
        assertEquals(count, decoded.get("fuelFigures").size());
        assertEquals(0, decoded.get("performanceFigures").size());
        JsonNodes.assertSemanticEquals(car, decoded);

        // One past the numInGroup maximum of the IR (uint8 max 254) is rejected on encode.
        fuelFigures.addObject().put("speed", 1).put("mpg", 1.0f);
        while (fuelFigures.size() < 255)
        {
            fuelFigures.addObject().put("speed", 1).put("mpg", 1.0f);
        }
        final SbeJsonException ex = assertThrows(
            SbeJsonException.class, () -> encoder.encode(car, buffer, 0, CAPACITY));
        assertEquals(ErrorCode.OUT_OF_RANGE, ex.code());
        assertEquals("Car.fuelFigures", ex.path());
    }

    static ObjectNode shuffle(final ObjectNode node)
    {
        final List<String> names = new ArrayList<>();
        node.fieldNames().forEachRemaining(names::add);
        Collections.reverse(names);

        final ObjectNode result = node.objectNode();
        for (final String name : names)
        {
            final JsonNode child = node.get(name);
            if (child.isObject())
            {
                result.set(name, shuffle((ObjectNode)child));
            }
            else if (child.isArray() && child.size() > 0 && child.get(0).isObject())
            {
                final com.fasterxml.jackson.databind.node.ArrayNode array = result.arrayNode(child.size());
                for (final JsonNode element : child)
                {
                    array.add(shuffle((ObjectNode)element));
                }
                result.set(name, array);
            }
            else
            {
                result.set(name, child);
            }
        }

        return result;
    }
}
