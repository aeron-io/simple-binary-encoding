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
import uk.co.real_logic.sbe.ir.Ir;

import java.nio.ByteOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VersioningTest
{
    private static final int CAPACITY = 4096;
    private static final int HEADER_LENGTH = 8;

    @Test
    void olderMessageDecodesWithNewerSchemaOmittingAbsentFields() throws Exception
    {
        final SbeJson v1 = SbeJson.builder(TestMessages.ir("versioned-group-v1.xml")).build();
        final SbeJson v2 = SbeJson.builder(TestMessages.ir("versioned-group-v2.xml")).build();
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        final JsonNode order = JsonNodes.MAPPER.readTree(
            "{\"id\":42,\"legs\":[{\"qty\":5},{\"qty\":6}],\"note\":\"hi\"}");
        final int length = v1.newEncoder("Order").encode(order, buffer, 0, CAPACITY);

        final SbeJsonDecoder decoder = v2.newDecoder();
        final ObjectNode decoded = decoder.decodeCopy(buffer, 0, length);

        assertEquals(1, decoder.lastHeader().actingVersion());
        assertEquals(4, decoder.lastHeader().blockLength());
        JsonNodes.assertSemanticEquals(order, decoded);
        assertFalse(decoded.has("price"));
        assertFalse(decoded.has("fills"));
        assertFalse(decoded.has("memo"));
        assertFalse(decoded.get("legs").get(0).has("side"));
    }

    @Test
    void newerMessageIsRejected() throws Exception
    {
        final SbeJson v1 = SbeJson.builder(TestMessages.ir("versioned-group-v1.xml")).build();
        final SbeJson v2 = SbeJson.builder(TestMessages.ir("versioned-group-v2.xml")).build();
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        final JsonNode order = JsonNodes.MAPPER.readTree(
            "{\"id\":42,\"price\":null,\"legs\":[{\"qty\":5,\"side\":1}],\"fills\":[{\"px\":7}]," +
            "\"note\":\"hi\",\"memo\":\"m\"}");
        final int length = v2.newEncoder("Order").encode(order, buffer, 0, CAPACITY);
        JsonNodes.assertSemanticEquals(order, v2.newDecoder().decodeCopy(buffer, 0, length));

        final SbeJsonDecoder decoder = v1.newDecoder();
        final SbeJsonException ex = assertThrows(
            SbeJsonException.class, () -> decoder.decodeCopy(buffer, 0, length));

        assertEquals(ErrorCode.UNSUPPORTED_VERSION, ex.code());
        assertEquals(1, ex.templateId());
        assertTrue(decoder.lastHeader().populated());
        assertEquals(2, decoder.lastHeader().actingVersion());
    }

    @Test
    void extensionCarWithOlderActingVersionOmitsVersionedFieldsAndSkipsTrailingGroupBytes()
    {
        final Ir ir = TestMessages.ir(TestMessages.EXTENSION_SCHEMA);
        final SbeJson sbeJson = SbeJson.builder(ir).build();
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        final int length = TestMessages.encodeExtensionCar(buffer, 0);
        final ObjectNode v2 = sbeJson.newDecoder().decodeCopy(buffer, 0, length);
        assertTrue(v2.has("uuid"));
        assertTrue(v2.get("fuelFigures").get(0).has("mpg"));

        buffer.putShort(6, (short)1, ByteOrder.LITTLE_ENDIAN);
        final ObjectNode v1 = sbeJson.newDecoder().decodeCopy(buffer, 0, length);
        assertTrue(v1.has("uuid"));
        assertTrue(v1.has("cupHolderCount"));
        assertFalse(v1.get("fuelFigures").get(0).has("mpg"));
        assertEquals("Urban Cycle", v1.get("fuelFigures").get(0).get("usageDescription").textValue());

        buffer.putShort(6, (short)0, ByteOrder.LITTLE_ENDIAN);
        final ObjectNode v0 = sbeJson.newDecoder().decodeCopy(buffer, 0, length);
        assertFalse(v0.has("uuid"));
        assertFalse(v0.has("cupHolderCount"));
        assertFalse(v0.get("fuelFigures").get(0).has("mpg"));

        final ObjectNode expected = v2.deepCopy();
        expected.remove("uuid");
        expected.remove("cupHolderCount");
        expected.withArray("fuelFigures").forEach(entry -> ((ObjectNode)entry).remove("mpg"));
        JsonNodes.assertSemanticEquals(expected, v0);
    }

    @Test
    void largerHeaderBlockLengthSkipsTrailingRootBytes()
    {
        final Ir ir = TestMessages.ir(TestMessages.BASELINE_SCHEMA);
        final SbeJson sbeJson = SbeJson.builder(ir).build();
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        final int length = TestMessages.encodeBaselineCar(buffer, 0);
        final ObjectNode reference = sbeJson.newDecoder().decodeCopy(buffer, 0, length);
        final int blockLength = ir.getMessage(1).get(0).encodedLength();

        final int padding = 4;
        final UnsafeBuffer padded = TestMessages.newBuffer(CAPACITY);
        padded.putBytes(0, buffer, 0, HEADER_LENGTH + blockLength);
        padded.putBytes(
            HEADER_LENGTH + blockLength + padding, buffer, HEADER_LENGTH + blockLength,
            length - HEADER_LENGTH - blockLength);
        padded.putShort(0, (short)(blockLength + padding), ByteOrder.LITTLE_ENDIAN);

        final SbeJsonDecoder decoder = sbeJson.newDecoder();
        final ObjectNode decoded = decoder.decodeCopy(padded, 0, length + padding);
        JsonNodes.assertSemanticEquals(reference, decoded);
        assertEquals(blockLength + padding, decoder.lastHeader().blockLength());
    }

    @Test
    void smallerHeaderBlockLengthThanFieldsNeedFailsFieldOutsideBlock()
    {
        final Ir ir = TestMessages.ir(TestMessages.BASELINE_SCHEMA);
        final SbeJson sbeJson = SbeJson.builder(ir).build();
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        final int length = TestMessages.encodeBaselineCar(buffer, 0);
        buffer.putShort(0, (short)40, ByteOrder.LITTLE_ENDIAN);

        final SbeJsonException ex = assertThrows(
            SbeJsonException.class, () -> sbeJson.newDecoder().decodeCopy(buffer, 0, length));

        assertEquals(ErrorCode.FIELD_OUTSIDE_BLOCK, ex.code());
        assertEquals("Car.engine.capacity", ex.path());
        assertEquals(HEADER_LENGTH + 39, ex.byteOffset());
    }

    @Test
    void sbeToolVersionedSchemasWithoutSinceVersionFailFieldOutsideBlock() throws Exception
    {
        final SbeJson v1 = SbeJson.builder(TestMessages.ir(TestMessages.VERSIONED_V1_SCHEMA)).build();
        final SbeJson v2 = SbeJson.builder(TestMessages.ir(TestMessages.VERSIONED_V2_SCHEMA)).build();
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        final JsonNode message = JsonNodes.MAPPER.readTree("{\"FieldA1\":1,\"FieldB1\":2,\"String1\":\"s\"}");
        final int length = v1.newEncoder("VersionedMessageV1").encode(message, buffer, 0, CAPACITY);
        JsonNodes.assertSemanticEquals(message, v1.newDecoder().decodeCopy(buffer, 0, length));

        // The v2 schema declares FieldC2..E2 without sinceVersion, so a v1 block cannot hold them.
        final SbeJsonException ex = assertThrows(
            SbeJsonException.class, () -> v2.newDecoder().decodeCopy(buffer, 0, length));
        assertEquals(ErrorCode.FIELD_OUTSIDE_BLOCK, ex.code());
        assertEquals("VersionedMessageV2.FieldC2", ex.path());
    }

    @Test
    void lastHeaderIsPopulatedAfterDecodeCopyAndOverwrittenByTheNext() throws Exception
    {
        final Ir ir = TestMessages.ir(TestMessages.BASELINE_SCHEMA);
        final SbeJsonDecoder decoder = SbeJson.builder(ir).build().newDecoder();
        assertFalse(decoder.lastHeader().populated());

        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        final int length = TestMessages.encodeBaselineCar(buffer, 0);
        decoder.decodeCopy(buffer, 0, length);
        assertTrue(decoder.lastHeader().populated());
        assertEquals(1, decoder.lastHeader().templateId());

        buffer.putShort(2, (short)2, ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(0, (short)0, ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(HEADER_LENGTH, (short)0, ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(HEADER_LENGTH + 1, 0, ByteOrder.LITTLE_ENDIAN);
        final ObjectNode credentials = decoder.decodeCopy(buffer, 0, HEADER_LENGTH + 1 + 4);
        assertEquals(2, decoder.lastHeader().templateId());
        assertEquals(0, decoder.lastHeader().blockLength());
        assertEquals("", credentials.get("login").textValue());
        assertEquals(0, credentials.get("encryptedPassword").binaryValue().length);
    }
}
