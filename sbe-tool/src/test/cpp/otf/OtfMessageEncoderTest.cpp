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
#include <map>
#include <memory>
#include <string>
#include <vector>

#include "gtest/gtest.h"

#include "otf/Token.h"
#include "otf/Encoding.h"
#include "otf/IrDecoder.h"
#include "otf/OtfHeaderDecoder.h"
#include "otf/OtfHeaderEncoder.h"
#include "otf/OtfMessageDecoder.h"
#include "otf/OtfMessageEncoder.h"

#include "code_generation_test/Car.h"

using namespace sbe::otf;
using namespace code::generation::test;

// IR (.sbeir) for the example schema, produced by SbeTool; supplied on argv or $SBE_EXAMPLE_IR.
static std::string g_irFile;

// =================================================================================================
// Encoding::put* / putAs*  <->  get* / getAs*
// =================================================================================================
class EncodingPutTest : public ::testing::TestWithParam<ByteOrder> {};

TEST_P(EncodingPutTest, IntRoundTrip)
{
    const ByteOrder bo = GetParam();
    char b[8] = {};

    for (const std::int64_t v : { std::int64_t(0), std::int64_t(-1), std::int64_t(1),
        std::int64_t(-128), std::int64_t(127) })
    {
        Encoding::putInt(PrimitiveType::INT8, bo, b, v);
        EXPECT_EQ(Encoding::getInt(PrimitiveType::INT8, bo, b), v);
    }
    Encoding::putInt(PrimitiveType::INT16, bo, b, -12345);
    EXPECT_EQ(Encoding::getInt(PrimitiveType::INT16, bo, b), -12345);
    Encoding::putInt(PrimitiveType::INT32, bo, b, -1234567);
    EXPECT_EQ(Encoding::getInt(PrimitiveType::INT32, bo, b), -1234567);
    Encoding::putInt(PrimitiveType::INT64, bo, b, -1234567890123LL);
    EXPECT_EQ(Encoding::getInt(PrimitiveType::INT64, bo, b), -1234567890123LL);
}

TEST_P(EncodingPutTest, UIntRoundTrip)
{
    const ByteOrder bo = GetParam();
    char b[8] = {};

    Encoding::putUInt(PrimitiveType::UINT8, bo, b, 200u);
    EXPECT_EQ(Encoding::getUInt(PrimitiveType::UINT8, bo, b), 200u);
    Encoding::putUInt(PrimitiveType::UINT16, bo, b, 54321u);
    EXPECT_EQ(Encoding::getUInt(PrimitiveType::UINT16, bo, b), 54321u);
    Encoding::putUInt(PrimitiveType::UINT32, bo, b, 4000000000u);
    EXPECT_EQ(Encoding::getUInt(PrimitiveType::UINT32, bo, b), 4000000000u);
    Encoding::putUInt(PrimitiveType::UINT64, bo, b, 0xdeadbeefcafef00dULL);
    EXPECT_EQ(Encoding::getUInt(PrimitiveType::UINT64, bo, b), 0xdeadbeefcafef00dULL);
}

TEST_P(EncodingPutTest, FloatingPointRoundTrip)
{
    const ByteOrder bo = GetParam();
    char b[8] = {};

    Encoding::putDouble(PrimitiveType::FLOAT, bo, b, 3.5f);
    EXPECT_EQ(Encoding::getDouble(PrimitiveType::FLOAT, bo, b), 3.5);
    Encoding::putDouble(PrimitiveType::DOUBLE, bo, b, 2.718281828459045);
    EXPECT_EQ(Encoding::getDouble(PrimitiveType::DOUBLE, bo, b), 2.718281828459045);
}

INSTANTIATE_TEST_SUITE_P(
    BothByteOrders, EncodingPutTest,
    ::testing::Values(ByteOrder::SBE_LITTLE_ENDIAN, ByteOrder::SBE_BIG_ENDIAN));

