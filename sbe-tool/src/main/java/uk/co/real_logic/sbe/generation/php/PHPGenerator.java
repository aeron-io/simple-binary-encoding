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
package uk.co.real_logic.sbe.generation.php;

import uk.co.real_logic.sbe.generation.CodeGenerator;
import org.agrona.generation.OutputManager;
import uk.co.real_logic.sbe.ir.*;
import org.agrona.Verify;

import java.io.IOException;
import java.io.Writer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static uk.co.real_logic.sbe.generation.php.PHPUtil.*;
import static uk.co.real_logic.sbe.ir.GenerationUtil.collectVarData;
import static uk.co.real_logic.sbe.ir.GenerationUtil.collectGroups;
import static uk.co.real_logic.sbe.ir.GenerationUtil.collectFields;

/**
 * Codec generator for the PHP programming language.
 */
@SuppressWarnings("MethodLength")
public class PHPGenerator implements CodeGenerator
{
    private final Ir ir;
    private final OutputManager outputManager;

    /**
     * Create a new PHP language {@link CodeGenerator}.
     *
     * @param ir            for the messages and types.
     * @param outputManager for generating the codecs to.
     */
    public PHPGenerator(final Ir ir, final OutputManager outputManager)
    {
        Verify.notNull(ir, "ir");
        Verify.notNull(outputManager, "outputManager");

        this.ir = ir;
        this.outputManager = outputManager;
    }

    /**
     * {@inheritDoc}
     */
    public void generate() throws IOException
    {
        generateTypeStubs();

        for (final List<Token> tokens : ir.messages())
        {
            final Token msgToken = tokens.get(0);
            final String className = formatClassName(msgToken.name());

            try (Writer out = outputManager.createOutput(className))
            {
                final StringBuilder sb = new StringBuilder();

                generateFileHeader(sb);
                generateMessageClass(sb, className, tokens);

                out.append(sb);
            }
        }
    }

    private void generateFileHeader(final StringBuilder sb)
    {
        sb.append("<?php\n");
        sb.append("/**\n");
        sb.append(" * Generated SBE (Simple Binary Encoding) message codec.\n");
        sb.append(" */\n\n");
    }

    private void generateTypeStubs() throws IOException
    {
        for (final List<Token> tokens : ir.types())
        {
            switch (tokens.get(0).signal())
            {
                case BEGIN_ENUM:
                    generateEnum(tokens);
                    break;

                case BEGIN_SET:
                    generateChoiceSet(tokens);
                    break;

                case BEGIN_COMPOSITE:
                    generateComposite(tokens);
                    break;

                default:
                    break;
            }
        }
    }

    private void generateEnum(final List<Token> tokens) throws IOException
    {
        final Token enumToken = tokens.get(0);
        final String enumName = formatClassName(enumToken.applicableTypeName());

        try (Writer out = outputManager.createOutput(enumName))
        {
            final StringBuilder sb = new StringBuilder();

            generateFileHeader(sb);

            sb.append(String.format("class %s\n{\n", enumName));

            for (int i = 1; i < tokens.size() - 1; i++)
            {
                final Token token = tokens.get(i);
                final String name = formatPropertyName(token.name()).toUpperCase();
                final String value = token.encoding().constValue().toString();
                sb.append(String.format("    public const %s = %s;\n", name, value));
            }

            // Add null value
            final String nullValue = enumToken.encoding().applicableNullValue().toString();
            sb.append(String.format("    public const NULL_VALUE = %s;\n\n", nullValue));

            // Add size method
            sb.append("    public static function encodedLength(): int\n");
            sb.append("    {\n");
            sb.append(String.format("        return %d;\n", enumToken.encodedLength()));
            sb.append("    }\n");
            sb.append("}\n");

            out.append(sb);
        }
    }

    private void generateChoiceSet(final List<Token> tokens) throws IOException
    {
        final Token choiceToken = tokens.get(0);
        final String choiceName = formatClassName(choiceToken.applicableTypeName());

        try (Writer out = outputManager.createOutput(choiceName))
        {
            final StringBuilder sb = new StringBuilder();

            generateFileHeader(sb);

            sb.append(String.format("class %s\n{\n", choiceName));

            for (int i = 1; i < tokens.size() - 1; i++)
            {
                final Token token = tokens.get(i);
                final String name = formatPropertyName(token.name()).toUpperCase();
                final String value = token.encoding().constValue().toString();
                sb.append(String.format("    public const %s = %s;\n", name, value));
            }

            sb.append("\n");
            sb.append("    public static function encodedLength(): int\n");
            sb.append("    {\n");
            sb.append(String.format("        return %d;\n", choiceToken.encodedLength()));
            sb.append("    }\n");
            sb.append("}\n");

            out.append(sb);
        }
    }

