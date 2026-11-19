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
#include <string>
#include <vector>

#include <gtest/gtest.h>

#include "uint64_length/uint64Lengths.h"

/*
 * uint64Lengths with an empty group puts its text length prefix at offset 14 and the text at offset 22.
 */
static const std::uint64_t TEXT_OFFSET = 22;

class CVarDataPutUint64LengthTest : public testing::Test
{
public:
    uint64_length_uint64Lengths *encode(std::vector<char> &buffer)
    {
        uint64_length_uint64Lengths_entries entries;

        EXPECT_NE(uint64_length_uint64Lengths_wrap_for_encode(&m_encoder, buffer.data(), 0, buffer.size()), nullptr);
        uint64_length_uint64Lengths_set_id(&m_encoder, 7);
        EXPECT_NE(uint64_length_uint64Lengths_entries_set_count(&m_encoder, &entries, 0), nullptr);
        errno = 0;
        return &m_encoder;
    }

private:
    uint64_length_uint64Lengths m_encoder = {};
};

TEST_F(CVarDataPutUint64LengthTest, shouldRejectLengthThatWrapsPosition)
{
    std::vector<char> buffer(TEXT_OFFSET + 8);
    const char src[4] = { 'a', 'b', 'c', 'd' };
    // not a constant, so the compiler does not see a huge memcpy at build time
    volatile std::uint64_t length = std::numeric_limits<std::uint64_t>::max() - 3;

    EXPECT_EQ(uint64_length_uint64Lengths_put_text(encode(buffer), src, length), nullptr);
    EXPECT_EQ(errno, E100);
}

TEST_F(CVarDataPutUint64LengthTest, shouldRejectLengthOneByteOverBufferEnd)
{
    std::vector<char> buffer(TEXT_OFFSET + 3);

    EXPECT_EQ(uint64_length_uint64Lengths_put_text(encode(buffer), "abcd", 4), nullptr);
    EXPECT_EQ(errno, E100);
}

TEST_F(CVarDataPutUint64LengthTest, shouldPutVarDataEndingAtBufferEnd)
{
    std::vector<char> buffer(TEXT_OFFSET + 3);

    uint64_length_uint64Lengths *encoder = encode(buffer);
    ASSERT_NE(uint64_length_uint64Lengths_put_text(encoder, "abc", 3), nullptr);

    EXPECT_EQ(uint64_length_uint64Lengths_encoded_length(encoder), buffer.size());
    EXPECT_EQ(std::string(buffer.data() + TEXT_OFFSET, 3), "abc");
}
