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
#include <cmath>
#include <iostream>

#include "gtest/gtest.h"
#include "optional_accessors_test/OptionalAccessorsMessage.h"

using namespace optional::accessors::test;

class OptionalAccessorsCodeGenTest : public testing::Test
{
public:
    char m_buffer[2048] = {};
    OptionalAccessorsMessage m_msg = {};
    OptionalAccessorsMessage m_msgDecoder = {};
};

TEST_F(OptionalAccessorsCodeGenTest, shouldReturnEmptyOptionalsForNullSentinels)
{
    m_msg.wrapForEncode(m_buffer, 0, sizeof(m_buffer))
        .rootPrimitive(OptionalAccessorsMessage::rootPrimitiveNullValue())
        .rootFloat(OptionalAccessorsMessage::rootFloatNullValue())
        .rootEnum(OptionalEnum::NULL_VALUE);

    OptionalComposite &composite = m_msg.rootComposite();
    composite
        .compositePrimitive(OptionalComposite::compositePrimitiveNullValue())
        .compositeFloat(OptionalComposite::compositeFloatNullValue())
        .compositeEnum(CompositeEnum::NULL_VALUE);

    OptionalAccessorsMessage::Entries &entries = m_msg.entriesCount(1);
    entries.next()
        .groupPrimitive(OptionalAccessorsMessage::Entries::groupPrimitiveNullValue())
        .groupEnum(OptionalEnum::NULL_VALUE);

    const std::uint64_t encodedLength = m_msg.encodedLength();

    m_msgDecoder.wrapForDecode(
        m_buffer,
        0,
        OptionalAccessorsMessage::sbeBlockLength(),
        OptionalAccessorsMessage::sbeSchemaVersion(),
        encodedLength);

    // Expect: raw accessors still return null values
    EXPECT_EQ(m_msgDecoder.rootPrimitive(), OptionalAccessorsMessage::rootPrimitiveNullValue());
    EXPECT_TRUE(std::isnan(m_msgDecoder.rootFloat()));
    EXPECT_EQ(m_msgDecoder.rootEnum(), OptionalEnum::NULL_VALUE);
    // Expect: optional accessors return empty optionals
    EXPECT_FALSE(m_msgDecoder.rootPrimitiveOpt().has_value());
    EXPECT_FALSE(m_msgDecoder.rootFloatOpt().has_value());
    EXPECT_FALSE(m_msgDecoder.rootEnumOpt().has_value());

    // Expect: optional accessors generated for composite types
    OptionalComposite &compositeDecoder = m_msgDecoder.rootComposite();
    EXPECT_EQ(compositeDecoder.compositePrimitive(), OptionalComposite::compositePrimitiveNullValue());
    EXPECT_TRUE(std::isnan(compositeDecoder.compositeFloat()));
    EXPECT_EQ(compositeDecoder.compositeEnum(), CompositeEnum::NULL_VALUE);
    EXPECT_FALSE(compositeDecoder.compositePrimitiveOpt().has_value());
    EXPECT_FALSE(compositeDecoder.compositeFloatOpt().has_value());

    // Expect: optional accessors generated for group fields
    OptionalAccessorsMessage::Entries &entriesDecoder = m_msgDecoder.entries();
    EXPECT_EQ(entriesDecoder.count(), 1u);
    ASSERT_TRUE(entriesDecoder.hasNext());
    entriesDecoder.next();
    EXPECT_EQ(entriesDecoder.groupPrimitive(), OptionalAccessorsMessage::Entries::groupPrimitiveNullValue());
    EXPECT_EQ(entriesDecoder.groupEnum(), OptionalEnum::NULL_VALUE);
    EXPECT_FALSE(entriesDecoder.groupPrimitiveOpt().has_value());
    EXPECT_FALSE(entriesDecoder.groupEnumOpt().has_value());
}

TEST_F(OptionalAccessorsCodeGenTest, shouldReturnValueOptionalsForNonNullFields)
{
    static const std::int32_t ROOT_PRIMITIVE = 42;
    static const float ROOT_FLOAT = 101.25f;
    static const std::int32_t COMPOSITE_PRIMITIVE = 7;
    static const float COMPOSITE_FLOAT = 3.5f;
    static const std::uint16_t GROUP_PRIMITIVE = 65000;

    m_msg.wrapForEncode(m_buffer, 0, sizeof(m_buffer))
        .rootPrimitive(ROOT_PRIMITIVE)
        .rootFloat(ROOT_FLOAT)
        .rootEnum(OptionalEnum::A);

    OptionalComposite &composite = m_msg.rootComposite();
    composite
        .compositePrimitive(COMPOSITE_PRIMITIVE)
        .compositeFloat(COMPOSITE_FLOAT)
        .compositeEnum(CompositeEnum::Y);

    OptionalAccessorsMessage::Entries &entries = m_msg.entriesCount(1);
    entries.next()
        .groupPrimitive(GROUP_PRIMITIVE)
        .groupEnum(OptionalEnum::A);

    const std::uint64_t encodedLength = m_msg.encodedLength();

    m_msgDecoder.wrapForDecode(
        m_buffer,
        0,
        OptionalAccessorsMessage::sbeBlockLength(),
        OptionalAccessorsMessage::sbeSchemaVersion(),
        encodedLength);

    const std::optional<std::int32_t> rootPrimitiveOpt = m_msgDecoder.rootPrimitiveOpt();
    ASSERT_TRUE(rootPrimitiveOpt.has_value());
    EXPECT_EQ(rootPrimitiveOpt.value(), ROOT_PRIMITIVE);

    const std::optional<float> rootFloatOpt = m_msgDecoder.rootFloatOpt();
    ASSERT_TRUE(rootFloatOpt.has_value());
    EXPECT_FLOAT_EQ(rootFloatOpt.value(), ROOT_FLOAT);

    const std::optional<OptionalEnum::Value> rootEnumOpt = m_msgDecoder.rootEnumOpt();
    ASSERT_TRUE(rootEnumOpt.has_value());
    EXPECT_EQ(rootEnumOpt.value(), OptionalEnum::A);

    OptionalComposite &compositeDecoder = m_msgDecoder.rootComposite();

    const std::optional<std::int32_t> compositePrimitiveOpt = compositeDecoder.compositePrimitiveOpt();
    ASSERT_TRUE(compositePrimitiveOpt.has_value());
    EXPECT_EQ(compositePrimitiveOpt.value(), COMPOSITE_PRIMITIVE);

    const std::optional<float> compositeFloatOpt = compositeDecoder.compositeFloatOpt();
    ASSERT_TRUE(compositeFloatOpt.has_value());
    EXPECT_FLOAT_EQ(compositeFloatOpt.value(), COMPOSITE_FLOAT);
    EXPECT_EQ(compositeDecoder.compositeEnum(), CompositeEnum::Y);

    OptionalAccessorsMessage::Entries &entriesDecoder = m_msgDecoder.entries();
    EXPECT_EQ(entriesDecoder.count(), 1u);
    ASSERT_TRUE(entriesDecoder.hasNext());
    entriesDecoder.next();

    const std::optional<std::uint16_t> groupPrimitiveOpt = entriesDecoder.groupPrimitiveOpt();
    ASSERT_TRUE(groupPrimitiveOpt.has_value());
    EXPECT_EQ(groupPrimitiveOpt.value(), GROUP_PRIMITIVE);

    const std::optional<OptionalEnum::Value> groupEnumOpt = entriesDecoder.groupEnumOpt();
    ASSERT_TRUE(groupEnumOpt.has_value());
    EXPECT_EQ(groupEnumOpt.value(), OptionalEnum::A);
}
