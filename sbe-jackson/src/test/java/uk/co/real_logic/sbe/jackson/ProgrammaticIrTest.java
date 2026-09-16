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
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.jupiter.api.Test;
import uk.co.real_logic.sbe.ir.Ir;
import uk.co.real_logic.sbe.json.JsonPrinter;
import uk.co.real_logic.sbe.otf.OtfHeaderDecoder;

import java.nio.ByteOrder;

import static java.nio.ByteOrder.BIG_ENDIAN;
import static java.nio.ByteOrder.LITTLE_ENDIAN;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Layouts only a hand-built IR can express: versioned composite members, mixed byte orders and uint32 headers.
 */
class ProgrammaticIrTest
{
    private static final int CAPACITY = 256;
    private static final JsonNodeFactory F = JsonNodeFactory.instance;

    @Test
    void compositeMembersAreBoundsCheckedIndividuallyAgainstTheActingBlock()
    {
        final Ir ir = ProgrammaticIrs.versionedComposite();
        final SbeJson sbeJson = SbeJson.builder(ir).build();
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);

        // A version 0 producer: block length 4, only member 'a' on the wire. The compiled composite is 8 bytes.
        writeHeader(buffer, 4, 1, ProgrammaticIrs.SCHEMA_ID, 0);
        buffer.putInt(8, 42, LITTLE_ENDIAN);
        final ObjectNode v0 = sbeJson.newDecoder().decodeCopy(buffer, 0, 12);
        assertEquals(42, v0.get("comp").get("a").intValue());
        assertFalse(v0.get("comp").has("b"));

        // Version 1 with block length 8: both members.
        writeHeader(buffer, 8, 1, ProgrammaticIrs.SCHEMA_ID, 1);
        buffer.putInt(12, 43, LITTLE_ENDIAN);
        final ObjectNode v1 = sbeJson.newDecoder().decodeCopy(buffer, 0, 16);
        assertEquals(42, v1.get("comp").get("a").intValue());
        assertEquals(43, v1.get("comp").get("b").intValue());

        // Version 1 claiming block length 4: member 'b' is present in the version but outside the block.
        writeHeader(buffer, 4, 1, ProgrammaticIrs.SCHEMA_ID, 1);
        final SbeJsonException ex = assertThrows(
            SbeJsonException.class, () -> sbeJson.newDecoder().decodeCopy(buffer, 0, 16));
        assertEquals(ErrorCode.FIELD_OUTSIDE_BLOCK, ex.code());
        assertEquals("Msg.comp.b", ex.path());
        assertEquals(12, ex.byteOffset());
    }

    @Test
    void headerAndGroupDimensionMembersKeepTheirOwnByteOrders() throws Exception
    {
        final Ir ir = ProgrammaticIrs.mixedByteOrders();
        final SbeJson sbeJson = SbeJson.builder(ir).build();
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);

        final ObjectNode message = F.objectNode();
        message.put("x", 7);
        message.putArray("g").addObject().put("y", -2);
        message.put("d", "hi");

        final SbeJsonEncoder encoder = sbeJson.newEncoder("Mixed");
        final int length = encoder.encode(message, buffer, 0, CAPACITY);
        assertEquals(22, length);
        assertEquals(length, encoder.encodedLength(message));

        // Header: blockLength LE, templateId BE, schemaId LE, version BE.
        assertEquals(4, buffer.getShort(0, LITTLE_ENDIAN));
        assertEquals(1, buffer.getShort(2, BIG_ENDIAN));
        assertEquals(ProgrammaticIrs.SCHEMA_ID, buffer.getShort(4, LITTLE_ENDIAN));
        assertEquals(0, buffer.getShort(6, BIG_ENDIAN));
        // Group dimensions: blockLength LE, numInGroup BE; entry field y BE; var-data length BE.
        assertEquals(2, buffer.getShort(12, LITTLE_ENDIAN));
        assertEquals(1, buffer.getShort(14, BIG_ENDIAN));
        assertEquals(-2, buffer.getShort(16, BIG_ENDIAN));
        assertEquals(2, buffer.getShort(18, BIG_ENDIAN));

        // Oracle: the OTF header decoder and JsonPrinter honour per-member encodings.
        final OtfHeaderDecoder otfHeader = new OtfHeaderDecoder(ir.headerStructure());
        assertEquals(4, otfHeader.getBlockLength(buffer, 0));
        assertEquals(1, otfHeader.getTemplateId(buffer, 0));
        assertEquals(ProgrammaticIrs.SCHEMA_ID, otfHeader.getSchemaId(buffer, 0));
        assertEquals(0, otfHeader.getSchemaVersion(buffer, 0));

        final StringBuilder printed = new StringBuilder();
        new JsonPrinter(ir).print(printed, buffer, 0);
        final JsonNode oracle = JsonNodes.MAPPER.readTree(printed.toString());
        assertEquals(7, oracle.get("x").intValue());
        assertEquals(1, oracle.get("g").size());
        assertEquals(-2, oracle.get("g").get(0).get("y").intValue());
        assertEquals("hi", oracle.get("d").textValue());

        final ObjectNode decoded = sbeJson.newDecoder().decodeCopy(buffer, 0, length);
        JsonNodes.assertSemanticEquals(message, decoded);
    }

    @Test
    void uint32HeaderMembersAtOrAbove2Pow31AreRejectedNotThrownAsIllegalState()
    {
        final Ir ir = ProgrammaticIrs.uint32Header();
        final SbeJson sbeJson = SbeJson.builder(ir).build();
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);

        buffer.putInt(0, 1, LITTLE_ENDIAN);
        buffer.putInt(4, 1, LITTLE_ENDIAN);
        buffer.putInt(8, ProgrammaticIrs.SCHEMA_ID, LITTLE_ENDIAN);
        buffer.putInt(12, 0, LITTLE_ENDIAN);
        buffer.putByte(16, (byte)9);
        assertEquals(9, sbeJson.newDecoder().decodeCopy(buffer, 0, 17).get("a").intValue());

        buffer.putInt(4, 0x80000000, LITTLE_ENDIAN);
        assertThrows(
            IllegalStateException.class, () -> new OtfHeaderDecoder(ir.headerStructure()).getTemplateId(buffer, 0));

        final SbeJsonException templateId = assertThrows(
            SbeJsonException.class, () -> sbeJson.newDecoder().decodeCopy(buffer, 0, 17));
        assertEquals(ErrorCode.OUT_OF_RANGE, templateId.code());
        assertEquals(4, templateId.byteOffset());

        buffer.putInt(4, 1, LITTLE_ENDIAN);
        buffer.putInt(0, 0xFFFFFFFF, LITTLE_ENDIAN);
        final SbeJsonException blockLength = assertThrows(
            SbeJsonException.class, () -> sbeJson.newDecoder().decodeCopy(buffer, 0, 17));
        assertEquals(ErrorCode.OUT_OF_RANGE, blockLength.code());
        assertEquals(0, blockLength.byteOffset());
    }

    private static void writeHeader(
        final UnsafeBuffer buffer, final int blockLength, final int templateId, final int schemaId, final int version)
    {
        final ByteOrder order = LITTLE_ENDIAN;
        buffer.putShort(0, (short)blockLength, order);
        buffer.putShort(2, (short)templateId, order);
        buffer.putShort(4, (short)schemaId, order);
        buffer.putShort(6, (short)version, order);
    }
}
