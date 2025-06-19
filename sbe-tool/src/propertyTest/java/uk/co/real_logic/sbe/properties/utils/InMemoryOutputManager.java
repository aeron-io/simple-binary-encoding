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
package uk.co.real_logic.sbe.properties.utils;

import org.agrona.generation.CompilerUtil;
import org.agrona.generation.DynamicPackageOutputManager;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

/**
 * An implementation of {@link DynamicPackageOutputManager} that stores generated source code in memory and compiles it
 * on demand.
 */
public class InMemoryOutputManager implements DynamicPackageOutputManager
{
    private final String packageName;
    private final Map<String, CharSequence> sources = new HashMap<>();
    private String packageNameOverride;

    public InMemoryOutputManager(final String packageName)
    {
        this.packageName = packageName;
    }

    public Writer createOutput(final String name)
    {
        return new InMemoryWriter(name);
    }

    public void setPackageName(final String packageName)
    {
        packageNameOverride = packageName;
    }

    /**
     * Compile the generated sources and return a {@link Class} matching the supplied fully-qualified name.
     *
     * @param fqClassName the fully-qualified class name to compile and load.
     * @return a {@link Class} matching the supplied fully-qualified name.
     */
    public Class<?> compileAndLoad(final String fqClassName)
    {
        try
        {
            return CompilerUtil.compileInMemory(fqClassName, sources);
        }
        catch (final Exception exception)
        {
            throw new RuntimeException(exception);
        }
    }

    public void dumpSources(final StringBuilder builder)
    {
        builder.append(System.lineSeparator()).append("Generated sources file count: ").append(sources.size())
            .append(System.lineSeparator());

        sources.forEach((qualifiedName, source) ->
        {
            builder.append(System.lineSeparator()).append("Source file: ").append(qualifiedName)
                .append(System.lineSeparator()).append(source)
                .append(System.lineSeparator());
        });
    }

    class InMemoryWriter extends StringWriter
    {
        private final String name;

        InMemoryWriter(final String name)
        {
            this.name = name;
        }

        public void close() throws IOException
        {
            super.close();
            final String actingPackageName = packageNameOverride == null ? packageName : packageNameOverride;
            packageNameOverride = null;

            final String qualifiedName = actingPackageName + "." + name;

            final String source = getBuffer().toString();
            final CharSequence existingSource = sources.putIfAbsent(qualifiedName, source);

            if (null != existingSource && 0 != CharSequence.compare(existingSource, source))
            {
                throw new IllegalStateException("Duplicate (but different) class: " + qualifiedName);
            }
        }
    }
}
