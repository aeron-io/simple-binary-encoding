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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.jupiter.api.Test;
import uk.co.real_logic.sbe.ir.Ir;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.function.Consumer;

import static java.nio.ByteOrder.LITTLE_ENDIAN;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Edge cases from {@code edge-cases-schema.xml}: uint64 enums and sets around 2^63 and 2^64, optional
 * float/double sentinels, non UTF-8 text, var-data limits enforced before allocation, messages beyond the int
 * range and trailing empty groups.
 */
class EdgeCaseTest
{
    private static final int CAPACITY = 1 << 17;
    private static final int HEADER = 8;
    private static final int BLOCK_LENGTH = 44;
    private static final JsonNodeFactory F = JsonNodeFactory.instance;
    private static final Ir IR = TestMessages.ir(TestMessages.EDGE_SCHEMA);
    private static final BigInteger TWO_POW_63 = BigInteger.ONE.shiftLeft(63);
    private static final BigInteger TWO_POW_64 = BigInteger.ONE.shiftLeft(64);
    private static final BigInteger MAX_UINT64 = TWO_POW_64.subtract(BigInteger.ONE);
    private static final BigInteger TOP = MAX_UINT64.subtract(BigInteger.ONE);

    @Test
    void uint64EnumRoundTripsAt2Pow63AndAboveInBothStyles()
    {
        final SbeJson names = SbeJson.builder(IR).build();
        final SbeJson ordinals = SbeJson.builder(IR).enumStyle(EnumStyle.ORDINAL).build();
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);

        int length = encode(names, "Edge", edge().put("bigEnum", "HIGH"), buffer);
        assertEquals(Long.MIN_VALUE, buffer.getLong(HEADER, LITTLE_ENDIAN));
        assertEquals("HIGH", names.newDecoder().decodeCopy(buffer, 0, length).get("bigEnum").textValue());
        final JsonNode high = ordinals.newDecoder().decodeCopy(buffer, 0, length).get("bigEnum");
        assertTrue(high.isBigInteger(), high.toString());
        assertEquals(TWO_POW_63, high.bigIntegerValue());

        length = encode(names, "Edge", edge().set("bigEnum", F.numberNode(TWO_POW_63)), buffer);
        assertEquals(Long.MIN_VALUE, buffer.getLong(HEADER, LITTLE_ENDIAN));

        length = encode(names, "Edge", edge().put("bigEnum", "TOP"), buffer);
        assertEquals(-2L, buffer.getLong(HEADER, LITTLE_ENDIAN));
        assertEquals(TOP, ordinals.newDecoder().decodeCopy(buffer, 0, length).get("bigEnum").bigIntegerValue());

        // An unknown but representable value: 2^64 - 1 encodes and decodes as an unsigned number in both styles.
        length = encode(names, "Edge", edge().set("bigEnum", F.numberNode(MAX_UINT64)), buffer);
        assertEquals(-1L, buffer.getLong(HEADER, LITTLE_ENDIAN));
        assertEquals(MAX_UINT64, names.newDecoder().decodeCopy(buffer, 0, length).get("bigEnum").bigIntegerValue());
        assertEquals(
            MAX_UINT64, ordinals.newDecoder().decodeCopy(buffer, 0, length).get("bigEnum").bigIntegerValue());

