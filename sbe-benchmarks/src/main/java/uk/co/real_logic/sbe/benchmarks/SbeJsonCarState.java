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
package uk.co.real_logic.sbe.benchmarks;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.agrona.concurrent.UnsafeBuffer;
import org.openjdk.jmh.annotations.*;
import uk.co.real_logic.sbe.CarBenchmark;
import uk.co.real_logic.sbe.ir.Ir;
import uk.co.real_logic.sbe.jackson.SbeJson;
import uk.co.real_logic.sbe.jackson.SbeJsonDecoder;
import uk.co.real_logic.sbe.jackson.SbeJsonEncoder;
import uk.co.real_logic.sbe.json.JsonPrinter;
import uk.co.real_logic.sbe.otf.OtfHeaderDecoder;
import uk.co.real_logic.sbe.otf.OtfMessageDecoder;
import uk.co.real_logic.sbe.xml.IrGenerator;
import uk.co.real_logic.sbe.xml.ParserOptions;
import uk.co.real_logic.sbe.xml.XmlSchemaParser;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Objects;

/**
 * Shared trial data for all three axes. Setup also checks the benchmark adapters against the generated corpus.
 * All mutable codecs, output buffers and future borrowed documents belong to one benchmark thread.
 */
@State(Scope.Thread)
public class SbeJsonCarState
{
    /** Corpus size; HEAVY is bounded deliberately, not the schema's nested group maximum. */
    @Param({ "BASELINE", "HEAVY" })
    public String corpus;

    final UnsafeBuffer input = new UnsafeBuffer(ByteBuffer.allocateDirect(64 * 1024));
    final UnsafeBuffer output = new UnsafeBuffer(ByteBuffer.allocateDirect(64 * 1024));
    final ObjectMapper mapper = new ObjectMapper();
    final MessageHeaderEncoder generatedHeader = new MessageHeaderEncoder();
    final CarEncoder generatedEncoder = new CarEncoder();
    final NaiveCarTokenListener listener = new NaiveCarTokenListener();
    final StringBuilder printed = new StringBuilder(64 * 1024);
    final ByteArrayOutputStream jsonOutput = new ByteArrayOutputStream(64 * 1024);
    Ir ir;
    SbeJson sbeJson;
    SbeJsonDecoder decoder;
    SbeJsonEncoder encoder;
    OtfHeaderDecoder header;
    JsonPrinter printer;
    ObjectNode tree;
    int length;

    @Setup(Level.Trial)
    public void setup() throws Exception
    {
        try (InputStream stream = Objects.requireNonNull(
            SbeJsonCarState.class.getResourceAsStream("/car.xml"), "car.xml"))
        {
            ir = new IrGenerator().generate(XmlSchemaParser.parse(stream, ParserOptions.DEFAULT));
        }
        sbeJson = SbeJson.builder(ir).build();
        decoder = sbeJson.newDecoder();
        encoder = sbeJson.newEncoder(CarEncoder.TEMPLATE_ID);
        header = new OtfHeaderDecoder(ir.headerStructure());
        printer = new JsonPrinter(ir);

        CarBenchmark.encode(generatedHeader, generatedEncoder, input, 0);
        length = MessageHeaderEncoder.ENCODED_LENGTH + generatedEncoder.encodedLength();
        tree = decoder.decodeCopy(input, 0, length);
        if ("HEAVY".equals(corpus))
        {
            expandCorpus();
            length = encodeGenerated(input);
            tree = decoder.decodeCopy(input, 0, length);
        }

        require(tree.equals(decodeOtf()), "OTF tree differs from decodeCopy");
        require(length == encodeGenerated(output), "generated length differs");
        requireBytesEqual();
        require(length == encoder.encodedLength(tree), "sizing pass differs");
        require(length == encoder.encode(tree, output, 0, output.capacity()), "plan length differs");
        requireBytesEqual();

        printer.print(printed, input, 0);
        final ObjectNode printerTree = (ObjectNode)mapper.readTree(printed.toString());
        // JsonPrinter uses a choice object and readTree uses DoubleNode for floats. Re-encoding compares
        // the actual wire values without charging normalization to either measured decoder.
        printerTree.set("extras", tree.get("extras"));
        require(length == encoder.encode(printerTree, output, 0, output.capacity()), "printer length differs");
        requireBytesEqual();
        require(mapper.readTree(mapper.writeValueAsBytes(tree)).equals(mapper.readTree(tree.toString())),
            "byte serialization differs");
        mapper.writeValue(jsonOutput, tree);
        require(mapper.readTree(jsonOutput.toByteArray()).equals(mapper.readTree(tree.toString())),
            "stream serialization differs");
        jsonOutput.reset();
    }

