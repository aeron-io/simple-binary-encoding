/**
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

import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import uk.co.real_logic.sbe.ir.Ir;
import uk.co.real_logic.sbe.xml.IrGenerator;
import uk.co.real_logic.sbe.xml.MessageSchema;
import uk.co.real_logic.sbe.xml.ParserOptions;
import uk.co.real_logic.sbe.xml.XmlSchemaParser;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Schema loading and sample message encoding shared by the tests. Sample messages are produced by the codecs
 * generated from the sbe-tool test schemas so that they are an independent oracle for this module.
 */
final class TestMessages
{
    static final String BASELINE_SCHEMA = "json-printer-test-schema.xml";
    static final String EXTENSION_SCHEMA = "example-extension-schema.xml";
    static final String COMPOSITE_ELEMENTS_SCHEMA = "composite-elements-schema.xml";
    static final String GROUP_WITH_DATA_SCHEMA = "group-with-data-schema.xml";
    static final String NESTED_GROUP_SCHEMA = "nested-group-schema.xml";
    static final String VERSIONED_V1_SCHEMA = "versioned-message-v1.xml";
    static final String VERSIONED_V2_SCHEMA = "versioned-message-v2.xml";
    static final String EDGE_SCHEMA = "edge-cases-schema.xml";

    private TestMessages()
    {
    }

    static Ir ir(final String resourceName)
    {
        try (InputStream in = TestMessages.class.getClassLoader().getResourceAsStream(resourceName))
        {
            if (null == in)
            {
                throw new IllegalArgumentException("resource not found: " + resourceName);
            }
            final MessageSchema schema = XmlSchemaParser.parse(in, ParserOptions.DEFAULT);
            return new IrGenerator().generate(schema);
        }
        catch (final Exception ex)
        {
            throw new RuntimeException(ex);
        }
    }

    static UnsafeBuffer newBuffer(final int capacity)
    {
        return new UnsafeBuffer(new byte[capacity]);
    }

    /*
     * The message from {@code EncodedCarTestBase} in sbe-tool, encoded with the generated {@code baseline} codecs.
     *
     * @return total encoded length including the header.
     */
    static int encodeBaselineCar(final MutableDirectBuffer buffer, final int offset)
    {
        final baseline.MessageHeaderEncoder header = new baseline.MessageHeaderEncoder();
        final baseline.CarEncoder car = new baseline.CarEncoder();

        car.wrapAndApplyHeader(buffer, offset, header)
            .serialNumber(1234)
            .modelYear(2013)
            .available(baseline.BooleanType.T)
            .code(baseline.Model.A)
            .putVehicleCode("ab\"def".getBytes(StandardCharsets.US_ASCII), 0);

        for (int i = 0, size = baseline.CarEncoder.someNumbersLength(); i < size; i++)
        {
            car.someNumbers(i, i);
        }

        car.extras()
            .clear()
            .cruiseControl(true)
            .sportsPack(true)
            .sunRoof(false);

        car.engine()
            .capacity(2000)
            .numCylinders((short)4)
            .putManufacturerCode("123".getBytes(StandardCharsets.US_ASCII), 0);

        car.putUuid(7L, 3L)
            .cupHolderCount((byte)5);

        car.fuelFiguresCount(3)
            .next().speed(30).mpg(35.9f)
            .next().speed(55).mpg(49.0f)
            .next().speed(75).mpg(40.0f);

        final baseline.CarEncoder.PerformanceFiguresEncoder perfFigures = car.performanceFiguresCount(2);
        perfFigures.next()
            .octaneRating((short)95)
            .accelerationCount(3)
            .next().mph(30).seconds(4.0f)
            .next().mph(60).seconds(7.5f)
            .next().mph(100).seconds(12.2f);
        perfFigures.next()
            .octaneRating((short)99)
            .accelerationCount(3)
            .next().mph(30).seconds(3.8f)
            .next().mph(60).seconds(7.1f)
            .next().mph(100).seconds(11.8f);

        car.manufacturer("Honda");
        car.model("Civic VTi");
        car.activationCode("315\\8");

        return header.encodedLength() + car.encodedLength();
    }

