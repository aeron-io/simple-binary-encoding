package baseline

import (
	"bytes"
	"encoding/binary"
	"runtime"
	"testing"
)

// A var data length comes off the wire, so a message that claims a long value but carries none
// of it must not make the decoder allocate the whole value up front.
func TestDecodeDoesNotPreallocateUnreceivedVarData(t *testing.T) {
	m := NewSbeGoMarshaller()
	var in Car
	var buf bytes.Buffer
	if err := in.Encode(m, &buf, false); err != nil {
		t.Fatal("Encoding Error", err)
	}

	// manufacturer's length follows the root block and the fuelFigures and performanceFigures group headers.
	offset := int(in.SbeBlockLength()) + 4 + 4
	data := buf.Bytes()[:offset+4]
	binary.LittleEndian.PutUint32(data[offset:], 16*1024*1024)

	var before, after runtime.MemStats
	var out Car
	runtime.GC()
	runtime.ReadMemStats(&before)
	err := out.Decode(m, bytes.NewReader(data), in.SbeSchemaVersion(), in.SbeBlockLength(), false)
	runtime.ReadMemStats(&after)

	if err == nil {
		t.Fatal("expected an error decoding truncated var data")
	}
	if allocated := after.TotalAlloc - before.TotalAlloc; allocated > 1024*1024 {
		t.Fatalf("decoding allocated %d bytes for var data with no bytes on the wire", allocated)
	}
}

func TestDecodeVarDataLongerThanOneChunk(t *testing.T) {
	m := NewSbeGoMarshaller()
	var in Car
	in.Manufacturer = make([]uint8, 200000)
	for i := range in.Manufacturer {
		in.Manufacturer[i] = uint8('a' + i%26)
	}
	in.Model = []uint8("Civic")
	var buf bytes.Buffer
	if err := in.Encode(m, &buf, false); err != nil {
		t.Fatal("Encoding Error", err)
	}

	var out Car
	if err := out.Decode(m, &buf, in.SbeSchemaVersion(), in.SbeBlockLength(), false); err != nil {
		t.Fatal("Decoding Error", err)
	}
	if !bytes.Equal(out.Manufacturer, in.Manufacturer) {
		t.Fatalf("manufacturer: expected %d bytes, got %d differing", len(in.Manufacturer), len(out.Manufacturer))
	}
	if string(out.Model) != "Civic" {
		t.Fatalf("model: expected Civic, got %q", out.Model)
	}
}
