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

#include "uint64_length/Uint64Lengths.h"

using namespace uint64_length;

class Uint64LengthTest : public testing::Test
{
};

TEST_F(Uint64LengthTest, shouldEncodeAndDecodeUint64GroupCountAndVarDataLength)
{
    char buffer[256] = {};
    Uint64Lengths encoder;
    encoder.wrapForEncode(buffer, 0, sizeof(buffer));
    encoder.id(7);
    encoder.entriesCount(2).next().value(1).next().value(2);
    encoder.putText(std::string("abc"));

    EXPECT_EQ(encoder.encodedLength(), Uint64Lengths::computeLength(2, 3));

    Uint64Lengths decoder(
        buffer, encoder.encodedLength(), Uint64Lengths::sbeBlockLength(), Uint64Lengths::sbeSchemaVersion());
    EXPECT_EQ(decoder.id(), 7u);

    Uint64Lengths::Entries &entries = decoder.entries();
    EXPECT_EQ(entries.count(), 2u);
    EXPECT_EQ(entries.next().value(), 1u);
    EXPECT_EQ(entries.next().value(), 2u);
    EXPECT_EQ(decoder.getTextAsString(), "abc");
}
