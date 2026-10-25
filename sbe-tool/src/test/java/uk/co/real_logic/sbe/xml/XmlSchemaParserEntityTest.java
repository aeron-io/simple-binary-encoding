/*
 * Copyright 2026 Real Logic Limited.
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
package uk.co.real_logic.sbe.xml;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import java.io.InputStream;
import java.io.StringReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class XmlSchemaParserEntityTest
{
    private static final String SCHEMA_WITH_EXTERNAL_ENTITY =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<!DOCTYPE sbe:messageSchema [ <!ENTITY secret SYSTEM \"%s\"> ]>\n" +
        "<sbe:messageSchema xmlns:sbe=\"http://fixprotocol.io/2016/sbe\" package=\"p\" id=\"1\" version=\"0\">\n" +
        "    <types>\n" +
        "        <composite name=\"messageHeader\">\n" +
        "            <type name=\"blockLength\" primitiveType=\"uint16\"/>\n" +
        "            <type name=\"templateId\" primitiveType=\"uint16\"/>\n" +
        "            <type name=\"schemaId\" primitiveType=\"uint16\"/>\n" +
        "            <type name=\"version\" primitiveType=\"uint16\"/>\n" +
        "        </composite>\n" +
        "        <type name=\"T\" primitiveType=\"char\" length=\"4\" presence=\"constant\">&secret;</type>\n" +
        "    </types>\n" +
        "</sbe:messageSchema>\n";

    private static InputSource schemaReading(final Path file)
    {
        return new InputSource(new StringReader(String.format(SCHEMA_WITH_EXTERNAL_ENTITY, file.toUri())));
    }

    @Test
    void shouldRejectDoctypeWithExternalEntityWhenParsing(final @TempDir Path dir) throws Exception
    {
        final Path secret = Files.write(dir.resolve("secret.txt"), "TOP".getBytes(StandardCharsets.UTF_8));

        assertThrows(
            SAXParseException.class, () -> XmlSchemaParser.parse(schemaReading(secret), ParserOptions.DEFAULT));
    }

    @Test
    void shouldRejectDoctypeWithExternalEntityWhenValidating(final @TempDir Path dir) throws Exception
    {
        final Path secret = Files.write(dir.resolve("secret.txt"), "TOP".getBytes(StandardCharsets.UTF_8));
        final String xsd = Paths.get("src/main/resources/fpl/sbe.xsd").toAbsolutePath().toString();

        assertThrows(
            SAXParseException.class,
            () -> XmlSchemaParser.validate(xsd, schemaReading(secret), ParserOptions.DEFAULT));
    }

    @Test
    void shouldStillResolveXIncludeWhenParsingAndValidating() throws Exception
    {
        final URL resource = getClass().getClassLoader().getResource("sub/basic-schema.xml");
        assertNotNull(resource);
        final String systemId = Paths.get(resource.toURI()).getParent().toUri().toString();
        final String xsd = Paths.get("src/main/resources/fpl/sbe.xsd").toAbsolutePath().toString();

        try (InputStream in = resource.openStream())
        {
            final InputSource source = new InputSource(in);
            source.setSystemId(systemId);
            assertDoesNotThrow(() -> XmlSchemaParser.validate(xsd, source, ParserOptions.DEFAULT));
        }

        try (InputStream in = resource.openStream())
        {
            final InputSource source = new InputSource(in);
            source.setSystemId(systemId);
            assertNotNull(XmlSchemaParser.parse(source, ParserOptions.DEFAULT).getType("Symbol"));
        }
    }
}
