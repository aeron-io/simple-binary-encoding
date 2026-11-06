package baseline

import (
	"bytes"
	"encoding/binary"
	"runtime"
	"testing"
)

// A group count comes off the wire, so a message that claims many entries but carries none
// must not make the decoder allocate room for all of them up front.
func TestDecodeDoesNotPreallocateUnreceivedGroupEntries(t *testing.T) {
	m := NewSbeGoMarshaller()
	var in Car
	var buf bytes.Buffer
	if err := in.Encode(m, &buf, false); err != nil {
		t.Fatal("Encoding Error", err)
	}

	// The fuelFigures group header follows the root block: blockLength then numInGroup.
	data := buf.Bytes()[:in.SbeBlockLength()+4]
	binary.LittleEndian.PutUint16(data[in.SbeBlockLength()+2:], 65534)

	var before, after runtime.MemStats
	var out Car
	runtime.GC()
	runtime.ReadMemStats(&before)
	err := out.Decode(m, bytes.NewReader(data), in.SbeSchemaVersion(), in.SbeBlockLength(), false)
	runtime.ReadMemStats(&after)

	if err == nil {
		t.Fatal("expected an error decoding a truncated group")
	}
	// Allocating all 65534 entries would take about 2 MiB.
	if allocated := after.TotalAlloc - before.TotalAlloc; allocated > 256*1024 {
		t.Fatalf("decoding allocated %d bytes for a group with no entries on the wire", allocated)
	}
}

func TestDecodeGroupLargerThanInitialCapacity(t *testing.T) {
	m := NewSbeGoMarshaller()
	var in Car
	for i := 0; i < 3000; i++ {
		in.FuelFigures = append(in.FuelFigures, CarFuelFigures{uint16(i), float32(i), []uint8("cycle")})
	}
	var buf bytes.Buffer
	if err := in.Encode(m, &buf, false); err != nil {
		t.Fatal("Encoding Error", err)
	}

	var out Car
	if err := out.Decode(m, &buf, in.SbeSchemaVersion(), in.SbeBlockLength(), false); err != nil {
		t.Fatal("Decoding Error", err)
	}
	if len(out.FuelFigures) != len(in.FuelFigures) {
		t.Fatalf("expected %d fuel figures, got %d", len(in.FuelFigures), len(out.FuelFigures))
	}
	for i := range in.FuelFigures {
		if out.FuelFigures[i].Speed != in.FuelFigures[i].Speed ||
			string(out.FuelFigures[i].UsageDescription) != string(in.FuelFigures[i].UsageDescription) {
			t.Fatalf("fuel figure %d: expected %v, got %v", i, in.FuelFigures[i], out.FuelFigures[i])
		}
	}
}
