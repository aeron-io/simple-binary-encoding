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

import org.junit.jupiter.api.Test;
import uk.co.real_logic.sbe.Tests;
import uk.co.real_logic.sbe.ir.Ir;
import uk.co.real_logic.sbe.ir.Signal;
import uk.co.real_logic.sbe.ir.Token;
import uk.co.real_logic.sbe.xml.IrGenerator;
import uk.co.real_logic.sbe.xml.ParserOptions;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.co.real_logic.sbe.xml.XmlSchemaParser.parse;

class MinBlockLengthExpressionTest
{
    @Test
    void shouldNotCountCompositeMembersNewerThanActingVersion() throws Exception
    {
        final Ir ir;
        try (InputStream in = Tests.getLocalResource("group-with-versioned-composite.xml"))
        {
            ir = new IrGenerator().generate(parse(in, ParserOptions.builder().stopOnError(true).build()));
        }

        final List<Token> tokens = ir.getMessage(1);
        int groupIndex = 0;
        while (Signal.BEGIN_GROUP != tokens.get(groupIndex).signal())
        {
            groupIndex++;
        }

        final int fieldsIndex = groupIndex + 1 + tokens.get(groupIndex + 1).componentTokenCount();
        final int groupEndIndex = groupIndex + tokens.get(groupIndex).componentTokenCount() - 1;

        // The px composite is 8 bytes in version 0; its side member is added in version 1.
        assertEquals(
            "(actingVersion >= 1u ? 9u : 8)",
            Generators.minBlockLengthExpression(tokens.subList(fieldsIndex, groupEndIndex), "actingVersion"));
    }
}
