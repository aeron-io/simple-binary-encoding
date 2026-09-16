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
package uk.co.real_logic.sbe.jackson;

import org.agrona.MutableDirectBuffer;
import uk.co.real_logic.sbe.PrimitiveType;
import uk.co.real_logic.sbe.ir.HeaderStructure;
import uk.co.real_logic.sbe.ir.Token;

import java.nio.ByteOrder;

/**
 * Write-side layout of the message header taken from the IR {@link HeaderStructure}. The read side reuses
 * {@link uk.co.real_logic.sbe.otf.OtfHeaderDecoder}. Header members other than the four standard ones are left
 * as zero bytes.
 */
final class HeaderLayout
{
    private final int encodedLength;
    private final int blockLengthOffset;
    private final int templateIdOffset;
    private final int schemaIdOffset;
    private final int schemaVersionOffset;
    private final PrimitiveType blockLengthType;
    private final PrimitiveType templateIdType;
    private final PrimitiveType schemaIdType;
    private final PrimitiveType schemaVersionType;
    private final ByteOrder byteOrder;

    HeaderLayout(final HeaderStructure headerStructure)
    {
        encodedLength = headerStructure.tokens().get(0).encodedLength();

        int blockLengthOffset = 0;
        int templateIdOffset = 0;
        int schemaIdOffset = 0;
        int schemaVersionOffset = 0;
        ByteOrder byteOrder = ByteOrder.LITTLE_ENDIAN;
        for (final Token token : headerStructure.tokens())
        {
            switch (token.name())
            {
                case HeaderStructure.BLOCK_LENGTH:
                    blockLengthOffset = token.offset();
                    byteOrder = token.encoding().byteOrder();
                    break;

                case HeaderStructure.TEMPLATE_ID:
                    templateIdOffset = token.offset();
                    break;

                case HeaderStructure.SCHEMA_ID:
                    schemaIdOffset = token.offset();
                    break;

                case HeaderStructure.SCHEMA_VERSION:
                    schemaVersionOffset = token.offset();
                    break;

                default:
                    break;
            }
        }

        this.blockLengthOffset = blockLengthOffset;
        this.templateIdOffset = templateIdOffset;
        this.schemaIdOffset = schemaIdOffset;
        this.schemaVersionOffset = schemaVersionOffset;
        this.byteOrder = byteOrder;
        blockLengthType = headerStructure.blockLengthType();
        templateIdType = headerStructure.templateIdType();
        schemaIdType = headerStructure.schemaIdType();
        schemaVersionType = headerStructure.schemaVersionType();
    }

    int encodedLength()
    {
        return encodedLength;
    }

    void write(
        final MutableDirectBuffer buffer,
        final int offset,
        final int blockLength,
        final int templateId,
        final int schemaId,
        final int version)
    {
        buffer.setMemory(offset, encodedLength, (byte)0);
        WireTypes.putLong(buffer, offset + blockLengthOffset, blockLengthType, byteOrder, blockLength);
        WireTypes.putLong(buffer, offset + templateIdOffset, templateIdType, byteOrder, templateId);
        WireTypes.putLong(buffer, offset + schemaIdOffset, schemaIdType, byteOrder, schemaId);
        WireTypes.putLong(buffer, offset + schemaVersionOffset, schemaVersionType, byteOrder, version);
    }
}
