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

import org.agrona.generation.CompilerUtil;
import org.agrona.generation.StringWriterOutputManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import uk.co.real_logic.sbe.Tests;
import uk.co.real_logic.sbe.generation.Generators;
import uk.co.real_logic.sbe.generation.csharp.CSharpDtoGenerator;
import uk.co.real_logic.sbe.generation.csharp.CSharpGenerator;
import uk.co.real_logic.sbe.generation.csharp.CSharpNamespaceOutputManager;
import uk.co.real_logic.sbe.generation.golang.struct.GolangGenerator;
import uk.co.real_logic.sbe.generation.golang.struct.GolangOutputManager;
import uk.co.real_logic.sbe.generation.rust.RustGenerator;
import uk.co.real_logic.sbe.generation.rust.RustOutputManager;
import uk.co.real_logic.sbe.ir.Ir;
import uk.co.real_logic.sbe.xml.IrGenerator;
import uk.co.real_logic.sbe.xml.ParserOptions;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static uk.co.real_logic.sbe.generation.java.JavaUtil.escapeJavaStringLiteral;
import static uk.co.real_logic.sbe.generation.java.JavaUtil.escapeJavadoc;
import static uk.co.real_logic.sbe.xml.XmlSchemaParser.parse;

class SchemaInjectionTest
{
    @Test
    void shouldEscapeJavaStringLiterals()
    {
        assertEquals("a\\\"b\\\\c\\n\\u0001é", escapeJavaStringLiteral("a\"b\\c\n\u0001é"));
    }

    @Test
    void shouldEscapeStringLiteralsForOtherLanguages()
    {
        assertEquals("a\\\"b\\\\c\\n\\u0001", Generators.escapeStringLiteral("a\"b\\c\n\u0001", false));
        assertEquals("\\u{1}", Generators.escapeStringLiteral("\u0001", true));
        assertEquals("a b c", Generators.toSingleLine("a\nb\rc"));
    }

    @Test
    void shouldNeutraliseJavadocTerminatorsAndUnicodeEscapes()
    {
        assertEquals("a *&#47; b", escapeJavadoc("a */ b"));
        assertEquals("&#92;u002a&#92;u002f", escapeJavadoc("\\u002a\\u002f"));
    }

    private static Ir injectionIr() throws Exception
    {
        try (InputStream in = Tests.getLocalResource("schema-injection.xml"))
        {
            final ParserOptions options = ParserOptions.builder().stopOnError(true).build();
            return new IrGenerator().generate(parse(in, options));
        }
    }

    private static String readAll(final Path dir) throws Exception
    {
        try (Stream<Path> files = Files.walk(dir))
        {
            return files.filter(Files::isRegularFile).map((file) ->
            {
                try
                {
                    return new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
                }
                catch (final Exception ex)
                {
                    throw new IllegalStateException(ex);
                }
            }).collect(Collectors.joining("\n"));
        }
    }

    @Test
    void shouldEscapeSchemaTextInCSharpGoAndRustSource(final @TempDir Path dir) throws Exception
    {
        final Ir ir = injectionIr();

        final Path csharp = dir.resolve("csharp");
        new CSharpGenerator(ir, new CSharpNamespaceOutputManager(csharp.toString(), ir.applicableNamespace()))
            .generate();
        new CSharpDtoGenerator(
            ir, false, new CSharpNamespaceOutputManager(csharp.toString(), ir.applicableNamespace())).generate();

        final Path golang = dir.resolve("golang");
        new GolangGenerator(ir, new GolangOutputManager(golang.toString(), ir.applicableNamespace())).generate();

        final Path rust = dir.resolve("rust");
        new RustGenerator(ir, "0.1.0", new RustOutputManager(rust.toString(), ir.applicableNamespace())).generate();

        assertAll(Stream.of(csharp, golang, rust).map((languageDir) -> () ->
        {
            final String source = readAll(languageDir);
            assertFalse(source.contains("a\"; } static {"), languageDir.toString());
            assertTrue(source.contains("a\\\"; } static {"), languageDir.toString());
            assertFalse(source.contains("\nfn injected() {}"), languageDir.toString());
        }));
    }

    @Test
    void shouldNotLetSchemaTextBecomeGeneratedCode() throws Exception
    {
        final Ir ir = injectionIr();

        final StringWriterOutputManager outputManager = new StringWriterOutputManager();
        outputManager.setPackageName(ir.applicableNamespace());
        final JavaGenerator generator = new JavaGenerator(
            ir, "org.agrona.MutableDirectBuffer", "org.agrona.DirectBuffer", false, false, false, outputManager);
        generator.generate();

        final Map<String, CharSequence> sources = outputManager.getSources();
        assertFalse(sources.containsKey("injection.test.Injected"));

        final Class<?> decoder = CompilerUtil.compileInMemory("injection.test.ProbeDecoder", sources);
        assertNotNull(decoder);
        assertThrows(NoSuchMethodException.class, () -> decoder.getDeclaredMethod("x"));
        final Method label = decoder.getDeclaredMethod("label");
        assertEquals(
            "a\"; } static { throw new Error(); } String x() { return \"b\\u0022",
            label.invoke(decoder.getConstructor().newInstance()));
    }
}
