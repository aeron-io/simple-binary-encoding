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
package uk.co.real_logic.sbe;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SbeToolTest
{
    @AfterEach
    void restoreSystemProperties()
    {
        System.clearProperty(SbeTool.VALIDATION_STOP_ON_ERROR);
        System.clearProperty(SbeTool.VALIDATION_WARNINGS_FATAL);
        System.clearProperty(SbeTool.VALIDATION_SUPPRESS_OUTPUT);
        System.clearProperty(SbeTool.GENERATE_STUBS);
    }

    @Test
    void shouldExitOnValidationErrorsWhenStopOnError() throws Exception
    {
        System.setProperty(SbeTool.VALIDATION_STOP_ON_ERROR, "true");
        System.setProperty(SbeTool.VALIDATION_SUPPRESS_OUTPUT, "true");
        System.setProperty(SbeTool.GENERATE_STUBS, "false");

        final AtomicInteger capturedCode = new AtomicInteger(0);
        SbeTool.run(new String[]{ schemaPath("error-handler-types-schema.xml") }, capturedCode::set);

        assertEquals(-1, capturedCode.get());
    }

    @Test
    void shouldExitOnValidationErrorsWithoutStopOnError() throws Exception
    {
        System.setProperty(SbeTool.VALIDATION_SUPPRESS_OUTPUT, "true");
        System.setProperty(SbeTool.GENERATE_STUBS, "false");

        final AtomicInteger capturedCode = new AtomicInteger(0);
        SbeTool.run(new String[]{ schemaPath("error-handler-types-schema.xml") }, capturedCode::set);

        assertEquals(-1, capturedCode.get());
    }

    @Test
    void shouldExitOnWarningsWhenWarningsFatal() throws Exception
    {
        System.setProperty(SbeTool.VALIDATION_WARNINGS_FATAL, "true");
        System.setProperty(SbeTool.VALIDATION_SUPPRESS_OUTPUT, "true");
        System.setProperty(SbeTool.GENERATE_STUBS, "false");

        final AtomicInteger capturedCode = new AtomicInteger(0);
        SbeTool.run(new String[]{ schemaPath("error-handler-types-dup-schema.xml") }, capturedCode::set);

        assertEquals(-1, capturedCode.get());
    }

    private static String schemaPath(final String resourceName) throws Exception
    {
        final URL url = Tests.class.getClassLoader().getResource(resourceName);
        if (url == null)
        {
            throw new IllegalArgumentException("resource not found: " + resourceName);
        }
        return Paths.get(url.toURI()).toString();
    }
}
