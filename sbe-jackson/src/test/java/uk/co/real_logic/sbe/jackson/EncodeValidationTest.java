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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.co.real_logic.sbe.ir.Ir;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Every reject row of DESIGN.md section 7 and every encode-reachable {@link ErrorCode}. The decode-only codes
 * ({@code FRAME_OVERFLOW}, {@code FIELD_OUTSIDE_BLOCK}, {@code UNSUPPORTED_VERSION}) are covered by
 * {@link VersioningTest} and {@link LimitsTest}; {@code SECTION_OUT_OF_ORDER} belongs to the parser path
 * (DESIGN.md section 13 step 6).
 */
class EncodeValidationTest
{
    private static final int CAPACITY = 4096;

    private Ir ir;
    private SbeJson sbeJson;
    private ObjectNode car;
    private UnsafeBuffer buffer;

    @BeforeEach
    void setUp() throws Exception
    {
        ir = TestMessages.ir(TestMessages.BASELINE_SCHEMA);
        sbeJson = SbeJson.builder(ir).build();
        car = (ObjectNode)JsonNodes.MAPPER.readTree(EncoderOracleTest.BASELINE_CAR_JSON);
        buffer = TestMessages.newBuffer(CAPACITY);
    }

    @Test
    void missingRequired()
    {
        assertRejects(ErrorCode.MISSING_REQUIRED, "Car.modelYear", c -> c.remove("modelYear"));
        assertRejects(ErrorCode.MISSING_REQUIRED, "Car.modelYear", c -> c.putNull("modelYear"));
        assertRejects(ErrorCode.MISSING_REQUIRED, "Car.engine", c -> c.remove("engine"));
        assertRejects(ErrorCode.MISSING_REQUIRED, "Car.engine.capacity", c -> engine(c).remove("capacity"));
        assertRejects(ErrorCode.MISSING_REQUIRED, "Car.fuelFigures[1].speed",
            c -> ((ObjectNode)c.get("fuelFigures").get(1)).remove("speed"));
        assertRejects(ErrorCode.MISSING_REQUIRED, "Car.fuelFigures[2]",
            c -> c.withArray("fuelFigures").set(2, c.nullNode()));

        final SbeJsonException ex = assertThrows(
            SbeJsonException.class, () -> sbeJson.newEncoder("Car").encode((JsonNode)null, buffer, 0, CAPACITY));
        assertEquals(ErrorCode.MISSING_REQUIRED, ex.code());
    }

    @Test
    void typeMismatch()
    {
        assertRejects(ErrorCode.TYPE_MISMATCH, "Car.modelYear", c -> c.put("modelYear", 5.0));
        assertRejects(ErrorCode.TYPE_MISMATCH, "Car.modelYear", c -> c.put("modelYear", "2013"));
        assertRejects(ErrorCode.TYPE_MISMATCH, "Car.serialNumber", c -> c.put("serialNumber", 1.5));
        assertRejects(ErrorCode.TYPE_MISMATCH, "Car.serialNumber", c -> c.put("serialNumber", "abc"));
        assertRejects(ErrorCode.TYPE_MISMATCH, "Car.fuelFigures", c -> c.putObject("fuelFigures"));
        assertRejects(ErrorCode.TYPE_MISMATCH, "Car.fuelFigures[0]", c -> c.withArray("fuelFigures").set(0, 1));
        assertRejects(ErrorCode.TYPE_MISMATCH, "Car.engine", c -> c.put("engine", 5));
        assertRejects(ErrorCode.TYPE_MISMATCH, "Car.someNumbers", c -> c.put("someNumbers", 3));
        assertRejects(ErrorCode.TYPE_MISMATCH, "Car.someNumbers",
            c -> c.withArray("someNumbers").set(1, 1.5));
        assertRejects(ErrorCode.TYPE_MISMATCH, "Car.extras", c -> c.put("extras", "abc"));
        assertRejects(ErrorCode.TYPE_MISMATCH, "Car.extras",
            c -> c.putObject("extras").put("sunRoof", 1));
        assertRejects(ErrorCode.TYPE_MISMATCH, "Car.vehicleCode", c -> c.put("vehicleCode", "abcdé"));
        assertRejects(ErrorCode.TYPE_MISMATCH, "Car.vehicleCode", c -> c.put("vehicleCode", 123456));
        assertRejects(ErrorCode.TYPE_MISMATCH, "Car.code", c -> c.put("code", 1.5));
        assertRejects(ErrorCode.TYPE_MISMATCH, "Car.manufacturer", c -> c.put("manufacturer", 7));
        assertRejects(ErrorCode.TYPE_MISMATCH, "Car.fuelFigures[0].mpg",
            c -> ((ObjectNode)c.get("fuelFigures").get(0)).put("mpg", "fast"));
        assertRejects(ErrorCode.TYPE_MISMATCH, "Car.performanceFigures[1].acceleration[2].seconds",
            c -> ((ObjectNode)c.get("performanceFigures").get(1).get("acceleration").get(2)).put("seconds", true));
    }

