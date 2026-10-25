/*
 * Copyright 2013-2026 Real Logic Limited.
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
package uk.co.real_logic.sbe.json;

import baseline.CarEncoder;
import baseline.CredentialsEncoder;
import baseline.MessageHeaderEncoder;
import org.agrona.concurrent.UnsafeBuffer;
import org.junit.jupiter.api.Test;
import uk.co.real_logic.sbe.EncodedCarTestBase;
import uk.co.real_logic.sbe.Tests;
import uk.co.real_logic.sbe.ir.Ir;
import uk.co.real_logic.sbe.ir.IrDecoder;
import uk.co.real_logic.sbe.ir.IrEncoder;
import uk.co.real_logic.sbe.ir.Signal;
import uk.co.real_logic.sbe.ir.Token;
import uk.co.real_logic.sbe.xml.IrGenerator;
import uk.co.real_logic.sbe.xml.MessageSchema;
import uk.co.real_logic.sbe.xml.ParserOptions;
import uk.co.real_logic.sbe.xml.XmlSchemaParser;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonPrinterTest extends EncodedCarTestBase
{
    private static final int SCHEMA_BUFFER_CAPACITY = 16 * 1024;
    private static final int MSG_BUFFER_CAPACITY = 4 * 1024;

    @Test
    void exampleMessagePrintedAsJson() throws Exception
    {
        final ByteBuffer encodedSchemaBuffer = ByteBuffer.allocate(SCHEMA_BUFFER_CAPACITY);
        encodeSchema(encodedSchemaBuffer);

        final ByteBuffer encodedMsgBuffer = ByteBuffer.allocate(MSG_BUFFER_CAPACITY);
        encodeTestMessage(encodedMsgBuffer);

        encodedSchemaBuffer.flip();
        final Ir ir = decodeIr(encodedSchemaBuffer);

        final JsonPrinter printer = new JsonPrinter(ir);
        final String result = printer.print(encodedMsgBuffer);
        assertEquals(
            "{\n" +
            "    \"serialNumber\": 1234,\n" +
            "    \"modelYear\": 2013,\n" +
            "    \"available\": \"T\",\n" +
            "    \"code\": \"A\",\n" +
            "    \"someNumbers\": [0, 1, 2, 3, 4],\n" +
            "    \"vehicleCode\": \"ab=5\\n\\u0001\",\n" +
            "    \"extras\": { \"sunRoof\": false, \"sportsPack\": true, \"cruiseControl\": true },\n" +
            "    \"engine\": \n" +
            "    {\n" +
            "        \"capacity\": 2000,\n" +
            "        \"numCylinders\": 4,\n" +
            "        \"maxRpm\": 9000,\n" +
            "        \"manufacturerCode\": \"$\",\n" +
            "        \"fuel\": \"Petrol\"\n" +
            "    },\n" +
            "    \"uuid\": [7, 3],\n" +
            "    \"cupHolderCount\": 5,\n" +
            "    \"fuelFigures\": [\n" +
            "    {\n" +
            "        \"speed\": 30,\n" +
            "        \"mpg\": 35.9\n" +
            "    },\n" +
            "    {\n" +
            "        \"speed\": 55,\n" +
            "        \"mpg\": 49.0\n" +
            "    },\n" +
            "    {\n" +
            "        \"speed\": 75,\n" +
            "        \"mpg\": 40.0\n" +
            "    }],\n" +
            "    \"performanceFigures\": [\n" +
            "    {\n" +
            "        \"octaneRating\": 95,\n" +
            "        \"acceleration\": [\n" +
            "        {\n" +
            "            \"mph\": 30,\n" +
            "            \"seconds\": 4.0\n" +
            "        },\n" +
            "        {\n" +
            "            \"mph\": 60,\n" +
            "            \"seconds\": 7.5\n" +
            "        },\n" +
            "        {\n" +
            "            \"mph\": 100,\n" +
            "            \"seconds\": 12.2\n" +
            "        }]\n" +
            "    },\n" +
            "    {\n" +
            "        \"octaneRating\": 99,\n" +
            "        \"acceleration\": [\n" +
            "        {\n" +
            "            \"mph\": 30,\n" +
            "            \"seconds\": 3.8\n" +
            "        },\n" +
            "        {\n" +
            "            \"mph\": 60,\n" +
            "            \"seconds\": 7.1\n" +
            "        },\n" +
            "        {\n" +
            "            \"mph\": 100,\n" +
            "            \"seconds\": 11.8\n" +
            "        }]\n" +
            "    }],\n" +
            "    \"manufacturer\": \"Honda™\\uD83D\\uDE00\",\n" +
            "    \"model\": \"Civic VTi\",\n" +
            "    \"activationCode\": \"315\\\\8\"\n" +
            "}",
            result);
    }

    @Test
    public void exampleVarData() throws Exception
    {
        final ByteBuffer encodedSchemaBuffer = ByteBuffer.allocate(SCHEMA_BUFFER_CAPACITY);
        encodeSchema(encodedSchemaBuffer);

        final ByteBuffer encodedMsgBuffer = ByteBuffer.allocate(MSG_BUFFER_CAPACITY);
        final UnsafeBuffer buffer = new UnsafeBuffer(encodedMsgBuffer);
        final CredentialsEncoder encoder = new CredentialsEncoder();
        encoder.wrapAndApplyHeader(buffer, 0, new MessageHeaderEncoder());
        encoder.login("example");
        encoder.putEncryptedPassword(new byte[] {11, 0, 64, 97}, 0, 4);
        encodedMsgBuffer.position(encoder.encodedLength());

        encodedSchemaBuffer.flip();
        final Ir ir = decodeIr(encodedSchemaBuffer);

        final JsonPrinter printer = new JsonPrinter(ir);
        final String result = printer.print(encodedMsgBuffer);
        assertEquals(
            "{\n" +
            "    \"login\": \"example\",\n" +
            "    \"encryptedPassword\": \"0b004061\"\n" +
            "}",
            result);
    }

    @Test
    void shouldRejectVarDataLengthBeyondBuffer() throws Exception
    {
        final ByteBuffer encodedSchemaBuffer = ByteBuffer.allocate(SCHEMA_BUFFER_CAPACITY);
        encodeSchema(encodedSchemaBuffer);

        final ByteBuffer scratch = ByteBuffer.allocate(MSG_BUFFER_CAPACITY);
        final CredentialsEncoder encoder = new CredentialsEncoder();
        encoder.wrapAndApplyHeader(new UnsafeBuffer(scratch), 0, new MessageHeaderEncoder());
        encoder.login("example");
        encoder.putEncryptedPassword(new byte[] {11, 0, 64, 97}, 0, 4);

        final int length = encoder.encodedLength() + MessageHeaderEncoder.ENCODED_LENGTH;
        final ByteBuffer exact = ByteBuffer.allocate(length).order(ByteOrder.LITTLE_ENDIAN);
        exact.put(scratch.array(), 0, length);
        exact.putInt(length - 4 - 4, Integer.MAX_VALUE); // encryptedPassword length prefix

        encodedSchemaBuffer.flip();
        final JsonPrinter printer = new JsonPrinter(decodeIr(encodedSchemaBuffer));

        assertThrows(IndexOutOfBoundsException.class, () -> printer.print(exact));
    }

    @Test
    void shouldRejectVarDataLengthBeyondBufferInListener()
    {
        final Token varDataToken = new Token.Builder().name("data").signal(Signal.BEGIN_VAR_DATA).build();
        final JsonTokenListener listener = new JsonTokenListener(new StringBuilder());
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[16]);

        assertThrows(
            IndexOutOfBoundsException.class,
            () -> listener.onVarData(varDataToken, buffer, 8, Integer.MAX_VALUE, varDataToken));
        assertThrows(
            IndexOutOfBoundsException.class,
            () -> listener.onVarData(varDataToken, buffer, 8, -1, varDataToken));
    }

    @Test
    void shouldRejectGroupBlockLengthBelowFields() throws Exception
    {
        final ByteBuffer encodedSchemaBuffer = ByteBuffer.allocate(SCHEMA_BUFFER_CAPACITY);
        encodeSchema(encodedSchemaBuffer);

        final ByteBuffer encodedMsgBuffer = ByteBuffer.allocate(MSG_BUFFER_CAPACITY).order(ByteOrder.LITTLE_ENDIAN);
        encodeTestMessage(encodedMsgBuffer);
        final int fuelFiguresHeaderOffset = MessageHeaderEncoder.ENCODED_LENGTH + CarEncoder.BLOCK_LENGTH;
        encodedMsgBuffer.putShort(fuelFiguresHeaderOffset, (short)0); // group blockLength

        encodedSchemaBuffer.flip();
        final JsonPrinter printer = new JsonPrinter(decodeIr(encodedSchemaBuffer));

        final IndexOutOfBoundsException ex =
            assertThrows(IndexOutOfBoundsException.class, () -> printer.print(encodedMsgBuffer));
        assertTrue(ex.getMessage().contains("group blockLength=0"), ex.getMessage());
    }

    @Test
    void shouldAcceptGroupBlockLengthThatExcludesCompositeMembersNewerThanActingVersion() throws Exception
    {
        final Ir ir;
        try (InputStream in = Tests.getLocalResource("group-with-versioned-composite.xml"))
        {
            final MessageSchema schema = XmlSchemaParser.parse(in, ParserOptions.DEFAULT);
            ir = new IrGenerator().generate(schema);
        }

        // A version 0 entry holds only the 8 byte mantissa: side was added to the composite in version 1.
        final ByteBuffer encodedMsgBuffer = ByteBuffer.allocate(64).order(ByteOrder.LITTLE_ENDIAN);
        encodedMsgBuffer.putShort(0, (short)0); // blockLength
        encodedMsgBuffer.putShort(2, (short)1); // templateId
        encodedMsgBuffer.putShort(4, (short)1); // schemaId
        encodedMsgBuffer.putShort(6, (short)0); // version
        final int groupHeaderOffset = MessageHeaderEncoder.ENCODED_LENGTH;
        encodedMsgBuffer.putShort(groupHeaderOffset, (short)8);
        encodedMsgBuffer.putShort(groupHeaderOffset + 2, (short)1);

        final JsonPrinter printer = new JsonPrinter(ir);
        assertTrue(printer.print(encodedMsgBuffer).contains("\"mantissa\""));

        encodedMsgBuffer.putShort(6, (short)1); // version
        assertThrows(IndexOutOfBoundsException.class, () -> printer.print(encodedMsgBuffer));
    }

    @Test
    void shouldAcceptGroupBlockLengthThatExcludesConstantFields() throws Exception
    {
        final Ir ir;
        try (InputStream in = Tests.getLocalResource("group-with-constant-fields.xml"))
        {
            final MessageSchema schema = XmlSchemaParser.parse(in, ParserOptions.DEFAULT);
            ir = new IrGenerator().generate(schema);
        }

        final List<Token> tokens = ir.getMessage(1);
        int groupBlockLength = -1;
        for (final Token token : tokens)
        {
            if (Signal.BEGIN_GROUP == token.signal())
            {
                groupBlockLength = token.encodedLength();
                break;
            }
        }

        final int messageBlockLength = tokens.get(0).encodedLength();
        final ByteBuffer encodedMsgBuffer = ByteBuffer.allocate(64).order(ByteOrder.LITTLE_ENDIAN);
        encodedMsgBuffer.putShort(0, (short)messageBlockLength);
        encodedMsgBuffer.putShort(2, (short)1); // templateId
        encodedMsgBuffer.putShort(4, (short)1); // schemaId
        encodedMsgBuffer.putShort(6, (short)0); // version
        final int groupHeaderOffset = MessageHeaderEncoder.ENCODED_LENGTH + messageBlockLength;
        encodedMsgBuffer.putShort(groupHeaderOffset, (short)groupBlockLength);
        encodedMsgBuffer.putShort(groupHeaderOffset + 2, (short)1);

        final JsonPrinter printer = new JsonPrinter(ir);
        assertTrue(printer.print(encodedMsgBuffer).contains("\"f\""));

        encodedMsgBuffer.putShort(groupHeaderOffset, (short)(groupBlockLength - 1));
        assertThrows(IndexOutOfBoundsException.class, () -> printer.print(encodedMsgBuffer));
    }

    @Test
    public void removeTrailingGarbage() throws Exception
    {
        final ByteBuffer encodedSchemaBuffer = ByteBuffer.allocate(SCHEMA_BUFFER_CAPACITY);
        encodeSchema(encodedSchemaBuffer);

        final ByteBuffer encodedMsgBuffer = ByteBuffer.allocate(MSG_BUFFER_CAPACITY);
        final UnsafeBuffer buffer = new UnsafeBuffer(encodedMsgBuffer);
        final CarEncoder encoder = new CarEncoder();
        encoder.wrapAndApplyHeader(buffer, 0, new MessageHeaderEncoder());
        encoder.vehicleCode("vc\0ﾉ�");
        encodedMsgBuffer.position(encoder.encodedLength());
        encodedSchemaBuffer.flip();
        final Ir ir = decodeIr(encodedSchemaBuffer);

        final JsonPrinter printer = new JsonPrinter(ir);
        final String result = printer.print(encodedMsgBuffer);
        assertEquals("{\n" + "    \"serialNumber\": 0,\n" +
            "    \"modelYear\": 0,\n" +
            "    \"available\": \"F\",\n" + "    \"code\": \"null\",\n" + "    \"someNumbers\": [0, 0, 0, 0, 0],\n" +
            "    \"vehicleCode\": \"vc\",\n" + //trailing garbage removed
            "    \"extras\": { \"sunRoof\": false, \"sportsPack\": false, \"cruiseControl\": false },\n" +
            "    \"engine\": \n" + "    {\n" + "        \"capacity\": 0,\n" + "        \"numCylinders\": 0,\n" +
            "        \"maxRpm\": 9000,\n" + "        \"manufacturerCode\": \"\\u0000\",\n" +
            "        \"fuel\": \"Petrol\"\n" + "    },\n" + "    \"uuid\": [0, 0],\n" + "    \"cupHolderCount\": 0,\n" +
            "    \"fuelFigures\": [],\n" + "    \"performanceFigures\": [],\n" + "    \"manufacturer\": \"\",\n" +
            "    \"model\": \"\",\n" + "    \"activationCode\": \"\"\n" + "}",
            result);
    }


    private static void encodeSchema(final ByteBuffer buffer) throws Exception
    {
        final Path path = Paths.get("src/test/resources/json-printer-test-schema.xml");
        try (InputStream in = new BufferedInputStream(Files.newInputStream(path)))
        {
            final MessageSchema schema = XmlSchemaParser.parse(in, ParserOptions.DEFAULT);
            final Ir ir = new IrGenerator().generate(schema);

            try (IrEncoder irEncoder = new IrEncoder(buffer, ir))
            {
                irEncoder.encode();
            }
        }
    }

    private static Ir decodeIr(final ByteBuffer buffer)
    {
        try (IrDecoder irDecoder = new IrDecoder(buffer))
        {
            return irDecoder.decode();
        }
    }
}
