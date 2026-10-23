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

#include <gtest/gtest.h>

#include "uint64_length/uint64Lengths.h"

class Uint64LengthTest : public testing::Test
{
};

TEST_F(Uint64LengthTest, shouldEncodeAndDecodeUint64GroupCountAndVarDataLength)
{
    char buffer[256] = {};
    uint64_length_uint64Lengths encoder;
    uint64_length_uint64Lengths_entries entries;

    ASSERT_NE(uint64_length_uint64Lengths_wrap_for_encode(&encoder, buffer, 0, sizeof(buffer)), nullptr);
    uint64_length_uint64Lengths_set_id(&encoder, 7);
    ASSERT_NE(uint64_length_uint64Lengths_entries_set_count(&encoder, &entries, 2), nullptr);
    uint64_length_uint64Lengths_entries_set_value(uint64_length_uint64Lengths_entries_next(&entries), 1);
    uint64_length_uint64Lengths_entries_set_value(uint64_length_uint64Lengths_entries_next(&entries), 2);
    ASSERT_NE(uint64_length_uint64Lengths_put_text(&encoder, "abc", 3), nullptr);

    const uint64_t encodedLength = uint64_length_uint64Lengths_encoded_length(&encoder);

    uint64_length_uint64Lengths decoder;
    ASSERT_NE(uint64_length_uint64Lengths_wrap_for_decode(
        &decoder,
        buffer,
        0,
        uint64_length_uint64Lengths_sbe_block_length(),
        uint64_length_uint64Lengths_sbe_schema_version(),
        encodedLength), nullptr);
    EXPECT_EQ(uint64_length_uint64Lengths_id(&decoder), 7u);

    ASSERT_NE(uint64_length_uint64Lengths_get_entries(&decoder, &entries), nullptr);
    EXPECT_EQ(uint64_length_uint64Lengths_entries_count(&entries), 2u);
    EXPECT_EQ(uint64_length_uint64Lengths_entries_value(uint64_length_uint64Lengths_entries_next(&entries)), 1u);
    EXPECT_EQ(uint64_length_uint64Lengths_entries_value(uint64_length_uint64Lengths_entries_next(&entries)), 2u);

    char text[3] = {};
    EXPECT_EQ(uint64_length_uint64Lengths_get_text(&decoder, text, sizeof(text)), 3u);
    EXPECT_EQ(std::string(text, sizeof(text)), "abc");
}