    @Test
    void outOfRange()
    {
        assertRejects(ErrorCode.OUT_OF_RANGE, "Car.modelYear", c -> c.put("modelYear", 70000));
        assertRejects(ErrorCode.OUT_OF_RANGE, "Car.modelYear", c -> c.put("modelYear", -1));
        assertRejects(ErrorCode.OUT_OF_RANGE, "Car.serialNumber", c -> c.put("serialNumber", -1));
        assertRejects(ErrorCode.OUT_OF_RANGE, "Car.serialNumber",
            c -> c.put("serialNumber", BigInteger.ONE.shiftLeft(64)));
        assertRejects(ErrorCode.OUT_OF_RANGE, "Car.serialNumber",
            c -> c.put("serialNumber", "18446744073709551615"));
        assertRejects(ErrorCode.OUT_OF_RANGE, "Car.vehicleCode", c -> c.put("vehicleCode", "abcdefg"));
        assertRejects(ErrorCode.OUT_OF_RANGE, "Car.someNumbers", c -> c.withArray("someNumbers").add(5));
        assertRejects(ErrorCode.OUT_OF_RANGE, "Car.someNumbers",
            c -> c.withArray("someNumbers").set(0, Long.MAX_VALUE));
        assertRejects(ErrorCode.OUT_OF_RANGE, "Car.extras", c -> c.put("extras", 256));
        assertRejects(ErrorCode.OUT_OF_RANGE, "Car.code", c -> c.put("code", 300));
        assertRejects(ErrorCode.OUT_OF_RANGE, "Car.performanceFigures[0].octaneRating",
            c -> ((ObjectNode)c.get("performanceFigures").get(0)).put("octaneRating", 89));
        assertRejects(ErrorCode.OUT_OF_RANGE, "Car.fuelFigures", c ->
        {
            final ObjectNode entry = (ObjectNode)c.get("fuelFigures").get(0);
            for (int i = 0; i < 252; i++)
            {
                c.withArray("fuelFigures").add(entry);
            }
        });
        assertRejects(ErrorCode.OUT_OF_RANGE, "Car.manufacturer", c -> c.put("manufacturer", "x".repeat(255)));
        assertRejects(ErrorCode.OUT_OF_RANGE, "Car.fuelFigures[0].mpg",
            c -> ((ObjectNode)c.get("fuelFigures").get(0)).put("mpg", 1e300));
    }

    @Test
    void unknownEnumAndChoice()
    {
        assertRejects(ErrorCode.UNKNOWN_ENUM, "Car.available", c -> c.put("available", "Maybe"));
        assertRejects(ErrorCode.UNKNOWN_ENUM, "Car.code", c -> c.put("code", "D"));
        assertRejects(ErrorCode.UNKNOWN_CHOICE, "Car.extras", c -> c.putObject("extras").put("turbo", true));
    }

    @Test
    void unknownEnumRawValueIsAcceptedForLosslessRoundTrip()
    {
        car.put("available", 7);
        final int length = sbeJson.newEncoder("Car").encode(car, buffer, 0, CAPACITY);
        assertEquals(7, sbeJson.newDecoder().decodeCopy(buffer, 0, length).get("available").intValue());
    }

    @Test
    void unknownPropertyPerObjectByDefaultAndIgnoredOnRequest()
    {
        assertRejects(ErrorCode.UNKNOWN_PROPERTY, "Car", c -> c.put("colour", "red"));
        assertRejects(ErrorCode.UNKNOWN_PROPERTY, "Car.engine", c -> engine(c).put("turbo", true));
        assertRejects(ErrorCode.UNKNOWN_PROPERTY, "Car.fuelFigures[1]",
            c -> ((ObjectNode)c.get("fuelFigures").get(1)).put("units", "mpg"));
        assertRejects(ErrorCode.UNKNOWN_PROPERTY, "Car.performanceFigures[0].acceleration[1]",
            c -> ((ObjectNode)c.get("performanceFigures").get(0).get("acceleration").get(1)).put("kph", 1));

        final SbeJsonException ex = assertThrows(SbeJsonException.class, () ->
        {
            car.put("colour", "red");
            sbeJson.newEncoder("Car").encode(car, buffer, 0, CAPACITY);
        });
        assertTrue(ex.getMessage().contains("'colour'"), ex.getMessage());

        final UnsafeBuffer reference = TestMessages.newBuffer(CAPACITY);
        final int expectedLength = TestMessages.encodeBaselineCar(reference, 0);
        final SbeJson lenient = SbeJson.builder(ir).unknownProperties(UnknownProperties.IGNORE).build();
        engine(car).put("turbo", true);
        ((ObjectNode)car.get("fuelFigures").get(1)).put("units", "mpg");
        final int length = lenient.newEncoder("Car").encode(car, buffer, 0, CAPACITY);
        assertEquals(expectedLength, length);
        assertArrayEquals(Arrays.copyOf(reference.byteArray(), length), Arrays.copyOf(buffer.byteArray(), length));
    }

