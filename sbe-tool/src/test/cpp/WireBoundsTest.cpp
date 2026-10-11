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
#include <sstream>
#include <stdexcept>
#include <string>

#include "gtest/gtest.h"
#include "gmock/gmock.h"
#include "wire_bounds_test/Versioned.h"
#include "wire_bounds_test/WideLength.h"
#include "WireBuffer.h"

using namespace wire::bounds::test;
using ::testing::HasSubstr;

static WireBuffer versionedBlockV1()
{
    WireBuffer buffer;
    buffer.u64(1).u64(2).u64(3);
    return buffer;
}

TEST(WireBoundsTest, shouldRejectMessageBlockLengthZeroWithTruncatedBuffer)
{
    WireBuffer buffer;
    buffer.u16(0).u16(Versioned::sbeTemplateId()).u16(Versioned::sbeSchemaId()).u16(1);

    Versioned decoder;
    EXPECT_THROW(
        decoder.wrapForDecode(buffer.data(), MessageHeader::encodedLength(), 0, 1, buffer.size()),
        std::runtime_error);
    EXPECT_THROW(Versioned(buffer.data(), MessageHeader::encodedLength(), buffer.size(), 0, 1), std::runtime_error);
}

TEST(WireBoundsTest, shouldRejectMessageBlockLengthShorterThanActingVersionFields)
{
    WireBuffer buffer = versionedBlockV1();
    buffer.u16(16).u16(0).u32(0);

    Versioned decoder;
    EXPECT_THROW(decoder.wrapForDecode(buffer.data(), 0, 8, 0, buffer.size()), std::runtime_error);
    EXPECT_THROW(decoder.wrapForDecode(buffer.data(), 0, 16, 1, buffer.size()), std::runtime_error);
    EXPECT_NO_THROW(decoder.wrapForDecode(buffer.data(), 0, 24, 1, buffer.size()));
}

TEST(WireBoundsTest, shouldDecodeOlderVersionWithShorterBlocks)
{
    WireBuffer buffer;
    buffer.u64(1).u64(2);
    buffer.u16(8).u16(1).u64(3);
    buffer.u32(2).bytes("hi");

    Versioned decoder;
    decoder.wrapForDecode(buffer.data(), 0, 16, 0, buffer.size());
    EXPECT_EQ(decoder.a(), 1u);
    EXPECT_EQ(decoder.b(), 2u);
    EXPECT_EQ(decoder.c(), Versioned::cNullValue());

    Versioned::Entries &entries = decoder.entries();
    ASSERT_EQ(entries.count(), 1u);
    entries.next();
    EXPECT_EQ(entries.x(), 3u);
    EXPECT_EQ(entries.y(), Versioned::Entries::yNullValue());

    EXPECT_EQ(decoder.getPayloadAsString(), "hi");
    EXPECT_EQ(decoder.sbePosition(), buffer.size());
}

TEST(WireBoundsTest, shouldRejectGroupBlockLengthZero)
{
    WireBuffer buffer = versionedBlockV1();
    buffer.u16(0).u16(2);

    Versioned decoder;
    decoder.wrapForDecode(buffer.data(), 0, Versioned::sbeBlockLength(), 1, buffer.size());
    EXPECT_THROW(
        {
            try
            {
                (void)decoder.entries();
            }
            catch (const std::runtime_error &e)
            {
                EXPECT_THAT(e.what(), HasSubstr("E108"));
                throw;
            }
        },
        std::runtime_error);
}

TEST(WireBoundsTest, shouldAcceptEmptyGroupWithBlockLengthZero)
{
    WireBuffer buffer = versionedBlockV1();
    buffer.u16(0).u16(0).u32(0);

    Versioned decoder;
    decoder.wrapForDecode(buffer.data(), 0, Versioned::sbeBlockLength(), 1, buffer.size());
    EXPECT_EQ(decoder.entries().count(), 0u);
    EXPECT_EQ(decoder.getPayloadAsString(), "");
}

class WireBoundsVarDataTest : public testing::Test
{
public:
    // Message with an empty group, then the start of the var data field.
    static WireBuffer prefix()
    {
        WireBuffer buffer = versionedBlockV1();
        buffer.u16(16).u16(0);
        return buffer;
    }

