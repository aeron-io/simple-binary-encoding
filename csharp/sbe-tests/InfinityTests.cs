using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Org.SbeTool.Sbe.Dll;
using Infinity_test;

namespace Org.SbeTool.Sbe.Tests
{
    [TestClass]
    public class InfinityTests
    {
        [TestMethod]
        public void ShouldRenderWidthAndSignCorrectInfinityConstants()
        {
            var buffer = new DirectBuffer(new byte[24]);
            var values = new InfinityValues();
            values.WrapForEncode(buffer, 0);

            Assert.AreEqual(0x7f800000, BitConverter.SingleToInt32Bits(values.FloatPositiveInfinity));
            Assert.AreEqual(unchecked((int)0xff800000), BitConverter.SingleToInt32Bits(values.FloatNegativeInfinity));
            Assert.AreEqual(0x7ff0000000000000L, BitConverter.DoubleToInt64Bits(values.DoublePositiveInfinity));
            Assert.AreEqual(unchecked((long)0xfff0000000000000UL), BitConverter.DoubleToInt64Bits(values.DoubleNegativeInfinity));

            Assert.AreEqual(0x7f800000, BitConverter.SingleToInt32Bits(InfinityValuesDto.FloatPositiveInfinity));
            Assert.AreEqual(unchecked((int)0xff800000), BitConverter.SingleToInt32Bits(InfinityValuesDto.FloatNegativeInfinity));
            Assert.AreEqual(0x7ff0000000000000L, BitConverter.DoubleToInt64Bits(InfinityValuesDto.DoublePositiveInfinity));
            Assert.AreEqual(unchecked((long)0xfff0000000000000UL), BitConverter.DoubleToInt64Bits(InfinityValuesDto.DoubleNegativeInfinity));
            Assert.AreEqual(0x7f800000, BitConverter.SingleToInt32Bits(FloatConstantsDto.Positive));
            Assert.AreEqual(unchecked((int)0xff800000), BitConverter.SingleToInt32Bits(FloatConstantsDto.Negative));

            Assert.AreEqual(unchecked((int)0xff800000), BitConverter.SingleToInt32Bits(InfinityValues.FloatPositiveNullMinValue));
            Assert.AreEqual(1.5f, InfinityValues.FloatPositiveNullMaxValue);
            Assert.AreEqual(-1.5f, InfinityValues.FloatNegativeNullMinValue);
            Assert.AreEqual(0x7f800000, BitConverter.SingleToInt32Bits(InfinityValues.FloatNegativeNullMaxValue));
            Assert.AreEqual(unchecked((long)0xfff0000000000000UL), BitConverter.DoubleToInt64Bits(InfinityValues.DoublePositiveNullMinValue));
            Assert.AreEqual(1.5d, InfinityValues.DoublePositiveNullMaxValue);
            Assert.AreEqual(-1.5d, InfinityValues.DoubleNegativeNullMinValue);
            Assert.AreEqual(0x7ff0000000000000L, BitConverter.DoubleToInt64Bits(InfinityValues.DoubleNegativeNullMaxValue));

            var constants = new FloatConstants();
            constants.WrapForEncode(buffer, 0);
            Assert.AreEqual(0x7f800000, BitConverter.SingleToInt32Bits(constants.Positive));
            Assert.AreEqual(unchecked((int)0xff800000), BitConverter.SingleToInt32Bits(constants.Negative));
        }

        [TestMethod]
        public void ShouldEncodeWidthAndSignCorrectOptionalNullValues()
        {
            var buffer = new DirectBuffer(new byte[24]);
            var values = new InfinityValues();
            values.WrapForEncode(buffer, 0);
            values.FloatPositiveNull = InfinityValues.FloatPositiveNullNullValue;
            values.FloatNegativeNull = InfinityValues.FloatNegativeNullNullValue;
            values.DoublePositiveNull = InfinityValues.DoublePositiveNullNullValue;
            values.DoubleNegativeNull = InfinityValues.DoubleNegativeNullNullValue;

            Assert.AreEqual(0x7f800000, BitConverter.SingleToInt32Bits(values.FloatPositiveNull));
            Assert.AreEqual(unchecked((int)0xff800000), BitConverter.SingleToInt32Bits(values.FloatNegativeNull));
            Assert.AreEqual(0x7ff0000000000000L, BitConverter.DoubleToInt64Bits(values.DoublePositiveNull));
            Assert.AreEqual(unchecked((long)0xfff0000000000000UL), BitConverter.DoubleToInt64Bits(values.DoubleNegativeNull));
        }
    }
}
