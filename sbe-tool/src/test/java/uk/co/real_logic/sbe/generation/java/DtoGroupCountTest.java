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
package uk.co.real_logic.sbe.generation.java;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.generation.CompilerUtil;
import org.agrona.generation.StringWriterOutputManager;
import org.junit.jupiter.api.Test;
import org.xml.sax.InputSource;
import uk.co.real_logic.sbe.ir.Ir;
import uk.co.real_logic.sbe.xml.IrGenerator;
import uk.co.real_logic.sbe.xml.ParserOptions;

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteOrder;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.co.real_logic.sbe.xml.XmlSchemaParser.parse;

class DtoGroupCountTest
{
    private static final String SCHEMA =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<sbe:messageSchema xmlns:sbe=\"http://fixprotocol.io/2016/sbe\" package=\"dto.group.count\"\n" +
        "                   id=\"1\" version=\"0\" byteOrder=\"littleEndian\">\n" +
        "    <types>\n" +
        "        <composite name=\"messageHeader\">\n" +
        "            <type name=\"blockLength\" primitiveType=\"uint16\"/>\n" +
        "            <type name=\"templateId\" primitiveType=\"uint16\"/>\n" +
        "            <type name=\"schemaId\" primitiveType=\"uint16\"/>\n" +
        "            <type name=\"version\" primitiveType=\"uint16\"/>\n" +
        "        </composite>\n" +
        "        <composite name=\"groupSizeEncoding\">\n" +
        "            <type name=\"blockLength\" primitiveType=\"uint16\"/>\n" +
        "            <type name=\"numInGroup\" primitiveType=\"uint32\" maxValue=\"2147483647\"/>\n" +
        "        </composite>\n" +
        "    </types>\n" +
        "    <sbe:message name=\"Holder\" id=\"1\">\n" +
        "        <group name=\"items\" id=\"1\" dimensionType=\"groupSizeEncoding\">\n" +
        "            <field name=\"value\" id=\"2\" type=\"int32\"/>\n" +
        "        </group>\n" +
        "    </sbe:message>\n" +
        "</sbe:messageSchema>\n";

    @Test
    void shouldFailOnFirstMissingElementRatherThanPresizeFromWireCount() throws Exception
    {
        final ParserOptions options = ParserOptions.builder().stopOnError(true).build();
        final Ir ir = new IrGenerator().generate(parse(new InputSource(new StringReader(SCHEMA)), options));

        final StringWriterOutputManager outputManager = new StringWriterOutputManager();
        outputManager.setPackageName(ir.applicableNamespace());
        final JavaGenerator generator = new JavaGenerator(
            ir, MutableDirectBuffer.class.getName(), DirectBuffer.class.getName(), false, false, false, outputManager);
        generator.generate();
        new JavaDtoGenerator(ir, false, outputManager).generate();

        final Map<String, CharSequence> sources = outputManager.getSources();
        final Class<?> dtoClass = CompilerUtil.compileInMemory(ir.applicableNamespace() + ".HolderDto", sources);
        final Method decodeFrom = dtoClass.getMethod(
            "decodeFrom", DirectBuffer.class, int.class, int.class, int.class);

        // Group header claiming Integer.MAX_VALUE elements with nothing behind it.
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[16]);
        buffer.putShort(0, (short)4, ByteOrder.LITTLE_ENDIAN);
        buffer.putInt(2, Integer.MAX_VALUE, ByteOrder.LITTLE_ENDIAN);

        final InvocationTargetException ex = assertThrows(
            InvocationTargetException.class, () -> decodeFrom.invoke(null, buffer, 0, 0, 0));
        assertInstanceOf(IndexOutOfBoundsException.class, ex.getCause());
    }
}
