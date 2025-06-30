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
package uk.co.real_logic.sbe.properties;

import net.jqwik.api.*;
import net.jqwik.api.footnotes.EnableFootnotes;
import net.jqwik.api.footnotes.Footnotes;
import uk.co.real_logic.sbe.generation.common.PrecedenceChecks;
import uk.co.real_logic.sbe.generation.cpp.CppDtoGenerator;
import uk.co.real_logic.sbe.generation.cpp.CppGenerator;
import uk.co.real_logic.sbe.generation.cpp.NamespaceOutputManager;
import uk.co.real_logic.sbe.generation.csharp.CSharpDtoGenerator;
import uk.co.real_logic.sbe.generation.csharp.CSharpGenerator;
import uk.co.real_logic.sbe.generation.csharp.CSharpNamespaceOutputManager;
import uk.co.real_logic.sbe.generation.java.JavaDtoGenerator;
import uk.co.real_logic.sbe.generation.java.JavaGenerator;
import uk.co.real_logic.sbe.ir.generated.MessageHeaderDecoder;
import uk.co.real_logic.sbe.properties.arbitraries.SbeArbitraries;
import uk.co.real_logic.sbe.properties.utils.InMemoryOutputManager;
import org.agrona.*;
import org.agrona.io.DirectBufferInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.fail;
import static uk.co.real_logic.sbe.SbeTool.JAVA_DEFAULT_DECODING_BUFFER_TYPE;
import static uk.co.real_logic.sbe.SbeTool.JAVA_DEFAULT_ENCODING_BUFFER_TYPE;
import static uk.co.real_logic.sbe.properties.PropertyTestUtil.addSchemaAndInputMessageFootnotes;

@SuppressWarnings("ReadWriteStringCanBeUsed")
@EnableFootnotes
public class DtosPropertyTest
{
    private static final String DOTNET_EXECUTABLE = System.getProperty("sbe.tests.dotnet.executable", "dotnet");
    private static final String SBE_DLL =
        System.getProperty("sbe.dll", "csharp/sbe-dll/bin/Release/netstandard2.0/SBE.dll");
    private static final String CPP_EXECUTABLE = System.getProperty("sbe.tests.cpp.executable", "g++");
    private final ExpandableArrayBuffer outputBuffer = new ExpandableArrayBuffer();

    @Property
    void javaDtoEncodeShouldBeTheInverseOfDtoDecode(
        @ForAll("encodedMessage") final SbeArbitraries.EncodedMessage encodedMessage,
        final Footnotes footnotes)
        throws Exception
    {
        final String packageName = encodedMessage.ir().applicableNamespace();
        final InMemoryOutputManager outputManager = new InMemoryOutputManager(packageName);

        try
        {
            try
            {
                new JavaGenerator(
                    encodedMessage.ir(),
                    JAVA_DEFAULT_ENCODING_BUFFER_TYPE,
                    JAVA_DEFAULT_DECODING_BUFFER_TYPE,
                    false,
                    false,
                    false,
                    false,
                    PrecedenceChecks.newInstance(new PrecedenceChecks.Context()),
                    outputManager)
                    .generate();

                new JavaDtoGenerator(encodedMessage.ir(), false, outputManager)
                    .generate();
            }
            catch (final Exception generationException)
            {
                fail("Code generation failed.", generationException);
            }

            final Class<?> dtoClass = outputManager.compileAndLoad(packageName + ".TestMessageDto");

            final Method decodeFrom =
                dtoClass.getMethod("decodeFrom", DirectBuffer.class, int.class, int.class, int.class);

            final Method encodeWith =
                dtoClass.getMethod("encodeWithHeaderWith", dtoClass, MutableDirectBuffer.class, int.class);

            final int inputLength = encodedMessage.length();
            final DirectBuffer inputBuffer = encodedMessage.buffer();
            final MessageHeaderDecoder header = new MessageHeaderDecoder().wrap(inputBuffer, 0);
            final int blockLength = header.blockLength();
            final int actingVersion = header.version();
            final Object dto = decodeFrom.invoke(
                null,
                encodedMessage.buffer(), MessageHeaderDecoder.ENCODED_LENGTH, blockLength, actingVersion);
            outputBuffer.setMemory(0, outputBuffer.capacity(), (byte)0);
            final int outputLength = (int)encodeWith.invoke(null, dto, outputBuffer, 0);
            assertEqual(inputBuffer, inputLength, outputBuffer, outputLength);
        }
        catch (final Throwable throwable)
        {
            if (null != footnotes)
            {
                addSchemaAndInputMessageFootnotes(footnotes, encodedMessage);

                final StringBuilder generatedSources = new StringBuilder();
                outputManager.dumpSources(generatedSources);
                footnotes.addFootnote(generatedSources.toString());
            }

            throw throwable;
        }
    }

