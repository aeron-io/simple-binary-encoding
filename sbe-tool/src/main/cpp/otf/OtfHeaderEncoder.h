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
#ifndef _OTF_HEADERENCODER_H
#define _OTF_HEADERENCODER_H

#include <cstdint>
#include <memory>
#include <vector>
#include <string>

#include "Token.h"

namespace sbe {
namespace otf {

/*
 * Writes an SBE message header. Inverse of OtfHeaderDecoder: it locates the same well-known
 * header fields by name and writes them, rather than reading them.
 */
class OtfHeaderEncoder
{
public:
    explicit OtfHeaderEncoder(const std::shared_ptr<std::vector<Token>> &tokens)
    {
        m_encodedLength = tokens->at(0).encodedLength();

        for (Token &token : *tokens)
        {
            const std::string &name = token.name();

            if (name == "blockLength")    { m_blockLength = field(token); }
            else if (name == "templateId"){ m_templateId = field(token); }
            else if (name == "schemaId")  { m_schemaId = field(token); }
            else if (name == "version")   { m_schemaVersion = field(token); }
        }

        require(m_blockLength, "blockLength");
        require(m_templateId, "templateId");
        require(m_schemaId, "schemaId");
        require(m_schemaVersion, "version");
    }

    inline std::uint32_t encodedLength() const
    {
        return static_cast<std::uint32_t>(m_encodedLength);
    }

    /* All header elements are unsigned integers per the SBE specification. */

    void setBlockLength(char *headerBuffer, std::uint64_t value) const   { put(m_blockLength, headerBuffer, value); }
    void setTemplateId(char *headerBuffer, std::uint64_t value) const    { put(m_templateId, headerBuffer, value); }
    void setSchemaId(char *headerBuffer, std::uint64_t value) const      { put(m_schemaId, headerBuffer, value); }
    void setSchemaVersion(char *headerBuffer, std::uint64_t value) const { put(m_schemaVersion, headerBuffer, value); }

    void encode(
        char *headerBuffer,
        std::uint64_t blockLength,
        std::uint64_t templateId,
        std::uint64_t schemaId,
        std::uint64_t schemaVersion) const
    {
        setBlockLength(headerBuffer, blockLength);
        setTemplateId(headerBuffer, templateId);
        setSchemaId(headerBuffer, schemaId);
        setSchemaVersion(headerBuffer, schemaVersion);
    }

private:
    struct Field
    {
        std::int32_t offset = -1;
        PrimitiveType type = PrimitiveType::NONE;
        ByteOrder byteOrder = ByteOrder::SBE_LITTLE_ENDIAN;
    };

    static Field field(const Token &token)
    {
        return { token.offset(), token.encoding().primitiveType(), token.encoding().byteOrder() };
    }

    static void require(const Field &f, const char *name)
    {
        if (f.offset < 0)
        {
            throw std::runtime_error(std::string(name) + " token not found");
        }
    }

    static void put(const Field &f, char *headerBuffer, std::uint64_t value)
    {
        Encoding::putUInt(f.type, f.byteOrder, headerBuffer + f.offset, value);
    }

    std::int32_t m_encodedLength = 0;
    Field m_blockLength;
    Field m_templateId;
    Field m_schemaId;
    Field m_schemaVersion;
};

}}

#endif