    private void generateComposite(final List<Token> tokens) throws IOException
    {
        final Token compositeToken = tokens.get(0);
        final String compositeName = formatClassName(compositeToken.applicableTypeName());

        try (Writer out = outputManager.createOutput(compositeName))
        {
            final StringBuilder sb = new StringBuilder();

            generateFileHeader(sb);

            sb.append(String.format("class %s\n{\n", compositeName));

            // Declare properties
            for (int i = 1; i < tokens.size() - 1; i++)
            {
                final Token token = tokens.get(i);
                if (token.signal() == Signal.ENCODING)
                {
                    final String propertyName = formatPropertyName(token.name());
                    if (token.arrayLength() > 1)
                    {
                        sb.append(String.format("    public array $%s = [];\n", propertyName));
                    }
                    else
                    {
                        sb.append(String.format("    public int|float $%s = 0;\n", propertyName));
                    }
                }
            }

            sb.append("\n");
            generateCompositeEncodeDecode(sb, compositeName, tokens);

            out.append(sb);
        }
    }

    private void generateCompositeEncodeDecode(
        final StringBuilder sb,
        final String compositeName,
        final List<Token> tokens)
    {
        final String byteOrder = ir.byteOrder() == ByteOrder.LITTLE_ENDIAN ? "<" : ">";

        // Encode method
        sb.append("    public function encode(): string\n");
        sb.append("    {\n");
        sb.append("        $buffer = '';\n\n");

        for (int i = 1; i < tokens.size() - 1; i++)
        {
            final Token token = tokens.get(i);
            if (token.signal() == Signal.ENCODING)
            {
                final String propertyName = formatPropertyName(token.name());
                final String packFormat = phpPackFormat(token.encoding().primitiveType());

                if (token.arrayLength() > 1)
                {
                    sb.append(String.format(
                        "        foreach ($this->%s as $val) {\n" +
                        "            $buffer .= pack('%s', $val);\n" +
                        "        }\n",
                        propertyName, packFormat));
                }
                else
                {
                    sb.append(String.format(
                        "        $buffer .= pack('%s', $this->%s);\n",
                        packFormat, propertyName));
                }
            }
        }

        sb.append("\n        return $buffer;\n");
        sb.append("    }\n\n");

        // Decode method
        sb.append("    public function decode(string $data, int &$offset = 0): void\n");
        sb.append("    {\n");

        for (int i = 1; i < tokens.size() - 1; i++)
        {
            final Token token = tokens.get(i);
            if (token.signal() == Signal.ENCODING)
            {
                final String propertyName = formatPropertyName(token.name());
                final String packFormat = phpPackFormat(token.encoding().primitiveType());
                final int size = primitiveTypeSize(token.encoding().primitiveType());

                if (token.arrayLength() > 1)
                {
                    sb.append(String.format(
                        "        $this->%s = [];\n" +
                        "        for ($i = 0; $i < %d; $i++) {\n" +
                        "            $this->%s[] = unpack('%s', substr($data, $offset, %d))[1];\n" +
                        "            $offset += %d;\n" +
                        "        }\n",
                        propertyName, token.arrayLength(), propertyName, packFormat, size, size));
                }
                else
                {
                    sb.append(String.format(
                        "        $this->%s = unpack('%s', substr($data, $offset, %d))[1];\n" +
                        "        $offset += %d;\n",
                        propertyName, packFormat, size, size));
                }
            }
        }

        sb.append("    }\n\n");

        sb.append("    public static function encodedLength(): int\n");
        sb.append("    {\n");
        sb.append(String.format("        return %d;\n", tokens.get(0).encodedLength()));
        sb.append("    }\n");
        sb.append("}\n");
    }