    @Property
    void csharpDtoEncodeShouldBeTheInverseOfDtoDecode(
        @ForAll("encodedMessage") final SbeArbitraries.EncodedMessage encodedMessage,
        final Footnotes footnotes)
        throws IOException, InterruptedException
    {
        final Path tempDir = Files.createTempDirectory("sbe-csharp-dto-test");

        try
        {
            final CSharpNamespaceOutputManager outputManager = new CSharpNamespaceOutputManager(
                tempDir.toString(),
                "SbePropertyTest"
            );

            try
            {
                new CSharpGenerator(encodedMessage.ir(), outputManager)
                    .generate();
                new CSharpDtoGenerator(encodedMessage.ir(), false, outputManager)
                    .generate();
            }
            catch (final Exception generationException)
            {
                throw new AssertionError(
                    "Code generation failed.\n\n" +
                        "DIR:" + tempDir + "\n\n" +
                        "SCHEMA:\n" + encodedMessage.schema(),
                    generationException);
            }

            copyResourceToFile("/CSharpDtosPropertyTest/SbePropertyTest.csproj", tempDir);
            copyResourceToFile("/CSharpDtosPropertyTest/Program.cs", tempDir);

            writeInputFile(encodedMessage, tempDir);

            execute(encodedMessage.schema(), tempDir, "test",
                DOTNET_EXECUTABLE, "run",
                "--property:SBE_DLL=" + SBE_DLL,
                "--", "input.dat");

            final byte[] inputBytes = new byte[encodedMessage.length()];
            encodedMessage.buffer().getBytes(0, inputBytes);
            final byte[] outputBytes = Files.readAllBytes(tempDir.resolve("output.dat"));
            if (!Arrays.equals(inputBytes, outputBytes))
            {
                throw new AssertionError(
                    "Input and output files differ\n\nDIR:" + tempDir);
            }
        }
        catch (final Throwable throwable)
        {
            addSchemaAndInputMessageFootnotes(footnotes, encodedMessage);
            addGeneratedSourcesFootnotes(footnotes, tempDir, ".cs");

            throw throwable;
        }
        finally
        {
            IoUtil.delete(tempDir.toFile(), true);
        }
    }

    @Property(shrinking = ShrinkingMode.OFF)
    void cppDtoEncodeShouldBeTheInverseOfDtoDecode(
        @ForAll("encodedMessage") final SbeArbitraries.EncodedMessage encodedMessage,
        final Footnotes footnotes) throws IOException, InterruptedException
    {
        final Path tempDir = Files.createTempDirectory("sbe-cpp-dto-test");

        try
        {
            final NamespaceOutputManager outputManager = new NamespaceOutputManager(
                tempDir.toString(), "sbe_property_test");

            try
            {
                new CppGenerator(encodedMessage.ir(), true, outputManager)
                    .generate();
                new CppDtoGenerator(encodedMessage.ir(), false, outputManager)
                    .generate();
            }
            catch (final Exception generationException)
            {
                throw new AssertionError(
                    "Code generation failed.\n\nSCHEMA:\n" + encodedMessage.schema(),
                    generationException);
            }

            copyResourceToFile("/CppDtosPropertyTest/main.cpp", tempDir);

            writeInputFile(encodedMessage, tempDir);

            execute(encodedMessage.schema(), tempDir, "compile",
                CPP_EXECUTABLE, "--std", "c++17", "-o", "round-trip-test", "main.cpp");

            execute(encodedMessage.schema(), tempDir, "test",
                tempDir.resolve("round-trip-test").toString(), "input.dat");

            final byte[] inputBytes = new byte[encodedMessage.length()];
            encodedMessage.buffer().getBytes(0, inputBytes);
            final byte[] outputBytes = Files.readAllBytes(tempDir.resolve("output.dat"));
            if (!Arrays.equals(inputBytes, outputBytes))
            {
                throw new AssertionError("Input and output files differ");
            }
        }
        catch (final Throwable throwable)
        {
            addSchemaAndInputMessageFootnotes(footnotes, encodedMessage);
            addGeneratedSourcesFootnotes(footnotes, tempDir, ".cpp");

            throw throwable;
        }
        finally
        {
            IoUtil.delete(tempDir.toFile(), true);
        }
    }

    private static void writeInputFile(
        final SbeArbitraries.EncodedMessage encodedMessage,
        final Path tempDir) throws IOException
    {
        try (
            DirectBufferInputStream inputStream = new DirectBufferInputStream(
                encodedMessage.buffer(),
                0,
                encodedMessage.length()
            );
            OutputStream outputStream = Files.newOutputStream(tempDir.resolve("input.dat")))
        {
            final byte[] buffer = new byte[2048];
            int read;
            while ((read = inputStream.read(buffer, 0, buffer.length)) >= 0)
            {
                outputStream.write(buffer, 0, read);
            }
        }
    }