    Versioned &decode(WireBuffer &buffer)
    {
        m_decoder.wrapForDecode(buffer.data(), 0, Versioned::sbeBlockLength(), 1, buffer.size());
        (void)m_decoder.entries();
        return m_decoder;
    }

private:
    Versioned m_decoder;
};

TEST_F(WireBoundsVarDataTest, shouldRejectLengthPastBufferEndInGetAsString)
{
    WireBuffer buffer = prefix();
    buffer.u32(4096).bytes("ab");
    char dst[16];

    EXPECT_THROW((void)decode(buffer).getPayloadAsString(), std::runtime_error);
    EXPECT_THROW((void)decode(buffer).getPayloadAsJsonEscapedString(), std::runtime_error);
    EXPECT_THROW((void)decode(buffer).getPayload(dst, sizeof(dst)), std::runtime_error);
    EXPECT_THROW((void)decode(buffer).payload(), std::runtime_error);
    EXPECT_THROW((void)decode(buffer).skipPayload(), std::runtime_error);

    std::ostringstream out;
    Versioned display(buffer.data(), 0, buffer.size(), Versioned::sbeBlockLength(), 1);
    EXPECT_THROW(out << display, std::runtime_error);
}

TEST_F(WireBoundsVarDataTest, shouldRejectBufferTruncatedInLengthPrefix)
{
    WireBuffer buffer = prefix();
    buffer.u16(2);
    char dst[16];

    EXPECT_THROW((void)decode(buffer).payloadLength(), std::runtime_error);
    EXPECT_THROW((void)decode(buffer).skipPayload(), std::runtime_error);
    EXPECT_THROW((void)decode(buffer).payload(), std::runtime_error);
    EXPECT_THROW((void)decode(buffer).getPayload(dst, sizeof(dst)), std::runtime_error);
    EXPECT_THROW((void)decode(buffer).getPayloadAsString(), std::runtime_error);
}

TEST_F(WireBoundsVarDataTest, shouldDecodeVarDataEndingAtBufferEnd)
{
    WireBuffer buffer = prefix();
    buffer.u32(3).bytes("abc");
    char dst[16];

    EXPECT_EQ(decode(buffer).payloadLength(), 3u);
    EXPECT_EQ(decode(buffer).skipPayload(), 3u);
    EXPECT_EQ(decode(buffer).getPayload(dst, sizeof(dst)), 3u);
    EXPECT_EQ(std::string(decode(buffer).payload(), 3), "abc");
    EXPECT_EQ(decode(buffer).getPayloadAsString(), "abc");

    Versioned skipper(buffer.data(), 0, buffer.size(), Versioned::sbeBlockLength(), 1);
    EXPECT_EQ(skipper.decodeLength(), buffer.size());
}

TEST(WireBoundsTest, shouldRejectUint64LengthThatWrapsPosition)
{
    WireBuffer buffer;
    buffer.u32(1).u8('x').u64(std::numeric_limits<std::uint64_t>::max() - 3).bytes("abcd");
    char dst[16];

    WideLength decoder;
    const auto decode = [&]() -> WideLength &
    {
        return decoder.wrapForDecode(buffer.data(), 0, WideLength::sbeBlockLength(), 1, buffer.size());
    };

    EXPECT_THROW((void)decode().getPayload(dst, sizeof(dst)), std::runtime_error);
    EXPECT_THROW((void)decode().getPayloadAsString(), std::runtime_error);
    EXPECT_THROW((void)decode().payload(), std::runtime_error);
    EXPECT_THROW((void)decode().skipPayload(), std::runtime_error);
}

TEST(WireBoundsTest, shouldEscapeQuoteAndBackslashInSingleCharDisplay)
{
    for (const char ch : { '"', '\\' })
    {
        WireBuffer buffer;
        buffer.u32(1).u8(static_cast<std::uint8_t>(ch)).u64(0);

        WideLength decoder(buffer.data(), 0, buffer.size(), WideLength::sbeBlockLength(), 1);
        std::ostringstream out;
        out << decoder;

        EXPECT_THAT(out.str(), HasSubstr(std::string("\"ch\": \"\\") + ch + "\""));
    }
}
