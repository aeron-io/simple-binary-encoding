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

import java.io.File;

/**
 * Checks on the names of files the code generators write.
 */
public final class OutputFiles
{
    private OutputFiles()
    {
    }

    /**
     * Check that a name taken from the schema can only name a file directly inside the output directory. Schema
     * names are not guaranteed to be identifiers, so a name such as {@code ../../X} would otherwise make a
     * generator write outside the directory it was given.
     *
     * @param name of the file to be created, without its extension.
     * @return the name.
     * @throws IllegalArgumentException if the name is empty or contains a path separator or drive separator.
     */
    public static String checkFileName(final String name)
    {
        if (null == name || name.isEmpty())
        {
            throw new IllegalArgumentException("output file name must not be empty");
        }

        for (int i = 0, length = name.length(); i < length; i++)
        {
            final char c = name.charAt(i);
            if ('/' == c || '\\' == c || ':' == c || File.separatorChar == c || '\0' == c)
            {
                throw new IllegalArgumentException("output file name must not contain a path: " + name);
            }
        }

        return name;
    }
}
