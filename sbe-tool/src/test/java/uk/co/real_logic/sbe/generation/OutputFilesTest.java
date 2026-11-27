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
package uk.co.real_logic.sbe.generation;

import org.agrona.generation.OutputManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import uk.co.real_logic.sbe.generation.c.COutputManager;
import uk.co.real_logic.sbe.generation.cpp.NamespaceOutputManager;
import uk.co.real_logic.sbe.generation.csharp.CSharpNamespaceOutputManager;
import uk.co.real_logic.sbe.generation.golang.flyweight.GolangFlyweightOutputManager;
import uk.co.real_logic.sbe.generation.golang.struct.GolangOutputManager;
import uk.co.real_logic.sbe.generation.java.JavaOutputManager;
import uk.co.real_logic.sbe.generation.rust.RustOutputManager;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.BiFunction;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.co.real_logic.sbe.generation.OutputFiles.checkFileName;

class OutputFilesTest
{
    @ParameterizedTest
    @ValueSource(strings = { "../X", "../../X", "a/b", "a\\b", "/tmp/X", "C:X", "" })
    void shouldRejectNamesThatAreNotASingleFileName(final String name)
    {
        assertThrows(IllegalArgumentException.class, () -> checkFileName(name));
    }

    @Test
    void shouldAcceptIdentifiers()
    {
        assertEquals("CarDecoder", checkFileName("CarDecoder"));
        assertEquals("..Car", checkFileName("..Car"));
    }

    @Test
    void shouldNotWriteOutsideTheOutputDirectory(final @TempDir Path dir) throws IOException
    {
        final Path outputDir = dir.resolve("a").resolve("b").resolve("out");
        final String base = outputDir.toString();
        final Stream<BiFunction<String, String, OutputManager>> managers = Stream.of(
            COutputManager::new,
            NamespaceOutputManager::new,
            CSharpNamespaceOutputManager::new,
            GolangOutputManager::new,
            GolangFlyweightOutputManager::new,
            RustOutputManager::new,
            JavaOutputManager::new);

        assertAll(managers.map((factory) -> () ->
        {
            final OutputManager outputManager = factory.apply(base, "pkg");
            assertThrows(
                IllegalArgumentException.class,
                () -> outputManager.createOutput("../../../../Escaped"),
                outputManager.getClass().getSimpleName());

            try (Writer ignore = outputManager.createOutput("Car"))
            {
                ignore.write("");
            }
        }));

        try (Stream<Path> files = Files.walk(dir))
        {
            assertFalse(files.anyMatch((path) -> path.getFileName().toString().startsWith("Escaped") ||
                path.getFileName().toString().startsWith("escaped")));
        }
    }
}
