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
package uk.co.real_logic.sbe.generation.c;

import org.agrona.generation.StringWriterOutputManager;
import org.junit.jupiter.api.Test;
import uk.co.real_logic.sbe.Tests;
import uk.co.real_logic.sbe.ir.Ir;
import uk.co.real_logic.sbe.xml.IrGenerator;
import uk.co.real_logic.sbe.xml.MessageSchema;
import uk.co.real_logic.sbe.xml.ParserOptions;

import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static uk.co.real_logic.sbe.xml.XmlSchemaParser.parse;

class CGeneratorTest
{
    @Test
    void shouldGenerateFloatingPointInfinityLiterals() throws Exception
    {
        try (InputStream in = Tests.getLocalResource("floating-point-infinity-schema.xml"))
        {
            final ParserOptions options = ParserOptions.builder().stopOnError(true).build();
            final MessageSchema schema = parse(in, options);
            final Ir ir = new IrGenerator().generate(schema);
            final StringWriterOutputManager outputManager = new StringWriterOutputManager();
            outputManager.setPackageName(ir.applicableNamespace());

            new CGenerator(ir, outputManager).generate();

            final String source = outputManager.getSources().values().stream()
                .map(CharSequence::toString)
                .collect(java.util.stream.Collectors.joining("\n"));
            assertThat(source, containsString("#define SBE_FLOAT_INFINITY ((float)INFINITY)"));
            assertThat(source, containsString("#define SBE_DOUBLE_INFINITY ((double)INFINITY)"));
            assertThat(source, containsString("SBE_FLOAT_INFINITY"));
            assertThat(source, containsString("-SBE_FLOAT_INFINITY"));
            assertThat(source, containsString("SBE_DOUBLE_INFINITY"));
            assertThat(source, containsString("-SBE_DOUBLE_INFINITY"));
        }
    }
}