        // Constants: the uint64 constant and the ORDINAL style constant enum are unsigned nodes.
        final ObjectNode decoded = ordinals.newDecoder().decodeCopy(buffer, 0, length);
        assertEquals(TOP, decoded.get("bigConst").bigIntegerValue());
        assertEquals(TOP, decoded.get("constEnum").bigIntegerValue());
        assertEquals("TOP", names.newDecoder().decodeCopy(buffer, 0, length).get("constEnum").textValue());
        encode(ordinals, "Edge", edge().set("constEnum", F.numberNode(TOP)), buffer);
    }

    @Test
    void uint64EnumAndSetRejectNegativeAndAbove2Pow64()
    {
        final SbeJson sbeJson = SbeJson.builder(IR).build();
        assertRejects(sbeJson, ErrorCode.OUT_OF_RANGE, "Edge.bigEnum", e -> e.put("bigEnum", -1));
        assertRejects(sbeJson, ErrorCode.OUT_OF_RANGE, "Edge.bigEnum", e -> e.put("bigEnum", Long.MIN_VALUE));
        assertRejects(sbeJson, ErrorCode.OUT_OF_RANGE, "Edge.bigEnum", e -> e.set("bigEnum", F.numberNode(TWO_POW_64)));
        assertRejects(sbeJson, ErrorCode.OUT_OF_RANGE, "Edge.bigSet", e -> e.put("bigSet", -1));
        assertRejects(sbeJson, ErrorCode.OUT_OF_RANGE, "Edge.bigSet", e -> e.set("bigSet", F.numberNode(TWO_POW_64)));
        assertRejects(sbeJson, ErrorCode.CONSTANT_MISMATCH, "Edge.constEnum", e -> e.put("constEnum", -2));
    }

    @Test
    void uint64SetBit63RoundTripsAsMaskAndAsObject()
    {
        final SbeJson masks = SbeJson.builder(IR).build();
        final SbeJson objects = SbeJson.builder(IR).bitSetStyle(BitSetStyle.OBJECT).build();
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);

        int length = encode(masks, "Edge", edge().set("bigSet", F.numberNode(TWO_POW_63)), buffer);
        assertEquals(Long.MIN_VALUE, buffer.getLong(HEADER + 8, LITTLE_ENDIAN));
        assertEquals(TWO_POW_63, masks.newDecoder().decodeCopy(buffer, 0, length).get("bigSet").bigIntegerValue());
        final JsonNode asObject = objects.newDecoder().decodeCopy(buffer, 0, length).get("bigSet");
        assertEquals(false, asObject.get("low").booleanValue());
        assertEquals(true, asObject.get("high").booleanValue());

        final ObjectNode viaObject = edge();
        viaObject.putObject("bigSet").put("high", true).put("low", true);
        length = encode(objects, "Edge", viaObject, buffer);
        assertEquals(Long.MIN_VALUE | 1L, buffer.getLong(HEADER + 8, LITTLE_ENDIAN));
        assertEquals(
            TWO_POW_63.add(BigInteger.ONE),
            masks.newDecoder().decodeCopy(buffer, 0, length).get("bigSet").bigIntegerValue());
    }

    @Test
    void utf16CharArrayTerminatesAtTheNulCharacterNotTheFirstZeroByte()
    {
        final SbeJson nulTerminated = SbeJson.builder(IR).build();
        final SbeJson exact = SbeJson.builder(IR).charArrayStyle(CharArrayStyle.EXACT).build();
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);

        final int length = encode(nulTerminated, "Edge", edge().put("name16", "AB"), buffer);
        final byte[] name = new byte[8];
        buffer.getBytes(HEADER + 36, name);
        assertArrayEquals(new byte[]{ 0, 0x41, 0, 0x42, 0, 0, 0, 0 }, name);

        assertEquals("AB", nulTerminated.newDecoder().decodeCopy(buffer, 0, length).get("name16").textValue());
        assertEquals("AB\0\0", exact.newDecoder().decodeCopy(buffer, 0, length).get("name16").textValue());

        assertRejects(nulTerminated, ErrorCode.OUT_OF_RANGE, "Edge.name16", e -> e.put("name16", "ABCDE"));
    }

    @Test
    void optionalFloatAndDoubleSentinelsDecodeAsNullAndEncodeFromNull()
    {
        final SbeJson sbeJson = SbeJson.builder(IR).build();
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);

        final ObjectNode omitted = edge();
        int length = encode(sbeJson, "Edge", omitted, buffer);
        assertTrue(Float.isNaN(buffer.getFloat(HEADER + 16, LITTLE_ENDIAN)));
        assertTrue(Double.isNaN(buffer.getDouble(HEADER + 20, LITTLE_ENDIAN)));
        assertEquals(-1L, buffer.getLong(HEADER + 28, LITTLE_ENDIAN));
        ObjectNode decoded = sbeJson.newDecoder().decodeCopy(buffer, 0, length);
        assertTrue(decoded.get("optFloat").isNull());
        assertTrue(decoded.get("optDouble").isNull());
        assertTrue(decoded.get("optU64").isNull());

        final ObjectNode explicitNull = edge();
        explicitNull.putNull("optFloat").putNull("optDouble").putNull("optU64");
        final UnsafeBuffer again = TestMessages.newBuffer(CAPACITY);
        assertEquals(length, encode(sbeJson, "Edge", explicitNull, again));
        assertArrayEquals(Arrays.copyOf(buffer.byteArray(), length), Arrays.copyOf(again.byteArray(), length));

        final ObjectNode present = edge().put("optFloat", 1.5f).put("optDouble", 2.5d);
        present.set("optU64", F.numberNode(MAX_UINT64.subtract(BigInteger.ONE)));
        length = encode(sbeJson, "Edge", present, buffer);
        decoded = sbeJson.newDecoder().decodeCopy(buffer, 0, length);
        assertEquals(1.5f, decoded.get("optFloat").floatValue());
        assertEquals(2.5d, decoded.get("optDouble").doubleValue());
        assertEquals(TOP, decoded.get("optU64").bigIntegerValue());

        // Required float NaN stays a number: a wire NaN in an optional slot is the sentinel, nothing else.
        buffer.putFloat(HEADER + 16, Float.NaN, LITTLE_ENDIAN);
        buffer.putDouble(HEADER + 20, Double.NaN, LITTLE_ENDIAN);
        decoded = sbeJson.newDecoder().decodeCopy(buffer, 0, length);
        assertTrue(decoded.get("optFloat").isNull());
        assertTrue(decoded.get("optDouble").isNull());
    }

    @Test
    void varDataLimitsAreEnforcedBeforeThePayloadIsMaterialised()
    {
        final SbeJson small = SbeJson.builder(IR).limits(Limits.builder().maxVarDataBytes(10).build()).build();
        final String longText = "x".repeat(100);
        final byte[] thirtyBytes = new byte[30];
        final String base64 = Base64.getEncoder().encodeToString(thirtyBytes);

        assertRejects(small, ErrorCode.LIMIT_EXCEEDED, "Edge.text16", e -> e.put("text16", longText));
        assertRejects(small, ErrorCode.LIMIT_EXCEEDED, "Edge.blob", e -> e.put("blob", base64));
        assertRejects(small, ErrorCode.LIMIT_EXCEEDED, "Edge.blob", e -> e.set("blob", F.binaryNode(thirtyBytes)));

        // Within budget in the other charset path: exactly 10 bytes of UTF-16 (BOM + 4 chars).
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        final int length = encode(small, "Edge", edge().put("text16", "abcd"), buffer);
        assertEquals("abcd", small.newDecoder().decodeCopy(buffer, 0, length).get("text16").textValue());

        // Length type maximum (uint16) beats a generous var-data budget and destination.
        final SbeJson defaults = SbeJson.builder(IR).build();
        assertRejects(defaults, ErrorCode.OUT_OF_RANGE, "Edge.text16", e -> e.put("text16", "y".repeat(40000)));
        assertRejects(defaults, ErrorCode.OUT_OF_RANGE, "Edge.blob",
            e -> e.put("blob", Base64.getEncoder().encodeToString(new byte[70000])));

        // Destination smaller than the payload: rejected before decoding the base64 or encoding the text.
        final UnsafeBuffer tiny = TestMessages.newBuffer(HEADER + BLOCK_LENGTH + 4 + 2 + 2 + 4);
        final SbeJsonEncoder encoder = defaults.newEncoder("Edge");
        final SbeJsonException text = assertThrows(SbeJsonException.class,
            () -> encoder.encode(edge().put("text16", "toolong"), tiny, 0, tiny.capacity()));
        assertEquals(ErrorCode.DESTINATION_OVERFLOW, text.code());
        assertEquals("Edge.text16", text.path());
        final SbeJsonException blob = assertThrows(SbeJsonException.class,
            () -> encoder.encode(edge().put("blob", base64), tiny, 0, tiny.capacity()));
        assertEquals(ErrorCode.DESTINATION_OVERFLOW, blob.code());
        assertEquals("Edge.blob", blob.path());

        // Ordinary UTF-16 text and binary round-trip.
        final ObjectNode full = edge().put("text16", "héllo 🚗");
        full.set("blob", F.binaryNode(new byte[]{ 1, 2, 3 }));
        final int fullLength = encode(defaults, "Edge", full, buffer);
        final ObjectNode decoded = defaults.newDecoder().decodeCopy(buffer, 0, fullLength);
        assertEquals("héllo 🚗", decoded.get("text16").textValue());
        assertEquals(F.binaryNode(new byte[]{ 1, 2, 3 }), decoded.get("blob"));
        assertEquals(
            "héllo 🚗".getBytes(StandardCharsets.UTF_16).length,
            buffer.getShort(fullLength - 2 - 3 - 2 - "héllo 🚗".getBytes(StandardCharsets.UTF_16).length,
            LITTLE_ENDIAN));
    }

    @Test
    void messagesBeyondTheIntRangeAreRejectedBeforeArithmeticWraps()
    {
        final SbeJson sbeJson = SbeJson.builder(IR).build();
        final ObjectNode one = F.objectNode();
        one.putArray("rows").addObject().put("pad", "");
        final SbeJsonEncoder encoder = sbeJson.newEncoder("Wide");
        assertEquals(HEADER + 4 + 65000, encoder.encodedLength(one));

        // 34000 x 65000 = 2.21e9 > Integer.MAX_VALUE: the sizing walk must reject, not wrap to a small positive.
        final ObjectNode wide = F.objectNode();
        final ArrayNode rows = wide.putArray("rows");
        for (int i = 0; i < 34000; i++)
        {
            rows.addObject().put("pad", "");
        }

        final SbeJsonException sizing = assertThrows(SbeJsonException.class, () -> encoder.encodedLength(wide));
        assertEquals(ErrorCode.DESTINATION_OVERFLOW, sizing.code());
        assertTrue(sizing.path().startsWith("Wide.rows["), sizing.path());

        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        final SbeJsonException writing = assertThrows(
            SbeJsonException.class, () -> encoder.encode(wide, buffer, 0, CAPACITY));
        assertEquals(ErrorCode.DESTINATION_OVERFLOW, writing.code());
    }

    @Test
    void trailingEmptyGroupDecodesFromAnExactFrame()
    {
        final SbeJson sbeJson = SbeJson.builder(IR).build();
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        final ObjectNode empty = F.objectNode();
        empty.putArray("rows");

        final int length = encode(sbeJson, "Wide", empty, buffer);
        assertEquals(HEADER + 4, length);
        assertEquals(65000, buffer.getShort(HEADER, LITTLE_ENDIAN) & 0xFFFF);

        final ObjectNode decoded = sbeJson.newDecoder().decodeCopy(buffer, 0, length);
        assertEquals(0, decoded.get("rows").size());

        final SbeJsonException ex = assertThrows(
            SbeJsonException.class, () -> sbeJson.newDecoder().decodeCopy(buffer, 0, length - 1));
        assertEquals(ErrorCode.FRAME_OVERFLOW, ex.code());
        assertEquals("Wide.rows", ex.path());
    }

    static ObjectNode edge()
    {
        final ObjectNode node = F.objectNode();
        node.put("bigEnum", "ONE");
        node.put("bigSet", 0);
        node.put("name16", "");
        node.putArray("items");
        node.put("text16", "");
        node.set("blob", F.binaryNode(new byte[0]));

        return node;
    }

    private static int encode(final SbeJson sbeJson, final String message, final ObjectNode node, final UnsafeBuffer dst)
    {
        final SbeJsonEncoder encoder = sbeJson.newEncoder(message);
        final int length = encoder.encode(node, dst, 0, dst.capacity());
        assertEquals(length, encoder.encodedLength(node));

        return length;
    }

    private static void assertRejects(
        final SbeJson sbeJson, final ErrorCode code, final String path, final Consumer<ObjectNode> mutation)
    {
        final ObjectNode node = edge();
        mutation.accept(node);
        final SbeJsonEncoder encoder = sbeJson.newEncoder("Edge");
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);

        final SbeJsonException ex = assertThrows(
            SbeJsonException.class, () -> encoder.encode(node, buffer, 0, CAPACITY), path);
        assertEquals(code, ex.code(), ex.getMessage());
        assertEquals(path, ex.path(), ex.getMessage());

        final SbeJsonException sizing = assertThrows(SbeJsonException.class, () -> encoder.encodedLength(node), path);
        assertEquals(code, sizing.code(), sizing.getMessage());
        assertEquals(path, sizing.path(), sizing.getMessage());
    }
}