TEST(EncodingPutTest, PutThrowsOnWrongType)
{
    char b[8] = {};
    EXPECT_THROW(Encoding::putInt(PrimitiveType::UINT8, ByteOrder::SBE_LITTLE_ENDIAN, b, 1), std::runtime_error);
    EXPECT_THROW(Encoding::putUInt(PrimitiveType::INT8, ByteOrder::SBE_LITTLE_ENDIAN, b, 1), std::runtime_error);
    EXPECT_THROW(Encoding::putDouble(PrimitiveType::INT8, ByteOrder::SBE_LITTLE_ENDIAN, b, 1.0), std::runtime_error);
}

// =================================================================================================
// A raw-byte DOM: a decode records each leaf field's exact wire bytes, its groups and composites;
// an encode replays them. If the encoder walks the token stream and frames the message correctly,
// the re-encoded bytes are identical to the original. This exercises the full engine
// (fields / composites / groups / nested groups / var-data), OtfHeaderEncoder, and the
// dimension/length writes, and is a strict regression guard: a field recorded under one path but
// looked up under another (as an incorrect composite/enum callback would cause) aborts the replay.
// =================================================================================================
namespace {

struct Node
{
    std::map<std::string, std::string>       leaves;
    std::map<std::string, Node>              composites;
    std::map<std::string, std::vector<Node>> groups;
};

std::size_t enumOrSetLength(std::vector<Token> &tokens, std::size_t fromIndex)
{
    return lengthOfType(tokens.at(fromIndex + 1).encoding().primitiveType());
}

struct RecordingListener : public OtfMessageDecoder::BasicTokenListener
{
    Node                root;
    std::vector<Node *> stack{ &root };
    Node &cur() { return *stack.back(); }

    void onEncoding(Token &f, const char *buffer, Token &type, std::uint64_t) override
    {
        cur().leaves[f.name()] = std::string(buffer, type.encodedLength());
    }
    void onEnum(Token &f, const char *buffer, std::vector<Token> &t, std::size_t from, std::size_t, std::uint64_t) override
    {
        cur().leaves[f.name()] = std::string(buffer, enumOrSetLength(t, from));
    }
    void onBitSet(Token &f, const char *buffer, std::vector<Token> &t, std::size_t from, std::size_t, std::uint64_t) override
    {
        cur().leaves[f.name()] = std::string(buffer, enumOrSetLength(t, from));
    }
    void onBeginComposite(Token &f, std::vector<Token> &, std::size_t, std::size_t) override
    {
        stack.push_back(&cur().composites[f.name()]);
    }
    void onEndComposite(Token &, std::vector<Token> &, std::size_t, std::size_t) override { stack.pop_back(); }
    void onBeginGroup(Token &token, std::uint64_t, std::uint64_t) override
    {
        auto &entries = cur().groups[token.name()];
        entries.emplace_back();
        stack.push_back(&entries.back());
    }
    void onEndGroup(Token &, std::uint64_t, std::uint64_t) override { stack.pop_back(); }
    void onVarData(Token &f, const char *buffer, std::uint64_t length, Token &) override
    {
        cur().leaves[f.name()] = std::string(buffer, length);
    }
};

template<class M>
const typename M::mapped_type &require(const M &m, const std::string &k)
{
    auto it = m.find(k);
    EXPECT_NE(it, m.end()) << "replay could not find '" << k << "' recorded on decode";
    return it->second;   // throws std::out_of_range if truly absent, failing the test cleanly
}

struct ReplayEncoder : public OtfMessageEncoder::BasicTokenEncoder
{
    const Node               &root;
    std::vector<const Node *> stack;
    explicit ReplayEncoder(const Node &r) : root(r) { stack.push_back(&root); }
    const Node &cur() const { return *stack.back(); }

