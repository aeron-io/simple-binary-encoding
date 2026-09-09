package infinity_test

import (
	"math"
	"testing"
)

func TestInfinityConstantsAndNullValues(t *testing.T) {
	var values InfinityValues

	if got := math.Float32bits(values.FloatPositiveInfinity()); got != 0x7f800000 {
		t.Fatalf("unexpected FLOAT positive infinity bits: %#x", got)
	}
	if got := math.Float32bits(values.FloatNegativeInfinity()); got != 0xff800000 {
		t.Fatalf("unexpected FLOAT negative infinity bits: %#x", got)
	}
	if got := math.Float64bits(values.DoublePositiveInfinity()); got != 0x7ff0000000000000 {
		t.Fatalf("unexpected DOUBLE positive infinity bits: %#x", got)
	}
	if got := math.Float64bits(values.DoubleNegativeInfinity()); got != 0xfff0000000000000 {
		t.Fatalf("unexpected DOUBLE negative infinity bits: %#x", got)
	}

	if got := math.Float32bits(values.FloatPositiveNullNullValue()); got != 0x7f800000 {
		t.Fatalf("unexpected FLOAT positive null bits: %#x", got)
	}
	if got := math.Float32bits(values.FloatNegativeNullNullValue()); got != 0xff800000 {
		t.Fatalf("unexpected FLOAT negative null bits: %#x", got)
	}
	if got := math.Float64bits(values.DoublePositiveNullNullValue()); got != 0x7ff0000000000000 {
		t.Fatalf("unexpected DOUBLE positive null bits: %#x", got)
	}
	if got := math.Float64bits(values.DoubleNegativeNullNullValue()); got != 0xfff0000000000000 {
		t.Fatalf("unexpected DOUBLE negative null bits: %#x", got)
	}

	if got := math.Float32bits(values.FloatPositiveNullMinValue()); got != 0xff800000 {
		t.Fatalf("unexpected FLOAT positive null minimum bits: %#x", got)
	}
	if got := values.FloatPositiveNullMaxValue(); got != 1.5 {
		t.Fatalf("unexpected FLOAT positive null maximum: %v", got)
	}
	if got := values.FloatNegativeNullMinValue(); got != -1.5 {
		t.Fatalf("unexpected FLOAT negative null minimum: %v", got)
	}
	if got := math.Float32bits(values.FloatNegativeNullMaxValue()); got != 0x7f800000 {
		t.Fatalf("unexpected FLOAT negative null maximum bits: %#x", got)
	}
	if got := math.Float64bits(values.DoublePositiveNullMinValue()); got != 0xfff0000000000000 {
		t.Fatalf("unexpected DOUBLE positive null minimum bits: %#x", got)
	}
	if got := values.DoublePositiveNullMaxValue(); got != 1.5 {
		t.Fatalf("unexpected DOUBLE positive null maximum: %v", got)
	}
	if got := values.DoubleNegativeNullMinValue(); got != -1.5 {
		t.Fatalf("unexpected DOUBLE negative null minimum: %v", got)
	}
	if got := math.Float64bits(values.DoubleNegativeNullMaxValue()); got != 0x7ff0000000000000 {
		t.Fatalf("unexpected DOUBLE negative null maximum bits: %#x", got)
	}
}

func TestFloatConstants(t *testing.T) {
	var values FloatConstants

	if got := math.Float32bits(values.Positive()); got != 0x7f800000 {
		t.Fatalf("unexpected FLOAT positive constant bits: %#x", got)
	}
	if got := math.Float32bits(values.Negative()); got != 0xff800000 {
		t.Fatalf("unexpected FLOAT negative constant bits: %#x", got)
	}
}

func TestInfinityValuesEncodeNulls(t *testing.T) {
	var data [24]byte
	var values InfinityValues
	values.WrapForEncode(data[:], 0, uint64(len(data)))
	values.SetFloatPositiveNull(values.FloatPositiveNullNullValue())
	values.SetFloatNegativeNull(values.FloatNegativeNullNullValue())
	values.SetDoublePositiveNull(values.DoublePositiveNullNullValue())
	values.SetDoubleNegativeNull(values.DoubleNegativeNullNullValue())

	if got := math.Float32bits(values.FloatPositiveNull()); got != 0x7f800000 {
		t.Fatalf("unexpected encoded FLOAT positive null bits: %#x", got)
	}
	if got := math.Float32bits(values.FloatNegativeNull()); got != 0xff800000 {
		t.Fatalf("unexpected encoded FLOAT negative null bits: %#x", got)
	}
	if got := math.Float64bits(values.DoublePositiveNull()); got != 0x7ff0000000000000 {
		t.Fatalf("unexpected encoded DOUBLE positive null bits: %#x", got)
	}
	if got := math.Float64bits(values.DoubleNegativeNull()); got != 0xfff0000000000000 {
		t.Fatalf("unexpected encoded DOUBLE negative null bits: %#x", got)
	}
}
