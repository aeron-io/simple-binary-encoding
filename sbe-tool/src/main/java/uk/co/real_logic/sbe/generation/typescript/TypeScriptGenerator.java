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
import uk.co.real_logic.sbe.PrimitiveType;
import uk.co.real_logic.sbe.generation.CodeGenerator;
import uk.co.real_logic.sbe.generation.Generators;
import uk.co.real_logic.sbe.ir.*;

import java.io.IOException;
import java.io.Writer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

import static uk.co.real_logic.sbe.generation.typescript.TypeScriptUtil.*;
import static uk.co.real_logic.sbe.ir.GenerationUtil.*;

/**
 * Code generator for the TypeScript programming language.
 * Generates decoder classes and interfaces for decoding SBE binary messages.
 */
@SuppressWarnings("MethodLength")
public class TypeScriptGenerator implements CodeGenerator
{
    private final Ir ir;
    private final TypeScriptOutputManager outputManager;

    /**
     * Create a new TypeScript {@link CodeGenerator}.
     *
     * @param ir            for the messages and types.
     * @param outputManager for generating the code to.
     */
    public TypeScriptGenerator(final Ir ir, final OutputManager outputManager)
    {
        Verify.notNull(ir, "ir");
        Verify.notNull(outputManager, "outputManager");

        this.ir = ir;
        this.outputManager = (TypeScriptOutputManager)outputManager;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void generate() throws IOException
    {
        generateTypeStubs();
        generateMessageHeaderStub();

        for (final List<Token> tokens : ir.messages())
        {
            generateMessage(tokens);
        }

        outputManager.generateIndexFile();
    }

    /**
     * Generate the stubs for the types used as message fields.
     *
     * @throws IOException if an error is encountered when writing the output.
     */
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
                    generateBitSet(tokens);
                    break;

                case BEGIN_COMPOSITE:
                    generateComposite(tokens);
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * Generate the message header stub.
     *
     * @throws IOException if an error is encountered when writing the output.
     */
    private void generateMessageHeaderStub() throws IOException
    {
        final String typeName = "MessageHeader";
        final List<Token> tokens = ir.headerStructure().tokens();

        try (Writer out = outputManager.createOutput(typeName))
        {
            final StringBuilder sb = new StringBuilder();
            sb.append(generateFileHeader(ir.packageName()));

            generateCompositeDecoder(sb, typeName, tokens.subList(1, tokens.size() - 1));

            out.append(sb);
        }
    }

    /**
     * Generate an enum type.
     *
     * @param tokens for the enum.
     * @throws IOException if an error is encountered when writing the output.
     */
    private void generateEnum(final List<Token> tokens) throws IOException
    {
        final Token enumToken = tokens.get(0);
        final String enumName = formatTypeName(enumToken.applicableTypeName());

        try (Writer out = outputManager.createOutput(enumName))
        {
            final StringBuilder sb = new StringBuilder();
            sb.append(generateFileHeader(ir.packageName()));

            sb.append(generateDocumentation("", enumToken));
            sb.append("export enum ").append(enumName).append(" {\n");

            for (int i = 1; i < tokens.size() - 1; i++)
            {
                final Token token = tokens.get(i);
                final String valueName = token.name();
                final String value = token.encoding().constValue().toString();

                sb.append(generateDocumentation("  ", token));
                sb.append("  ").append(valueName).append(" = ").append(value);

                if (i < tokens.size() - 2)
                {
                    sb.append(",\n");
                }
                else
                {
                    sb.append("\n");
                }
            }

            sb.append("}\n");

            out.append(sb);
        }
    }

    /**
     * Generate a bitset type.
     *
     * @param tokens for the bitset.
     * @throws IOException if an error is encountered when writing the output.
     */
    private void generateBitSet(final List<Token> tokens) throws IOException
    {
        final Token setToken = tokens.get(0);
        final String setName = formatTypeName(setToken.applicableTypeName());
        final String interfaceName = setName;
        final String decoderName = setName + "Decoder";

        try (Writer out = outputManager.createOutput(setName))
        {
            final StringBuilder sb = new StringBuilder();
            sb.append(generateFileHeader(ir.packageName()));

            // Generate interface
            sb.append(generateDocumentation("", setToken));
            sb.append("export interface ").append(interfaceName).append(" {\n");

            for (int i = 1; i < tokens.size() - 1; i++)
            {
                final Token token = tokens.get(i);
                sb.append("  ").append(formatFieldName(token.name())).append(": boolean;\n");
            }

            sb.append("}\n\n");

            // Generate decoder class
            sb.append("export class ").append(decoderName).append(" {\n");
            sb.append("  static decode(view: DataView, offset: number, littleEndian: boolean): ")
                .append(interfaceName).append(" {\n");
            sb.append("    const bits = view.get")
                .append(formatTypeName(setToken.encoding().primitiveType().primitiveName()))
                .append("(offset");

            if (needsEndianness(setToken.encoding().primitiveType()))
            {
                sb.append(", littleEndian");
            }

            sb.append(");\n\n");
            sb.append("    return {\n");

            for (int i = 1; i < tokens.size() - 1; i++)
            {
                final Token token = tokens.get(i);
                final int bitPosition = Integer.parseInt(token.encoding().constValue().toString());
                sb.append("      ").append(formatFieldName(token.name()))
                    .append(": (bits & (1 << ").append(bitPosition).append(")) !== 0");

                if (i < tokens.size() - 2)
                {
                    sb.append(",\n");
                }
                else
                {
                    sb.append("\n");
                }
            }

            sb.append("    };\n");
            sb.append("  }\n\n");

            sb.append("  static getEncodedLength(): number {\n");
            sb.append("    return ").append(setToken.encodedLength()).append(";\n");
            sb.append("  }\n");
            sb.append("}\n");

            out.append(sb);
        }
    }

    /**
     * Generate a composite type decoder.
     *
     * @param tokens for the composite.
     * @throws IOException if an error is encountered when writing the output.
     */
    private void generateComposite(final List<Token> tokens) throws IOException
    {
        final Token compositeToken = tokens.get(0);
        final String compositeName = formatTypeName(compositeToken.applicableTypeName());

        try (Writer out = outputManager.createOutput(compositeName))
        {
            final StringBuilder sb = new StringBuilder();
            sb.append(generateFileHeader(ir.packageName()));

            generateCompositeDecoder(sb, compositeName, tokens.subList(1, tokens.size() - 1));

            out.append(sb);
        }
    }

    /**
     * Generate a composite decoder implementation.
     *
     * @param sb         to append to.
     * @param name       of the composite.
     * @param tokens     for the composite fields.
     */
    private void generateCompositeDecoder(
        final StringBuilder sb,
        final String name,
        final List<Token> tokens)
    {
        final String interfaceName = name;
        final String decoderName = name + "Decoder";

        // Generate interface
        sb.append("export interface ").append(interfaceName).append(" {\n");

        Generators.forEachField(tokens, (fieldToken, typeToken) ->
        {
            final String fieldName = formatFieldName(fieldToken.name());
            final String typeName = typeScriptTypeName(typeToken.encoding().primitiveType());
            final boolean isConstant = typeToken.isConstantEncoding();

            if (isConstant)
            {
                sb.append("  readonly ");
            }
            else
            {
                sb.append("  ");
            }

            sb.append(fieldName).append(": ").append(typeName).append(";\n");
        });

        sb.append("}\n\n");

        // Generate decoder class
        sb.append("export class ").append(decoderName).append(" {\n");

        // Add constants
        Generators.forEachField(tokens, (fieldToken, typeToken) ->
        {
            if (typeToken.isConstantEncoding())
            {
                final String constantName = formatConstantName(fieldToken.name());
                final String value = generateLiteral(
                    typeToken.encoding().primitiveType(),
                    typeToken.encoding().constValue().toString());

                sb.append("  private static readonly ").append(constantName)
                    .append(" = ").append(value).append(";\n");
            }
        });

        sb.append("  private static readonly ENCODED_LENGTH = ")
            .append(tokens.get(0).encodedLength()).append(";\n\n");

        // Generate decode method
        sb.append("  static decode(view: DataView, offset: number, littleEndian: boolean): ")
            .append(interfaceName).append(" {\n");
        sb.append("    let pos = offset;\n\n");

        final List<String> fieldNames = new ArrayList<>();
        final List<String> fieldValues = new ArrayList<>();

        Generators.forEachField(tokens, (fieldToken, typeToken) ->
        {
            final String fieldName = formatFieldName(fieldToken.name());
            fieldNames.add(fieldName);

            if (typeToken.isConstantEncoding())
            {
                // Constant field - not read from buffer
                final String constantName = formatConstantName(fieldToken.name());
                fieldValues.add(decoderName + "." + constantName);
            }
            else
            {
                // Regular field - read from buffer
                final PrimitiveType primitiveType = typeToken.encoding().primitiveType();
                final String dataViewMethod = dataViewMethod(primitiveType);

                sb.append("    const ").append(fieldName).append(" = view.")
                    .append(dataViewMethod).append("(pos");

                if (needsEndianness(primitiveType))
                {
                    sb.append(", littleEndian");
                }

                sb.append(");\n");
                sb.append("    pos += ").append(typeToken.encodedLength()).append(";\n\n");

                fieldValues.add(fieldName);
            }
        });

        sb.append("    return {\n");
        for (int i = 0; i < fieldNames.size(); i++)
        {
            sb.append("      ").append(fieldNames.get(i)).append(": ").append(fieldValues.get(i));
            if (i < fieldNames.size() - 1)
            {
                sb.append(",\n");
            }
            else
            {
                sb.append("\n");
            }
        }
        sb.append("    };\n");
        sb.append("  }\n\n");

        sb.append("  static getEncodedLength(): number {\n");
        sb.append("    return ").append(decoderName).append(".ENCODED_LENGTH;\n");
        sb.append("  }\n");
        sb.append("}\n");
    }

    /**
     * Generate a message decoder.
     *
     * @param tokens for the message.
     * @throws IOException if an error is encountered when writing the output.
     */
    private void generateMessage(final List<Token> tokens) throws IOException
    {
        final Token msgToken = tokens.get(0);
        final String messageName = formatTypeName(msgToken.name());

        try (Writer out = outputManager.createOutput(messageName))
        {
            final StringBuilder sb = new StringBuilder();
            sb.append(generateFileHeader(ir.packageName()));

            final List<Token> messageBody = tokens.subList(1, tokens.size() - 1);
            int i = 0;

            final List<Token> fields = new ArrayList<>();
            i = collectFields(messageBody, i, fields);

            final List<Token> groups = new ArrayList<>();
            i = collectGroups(messageBody, i, groups);

            final List<Token> varData = new ArrayList<>();
            collectVarData(messageBody, i, varData);

            generateMessageDecoder(sb, messageName, msgToken, fields, groups, varData);

            out.append(sb);
        }
    }

    /**
     * Generate a message decoder implementation.
     *
     * @param sb          to append to.
     * @param messageName the name of the message.
     * @param msgToken    the message token.
     * @param fields      the field tokens.
     * @param groups      the group tokens.
     * @param varData     the variable-length data tokens.
     */
    private void generateMessageDecoder(
        final StringBuilder sb,
        final String messageName,
        final Token msgToken,
        final List<Token> fields,
        final List<Token> groups,
        final List<Token> varData)
    {
        final String interfaceName = messageName;
        final String decoderName = messageName + "Decoder";

        // Generate interface first
        generateMessageInterface(sb, interfaceName, fields, groups, varData);
        sb.append("\n");

        // Generate decoder class
        sb.append("export class ").append(decoderName).append(" {\n");

        // Add constants
        sb.append("  private static readonly BLOCK_LENGTH = ").append(msgToken.encodedLength()).append(";\n");
        sb.append("  private static readonly TEMPLATE_ID = ").append(msgToken.id()).append(";\n");
        sb.append("  private static readonly SCHEMA_ID = ").append(ir.id()).append(";\n");
        sb.append("  private static readonly SCHEMA_VERSION = ").append(ir.version()).append(";\n\n");

        sb.append("  private readonly littleEndian: boolean;\n\n");

        // Constructor
        final String defaultEndian = ir.byteOrder() == ByteOrder.LITTLE_ENDIAN ? "little" : "big";
        sb.append("  constructor(byteOrder: 'little' | 'big' = '").append(defaultEndian).append("') {\n");
        sb.append("    this.littleEndian = byteOrder === 'little';\n");
        sb.append("  }\n\n");

        // Generate decode method
        generateDecodeMethod(sb, interfaceName, decoderName, fields, groups, varData);

        // Generate group decoder methods
        generateGroupDecoderMethods(sb, groups, messageName);

        // Generate variable data decoder methods
        generateVarDataDecoderMethods(sb, varData);

        // Generate static methods
        sb.append("\n  static getBlockLength(): number {\n");
        sb.append("    return ").append(decoderName).append(".BLOCK_LENGTH;\n");
        sb.append("  }\n\n");

        sb.append("  static getTemplateId(): number {\n");
        sb.append("    return ").append(decoderName).append(".TEMPLATE_ID;\n");
        sb.append("  }\n\n");

        sb.append("  static getSchemaId(): number {\n");
        sb.append("    return ").append(decoderName).append(".SCHEMA_ID;\n");
        sb.append("  }\n\n");

        sb.append("  static getSchemaVersion(): number {\n");
        sb.append("    return ").append(decoderName).append(".SCHEMA_VERSION;\n");
        sb.append("  }\n");

        sb.append("}\n");
    }

    /**
     * Generate a message interface.
     *
     * @param sb            to append to.
     * @param interfaceName the interface name.
     * @param fields        the field tokens.
     * @param groups        the group tokens.
     * @param varData       the variable-length data tokens.
     */
    private void generateMessageInterface(
        final StringBuilder sb,
        final String interfaceName,
        final List<Token> fields,
        final List<Token> groups,
        final List<Token> varData)
    {
        sb.append("export interface ").append(interfaceName).append(" {\n");

        // Fields
        Generators.forEachField(fields, (fieldToken, typeToken) ->
        {
            final String fieldName = formatFieldName(fieldToken.name());
            final String typeName = typeScriptTypeName(typeToken.encoding().primitiveType());
            sb.append("  ").append(fieldName).append(": ").append(typeName).append(";\n");
        });

        // Groups
        for (int i = 0; i < groups.size();)
        {
            final Token groupToken = groups.get(i);
            if (groupToken.signal() == Signal.BEGIN_GROUP)
            {
                final String groupName = formatTypeName(groupToken.name());
                final String fieldName = formatFieldName(groupToken.name());
                sb.append("  ").append(fieldName).append(": ").append(groupName).append("[];\n");

                i += groupToken.componentTokenCount();
            }
            else
            {
                i++;
            }
        }

        // Variable-length data
        for (int i = 0; i < varData.size();)
        {
            final Token varDataToken = varData.get(i);
            if (varDataToken.signal() == Signal.BEGIN_VAR_DATA)
            {
                final String fieldName = formatFieldName(varDataToken.name());
                final Encoding encoding = varDataToken.encoding();
                final String characterEncoding = encoding.characterEncoding();

                if (null == characterEncoding)
                {
                    sb.append("  ").append(fieldName).append(": Uint8Array;\n");
                }
                else
                {
                    sb.append("  ").append(fieldName).append(": string;\n");
                }

                i += varDataToken.componentTokenCount();
            }
            else
            {
                i++;
            }
        }

        sb.append("}\n");
    }

    /**
     * Generate the main decode method.
     *
     * @param sb            to append to.
     * @param interfaceName the interface name.
     * @param decoderName   the decoder class name.
     * @param fields        the field tokens.
     * @param groups        the group tokens.
     * @param varData       the variable-length data tokens.
     */
    private void generateDecodeMethod(
        final StringBuilder sb,
        final String interfaceName,
        final String decoderName,
        final List<Token> fields,
        final List<Token> groups,
        final List<Token> varData)
    {
        sb.append("  decode(buffer: ArrayBuffer, offset: number = 0): ").append(interfaceName).append(" {\n");
        sb.append("    const view = new DataView(buffer);\n");
        sb.append("    let pos = offset;\n\n");

        // Decode fields
        final List<String> fieldNames = new ArrayList<>();
        final List<String> fieldValues = new ArrayList<>();

        Generators.forEachField(fields, (fieldToken, typeToken) ->
        {
            final String fieldName = formatFieldName(fieldToken.name());
            fieldNames.add(fieldName);

            final PrimitiveType primitiveType = typeToken.encoding().primitiveType();
            final String dataViewMethod = dataViewMethod(primitiveType);

            sb.append("    const ").append(fieldName).append(" = view.")
                .append(dataViewMethod).append("(pos");

            if (needsEndianness(primitiveType))
            {
                sb.append(", this.littleEndian");
            }

            sb.append(");\n");
            sb.append("    pos += ").append(typeToken.encodedLength()).append(";\n\n");

            fieldValues.add(fieldName);
        });

        // Skip to block end for forward compatibility
        sb.append("    // Skip to end of block for forward compatibility\n");
        sb.append("    pos = offset + ").append(decoderName).append(".BLOCK_LENGTH;\n\n");

        // Decode groups
        for (int i = 0; i < groups.size();)
        {
            final Token groupToken = groups.get(i);
            if (groupToken.signal() == Signal.BEGIN_GROUP)
            {
                final String groupName = formatTypeName(groupToken.name());
                final String fieldName = formatFieldName(groupToken.name());
                final String methodName = "decode" + groupName + "Group";

                sb.append("    const ").append(fieldName).append(" = this.")
                    .append(methodName).append("(view, pos);\n");
                sb.append("    pos = ").append(fieldName).append(".nextOffset;\n\n");

                fieldNames.add(fieldName);
                fieldValues.add(fieldName + ".items");

                i += groupToken.componentTokenCount();
            }
            else
            {
                i++;
            }
        }

        // Decode variable-length data
        for (int i = 0; i < varData.size();)
        {
            final Token varDataToken = varData.get(i);
            if (varDataToken.signal() == Signal.BEGIN_VAR_DATA)
            {
                final String fieldName = formatFieldName(varDataToken.name());
                final Encoding encoding = varDataToken.encoding();
                final String characterEncoding = encoding.characterEncoding();

                if (null == characterEncoding)
                {
                    sb.append("    const ").append(fieldName).append(" = this.decodeVarData(view, pos);\n");
                }
                else if ("UTF-8".equals(characterEncoding))
                {
                    sb.append("    const ").append(fieldName).append(" = this.decodeVarStringUtf8(view, pos);\n");
                }
                else
                {
                    sb.append("    const ").append(fieldName).append(" = this.decodeVarStringAscii(view, pos);\n");
                }

                sb.append("    pos = ").append(fieldName).append(".nextOffset;\n\n");

                fieldNames.add(fieldName);
                fieldValues.add(fieldName + ".value");

                i += varDataToken.componentTokenCount();
            }
            else
            {
                i++;
            }
        }

        // Return object
        sb.append("    return {\n");
        for (int i = 0; i < fieldNames.size(); i++)
        {
            sb.append("      ").append(fieldNames.get(i)).append(": ").append(fieldValues.get(i));
            if (i < fieldNames.size() - 1)
            {
                sb.append(",\n");
            }
            else
            {
                sb.append("\n");
            }
        }
        sb.append("    };\n");
        sb.append("  }\n");
    }

    /**
     * Generate group decoder methods.
     *
     * @param sb      to append to.
     * @param groups  the group tokens.
     * @param parentName the parent message name.
     */
    private void generateGroupDecoderMethods(
        final StringBuilder sb,
        final List<Token> groups,
        final String parentName)
    {
        for (int i = 0; i < groups.size();)
        {
            final Token groupToken = groups.get(i);
            if (groupToken.signal() == Signal.BEGIN_GROUP)
            {
                final String groupName = formatTypeName(groupToken.name());
                final List<Token> groupTokens = new ArrayList<>();

                final int endIndex = i + groupToken.componentTokenCount();
                for (int j = i; j < endIndex; j++)
                {
                    groupTokens.add(groups.get(j));
                }

                // First, generate the interface for the group
                final List<Token> groupBody = groupTokens.subList(1, groupTokens.size() - 1);

                // Generate group interface
                sb.append("\nexport interface ").append(groupName).append(" {\n");

                Generators.forEachField(groupBody, (fieldToken, typeToken) ->
                {
                    final String fieldName = formatFieldName(fieldToken.name());
                    final String typeName = typeScriptTypeName(typeToken.encoding().primitiveType());
                    sb.append("  ").append(fieldName).append(": ").append(typeName).append(";\n");
                });

                sb.append("}\n\n");

                // Generate group decoder method
                final String methodName = "decode" + groupName + "Group";
                sb.append("  private ").append(methodName)
                    .append("(view: DataView, offset: number): { items: ")
                    .append(groupName).append("[], nextOffset: number } {\n");

                sb.append("    let pos = offset;\n\n");

                // Read group header
                sb.append("    const blockLength = view.getUint16(pos, this.littleEndian);\n");
                sb.append("    pos += 2;\n");
                sb.append("    const numInGroup = view.getUint16(pos, this.littleEndian);\n");
                sb.append("    pos += 2;\n\n");

                sb.append("    const items: ").append(groupName).append("[] = [];\n\n");

                sb.append("    for (let i = 0; i < numInGroup; i++) {\n");
                sb.append("      const itemStart = pos;\n\n");

                // Decode group fields
                final List<String> groupFieldNames = new ArrayList<>();
                final List<String> groupFieldValues = new ArrayList<>();

                Generators.forEachField(groupBody, (fieldToken, typeToken) ->
                {
                    final String fieldName = formatFieldName(fieldToken.name());
                    groupFieldNames.add(fieldName);

                    final PrimitiveType primitiveType = typeToken.encoding().primitiveType();
                    final String dataViewMethod = dataViewMethod(primitiveType);

                    sb.append("      const ").append(fieldName).append(" = view.")
                        .append(dataViewMethod).append("(pos");

                    if (needsEndianness(primitiveType))
                    {
                        sb.append(", this.littleEndian");
                    }

                    sb.append(");\n");
                    sb.append("      pos += ").append(typeToken.encodedLength()).append(";\n\n");

                    groupFieldValues.add(fieldName);
                });

                // Skip to next block for forward compatibility
                sb.append("      // Skip to next block for forward compatibility\n");
                sb.append("      pos = itemStart + blockLength;\n\n");

                // Add item to array
                sb.append("      items.push({\n");
                for (int k = 0; k < groupFieldNames.size(); k++)
                {
                    sb.append("        ").append(groupFieldNames.get(k)).append(": ").append(groupFieldValues.get(k));
                    if (k < groupFieldNames.size() - 1)
                    {
                        sb.append(",\n");
                    }
                    else
                    {
                        sb.append("\n");
                    }
                }
                sb.append("      });\n");

                sb.append("    }\n\n");

                sb.append("    return { items, nextOffset: pos };\n");
                sb.append("  }\n");

                i = endIndex;
            }
            else
            {
                i++;
            }
        }
    }

    /**
     * Generate variable-length data decoder methods.
     *
     * @param sb      to append to.
     * @param varData the variable-length data tokens.
     */
    private void generateVarDataDecoderMethods(final StringBuilder sb, final List<Token> varData)
    {
        boolean needsVarDataDecoder = false;
        boolean needsUtf8Decoder = false;
        boolean needsAsciiDecoder = false;

        for (int i = 0; i < varData.size();)
        {
            final Token varDataToken = varData.get(i);
            if (varDataToken.signal() == Signal.BEGIN_VAR_DATA)
            {
                final Encoding encoding = varDataToken.encoding();
                final String characterEncoding = encoding.characterEncoding();

                if (null == characterEncoding)
                {
                    needsVarDataDecoder = true;
                }
                else if ("UTF-8".equals(characterEncoding))
                {
                    needsUtf8Decoder = true;
                }
                else
                {
                    needsAsciiDecoder = true;
                }

                i += varDataToken.componentTokenCount();
            }
            else
            {
                i++;
            }
        }

        if (needsVarDataDecoder)
        {
            sb.append("\n  private decodeVarData(view: DataView, offset: number): ")
                .append("{ value: Uint8Array, nextOffset: number } {\n");
            sb.append("    let pos = offset;\n\n");
            sb.append("    const length = view.getUint32(pos, this.littleEndian);\n");
            sb.append("    pos += 4;\n\n");
            sb.append("    const value = new Uint8Array(view.buffer, view.byteOffset + pos, length);\n");
            sb.append("    pos += length;\n\n");
            sb.append("    return { value, nextOffset: pos };\n");
            sb.append("  }\n");
        }

        if (needsUtf8Decoder)
        {
            sb.append("\n  private decodeVarStringUtf8(view: DataView, offset: number): ")
                .append("{ value: string, nextOffset: number } {\n");
            sb.append("    let pos = offset;\n\n");
            sb.append("    const length = view.getUint32(pos, this.littleEndian);\n");
            sb.append("    pos += 4;\n\n");
            sb.append("    const bytes = new Uint8Array(view.buffer, view.byteOffset + pos, length);\n");
            sb.append("    const value = new TextDecoder('utf-8').decode(bytes);\n");
            sb.append("    pos += length;\n\n");
            sb.append("    return { value, nextOffset: pos };\n");
            sb.append("  }\n");
        }

        if (needsAsciiDecoder)
        {
            sb.append("\n  private decodeVarStringAscii(view: DataView, offset: number): ")
                .append("{ value: string, nextOffset: number } {\n");
            sb.append("    let pos = offset;\n\n");
            sb.append("    const length = view.getUint32(pos, this.littleEndian);\n");
            sb.append("    pos += 4;\n\n");
            sb.append("    const bytes = new Uint8Array(view.buffer, view.byteOffset + pos, length);\n");
            sb.append("    const value = new TextDecoder('ascii').decode(bytes);\n");
            sb.append("    pos += length;\n\n");
            sb.append("    return { value, nextOffset: pos };\n");
            sb.append("  }\n");
        }
    }
}
