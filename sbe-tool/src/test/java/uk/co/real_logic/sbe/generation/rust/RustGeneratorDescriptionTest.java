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
package uk.co.real_logic.sbe.generation.rust;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.real_logic.sbe.Tests;
import uk.co.real_logic.sbe.ir.Ir;
import uk.co.real_logic.sbe.xml.IrGenerator;
import uk.co.real_logic.sbe.xml.MessageSchema;
import uk.co.real_logic.sbe.xml.ParserOptions;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static uk.co.real_logic.sbe.xml.XmlSchemaParser.parse;

class RustGeneratorDescriptionTest
{
    @TempDir
    Path tempDir;

    private Path generatedSrcDir;

    @BeforeEach
    void generate() throws Exception
    {
        try (InputStream in = Tests.getLocalResource("rust-description-test-schema.xml"))
        {
            final ParserOptions options = ParserOptions.builder().stopOnError(true).build();
            final MessageSchema schema = parse(in, options);
            final IrGenerator irg = new IrGenerator();
            final Ir ir = irg.generate(schema);

            final RustOutputManager outputManager = new RustOutputManager(
                tempDir.toString(), ir.applicableNamespace());
            final RustGenerator generator = new RustGenerator(ir, "0.1.0", outputManager);
            generator.generate();

            generatedSrcDir = tempDir.resolve("description_test").resolve("src");
        }
    }

    @Test
    void enumTypeDescriptionAppearsAsDocComment() throws Exception
    {
        final String source = readGeneratedFile("colour.rs");
        assertThat(source, containsString("/// The colour of the vehicle"));
    }

    @Test
    void enumVariantDescriptionsAppearAsDocComments() throws Exception
    {
        final String source = readGeneratedFile("colour.rs");
        assertThat(source, containsString("/// Red colour"));
        assertThat(source, containsString("/// Green colour"));
        assertThat(source, containsString("/// Blue colour"));
    }

    @Test
    void bitsetTypeDescriptionAppearsAsDocComment() throws Exception
    {
        final String source = readGeneratedFile("features.rs");
        assertThat(source, containsString("/// Optional features fitted to the vehicle"));
    }

    @Test
    void bitsetChoiceDescriptionsAppearAsDocComments() throws Exception
    {
        final String source = readGeneratedFile("features.rs");
        assertThat(source, containsString("/// Panoramic sun roof"));
        assertThat(source, containsString("/// Sports handling pack"));
    }

    @Test
    void compositeDescriptionAppearsAsModuleDocComment() throws Exception
    {
        final String source = readGeneratedFile("engine_codec.rs");
        assertThat(source, containsString("//! Internal combustion engine specification"));
    }

    @Test
    void primitiveFieldDescriptionAppearsInDocComment() throws Exception
    {
        final String source = readGeneratedFile("vehicle_codec.rs");
        assertThat(source, containsString("/// - description: Unique serial number of the vehicle"));
    }

    @Test
    void enumFieldDescriptionAppearsInDocComment() throws Exception
    {
        final String source = readGeneratedFile("vehicle_codec.rs");
        assertThat(source, containsString("/// - description: Exterior colour of the vehicle"));
    }

    @Test
    void bitsetFieldDescriptionAppearsInDocComment() throws Exception
    {
        final String source = readGeneratedFile("vehicle_codec.rs");
        assertThat(source, containsString("/// - description: Bitset of optional features fitted"));
    }

    @Test
    void compositeFieldDescriptionAppearsInDocComment() throws Exception
    {
        final String source = readGeneratedFile("vehicle_codec.rs");
        assertThat(source, containsString("/// - description: Engine specification composite"));
    }

    @Test
    void groupDescriptionAppearsOnEncoderAndDecoder() throws Exception
    {
        final String source = readGeneratedFile("vehicle_codec.rs");
        assertThat(source, containsString(
            "/// GROUP ENCODER (id=5, description='Passengers currently in the vehicle')"));
        assertThat(source, containsString(
            "/// GROUP DECODER (id=5, description='Passengers currently in the vehicle')"));
    }

    @Test
    void groupFieldDescriptionAppearsInDocComment() throws Exception
    {
        final String source = readGeneratedFile("vehicle_codec.rs");
        assertThat(source, containsString("/// - description: Age of the passenger in years"));
    }

    @Test
    void varDataDescriptionAppearsInDocComment() throws Exception
    {
        final String source = readGeneratedFile("vehicle_codec.rs");
        assertThat(source, containsString("/// - description: Full model name of the vehicle"));
    }

    @Test
    void generatorSucceedsAndProducesNoDescriptionCommentsWhenSchemaHasNoDescriptions() throws Exception
    {
        try (InputStream in = Tests.getLocalResource("rust-no-description-test-schema.xml"))
        {
            final ParserOptions options = ParserOptions.builder().stopOnError(true).build();
            final MessageSchema schema = parse(in, options);
            final IrGenerator irg = new IrGenerator();
            final Ir ir = irg.generate(schema);

            final RustOutputManager outputManager = new RustOutputManager(
                tempDir.toString(), ir.applicableNamespace());
            final RustGenerator generator = new RustGenerator(ir, "0.1.0", outputManager);

            assertDoesNotThrow(generator::generate);

            final Path srcDir = tempDir.resolve("no_description_test").resolve("src");
            final String enumSource = Files.readString(srcDir.resolve("colour.rs"));
            final String bitsetSource = Files.readString(srcDir.resolve("features.rs"));
            final String compositeSource = Files.readString(srcDir.resolve("engine_codec.rs"));
            final String messageSource = Files.readString(srcDir.resolve("vehicle_codec.rs"));

            assertThat(enumSource, not(containsString("/// -")));
            assertThat(bitsetSource, not(containsString("/// -")));
            assertThat(compositeSource, not(containsString("//!")));
            assertThat(messageSource, not(containsString("/// - description:")));
        }
    }

    private String readGeneratedFile(final String fileName) throws Exception
    {
        return Files.readString(generatedSrcDir.resolve(fileName));
    }
}
