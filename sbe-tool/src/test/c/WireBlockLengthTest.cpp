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
#include <string>

#include <gtest/gtest.h>

#include "wire_bounds_test/versioned.h"
#include "WireBuffer.h"

static WireBuffer versionedBlockV1()
{
    WireBuffer buffer;
    buffer.u64(1).u64(2).u64(3);
    return buffer;
}

TEST(CWireBlockLengthTest, shouldRejectMessageBlockLengthZeroWithTruncatedBuffer)
{
    WireBuffer buffer;
    buffer.u16(0)
        .u16(wire_bounds_test_versioned_sbe_template_id())
        .u16(wire_bounds_test_versioned_sbe_schema_id())
        .u16(1);
    wire_bounds_test_versioned decoder;

    errno = 0;
    EXPECT_EQ(wire_bounds_test_versioned_wrap_for_decode(&decoder, buffer.data(), 8, 0, 1, buffer.size()), nullptr);
    EXPECT_EQ(errno, E100);
}

TEST(CWireBlockLengthTest, shouldRejectMessageBlockLengthShorterThanActingVersionFields)
{
    WireBuffer buffer = versionedBlockV1();
    buffer.u16(16).u16(0).u32(0);
    wire_bounds_test_versioned decoder;

    EXPECT_EQ(wire_bounds_test_versioned_wrap_for_decode(&decoder, buffer.data(), 0, 8, 0, buffer.size()), nullptr);
    EXPECT_EQ(wire_bounds_test_versioned_wrap_for_decode(&decoder, buffer.data(), 0, 16, 1, buffer.size()), nullptr);
    EXPECT_NE(wire_bounds_test_versioned_wrap_for_decode(&decoder, buffer.data(), 0, 24, 1, buffer.size()), nullptr);
}

TEST(CWireBlockLengthTest, shouldDecodeOlderVersionWithShorterBlocks)
{
    WireBuffer buffer;
    buffer.u64(1).u64(2);
    buffer.u16(8).u16(1).u64(3);
    buffer.u32(2).bytes("hi");
    wire_bounds_test_versioned decoder;
    wire_bounds_test_versioned_entries entries;

    ASSERT_NE(wire_bounds_test_versioned_wrap_for_decode(&decoder, buffer.data(), 0, 16, 0, buffer.size()), nullptr);
    EXPECT_EQ(wire_bounds_test_versioned_a(&decoder), 1u);
    EXPECT_EQ(wire_bounds_test_versioned_b(&decoder), 2u);
    EXPECT_EQ(wire_bounds_test_versioned_c(&decoder), wire_bounds_test_versioned_c_null_value());

    ASSERT_NE(wire_bounds_test_versioned_get_entries(&decoder, &entries), nullptr);
    ASSERT_EQ(wire_bounds_test_versioned_entries_count(&entries), 1u);
    ASSERT_NE(wire_bounds_test_versioned_entries_next(&entries), nullptr);
    EXPECT_EQ(wire_bounds_test_versioned_entries_x(&entries), 3u);
    EXPECT_EQ(wire_bounds_test_versioned_entries_y(&entries), wire_bounds_test_versioned_entries_y_null_value());

    const wire_bounds_test_versioned_string_view view = wire_bounds_test_versioned_get_payload_as_string_view(&decoder);
    ASSERT_NE(view.data, nullptr);
    EXPECT_EQ(std::string(view.data, view.length), "hi");
    EXPECT_EQ(wire_bounds_test_versioned_sbe_position(&decoder), buffer.size());
}

TEST(CWireBlockLengthTest, shouldRejectGroupBlockLengthZero)
{
    WireBuffer buffer = versionedBlockV1();
    buffer.u16(0).u16(2);
    wire_bounds_test_versioned decoder;
    wire_bounds_test_versioned_entries entries;

    ASSERT_NE(wire_bounds_test_versioned_wrap_for_decode(&decoder, buffer.data(), 0, 24, 1, buffer.size()), nullptr);
    errno = 0;
    EXPECT_EQ(wire_bounds_test_versioned_get_entries(&decoder, &entries), nullptr);
    EXPECT_EQ(errno, E108);
}

TEST(CWireBlockLengthTest, shouldAcceptEmptyGroupWithBlockLengthZero)
{
    WireBuffer buffer = versionedBlockV1();
    buffer.u16(0).u16(0).u32(0);
    wire_bounds_test_versioned decoder;
    wire_bounds_test_versioned_entries entries;

    ASSERT_NE(wire_bounds_test_versioned_wrap_for_decode(&decoder, buffer.data(), 0, 24, 1, buffer.size()), nullptr);
    ASSERT_NE(wire_bounds_test_versioned_get_entries(&decoder, &entries), nullptr);
    EXPECT_EQ(wire_bounds_test_versioned_entries_count(&entries), 0u);
    EXPECT_EQ(wire_bounds_test_versioned_payload_length(&decoder), 0u);
}
