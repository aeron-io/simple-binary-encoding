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
#include <memory>
#include <stdexcept>
#include <vector>

#include "gtest/gtest.h"
#include "otf/IrDecoder.h"
#include "otf/OtfMessageDecoder.h"

using namespace sbe::otf;

/*
 * AddVarDataV1 in field-order-check-schema.xml is an int32 field followed by var data added in version 1, so a
 * version 0 message is the 4 byte block alone.
 */
static const char *SCHEMA_FILENAME = "field-order-check-schema.sbeir";
static const int ADD_VAR_DATA_V1_TEMPLATE_ID = 1019;
static const std::uint64_t BLOCK_LENGTH = 4;

class OtfAbsentVarDataTest : public testing::Test, public OtfMessageDecoder::BasicTokenListener
{
public:
    void SetUp() override
    {
        ASSERT_GE(m_irDecoder.decode(SCHEMA_FILENAME), 0);
        m_messageTokens = m_irDecoder.message(ADD_VAR_DATA_V1_TEMPLATE_ID);
        ASSERT_TRUE(m_messageTokens != nullptr);
    }

    void onVarData(Token &, const char *, std::uint64_t length, Token &) override
    {
        m_varDataCount++;
        m_varDataLength = length;
    }

    std::size_t decode(const std::vector<char> &buffer, const std::uint64_t actingVersion)
    {
        return OtfMessageDecoder::decode(
            buffer.data(), buffer.size(), actingVersion, BLOCK_LENGTH, m_messageTokens, *this);
    }

protected:
    IrDecoder m_irDecoder = {};
    std::shared_ptr<std::vector<Token>> m_messageTokens;
    int m_varDataCount = 0;
    std::uint64_t m_varDataLength = 1;
};

TEST_F(OtfAbsentVarDataTest, shouldDecodeOlderVersionMessageEndingBeforeAbsentVarData)
{
    const std::vector<char> buffer = { 42, 0, 0, 0 };

    EXPECT_EQ(decode(buffer, 0), buffer.size());
    EXPECT_EQ(m_varDataCount, 1);
    EXPECT_EQ(m_varDataLength, 0u);
}

TEST_F(OtfAbsentVarDataTest, shouldRejectMissingLengthOfPresentVarData)
{
    const std::vector<char> buffer = { 42, 0, 0, 0 };

    EXPECT_THROW(decode(buffer, 1), std::runtime_error);
}
