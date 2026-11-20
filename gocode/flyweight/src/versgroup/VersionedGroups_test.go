package versgroup

import (
	"fmt"
	"testing"
)

const bufferLen = 2048

func TestV2DecodesV1Message(t *testing.T) {
	for _, group1Count := range []uint16{0, 2} {
		t.Run(fmt.Sprintf("Group1Count=%d", group1Count), func(t *testing.T) {
			buffer := make([]byte, bufferLen)

			var v1 VersionedGroupsV1
			v1.WrapForEncode(buffer, 0, uint64(len(buffer)))
			v1.SetFieldA1(1)
			group1V1 := v1.Group1Count(group1Count)
			for i := uint32(0); i < uint32(group1Count); i++ {
				group1V1.Next().SetFieldB1(10 + i)
			}
			v1.PutString1("asdf")

			var v2 VersionedGroupsV2
			v2.WrapForDecode(
				buffer,
				0,
				uint64(v1.SbeBlockLength()),
				uint64(v1.SbeSchemaVersion()),
				v1.EncodedLength())

			if v2.FieldA1() != 1 {
				t.Errorf("FieldA1: got %d, want 1", v2.FieldA1())
			}

			group1 := v2.Group1()
			if count := group1.Count(); count != uint64(group1Count) {
				t.Fatalf("Group1 count: got %d, want %d", count, group1Count)
			}
			for i := uint32(0); group1.HasNext(); i++ {
				entry := group1.Next()
				if value := entry.FieldB1(); value != 10+i {
					t.Errorf("Group1[%d].FieldB1: got %d, want %d", i, value, 10+i)
				}
				if entry.NestedGroup2InActingVersion() {
					t.Errorf("Group1[%d].NestedGroup2InActingVersion: got true, want false", i)
				}
				if count := entry.NestedGroup2().Count(); count != 0 {
					t.Errorf("Group1[%d].NestedGroup2 count: got %d, want 0", i, count)
				}
			}

			if v2.Group2InActingVersion() {
				t.Errorf("Group2InActingVersion: got true, want false")
			}
			if count := v2.Group2().Count(); count != 0 {
				t.Errorf("Group2 count: got %d, want 0", count)
			}
			if value := v2.String1(); value != "asdf" {
				t.Errorf("String1: got %q, want %q", value, "asdf")
			}
			if length, want := v2.DecodeLength(), v1.EncodedLength(); length != want {
				t.Errorf("DecodeLength: got %d, want %d", length, want)
			}
		})
	}
}

func TestV2DecodesV2Message(t *testing.T) {
	buffer := make([]byte, bufferLen)

	var in VersionedGroupsV2
	in.WrapForEncode(buffer, 0, uint64(len(buffer)))
	in.SetFieldA1(1)
	group1In := in.Group1Count(2)
	for i := uint32(0); i < 2; i++ {
		group1In.Next().SetFieldB1(10 + i)
		group1In.NestedGroup2Count(1).Next().SetFieldC2(20 + i)
	}
	in.Group2Count(1).Next().SetFieldD2(30)
	in.PutString1("asdf")

	var out VersionedGroupsV2
	out.WrapForDecode(
		buffer,
		0,
		uint64(in.SbeBlockLength()),
		uint64(in.SbeSchemaVersion()),
		in.EncodedLength())

	group1 := out.Group1()
	if count := group1.Count(); count != 2 {
		t.Fatalf("Group1 count: got %d, want 2", count)
	}
	for i := uint32(0); group1.HasNext(); i++ {
		entry := group1.Next()
		if value := entry.FieldB1(); value != 10+i {
			t.Errorf("Group1[%d].FieldB1: got %d, want %d", i, value, 10+i)
		}
		nested := entry.NestedGroup2()
		if count := nested.Count(); count != 1 {
			t.Fatalf("Group1[%d].NestedGroup2 count: got %d, want 1", i, count)
		}
		if value := nested.Next().FieldC2(); value != 20+i {
			t.Errorf("Group1[%d].NestedGroup2[0].FieldC2: got %d, want %d", i, value, 20+i)
		}
	}

	group2 := out.Group2()
	if count := group2.Count(); count != 1 {
		t.Fatalf("Group2 count: got %d, want 1", count)
	}
	if value := group2.Next().FieldD2(); value != 30 {
		t.Errorf("Group2[0].FieldD2: got %d, want 30", value)
	}
	if value := out.String1(); value != "asdf" {
		t.Errorf("String1: got %q, want %q", value, "asdf")
	}
	if length, want := out.DecodeLength(), in.EncodedLength(); length != want {
		t.Errorf("DecodeLength: got %d, want %d", length, want)
	}
}
