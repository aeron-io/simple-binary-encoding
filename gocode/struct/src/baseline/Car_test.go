package baseline

import (
	"bytes"
	_ "fmt"
	"testing"
)

func TestEncodeDecodeCar(t *testing.T) {

	m := NewSbeGoMarshaller()
	var vehicleCode [6]byte
	copy(vehicleCode[:], "abcdef")

	var manufacturerCode [3]byte
	copy(manufacturerCode[:], "123")

	var optionalExtras [8]bool
	optionalExtras[OptionalExtrasChoice.CruiseControl] = true
	optionalExtras[OptionalExtrasChoice.SportsPack] = true

	var engine Engine
	engine = Engine{2000, 4, 0, manufacturerCode, [6]byte{}, 42, BooleanType.T, EngineBooster{BoostType.NITROUS, 200}}

	manufacturer := []uint8("Honda")
	model := []uint8("Civic VTi")
	activationCode := []uint8("deadbeef")

	var fuel []CarFuelFigures
	fuel = append(fuel, CarFuelFigures{30, 35.9, []uint8("Urban Cycle")})
	fuel = append(fuel, CarFuelFigures{55, 49.0, []uint8("Combined Cycle")})
	fuel = append(fuel, CarFuelFigures{75, 40.0, []uint8("Highway Cycle")})

	var acc1 []CarPerformanceFiguresAcceleration
	acc1 = append(acc1, CarPerformanceFiguresAcceleration{30, 3.8})
	acc1 = append(acc1, CarPerformanceFiguresAcceleration{60, 7.5})
	acc1 = append(acc1, CarPerformanceFiguresAcceleration{100, 12.2})

	var acc2 []CarPerformanceFiguresAcceleration
	acc2 = append(acc2, CarPerformanceFiguresAcceleration{30, 3.8})
	acc2 = append(acc2, CarPerformanceFiguresAcceleration{60, 7.5})
	acc2 = append(acc2, CarPerformanceFiguresAcceleration{100, 12.2})

	var pf []CarPerformanceFigures
	pf = append(pf, CarPerformanceFigures{95, acc1})
	pf = append(pf, CarPerformanceFigures{99, acc2})

	in := Car{1234, 2013, BooleanType.T, Model.A, [4]uint32{0, 1, 2, 3}, vehicleCode, optionalExtras, Model.A, engine, fuel, pf, manufacturer, model, activationCode}

	var buf = new(bytes.Buffer)
	if err := in.Encode(m, buf, true); err != nil {
		t.Log("Encoding Error", err)
		t.Fail()
	}

	var out Car = *new(Car)
	if err := out.Decode(m, buf, in.SbeSchemaVersion(), in.SbeBlockLength(), true); err != nil {
		t.Log("Decoding Error", err)
		t.Fail()
	}

	if in.SerialNumber != out.SerialNumber {
		t.Log("in.SerialNumber != out.SerialNumber:\n", in.SerialNumber, out.SerialNumber)
		t.Fail()
	}
	if in.ModelYear != out.ModelYear {
		t.Log("in.ModelYear != out.ModelYear:\n", in.ModelYear, out.ModelYear)
		t.Fail()
	}
	if in.Available != out.Available {
		t.Log("in.Available != out.Available:\n", in.Available, out.Available)
		t.Fail()
	}
	if in.Code != out.Code {
		t.Log("in.Code != out.Code:\n", in.Code, out.Code)
		t.Fail()
	}
	if in.SomeNumbers != out.SomeNumbers {
		t.Log("in.SomeNumbers != out.SomeNumbers:\n", in.SomeNumbers, out.SomeNumbers)
		t.Fail()
	}
	if in.VehicleCode != out.VehicleCode {
		t.Log("in.VehicleCode != out.VehicleCode:\n", in.VehicleCode, out.VehicleCode)
		t.Fail()
	}
	if in.Extras != out.Extras {
		t.Log("in.Extras != out.Extras:\n", in.Extras, out.Extras)
		t.Fail()
	}

	// DiscountedModel is constant
	if Model.C != out.DiscountedModel {
		t.Log("in.DiscountedModel != out.DiscountedModel:\n", in.DiscountedModel, out.DiscountedModel)
		t.Fail()
	}

	// Engine has two constant values which should come back filled in
	if in.Engine == out.Engine {
		t.Log("in.Engine == out.Engine (and they should be different):\n", in.Engine, out.Engine)
		t.Fail()
	}

	copy(in.Engine.Fuel[:], "Petrol")
	in.Engine.MaxRpm = 9000
	if in.Engine != out.Engine {
		t.Log("in.Engine != out.Engine:\n", in.Engine, out.Engine)
		t.Fail()
	}

	return

}

