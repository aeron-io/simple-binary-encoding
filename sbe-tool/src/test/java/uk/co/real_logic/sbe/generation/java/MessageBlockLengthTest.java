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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.co.real_logic.sbe.Tests;
import uk.co.real_logic.sbe.ir.Ir;
import uk.co.real_logic.sbe.otf.OtfMessageDecoder;
import uk.co.real_logic.sbe.otf.TokenListener;
import uk.co.real_logic.sbe.xml.IrGenerator;
import uk.co.real_logic.sbe.xml.ParserOptions;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static uk.co.real_logic.sbe.xml.XmlSchemaParser.parse;

/**
 * The message has a (0), b (8), px (16, with side at 24 since version 1), a constant, and c (25, since version 1),
 * so the minimum block length is 24 at version 0 and 26 at version 1.
 */
class MessageBlockLengthTest
{
    private static Ir ir;
    private static Class<?> decoderClass;
    private static Method wrap;

    @BeforeAll
    static void generate() throws Exception
    {
        try (InputStream in = Tests.getLocalResource("message-with-versioned-fields.xml"))
        {
            ir = new IrGenerator().generate(parse(in, ParserOptions.builder().stopOnError(true).build()));
        }

        final StringWriterOutputManager outputManager = new StringWriterOutputManager();
        outputManager.setPackageName(ir.applicableNamespace());
        new JavaGenerator(
            ir, MutableDirectBuffer.class.getName(), DirectBuffer.class.getName(), false, false, false, outputManager)
            .generate();

        final String decoderName = ir.applicableNamespace() + ".VersionedDecoder";
        decoderClass = CompilerUtil.compileInMemory(decoderName, outputManager.getSources());
        wrap = decoderClass.getMethod("wrap", DirectBuffer.class, int.class, int.class, int.class);
    }

    private static void wrap(final int blockLength, final int actingVersion) throws Throwable
    {
        try
        {
            wrap.invoke(decoderClass.getConstructor().newInstance(), new UnsafeBuffer(new byte[64]), 0,
                blockLength, actingVersion);
        }
        catch (final InvocationTargetException ex)
        {
            throw ex.getCause();
        }
    }

    @ParameterizedTest
    @CsvSource({ "0, 0", "8, 0", "23, 0", "24, 1", "25, 1", "0, 1" })
    void shouldRejectMessageBlockLengthBelowFieldsAtActingVersion(final int blockLength, final int actingVersion)
    {
        assertThrows(IndexOutOfBoundsException.class, () -> wrap(blockLength, actingVersion));
    }

    @ParameterizedTest
    @CsvSource({ "24, 0", "32, 0", "26, 1", "40, 1" })
    void shouldAcceptMessageBlockLengthCoveringFieldsAtActingVersion(final int blockLength, final int actingVersion)
        throws Throwable
    {
        wrap(blockLength, actingVersion);
    }

    @ParameterizedTest
    @CsvSource({ "0, 0", "8, 0", "23, 0", "24, 1", "25, 1", "0, 1" })
    void shouldRejectMessageBlockLengthBelowFieldsInOtfDecoder(final int blockLength, final int actingVersion)
    {
        assertThrows(IndexOutOfBoundsException.class, () -> decodeOtf(blockLength, actingVersion));
    }

    @ParameterizedTest
    @CsvSource({ "24, 0", "32, 0", "26, 1", "40, 1" })
    void shouldAcceptMessageBlockLengthCoveringFieldsInOtfDecoder(final int blockLength, final int actingVersion)
    {
        decodeOtf(blockLength, actingVersion);
    }

    @Test
    void shouldDecodeOlderVersionMessageWithShorterBlock() throws Throwable
    {
        final int blockLength = 24;
        final int actingVersion = 0;
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[64]);
        buffer.putLong(0, 11, ByteOrder.LITTLE_ENDIAN);
        buffer.putLong(8, 22, ByteOrder.LITTLE_ENDIAN);
        final Object decoder = decoderClass.getConstructor().newInstance();
        wrap.invoke(decoder, buffer, 0, blockLength, actingVersion);

        assertEquals(11L, decoderClass.getMethod("a").invoke(decoder));
        assertEquals(22L, decoderClass.getMethod("b").invoke(decoder));
        assertEquals((short)255, decoderClass.getMethod("c").invoke(decoder));
        assertEquals(blockLength, decoderClass.getMethod("limit").invoke(decoder));
    }

    private static void decodeOtf(final int blockLength, final int actingVersion)
    {
        OtfMessageDecoder.decode(
            new UnsafeBuffer(new byte[64]), 0, actingVersion, blockLength, ir.getMessage(1), mock(TokenListener.class));
    }
}