    void onEncoding(Token &f, char *buffer, Token &, std::uint64_t) override
    {
        const std::string &raw = require(cur().leaves, f.name());
        std::memcpy(buffer, raw.data(), raw.size());
    }
    void onEnum(Token &f, char *buffer, std::vector<Token> &, std::size_t, std::size_t, std::uint64_t) override
    {
        const std::string &raw = require(cur().leaves, f.name());
        std::memcpy(buffer, raw.data(), raw.size());
    }
    void onBitSet(Token &f, char *buffer, std::vector<Token> &, std::size_t, std::size_t, std::uint64_t) override
    {
        const std::string &raw = require(cur().leaves, f.name());
        std::memcpy(buffer, raw.data(), raw.size());
    }
    void onBeginComposite(Token &f, std::vector<Token> &, std::size_t, std::size_t) override
    {
        stack.push_back(&require(cur().composites, f.name()));
    }
    void onEndComposite(Token &, std::vector<Token> &, std::size_t, std::size_t) override { stack.pop_back(); }
    std::uint64_t numInGroup(Token &token) override
    {
        auto it = cur().groups.find(token.name());
        return it == cur().groups.end() ? 0 : it->second.size();
    }
    void onBeginGroup(Token &token, std::uint64_t index, std::uint64_t) override
    {
        stack.push_back(&require(cur().groups, token.name())[index]);
    }
    void onEndGroup(Token &, std::uint64_t, std::uint64_t) override { stack.pop_back(); }
    std::uint64_t varDataLength(Token &f) override
    {
        auto it = cur().leaves.find(f.name());
        return it == cur().leaves.end() ? 0 : it->second.size();
    }
    void onVarData(Token &f, char *buffer, std::uint64_t length, Token &) override
    {
        const std::string &raw = require(cur().leaves, f.name());
        std::memcpy(buffer, raw.data(), length);
    }
};

// decode `reference` (a message written into a `capacity`-byte buffer) then re-encode it; assert
// the result is byte-identical. The true framed length comes from the decode, not the caller.
void assertOtfRoundTripIsIdentical(const char *reference, std::size_t capacity)
{
    IrDecoder ir;
    ASSERT_EQ(ir.decode(g_irFile.c_str()), 0) << "failed to load IR: " << g_irFile;
    auto headerTokens = ir.header();

    OtfHeaderDecoder headerDecoder(headerTokens);
    const std::size_t headerLength = headerDecoder.encodedLength();
    const std::uint64_t blockLength = headerDecoder.getBlockLength(reference);
    const std::uint64_t templateId  = headerDecoder.getTemplateId(reference);
    const std::uint64_t schemaId    = headerDecoder.getSchemaId(reference);
    const std::uint64_t version     = headerDecoder.getSchemaVersion(reference);

    auto msgTokens = ir.message(static_cast<int>(templateId), static_cast<int>(version));
    ASSERT_TRUE(msgTokens);

    RecordingListener rec;
    const std::size_t decodedBody = OtfMessageDecoder::decode(
        reference + headerLength, capacity - headerLength, version, blockLength, msgTokens, rec);
    const std::size_t refLen = headerLength + decodedBody;

    std::vector<char> out(capacity + 64, '\0');

    OtfHeaderEncoder headerEncoder(headerTokens);
    EXPECT_EQ(headerEncoder.encodedLength(), headerLength);
    headerEncoder.encode(out.data(), blockLength, templateId, schemaId, version);

    ReplayEncoder rep(rec.root);
    const std::size_t encodedBody = OtfMessageEncoder::encode(
        out.data() + headerLength, out.size() - headerLength, version, blockLength, msgTokens, rep);

    ASSERT_EQ(headerLength + encodedBody, refLen);
    EXPECT_EQ(0, std::memcmp(out.data(), reference, refLen));
}

// Build a reference Car via the generated (jar-produced) flyweight — an independent encoder.
std::size_t encodeCar(char *buffer, std::size_t capacity,
    std::uint16_t fuelFigures, std::uint16_t performanceFigures, std::uint16_t accelerationPer,
    const std::string &manufacturer, const std::string &model, const std::string &activationCode)
{
    std::memset(buffer, 0, capacity);

    Car car;
    car.wrapAndApplyHeader(buffer, 0, static_cast<std::uint64_t>(capacity));
    car.serialNumber(1234567ULL);
    car.modelYear(2024);
    car.available(BooleanType::T);
    car.code(Model::C);
    for (std::uint64_t i = 0; i < Car::someNumbersLength(); i++)
    {
        car.someNumbers(i, static_cast<std::uint32_t>(10 * (i + 1)));
    }

    Car::FuelFigures &ff = car.fuelFiguresCount(fuelFigures);
    for (std::uint16_t i = 0; i < fuelFigures; i++)
    {
        ff.next().speed(static_cast<std::uint16_t>(30 + 30 * i)).mpg(35.5f - i)
            .putUsageDescription(std::string("usage-") + std::to_string(i));  // group-level var-data
    }

    Car::PerformanceFigures &pf = car.performanceFiguresCount(performanceFigures);
    for (std::uint16_t i = 0; i < performanceFigures; i++)
    {
        Car::PerformanceFigures::Acceleration &acc =
            pf.next().octaneRating(static_cast<std::uint8_t>(95 + i)).accelerationCount(accelerationPer);
        for (std::uint16_t j = 0; j < accelerationPer; j++)
        {
            acc.next().mph(static_cast<std::uint16_t>(30 * (j + 1))).seconds(4.5f + j);
        }
    }

    car.putManufacturer(manufacturer);
    car.putModel(model);
    car.putActivationCode(activationCode);

    return Car::messageHeader::encodedLength() + car.encodedLength();
}

} // namespace

