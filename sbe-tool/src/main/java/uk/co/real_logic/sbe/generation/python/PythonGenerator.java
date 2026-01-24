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
package uk.co.real_logic.sbe.generation.python;

import uk.co.real_logic.sbe.generation.CodeGenerator;
import org.agrona.generation.OutputManager;
import uk.co.real_logic.sbe.ir.*;
import org.agrona.Verify;

import java.io.IOException;
import java.io.Writer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static uk.co.real_logic.sbe.generation.python.PythonUtil.*;
import static uk.co.real_logic.sbe.ir.GenerationUtil.collectVarData;
import static uk.co.real_logic.sbe.ir.GenerationUtil.collectGroups;
import static uk.co.real_logic.sbe.ir.GenerationUtil.collectFields;

/**
 * Codec generator for the Python programming language.
 */
@SuppressWarnings("MethodLength")
public class PythonGenerator implements CodeGenerator
{
    private final Ir ir;
    private final OutputManager outputManager;

    /**
     * Create a new Python language {@link CodeGenerator}.
     *
     * @param ir            for the messages and types.
     * @param outputManager for generating the codecs to.
     */
    public PythonGenerator(final Ir ir, final OutputManager outputManager)
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
        generatePackageInit();
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

    private void generatePackageInit() throws IOException
    {
        try (Writer out = outputManager.createOutput("__init__"))
        {
            out.append("\"\"\"Generated SBE (Simple Binary Encoding) message codecs.\"\"\"\n");
        }
    }

