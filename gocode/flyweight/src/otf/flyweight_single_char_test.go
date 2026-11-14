package otf

import (
	stdjson "encoding/json"
	"github.com/real-logic/simple-binary-encoding/otf/test/flyweight_single_char"
	"github.com/stretchr/testify/assert"
	"testing"
)

//go:generate java --add-opens java.base/jdk.internal.misc=ALL-UNNAMED -Dsbe.output.dir=. -Dsbe.target.language=golang -Dsbe.go.generate.generate.flyweights=true -Dsbe.generate.ir=true -Dsbe.keyword.append.token=X -jar ../../../${SBE_JAR} ../../../../sbe-tool/src/test/resources/flyweight-single-char-schema.xml

func TestSingleCharFieldsRenderAsValidJson(t *testing.T) {
	cases := []struct {
		name     string
		value    byte
		expected string
	}{
		{"letter", 'A', "A"},
		{"quote", '"', "\""},
		{"backslash", '\\', "\\"},
		{"newline", '\n', "\n"},
		{"control", 0x01, "\x01"},
		{"high", 0xe9, "é"},
	}

	for _, c := range cases {
		t.Run(c.name, func(t *testing.T) {
			buffer := make([]byte, 64)
			var letter flyweight_single_char.Letter
			letter.WrapAndApplyHeader(buffer, 0, uint64(len(buffer))).SetCode(c.value)
			letter.EntriesCount(1).Next().SetSide(c.value)

			var decoder flyweight_single_char.Letter
			decoder.WrapForDecode(buffer, 8, uint64(letter.SbeBlockLength()), 0, uint64(len(buffer)))

			var decoded struct {
				Code    string
				Entries []struct{ Side string }
			}
			if err := stdjson.Unmarshal([]byte(decoder.String()), &decoded); err != nil {
				t.Fatalf("invalid JSON: %v\n%s", err, decoder.String())
			}
			assert.Equal(t, c.expected, decoded.Code)
			assert.Equal(t, c.expected, decoded.Entries[0].Side)
		})
	}
}