    @Test
    void constantsMayBeOmittedButMustMatchWhenSupplied() throws Exception
    {
        final UnsafeBuffer reference = TestMessages.newBuffer(CAPACITY);
        final int expectedLength = TestMessages.encodeBaselineCar(reference, 0);

        engine(car).put("maxRpm", 9000);
        engine(car).put("fuel", "Petrol");
        final int length = sbeJson.newEncoder("Car").encode(car, buffer, 0, CAPACITY);
        assertArrayEquals(
            Arrays.copyOf(reference.byteArray(), expectedLength), Arrays.copyOf(buffer.byteArray(), length));

        assertRejects(ErrorCode.CONSTANT_MISMATCH, "Car.engine.maxRpm", c -> engine(c).put("maxRpm", 9001));
        assertRejects(ErrorCode.CONSTANT_MISMATCH, "Car.engine.maxRpm", c -> engine(c).put("maxRpm", "9000"));
        assertRejects(ErrorCode.CONSTANT_MISMATCH, "Car.engine.fuel", c -> engine(c).put("fuel", "Diesel"));

        final SbeJson extension = SbeJson.builder(TestMessages.ir(TestMessages.EXTENSION_SCHEMA)).build();
        final ObjectNode extended = (ObjectNode)JsonNodes.MAPPER.readTree(EncoderOracleTest.EXTENSION_CAR_JSON);
        extended.put("discountedModel", "A");
        final SbeJsonException ex = assertThrows(
            SbeJsonException.class, () -> extension.newEncoder("Car").encode(extended, buffer, 0, CAPACITY));
        assertEquals(ErrorCode.CONSTANT_MISMATCH, ex.code());
        assertEquals("Car.discountedModel", ex.path());
        extended.put("discountedModel", (int)'C');
        extension.newEncoder("Car").encode(extended, buffer, 0, CAPACITY);
    }

    @Test
    void destinationOverflow()
    {
        final SbeJsonEncoder encoder = sbeJson.newEncoder("Car");
        final int needed = encoder.encodedLength(car);

        assertEquals(needed, encoder.encode(car, buffer, 0, needed));

        final SbeJsonException tail = assertThrows(
            SbeJsonException.class, () -> encoder.encode(car, buffer, 0, needed - 1));
        assertEquals(ErrorCode.DESTINATION_OVERFLOW, tail.code());
        assertEquals("Car.activationCode", tail.path());

        final SbeJsonException header = assertThrows(
            SbeJsonException.class, () -> encoder.encode(car, buffer, 0, 3));
        assertEquals(ErrorCode.DESTINATION_OVERFLOW, header.code());

        final SbeJsonException block = assertThrows(
            SbeJsonException.class, () -> encoder.encode(car, buffer, 0, 20));
        assertEquals(ErrorCode.DESTINATION_OVERFLOW, block.code());

        final SbeJsonException group = assertThrows(
            SbeJsonException.class, () -> encoder.encode(car, buffer, 0, 8 + 62 + 4));
        assertEquals(ErrorCode.DESTINATION_OVERFLOW, group.code());
        assertTrue(group.path().startsWith("Car.fuelFigures"), group.path());

        final SbeJsonException capacity = assertThrows(
            SbeJsonException.class, () -> encoder.encode(car, buffer, CAPACITY - 10, needed));
        assertEquals(ErrorCode.DESTINATION_OVERFLOW, capacity.code());
    }

    @Test
    void unknownTemplateFromEncoderFactory()
    {
        final SbeJsonException byName = assertThrows(SbeJsonException.class, () -> sbeJson.newEncoder("Nope"));
        assertEquals(ErrorCode.UNKNOWN_TEMPLATE, byName.code());

        final SbeJsonException byId = assertThrows(SbeJsonException.class, () -> sbeJson.newEncoder(99));
        assertEquals(ErrorCode.UNKNOWN_TEMPLATE, byId.code());
        assertEquals(99, byId.templateId());
    }

