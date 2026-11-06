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

#include <sstream>
#include <string>
#include <utility>

#include <gtest/gtest.h>

#include "json_char/CharMessage.h"

using namespace json_char;

class JsonCharTest : public testing::TestWithParam<std::pair<char, std::string>>
{
};

TEST_P(JsonCharTest, shouldWriteSingleCharFieldAsValidJson)
{
    char buffer[64] = {};
    CharMessage encoder;
    encoder.wrapForEncode(buffer, 0, sizeof(buffer));
    encoder.code(GetParam().first);

    CharMessage decoder(
        buffer, encoder.encodedLength(), CharMessage::sbeBlockLength(), CharMessage::sbeSchemaVersion());
    std::ostringstream output;
    output << decoder;

    const std::string expected = std::string(R"("code": )") + GetParam().second;
    EXPECT_NE(output.str().find(expected), std::string::npos) << output.str();
}

INSTANTIATE_TEST_SUITE_P(
    JsonCharTest,
    JsonCharTest,
    testing::Values(
        std::make_pair('A', std::string(R"("A")")),
        std::make_pair('"', std::string(R"("\"")")),
        std::make_pair('\\', std::string(R"("\\")")),
        std::make_pair('\x01', std::string("1")),
        std::make_pair(static_cast<char>(0xe9), std::to_string(static_cast<int>(static_cast<char>(0xe9))))));
