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
#include <limits>
#include <stdexcept>
#include <string>
#include <vector>

#include <gtest/gtest.h>

#include "uint64_length/Uint64Lengths.h"

using namespace uint64_length;

/*
 * Uint64Lengths with an empty group puts its text length prefix at offset 14 and the text at offset 22.
 */
static const std::uint64_t TEXT_OFFSET = 22;

class VarDataPutUint64LengthTest : public testing::Test
{
public:
    Uint64Lengths &encode(std::vector<char> &buffer)
    {
        m_encoder.wrapForEncode(buffer.data(), 0, buffer.size());
        m_encoder.id(7);
        m_encoder.entriesCount(0);
        return m_encoder;
    }

private:
    Uint64Lengths m_encoder;
};

TEST_F(VarDataPutUint64LengthTest, shouldRejectLengthThatWrapsPosition)
{
    std::vector<char> buffer(TEXT_OFFSET + 8);
    const char src[4] = { 'a', 'b', 'c', 'd' };
    // not a constant, so the compiler does not see a huge memcpy at build time
    volatile std::uint64_t length = std::numeric_limits<std::uint64_t>::max() - 3;

    EXPECT_THROW(encode(buffer).putText(src, length), std::runtime_error);
}

TEST_F(VarDataPutUint64LengthTest, shouldRejectLengthOneByteOverBufferEnd)
{
    std::vector<char> buffer(TEXT_OFFSET + 3);

    EXPECT_THROW(encode(buffer).putText("abcd", 4), std::runtime_error);
}

TEST_F(VarDataPutUint64LengthTest, shouldPutVarDataEndingAtBufferEnd)
{
    std::vector<char> buffer(TEXT_OFFSET + 3);

    Uint64Lengths &encoder = encode(buffer);
    encoder.putText(std::string("abc"));

    EXPECT_EQ(encoder.encodedLength(), buffer.size());
    EXPECT_EQ(std::string(buffer.data() + TEXT_OFFSET, 3), "abc");
}
