package baseline

import "testing"

var benchSink uint64

func encodeCar(car *Car, buffer []byte) {
	car.WrapForEncode(buffer, 0, uint64(len(buffer))).
		SetSerialNumber(1234).
		SetModelYear(2013).
		SetAvailable(BooleanType_T).
		SetCode(Model_A).
		PutSomeNumbersValues(1, 2, 3, 4).
		SetVehicleCode("abcdef")
	car.Extras().SetCruiseControl(true).SetSportsPack(true)
	car.Engine().SetCapacity(2000).SetNumCylinders(4).SetManufacturerCode("123").SetEfficiency(35).
		SetBoosterEnabled(BooleanType_T).Booster().SetBoostType(BoostType_NITROUS).SetHorsePower(200)
	car.FuelFiguresCount(3).Next().
		SetSpeed(30).SetMpg(35.9).PutUsageDescription("Urban Cycle").Next().
		SetSpeed(55).SetMpg(49.0).PutUsageDescription("Combined Cycle").Next().
		SetSpeed(75).SetMpg(40.0).PutUsageDescription("Highway Cycle")
	performanceFigures := car.PerformanceFiguresCount(2)
	for octane := uint8(95); octane <= 99; octane += 4 {
		performanceFigures.Next().SetOctaneRating(octane).
			AccelerationCount(3).Next().
			SetMph(30).SetSeconds(3.8).Next().
			SetMph(60).SetSeconds(7.5).Next().
			SetMph(100).SetSeconds(12.2)
	}
	car.PutManufacturer("Honda").PutModel("Civic VTi").PutActivationCode("abcdef")
}

// BenchmarkEncodeCar encodes the Car that the Java and C++ benchmarks use.
func BenchmarkEncodeCar(b *testing.B) {
	var data [256]byte
	var car Car
	b.ReportAllocs()
	for i := 0; i < b.N; i++ {
		encodeCar(&car, data[:])
	}
}

// decodeCar reads every field of the car, with varData read by readVarData.
func decodeCar(car *Car, data []byte, readVarData func(*Car, *CarFuelFigures, int) int) uint64 {
	car.WrapForDecode(data, 0, uint64(car.SbeBlockLength()), uint64(car.SbeSchemaVersion()), uint64(len(data)))
	sum := car.SerialNumber() + uint64(car.ModelYear()) + uint64(car.Available()) + uint64(car.Code())
	for j := uint64(0); j < uint64(car.SomeNumbersLength()); j++ {
		sum += uint64(car.SomeNumbersIndex(j))
	}
	sum += uint64(car.Engine().Capacity()) + uint64(car.Engine().NumCylinders())
	fuelFigures := car.FuelFigures()
	for j := uint64(0); j < fuelFigures.Count(); j++ {
		fuelFigures.Next()
		sum += uint64(fuelFigures.Speed()) + uint64(fuelFigures.Mpg())
		sum += uint64(readVarData(nil, fuelFigures, 0))
	}
	performanceFigures := car.PerformanceFigures()
	for j := uint64(0); j < performanceFigures.Count(); j++ {
		performanceFigures.Next()
		sum += uint64(performanceFigures.OctaneRating())
		acceleration := performanceFigures.Acceleration()
		for k := uint64(0); k < acceleration.Count(); k++ {
			acceleration.Next()
			sum += uint64(acceleration.Mph()) + uint64(acceleration.Seconds())
		}
	}
	for field := 1; field <= 3; field++ {
		sum += uint64(readVarData(car, nil, field))
	}
	return sum
}

func readVarDataAsString(car *Car, fuelFigures *CarFuelFigures, field int) int {
	switch field {
	case 1:
		return len(car.Manufacturer())
	case 2:
		return len(car.Model())
	case 3:
		return len(car.ActivationCode())
	}
	return len(fuelFigures.UsageDescription())
}

// BenchmarkDecodeCar decodes every field of the car, reading varData as strings.
func BenchmarkDecodeCar(b *testing.B) {
	var data [256]byte
	var car Car
	encodeCar(&car, data[:])
	b.ReportAllocs()
	for i := 0; i < b.N; i++ {
		benchSink += decodeCar(&car, data[:], readVarDataAsString)
	}
}

func readVarDataAsBytes(car *Car, fuelFigures *CarFuelFigures, field int) int {
	switch field {
	case 1:
		return len(car.ManufacturerBytes())
	case 2:
		return len(car.ModelBytes())
	case 3:
		return len(car.ActivationCodeBytes())
	}
	return len(fuelFigures.UsageDescriptionBytes())
}

// BenchmarkDecodeCarBytes decodes every field of the car, reading varData as
// views of the buffer.
func BenchmarkDecodeCarBytes(b *testing.B) {
	var data [256]byte
	var car Car
	encodeCar(&car, data[:])
	b.ReportAllocs()
	for i := 0; i < b.N; i++ {
		benchSink += decodeCar(&car, data[:], readVarDataAsBytes)
	}
}
