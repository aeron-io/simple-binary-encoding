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
package uk.co.real_logic.sbe.generation.cpp;

import org.agrona.generation.StringWriterOutputManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceLock;
import org.junit.jupiter.api.parallel.Resources;
import uk.co.real_logic.sbe.Tests;
import uk.co.real_logic.sbe.ir.Ir;
import uk.co.real_logic.sbe.xml.IrGenerator;
import uk.co.real_logic.sbe.xml.MessageSchema;
import uk.co.real_logic.sbe.xml.ParserOptions;

import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static uk.co.real_logic.sbe.xml.XmlSchemaParser.parse;

/**
 * Coverage for the {@code sbe.cpp.generate.enum.parse} option, which emits
 * {@code fromString()}/{@code operator>>} for enums (the inverse of {@code c_str()}/{@code operator<<}).
 */
@ResourceLock(Resources.SYSTEM_PROPERTIES)
class CppEnumParseGeneratorTest
{
    // Generate the schema under the given generator system-properties (restored afterwards) and
    // return the source of the "Model" enum.
    private String generateModelHeader(final String... properties) throws Exception
    {
        final Map<String, String> toRestore = new LinkedHashMap<>();
        // The GlobalKeywords message uses reserved words, so a keyword token is required.
        setProperty(toRestore, "sbe.keyword.append.token", "X");
        for (int i = 0; i < properties.length; i += 2)
        {
            setProperty(toRestore, properties[i], properties[i + 1]);
        }

        try (InputStream in = Tests.getLocalResource("code-generation-schema.xml"))
        {
            final ParserOptions options = ParserOptions.builder().stopOnError(true).build();
            final MessageSchema schema = parse(in, options);
            final Ir ir = new IrGenerator().generate(schema);
            final StringWriterOutputManager outputManager = new StringWriterOutputManager();
            outputManager.setPackageName(ir.applicableNamespace());

            new CppGenerator(ir, false, outputManager).generate();

            return outputManager.getSource("code.generation.test.Model").toString();
        }
        finally
        {
            for (final Map.Entry<String, String> e : toRestore.entrySet())
            {
                if (null == e.getValue())
                {
                    System.clearProperty(e.getKey());
                }
                else
                {
                    System.setProperty(e.getKey(), e.getValue());
                }
            }
        }
    }

    private static void setProperty(final Map<String, String> toRestore, final String key, final String value)
    {
        toRestore.putIfAbsent(key, System.getProperty(key));
        System.setProperty(key, value);
    }

    @Test
    void doesNotEmitEnumParsingByDefault() throws Exception
    {
        final String model = generateModelHeader();
        assertThat(model, not(containsString("fromString")));
        assertThat(model, not(containsString("operator >> (")));
        assertThat(model, not(containsString("#include <unordered_map>")));
    }

    @Test
    void emitsFromStringAndInputOperatorWhenEnabled() throws Exception
    {
        final String model = generateModelHeader("sbe.cpp.generate.enum.parse", "true");

        assertThat(model, containsString("static Model::Value fromString(const char *str)"));
        assertThat(model, containsString("operator >> ("));
        // fromString() pulls in <unordered_map> for the inline reverse-lookup map
        assertThat(model, containsString("#include <unordered_map>"));
        // the reverse map covers the same names c_str()/operator<< emit
        assertThat(model, containsString("map[\"A\"] = A;"));
        assertThat(model, containsString("map[\"NULL_VALUE\"] = NULL_VALUE;"));
    }
}
