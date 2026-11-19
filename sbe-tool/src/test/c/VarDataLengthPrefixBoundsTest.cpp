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
#include <cerrno>
#include <stdexcept>
#include <string>

#include <gtest/gtest.h>

#include "wire_bounds_test/versioned.h"
#include "WireBuffer.h"

class CVarDataLengthPrefixBoundsTest : public testing::Test
{
public:
    // Fixed block and an empty group, followed by the var data length prefix.
    static WireBuffer prefix()
    {
        WireBuffer buffer;
        buffer.u64(1).u64(2).u64(3).u16(16).u16(0);
        return buffer;
    }

    wire_bounds_test_versioned *decode(WireBuffer &buffer)
    {
        if (!wire_bounds_test_versioned_wrap_for_decode(&m_decoder, buffer.data(), 0, 24, 1, buffer.size()) ||
            !wire_bounds_test_versioned_get_entries(&m_decoder, &m_entries))
        {
            throw std::runtime_error(sbe_strerror(errno));
        }
        errno = 0;
        return &m_decoder;
    }

private:
    wire_bounds_test_versioned m_decoder = {};
    wire_bounds_test_versioned_entries m_entries = {};
};

TEST_F(CVarDataLengthPrefixBoundsTest, shouldRejectBufferTruncatedInLengthPrefix)
{
    WireBuffer buffer = prefix();
    buffer.u16(2);
    char dst[16];

    EXPECT_EQ(wire_bounds_test_versioned_payload_length(decode(buffer)), 0u);
    EXPECT_EQ(errno, E100);
    EXPECT_EQ(wire_bounds_test_versioned_payload(decode(buffer)), nullptr);
    EXPECT_EQ(errno, E100);
    EXPECT_EQ(wire_bounds_test_versioned_get_payload(decode(buffer), dst, sizeof(dst)), 0u);
    EXPECT_EQ(errno, E100);
    EXPECT_EQ(wire_bounds_test_versioned_get_payload_as_string_view(decode(buffer)).data, nullptr);
    EXPECT_EQ(errno, E100);
}

TEST_F(CVarDataLengthPrefixBoundsTest, shouldDecodeVarDataEndingAtBufferEnd)
{
    WireBuffer buffer = prefix();
    buffer.u32(3).bytes("abc");
    char dst[16];

    EXPECT_EQ(wire_bounds_test_versioned_payload_length(decode(buffer)), 3u);
    EXPECT_EQ(std::string(wire_bounds_test_versioned_payload(decode(buffer)), 3), "abc");
    EXPECT_EQ(wire_bounds_test_versioned_get_payload(decode(buffer), dst, sizeof(dst)), 3u);

    wire_bounds_test_versioned *decoder = decode(buffer);
    const wire_bounds_test_versioned_string_view view = wire_bounds_test_versioned_get_payload_as_string_view(decoder);
    EXPECT_EQ(std::string(view.data, view.length), "abc");
    EXPECT_EQ(wire_bounds_test_versioned_sbe_position(decoder), buffer.size());
}
