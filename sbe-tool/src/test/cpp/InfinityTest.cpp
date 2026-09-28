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
#include <cstring>
#include <vector>

#include <gtest/gtest.h>

#include "infinity_test/FloatConstants.h"
#include "infinity_test/InfinityValues.h"

using namespace infinity_test;

namespace
{
std::uint32_t floatBits(const float value)
{
    std::uint32_t bits;
    std::memcpy(&bits, &value, sizeof(bits));
    return bits;
}

std::uint64_t doubleBits(const double value)
{
    std::uint64_t bits;
    std::memcpy(&bits, &value, sizeof(bits));
    return bits;
}
}

TEST(InfinityTest, shouldGenerateWidthAndSignCorrectConstants)
{
    EXPECT_EQ(0x7f800000U, floatBits(InfinityValues::floatPositiveInfinity()));
    EXPECT_EQ(0xff800000U, floatBits(InfinityValues::floatNegativeInfinity()));
    EXPECT_EQ(0x7ff0000000000000ULL, doubleBits(InfinityValues::doublePositiveInfinity()));
    EXPECT_EQ(0xfff0000000000000ULL, doubleBits(InfinityValues::doubleNegativeInfinity()));

    EXPECT_EQ(0x7f800000U, floatBits(FloatConstants::positive()));
    EXPECT_EQ(0xff800000U, floatBits(FloatConstants::negative()));
}

TEST(InfinityTest, shouldGenerateWidthAndSignCorrectBounds)
{
    EXPECT_EQ(0xff800000U, floatBits(InfinityValues::floatPositiveNullMinValue()));
    EXPECT_FLOAT_EQ(1.5f, InfinityValues::floatPositiveNullMaxValue());
    EXPECT_FLOAT_EQ(-1.5f, InfinityValues::floatNegativeNullMinValue());
    EXPECT_EQ(0x7f800000U, floatBits(InfinityValues::floatNegativeNullMaxValue()));
    EXPECT_EQ(0xfff0000000000000ULL, doubleBits(InfinityValues::doublePositiveNullMinValue()));
    EXPECT_DOUBLE_EQ(1.5, InfinityValues::doublePositiveNullMaxValue());
    EXPECT_DOUBLE_EQ(-1.5, InfinityValues::doubleNegativeNullMinValue());
    EXPECT_EQ(0x7ff0000000000000ULL, doubleBits(InfinityValues::doubleNegativeNullMaxValue()));
}

TEST(InfinityTest, shouldEncodeWidthAndSignCorrectOptionalNullValues)
{
    std::vector<char> buffer(InfinityValues::sbeBlockLength());
    InfinityValues codec;
    codec.wrapForEncode(buffer.data(), 0, buffer.size())
        .floatPositiveNull(InfinityValues::floatPositiveNullNullValue())
        .floatNegativeNull(InfinityValues::floatNegativeNullNullValue())
        .doublePositiveNull(InfinityValues::doublePositiveNullNullValue())
        .doubleNegativeNull(InfinityValues::doubleNegativeNullNullValue());

    std::uint32_t floatValue;
    std::uint64_t doubleValue;
    std::memcpy(&floatValue, buffer.data(), sizeof(floatValue));
    EXPECT_EQ(0x7f800000U, floatValue);
    std::memcpy(&floatValue, buffer.data() + sizeof(floatValue), sizeof(floatValue));
    EXPECT_EQ(0xff800000U, floatValue);
    std::memcpy(&doubleValue, buffer.data() + 8, sizeof(doubleValue));
    EXPECT_EQ(0x7ff0000000000000ULL, doubleValue);
    std::memcpy(&doubleValue, buffer.data() + 16, sizeof(doubleValue));
    EXPECT_EQ(0xfff0000000000000ULL, doubleValue);
}
