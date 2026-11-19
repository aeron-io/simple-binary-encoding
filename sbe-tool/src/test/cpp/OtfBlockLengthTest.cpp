/*
 * Copyright 2026 Real Logic Limited.
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
#include <cstdint>
#include <memory>
#include <stdexcept>
#include <vector>

#include "gtest/gtest.h"
#include "otf/IrDecoder.h"
#include "otf/OtfMessageDecoder.h"
#include "WireBuffer.h"

using namespace sbe::otf;

static const char *IR_FILENAME = "wire-bounds-test-schema.sbeir";
static const int VERSIONED_TEMPLATE_ID = 1;

class CountingListener : public OtfMessageDecoder::BasicTokenListener
{
public:
    void onEncoding(Token &fieldToken, const char *buffer, Token &typeToken, std::uint64_t actingVersion) override
    {
        // reads the value of a field present at the acting version, as a printing listener would
        if (fieldToken.tokenVersion() <= static_cast<std::int32_t>(actingVersion))
        {
            lastValue = typeToken.encoding().getAsUInt(buffer);
        }
        ++encodingCount;
    }

    void onBeginGroup(Token &, std::uint64_t, std::uint64_t) override
    {
        ++beginGroupCount;
    }

    void onVarData(Token &, const char *, std::uint64_t, Token &) override
    {
        ++varDataCount;
    }

    std::uint64_t lastValue = 0;
    std::uint64_t encodingCount = 0;
    std::uint64_t beginGroupCount = 0;
    std::uint64_t varDataCount = 0;
};

class OtfBlockLengthTest : public testing::Test
{
protected:
    void SetUp() override
    {
        ASSERT_EQ(m_ir.decode(IR_FILENAME), 0);
        m_tokens = m_ir.message(VERSIONED_TEMPLATE_ID, 1);
        ASSERT_TRUE(m_tokens);
    }

    IrDecoder m_ir;
    std::shared_ptr<std::vector<Token>> m_tokens;
};

TEST_F(OtfBlockLengthTest, shouldRejectMessageBlockLengthZeroBeforeDispatchingFields)
{
    // an empty group and empty var data follow a zero length block, the fixed fields would lie past the end
    WireBuffer buffer;
    buffer.u16(16).u16(0).u32(0);
    CountingListener listener;

    EXPECT_THROW(
        OtfMessageDecoder::decode(buffer.data(), buffer.size(), 1, 0, m_tokens, listener), std::runtime_error);
    EXPECT_EQ(listener.encodingCount, 0u);
}

TEST_F(OtfBlockLengthTest, shouldRejectGroupBlockLengthZeroBeforeIterating)
{
    WireBuffer buffer;
    buffer.u64(1).u64(2).u64(3).u16(0).u16(65535);
    CountingListener listener;

    EXPECT_THROW(
        OtfMessageDecoder::decode(buffer.data(), buffer.size(), 1, 24, m_tokens, listener), std::runtime_error);
    EXPECT_EQ(listener.beginGroupCount, 0u);
}

TEST_F(OtfBlockLengthTest, shouldDecodeOlderVersionWithShorterBlocks)
{
    WireBuffer buffer;
    buffer.u64(1).u64(2);
    buffer.u16(8).u16(1).u64(3);
    buffer.u32(2).bytes("hi");
    CountingListener listener;

    EXPECT_EQ(OtfMessageDecoder::decode(buffer.data(), buffer.size(), 0, 16, m_tokens, listener), buffer.size());
    EXPECT_EQ(listener.beginGroupCount, 1u);
    EXPECT_EQ(listener.varDataCount, 1u);
}

TEST_F(OtfBlockLengthTest, shouldAcceptEmptyGroupWithBlockLengthZero)
{
    WireBuffer buffer;
    buffer.u64(1).u64(2).u64(3).u16(0).u16(0).u32(0);
    CountingListener listener;

    EXPECT_EQ(OtfMessageDecoder::decode(buffer.data(), buffer.size(), 1, 24, m_tokens, listener), buffer.size());
    EXPECT_EQ(listener.beginGroupCount, 0u);
}

static Token versionedToken(
    const std::int32_t offset, const std::int32_t version, const std::int32_t encodedLength,
    const std::int32_t componentTokenCount, const Signal signal, const PrimitiveType type)
{
    const PrimitiveValue none(type, 0, nullptr);
    return { offset, 0, version, encodedLength, componentTokenCount, signal, "", "",
        Encoding(type, Presence::SBE_REQUIRED, ByteOrder::SBE_LITTLE_ENDIAN, none, none, none, none, "", "", "", "") };
}

TEST(OtfMinBlockLengthTest, shouldNotCountCompositeMembersNewerThanActingVersion)
{
    // field px : composite { int64 mantissa; enum side sinceVersion=1 }, 8 bytes in version 0 and 9 from version 1.
    const std::vector<Token> tokens =
    {
        versionedToken(0, 0, 9, 8, Signal::BEGIN_FIELD, PrimitiveType::NONE),
        versionedToken(0, 0, 9, 6, Signal::BEGIN_COMPOSITE, PrimitiveType::NONE),
        versionedToken(0, 0, 8, 1, Signal::ENCODING, PrimitiveType::INT64),
        versionedToken(8, 1, 1, 3, Signal::BEGIN_ENUM, PrimitiveType::UINT8),
        versionedToken(0, 1, 1, 1, Signal::VALID_VALUE, PrimitiveType::UINT8),
        versionedToken(8, 1, 1, 3, Signal::END_ENUM, PrimitiveType::UINT8),
        versionedToken(0, 0, 9, 6, Signal::END_COMPOSITE, PrimitiveType::NONE),
        versionedToken(0, 0, 9, 8, Signal::END_FIELD, PrimitiveType::NONE)
    };

    EXPECT_EQ(OtfMessageDecoder::minBlockLength(tokens, 0, tokens.size(), 0), 8u);
    EXPECT_EQ(OtfMessageDecoder::minBlockLength(tokens, 0, tokens.size(), 1), 9u);
}