    private static void execute(
        final String schema,
        final Path tempDir,
        final String name,
        final String... args) throws InterruptedException, IOException
    {
        final Path stdout = tempDir.resolve(name + "_stdout.txt");
        final Path stderr = tempDir.resolve(name + "_stderr.txt");
        final ProcessBuilder processBuilder = new ProcessBuilder(args)
            .directory(tempDir.toFile())
            .redirectOutput(stdout.toFile())
            .redirectError(stderr.toFile());

        final Process process = processBuilder.start();

        if (0 != process.waitFor())
        {
            throw new AssertionError(
                "Process failed with exit code: " + process.exitValue() + "\n\n" +
                    "DIR:" + tempDir + "\n\n" +
                    "STDOUT:\n" + new String(Files.readAllBytes(stdout)) + "\n\n" +
                    "STDERR:\n" + new String(Files.readAllBytes(stderr)) + "\n\n" +
                    "SCHEMA:\n" + schema);
        }

        final byte[] errorBytes = Files.readAllBytes(stderr);
        if (errorBytes.length != 0)
        {
            throw new AssertionError(
                "Process wrote to stderr.\n\n" +
                    "DIR:" + tempDir + "\n\n" +
                    "STDOUT:\n" + new String(Files.readAllBytes(stdout)) + "\n\n" +
                    "STDERR:\n" + new String(errorBytes) + "\n\n" +
                    "SCHEMA:\n" + schema + "\n\n"
            );
        }
    }

    @Provide
    Arbitrary<SbeArbitraries.EncodedMessage> encodedMessage()
    {
        final SbeArbitraries.CharGenerationConfig config =
            SbeArbitraries.CharGenerationConfig.firstNullTerminatesCharArray();
        return SbeArbitraries.encodedMessage(config);
    }

    private static void copyResourceToFile(
        final String resourcePath,
        final Path outputDir)
    {
        try (InputStream inputStream = DtosPropertyTest.class.getResourceAsStream(resourcePath))
        {
            if (inputStream == null)
            {
                throw new IOException("Resource not found: " + resourcePath);
            }

            final int resourceNameIndex = resourcePath.lastIndexOf('/') + 1;
            final String resourceName = resourcePath.substring(resourceNameIndex);
            final Path outputFilePath = outputDir.resolve(resourceName);
            Files.copy(inputStream, outputFilePath);
        }
        catch (final IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static String readResourceFileAsString(final String resourcePath) throws IOException
    {
        try (InputStream inputStream = DtosPropertyTest.class.getResourceAsStream(resourcePath))
        {
            if (inputStream == null)
            {
                throw new IllegalArgumentException("Resource not found: " + resourcePath);
            }

            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void assertEqual(
        final DirectBuffer inputBuffer,
        final int inputLength,
        final DirectBuffer outputBuffer,
        final int outputLength)
    {
        final boolean lengthsDiffer = inputLength != outputLength;
        final int minLength = Math.min(inputLength, outputLength);

        for (int i = 0; i < minLength; i++)
        {
            if (inputBuffer.getByte(i) != outputBuffer.getByte(i))
            {
                throw new AssertionError(
                    "Input and output differ at byte " + i + ".\n" +
                        "Input length: " + inputLength + ", Output length: " + outputLength + "\n" +
                        "Input: " + inputBuffer.getByte(i) + ", Output: " + outputBuffer.getByte(i) +
                        (lengthsDiffer ? "\nLengths differ." : ""));
            }
        }

        if (lengthsDiffer)
        {
            throw new AssertionError(
                "Input and output differ in length.\n" +
                    "Input length: " + inputLength + ", Output length: " + outputLength);
        }
    }

    private void addGeneratedSourcesFootnotes(
        final Footnotes footnotes,
        final Path directory,
        final String suffix)
    {
        try (Stream<Path> contents = Files.walk(directory))
        {
            contents
                .filter(path -> path.toString().endsWith(suffix))
                .forEach(path ->
                {
                    try
                    {
                        footnotes.addFootnote(System.lineSeparator() + "File: " + path +
                            System.lineSeparator() +
                            new String(Files.readAllBytes(path), StandardCharsets.UTF_8));
                    }
                    catch (final IOException exn)
                    {
                        LangUtil.rethrowUnchecked(exn);
                    }
                });
        }
        catch (final IOException exn)
        {
            LangUtil.rethrowUnchecked(exn);
        }
    }
}