    @Test
    void optionalMissingOrNullEncodesTheNullSentinel()
    {
        car.remove("uuid");
        car.putNull("cupHolderCount");
        car.remove("performanceFigures");
        car.remove("model");
        final int length = sbeJson.newEncoder("Car").encode(car, buffer, 0, CAPACITY);

        final ObjectNode decoded = sbeJson.newDecoder().decodeCopy(buffer, 0, length);
        assertTrue(decoded.get("uuid").isArray());
        assertEquals(Long.MIN_VALUE, decoded.get("uuid").get(0).longValue());
        assertTrue(decoded.get("cupHolderCount").isNull());
        assertEquals(0, decoded.get("performanceFigures").size());
        assertEquals("", decoded.get("model").textValue());
    }

    @Test
    void uint64AcceptsBigIntegerAndDecimalString()
    {
        car.put("serialNumber", new BigInteger("18446744073709551614"));
        int length = sbeJson.newEncoder("Car").encode(car, buffer, 0, CAPACITY);
        assertEquals(
            new BigInteger("18446744073709551614"),
            sbeJson.newDecoder().decodeCopy(buffer, 0, length).get("serialNumber").bigIntegerValue());

        car.put("serialNumber", "9223372036854775808");
        length = sbeJson.newEncoder("Car").encode(car, buffer, 0, CAPACITY);
        assertEquals(
            new BigInteger("9223372036854775808"),
            sbeJson.newDecoder().decodeCopy(buffer, 0, length).get("serialNumber").bigIntegerValue());
    }

    @Test
    void floatingPointAcceptsNanAndInfinityStrings()
    {
        ((ObjectNode)car.get("fuelFigures").get(0)).put("mpg", "NaN");
        ((ObjectNode)car.get("fuelFigures").get(1)).put("mpg", "Infinity");
        ((ObjectNode)car.get("fuelFigures").get(2)).put("mpg", "-Infinity");
        final int length = sbeJson.newEncoder("Car").encode(car, buffer, 0, CAPACITY);

        final JsonNode fuelFigures = sbeJson.newDecoder().decodeCopy(buffer, 0, length).get("fuelFigures");
        assertTrue(Float.isNaN(fuelFigures.get(0).get("mpg").floatValue()));
        assertEquals(Float.POSITIVE_INFINITY, fuelFigures.get(1).get("mpg").floatValue());
        assertEquals(Float.NEGATIVE_INFINITY, fuelFigures.get(2).get("mpg").floatValue());
    }

    @Test
    void asciiVarDataRejectsNonAscii() throws Exception
    {
        final SbeJson extension = SbeJson.builder(TestMessages.ir(TestMessages.EXTENSION_SCHEMA)).build();
        final ObjectNode extended = (ObjectNode)JsonNodes.MAPPER.readTree(EncoderOracleTest.EXTENSION_CAR_JSON);
        extended.put("activationCode", "café");

        final SbeJsonException ex = assertThrows(
            SbeJsonException.class, () -> extension.newEncoder("Car").encode(extended, buffer, 0, CAPACITY));
        assertEquals(ErrorCode.TYPE_MISMATCH, ex.code());
        assertEquals("Car.activationCode", ex.path());
    }

    @Test
    void exceptionCarriesTemplateIdAndOptionallyNoStackTrace()
    {
        final SbeJson quiet = SbeJson.builder(ir).exceptionStackTraces(false).build();
        car.put("modelYear", 5.5);
        final SbeJsonException ex = assertThrows(
            SbeJsonException.class, () -> quiet.newEncoder("Car").encode(car, buffer, 0, CAPACITY));

        assertEquals(1, ex.templateId());
        assertEquals("Car.modelYear", ex.path());
        assertEquals(8 + 8, ex.byteOffset());
        assertEquals(0, ex.getStackTrace().length);

        final SbeJsonException loud = assertThrows(
            SbeJsonException.class, () -> sbeJson.newEncoder("Car").encode(car, buffer, 0, CAPACITY));
        assertTrue(loud.getStackTrace().length > 0);
    }

    private void assertRejects(final ErrorCode code, final String path, final Consumer<ObjectNode> mutation)
    {
        final ObjectNode mutated = car.deepCopy();
        mutation.accept(mutated);
        final SbeJsonEncoder encoder = sbeJson.newEncoder("Car");

        final SbeJsonException ex = assertThrows(
            SbeJsonException.class, () -> encoder.encode(mutated, buffer, 0, CAPACITY), path);
        assertEquals(code, ex.code(), ex.getMessage());
        assertEquals(path, ex.path(), ex.getMessage());

        final SbeJsonException sizing = assertThrows(
            SbeJsonException.class, () -> encoder.encodedLength(mutated), path);
        assertEquals(code, sizing.code(), sizing.getMessage());
    }

    private static ObjectNode engine(final ObjectNode car)
    {
        return (ObjectNode)car.get("engine");
    }
}
