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
#include <cstdint>
#include <limits>
#include <stdexcept>
#include <string>

#include <gtest/gtest.h>

#include "uint64_var_data_length/wideLength.h"
#include "WireBuffer.h"

class CVarDataUint64LengthTest : public testing::Test
{
public:
    uint64_var_data_length_wideLength *decode(WireBuffer &buffer)
    {
        if (!uint64_var_data_length_wideLength_wrap_for_decode(&m_decoder, buffer.data(), 0, 4, 0, buffer.size()))
        {
            throw std::runtime_error(sbe_strerror(errno));
        }
        errno = 0;
        return &m_decoder;
    }

private:
    uint64_var_data_length_wideLength m_decoder = {};
};

TEST_F(CVarDataUint64LengthTest, shouldRejectLengthThatWrapsPosition)
{
    WireBuffer buffer;
    buffer.u32(1).u64(std::numeric_limits<std::uint64_t>::max() - 3).bytes("abcd");
    char dst[16];

    EXPECT_EQ(uint64_var_data_length_wideLength_get_payload(decode(buffer), dst, sizeof(dst)), 0u);
    EXPECT_EQ(errno, E100);
    EXPECT_EQ(uint64_var_data_length_wideLength_payload(decode(buffer)), nullptr);
    EXPECT_EQ(errno, E100);
    EXPECT_EQ(uint64_var_data_length_wideLength_get_payload_as_string_view(decode(buffer)).data, nullptr);
    EXPECT_EQ(errno, E100);
}

TEST_F(CVarDataUint64LengthTest, shouldDecodeVarDataEndingAtBufferEnd)
{
    WireBuffer buffer;
    buffer.u32(1).u64(4).bytes("abcd");
    char dst[16];

    EXPECT_EQ(uint64_var_data_length_wideLength_get_payload(decode(buffer), dst, sizeof(dst)), 4u);
    EXPECT_EQ(std::string(dst, 4), "abcd");
    EXPECT_EQ(std::string(uint64_var_data_length_wideLength_payload(decode(buffer)), 4), "abcd");

    uint64_var_data_length_wideLength *decoder = decode(buffer);
    const uint64_var_data_length_wideLength_string_view view =
        uint64_var_data_length_wideLength_get_payload_as_string_view(decoder);
    EXPECT_EQ(std::string(view.data, view.length), "abcd");
    EXPECT_EQ(uint64_var_data_length_wideLength_sbe_position(decoder), buffer.size());
}
