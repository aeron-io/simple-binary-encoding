package group_with_data

import (
	"bytes"
	"testing"
)

// The var data length and numInGroup are uint8 in this schema, so a longer value must be
// rejected rather than written with a length or count that no longer matches the payload.
func TestEncodeRejectsVarDataLongerThanLengthType(t *testing.T) {
	m := NewSbeGoMarshaller()
	var in TestMessage1
	in.Entries = []TestMessage1Entries{{VarDataField: make([]byte, 300)}}

	var buf bytes.Buffer
	if err := in.Encode(m, &buf, true); err == nil {
		t.Fatalf("expected an error encoding %d bytes of var data with a uint8 length", len(in.Entries[0].VarDataField))
	}
}

func TestEncodeRejectsGroupLargerThanNumInGroupType(t *testing.T) {
	m := NewSbeGoMarshaller()
	var in TestMessage1
	in.Entries = make([]TestMessage1Entries, 300)

	var buf bytes.Buffer
	if err := in.Encode(m, &buf, true); err == nil {
		t.Fatalf("expected an error encoding %d group entries with a uint8 numInGroup", len(in.Entries))
	}
}

func TestEncodeAcceptsMaximumVarDataLengthAndGroupCount(t *testing.T) {
	m := NewSbeGoMarshaller()
	var in TestMessage1
	in.Entries = make([]TestMessage1Entries, 254)
	in.Entries[0].VarDataField = bytes.Repeat([]byte("a"), 254)

	var buf bytes.Buffer
	if err := in.Encode(m, &buf, true); err != nil {
		t.Fatal("Encoding Error", err)
	}

	var out TestMessage1
	if err := out.Decode(m, &buf, in.SbeSchemaVersion(), in.SbeBlockLength(), true); err != nil {
		t.Fatal("Decoding Error", err)
	}
	if len(out.Entries) != 254 || !bytes.Equal(out.Entries[0].VarDataField, in.Entries[0].VarDataField) {
		t.Fatalf("expected 254 entries and 254 bytes of var data, got %d entries", len(out.Entries))
	}
}