func TestDecodeJavaBuffer(t *testing.T) {
	// See ~gocode/src/example-schema/CarExample.go for how this is generated
	data := []byte{45, 0, 1, 0, 1, 0, 0, 0, 210, 4, 0, 0, 0, 0, 0, 0, 221, 7, 1, 65, 1, 0, 0, 0, 2, 0, 0, 0, 3, 0, 0, 0, 4, 0, 0, 0, 97, 98, 99, 100, 101, 102, 6, 208, 7, 4, 49, 50, 51, 35, 1, 78, 200, 6, 0, 3, 0, 30, 0, 154, 153, 15, 66, 11, 0, 0, 0, 85, 114, 98, 97, 110, 32, 67, 121, 99, 108, 101, 55, 0, 0, 0, 68, 66, 14, 0, 0, 0, 67, 111, 109, 98, 105, 110, 101, 100, 32, 67, 121, 99, 108, 101, 75, 0, 0, 0, 32, 66, 13, 0, 0, 0, 72, 105, 103, 104, 119, 97, 121, 32, 67, 121, 99, 108, 101, 1, 0, 2, 0, 95, 6, 0, 3, 0, 30, 0, 0, 0, 128, 64, 60, 0, 0, 0, 240, 64, 100, 0, 51, 51, 67, 65, 99, 6, 0, 3, 0, 30, 0, 51, 51, 115, 64, 60, 0, 51, 51, 227, 64, 100, 0, 205, 204, 60, 65, 5, 0, 0, 0, 72, 111, 110, 100, 97, 9, 0, 0, 0, 67, 105, 118, 105, 99, 32, 86, 84, 105, 6, 0, 0, 0, 97, 98, 99, 100, 101, 102}

	buf := bytes.NewBuffer(data)
	m := NewSbeGoMarshaller()

	var hdr SbeGoMessageHeader
	if err := hdr.Decode(m, buf); err != nil {
		t.Log("Failed to decode message header", err)
		t.Fail()
	}

	// fmt.Println("BlockLength = ", m.BlockLength)
	// fmt.Println("TemplateId = ", m.TemplateId)
	// fmt.Println("SchemaId = ", m.SchemaId)
	// fmt.Println("Version = ", m.Version)
	// fmt.Println("bytes: ", buf.Len())
	var c Car
	if err := c.Decode(m, buf, hdr.Version, hdr.BlockLength, true); err != nil {
		t.Log("Failed to decode car", err)
		t.Fail()
	}
	// fmt.Println(c)
	return
}

// newRangeCheckableCar builds a Car that passes RangeCheck, so that tests below can vary a single
// character array and attribute any failure to that field alone.
func newRangeCheckableCar(vehicleCode [6]byte, manufacturerCode [3]byte) Car {
	var optionalExtras [8]bool
	optionalExtras[OptionalExtrasChoice.CruiseControl] = true

	engine := Engine{2000, 4, 0, manufacturerCode, [6]byte{}, 42, BooleanType.T,
		EngineBooster{BoostType.NITROUS, 200}}

	fuel := []CarFuelFigures{{30, 35.9, []uint8("Urban Cycle")}}
	acceleration := []CarPerformanceFiguresAcceleration{{30, 3.8}}
	performance := []CarPerformanceFigures{{95, acceleration}}

	return Car{1234, 2013, BooleanType.T, Model.A, [4]uint32{0, 1, 2, 3}, vehicleCode,
		optionalExtras, Model.A, engine, fuel, performance,
		[]uint8("Honda"), []uint8("Civic VTi"), []uint8("deadbeef")}
}

