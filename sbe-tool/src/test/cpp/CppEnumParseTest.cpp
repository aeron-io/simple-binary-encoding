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

// Compile-and-run coverage for sbe.cpp.generate.enum.parse: the generated enum must expose
// fromString()/operator>> as the inverse of c_str()/operator<<. The codecs for this target are
// generated with the option enabled (see CMakeLists.txt).

#include <sstream>
#include <string>

#include "gtest/gtest.h"

#include "code_generation_test/Model.h"

using namespace code::generation::test;

TEST(CppEnumParseTest, FromStringInvertsCStr)
{
    EXPECT_EQ(Model::A, Model::fromString(Model::c_str(Model::A)));
    EXPECT_EQ(Model::B, Model::fromString(Model::c_str(Model::B)));
    EXPECT_EQ(Model::C, Model::fromString(Model::c_str(Model::C)));
    EXPECT_EQ(Model::NULL_VALUE, Model::fromString(Model::c_str(Model::NULL_VALUE)));
}

TEST(CppEnumParseTest, FromStringMatchesLiteralNames)
{
    EXPECT_EQ(Model::A, Model::fromString("A"));
    EXPECT_EQ(Model::NULL_VALUE, Model::fromString("NULL_VALUE"));
}

TEST(CppEnumParseTest, StreamOperatorsRoundTrip)
{
    std::ostringstream out;
    out << Model::B;
    EXPECT_EQ("B", out.str());

    std::istringstream in("C");
    Model::Value value = Model::NULL_VALUE;
    in >> value;
    EXPECT_EQ(Model::C, value);
}

// Model has no decode-unknown policy, so an unrecognised name must throw (mirrors c_str/get).
TEST(CppEnumParseTest, FromStringThrowsOnUnknown)
{
    EXPECT_THROW(Model::fromString("NOT_A_VALUE"), std::runtime_error);
}