    private void generateFileHeader(final StringBuilder sb)
    {
        sb.append("\"\"\"Generated SBE (Simple Binary Encoding) message codec.\"\"\"\n\n");
        sb.append("import struct\n");
        sb.append("from typing import List, Optional, Tuple, Set\n");
        sb.append("from io import BytesIO\n\n");
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

            sb.append(String.format("class %s:\n", enumName));
            sb.append("    \"\"\"Enum values.\"\"\"\n");

            for (int i = 1; i < tokens.size() - 1; i++)
            {
                final Token token = tokens.get(i);
                final String name = formatPropertyName(token.name()).toUpperCase();
                final String value = token.encoding().constValue().toString();
                sb.append(String.format("    %s = %s\n", name, value));
            }

            // Add null value
            final String nullValue = enumToken.encoding().applicableNullValue().toString();
            sb.append(String.format("    NULL_VALUE = %s\n\n", nullValue));

            // Add size method
            sb.append(String.format("    @staticmethod\n"));
            sb.append(String.format("    def encoded_length() -> int:\n"));
            sb.append(String.format("        return %d\n", enumToken.encodedLength()));

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

            sb.append(String.format("class %s:\n", choiceName));
            sb.append("    \"\"\"Choice set values.\"\"\"\n");

            for (int i = 1; i < tokens.size() - 1; i++)
            {
                final Token token = tokens.get(i);
                final String name = formatPropertyName(token.name()).toUpperCase();
                final String value = token.encoding().constValue().toString();
                sb.append(String.format("    %s = %s\n", name, value));
            }

            sb.append("\n");
            sb.append(String.format("    @staticmethod\n"));
            sb.append(String.format("    def encoded_length() -> int:\n"));
            sb.append(String.format("        return %d\n", choiceToken.encodedLength()));

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

            sb.append(String.format("class %s:\n", compositeName));
            sb.append("    \"\"\"Composite type.\"\"\"\n\n");
            sb.append("    def __init__(self):\n");

            // Initialize fields
            for (int i = 1; i < tokens.size() - 1; i++)
            {
                final Token token = tokens.get(i);
                if (token.signal() == Signal.ENCODING)
                {
                    final String propertyName = formatPropertyName(token.name());
                    if (token.arrayLength() > 1)
                    {
                        sb.append(String.format("        self.%s = [0] * %d\n", propertyName, token.arrayLength()));
                    }
                    else
                    {
                        sb.append(String.format("        self.%s = 0\n", propertyName));
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
        sb.append("    def encode(self, buffer: BytesIO) -> None:\n");
        sb.append("        \"\"\"Encode the composite to the buffer.\"\"\"\n");

        for (int i = 1; i < tokens.size() - 1; i++)
        {
            final Token token = tokens.get(i);
            if (token.signal() == Signal.ENCODING)
            {
                final String propertyName = formatPropertyName(token.name());
                final String structFormat = pythonStructFormat(token.encoding().primitiveType());

                if (token.arrayLength() > 1)
                {
                    sb.append(String.format(
                        "        for val in self.%s:\n" +
                        "            buffer.write(struct.pack('%s%s', val))\n",
                        propertyName, byteOrder, structFormat));
                }
                else
                {
                    sb.append(String.format(
                        "        buffer.write(struct.pack('%s%s', self.%s))\n",
                        byteOrder, structFormat, propertyName));
                }
            }
        }

        sb.append("\n");

        // Decode method
        sb.append("    def decode(self, buffer: BytesIO) -> None:\n");
        sb.append("        \"\"\"Decode the composite from the buffer.\"\"\"\n");

        for (int i = 1; i < tokens.size() - 1; i++)
        {
            final Token token = tokens.get(i);
            if (token.signal() == Signal.ENCODING)
            {
                final String propertyName = formatPropertyName(token.name());
                final String structFormat = pythonStructFormat(token.encoding().primitiveType());
                final int size = primitiveTypeSize(token.encoding().primitiveType());

                if (token.arrayLength() > 1)
                {
                    sb.append(String.format(
                        "        self.%s = []\n" +
                        "        for _ in range(%d):\n" +
                        "            self.%s.append(struct.unpack('%s%s', buffer.read(%d))[0])\n",
                        propertyName, token.arrayLength(), propertyName, byteOrder, structFormat, size));
                }
                else
                {
                    sb.append(String.format(
                        "        self.%s = struct.unpack('%s%s', buffer.read(%d))[0]\n",
                        propertyName, byteOrder, structFormat, size));
                }
            }
        }

        sb.append("\n");
        sb.append(String.format("    @staticmethod\n"));
        sb.append(String.format("    def encoded_length() -> int:\n"));
        sb.append(String.format("        return %d\n", tokens.get(0).encodedLength()));
    }

    private void generateGroupClasses(
        final StringBuilder sb,
        final List<Token> groups)
    {
        for (int i = 0; i < groups.size();)
        {
            final Token groupToken = groups.get(i);
            if (groupToken.signal() == Signal.BEGIN_GROUP)
            {
                final String groupName = formatClassName(groupToken.name());
                final List<Token> groupTokens = new ArrayList<>();

                final int endIndex = i + groupToken.componentTokenCount();
                for (int j = i; j < endIndex; j++)
                {
                    groupTokens.add(groups.get(j));
                }

                final List<Token> groupBody = groupTokens.subList(1, groupTokens.size() - 1);

                sb.append(String.format("class %s:\n", groupName));
                sb.append("    \"\"\"Repeating group item.\"\"\"\n\n");
                sb.append("    def __init__(self):\n");

                final java.util.Set<String> seenFields = new java.util.HashSet<>();
                for (int j = 0; j < groupBody.size(); j++)
                {
                    final Token token = groupBody.get(j);
                    if (token.signal() == Signal.BEGIN_FIELD && j + 1 < groupBody.size())
                    {
                        final Token encodingToken = groupBody.get(j + 1);
                        if (encodingToken.signal() == Signal.ENCODING)
                        {
                            final String propName = formatPropertyName(token.name());
                            if (seenFields.add(propName))
                            {
                                sb.append(String.format("        self.%s = None\n", propName));
                            }
                        }
                    }
                }

                sb.append("\n");

                i = endIndex;
            }
            else
            {
                i++;
            }
        }
    }

    private void generateMessageClass(
        final StringBuilder sb,
        final String className,
        final List<Token> tokens)
    {
        final Token msgToken = tokens.get(0);

        final List<Token> messageBody = tokens.subList(1, tokens.size() - 1);
        final List<Token> fields = new ArrayList<>();
        int i = collectFields(messageBody, 0, fields);

        final List<Token> groups = new ArrayList<>();
        i = collectGroups(messageBody, i, groups);

        final List<Token> varData = new ArrayList<>();
        collectVarData(messageBody, i, varData);

        // Generate group classes first
        generateGroupClasses(sb, groups);

        sb.append(String.format("class %s:\n", className));
        sb.append(String.format("    \"\"\"SBE message: %s.\"\"\"\n\n", msgToken.name()));
        sb.append(String.format("    TEMPLATE_ID = %d\n", msgToken.id()));
        sb.append(String.format("    SCHEMA_ID = %d\n", ir.id()));
        sb.append(String.format("    SCHEMA_VERSION = %d\n", ir.version()));
        sb.append(String.format("    BLOCK_LENGTH = %d\n\n", msgToken.encodedLength()));

        // Constructor
        sb.append("    def __init__(self):\n");

        // Initialize fields
        for (final Token fieldToken : fields)
        {
            if (fieldToken.signal() == Signal.BEGIN_FIELD)
            {
                final String propertyName = formatPropertyName(fieldToken.name());
                sb.append(String.format("        self.%s = None\n", propertyName));
            }
        }

        for (int j = 0; j < groups.size();)
        {
            final Token groupToken = groups.get(j);
            if (groupToken.signal() == Signal.BEGIN_GROUP)
            {
                final String propertyName = formatPropertyName(groupToken.name());
                sb.append(String.format("        self.%s = []\n", propertyName));
                j += groupToken.componentTokenCount();
            }
            else
            {
                j++;
            }
        }

        for (int j = 0; j < varData.size();)
        {
            final Token varDataToken = varData.get(j);
            if (varDataToken.signal() == Signal.BEGIN_VAR_DATA)
            {
                final String propertyName = formatPropertyName(varDataToken.name());
                sb.append(String.format("        self.%s = b''\n", propertyName));
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

    private void generateGroupDecoderMethods(
        final StringBuilder sb,
        final List<Token> groups,
        final String byteOrder)
    {
        for (int i = 0; i < groups.size();)
        {
            final Token groupToken = groups.get(i);
            if (groupToken.signal() == Signal.BEGIN_GROUP)
            {
                final String groupName = formatClassName(groupToken.name());
                final List<Token> groupTokens = new ArrayList<>();

                final int endIndex = i + groupToken.componentTokenCount();
                for (int j = i; j < endIndex; j++)
                {
                    groupTokens.add(groups.get(j));
                }

                final List<Token> groupBody = groupTokens.subList(1, groupTokens.size() - 1);

                final Token blockLengthToken = uk.co.real_logic.sbe.generation.Generators.findFirst(
                    "blockLength", groupTokens, 0);
                final Token numInGroupToken = uk.co.real_logic.sbe.generation.Generators.findFirst(
                    "numInGroup", groupTokens, 0);

                final String blockLengthFormat = pythonStructFormat(blockLengthToken.encoding().primitiveType());
                final String numInGroupFormat = pythonStructFormat(numInGroupToken.encoding().primitiveType());
                final int blockLengthSize = primitiveTypeSize(blockLengthToken.encoding().primitiveType());
                final int numInGroupSize = primitiveTypeSize(numInGroupToken.encoding().primitiveType());

                sb.append(String.format(
                    "    def _decode_%s_group(self, data: bytes, offset: int) -> Tuple[List[%s], int]:\n",
                    formatPropertyName(groupToken.name()), groupName));
                sb.append("        \"\"\"Decode repeating group.\"\"\"\n");
                sb.append("        pos = offset\n\n");

                sb.append(String.format(
                    "        block_length = struct.unpack_from('%s%s', data, pos)[0]\n",
                    byteOrder, blockLengthFormat));
                sb.append(String.format("        pos += %d\n", blockLengthSize));
                sb.append(String.format(
                    "        num_in_group = struct.unpack_from('%s%s', data, pos)[0]\n",
                    byteOrder, numInGroupFormat));
                sb.append(String.format("        pos += %d\n\n", numInGroupSize));

                sb.append("        items = []\n");
                sb.append("        for _ in range(num_in_group):\n");
                sb.append("            item_start = pos\n");
                sb.append(String.format("            item = %s()\n\n", groupName));

                final java.util.Set<String> seenFields = new java.util.HashSet<>();
                for (int j = 0; j < groupBody.size(); j++)
                {
                    final Token fieldToken = groupBody.get(j);
                    if (fieldToken.signal() == Signal.BEGIN_FIELD && j + 1 < groupBody.size())
                    {
                        final Token encodingToken = groupBody.get(j + 1);
                        if (encodingToken.signal() == Signal.ENCODING)
                        {
                            final String propName = formatPropertyName(fieldToken.name());
                            final String structFormat = pythonStructFormat(encodingToken.encoding().primitiveType());
                            final int size = primitiveTypeSize(encodingToken.encoding().primitiveType());

                            if (seenFields.add(propName))
                            {
                                sb.append(String.format(
                                    "            item.%s = struct.unpack_from('%s%s', data, pos)[0]\n",
                                    propName, byteOrder, structFormat));
                                sb.append(String.format("            pos += %d\n", size));
                            }
                            else
                            {
                                sb.append(String.format("            pos += %d  # Skip duplicate field\n", size));
                            }
                        }
                        j++;
                    }
                }

                sb.append("\n            # Skip to next block for forward compatibility\n");
                sb.append("            pos = item_start + block_length\n");
                sb.append("            items.append(item)\n\n");

                sb.append("        return (items, pos)\n\n");

                i = endIndex;
            }
            else
            {
                i++;
            }
        }
    }

    private void generateVarDataDecoderMethods(final StringBuilder sb, final List<Token> varData)
    {
        boolean needsVarData = false;
        boolean needsUtf8 = false;
        boolean needsAscii = false;

        for (int i = 0; i < varData.size();)
        {
            final Token varDataToken = varData.get(i);
            if (varDataToken.signal() == Signal.BEGIN_VAR_DATA)
            {
                final Encoding encoding = varDataToken.encoding();
                final String characterEncoding = encoding.characterEncoding();

                if (null == characterEncoding)
                {
                    needsVarData = true;
                }
                else if ("UTF-8".equals(characterEncoding))
                {
                    needsUtf8 = true;
                }
                else
                {
                    needsAscii = true;
                }

                i += varDataToken.componentTokenCount();
            }
            else
            {
                i++;
            }
        }

        if (needsVarData)
        {
            sb.append("    def _decode_var_data(self, data: bytes, offset: int) -> Tuple[bytes, int]:\n");
            sb.append("        \"\"\"Decode variable-length binary data.\"\"\"\n");
            sb.append("        pos = offset\n");
            sb.append("        length = struct.unpack_from('<I', data, pos)[0]\n");
            sb.append("        pos += 4\n");
            sb.append("        value = data[pos:pos+length]\n");
            sb.append("        pos += length\n");
            sb.append("        return (value, pos)\n\n");
        }

        if (needsUtf8)
        {
            sb.append("    def _decode_var_string_utf8(self, data: bytes, offset: int) -> Tuple[str, int]:\n");
            sb.append("        \"\"\"Decode variable-length UTF-8 string.\"\"\"\n");
            sb.append("        pos = offset\n");
            sb.append("        length = struct.unpack_from('<I', data, pos)[0]\n");
            sb.append("        pos += 4\n");
            sb.append("        value = data[pos:pos+length].decode('utf-8')\n");
            sb.append("        pos += length\n");
            sb.append("        return (value, pos)\n\n");
        }

        if (needsAscii)
        {
            sb.append("    def _decode_var_string_ascii(self, data: bytes, offset: int) -> Tuple[str, int]:\n");
            sb.append("        \"\"\"Decode variable-length ASCII string.\"\"\"\n");
            sb.append("        pos = offset\n");
            sb.append("        length = struct.unpack_from('<I', data, pos)[0]\n");
            sb.append("        pos += 4\n");
            sb.append("        value = data[pos:pos+length].decode('ascii')\n");
            sb.append("        pos += length\n");
            sb.append("        return (value, pos)\n\n");
        }
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
        final Token msgToken = tokens.get(0);

        // Generate group decoder methods
        generateGroupDecoderMethods(sb, groups, byteOrder);

        // Generate variable data decoder methods
        generateVarDataDecoderMethods(sb, varData);

        // Encode method
        sb.append("    def encode(self) -> bytes:\n");
        sb.append("        \"\"\"Encode the message to bytes.\"\"\"\n");
        sb.append("        buffer = BytesIO()\n\n");

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
                    final String structFormat = pythonStructFormat(encodingToken.encoding().primitiveType());

                    if (encodingToken.arrayLength() > 1)
                    {
                        sb.append(String.format(
                            "        if self.%s is not None:\n" +
                            "            for val in self.%s:\n" +
                            "                buffer.write(struct.pack('%s%s', val))\n",
                            propertyName, propertyName, byteOrder, structFormat));
                    }
                    else
                    {
                        sb.append(String.format(
                            "        if self.%s is not None:\n" +
                            "            buffer.write(struct.pack('%s%s', self.%s))\n",
                            propertyName, byteOrder, structFormat, propertyName));
                    }
                }
                i++;  // Skip encoding token
            }
        }

        sb.append("\n        return buffer.getvalue()\n\n");

        // Decode method
        sb.append("    def decode(self, data: bytes) -> None:\n");
        sb.append("        \"\"\"Decode the message from bytes.\"\"\"\n");
        sb.append("        pos = 0\n\n");

        // Decode fields with duplicate detection
        final java.util.Set<String> seenFieldNames = new java.util.HashSet<>();
        for (int i = 0; i < fields.size(); i++)
        {
            final Token fieldToken = fields.get(i);
            if (fieldToken.signal() == Signal.BEGIN_FIELD && i + 1 < fields.size())
            {
                final Token encodingToken = fields.get(i + 1);
                if (encodingToken.signal() == Signal.ENCODING)
                {
                    final String propertyName = formatPropertyName(fieldToken.name());
                    final String structFormat = pythonStructFormat(encodingToken.encoding().primitiveType());
                    final int size = primitiveTypeSize(encodingToken.encoding().primitiveType());

                    if (seenFieldNames.add(propertyName))
                    {
                        if (encodingToken.arrayLength() > 1)
                        {
                            sb.append(String.format(
                                "        self.%s = []\n" +
                                "        for _ in range(%d):\n" +
                                "            self.%s.append(struct.unpack_from('%s%s', data, pos)[0])\n" +
                                "            pos += %d\n",
                                propertyName, encodingToken.arrayLength(), propertyName, byteOrder,
                                structFormat, size));
                        }
                        else
                        {
                            sb.append(String.format(
                                "        self.%s = struct.unpack_from('%s%s', data, pos)[0]\n",
                                propertyName, byteOrder, structFormat));
                            sb.append(String.format("        pos += %d\n", size));
                        }
                    }
                    else
                    {
                        if (encodingToken.arrayLength() > 1)
                        {
                            sb.append(String.format(
                                "        pos += %d  # Skip duplicate field array\n",
                                size * encodingToken.arrayLength()));
                        }
                        else
                        {
                            sb.append(String.format("        pos += %d  # Skip duplicate field\n", size));
                        }
                    }
                }
                i++;  // Skip encoding token
            }
        }

        // Skip to block end for forward compatibility
        sb.append("\n        # Skip to end of block for forward compatibility\n");
        sb.append(String.format("        pos = %d\n\n", msgToken.encodedLength()));

        // Decode groups
        for (int j = 0; j < groups.size();)
        {
            final Token groupToken = groups.get(j);
            if (groupToken.signal() == Signal.BEGIN_GROUP)
            {
                final String propertyName = formatPropertyName(groupToken.name());

                if (seenFieldNames.add(propertyName))
                {
                    sb.append(String.format("        self.%s, pos = self._decode_%s_group(data, pos)\n",
                        propertyName, propertyName));
                }
                else
                {
                    sb.append(String.format("        _, pos = self._decode_%s_group(data, pos)  " +
                        "# Skip duplicate group\n", propertyName));
                }

                j += groupToken.componentTokenCount();
            }
            else
            {
                j++;
            }
        }

        // Decode variable-length data
        if (!varData.isEmpty())
        {
            sb.append("\n");
        }

        for (int j = 0; j < varData.size();)
        {
            final Token varDataToken = varData.get(j);
            if (varDataToken.signal() == Signal.BEGIN_VAR_DATA)
            {
                final String propertyName = formatPropertyName(varDataToken.name());
                final Encoding encoding = varDataToken.encoding();
                final String characterEncoding = encoding.characterEncoding();

                if (seenFieldNames.add(propertyName))
                {
                    if (null == characterEncoding)
                    {
                        sb.append(String.format("        self.%s, pos = self._decode_var_data(data, pos)\n",
                            propertyName));
                    }
                    else if ("UTF-8".equals(characterEncoding))
                    {
                        sb.append(String.format("        self.%s, pos = self._decode_var_string_utf8(data, pos)\n",
                            propertyName));
                    }
                    else
                    {
                        sb.append(String.format("        self.%s, pos = self._decode_var_string_ascii(data, pos)\n",
                            propertyName));
                    }
                }
                else
                {
                    if (null == characterEncoding)
                    {
                        sb.append(String.format("        _, pos = self._decode_var_data(data, pos)  " +
                            "# Skip duplicate vardata\n"));
                    }
                    else if ("UTF-8".equals(characterEncoding))
                    {
                        sb.append(String.format("        _, pos = self._decode_var_string_utf8(data, pos)  " +
                            "# Skip duplicate vardata\n"));
                    }
                    else
                    {
                        sb.append(String.format("        _, pos = self._decode_var_string_ascii(data, pos)  " +
                            "# Skip duplicate vardata\n"));
                    }
                }

                j += varDataToken.componentTokenCount();
            }
            else
            {
                j++;
            }
        }
    }
}
