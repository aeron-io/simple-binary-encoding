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
package uk.co.real_logic.sbe.ir;

import org.junit.jupiter.api.Test;
import uk.co.real_logic.sbe.xml.IrGenerator;
import uk.co.real_logic.sbe.xml.MessageSchema;
import uk.co.real_logic.sbe.xml.ParserOptions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static uk.co.real_logic.sbe.xml.XmlSchemaParser.parse;

class EnumPresenceIrTest
{
    private static final String SCHEMA =
        "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>" +
        "<sbe:messageSchema xmlns:sbe=\"http://fixprotocol.io/2016/sbe\"" +
        "    package=\"enum.presence\" id=\"1063\" version=\"0\" byteOrder=\"littleEndian\">" +
        "  <types>" +
        "    <composite name=\"messageHeader\">" +
        "      <type name=\"blockLength\" primitiveType=\"uint16\"/>" +
        "      <type name=\"templateId\" primitiveType=\"uint16\"/>" +
        "      <type name=\"schemaId\" primitiveType=\"uint16\"/>" +
        "      <type name=\"version\" primitiveType=\"uint16\"/>" +
        "    </composite>" +
        "    <type name=\"uInt8NULL\" primitiveType=\"uint8\" presence=\"optional\" nullValue=\"255\"/>" +
        "    <enum name=\"OptionalEncodingEnum\" encodingType=\"uInt8NULL\">" +
        "      <validValue name=\"A\">0</validValue>" +
        "    </enum>" +
        "    <enum name=\"OptionalEnum\" encodingType=\"uint8\" presence=\"optional\" nullValue=\"255\">" +
        "      <validValue name=\"A\">0</validValue>" +
        "    </enum>" +
        "    <enum name=\"RequiredEnum\" encodingType=\"uint8\">" +
        "      <validValue name=\"A\">0</validValue>" +
        "    </enum>" +
        "    <composite name=\"Holder\">" +
        "      <ref name=\"optionalEncoding\" type=\"OptionalEncodingEnum\"/>" +
        "      <ref name=\"required\" type=\"RequiredEnum\"/>" +
        "    </composite>" +
        "  </types>" +
        "  <sbe:message name=\"Msg\" id=\"1\">" +
        "    <field name=\"optionalEncoding\" id=\"1\" type=\"OptionalEncodingEnum\"/>" +
        "    <field name=\"optionalEnum\" id=\"2\" type=\"OptionalEnum\"/>" +
        "    <field name=\"required\" id=\"3\" type=\"RequiredEnum\"/>" +
        "    <field name=\"optionalField\" id=\"4\" type=\"RequiredEnum\" presence=\"optional\"/>" +
        "    <field name=\"holder\" id=\"5\" type=\"Holder\"/>" +
        "  </sbe:message>" +
        "</sbe:messageSchema>";

    @Test
    void shouldCarryEnumTypePresenceOnEnumTokens() throws Exception
    {
        final List<Token> tokens = generateMessageTokens();

        assertEnumPresence(tokens, "optionalEncoding", Encoding.Presence.OPTIONAL);
        assertEnumPresence(tokens, "optionalEnum", Encoding.Presence.OPTIONAL);
        assertEnumPresence(tokens, "required", Encoding.Presence.REQUIRED);
    }

    @Test
    void shouldKeepFieldPresenceOnFieldTokenOnly() throws Exception
    {
        final List<Token> tokens = generateMessageTokens();
        final int fieldIndex = indexOfField(tokens, "optionalField");

        assertThat(tokens.get(fieldIndex).encoding().presence(), is(Encoding.Presence.OPTIONAL));
        assertThat(tokens.get(fieldIndex + 1).encoding().presence(), is(Encoding.Presence.REQUIRED));
    }

    @Test
    void shouldCarryEnumTypePresenceOnEnumTokensInComposite() throws Exception
    {
        final List<Token> tokens = generateMessageTokens();
        final int compositeIndex = indexOfField(tokens, "holder") + 1;
        final Token optionalEncoding = tokens.get(compositeIndex + 1);
        final int requiredIndex = compositeIndex + 1 + optionalEncoding.componentTokenCount();

        assertThat(optionalEncoding.signal(), is(Signal.BEGIN_ENUM));
        assertThat(optionalEncoding.encoding().presence(), is(Encoding.Presence.OPTIONAL));
        assertThat(tokens.get(requiredIndex - 1).signal(), is(Signal.END_ENUM));
        assertThat(tokens.get(requiredIndex - 1).encoding().presence(), is(Encoding.Presence.OPTIONAL));
        assertThat(tokens.get(requiredIndex).signal(), is(Signal.BEGIN_ENUM));
        assertThat(tokens.get(requiredIndex).encoding().presence(), is(Encoding.Presence.REQUIRED));
    }

    private static List<Token> generateMessageTokens() throws Exception
    {
        try (InputStream in = new ByteArrayInputStream(SCHEMA.getBytes(StandardCharsets.UTF_8)))
        {
            final MessageSchema schema = parse(in, ParserOptions.DEFAULT);
            final Ir ir = new IrGenerator().generate(schema);

            return ir.getMessage(1);
        }
    }

    private static void assertEnumPresence(
        final List<Token> tokens, final String fieldName, final Encoding.Presence expected)
    {
        final int fieldIndex = indexOfField(tokens, fieldName);
        final Token beginEnum = tokens.get(fieldIndex + 1);
        final Token endEnum = tokens.get(fieldIndex + beginEnum.componentTokenCount());

        assertThat(beginEnum.signal(), is(Signal.BEGIN_ENUM));
        assertThat(beginEnum.encoding().presence(), is(expected));
        assertThat(endEnum.signal(), is(Signal.END_ENUM));
        assertThat(endEnum.encoding().presence(), is(expected));
    }

    private static int indexOfField(final List<Token> tokens, final String fieldName)
    {
        for (int i = 0; i < tokens.size(); i++)
        {
            final Token token = tokens.get(i);
            if (token.signal() == Signal.BEGIN_FIELD && token.name().equals(fieldName))
            {
                return i;
            }
        }

        throw new IllegalArgumentException("field not found: " + fieldName);
    }
}
