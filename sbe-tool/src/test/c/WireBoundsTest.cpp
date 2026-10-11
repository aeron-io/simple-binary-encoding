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
#include <cerrno>
#include <cstdint>
#include <limits>
#include <stdexcept>
#include <string>
#include <vector>

#include <gtest/gtest.h>

#include "wire_bounds_test/versioned.h"
#include "wire_bounds_test/wideLength.h"

#define WBT(name) wire_bounds_test_##name

/*
 * Buffers are built byte by byte in little-endian order and sized exactly to the bytes written, so a read past
 * the end of the buffer is a read past the end of the allocation.
 */
class WireBuffer
{
public:
    WireBuffer &u8(const std::uint8_t value)
    {
        m_bytes.push_back(static_cast<char>(value));
        return *this;
    }

    WireBuffer &u16(const std::uint16_t value)
    {
        return putLittleEndian(value, 2);
    }

    WireBuffer &u32(const std::uint32_t value)
    {
        return putLittleEndian(value, 4);
    }

    WireBuffer &u64(const std::uint64_t value)
    {
        return putLittleEndian(value, 8);
    }

    WireBuffer &bytes(const std::string &value)
    {
        m_bytes.insert(m_bytes.end(), value.begin(), value.end());
        return *this;
    }

    char *data()
    {
        return m_bytes.data();
    }

    std::uint64_t size() const
    {
        return m_bytes.size();
    }

private:
    WireBuffer &putLittleEndian(std::uint64_t value, const int length)
    {
        for (int i = 0; i < length; i++)
        {
            m_bytes.push_back(static_cast<char>(value & 0xFF));
            value >>= 8;
        }
        return *this;
    }

    std::vector<char> m_bytes;
};

static WireBuffer versionedBlockV1()
{
    WireBuffer buffer;
    buffer.u64(1).u64(2).u64(3);
    return buffer;
}

TEST(CWireBoundsTest, shouldRejectMessageBlockLengthZeroWithTruncatedBuffer)
{
    WireBuffer buffer;
    buffer.u16(0).u16(WBT(versioned_sbe_template_id)()).u16(WBT(versioned_sbe_schema_id)()).u16(1);
    WBT(versioned) decoder;

    errno = 0;
    EXPECT_EQ(WBT(versioned_wrap_for_decode)(&decoder, buffer.data(), 8, 0, 1, buffer.size()), nullptr);
    EXPECT_EQ(errno, E100);
}

TEST(CWireBoundsTest, shouldRejectMessageBlockLengthShorterThanActingVersionFields)
{
    WireBuffer buffer = versionedBlockV1();
    buffer.u16(16).u16(0).u32(0);
    WBT(versioned) decoder;

    EXPECT_EQ(WBT(versioned_wrap_for_decode)(&decoder, buffer.data(), 0, 8, 0, buffer.size()), nullptr);
    EXPECT_EQ(WBT(versioned_wrap_for_decode)(&decoder, buffer.data(), 0, 16, 1, buffer.size()), nullptr);
    EXPECT_NE(WBT(versioned_wrap_for_decode)(&decoder, buffer.data(), 0, 24, 1, buffer.size()), nullptr);
}

TEST(CWireBoundsTest, shouldDecodeOlderVersionWithShorterBlocks)
{
    WireBuffer buffer;
    buffer.u64(1).u64(2);
    buffer.u16(8).u16(1).u64(3);
    buffer.u32(2).bytes("hi");
    WBT(versioned) decoder;
    WBT(versioned_entries) entries;

    ASSERT_NE(WBT(versioned_wrap_for_decode)(&decoder, buffer.data(), 0, 16, 0, buffer.size()), nullptr);
    EXPECT_EQ(WBT(versioned_a)(&decoder), 1u);
    EXPECT_EQ(WBT(versioned_b)(&decoder), 2u);
    EXPECT_EQ(WBT(versioned_c)(&decoder), WBT(versioned_c_null_value)());

    ASSERT_NE(WBT(versioned_get_entries)(&decoder, &entries), nullptr);
    ASSERT_EQ(WBT(versioned_entries_count)(&entries), 1u);
    ASSERT_NE(WBT(versioned_entries_next)(&entries), nullptr);
    EXPECT_EQ(WBT(versioned_entries_x)(&entries), 3u);
    EXPECT_EQ(WBT(versioned_entries_y)(&entries), WBT(versioned_entries_y_null_value)());

    const WBT(versioned_string_view) view = WBT(versioned_get_payload_as_string_view)(&decoder);
    ASSERT_NE(view.data, nullptr);
    EXPECT_EQ(std::string(view.data, view.length), "hi");
    EXPECT_EQ(WBT(versioned_sbe_position)(&decoder), buffer.size());
}

TEST(CWireBoundsTest, shouldRejectGroupBlockLengthZero)
{
    WireBuffer buffer = versionedBlockV1();
    buffer.u16(0).u16(2);
    WBT(versioned) decoder;
    WBT(versioned_entries) entries;

    ASSERT_NE(WBT(versioned_wrap_for_decode)(&decoder, buffer.data(), 0, 24, 1, buffer.size()), nullptr);
    errno = 0;
    EXPECT_EQ(WBT(versioned_get_entries)(&decoder, &entries), nullptr);
    EXPECT_EQ(errno, E108);
}