    /*
     * A message from the {@code extension} schema (nested composites, constant enum, var-data inside a group,
     * two var-data encodings), encoded with the generated {@code extension} codecs.
     */
    static int encodeExtensionCar(final MutableDirectBuffer buffer, final int offset)
    {
        final extension.MessageHeaderEncoder header = new extension.MessageHeaderEncoder();
        final extension.CarEncoder car = new extension.CarEncoder();

        car.wrapAndApplyHeader(buffer, offset, header)
            .serialNumber(1234)
            .modelYear(2013)
            .available(extension.BooleanType.T)
            .code(extension.Model.A)
            .putSomeNumbers(1, 2, 3, 4)
            .vehicleCode("abcdef");

        car.extras()
            .clear()
            .cruiseControl(true)
            .sportsPack(true)
            .sunRoof(false);

        final extension.EngineEncoder engine = car.engine();
        engine.capacity(2000)
            .numCylinders((short)4)
            .manufacturerCode("123")
            .efficiency((byte)35)
            .boosterEnabled(extension.BooleanType.T)
            .booster().boostType(extension.BoostType.NITROUS).horsePower((short)200);

        car.putUuid(7L, 3L)
            .cupHolderCount((short)5);

        car.fuelFiguresCount(2)
            .next().speed(30).mpg(35.9f).usageDescription("Urban Cycle")
            .next().speed(55).mpg(49.0f).usageDescription("Combined Cycle");

        final extension.CarEncoder.PerformanceFiguresEncoder perfFigures = car.performanceFiguresCount(2);
        perfFigures.next()
            .octaneRating((short)95)
            .accelerationCount(2)
            .next().mph(30).seconds(4.0f)
            .next().mph(60).seconds(7.5f);
        perfFigures.next()
            .octaneRating((short)99)
            .accelerationCount(0);

        car.manufacturer("Honda");
        car.model("Civic VTi éè 🚗");
        car.activationCode("abcdef");

        return header.encodedLength() + car.encodedLength();
    }

    /*
     * Composite with an enum, a set and a nested composite as members.
     */
    static int encodeCompositeElements(final MutableDirectBuffer buffer, final int offset)
    {
        final composite.elements.MessageHeaderEncoder header = new composite.elements.MessageHeaderEncoder();
        final composite.elements.MsgEncoder msg = new composite.elements.MsgEncoder();

        final composite.elements.OuterEncoder outer = msg.wrapAndApplyHeader(buffer, offset, header).structure();
        outer.enumOne(composite.elements.EnumOne.Value10);
        outer.zeroth((short)42);
        outer.setOne().clear().bit0(true).bit26(true);
        outer.inner().first(101L).second(-202L);

        return header.encodedLength() + msg.encodedLength();
    }

    /*
     * Group containing a nested group containing var-data, plus var-data at the outer group level.
     */
    static int encodeGroupWithData(final MutableDirectBuffer buffer, final int offset)
    {
        final group.with.data.MessageHeaderEncoder header = new group.with.data.MessageHeaderEncoder();
        final group.with.data.TestMessage3Encoder msg = new group.with.data.TestMessage3Encoder();

        msg.wrapAndApplyHeader(buffer, offset, header).tag1(99);
        final group.with.data.TestMessage3Encoder.EntriesEncoder entries = msg.entriesCount(2);
        entries.next().tagGroup1("ABCDEFGHI");
        entries.nestedEntriesCount(2)
            .next().tagGroup2(1L).varDataFieldNested("nested one")
            .next().tagGroup2(2L).varDataFieldNested("");
        entries.varDataField("outer one");

        entries.next().tagGroup1("JKLMNOPQR");
        entries.nestedEntriesCount(0);
        entries.varDataField("outer two");

        return header.encodedLength() + msg.encodedLength();
    }

    /*
     * Hand-encoded message for {@code nested-group-schema.xml} (three group levels, all uint8, root block length
     * 16 as declared by the schema; the schema has no legal Java package so there are no generated codecs).
     * <pre>
     * a=7
     * x[2]: {b=1, y[1]: {c=11, z[2]: {d=21}, {d=22}}}, {b=2, y[0]}
     * </pre>
     */
    static int encodeNestedGroups(final MutableDirectBuffer buffer, final int offset)
    {
        int pos = offset;
        buffer.putShort(pos, (short)16);
        pos += 2;
        buffer.putShort(pos, (short)1);
        pos += 2;
        buffer.putShort(pos, (short)2);
        pos += 2;
        buffer.putShort(pos, (short)0);
        pos += 2;

        buffer.putByte(pos, (byte)7);
        pos += 16;
        buffer.putByte(pos++, (byte)1);
        buffer.putByte(pos++, (byte)2);

        buffer.putByte(pos++, (byte)1);
        buffer.putByte(pos++, (byte)1);
        buffer.putByte(pos++, (byte)1);
        buffer.putByte(pos++, (byte)11);
        buffer.putByte(pos++, (byte)1);
        buffer.putByte(pos++, (byte)2);
        buffer.putByte(pos++, (byte)21);
        buffer.putByte(pos++, (byte)22);

        buffer.putByte(pos++, (byte)2);
        buffer.putByte(pos++, (byte)1);
        buffer.putByte(pos++, (byte)0);

        return pos - offset;
    }
}
