package otf

import (
	"encoding/binary"
	stdjson "encoding/json"
	"github.com/real-logic/simple-binary-encoding/json"
	"github.com/real-logic/simple-binary-encoding/otf"
	"github.com/stretchr/testify/assert"
	"testing"
)

//go:generate java --add-opens java.base/jdk.internal.misc=ALL-UNNAMED -Dsbe.output.dir=. -Dsbe.target.language=golang -Dsbe.go.generate.generate.flyweights=true -Dsbe.generate.ir=true -Dsbe.keyword.append.token=X -jar ../../../${SBE_JAR} ../../../../sbe-tool/src/test/resources/json-char-escaping-schema.xml

const JsonCharSchemaFilename = "json-char-escaping-schema.sbeir"

// printChars renders a Chars message whose char fields and var data hold the given bytes.
func printChars(t *testing.T, single byte, array string, first byte, last byte, note string) string {
	ir := otf.NewIrDecoder()
	if ir.DecodeFile(JsonCharSchemaFilename) < 0 {
		t.Fatalf("Error decoding %s", JsonCharSchemaFilename)
	}
	printer, err := json.NewJsonPrinter(ir)
	assert.NoError(t, err)

	const headerLength = 8
	const blockLength = 1 + 4 + 2
	buffer := make([]byte, headerLength+blockLength+2+len(note))
	binary.LittleEndian.PutUint16(buffer[0:], blockLength)
	binary.LittleEndian.PutUint16(buffer[2:], 1)
	binary.LittleEndian.PutUint16(buffer[4:], 8)
	binary.LittleEndian.PutUint16(buffer[6:], 0)
	buffer[headerLength] = single
	copy(buffer[headerLength+1:], array)
	buffer[headerLength+5] = first
	buffer[headerLength+6] = last
	binary.LittleEndian.PutUint16(buffer[headerLength+blockLength:], uint16(len(note)))
	copy(buffer[headerLength+blockLength+2:], note)

	out, err := printer.PrintJson(buffer)
	assert.NoError(t, err)
	return out
}

func decodeChars(t *testing.T, out string) map[string]interface{} {
	var decoded map[string]interface{}
	if err := stdjson.Unmarshal([]byte(out), &decoded); err != nil {
		t.Fatalf("invalid JSON: %v\n%s", err, out)
	}
	return decoded
}

func TestJsonCharFieldsAreEscaped(t *testing.T) {
	assert := assert.New(t)

	decoded := decodeChars(t, printChars(t, '"', "a\"\\\n", '\\', '"', "x\ty\nz\x01"))

	assert.Equal("\"", decoded["single"])
	assert.Equal([]interface{}{"a", "\"", "\\", "\n"}, decoded["array"])
	assert.Equal(map[string]interface{}{"first": "\\", "last": "\""}, decoded["Pair"])
	assert.Equal("x\ty\nz\x01", decoded["note"])
	assert.Len(decoded, 4)
}

func TestJsonPlainCharFieldsAreUnchanged(t *testing.T) {
	assert := assert.New(t)

	decoded := decodeChars(t, printChars(t, 'S', "abcd", 'f', 'l', "note"))

	assert.Equal("S", decoded["single"])
	assert.Equal([]interface{}{"a", "b", "c", "d"}, decoded["array"])
	assert.Equal(map[string]interface{}{"first": "f", "last": "l"}, decoded["Pair"])
	assert.Equal("note", decoded["note"])
}
