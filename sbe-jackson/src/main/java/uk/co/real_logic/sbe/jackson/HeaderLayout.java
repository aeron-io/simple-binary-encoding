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

import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;
import uk.co.real_logic.sbe.PrimitiveType;
import uk.co.real_logic.sbe.ir.HeaderStructure;
import uk.co.real_logic.sbe.ir.Token;

import java.nio.ByteOrder;

/**
 * Layout of the message header taken from the IR {@link HeaderStructure}: offset, primitive type and byte order
 * of each of the four standard members, consulted individually on both read and write (a header may mix byte
 * orders per member). Reads return {@code long} so that uint32 members at or above 2^31 never surface as an
 * {@code IllegalStateException}; the decoder range checks them. Header members other than the four standard
 * ones are written as zero bytes.
 */
final class HeaderLayout
{
    /**
     * Index of the block length member in the per-member arrays.
     */
    static final int BLOCK_LENGTH = 0;

    /**
     * Index of the template id member in the per-member arrays.
     */
    static final int TEMPLATE_ID = 1;

    /**
     * Index of the schema id member in the per-member arrays.
     */
    static final int SCHEMA_ID = 2;

    /**
     * Index of the schema version member in the per-member arrays.
     */
    static final int SCHEMA_VERSION = 3;

    private static final String[] MEMBER_NAMES = {
        HeaderStructure.BLOCK_LENGTH, HeaderStructure.TEMPLATE_ID, HeaderStructure.SCHEMA_ID,
        HeaderStructure.SCHEMA_VERSION
    };

    private final int encodedLength;
    private final int[] offsets = new int[MEMBER_NAMES.length];
    private final PrimitiveType[] types = new PrimitiveType[MEMBER_NAMES.length];
    private final ByteOrder[] byteOrders = new ByteOrder[MEMBER_NAMES.length];

    HeaderLayout(final HeaderStructure headerStructure)
    {
        encodedLength = headerStructure.tokens().get(0).encodedLength();

        for (final Token token : headerStructure.tokens())
        {
            for (int member = 0; member < MEMBER_NAMES.length; member++)
            {
                if (MEMBER_NAMES[member].equals(token.name()) && null != token.encoding().primitiveType())
                {
                    offsets[member] = token.offset();
                    types[member] = token.encoding().primitiveType();
                    byteOrders[member] = token.encoding().byteOrder();
                }
            }
        }

        for (int member = 0; member < MEMBER_NAMES.length; member++)
        {
            if (null == types[member])
            {
                throw new IllegalArgumentException("header is missing member " + MEMBER_NAMES[member]);
            }
        }
    }

    int encodedLength()
    {
        return encodedLength;
    }

    /**
     * Read one header member with its own type and byte order.
     *
     * @param buffer buffer holding the header.
     * @param offset offset of the header.
     * @param member one of {@link #BLOCK_LENGTH}, {@link #TEMPLATE_ID}, {@link #SCHEMA_ID},
     *               {@link #SCHEMA_VERSION}.
     * @return the value widened to a long (unsigned for unsigned types).
     */
    long read(final DirectBuffer buffer, final int offset, final int member)
    {
        return WireTypes.getLong(buffer, offset + offsets[member], types[member], byteOrders[member]);
    }

    int memberOffset(final int member)
    {
        return offsets[member];
    }

    String memberName(final int member)
    {
        return MEMBER_NAMES[member];
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
        put(buffer, offset, BLOCK_LENGTH, blockLength);
        put(buffer, offset, TEMPLATE_ID, templateId);
        put(buffer, offset, SCHEMA_ID, schemaId);
        put(buffer, offset, SCHEMA_VERSION, version);
    }

    private void put(final MutableDirectBuffer buffer, final int offset, final int member, final long value)
    {
        WireTypes.putLong(buffer, offset + offsets[member], types[member], byteOrders[member], value);
    }
}
