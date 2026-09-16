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
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.jupiter.api.Test;
import uk.co.real_logic.sbe.ir.Ir;

import java.util.Arrays;
import java.util.function.ToIntBiFunction;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Bytes from {@link SbeJsonEncoder#encode} must equal the bytes the generated encoders produce for the same
 * logical content. sbe-tool has no OTF encoder, so the generated codecs are the oracle.
 */
class EncoderOracleTest
{
    private static final int CAPACITY = 4096;

    static final String BASELINE_CAR_JSON =
        "{\"serialNumber\":1234,\"modelYear\":2013,\"available\":\"T\",\"code\":\"A\"," +
        "\"someNumbers\":[0,1,2,3,4],\"vehicleCode\":\"ab\\\"def\",\"extras\":6," +
        "\"engine\":{\"capacity\":2000,\"numCylinders\":4,\"manufacturerCode\":\"123\"}," +
        "\"uuid\":[7,3],\"cupHolderCount\":5," +
        "\"fuelFigures\":[{\"speed\":30,\"mpg\":35.9},{\"speed\":55,\"mpg\":49.0},{\"speed\":75,\"mpg\":40.0}]," +
        "\"performanceFigures\":[" +
        "{\"octaneRating\":95,\"acceleration\":[{\"mph\":30,\"seconds\":4.0},{\"mph\":60,\"seconds\":7.5}," +
        "{\"mph\":100,\"seconds\":12.2}]}," +
        "{\"octaneRating\":99,\"acceleration\":[{\"mph\":30,\"seconds\":3.8},{\"mph\":60,\"seconds\":7.1}," +
        "{\"mph\":100,\"seconds\":11.8}]}]," +
        "\"manufacturer\":\"Honda\",\"model\":\"Civic VTi\",\"activationCode\":\"315\\\\8\"}";

    static final String EXTENSION_CAR_JSON =
        "{\"serialNumber\":1234,\"modelYear\":2013,\"available\":\"T\",\"code\":\"A\"," +
        "\"someNumbers\":[1,2,3,4],\"vehicleCode\":\"abcdef\"," +
        "\"extras\":{\"sportsPack\":true,\"cruiseControl\":true,\"sunRoof\":false}," +
        "\"discountedModel\":\"C\"," +
        "\"engine\":{\"capacity\":2000,\"numCylinders\":4,\"maxRpm\":9000,\"manufacturerCode\":\"123\"," +
        "\"fuel\":\"Petrol\",\"efficiency\":35,\"boosterEnabled\":\"T\"," +
        "\"booster\":{\"BoostType\":\"NITROUS\",\"horsePower\":200}}," +
        "\"uuid\":[7,3],\"cupHolderCount\":5," +
        "\"fuelFigures\":[{\"speed\":30,\"mpg\":35.9,\"usageDescription\":\"Urban Cycle\"}," +
        "{\"speed\":55,\"mpg\":49.0,\"usageDescription\":\"Combined Cycle\"}]," +
        "\"performanceFigures\":[" +
        "{\"octaneRating\":95,\"acceleration\":[{\"mph\":30,\"seconds\":4.0},{\"mph\":60,\"seconds\":7.5}]}," +
        "{\"octaneRating\":99,\"acceleration\":[]}]," +
        "\"manufacturer\":\"Honda\",\"model\":\"Civic VTi éè 🚗\"," +
        "\"activationCode\":\"abcdef\"}";

    static final String COMPOSITE_ELEMENTS_JSON =
        "{\"structure\":{\"enumOne\":\"Value10\",\"zeroth\":42,\"setOne\":{\"Bit0\":true,\"Bit26\":true}," +
        "\"inner\":{\"first\":101,\"second\":-202}}}";

    static final String GROUP_WITH_DATA_JSON =
        "{\"Tag1\":99,\"Entries\":[" +
        "{\"TagGroup1\":\"ABCDEFGHI\",\"NestedEntries\":[{\"TagGroup2\":1,\"varDataFieldNested\":\"nested one\"}," +
        "{\"TagGroup2\":2,\"varDataFieldNested\":\"\"}],\"varDataField\":\"outer one\"}," +
        "{\"TagGroup1\":\"JKLMNOPQR\",\"NestedEntries\":[],\"varDataField\":\"outer two\"}]}";

    @Test
    void baselineCarBytesEqualGeneratedEncoder() throws Exception
    {
        assertBytesEqualOracle(TestMessages.BASELINE_SCHEMA, "Car", BASELINE_CAR_JSON, TestMessages::encodeBaselineCar);
    }

    @Test
    void extensionCarBytesEqualGeneratedEncoder() throws Exception
    {
        assertBytesEqualOracle(
            TestMessages.EXTENSION_SCHEMA, "Car", EXTENSION_CAR_JSON, TestMessages::encodeExtensionCar);
    }

    @Test
    void compositeElementsBytesEqualGeneratedEncoder() throws Exception
    {
        assertBytesEqualOracle(
            TestMessages.COMPOSITE_ELEMENTS_SCHEMA, "Msg", COMPOSITE_ELEMENTS_JSON,
            TestMessages::encodeCompositeElements);
    }

    @Test
    void groupWithDataBytesEqualGeneratedEncoder() throws Exception
    {
        assertBytesEqualOracle(
            TestMessages.GROUP_WITH_DATA_SCHEMA, "TestMessage3", GROUP_WITH_DATA_JSON,
            TestMessages::encodeGroupWithData);
    }

    @Test
    void encodeAtOffsetWithExactAvailableSucceeds() throws Exception
    {
        final Ir ir = TestMessages.ir(TestMessages.BASELINE_SCHEMA);
        final SbeJsonEncoder encoder = SbeJson.builder(ir).build().newEncoder(1);
        final JsonNode car = JsonNodes.MAPPER.readTree(BASELINE_CAR_JSON);
        final int needed = encoder.encodedLength(car);

        final UnsafeBuffer expected = TestMessages.newBuffer(CAPACITY);
        final int expectedLength = TestMessages.encodeBaselineCar(expected, 0);
        assertEquals(expectedLength, needed);

        final UnsafeBuffer actual = TestMessages.newBuffer(CAPACITY);
        final int written = encoder.encode(car, actual, 200, needed);
        assertEquals(needed, written);
        assertArrayEquals(
            Arrays.copyOfRange(expected.byteArray(), 0, expectedLength),
            Arrays.copyOfRange(actual.byteArray(), 200, 200 + written));
        assertEquals("Car", encoder.messageName());
        assertEquals(1, encoder.templateId());
    }

    private static void assertBytesEqualOracle(
        final String schema,
        final String messageName,
        final String json,
        final ToIntBiFunction<UnsafeBuffer, Integer> oracle) throws Exception
    {
        final Ir ir = TestMessages.ir(schema);
        final UnsafeBuffer expected = TestMessages.newBuffer(CAPACITY);
        final int expectedLength = oracle.applyAsInt(expected, 0);

        final SbeJsonEncoder encoder = SbeJson.builder(ir).build().newEncoder(messageName);
        final JsonNode tree = JsonNodes.MAPPER.readTree(json);
        final UnsafeBuffer actual = TestMessages.newBuffer(CAPACITY);
        final int actualLength = encoder.encode(tree, actual, 0, CAPACITY);

        assertEquals(expectedLength, actualLength);
        assertEquals(expectedLength, encoder.encodedLength(tree));
        assertArrayEquals(
            Arrays.copyOf(expected.byteArray(), expectedLength), Arrays.copyOf(actual.byteArray(), actualLength));
    }
}
