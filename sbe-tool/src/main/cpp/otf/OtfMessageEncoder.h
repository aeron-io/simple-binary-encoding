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
#ifndef _OTF_MESSAGEENCODER_H
#define _OTF_MESSAGEENCODER_H

#include <memory>
#include <stdexcept>
#include <utility>
#include <vector>

#include "Token.h"

using namespace sbe::otf;

namespace sbe { namespace otf { namespace OtfMessageEncoder {

/*
 * The on-the-fly encoder: the structural inverse of OtfMessageDecoder. It walks the same IR token
 * stream and writes wire bytes, obtaining each field's value from a TokenEncoder. It owns all
 * schema-driven mechanics (offsets, block/dimension framing, var-data length prefixes,
 * constant-field skipping, byte order and acting-version gating).
 *
 * A TokenEncoder mirrors OtfMessageDecoder's TokenListener, with two differences: the char*
 * buffers passed to its callbacks are destinations to be written (via Token::encoding().putAs*),
 * not sources to read; and because repeating-group counts and variable-length-data lengths are
 * not yet on the wire when encoding, the encoder queries them via numInGroup()/varDataLength().
 * See BasicTokenEncoder for the full callback surface.
 */

class BasicTokenEncoder
{
public:
    virtual ~BasicTokenEncoder() = default;

    virtual void onBeginMessage(Token &) {}

    virtual void onEndMessage(Token &) {}

    virtual void onEncoding(Token &, char *, Token &, std::uint64_t) {}

    virtual void onEnum(Token &, char *, std::vector<Token> &, std::size_t, std::size_t, std::uint64_t) {}

    virtual void onBitSet(Token &, char *, std::vector<Token> &, std::size_t, std::size_t, std::uint64_t) {}

    virtual void onBeginComposite(Token &, std::vector<Token> &, std::size_t, std::size_t) {}

    virtual void onEndComposite(Token &, std::vector<Token> &, std::size_t, std::size_t) {}

    virtual std::uint64_t numInGroup(Token &) { return 0; }

    virtual void onBeginGroup(Token &, std::uint64_t, std::uint64_t) {}

    virtual void onEndGroup(Token &, std::uint64_t, std::uint64_t) {}

    virtual std::uint64_t varDataLength(Token &) { return 0; }

