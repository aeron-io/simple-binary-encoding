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

#include "gtest/gtest.h"
#include "uint64_var_data_length/WideLength.h"
#include "WireBuffer.h"

using namespace uint64::var::data::length;

class VarDataUint64LengthTest : public testing::Test
{
public:
    WideLength &decode(WireBuffer &buffer)
    {
        return m_decoder.wrapForDecode(buffer.data(), 0, WideLength::sbeBlockLength(), 0, buffer.size());
    }

private:
    WideLength m_decoder;
};

TEST_F(VarDataUint64LengthTest, shouldRejectLengthThatWrapsPosition)
{
    WireBuffer buffer;
    buffer.u32(1).u64(std::numeric_limits<std::uint64_t>::max() - 3).bytes("abcd");
    char dst[16];

    EXPECT_THROW((void)decode(buffer).getPayload(dst, sizeof(dst)), std::runtime_error);
    EXPECT_THROW((void)decode(buffer).payload(), std::runtime_error);
    EXPECT_THROW((void)decode(buffer).skipPayload(), std::runtime_error);
}

TEST_F(VarDataUint64LengthTest, shouldDecodeVarDataEndingAtBufferEnd)
{
    WireBuffer buffer;
    buffer.u32(1).u64(4).bytes("abcd");
    char dst[16];

    EXPECT_EQ(decode(buffer).getPayload(dst, sizeof(dst)), 4u);
    EXPECT_EQ(std::string(dst, 4), "abcd");
    EXPECT_EQ(std::string(decode(buffer).payload(), 4), "abcd");

    WideLength &decoder = decode(buffer);
    EXPECT_EQ(decoder.skipPayload(), 4u);
    EXPECT_EQ(decoder.sbePosition(), buffer.size());
}
