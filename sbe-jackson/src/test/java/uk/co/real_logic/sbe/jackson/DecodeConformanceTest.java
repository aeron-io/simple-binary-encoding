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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.real_logic.sbe.ir.Ir;
import uk.co.real_logic.sbe.json.JsonPrinter;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.function.ToIntBiFunction;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * {@code readTree(JsonPrinter.print(...))} must equal {@code decodeCopy(...)} semantically. The printer emits bit
 * sets as objects of booleans, so the comparison uses {@link BitSetStyle#OBJECT}; the remaining documented
 * divergences (uint64 above {@code Long.MAX_VALUE}, binary var-data, unknown enum values, NaN) are covered by
 * dedicated tests below.
 */
class DecodeConformanceTest
{
    private static final int CAPACITY = 4096;

    @Test
    void baselineCarMatchesJsonPrinter() throws Exception
    {
        assertMatchesPrinter(TestMessages.BASELINE_SCHEMA, TestMessages::encodeBaselineCar);
    }

    @Test
    void extensionCarMatchesJsonPrinter() throws Exception
    {
        assertMatchesPrinter(TestMessages.EXTENSION_SCHEMA, TestMessages::encodeExtensionCar);
    }

    @Test
    void compositeElementsMatchJsonPrinter() throws Exception
    {
        assertMatchesPrinter(TestMessages.COMPOSITE_ELEMENTS_SCHEMA, TestMessages::encodeCompositeElements);
    }

    @Test
    void groupWithDataMatchesJsonPrinter() throws Exception
    {
        assertMatchesPrinter(TestMessages.GROUP_WITH_DATA_SCHEMA, TestMessages::encodeGroupWithData);
    }

    @Test
    void decodeCopyAtNonZeroOffsetProducesSameTree()
    {
        final Ir ir = TestMessages.ir(TestMessages.BASELINE_SCHEMA);
        final SbeJson sbeJson = SbeJson.builder(ir).build();
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        final int length = TestMessages.encodeBaselineCar(buffer, 0);
        final ObjectNode atZero = sbeJson.newDecoder().decodeCopy(buffer, 0, length);

        final UnsafeBuffer shifted = TestMessages.newBuffer(CAPACITY);
        shifted.putBytes(101, buffer, 0, length);
        final SbeJsonDecoder decoder = sbeJson.newDecoder();
        final ObjectNode atOffset = decoder.decodeCopy(shifted, 101, length);

        JsonNodes.assertSemanticEquals(atZero, atOffset);
        assertEquals(1, decoder.lastHeader().templateId());
        assertEquals(1, decoder.lastHeader().schemaId());
        assertEquals(2, decoder.lastHeader().actingVersion());
        assertEquals(ir.getMessage(1).get(0).encodedLength(), decoder.lastHeader().blockLength());
    }

    @Test
    void bitSetDecodesAsMaskByDefault()
    {
        final ObjectNode car = decodeBaselineCar(SbeJson.builder(TestMessages.ir(TestMessages.BASELINE_SCHEMA)));
        assertEquals(6, car.get("extras").intValue());
        assertTrue(car.get("extras").isIntegralNumber());
    }

    @Test
    void enumDecodesAsNameByDefaultAndOrdinalOnRequest()
    {
        final Ir ir = TestMessages.ir(TestMessages.BASELINE_SCHEMA);
        final ObjectNode named = decodeBaselineCar(SbeJson.builder(ir));
        assertEquals("T", named.get("available").textValue());
        assertEquals("A", named.get("code").textValue());
        assertEquals("Petrol", named.get("engine").get("fuel").textValue());
        assertEquals(9000, named.get("engine").get("maxRpm").intValue());

        final ObjectNode ordinal = decodeBaselineCar(SbeJson.builder(ir).enumStyle(EnumStyle.ORDINAL));
        assertEquals(1, ordinal.get("available").intValue());
        assertEquals('A', ordinal.get("code").intValue());
    }

    @Test
    void unknownEnumValueDecodesAsNumber()
    {
        final Ir ir = TestMessages.ir(TestMessages.BASELINE_SCHEMA);
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        final int length = TestMessages.encodeBaselineCar(buffer, 0);
        buffer.putByte(8 + 10, (byte)7);

        final ObjectNode car = SbeJson.builder(ir).build().newDecoder().decodeCopy(buffer, 0, length);
        assertEquals(7, car.get("available").intValue());
    }

    @Test
    void uint64AboveLongMaxDecodesUnsigned()
    {
        final Ir ir = TestMessages.ir(TestMessages.BASELINE_SCHEMA);
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        final int length = TestMessages.encodeBaselineCar(buffer, 0);
        buffer.putLong(8, -2L, java.nio.ByteOrder.LITTLE_ENDIAN);

        final ObjectNode car = SbeJson.builder(ir).build().newDecoder().decodeCopy(buffer, 0, length);
        final JsonNode serialNumber = car.get("serialNumber");
        assertTrue(serialNumber.isBigInteger());
        assertEquals(new BigInteger("18446744073709551614"), serialNumber.bigIntegerValue());
    }

    @Test
    void binaryVarDataDecodesAsBinaryNode() throws Exception
    {
        final Ir ir = TestMessages.ir(TestMessages.BASELINE_SCHEMA);
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        final baseline.MessageHeaderEncoder header = new baseline.MessageHeaderEncoder();
        final baseline.CredentialsEncoder credentials = new baseline.CredentialsEncoder();
        final byte[] password = { 0, 1, 2, (byte)0xFE, (byte)0xFF, 'x' };
        credentials.wrapAndApplyHeader(buffer, 0, header)
            .login("bob")
            .putEncryptedPassword(password, 0, password.length);
        final int length = header.encodedLength() + credentials.encodedLength();

        final ObjectNode tree = SbeJson.builder(ir).build().newDecoder().decodeCopy(buffer, 0, length);
        assertEquals("bob", tree.get("login").textValue());
        assertTrue(tree.get("encryptedPassword").isBinary());
        assertArrayEquals(password, tree.get("encryptedPassword").binaryValue());
        assertEquals("\"AAEC/v94\"", JsonNodes.MAPPER.writeValueAsString(tree.get("encryptedPassword")));
    }

    @Test
    void nanFloatDecodesAsNanNumberWhenRequiredAndNullWhenOptional()
    {
        final Ir ir = TestMessages.ir(TestMessages.BASELINE_SCHEMA);
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        final int length = TestMessages.encodeBaselineCar(buffer, 0);
        final ObjectNode reference = SbeJson.builder(ir).build().newDecoder().decodeCopy(buffer, 0, length);

        // fuelFigures[0].mpg is required: NaN stays a number.
        final int fuelFiguresDims = 8 + ir.getMessage(1).get(0).encodedLength();
        buffer.putFloat(fuelFiguresDims + 3 + 2, Float.NaN, java.nio.ByteOrder.LITTLE_ENDIAN);
        final ObjectNode car = SbeJson.builder(ir).build().newDecoder().decodeCopy(buffer, 0, length);
        final JsonNode mpg = car.get("fuelFigures").get(0).get("mpg");
        assertTrue(mpg.isNumber());
        assertTrue(Double.isNaN(mpg.doubleValue()));
        assertEquals(reference.get("fuelFigures").get(1), car.get("fuelFigures").get(1));

        // cupHolderCount is optional uint8: 255 decodes as null.
        buffer.putByte(8 + 61, (byte)0xFF);
        final ObjectNode withNull = SbeJson.builder(ir).build().newDecoder().decodeCopy(buffer, 0, length);
        assertTrue(withNull.get("cupHolderCount").isNull());
        assertTrue(withNull.has("cupHolderCount"));
    }

    @ParameterizedTest
    @ValueSource(strings = { "NUL_TERMINATED", "EXACT" })
    void charArrayStyleControlsTrailingNuls(final String style)
    {
        final Ir ir = TestMessages.ir(TestMessages.BASELINE_SCHEMA);
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        final int length = TestMessages.encodeBaselineCar(buffer, 0);
        buffer.putBytes(8 + 32, "ab\0\0ef".getBytes(StandardCharsets.US_ASCII));

        final ObjectNode car = SbeJson.builder(ir).charArrayStyle(CharArrayStyle.valueOf(style)).build()
            .newDecoder().decodeCopy(buffer, 0, length);
        final String expected = "EXACT".equals(style) ? "ab\0\0ef" : "ab";
        assertEquals(expected, car.get("vehicleCode").textValue());
    }

    private static ObjectNode decodeBaselineCar(final SbeJson.Builder builder)
    {
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        final int length = TestMessages.encodeBaselineCar(buffer, 0);

        return builder.build().newDecoder().decodeCopy(buffer, 0, length);
    }

    private static void assertMatchesPrinter(
        final String schema, final ToIntBiFunction<UnsafeBuffer, Integer> encoder) throws Exception
    {
        final Ir ir = TestMessages.ir(schema);
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        final int length = encoder.applyAsInt(buffer, 0);

        final StringBuilder printed = new StringBuilder();
        new JsonPrinter(ir).print(printed, buffer, 0);
        final JsonNode expected = JsonNodes.MAPPER.readTree(printed.toString());

        final SbeJson sbeJson = SbeJson.builder(ir).bitSetStyle(BitSetStyle.OBJECT).build();
        final ObjectNode actual = sbeJson.newDecoder().decodeCopy(buffer, 0, length);

        JsonNodes.assertSemanticEquals(expected, actual);
    }
}