    private void generateMessageClass(
        final StringBuilder sb,
        final String className,
        final List<Token> tokens)
    {
        final Token msgToken = tokens.get(0);

        sb.append(String.format("class %s\n{\n", className));
        sb.append(String.format("    public const TEMPLATE_ID = %d;\n", msgToken.id()));
        sb.append(String.format("    public const SCHEMA_ID = %d;\n", ir.id()));
        sb.append(String.format("    public const SCHEMA_VERSION = %d;\n", ir.version()));
        sb.append(String.format("    public const BLOCK_LENGTH = %d;\n\n", msgToken.encodedLength()));

        final List<Token> messageBody = tokens.subList(1, tokens.size() - 1);
        final List<Token> fields = new ArrayList<>();
        int i = collectFields(messageBody, 0, fields);

        // Declare properties
        for (final Token fieldToken : fields)
        {
            if (fieldToken.signal() == Signal.BEGIN_FIELD)
            {
                final String propertyName = formatPropertyName(fieldToken.name());
                sb.append(String.format("    public int|float|array|null $%s = null;\n", propertyName));
            }
        }

        final List<Token> groups = new ArrayList<>();
        i = collectGroups(messageBody, i, groups);

        for (int j = 0; j < groups.size();)
        {
            final Token groupToken = groups.get(j);
            if (groupToken.signal() == Signal.BEGIN_GROUP)
            {
                final String propertyName = formatPropertyName(groupToken.name());
                sb.append(String.format("    public array $%s = [];\n", propertyName));
                j += groupToken.componentTokenCount();
            }
            else
            {
                j++;
            }
        }

        final List<Token> varData = new ArrayList<>();
        collectVarData(messageBody, i, varData);

        for (int j = 0; j < varData.size();)
        {
            final Token varDataToken = varData.get(j);
            if (varDataToken.signal() == Signal.BEGIN_VAR_DATA)
            {
                final String propertyName = formatPropertyName(varDataToken.name());
                sb.append(String.format("    public string $%s = '';\n", propertyName));
                j += varDataToken.componentTokenCount();
            }
            else
            {
                j++;
            }
        }

        sb.append("\n");

        generateMessageEncodeDecode(sb, className, tokens, fields, groups, varData);
    }

    private void generateMessageEncodeDecode(
        final StringBuilder sb,
        final String className,
        final List<Token> tokens,
        final List<Token> fields,
        final List<Token> groups,
        final List<Token> varData)
    {
        final String byteOrder = ir.byteOrder() == ByteOrder.LITTLE_ENDIAN ? "<" : ">";

        // Encode method
        sb.append("    public function encode(): string\n");
        sb.append("    {\n");
        sb.append("        $buffer = '';\n\n");

        // Encode fields
        for (int i = 0; i < fields.size(); i++)
        {
            final Token fieldToken = fields.get(i);
            if (fieldToken.signal() == Signal.BEGIN_FIELD && i + 1 < fields.size())
            {
                final Token encodingToken = fields.get(i + 1);
                if (encodingToken.signal() == Signal.ENCODING)
                {
                    final String propertyName = formatPropertyName(fieldToken.name());
                    final String packFormat = phpPackFormat(encodingToken.encoding().primitiveType());

                    if (encodingToken.arrayLength() > 1)
                    {
                        sb.append(String.format(
                            "        if ($this->%s !== null) {\n" +
                            "            foreach ($this->%s as $val) {\n" +
                            "                $buffer .= pack('%s', $val);\n" +
                            "            }\n" +
                            "        }\n",
                            propertyName, propertyName, packFormat));
                    }
                    else
                    {
                        sb.append(String.format(
                            "        if ($this->%s !== null) {\n" +
                            "            $buffer .= pack('%s', $this->%s);\n" +
                            "        }\n",
                            propertyName, packFormat, propertyName));
                    }
                }
                i++;  // Skip encoding token
            }
        }

        sb.append("\n        return $buffer;\n");
        sb.append("    }\n\n");

        // Decode method
        sb.append("    public function decode(string $data): void\n");
        sb.append("    {\n");
        sb.append("        $offset = 0;\n\n");

        // Decode fields
        for (int i = 0; i < fields.size(); i++)
        {
            final Token fieldToken = fields.get(i);
            if (fieldToken.signal() == Signal.BEGIN_FIELD && i + 1 < fields.size())
            {
                final Token encodingToken = fields.get(i + 1);
                if (encodingToken.signal() == Signal.ENCODING)
                {
                    final String propertyName = formatPropertyName(fieldToken.name());
                    final String packFormat = phpPackFormat(encodingToken.encoding().primitiveType());
                    final int size = primitiveTypeSize(encodingToken.encoding().primitiveType());

                    if (encodingToken.arrayLength() > 1)
                    {
                        sb.append(String.format(
                            "        $this->%s = [];\n" +
                            "        for ($i = 0; $i < %d; $i++) {\n" +
                            "            $this->%s[] = unpack('%s', substr($data, $offset, %d))[1];\n" +
                            "            $offset += %d;\n" +
                            "        }\n",
                            propertyName, encodingToken.arrayLength(), propertyName, packFormat, size, size));
                    }
                    else
                    {
                        sb.append(String.format(
                            "        $this->%s = unpack('%s', substr($data, $offset, %d))[1];\n" +
                            "        $offset += %d;\n",
                            propertyName, packFormat, size, size));
                    }
                }
                i++;  // Skip encoding token
            }
        }

        sb.append("    }\n");
        sb.append("}\n");
    }
}
