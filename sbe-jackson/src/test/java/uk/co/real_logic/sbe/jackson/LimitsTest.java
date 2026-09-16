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

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.jupiter.api.Test;
import uk.co.real_logic.sbe.ir.Ir;

import java.nio.ByteOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LimitsTest
{
    private static final int CAPACITY = 4096;
    private static final int HEADER_LENGTH = 8;
    private static final int CAR_BLOCK_LENGTH = 62;

    @Test
    void hostileNestedNumInGroupStopsAtMaxGroupEntries()
    {
        final Ir ir = TestMessages.ir(TestMessages.NESTED_GROUP_SCHEMA);
        final SbeJson sbeJson = SbeJson.builder(ir).limits(Limits.builder().maxGroupEntries(300).build()).build();
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        TestMessages.encodeNestedGroups(buffer, 0);
        buffer.putByte(HEADER_LENGTH + 16 + 1, (byte)200);
        buffer.putByte(HEADER_LENGTH + 16 + 4, (byte)200);

        final SbeJsonException ex = assertThrows(
            SbeJsonException.class, () -> sbeJson.newDecoder().decodeCopy(buffer, 0, CAPACITY));

        assertEquals(ErrorCode.LIMIT_EXCEEDED, ex.code());
        assertEquals("Top.x[0].y", ex.path());
        assertTrue(ex.getMessage().contains("maxGroupEntries 300"), ex.getMessage());
    }

    @Test
    void schemaDeeperThanMaxDepthIsRejectedAtBuildTime()
    {
        final Ir ir = TestMessages.ir(TestMessages.NESTED_GROUP_SCHEMA);
        final SbeJsonException ex = assertThrows(
            SbeJsonException.class,
            () -> SbeJson.builder(ir).limits(Limits.builder().maxDepth(2).build()).build());
        assertEquals(ErrorCode.LIMIT_EXCEEDED, ex.code());

        assertNotNull(SbeJson.builder(ir).limits(Limits.builder().maxDepth(3).build()).build());
    }

    @Test
    void varDataOverMaxVarDataBytesIsRejected()
    {
        final Ir ir = TestMessages.ir(TestMessages.BASELINE_SCHEMA);
        final SbeJson sbeJson = SbeJson.builder(ir).limits(Limits.builder().maxVarDataBytes(10).build()).build();
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        final int length = TestMessages.encodeBaselineCar(buffer, 0);

        final SbeJsonException ex = assertThrows(
            SbeJsonException.class, () -> sbeJson.newDecoder().decodeCopy(buffer, 0, length));

        assertEquals(ErrorCode.LIMIT_EXCEEDED, ex.code());
        assertEquals("Car.model", ex.path());
    }

    @Test
    void frameShorterThanMessageIsRejectedEvenThoughBufferHoldsIt()
    {
        final Ir ir = TestMessages.ir(TestMessages.BASELINE_SCHEMA);
        final SbeJson sbeJson = SbeJson.builder(ir).build();
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        final int length = TestMessages.encodeBaselineCar(buffer, 0);

        final SbeJsonException tail = assertThrows(
            SbeJsonException.class, () -> sbeJson.newDecoder().decodeCopy(buffer, 0, length - 3));
        assertEquals(ErrorCode.FRAME_OVERFLOW, tail.code());
        assertEquals("Car.activationCode", tail.path());

        final SbeJsonException block = assertThrows(
            SbeJsonException.class, () -> sbeJson.newDecoder().decodeCopy(buffer, 0, HEADER_LENGTH + 10));
        assertEquals(ErrorCode.FRAME_OVERFLOW, block.code());

        final SbeJsonException header = assertThrows(
            SbeJsonException.class, () -> sbeJson.newDecoder().decodeCopy(buffer, 0, HEADER_LENGTH - 1));
        assertEquals(ErrorCode.FRAME_OVERFLOW, header.code());

        final SbeJsonException capacity = assertThrows(
            SbeJsonException.class, () -> sbeJson.newDecoder().decodeCopy(buffer, CAPACITY - 4, length));
        assertEquals(ErrorCode.FRAME_OVERFLOW, capacity.code());
    }

    @Test
    void numInGroupAtNullSentinelIsOutOfRange()
    {
        final Ir ir = TestMessages.ir(TestMessages.BASELINE_SCHEMA);
        final SbeJson sbeJson = SbeJson.builder(ir).build();
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        final int length = TestMessages.encodeBaselineCar(buffer, 0);
        buffer.putByte(HEADER_LENGTH + CAR_BLOCK_LENGTH + 2, (byte)0xFF);

        final SbeJsonException ex = assertThrows(
            SbeJsonException.class, () -> sbeJson.newDecoder().decodeCopy(buffer, 0, length));

        assertEquals(ErrorCode.OUT_OF_RANGE, ex.code());
        assertEquals("Car.fuelFigures", ex.path());
    }

    @Test
    void varDataLengthPastFrameIsFrameOverflow()
    {
        final Ir ir = TestMessages.ir(TestMessages.BASELINE_SCHEMA);
        final SbeJson sbeJson = SbeJson.builder(ir).build();
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        final int length = TestMessages.encodeBaselineCar(buffer, 0);
        final int manufacturerLengthOffset = length - 5 - 1 - 9 - 1 - 5 - 1;
        assertEquals(5, buffer.getByte(manufacturerLengthOffset));
        buffer.putByte(manufacturerLengthOffset, (byte)200);

        final SbeJsonException ex = assertThrows(
            SbeJsonException.class, () -> sbeJson.newDecoder().decodeCopy(buffer, 0, length));

        assertEquals(ErrorCode.FRAME_OVERFLOW, ex.code());
        assertEquals("Car.manufacturer", ex.path());
    }

    @Test
    void groupBlockLengthPastFrameIsFrameOverflow()
    {
        final Ir ir = TestMessages.ir(TestMessages.BASELINE_SCHEMA);
        final SbeJson sbeJson = SbeJson.builder(ir).build();
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        final int length = TestMessages.encodeBaselineCar(buffer, 0);
        buffer.putShort(HEADER_LENGTH + CAR_BLOCK_LENGTH, (short)0x7FFF, ByteOrder.LITTLE_ENDIAN);

        final SbeJsonException ex = assertThrows(
            SbeJsonException.class, () -> sbeJson.newDecoder().decodeCopy(buffer, 0, length));

        assertEquals(ErrorCode.FRAME_OVERFLOW, ex.code());
        assertTrue(ex.path().startsWith("Car.fuelFigures"), ex.path());
    }

    @Test
    void unknownTemplateAndForeignSchemaIdAreRejected()
    {
        final Ir ir = TestMessages.ir(TestMessages.BASELINE_SCHEMA);
        final SbeJson sbeJson = SbeJson.builder(ir).build();
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        final int length = TestMessages.encodeBaselineCar(buffer, 0);

        buffer.putShort(2, (short)99, ByteOrder.LITTLE_ENDIAN);
        final SbeJsonException template = assertThrows(
            SbeJsonException.class, () -> sbeJson.newDecoder().decodeCopy(buffer, 0, length));
        assertEquals(ErrorCode.UNKNOWN_TEMPLATE, template.code());
        assertEquals(99, template.templateId());

        buffer.putShort(2, (short)1, ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(4, (short)5, ByteOrder.LITTLE_ENDIAN);
        final SbeJsonException schema = assertThrows(
            SbeJsonException.class, () -> sbeJson.newDecoder().decodeCopy(buffer, 0, length));
        assertEquals(ErrorCode.UNKNOWN_TEMPLATE, schema.code());
        assertTrue(schema.getMessage().contains("schema id 5"), schema.getMessage());
    }

    @Test
    void encodeEnforcesGroupEntryAndVarDataBudgets()
    {
        final Ir ir = TestMessages.ir(TestMessages.BASELINE_SCHEMA);
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        final int length = TestMessages.encodeBaselineCar(buffer, 0);
        final ObjectNode car = SbeJson.builder(ir).build().newDecoder().decodeCopy(buffer, 0, length);

        final SbeJson groups = SbeJson.builder(ir).limits(Limits.builder().maxGroupEntries(5).build()).build();
        final SbeJsonException groupEx = assertThrows(
            SbeJsonException.class, () -> groups.newEncoder("Car").encode(car, buffer, 0, CAPACITY));
        assertEquals(ErrorCode.LIMIT_EXCEEDED, groupEx.code());
        assertEquals("Car.performanceFigures[0].acceleration", groupEx.path());

        final SbeJson varData = SbeJson.builder(ir).limits(Limits.builder().maxVarDataBytes(13).build()).build();
        final SbeJsonException varDataEx = assertThrows(
            SbeJsonException.class, () -> varData.newEncoder("Car").encode(car, buffer, 0, CAPACITY));
        assertEquals(ErrorCode.LIMIT_EXCEEDED, varDataEx.code());
        assertEquals("Car.model", varDataEx.path());

        final SbeJsonException sizingEx = assertThrows(
            SbeJsonException.class, () -> varData.newEncoder("Car").encodedLength(car));
        assertEquals(ErrorCode.LIMIT_EXCEEDED, sizingEx.code());
    }
}
