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
#include <memory>
#include <stdexcept>
#include <vector>

#include "gtest/gtest.h"
#include "otf/IrDecoder.h"
#include "otf/OtfMessageDecoder.h"
#include "WireBuffer.h"

using namespace sbe::otf;

static const char *IR_FILENAME = "uint64-var-data-length-schema.sbeir";
static const int WIDE_LENGTH_TEMPLATE_ID = 1;

class VarDataListener : public OtfMessageDecoder::BasicTokenListener
{
public:
    void onVarData(Token &, const char *, const std::uint64_t length, Token &) override
    {
        ++varDataCount;
        lastLength = length;
    }

    std::uint64_t varDataCount = 0;
    std::uint64_t lastLength = 0;
};

class OtfVarDataLengthTest : public testing::Test
{
protected:
    void SetUp() override
    {
        ASSERT_EQ(m_ir.decode(IR_FILENAME), 0);
        m_tokens = m_ir.message(WIDE_LENGTH_TEMPLATE_ID, 0);
        ASSERT_TRUE(m_tokens);
    }

    IrDecoder m_ir;
    std::shared_ptr<std::vector<Token>> m_tokens;
};

TEST_F(OtfVarDataLengthTest, shouldRejectUint64LengthThatWrapsBufferIndex)
{
    WireBuffer buffer;
    buffer.u32(1).u64(std::numeric_limits<std::uint64_t>::max()).bytes("abcd");
    VarDataListener listener;

    EXPECT_THROW(
        OtfMessageDecoder::decode(buffer.data(), buffer.size(), 0, 4, m_tokens, listener), std::runtime_error);
    EXPECT_EQ(listener.varDataCount, 0u);
}

TEST_F(OtfVarDataLengthTest, shouldDecodeVarDataEndingAtBufferEnd)
{
    WireBuffer buffer;
    buffer.u32(1).u64(4).bytes("abcd");
    VarDataListener listener;

    EXPECT_EQ(OtfMessageDecoder::decode(buffer.data(), buffer.size(), 0, 4, m_tokens, listener), buffer.size());
    EXPECT_EQ(listener.varDataCount, 1u);
    EXPECT_EQ(listener.lastLength, 4u);
}
