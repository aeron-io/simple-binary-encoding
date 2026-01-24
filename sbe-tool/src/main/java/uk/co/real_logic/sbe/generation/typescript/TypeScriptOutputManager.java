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
package uk.co.real_logic.sbe.generation.typescript;

import org.agrona.Verify;
import org.agrona.generation.OutputManager;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import static java.io.File.separatorChar;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * {@link OutputManager} for managing the creation of TypeScript source files as the target of code generation.
 * The character encoding for the {@link Writer} is UTF-8.
 */
public class TypeScriptOutputManager implements OutputManager
{
    private final File outputDir;
    private final List<String> generatedFiles = new ArrayList<>();

    /**
     * Create a new {@link OutputManager} for generating TypeScript source files into a given namespace directory.
     *
     * @param baseDirName for the generated source code.
     * @param packageName for the generated source code relative to the baseDirName.
     */
    public TypeScriptOutputManager(final String baseDirName, final String packageName)
    {
        Verify.notNull(baseDirName, "baseDirName");
        Verify.notNull(packageName, "packageName");

        String dirName = baseDirName.endsWith("" + separatorChar) ? baseDirName : baseDirName + separatorChar;

        // Convert package name to directory path (e.g., "com.example" -> "com/example")
        if (packageName != null && !packageName.isEmpty())
        {
            dirName += packageName.replace('.', separatorChar);
        }

        this.outputDir = createDir(dirName);
    }

    private static File createDir(final String dirName)
    {
        final File dir = new File(dirName);
        if (!dir.exists() && !dir.mkdirs())
        {
            throw new IllegalStateException("Unable to create directory: " + dirName);
        }

        return dir;
    }

    /**
     * Create a new output which will be a TypeScript source file in the given namespace directory.
     * <p>
     * The {@link Writer} should be closed once the caller has finished with it. The Writer is
     * buffered for efficient IO operations.
     *
     * @param name the name of the TypeScript type/class (without .ts extension).
     * @return a {@link Writer} to which the source code should be written.
     * @throws IOException if an issue occurs when creating the file.
     */
    @Override
    public Writer createOutput(final String name) throws IOException
    {
        final String fileName = name + ".ts";
        final File targetFile = new File(outputDir, fileName);
        generatedFiles.add(name);
        return Files.newBufferedWriter(targetFile.toPath(), UTF_8);
    }

    /**
     * Generate an index.ts barrel file that exports all generated TypeScript modules.
     * This provides a convenient single entry point for importing all generated types.
     *
     * @throws IOException if an issue occurs when creating the file.
     */
    public void generateIndexFile() throws IOException
    {
        if (generatedFiles.isEmpty())
        {
            return;
        }

        final File indexFile = new File(outputDir, "index.ts");
        try (Writer writer = Files.newBufferedWriter(indexFile.toPath(), UTF_8))
        {
            writer.write("/**\n");
            writer.write(" * Barrel export file for all generated SBE types\n");
            writer.write(" * Auto-generated - DO NOT EDIT\n");
            writer.write(" */\n\n");

            for (final String fileName : generatedFiles)
            {
                writer.write("export * from './" + fileName + "';\n");
            }
        }
    }

    /**
     * Get the output directory path.
     *
     * @return the output directory.
     */
    public File getOutputDir()
    {
        return outputDir;
    }

    /**
     * Get the list of generated file names (without .ts extension).
     *
     * @return list of generated file names.
     */
    public List<String> getGeneratedFiles()
    {
        return new ArrayList<>(generatedFiles);
    }
}
