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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.agrona.DirectBuffer;
import uk.co.real_logic.sbe.otf.OtfHeaderDecoder;

import java.io.IOException;

/**
 * Thread-confined, non-reentrant decoder for any message of one {@link SbeJson}. Routes by the header template
 * id. Every decode takes an explicit frame {@code (buffer, offset, length)} and no read goes past
 * {@code offset + length}: an SBE header carries no total length and a receive buffer may hold several messages.
 */
public final class SbeJsonDecoder
{
    private final SbeJson sbeJson;
    private final OtfHeaderDecoder headerDecoder;
    private final HeaderView header = new HeaderView();
    private final WalkContext context;

    SbeJsonDecoder(final SbeJson sbeJson)
    {
        this.sbeJson = sbeJson;
        this.headerDecoder = sbeJson.headerDecoder();
        this.context = new WalkContext(JsonNodeFactory.instance, sbeJson.exceptionStackTraces());
    }

    /**
     * Decode one message (header plus body) into a fresh tree of stock Jackson nodes. The tree is independent of
     * the buffer and of this decoder and may be retained. Allocates one node per value.
     *
     * @param buffer buffer holding the message.
     * @param offset offset of the message header.
     * @param length bytes available from {@code offset}; no read goes past {@code offset + length}.
     * @return the body as an object; header fields are available from {@link #lastHeader()}.
     * @throws SbeJsonException on framing or validation failure.
     */
    public ObjectNode decodeCopy(final DirectBuffer buffer, final int offset, final int length)
    {
        final int frameEnd = prologue(buffer, offset, length);
        final MessageCodec codec = route(buffer, offset);

        return codec.decodeCopy(
            buffer,
            offset + headerDecoder.encodedLength(),
            frameEnd,
            header.blockLength(),
            header.actingVersion(),
            context);
    }

    /**
     * Header of the last message decoded on this decoder. Overwritten by every decode, including decodes that
     * fail after the header was read.
     *
     * @return the header view; {@link HeaderView#populated()} is false before the first decode.
     */
    public HeaderView lastHeader()
    {
        return header;
    }

    /**
     * Write one message straight to a generator without building a tree. Arrives with DESIGN.md section 13
     * step 6.
     *
     * @param buffer    buffer holding the message.
     * @param offset    offset of the message header.
     * @param length    bytes available from {@code offset}.
     * @param generator destination.
     * @throws IOException                   from the generator.
     * @throws UnsupportedOperationException always, until step 6 lands.
     */
    public void writeJson(final DirectBuffer buffer, final int offset, final int length, final JsonGenerator generator)
        throws IOException
    {
        // TODO(DESIGN.md section 13 step 6): generator sink over the same plan walk.
        throw new UnsupportedOperationException("writeJson arrives with the generator sink");
    }

    /**
     * Decode into a borrowed document for zero steady-state allocation. Arrives with DESIGN.md section 13
     * step 5.
     *
     * @param buffer   buffer holding the message.
     * @param offset   offset of the message header.
     * @param length   bytes available from {@code offset}.
     * @param document document created by {@link #newDocument()}.
     * @return bytes consumed.
     * @throws UnsupportedOperationException always, until step 5 lands.
     */
    public int decodeInto(final DirectBuffer buffer, final int offset, final int length, final Object document)
    {
        // TODO(DESIGN.md section 13 step 5): BorrowedDocument with skeleton registry and Sbe*Node leaves.
        throw new UnsupportedOperationException("decodeInto arrives with the borrowed-document path");
    }

    /**
     * Create a borrowed document for {@link #decodeInto}. Arrives with DESIGN.md section 13 step 5.
     *
     * @return never returns in this release.
     * @throws UnsupportedOperationException always, until step 5 lands.
     */
    public Object newDocument()
    {
        // TODO(DESIGN.md section 13 step 5): BorrowedDocument.
        throw new UnsupportedOperationException("newDocument arrives with the borrowed-document path");
    }

    private int prologue(final DirectBuffer buffer, final int offset, final int length)
    {
        if (offset < 0 || length < 0 || length > buffer.capacity() - offset)
        {
            throw new SbeJsonException(
                ErrorCode.FRAME_OVERFLOW, SbeJsonException.NO_TEMPLATE_ID, offset, null,
                "frame [" + offset + ", " + offset + " + " + length + ") outside buffer capacity " +
                buffer.capacity(), sbeJson.exceptionStackTraces());
        }

        final int headerLength = headerDecoder.encodedLength();
        if (headerLength > length)
        {
            throw new SbeJsonException(
                ErrorCode.FRAME_OVERFLOW, SbeJsonException.NO_TEMPLATE_ID, offset, null,
                "frame of " + length + " bytes is shorter than the " + headerLength + " byte header",
                sbeJson.exceptionStackTraces());
        }

        return offset + length;
    }

    private MessageCodec route(final DirectBuffer buffer, final int offset)
    {
        final int templateId = headerDecoder.getTemplateId(buffer, offset);
        final int schemaId = headerDecoder.getSchemaId(buffer, offset);
        final int actingVersion = headerDecoder.getSchemaVersion(buffer, offset);
        final int blockLength = headerDecoder.getBlockLength(buffer, offset);
        header.set(templateId, schemaId, actingVersion, blockLength);

        if (schemaId != sbeJson.ir().id())
        {
            throw new SbeJsonException(
                ErrorCode.UNKNOWN_TEMPLATE, templateId, offset, null,
                "header schema id " + schemaId + " does not match schema id " + sbeJson.ir().id(),
                sbeJson.exceptionStackTraces());
        }

        final MessageCodec codec = sbeJson.codecForTemplate(templateId);
        if (actingVersion > codec.plan().schemaVersion)
        {
            throw new SbeJsonException(
                ErrorCode.UNSUPPORTED_VERSION, templateId, offset, codec.plan().name,
                "acting version " + actingVersion + " is newer than schema version " + codec.plan().schemaVersion,
                sbeJson.exceptionStackTraces());
        }

        return codec;
    }
}