    ObjectNode decodeOtf()
    {
        final int templateId = header.getTemplateId(input, 0);
        OtfMessageDecoder.decode(input, header.encodedLength(), header.getSchemaVersion(input, 0),
            header.getBlockLength(input, 0), ir.getMessage(templateId), listener);
        return listener.root();
    }

    int encodeGenerated(final UnsafeBuffer destination)
    {
        final CarEncoder car = generatedEncoder.wrapAndApplyHeader(destination, 0, generatedHeader);
        car.serialNumber(tree.get("serialNumber").longValue())
            .modelYear(tree.get("modelYear").intValue())
            .available(BooleanType.valueOf(tree.get("available").textValue()))
            .code(Model.valueOf(tree.get("code").textValue()))
            .vehicleCode(tree.get("vehicleCode").textValue());
        final JsonNode numbers = tree.get("someNumbers");
        for (int i = 0; i < numbers.size(); i++)
        {
            car.someNumbers(i, numbers.get(i).intValue());
        }
        final int extras = tree.get("extras").intValue();
        car.extras().clear().sunRoof((extras & 1) != 0).sportsPack((extras & 2) != 0)
            .cruiseControl((extras & 4) != 0);
        final JsonNode engine = tree.get("engine");
        car.engine().capacity(engine.get("capacity").intValue())
            .numCylinders((short)engine.get("numCylinders").intValue())
            .manufacturerCode(engine.get("manufacturerCode").textValue());

        final JsonNode fuel = tree.get("fuelFigures");
        final CarEncoder.FuelFiguresEncoder fuelEncoder = car.fuelFiguresCount(fuel.size());
        for (int i = 0; i < fuel.size(); i++)
        {
            final JsonNode entry = fuel.get(i);
            fuelEncoder.next().speed(entry.get("speed").intValue()).mpg(entry.get("mpg").floatValue());
        }
        final JsonNode performance = tree.get("performanceFigures");
        final CarEncoder.PerformanceFiguresEncoder performanceEncoder = car.performanceFiguresCount(performance.size());
        for (int i = 0; i < performance.size(); i++)
        {
            final JsonNode entry = performance.get(i);
            performanceEncoder.next().octaneRating((short)entry.get("octaneRating").intValue());
            final JsonNode acceleration = entry.get("acceleration");
            final CarEncoder.PerformanceFiguresEncoder.AccelerationEncoder accelerationEncoder =
                performanceEncoder.accelerationCount(acceleration.size());
            for (int j = 0; j < acceleration.size(); j++)
            {
                final JsonNode value = acceleration.get(j);
                accelerationEncoder.next().mph(value.get("mph").intValue()).seconds(value.get("seconds").floatValue());
            }
        }
        car.manufacturer(tree.get("manufacturer").textValue());
        car.model(tree.get("model").textValue());
        return MessageHeaderEncoder.ENCODED_LENGTH + car.encodedLength();
    }

    private void expandCorpus()
    {
        final ArrayNode fuel = (ArrayNode)tree.get("fuelFigures");
        final ObjectNode fuelEntry = (ObjectNode)fuel.get(0);
        fuel.removeAll();
        for (int i = 0; i < 32; i++)
        {
            fuel.add(fuelEntry.deepCopy().put("speed", 256 + i));
        }
        final ArrayNode performance = (ArrayNode)tree.get("performanceFigures");
        final ObjectNode performanceEntry = (ObjectNode)performance.get(0);
        final ArrayNode acceleration = (ArrayNode)performanceEntry.get("acceleration");
        final ObjectNode accelerationEntry = (ObjectNode)acceleration.get(0);
        acceleration.removeAll();
        for (int i = 0; i < 16; i++)
        {
            acceleration.add(accelerationEntry.deepCopy().put("mph", 256 + i));
        }
        performance.removeAll();
        for (int i = 0; i < 8; i++)
        {
            performance.add(performanceEntry.deepCopy());
        }
        tree.put("manufacturer", "Café".repeat(1024));
        tree.put("model", "Modèle".repeat(683));
    }

    private void requireBytesEqual()
    {
        for (int i = 0; i < length; i++)
        {
            require(input.getByte(i) == output.getByte(i), "wire bytes differ at " + i);
        }
    }

    private static void require(final boolean condition, final String message)
    {
        if (!condition)
        {
            throw new IllegalStateException(message);
        }
    }
}
