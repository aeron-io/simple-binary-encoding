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
#include <stdexcept>
#include <string>

#include "gtest/gtest.h"
#include "wire_bounds_test/Versioned.h"
#include "WireBuffer.h"

using namespace wire::bounds::test;

class VarDataLengthPrefixBoundsTest : public testing::Test
{
public:
    // Fixed block and an empty group, followed by the var data length prefix.
    static WireBuffer prefix()
    {
        WireBuffer buffer;
        buffer.u64(1).u64(2).u64(3).u16(16).u16(0);
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

TEST_F(VarDataLengthPrefixBoundsTest, shouldRejectBufferTruncatedInLengthPrefix)
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

TEST_F(VarDataLengthPrefixBoundsTest, shouldDecodeVarDataEndingAtBufferEnd)
{
    WireBuffer buffer = prefix();
    buffer.u32(3).bytes("abc");
    char dst[16];

    EXPECT_EQ(decode(buffer).payloadLength(), 3u);
    EXPECT_EQ(decode(buffer).skipPayload(), 3u);
    EXPECT_EQ(std::string(decode(buffer).payload(), 3), "abc");
    EXPECT_EQ(decode(buffer).getPayload(dst, sizeof(dst)), 3u);

    Versioned skipper(buffer.data(), 0, buffer.size(), Versioned::sbeBlockLength(), 1);
    EXPECT_EQ(skipper.decodeLength(), buffer.size());
}