TEST(CWireBoundsTest, shouldAcceptEmptyGroupWithBlockLengthZero)
{
    WireBuffer buffer = versionedBlockV1();
    buffer.u16(0).u16(0).u32(0);
    WBT(versioned) decoder;
    WBT(versioned_entries) entries;

    ASSERT_NE(WBT(versioned_wrap_for_decode)(&decoder, buffer.data(), 0, 24, 1, buffer.size()), nullptr);
    ASSERT_NE(WBT(versioned_get_entries)(&decoder, &entries), nullptr);
    EXPECT_EQ(WBT(versioned_entries_count)(&entries), 0u);
    EXPECT_EQ(WBT(versioned_payload_length)(&decoder), 0u);
}

class CWireBoundsVarDataTest : public testing::Test
{
public:
    static WireBuffer prefix()
    {
        WireBuffer buffer = versionedBlockV1();
        buffer.u16(16).u16(0);
        return buffer;
    }

    WBT(versioned) *decode(WireBuffer &buffer)
    {
        if (!WBT(versioned_wrap_for_decode)(&m_decoder, buffer.data(), 0, 24, 1, buffer.size()) ||
            !WBT(versioned_get_entries)(&m_decoder, &m_entries))
        {
            throw std::runtime_error(sbe_strerror(errno));
        }
        errno = 0;
        return &m_decoder;
    }

private:
    WBT(versioned) m_decoder = {};
    WBT(versioned_entries) m_entries = {};
};

TEST_F(CWireBoundsVarDataTest, shouldRejectLengthPastBufferEnd)
{
    WireBuffer buffer = prefix();
    buffer.u32(4096).bytes("ab");
    char dst[16];

    EXPECT_EQ(WBT(versioned_get_payload)(decode(buffer), dst, sizeof(dst)), 0u);
    EXPECT_EQ(errno, E100);
    EXPECT_EQ(WBT(versioned_payload)(decode(buffer)), nullptr);
    EXPECT_EQ(errno, E100);
    EXPECT_EQ(WBT(versioned_get_payload_as_string_view)(decode(buffer)).data, nullptr);
    EXPECT_EQ(errno, E100);
}

TEST_F(CWireBoundsVarDataTest, shouldRejectBufferTruncatedInLengthPrefix)
{
    WireBuffer buffer = prefix();
    buffer.u16(2);
    char dst[16];

    EXPECT_EQ(WBT(versioned_payload_length)(decode(buffer)), 0u);
    EXPECT_EQ(errno, E100);
    EXPECT_EQ(WBT(versioned_payload)(decode(buffer)), nullptr);
    EXPECT_EQ(errno, E100);
    EXPECT_EQ(WBT(versioned_get_payload)(decode(buffer), dst, sizeof(dst)), 0u);
    EXPECT_EQ(errno, E100);
    EXPECT_EQ(WBT(versioned_get_payload_as_string_view)(decode(buffer)).data, nullptr);
    EXPECT_EQ(errno, E100);
}

TEST_F(CWireBoundsVarDataTest, shouldDecodeVarDataEndingAtBufferEnd)
{
    WireBuffer buffer = prefix();
    buffer.u32(3).bytes("abc");
    char dst[16];

    EXPECT_EQ(WBT(versioned_payload_length)(decode(buffer)), 3u);
    EXPECT_EQ(WBT(versioned_get_payload)(decode(buffer), dst, sizeof(dst)), 3u);
    EXPECT_EQ(std::string(WBT(versioned_payload)(decode(buffer)), 3), "abc");
    const WBT(versioned_string_view) view = WBT(versioned_get_payload_as_string_view)(decode(buffer));
    EXPECT_EQ(std::string(view.data, view.length), "abc");
    EXPECT_EQ(WBT(versioned_sbe_position)(decode(buffer)), buffer.size() - 7);
}

TEST(CWireBoundsTest, shouldRejectUint64LengthThatWrapsPosition)
{
    WireBuffer buffer;
    buffer.u32(1).u8('x').u64(std::numeric_limits<std::uint64_t>::max() - 3).bytes("abcd");
    char dst[16];
    WBT(wideLength) decoder;

    ASSERT_NE(WBT(wideLength_wrap_for_decode)(&decoder, buffer.data(), 0, 5, 1, buffer.size()), nullptr);
    errno = 0;
    EXPECT_EQ(WBT(wideLength_get_payload)(&decoder, dst, sizeof(dst)), 0u);
    EXPECT_EQ(errno, E100);

    ASSERT_NE(WBT(wideLength_wrap_for_decode)(&decoder, buffer.data(), 0, 5, 1, buffer.size()), nullptr);
    errno = 0;
    EXPECT_EQ(WBT(wideLength_payload)(&decoder), nullptr);
    EXPECT_EQ(errno, E100);

    ASSERT_NE(WBT(wideLength_wrap_for_decode)(&decoder, buffer.data(), 0, 5, 1, buffer.size()), nullptr);
    errno = 0;
    EXPECT_EQ(WBT(wideLength_get_payload_as_string_view)(&decoder).data, nullptr);
    EXPECT_EQ(errno, E100);
}
