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
package org.agrona.sbe;

import org.agrona.MutableDirectBuffer;

/**
 * Interface implemented by generated DTO message encoders.
 */
public interface MessageDtoEncoder
{
    /**
     * Encode the DTO into the buffer at a given offset.
     *
     * @param buffer to which the message should be encoded.
     * @param offset at which the encoding should occur.
     * @return the number of bytes encoded.
     */
    int encode(MutableDirectBuffer buffer, int offset);

    /**
     * Encode the DTO into the buffer at a given offset including the message header.
     *
     * @param buffer to which the message should be encoded.
     * @param offset at which the encoding should occur.
     * @return the number of bytes encoded.
     */
    int encodeWithHeader(MutableDirectBuffer buffer, int offset);

    /**
     * Compute the encoded length in bytes for the DTO without the message header.
     *
     * @return the encoded length in bytes for the DTO without the message header.
     */
    int computeEncodedLength();

    /**
     * Compute the encoded length in bytes for the DTO including the message header.
     *
     * @return the encoded length in bytes for the DTO including the message header.
     */
    int computeEncodedLengthWithHeader();
}
