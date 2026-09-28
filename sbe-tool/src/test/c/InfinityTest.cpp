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

#include <gtest/gtest.h>

#include "infinity_test/floatConstants.h"
#include "infinity_test/infinityValues.h"

#define IT(name) infinity_test_##name

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
    EXPECT_EQ(0x7f800000U, floatBits(IT(infinityValues_floatPositiveInfinity)()));
    EXPECT_EQ(0xff800000U, floatBits(IT(infinityValues_floatNegativeInfinity)()));
    EXPECT_EQ(0x7ff0000000000000ULL, doubleBits(IT(infinityValues_doublePositiveInfinity)()));
    EXPECT_EQ(0xfff0000000000000ULL, doubleBits(IT(infinityValues_doubleNegativeInfinity)()));

    EXPECT_EQ(0x7f800000U, floatBits(IT(floatConstants_positive)()));
    EXPECT_EQ(0xff800000U, floatBits(IT(floatConstants_negative)()));
}

TEST(InfinityTest, shouldGenerateWidthAndSignCorrectBounds)
{
    EXPECT_EQ(0xff800000U, floatBits(IT(infinityValues_floatPositiveNull_min_value)()));
    EXPECT_FLOAT_EQ(1.5f, IT(infinityValues_floatPositiveNull_max_value)());
    EXPECT_FLOAT_EQ(-1.5f, IT(infinityValues_floatNegativeNull_min_value)());
    EXPECT_EQ(0x7f800000U, floatBits(IT(infinityValues_floatNegativeNull_max_value)()));
    EXPECT_EQ(0xfff0000000000000ULL, doubleBits(IT(infinityValues_doublePositiveNull_min_value)()));
    EXPECT_DOUBLE_EQ(1.5, IT(infinityValues_doublePositiveNull_max_value)());
    EXPECT_DOUBLE_EQ(-1.5, IT(infinityValues_doubleNegativeNull_min_value)());
    EXPECT_EQ(0x7ff0000000000000ULL, doubleBits(IT(infinityValues_doubleNegativeNull_max_value)()));
}

TEST(InfinityTest, shouldEncodeWidthAndSignCorrectOptionalNullValues)
{
    char buffer[24] = {};
    IT(infinityValues) codec;
    IT(infinityValues_wrap_for_encode)(&codec, buffer, 0, sizeof(buffer));
    IT(infinityValues_set_floatPositiveNull)(&codec, IT(infinityValues_floatPositiveNull_null_value)());
    IT(infinityValues_set_floatNegativeNull)(&codec, IT(infinityValues_floatNegativeNull_null_value)());
    IT(infinityValues_set_doublePositiveNull)(&codec, IT(infinityValues_doublePositiveNull_null_value)());
    IT(infinityValues_set_doubleNegativeNull)(&codec, IT(infinityValues_doubleNegativeNull_null_value)());

    std::uint32_t floatValue;
    std::uint64_t doubleValue;
    std::memcpy(&floatValue, buffer, sizeof(floatValue));
    EXPECT_EQ(0x7f800000U, floatValue);
    std::memcpy(&floatValue, buffer + sizeof(floatValue), sizeof(floatValue));
    EXPECT_EQ(0xff800000U, floatValue);
    std::memcpy(&doubleValue, buffer + 8, sizeof(doubleValue));
    EXPECT_EQ(0x7ff0000000000000ULL, doubleValue);
    std::memcpy(&doubleValue, buffer + 16, sizeof(doubleValue));
    EXPECT_EQ(0xfff0000000000000ULL, doubleValue);
}
