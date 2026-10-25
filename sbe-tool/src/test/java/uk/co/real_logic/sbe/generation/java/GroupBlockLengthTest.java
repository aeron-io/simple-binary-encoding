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

import extension.CarDecoder;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.concurrent.UnsafeBuffer;
import org.agrona.generation.CompilerUtil;
import org.agrona.generation.StringWriterOutputManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import uk.co.real_logic.sbe.Tests;
import uk.co.real_logic.sbe.ir.Ir;
import uk.co.real_logic.sbe.xml.IrGenerator;
import uk.co.real_logic.sbe.xml.ParserOptions;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteOrder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.co.real_logic.sbe.xml.XmlSchemaParser.parse;

class GroupBlockLengthTest
{
    // fuelFigures has speed (uint16) since version 0 and mpg (float) since version 2.
    private static UnsafeBuffer carWithFuelFiguresHeader(final int blockLength, final int count)
    {
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[256]);
        buffer.putShort(CarDecoder.BLOCK_LENGTH, (short)blockLength, ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(CarDecoder.BLOCK_LENGTH + 2, (short)count, ByteOrder.LITTLE_ENDIAN);
        return buffer;
    }

    private static CarDecoder.FuelFiguresDecoder fuelFigures(
        final int blockLength, final int count, final int actingVersion)
    {
        final CarDecoder decoder = new CarDecoder();
        decoder.wrap(carWithFuelFiguresHeader(blockLength, count), 0, CarDecoder.BLOCK_LENGTH, actingVersion);
        return decoder.fuelFigures();
    }

    @ParameterizedTest
    @CsvSource({ "0, 1, 2", "2, 1, 2", "5, 1, 2", "0, 1, 0", "1, 1, 1", "1, 3, 0" })
    void shouldRejectGroupBlockLengthBelowFieldsAtActingVersion(
        final int blockLength, final int count, final int actingVersion)
    {
        assertThrows(IndexOutOfBoundsException.class, () -> fuelFigures(blockLength, count, actingVersion));
    }

    @ParameterizedTest
    @CsvSource({ "6, 1, 2", "8, 1, 2", "2, 1, 0", "2, 1, 1", "6, 3, 1", "0, 0, 2" })
    void shouldAcceptGroupBlockLengthCoveringFieldsAtActingVersion(
        final int blockLength, final int count, final int actingVersion)
    {
        assertEquals(count, fuelFigures(blockLength, count, actingVersion).count());
    }

    @Test
    void shouldNotCountConstantFieldsTowardsMinimumBlockLength() throws Exception
    {
        final Ir ir;
        try (InputStream in = Tests.getLocalResource("group-with-constant-fields.xml"))
        {
            ir = new IrGenerator().generate(parse(in, ParserOptions.builder().stopOnError(true).build()));
        }

        final StringWriterOutputManager outputManager = new StringWriterOutputManager();
        outputManager.setPackageName(ir.applicableNamespace());
        new JavaGenerator(
            ir, MutableDirectBuffer.class.getName(), DirectBuffer.class.getName(), false, false, false, outputManager)
            .generate();

        final String decoderName = ir.applicableNamespace() + ".ConstantsGaloreDecoder";
        final Class<?> decoderClass = CompilerUtil.compileInMemory(decoderName, outputManager.getSources());
        final Class<?> groupClass = decoderClass.getClassLoader().loadClass(decoderName + "$FDecoder");
        final int messageBlockLength = decoderClass.getField("BLOCK_LENGTH").getInt(null);
        final int groupBlockLength = (int)groupClass.getMethod("sbeBlockLength").invoke(null);
        final Method wrap = decoderClass.getMethod("wrap", DirectBuffer.class, int.class, int.class, int.class);
        final Method f = decoderClass.getMethod("f");

        // The group ends with constant fields, which have no bytes on the wire.
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[64]);
        buffer.putShort(messageBlockLength, (short)groupBlockLength, ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(messageBlockLength + 2, (short)1, ByteOrder.LITTLE_ENDIAN);
        final Object decoder = decoderClass.getConstructor().newInstance();
        wrap.invoke(decoder, buffer, 0, messageBlockLength, 0);
        f.invoke(decoder);

        buffer.putShort(messageBlockLength, (short)(groupBlockLength - 1), ByteOrder.LITTLE_ENDIAN);
        wrap.invoke(decoder, buffer, 0, messageBlockLength, 0);
        final InvocationTargetException ex = assertThrows(InvocationTargetException.class, () -> f.invoke(decoder));
        assertInstanceOf(IndexOutOfBoundsException.class, ex.getCause());
    }

    @Test
    void shouldNotCountCompositeMembersNewerThanActingVersion() throws Exception
    {
        final Ir ir;
        try (InputStream in = Tests.getLocalResource("group-with-versioned-composite.xml"))
        {
            ir = new IrGenerator().generate(parse(in, ParserOptions.builder().stopOnError(true).build()));
        }

        final StringWriterOutputManager outputManager = new StringWriterOutputManager();
        outputManager.setPackageName(ir.applicableNamespace());
        new JavaGenerator(
            ir, MutableDirectBuffer.class.getName(), DirectBuffer.class.getName(), false, false, false, outputManager)
            .generate();

        final String decoderName = ir.applicableNamespace() + ".QuotesDecoder";
        final Class<?> decoderClass = CompilerUtil.compileInMemory(decoderName, outputManager.getSources());
        final int messageBlockLength = decoderClass.getField("BLOCK_LENGTH").getInt(null);
        final Method wrap = decoderClass.getMethod("wrap", DirectBuffer.class, int.class, int.class, int.class);
        final Method entries = decoderClass.getMethod("entries");
        final Object decoder = decoderClass.getConstructor().newInstance();

        // A version 0 entry holds only the 8 byte mantissa: side was added to the composite in version 1.
        final UnsafeBuffer buffer = new UnsafeBuffer(new byte[64]);
        buffer.putShort(messageBlockLength, (short)8, ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(messageBlockLength + 2, (short)1, ByteOrder.LITTLE_ENDIAN);
        wrap.invoke(decoder, buffer, 0, messageBlockLength, 0);
        entries.invoke(decoder);

        wrap.invoke(decoder, buffer, 0, messageBlockLength, 1);
        final InvocationTargetException ex = assertThrows(
            InvocationTargetException.class, () -> entries.invoke(decoder));
        assertInstanceOf(IndexOutOfBoundsException.class, ex.getCause());
    }
}
