package composite

import (
	"bytes"
	"testing"
)

func TestEncodeDecode(t *testing.T) {
	var data [256]byte
	var in Composite
	in.WrapForEncode(data[:], 0, uint64(len(data)))

	if in.SbeBlockAndHeaderLength() != 50 {
		t.Logf("Failed to encode, expected %d, got %d",
			50,
			in.SbeBlockAndHeaderLength(),
		)
		t.Fail()
	}

	in.Start().
		SetName("start").
		SetD(3.14).
		SetI(1).
		SetUIndex(0, 66).
		SetUIndex(1, 77).
		SetTruthval1(BooleanEnum_NULL_VALUE).
		SetTruthval2(BooleanEnum_T)

	in.End().
		SetName("end").
		SetD(0.31).
		SetI(2).
		SetUIndex(0, 77).
		SetUIndex(1, 88).
		SetTruthval1(BooleanEnum_T).
		SetTruthval2(BooleanEnum_F)

	var out Composite
	out.WrapForDecode(
		data[:],
		0,
		uint64(in.SbeSchemaVersion()),
		uint64(in.SbeBlockLength()),
		uint64(len(data)),
	)

	expected := `{"Name": "Composite", "sbeTemplateId": 1, "start": {"name": "start", "d": "3.14", "i": "1", "u": [66,77], "truthval1": "NULL_VALUE", "truthval2": "T"}, "end": {"name": "end", "d": "0.31", "i": "2", "u": [77,88], "truthval1": "T", "truthval2": "F"}}`
	if actual := out.String(); actual != expected {
		t.Logf("Failed to decode, expected %s, got %s", expected, actual)
		t.Fail()
	}
}

// wrapDirtyComposite wraps a buffer pre-filled with a non-zero sentinel, so that a setter which fails to pad
// cannot pass by accident on a freshly zeroed buffer.
func wrapDirtyComposite(t *testing.T, data []byte) *Composite {
	t.Helper()

	for i := range data {
		data[i] = 0xFF
	}

	var in Composite
	in.WrapForEncode(data, 0, uint64(len(data)))

	return &in
}

// name is a char[5], so every value shorter than 5 bytes must leave the remainder of the field NUL filled.
func TestSetNamePadsShortStringWithNul(t *testing.T) {
	tests := []struct {
		value    string
		expected []byte
	}{
		{"start", []byte{'s', 't', 'a', 'r', 't'}},
		{"end", []byte{'e', 'n', 'd', 0, 0}},
		{"a", []byte{'a', 0, 0, 0, 0}},
		{"", []byte{0, 0, 0, 0, 0}},
	}

	for _, test := range tests {
		var data [256]byte
		point := wrapDirtyComposite(t, data[:]).Start()

		point.SetName(test.value)

		if actual := point.Name(); !bytes.Equal(actual, test.expected) {
			t.Errorf("SetName(%q) encoded %v, expected %v", test.value, actual, test.expected)
		}
		if actual := point.GetNameAsString(); actual != test.value {
			t.Errorf("SetName(%q) decoded as %q", test.value, actual)
		}
	}
}

// Overwriting a longer value with a shorter one must not leave the tail of the previous value behind.
func TestSetNameOverwriteDoesNotLeakPreviousValue(t *testing.T) {
	var data [256]byte
	point := wrapDirtyComposite(t, data[:]).Start()

	point.SetName("start")
	point.SetName("end")

	if actual := point.GetNameAsString(); actual != "end" {
		t.Errorf("expected \"end\" after overwrite, got %q", actual)
	}
	if actual := point.Name(); !bytes.Equal(actual, []byte{'e', 'n', 'd', 0, 0}) {
		t.Errorf("stale bytes left after overwrite: %v", actual)
	}
}

// PutName shares the padding contract with SetName so the invariant holds whichever setter is used.
func TestPutNamePadsShortValueWithNul(t *testing.T) {
	var data [256]byte
	point := wrapDirtyComposite(t, data[:]).Start()

	point.PutName([]byte("start"))
	point.PutName([]byte("end"))

	if actual := point.Name(); !bytes.Equal(actual, []byte{'e', 'n', 'd', 0, 0}) {
		t.Errorf("PutName left stale bytes: %v", actual)
	}
}

// A value longer than the field must be rejected rather than overrun into the following field, matching the
// Java and C++ codecs which throw.
func TestSetNameTooLongPanics(t *testing.T) {
	var data [256]byte
	point := wrapDirtyComposite(t, data[:]).Start()

	defer func() {
		if recover() == nil {
			t.Error("expected SetName to panic for a value longer than the field")
		}
	}()

	point.SetName("toolong")
}

func TestPutNameTooLongPanics(t *testing.T) {
	var data [256]byte
	point := wrapDirtyComposite(t, data[:]).Start()

	defer func() {
		if recover() == nil {
			t.Error("expected PutName to panic for a value longer than the field")
		}
	}()

	point.PutName([]byte("toolong"))
}

// The padding must stop at the end of the field and not spill into whatever follows it.
func TestSetNamePaddingDoesNotDisturbAdjacentFields(t *testing.T) {
	var data [256]byte
	in := wrapDirtyComposite(t, data[:])

	in.Start().SetName("end").SetD(3.14).SetI(7)
	in.End().SetName("x")

	if actual := in.Start().GetNameAsString(); actual != "end" {
		t.Errorf("start.name corrupted: %q", actual)
	}
	if actual := in.Start().D(); actual != 3.14 {
		t.Errorf("start.d corrupted by name setter: %v", actual)
	}
	if actual := in.Start().I(); actual != 7 {
		t.Errorf("start.i corrupted by name setter: %v", actual)
	}
	if actual := in.End().GetNameAsString(); actual != "x" {
		t.Errorf("end.name corrupted: %q", actual)
	}
}
