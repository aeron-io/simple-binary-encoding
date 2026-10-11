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
#include <cstdint>
#include <limits>
#include <memory>
#include <stdexcept>
#include <vector>

#include "gtest/gtest.h"
#include "gmock/gmock.h"
#include "otf/IrDecoder.h"
#include "otf/OtfMessageDecoder.h"
#include "otf/OtfMessageEncoder.h"
#include "WireBuffer.h"

using namespace sbe::otf;
using ::testing::HasSubstr;

static const char *IR_FILENAME = "wire-bounds-test-schema.sbeir";
static const int VERSIONED_TEMPLATE_ID = 1;
static const int WIDE_LENGTH_TEMPLATE_ID = 2;

static WireBuffer versionedBlockV1()
{
    WireBuffer buffer;
    buffer.u64(1).u64(2).u64(3);
    return buffer;
}

static std::shared_ptr<std::vector<Token>> loadMessage(IrDecoder &ir, const int templateId)
{
    if (ir.decode(IR_FILENAME) != 0)
    {
        throw std::runtime_error("could not load IR");
    }

    return ir.message(templateId, 1);
}

class CountingListener : public OtfMessageDecoder::BasicTokenListener
{
public:
    void onBeginGroup(Token &, std::uint64_t, std::uint64_t) override
    {
        ++beginGroupCount;
    }

    void onVarData(Token &, const char *, std::uint64_t, Token &) override
    {
        ++varDataCount;
    }

    std::uint64_t beginGroupCount = 0;
    std::uint64_t varDataCount = 0;
};

TEST(WireBoundsOtfTest, shouldRejectMessageBlockLengthZero)
{
    IrDecoder ir;
    auto tokens = loadMessage(ir, VERSIONED_TEMPLATE_ID);
    // an empty group and empty var data follow a zero length block, the fixed fields would lie past the end
    WireBuffer buffer;
    buffer.u16(16).u16(0).u32(0);
    CountingListener listener;

    EXPECT_THROW(
        OtfMessageDecoder::decode(buffer.data(), buffer.size(), 1, 0, tokens, listener), std::runtime_error);
}

TEST(WireBoundsOtfTest, shouldRejectGroupBlockLengthZeroBeforeIterating)
{
    IrDecoder ir;
    auto tokens = loadMessage(ir, VERSIONED_TEMPLATE_ID);
    WireBuffer buffer = versionedBlockV1();
    buffer.u16(0).u16(65535);
    CountingListener listener;

    EXPECT_THROW(
        OtfMessageDecoder::decode(buffer.data(), buffer.size(), 1, 24, tokens, listener), std::runtime_error);
    EXPECT_EQ(listener.beginGroupCount, 0u);
}

TEST(WireBoundsOtfTest, shouldDecodeOlderVersionWithShorterBlocks)
{
    IrDecoder ir;
    auto tokens = loadMessage(ir, VERSIONED_TEMPLATE_ID);
    WireBuffer buffer;
    buffer.u64(1).u64(2);
    buffer.u16(8).u16(1).u64(3);
    buffer.u32(2).bytes("hi");
    CountingListener listener;

    EXPECT_EQ(OtfMessageDecoder::decode(buffer.data(), buffer.size(), 0, 16, tokens, listener), buffer.size());
    EXPECT_EQ(listener.beginGroupCount, 1u);
    EXPECT_EQ(listener.varDataCount, 1u);
}

TEST(WireBoundsOtfTest, shouldRejectUint64VarDataLengthThatWraps)
{
    IrDecoder ir;
    auto tokens = loadMessage(ir, WIDE_LENGTH_TEMPLATE_ID);
    WireBuffer buffer;
    buffer.u32(1).u8('x').u64(std::numeric_limits<std::uint64_t>::max()).bytes("abcd");
    CountingListener listener;

    EXPECT_THROW(
        OtfMessageDecoder::decode(buffer.data(), buffer.size(), 1, 5, tokens, listener), std::runtime_error);
    EXPECT_EQ(listener.varDataCount, 0u);
}

class OversizedEncoder : public OtfMessageEncoder::BasicTokenEncoder
{
public:
    OversizedEncoder(const std::uint64_t numInGroup, const std::uint64_t varDataLength) :
        m_numInGroup(numInGroup), m_varDataLength(varDataLength)
    {
    }

    std::uint64_t numInGroup(Token &) override
    {
        return m_numInGroup;
    }

    std::uint64_t varDataLength(Token &) override
    {
        return m_varDataLength;
    }

private:
    std::uint64_t m_numInGroup;
    std::uint64_t m_varDataLength;
};

TEST(WireBoundsOtfTest, shouldRejectNumInGroupTooLargeForWireField)
{
    IrDecoder ir;
    auto tokens = loadMessage(ir, VERSIONED_TEMPLATE_ID);
    // large enough for every entry, so only the width of numInGroup can reject the count
    std::vector<char> buffer(24 + 4 + (65536 * 16) + 4, 0);
    OversizedEncoder encoder(65536, 0);

    EXPECT_THROW(
        {
            try
            {
                OtfMessageEncoder::encode(buffer.data(), buffer.size(), 1, 24, tokens, encoder);
            }
            catch (const std::runtime_error &e)
            {
                EXPECT_THAT(e.what(), HasSubstr("numInGroup too large"));
                throw;
            }
        },
        std::runtime_error);
}

TEST(WireBoundsOtfTest, shouldRejectVarDataLengthTooLargeForLengthField)
{
    IrDecoder ir;
    auto tokens = loadMessage(ir, VERSIONED_TEMPLATE_ID);
    std::vector<char> buffer(64, 0);
    OversizedEncoder encoder(0, 1073741825);

    EXPECT_THROW(
        {
            try
            {
                OtfMessageEncoder::encode(buffer.data(), buffer.size(), 1, 24, tokens, encoder);
            }
            catch (const std::runtime_error &e)
            {
                EXPECT_THAT(e.what(), HasSubstr("var data length too large"));
                throw;
            }
        },
        std::runtime_error);
}
