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

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import org.agrona.MutableDirectBuffer;

import java.io.IOException;

/**
 * Thread-confined, non-reentrant encoder bound to one message template and to the schema version of the
 * {@link SbeJson} it came from. Walks the plan, not the JSON: block fields are written at fixed offsets so JSON
 * key order is irrelevant; groups and var-data are written in schema order.
 */
public final class SbeJsonEncoder
{
    private final MessageCodec codec;
    private final WalkContext context;

    SbeJsonEncoder(final SbeJson sbeJson, final MessageCodec codec)
    {
        this.codec = codec;
        this.context = new WalkContext(JsonNodeFactory.instance, sbeJson.exceptionStackTraces());
    }

    /**
     * Template id this encoder writes.
     *
     * @return the template id.
     */
    public int templateId()
    {
        return codec.plan().templateId;
    }

    /**
     * Message name this encoder writes.
     *
     * @return the message name.
     */
    public String messageName()
    {
        return codec.plan().name;
    }

    /**
     * Encode header and body in a single pass. On failure the destination region is undefined and the exception
     * carries the field path. The destination is not retained after the call returns or throws.
     * <p>
     * Text is coerced strictly: an unpaired UTF-16 surrogate, or a character the field's charset cannot
     * represent, is rejected with {@link ErrorCode#TYPE_MISMATCH} for fixed {@code char[N]} and var-data alike;
     * nothing is replaced. An optional scalar takes its null sentinel from omission or JSON {@code null} only;
     * elements of an optional numeric array may also carry the sentinel value so decoded arrays re-encode.
     *
     * @param body      root object; see DESIGN.md section 9 for the accepted shapes per field type.
     * @param dst       destination buffer.
     * @param offset    where the header starts.
     * @param available bytes available from {@code offset}.
     * @return bytes written including the header.
     * @throws SbeJsonException on validation failure or {@link ErrorCode#DESTINATION_OVERFLOW}.
     */
    public int encode(final JsonNode body, final MutableDirectBuffer dst, final int offset, final int available)
    {
        return codec.encode(body, dst, offset, available, context);
    }

    /**
     * Compute the encoded length of header and body without writing. Optional sizing pass; the input must not
     * change before the following {@link #encode}.
     *
     * @param body root object.
     * @return bytes that {@link #encode} would write.
     * @throws SbeJsonException on validation failure.
     */
    public int encodedLength(final JsonNode body)
    {
        return codec.encodedLength(body, context);
    }

    /**
     * Encode straight from a parser. Groups and var-data must arrive in schema order. Arrives with DESIGN.md
     * section 13 step 6.
     *
     * @param parser    source positioned before the root object.
     * @param dst       destination buffer.
     * @param offset    where the header starts.
     * @param available bytes available from {@code offset}.
     * @return bytes written including the header.
     * @throws IOException                   from the parser.
     * @throws UnsupportedOperationException always, until step 6 lands.
     */
    public int encode(final JsonParser parser, final MutableDirectBuffer dst, final int offset, final int available)
        throws IOException
    {
        // TODO(DESIGN.md section 13 step 6): streaming encode with schema-order rule and back-filled prefixes.
        throw new UnsupportedOperationException("encode(JsonParser) arrives with the streaming path");
    }
}