// =================================================================================================
// Message round-trips (each is byte-identical decode -> encode)
// =================================================================================================
class OtfMessageEncoderTest : public ::testing::Test
{
protected:
    void SetUp() override
    {
        if (g_irFile.empty())
        {
            GTEST_SKIP() << "set $SBE_EXAMPLE_IR (or pass the .sbeir path on argv) to run round-trip tests";
        }
    }
    char m_buffer[8192];
};

TEST_F(OtfMessageEncoderTest, FullyPopulatedCar)
{
    (void) encodeCar(m_buffer, sizeof(m_buffer), 2, 2, 2, "Honda", "Civic", "abcdef");
    assertOtfRoundTripIsIdentical(m_buffer, sizeof(m_buffer));
}

TEST_F(OtfMessageEncoderTest, EmptyGroupsAndVarData)
{
    (void) encodeCar(m_buffer, sizeof(m_buffer), 0, 0, 0, "", "", "");
    assertOtfRoundTripIsIdentical(m_buffer, sizeof(m_buffer));
}

TEST_F(OtfMessageEncoderTest, EmptyNestedGroup)
{
    // performance figures present, but each with zero acceleration entries
    (void) encodeCar(m_buffer, sizeof(m_buffer), 1, 3, 0, "M", "Md", "code");
    assertOtfRoundTripIsIdentical(m_buffer, sizeof(m_buffer));
}

TEST_F(OtfMessageEncoderTest, ManyGroupEntriesAndNestedGroups)
{
    (void) encodeCar(m_buffer, sizeof(m_buffer), 5, 4, 3,
        "AVeryLongManufacturerName", "AVeryLongModelName", "activation-code-value");
    assertOtfRoundTripIsIdentical(m_buffer, sizeof(m_buffer));
}

// =================================================================================================
// OtfHeaderEncoder writes a header that OtfHeaderDecoder reads back unchanged
// =================================================================================================
TEST(OtfHeaderEncoderTest, RoundTripsHeaderFields)
{
    if (g_irFile.empty())
    {
        GTEST_SKIP() << "set $SBE_EXAMPLE_IR to run this test";
    }

    IrDecoder ir;
    ASSERT_EQ(ir.decode(g_irFile.c_str()), 0);
    auto headerTokens = ir.header();

    OtfHeaderEncoder encoder(headerTokens);
    OtfHeaderDecoder decoder(headerTokens);

    char header[64] = {};
    encoder.encode(header, 47, 1, 10000, 1);

    EXPECT_EQ(decoder.getBlockLength(header), 47u);
    EXPECT_EQ(decoder.getTemplateId(header), 1u);
    EXPECT_EQ(decoder.getSchemaId(header), 10000u);
    EXPECT_EQ(decoder.getSchemaVersion(header), 1u);
    EXPECT_EQ(encoder.encodedLength(), decoder.encodedLength());
}

int main(int argc, char **argv)
{
    ::testing::InitGoogleTest(&argc, argv);
    if (const char *env = std::getenv("SBE_EXAMPLE_IR"))
    {
        g_irFile = env;
    }
    if (argc > 1)
    {
        g_irFile = argv[1];
    }
    return RUN_ALL_TESTS();
}