func makeVehicleCode(value string) [6]byte {
	var code [6]byte
	copy(code[:], value)

	return code
}

// vehicleCode is a char[6]. NUL is the pad byte written by the Java, C++ and Go flyweight codecs, and is
// also what the idiomatic `copy` into a zeroed Go array leaves behind, so RangeCheck has to accept it.
// Space padding must keep working, and genuinely out of range bytes must still be rejected.
func TestRangeCheckAcceptsPaddedCharArrays(t *testing.T) {
	var manufacturerCode [3]byte
	copy(manufacturerCode[:], "123")

	tests := []struct {
		name      string
		code      [6]byte
		wantError bool
	}{
		{"exact length", makeVehicleCode("abcdef"), false},
		{"NUL padded", makeVehicleCode("abc"), false},
		{"space padded", makeVehicleCode("abc   "), false},
		{"all NUL", makeVehicleCode(""), false},
		{"all space", makeVehicleCode("      "), false},
		{"below range", [6]byte{'a', 'b', 'c', 0x01, 0, 0}, true},
		{"above range", [6]byte{'a', 'b', 'c', 0x7F, 0, 0}, true},
	}

	for _, test := range tests {
		c := newRangeCheckableCar(test.code, manufacturerCode)
		err := c.RangeCheck(c.SbeSchemaVersion(), c.SbeSchemaVersion())

		if test.wantError && err == nil {
			t.Errorf("%s (%q): expected RangeCheck to fail, got nil", test.name, test.code)
		}
		if !test.wantError && err != nil {
			t.Errorf("%s (%q): expected RangeCheck to pass, got %v", test.name, test.code, err)
		}
	}
}

// A NUL padded char array must survive a round trip with range checking enabled at both ends.
func TestEncodeDecodeNulPaddedCharArray(t *testing.T) {
	var manufacturerCode [3]byte
	copy(manufacturerCode[:], "12")

	in := newRangeCheckableCar(makeVehicleCode("abc"), manufacturerCode)

	m := NewSbeGoMarshaller()
	buf := new(bytes.Buffer)
	if err := in.Encode(m, buf, true); err != nil {
		t.Fatalf("Encode with range check failed: %v", err)
	}

	var out Car
	if err := out.Decode(m, buf, in.SbeSchemaVersion(), in.SbeBlockLength(), true); err != nil {
		t.Fatalf("Decode with range check failed: %v", err)
	}

	if in.VehicleCode != out.VehicleCode {
		t.Errorf("VehicleCode round trip: encoded %q, decoded %q", in.VehicleCode, out.VehicleCode)
	}
	if in.Engine.ManufacturerCode != out.Engine.ManufacturerCode {
		t.Errorf("ManufacturerCode round trip: encoded %q, decoded %q",
			in.Engine.ManufacturerCode, out.Engine.ManufacturerCode)
	}
}

// The NullValue escape must apply only to character arrays; numeric arrays keep strict range checking.
func TestRangeCheckStillStrictForNumericArrays(t *testing.T) {
	var manufacturerCode [3]byte
	copy(manufacturerCode[:], "123")

	c := newRangeCheckableCar(makeVehicleCode("abcdef"), manufacturerCode)
	c.SomeNumbers[2] = c.SomeNumbersMaxValue() + 1

	if err := c.RangeCheck(c.SbeSchemaVersion(), c.SbeSchemaVersion()); err == nil {
		t.Error("expected RangeCheck to reject an out of range uint32 array element, got nil")
	}
}
