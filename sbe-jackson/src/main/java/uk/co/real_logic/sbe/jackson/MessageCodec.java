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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.agrona.DirectBuffer;
import org.agrona.MutableDirectBuffer;

/**
 * Backend for one message template. {@link PlanMessageCodec} is the interpreter over a {@link MessagePlan};
 * generated adapters may implement this later without changing {@link SbeJsonDecoder} / {@link SbeJsonEncoder}.
 * <p>
 * The decoder performs the frame and header prologue (frame bounds, template routing, version check) and hands
 * the body to the codec. The encoder writes the header itself so that a single {@link #encode} call produces a
 * complete message.
 * <p>
 * {@code decodeInto(DirectBuffer, int, int, BorrowedDocument)} joins this interface with the borrowed-document
 * path (DESIGN.md section 13 step 5).
 */
interface MessageCodec
{
    /**
     * The plan this codec was built from.
     *
     * @return the compiled plan.
     */
    MessagePlan plan();

    /**
     * Decode a message body into a fresh tree of stock Jackson nodes.
     *
     * @param buffer            source buffer.
     * @param bodyOffset        offset of the root block (just after the header).
     * @param frameEnd          exclusive end of the frame; no read may reach it.
     * @param actingBlockLength root block length from the header.
     * @param actingVersion     acting version from the header, already checked against the schema version.
     * @param context           scratch state owned by the calling decoder.
     * @return the root object.
     */
    ObjectNode decodeCopy(
        DirectBuffer buffer,
        int bodyOffset,
        int frameEnd,
        int actingBlockLength,
        int actingVersion,
        WalkContext context);

    /**
     * Encode header and body in one pass.
     *
     * @param body      root object of the message.
     * @param dst       destination buffer.
     * @param offset    where the header starts.
     * @param available bytes available from {@code offset}.
     * @param context   scratch state owned by the calling encoder.
     * @return bytes written including the header.
     */
    int encode(JsonNode body, MutableDirectBuffer dst, int offset, int available, WalkContext context);

    /**
     * Compute the encoded length of header and body without writing.
     *
     * @param body    root object of the message.
     * @param context scratch state owned by the calling encoder.
     * @return bytes that {@link #encode} would write.
     */
    int encodedLength(JsonNode body, WalkContext context);
}
