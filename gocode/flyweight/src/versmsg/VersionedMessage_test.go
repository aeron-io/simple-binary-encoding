package versmsg

import (
	"testing"
)

const bufferLen = 2048

func TestV1DecodesV2Message(t *testing.T) {
	buffer := make([]byte, bufferLen)

	var v2 VersionedMessageV2
	v2.WrapForEncode(buffer, 0, uint64(len(buffer)))
	v2.SetFieldA1(1)
	v2.SetFieldB1(2)
	v2.SetFieldC2(3)
	v2.SetFieldD2(4)
	v2.SetFieldE2(5)
	v2.PutString1("asdf")

	var v1 VersionedMessageV1
	v1.WrapForDecode(
		buffer,
		0,
		uint64(v2.SbeBlockLength()),
		uint64(v2.SbeSchemaVersion()),
		v2.EncodedLength())

	if v1.FieldA1() != 1 {
		t.Errorf("FieldA1: got %d, want 1", v1.FieldA1())
	}
	if v1.FieldB1() != 2 {
		t.Errorf("FieldB1: got %d, want 2", v1.FieldB1())
	}
	if value := v1.String1(); value != "asdf" {
		t.Errorf("String1: got %q, want %q", value, "asdf")
	}
	if length := v1.DecodeLength(); length != 26 {
		t.Errorf("DecodeLength: got %d, want 26", length)
	}
}

func TestV2DecodesV1Message(t *testing.T) {
	buffer := make([]byte, bufferLen)

	var v1 VersionedMessageV1
	v1.WrapForEncode(buffer, 0, uint64(len(buffer)))
	v1.SetFieldA1(1)
	v1.SetFieldB1(2)
	v1.PutString1("asdf")

	var v2 VersionedMessageV2
	v2.WrapForDecode(
		buffer,
		0,
		uint64(v1.SbeBlockLength()),
		uint64(v1.SbeSchemaVersion()),
		v1.EncodedLength())

	if v2.FieldA1() != 1 {
		t.Errorf("FieldA1: got %d, want 1", v2.FieldA1())
	}
	if v2.FieldB1() != 2 {
		t.Errorf("FieldB1: got %d, want 2", v2.FieldB1())
	}
	if value := v2.String1(); value != "asdf" {
		t.Errorf("String1: got %q, want %q", value, "asdf")
	}
	if length := v2.DecodeLength(); length != 14 {
		t.Errorf("DecodeLength: got %d, want 14", length)
	}
}