    virtual void onVarData(Token &, char *, std::uint64_t, Token &) {}
};

template<typename TokenEncoder>
static void encodeComposite(
    char *buffer,
    std::size_t bufferIndex,
    std::uint64_t actingVersion,
    std::vector<Token> &tokens,
    Token &fieldToken,
    std::size_t tokenIndex,
    std::size_t toIndex,
    TokenEncoder &encoder)
{
    // Mirror of OtfMessageDecoder::decodeComposite: onBeginComposite/onEndComposite bracket the walk
    // and carry the enclosing fieldToken, which is also threaded to enum/set members and nested
    // composites (matching the decoder's callback contract).
    encoder.onBeginComposite(fieldToken, tokens, tokenIndex, toIndex);

    for (std::size_t i = tokenIndex + 1; i < toIndex;)
    {
        Token &token = tokens.at(i);
        const std::size_t nextIndex = i + token.componentTokenCount();
        const std::size_t offset = bufferIndex + token.offset();

        switch (token.signal())
        {
            case Signal::BEGIN_COMPOSITE:
                encodeComposite(buffer, offset, actingVersion, tokens, fieldToken, i, nextIndex - 1, encoder);
                break;

            case Signal::BEGIN_ENUM:
                if (!token.isConstantEncoding())
                {
                    encoder.onEnum(fieldToken, buffer + offset, tokens, i, nextIndex - 1, actingVersion);
                }
                break;

            case Signal::BEGIN_SET:
                if (!token.isConstantEncoding())
                {
                    encoder.onBitSet(fieldToken, buffer + offset, tokens, i, nextIndex - 1, actingVersion);
                }
                break;

            case Signal::ENCODING:
                // constant-presence fields carry no wire bytes; their value lives in the schema
                if (!token.isConstantEncoding())
                {
                    encoder.onEncoding(token, buffer + offset, token, actingVersion);
                }
                break;

            default:
                break;
        }

        i += token.componentTokenCount();
    }

    encoder.onEndComposite(fieldToken, tokens, tokenIndex, toIndex);
}

template<typename TokenEncoder>
static std::size_t encodeFields(
    char *buffer,
    std::size_t bufferIndex,
    std::uint64_t actingVersion,
    std::vector<Token> &tokens,
    std::size_t tokenIndex,
    const std::size_t numTokens,
    TokenEncoder &encoder)
{
    while (tokenIndex < numTokens)
    {
        Token &fieldToken = tokens.at(tokenIndex);
        if (Signal::BEGIN_FIELD != fieldToken.signal())
        {
            break;
        }

        const std::size_t nextFieldIndex = tokenIndex + fieldToken.componentTokenCount();
        tokenIndex++;

        Token &typeToken = tokens.at(tokenIndex);
        const std::size_t offset = bufferIndex + typeToken.offset();

        switch (typeToken.signal())
        {
            case Signal::BEGIN_COMPOSITE:
                encodeComposite(buffer, offset, actingVersion, tokens, fieldToken, tokenIndex, nextFieldIndex - 2, encoder);
                break;

            case Signal::BEGIN_ENUM:
                if (!fieldToken.isConstantEncoding())
                {
                    encoder.onEnum(fieldToken, buffer + offset, tokens, tokenIndex, nextFieldIndex - 2, actingVersion);
                }
                break;

            case Signal::BEGIN_SET:
                if (!fieldToken.isConstantEncoding())
                {
                    encoder.onBitSet(fieldToken, buffer + offset, tokens, tokenIndex, nextFieldIndex - 2, actingVersion);
                }
                break;

            case Signal::ENCODING:
                if (!typeToken.isConstantEncoding())
                {
                    encoder.onEncoding(fieldToken, buffer + offset, typeToken, actingVersion);
                }
                break;

            default:
                break;
        }

        tokenIndex = nextFieldIndex;
    }

    return tokenIndex;
}

template<typename TokenEncoder>
static std::size_t encodeData(
    char *buffer,
    std::size_t bufferIndex,
    const std::size_t length,
    std::vector<Token> &tokens,
    std::size_t tokenIndex,
    const std::size_t numTokens,
    std::uint64_t actingVersion,
    TokenEncoder &encoder)
{
    while (tokenIndex < numTokens)
    {
        Token &token = tokens.at(tokenIndex);
        if (Signal::BEGIN_VAR_DATA != token.signal())
        {
            break;
        }

        const bool isPresent = token.tokenVersion() <= static_cast<std::int32_t>(actingVersion);

        if (isPresent)
        {
            Token &lengthToken = tokens.at(tokenIndex + 2);
            Token &dataToken = tokens.at(tokenIndex + 3);

            if ((bufferIndex + dataToken.offset()) > length)
            {
                throw std::runtime_error("length too short for data length field");
            }

            const std::uint64_t dataLength = encoder.varDataLength(token);
            lengthToken.encoding().putAsUInt(buffer + bufferIndex + lengthToken.offset(), dataLength);

            bufferIndex += dataToken.offset();

            if ((bufferIndex + dataLength) > length)
            {
                throw std::runtime_error("length too short for data field");
            }

            encoder.onVarData(token, buffer + bufferIndex, dataLength, dataToken);
            bufferIndex += dataLength;
        }

        tokenIndex += token.componentTokenCount();
    }

    return bufferIndex;
}

template<typename TokenEncoder>
static std::pair<std::size_t, std::size_t> encodeGroups(
    char *buffer,
    std::size_t bufferIndex,
    const std::size_t length,
    std::uint64_t actingVersion,
    std::vector<Token> &tokens,
    std::size_t tokenIndex,
    const std::size_t numTokens,
    TokenEncoder &encoder)
{
    while (tokenIndex < numTokens)
    {
        Token &token = tokens.at(tokenIndex);
        if (Signal::BEGIN_GROUP != token.signal())
        {
            break;
        }

        const bool isPresent = token.tokenVersion() <= static_cast<std::int32_t>(actingVersion);

        Token &dimensionsTypeComposite = tokens.at(tokenIndex + 1);
        Token &blockLengthToken = tokens.at(tokenIndex + 2);
        Token &numInGroupToken = tokens.at(tokenIndex + 3);

        auto dimensionsLength = static_cast<std::size_t>(dimensionsTypeComposite.encodedLength());
        // a BEGIN_GROUP token's encodedLength is the group's per-entry block length
        auto blockLength = static_cast<std::size_t>(token.encodedLength());
        const std::uint64_t numInGroup = isPresent ? encoder.numInGroup(token) : 0;

        if (isPresent)
        {
            if ((bufferIndex + dimensionsLength) > length)
            {
                throw std::runtime_error("length too short for group dimensions");
            }

            blockLengthToken.encoding().putAsUInt(buffer + bufferIndex + blockLengthToken.offset(), blockLength);
            numInGroupToken.encoding().putAsUInt(buffer + bufferIndex + numInGroupToken.offset(), numInGroup);

            bufferIndex += dimensionsLength;
        }

        size_t beginFieldsIndex = tokenIndex + dimensionsTypeComposite.componentTokenCount() + 1;

        for (std::uint64_t i = 0; i < numInGroup; i++)
        {
            encoder.onBeginGroup(token, i, numInGroup);

            if ((bufferIndex + blockLength) > length)
            {
                throw std::runtime_error("length too short for group blockLength");
            }

            size_t afterFieldsIndex = encodeFields(
                buffer, bufferIndex, actingVersion, tokens, beginFieldsIndex, numTokens, encoder);
            bufferIndex += blockLength;

            std::pair<std::size_t, std::size_t> groupsResult = encodeGroups(
                buffer, bufferIndex, length, actingVersion, tokens, afterFieldsIndex, numTokens, encoder);

            bufferIndex = encodeData(
                buffer, groupsResult.first, length, tokens, groupsResult.second, numTokens, actingVersion, encoder);

            encoder.onEndGroup(token, i, numInGroup);
        }

        tokenIndex += token.componentTokenCount();
    }

    return { bufferIndex, tokenIndex };
}

/**
 * Entry point for the encoder. `buffer` points past the message header; `blockLength` is the
 * message's root block length. Returns the number of bytes written (block + groups + var-data).
 */
template<typename TokenEncoder>
std::size_t encode(
    char *buffer,
    const std::size_t length,
    std::uint64_t actingVersion,
    std::size_t blockLength,
    std::shared_ptr<std::vector<Token>> msgTokens,
    TokenEncoder &encoder)
{
    encoder.onBeginMessage(msgTokens->at(0));

    if (length < blockLength)
    {
        throw std::runtime_error("length too short for message blockLength");
    }

    size_t numTokens = msgTokens->size();
    const size_t tokenIndex = encodeFields(buffer, 0, actingVersion, *msgTokens, 1, numTokens, encoder);

    size_t bufferIndex = blockLength;

    std::pair<std::size_t, std::size_t> groupResult = encodeGroups(
        buffer, bufferIndex, length, actingVersion, *msgTokens, tokenIndex, numTokens, encoder);

    bufferIndex = encodeData(
        buffer, groupResult.first, length, *msgTokens, groupResult.second, numTokens, actingVersion, encoder);

    encoder.onEndMessage(msgTokens->at(numTokens - 1));

    return bufferIndex;
}

}}}

#endif
