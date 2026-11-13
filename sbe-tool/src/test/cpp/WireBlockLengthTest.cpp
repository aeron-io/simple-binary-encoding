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

#include "gtest/gtest.h"
#include "gmock/gmock.h"
#include "wire_bounds_test/Versioned.h"
#include "WireBuffer.h"

using namespace wire::bounds::test;
using ::testing::HasSubstr;

static WireBuffer versionedBlockV1()
{
    WireBuffer buffer;
    buffer.u64(1).u64(2).u64(3);
    return buffer;
}

TEST(WireBlockLengthTest, shouldRejectMessageBlockLengthZeroWithTruncatedBuffer)
{
    WireBuffer buffer;
    buffer.u16(0).u16(Versioned::sbeTemplateId()).u16(Versioned::sbeSchemaId()).u16(1);

    Versioned decoder;
    EXPECT_THROW(
        decoder.wrapForDecode(buffer.data(), MessageHeader::encodedLength(), 0, 1, buffer.size()),
        std::runtime_error);
    EXPECT_THROW(Versioned(buffer.data(), MessageHeader::encodedLength(), buffer.size(), 0, 1), std::runtime_error);
}

TEST(WireBlockLengthTest, shouldRejectMessageBlockLengthShorterThanActingVersionFields)
{
    WireBuffer buffer = versionedBlockV1();
    buffer.u16(16).u16(0).u32(0);

    Versioned decoder;
    EXPECT_THROW(decoder.wrapForDecode(buffer.data(), 0, 8, 0, buffer.size()), std::runtime_error);
    EXPECT_THROW(decoder.wrapForDecode(buffer.data(), 0, 16, 1, buffer.size()), std::runtime_error);
    EXPECT_NO_THROW(decoder.wrapForDecode(buffer.data(), 0, 24, 1, buffer.size()));
}

TEST(WireBlockLengthTest, shouldDecodeOlderVersionWithShorterBlocks)
{
    WireBuffer buffer;
    buffer.u64(1).u64(2);
    buffer.u16(8).u16(1).u64(3);
    buffer.u32(2).bytes("hi");

    Versioned decoder;
    decoder.wrapForDecode(buffer.data(), 0, 16, 0, buffer.size());
    EXPECT_EQ(decoder.a(), 1u);
    EXPECT_EQ(decoder.b(), 2u);
    EXPECT_EQ(decoder.c(), Versioned::cNullValue());

    Versioned::Entries &entries = decoder.entries();
    ASSERT_EQ(entries.count(), 1u);
    entries.next();
    EXPECT_EQ(entries.x(), 3u);
    EXPECT_EQ(entries.y(), Versioned::Entries::yNullValue());

    EXPECT_EQ(decoder.getPayloadAsString(), "hi");
    EXPECT_EQ(decoder.sbePosition(), buffer.size());
}

TEST(WireBlockLengthTest, shouldRejectGroupBlockLengthZero)
{
    WireBuffer buffer = versionedBlockV1();
    buffer.u16(0).u16(2);

    Versioned decoder;
    decoder.wrapForDecode(buffer.data(), 0, Versioned::sbeBlockLength(), 1, buffer.size());
    EXPECT_THROW(
        {
            try
            {
                (void)decoder.entries();
            }
            catch (const std::runtime_error &e)
            {
                EXPECT_THAT(e.what(), HasSubstr("E108"));
                throw;
            }
        },
        std::runtime_error);
}

TEST(WireBlockLengthTest, shouldAcceptEmptyGroupWithBlockLengthZero)
{
    WireBuffer buffer = versionedBlockV1();
    buffer.u16(0).u16(0).u32(0);

    Versioned decoder;
    decoder.wrapForDecode(buffer.data(), 0, Versioned::sbeBlockLength(), 1, buffer.size());
    EXPECT_EQ(decoder.entries().count(), 0u);
    EXPECT_EQ(decoder.getPayloadAsString(), "");
}
