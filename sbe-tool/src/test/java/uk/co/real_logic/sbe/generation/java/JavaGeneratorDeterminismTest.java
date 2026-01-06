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
package uk.co.real_logic.sbe.generation.java;

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import org.agrona.generation.StringWriterOutputManager;
import org.junit.jupiter.api.Test;
import uk.co.real_logic.sbe.Tests;
import uk.co.real_logic.sbe.generation.common.PrecedenceChecks;
import uk.co.real_logic.sbe.ir.Ir;
import uk.co.real_logic.sbe.xml.IrGenerator;
import uk.co.real_logic.sbe.xml.MessageSchema;
import uk.co.real_logic.sbe.xml.ParserOptions;

import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.co.real_logic.sbe.xml.XmlSchemaParser.parse;

/**
 * Tests that generated code is deterministic when precedence checks are enabled.
 */
class JavaGeneratorDeterminismTest
{
    private static final String BUFFER_NAME = MutableDirectBuffer.class.getName();
    private static final String READ_ONLY_BUFFER_NAME = DirectBuffer.class.getName();

    @Test
    void shouldGenerateDeterministicOutputWithPrecedenceChecks() throws Exception
    {
        final Map<String, CharSequence> firstGeneration = generateWithPrecedenceChecks();
        final Map<String, CharSequence> secondGeneration = generateWithPrecedenceChecks();

        assertEquals(firstGeneration.keySet(), secondGeneration.keySet());

        for (final String fileName : firstGeneration.keySet())
        {
            final CharSequence firstSource = firstGeneration.get(fileName);
            final CharSequence secondSource = secondGeneration.get(fileName);
            assertEquals(firstSource, secondSource);
        }
    }

    private Map<String, CharSequence> generateWithPrecedenceChecks() throws Exception
    {
        try (InputStream in = Tests.getLocalResource("field-order-check-schema.xml"))
        {
            final ParserOptions options = ParserOptions.builder().stopOnError(true).build();
            final MessageSchema schema = parse(in, options);
            final IrGenerator irg = new IrGenerator();
            final Ir ir = irg.generate(schema);
            final StringWriterOutputManager outputManager = new StringWriterOutputManager();
            outputManager.setPackageName(ir.applicableNamespace());
            final PrecedenceChecks.Context context = new PrecedenceChecks.Context()
                .shouldGeneratePrecedenceChecks(true);
            final PrecedenceChecks precedenceChecks = PrecedenceChecks.newInstance(context);
            final JavaGenerator generator = new JavaGenerator(
                ir, BUFFER_NAME, READ_ONLY_BUFFER_NAME, false, false, false, false, precedenceChecks, outputManager);
            generator.generate();
            return new TreeMap<>(outputManager.getSources());
        }
    }
}
