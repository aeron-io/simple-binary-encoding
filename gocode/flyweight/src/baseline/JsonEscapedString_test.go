package baseline

import (
	"encoding/json"
	"testing"
)

func TestJsonEscapedStringEscapesControlCharacters(t *testing.T) {
	var data [256]byte
	var in Car
	in.WrapForEncode(data[:], 0, uint64(len(data)))
	in.FuelFiguresCount(0)
	in.PerformanceFiguresCount(0)
	in.PutManufacturer("a\x01\"b\x1f").
		PutModel("Civic").
		PutActivationCode("x")

	var out Car
	out.WrapForDecode(data[:], 0, uint64(in.SbeBlockLength()), uint64(in.SbeSchemaVersion()), uint64(len(data)))
	out.FuelFigures()
	out.PerformanceFigures()

	escaped := out.GetManufacturerAsJsonEscapedString()
	if escaped != `a\u0001\"b\u001f` {
		t.Fatalf("unexpected escaped string: %s", escaped)
	}

	var decoded string
	if err := json.Unmarshal([]byte(`"`+escaped+`"`), &decoded); err != nil || decoded != "a\x01\"b\x1f" {
		t.Fatalf("escaped string does not round trip through JSON: %q, %v", decoded, err)
	}
}
