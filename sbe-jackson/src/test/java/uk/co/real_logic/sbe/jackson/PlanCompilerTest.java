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

import org.agrona.concurrent.UnsafeBuffer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.real_logic.sbe.PrimitiveType;
import uk.co.real_logic.sbe.ir.Ir;
import uk.co.real_logic.sbe.ir.Token;
import uk.co.real_logic.sbe.otf.OtfHeaderDecoder;
import uk.co.real_logic.sbe.otf.OtfMessageDecoder;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlanCompilerTest
{
    private static final int CAPACITY = 4096;

    @ParameterizedTest
    @ValueSource(ints = { 2, 1, 0 })
    void baselineCarPlanVisitsWhatOtfDecoderVisits(final int actingVersion)
    {
        final Ir ir = TestMessages.ir(TestMessages.BASELINE_SCHEMA);
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        TestMessages.encodeBaselineCar(buffer, 0);

        assertWalksMatch(ir, buffer, actingVersion);
    }

    @ParameterizedTest
    @ValueSource(ints = { 2, 1, 0 })
    void extensionCarPlanVisitsWhatOtfDecoderVisits(final int actingVersion)
    {
        final Ir ir = TestMessages.ir(TestMessages.EXTENSION_SCHEMA);
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        TestMessages.encodeExtensionCar(buffer, 0);

        assertWalksMatch(ir, buffer, actingVersion);
    }

    @Test
    void compositeElementsPlanVisitsWhatOtfDecoderVisits()
    {
        final Ir ir = TestMessages.ir(TestMessages.COMPOSITE_ELEMENTS_SCHEMA);
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        TestMessages.encodeCompositeElements(buffer, 0);

        assertWalksMatch(ir, buffer, 0);
    }

    @Test
    void groupWithDataPlanVisitsWhatOtfDecoderVisits()
    {
        final Ir ir = TestMessages.ir(TestMessages.GROUP_WITH_DATA_SCHEMA);
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        TestMessages.encodeGroupWithData(buffer, 0);

        assertWalksMatch(ir, buffer, 0);
    }

    @Test
    void nestedGroupsPlanVisitsWhatOtfDecoderVisits()
    {
        final Ir ir = TestMessages.ir(TestMessages.NESTED_GROUP_SCHEMA);
        final UnsafeBuffer buffer = TestMessages.newBuffer(CAPACITY);
        TestMessages.encodeNestedGroups(buffer, 0);

        final List<String> events = assertWalksMatch(ir, buffer, 0);
        assertTrue(events.contains("encoding d@18 len=1"), events.toString());
        assertTrue(events.contains("encoding b@19 len=1"), events.toString());
    }

    @Test
    void shouldCompileBaselineCarShape()
    {
        final Ir ir = TestMessages.ir(TestMessages.BASELINE_SCHEMA);
        final MessagePlan plan = PlanCompiler.compile(ir, ir.getMessage(1));

        assertEquals(1, plan.templateId);
        assertEquals("Car", plan.name);
        assertEquals(1, plan.schemaId);
        assertEquals(2, plan.schemaVersion);
        assertEquals(ir.getMessage(1).get(0).encodedLength(), plan.blockLength);
        assertEquals(2, plan.maxGroupDepth);
        assertEquals(0, plan.rootStart);

        final List<String> rootNames = new ArrayList<>();
        for (int i = plan.rootStart; i < plan.rootEnd; i++)
        {
            rootNames.add(plan.fields[i].name);
        }
        assertEquals(
            List.of(
                "serialNumber", "modelYear", "available", "code", "someNumbers", "vehicleCode", "extras", "engine",
                "uuid", "cupHolderCount", "fuelFigures", "performanceFigures",
                "manufacturer", "model", "activationCode"),
            rootNames);

        final FieldPlan serialNumber = field(plan, "serialNumber");
        assertEquals(FieldPlan.KIND_UINT64, serialNumber.kind);
        assertEquals(0, serialNumber.offset);
        assertEquals(8, serialNumber.encodedLength);
        assertFalse(serialNumber.optional);

        final FieldPlan modelYear = field(plan, "modelYear");
        assertEquals(FieldPlan.KIND_INT, modelYear.kind);
        assertEquals(8, modelYear.offset);
        assertEquals(PrimitiveType.UINT16, modelYear.primitiveType);
        assertEquals(65534, modelYear.maxValueLong);

        final FieldPlan available = field(plan, "available");
        assertEquals(FieldPlan.KIND_ENUM, available.kind);
        assertEquals(10, available.offset);
        assertArrayEquals(new long[]{ 0, 1 }, available.enumValues);
        assertArrayEquals(new String[]{ "F", "T" }, available.enumNames);
        assertEquals(1, available.enumNameToIndex.getValue("T"));
        assertEquals(-1, available.enumNameToIndex.getValue("X"));
        assertEquals(255, available.nullValueLong);

        final FieldPlan code = field(plan, "code");
        assertEquals(PrimitiveType.CHAR, code.primitiveType);
        assertEquals('A', code.enumValues[0]);
        assertEquals(0, code.enumIndexOf('A'));
        assertEquals(2, code.enumIndexOf('C'));
        assertEquals(-1, code.enumIndexOf('D'));

        final FieldPlan someNumbers = field(plan, "someNumbers");
        assertEquals(FieldPlan.KIND_NUMERIC_ARRAY, someNumbers.kind);
        assertEquals(5, someNumbers.arrayLength);
        assertEquals(20, someNumbers.encodedLength);
        assertEquals(12, someNumbers.offset);

        final FieldPlan vehicleCode = field(plan, "vehicleCode");
        assertEquals(FieldPlan.KIND_CHAR_ARRAY, vehicleCode.kind);
        assertEquals(6, vehicleCode.arrayLength);
        assertEquals(FieldPlan.ENC_ASCII, vehicleCode.characterEncodingTag);

        final FieldPlan extras = field(plan, "extras");
        assertEquals(FieldPlan.KIND_BIT_SET, extras.kind);
        assertArrayEquals(new int[]{ 0, 1, 2 }, extras.choiceBits);
        assertArrayEquals(new String[]{ "sunRoof", "sportsPack", "cruiseControl" }, extras.choiceNames);
        assertEquals(7, extras.knownMask);
        assertEquals(0xFF, extras.maxValueLong);

        final FieldPlan uuid = field(plan, "uuid");
        assertEquals(2, uuid.sinceVersion);
        assertTrue(uuid.optional);
        assertEquals(2, uuid.arrayLength);
        assertEquals(Long.MIN_VALUE, uuid.nullValueLong);
    }

    @Test
    void shouldCompileCompositeMembersWithAbsoluteOffsetsInScope()
    {
        final Ir ir = TestMessages.ir(TestMessages.BASELINE_SCHEMA);
        final MessagePlan plan = PlanCompiler.compile(ir, ir.getMessage(1));

        final FieldPlan engine = field(plan, "engine");
        assertEquals(FieldPlan.KIND_COMPOSITE, engine.kind);
        assertEquals(39, engine.offset);
        assertEquals(6, engine.encodedLength);
        assertEquals(5, engine.childEnd - engine.childStart);

        final FieldPlan capacity = plan.fields[engine.childStart];
        assertEquals("capacity", capacity.name);
        assertEquals(39, capacity.offset);
        final FieldPlan numCylinders = plan.fields[engine.childStart + 1];
        assertEquals("numCylinders", numCylinders.name);
        assertEquals(41, numCylinders.offset);
        final FieldPlan maxRpm = plan.fields[engine.childStart + 2];
        assertEquals("maxRpm", maxRpm.name);
        assertTrue(maxRpm.constant);
        assertEquals(FieldPlan.KIND_INT, maxRpm.kind);
        assertEquals(9000, maxRpm.constLong);
        final FieldPlan manufacturerCode = plan.fields[engine.childStart + 3];
        assertEquals("manufacturerCode", manufacturerCode.name);
        assertEquals(42, manufacturerCode.offset);
        assertEquals(FieldPlan.KIND_CHAR_ARRAY, manufacturerCode.kind);
        assertEquals(3, manufacturerCode.arrayLength);
        final FieldPlan fuel = plan.fields[engine.childStart + 4];
        assertEquals("fuel", fuel.name);
        assertTrue(fuel.constant);
        assertEquals(FieldPlan.KIND_CHAR_ARRAY, fuel.kind);
        assertEquals("Petrol", fuel.constString);
    }

    @Test
    void shouldCompileGroupAndVarDataLayouts()
    {
        final Ir ir = TestMessages.ir(TestMessages.BASELINE_SCHEMA);
        final MessagePlan plan = PlanCompiler.compile(ir, ir.getMessage(1));

        final FieldPlan fuelFigures = field(plan, "fuelFigures");
        assertEquals(FieldPlan.KIND_GROUP, fuelFigures.kind);
        assertEquals(6, fuelFigures.blockLength);
        assertEquals(3, fuelFigures.dimensionSize);
        assertEquals(PrimitiveType.UINT16, fuelFigures.blockLengthType);
        assertEquals(0, fuelFigures.blockLengthOffset);
        assertEquals(PrimitiveType.UINT8, fuelFigures.numInGroupType);
        assertEquals(2, fuelFigures.numInGroupOffset);
        assertEquals(0, fuelFigures.numInGroupMin);
        assertEquals(254, fuelFigures.numInGroupMax);
        assertEquals(2, fuelFigures.childEnd - fuelFigures.childStart);
        assertEquals("speed", plan.fields[fuelFigures.childStart].name);
        assertEquals(0, plan.fields[fuelFigures.childStart].offset);
        assertEquals("mpg", plan.fields[fuelFigures.childStart + 1].name);
        assertEquals(2, plan.fields[fuelFigures.childStart + 1].offset);
        assertEquals(FieldPlan.KIND_FLOAT, plan.fields[fuelFigures.childStart + 1].kind);

        final FieldPlan performanceFigures = field(plan, "performanceFigures");
        final FieldPlan acceleration = plan.fields[performanceFigures.childStart + 1];
        assertEquals("acceleration", acceleration.name);
        assertEquals(FieldPlan.KIND_GROUP, acceleration.kind);
        assertEquals(6, acceleration.blockLength);

        final FieldPlan manufacturer = field(plan, "manufacturer");
        assertEquals(FieldPlan.KIND_VAR_DATA, manufacturer.kind);
        assertEquals(PrimitiveType.UINT8, manufacturer.lengthType);
        assertEquals(0, manufacturer.lengthOffset);
        assertEquals(254, manufacturer.lengthMax);
        assertEquals(1, manufacturer.dataOffset);
        assertEquals(FieldPlan.ENC_UTF8, manufacturer.characterEncodingTag);

        final MessagePlan credentials = PlanCompiler.compile(ir, ir.getMessage(2));
        final FieldPlan encryptedPassword = field(credentials, "encryptedPassword");
        assertEquals(FieldPlan.ENC_BINARY, encryptedPassword.characterEncodingTag);
        assertEquals(PrimitiveType.UINT32, encryptedPassword.lengthType);
        assertEquals(1073741824L, encryptedPassword.lengthMax);
        assertEquals(4, encryptedPassword.dataOffset);
    }

    @Test
    void shouldCompileEffectiveLayoutTable()
    {
        final Ir baseline = TestMessages.ir(TestMessages.BASELINE_SCHEMA);
        final MessagePlan car = PlanCompiler.compile(baseline, baseline.getMessage(1));
        assertArrayEquals(new int[]{ 0, 2 }, car.versionThresholds);
        assertEquals(3, car.layoutCount());
        assertEquals(1, car.layoutIndex(0));
        assertEquals(1, car.layoutIndex(1));
        assertEquals(2, car.layoutIndex(2));
        assertEquals(2, car.layoutIndex(99));

        final Ir extension = TestMessages.ir(TestMessages.EXTENSION_SCHEMA);
        final MessagePlan extended = PlanCompiler.compile(extension, extension.getMessage(1));
        assertArrayEquals(new int[]{ 0, 1, 2 }, extended.versionThresholds);
        assertEquals(4, extended.layoutCount());
        assertEquals(2, extended.layoutIndex(1));
    }

    @Test
    void shouldCompileConstantEnumAndNestedCompositeInExtensionSchema()
    {
        final Ir ir = TestMessages.ir(TestMessages.EXTENSION_SCHEMA);
        final MessagePlan plan = PlanCompiler.compile(ir, ir.getMessage(1));

        final FieldPlan discountedModel = field(plan, "discountedModel");
        assertEquals(FieldPlan.KIND_ENUM, discountedModel.kind);
        assertTrue(discountedModel.constant);
        assertEquals("C", discountedModel.constString);
        assertEquals('C', discountedModel.constLong);

        final FieldPlan engine = field(plan, "engine");
        final FieldPlan booster = plan.fields[engine.childEnd - 1];
        assertEquals("booster", booster.name);
        assertEquals(FieldPlan.KIND_COMPOSITE, booster.kind);
        assertEquals(engine.offset + 8, booster.offset);
        final FieldPlan boostType = plan.fields[booster.childStart];
        assertEquals("BoostType", boostType.name);
        assertEquals(FieldPlan.KIND_ENUM, boostType.kind);
        assertEquals(booster.offset, boostType.offset);
        final FieldPlan horsePower = plan.fields[booster.childStart + 1];
        assertEquals("horsePower", horsePower.name);
        assertEquals(booster.offset + 1, horsePower.offset);

        final FieldPlan boosterEnabled = plan.fields[engine.childEnd - 2];
        assertEquals("boosterEnabled", boosterEnabled.name);
        assertEquals(FieldPlan.KIND_ENUM, boosterEnabled.kind);

        final FieldPlan fuelFigures = field(plan, "fuelFigures");
        final FieldPlan mpg = plan.fields[fuelFigures.childStart + 1];
        assertEquals("mpg", mpg.name);
        assertEquals(2, mpg.sinceVersion);
        final FieldPlan usageDescription = plan.fields[fuelFigures.childStart + 2];
        assertEquals("usageDescription", usageDescription.name);
        assertEquals(FieldPlan.KIND_VAR_DATA, usageDescription.kind);
        assertEquals(FieldPlan.ENC_ASCII, usageDescription.characterEncodingTag);
    }

    @Test
    void shouldInternFieldNames()
    {
        final Ir ir = TestMessages.ir(TestMessages.BASELINE_SCHEMA);
        final MessagePlan plan = PlanCompiler.compile(ir, ir.getMessage(1));
        assertSame("serialNumber", field(plan, "serialNumber").name);
        assertSame("Car", plan.name);
    }

    private static List<String> assertWalksMatch(final Ir ir, final UnsafeBuffer buffer, final int actingVersion)
    {
        final OtfHeaderDecoder headerDecoder = new OtfHeaderDecoder(ir.headerStructure());
        final int templateId = headerDecoder.getTemplateId(buffer, 0);
        final int blockLength = headerDecoder.getBlockLength(buffer, 0);
        final int bodyOffset = headerDecoder.encodedLength();
        final List<Token> tokens = ir.getMessage(templateId);

        final SpyTokenListener spy = new SpyTokenListener(actingVersion);
        OtfMessageDecoder.decode(buffer, bodyOffset, actingVersion, blockLength, tokens, spy);

        final MessagePlan plan = PlanCompiler.compile(ir, tokens);
        final List<String> planEvents = PlanWalker.walk(plan, buffer, bodyOffset, blockLength, actingVersion);

        assertEquals(spy.events(), planEvents);
        assertTrue(planEvents.size() > 2);

        return planEvents;
    }

    private static FieldPlan field(final MessagePlan plan, final String name)
    {
        for (int i = plan.rootStart; i < plan.rootEnd; i++)
        {
            if (plan.fields[i].name.equals(name))
            {
                return plan.fields[i];
            }
        }

        throw new IllegalArgumentException("no root field " + name);
    }
}
