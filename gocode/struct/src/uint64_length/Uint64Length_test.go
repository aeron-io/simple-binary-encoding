package uint64_length

import (
	"bytes"
	"encoding/binary"
	"testing"
)

// A uint64 var data length above math.MaxInt must not convert to a negative int and leave the
// field empty: the decoder has to fail on the missing bytes.
func TestDecodeRejectsVarDataLengthAboveMaxInt(t *testing.T) {
	m := NewSbeGoMarshaller()
	in := Uint64Lengths{Id: 7, Text: []uint8("abc")}
	var buf bytes.Buffer
	if err := in.Encode(m, &buf, false); err != nil {
		t.Fatal("Encoding Error", err)
	}

	// text's length follows the root block and the entries group header.
	offset := int(in.SbeBlockLength()) + 2 + 8
	data := buf.Bytes()
	binary.LittleEndian.PutUint64(data[offset:], 1<<63)

	var out Uint64Lengths
	defer func() {
		if r := recover(); r != nil {
			t.Fatalf("decoding panicked: %v", r)
		}
	}()
	if err := out.Decode(m, bytes.NewReader(data), in.SbeSchemaVersion(), in.SbeBlockLength(), false); err == nil {
		t.Fatalf("expected an error decoding a var data length of 2^63, got text %q", out.Text)
	}
}

func TestDecodeUint64LengthRoundTrip(t *testing.T) {
	m := NewSbeGoMarshaller()
	in := Uint64Lengths{Id: 7, Entries: []Uint64LengthsEntries{{Value: 1}, {Value: 2}}, Text: []uint8("abc")}
	var buf bytes.Buffer
	if err := in.Encode(m, &buf, false); err != nil {
		t.Fatal("Encoding Error", err)
	}

	var out Uint64Lengths
	if err := out.Decode(m, &buf, in.SbeSchemaVersion(), in.SbeBlockLength(), false); err != nil {
		t.Fatal("Decoding Error", err)
	}
	if out.Id != 7 || len(out.Entries) != 2 || out.Entries[1].Value != 2 || string(out.Text) != "abc" {
		t.Fatalf("round trip mismatch: %+v", out)
	}
}
