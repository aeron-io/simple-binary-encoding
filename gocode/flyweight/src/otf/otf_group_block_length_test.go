package otf

import (
	"encoding/binary"
	"github.com/real-logic/simple-binary-encoding/otf"
	"github.com/real-logic/simple-binary-encoding/otf/test/code_generation_test"
	"github.com/stretchr/testify/assert"
	"testing"
)

//go:generate java --add-opens java.base/jdk.internal.misc=ALL-UNNAMED -Dsbe.output.dir=. -Dsbe.target.language=golang -Dsbe.go.generate.generate.flyweights=true -Dsbe.generate.ir=true -Dsbe.keyword.append.token=X -jar ../../../${SBE_JAR} ../../../../sbe-tool/src/test/resources/otf-group-block-length-schema.xml

const BlockLengthSchemaFilename = "otf-group-block-length-schema.sbeir"

func TestRejectsGroupBlockLengthBelowFields(t *testing.T) {
	assert := assert.New(t)

	ir := otf.NewIrDecoder()
	assert.GreaterOrEqual(ir.DecodeFile(SchemaFilename), 0)

	var car code_generation_test.Car
	buffer := make([]byte, MsgBufferCapacity)
	encodeCar(&car, buffer)

	headerDecoder, _ := otf.NewOtfHeaderDecoder(ir.Header())
	templateId, _ := headerDecoder.TemplateId(buffer)
	actingVersion, _ := headerDecoder.SchemaVersion(buffer)
	blockLength, _ := headerDecoder.BlockLength(buffer)
	headerLength := int(headerDecoder.EncodedLength())
	tokens := ir.MessageByID(int32(templateId))

	// The fuelFigures group header follows the message block; its blockLength is the first uint16.
	binary.LittleEndian.PutUint16(buffer[headerLength+int(blockLength):], 0)

	assert.PanicsWithValue(
		"group blockLength=0 is less than the fields present at actingVersion=0 for group=fuelFigures",
		func() {
			otf.Decode(buffer[headerLength:], actingVersion, blockLength, tokens, &TestListener{})
		})
}

func decodeQuotes(t *testing.T, actingVersion uint64, groupBlockLength uint16, numInGroup uint16) (out string) {
	ir := otf.NewIrDecoder()
	if ir.DecodeFile(BlockLengthSchemaFilename) < 0 {
		t.Fatalf("Error decoding %s", BlockLengthSchemaFilename)
	}

	buffer := make([]byte, 4+int(numInGroup)*16)
	binary.LittleEndian.PutUint16(buffer[0:], groupBlockLength)
	binary.LittleEndian.PutUint16(buffer[2:], numInGroup)

	listener := &TestListener{}
	otf.Decode(buffer, actingVersion, 0, ir.MessageByID(1), listener)
	return listener.out.String()
}

func TestGroupBlockLengthExcludesMembersNewerThanActingVersion(t *testing.T) {
	assert := assert.New(t)

	// A version 0 entry holds only the 8 byte mantissa: side was added to the composite in version 1.
	assert.NotPanics(func() { decodeQuotes(t, 0, 8, 2) })
	assert.PanicsWithValue(
		"group blockLength=7 is less than the fields present at actingVersion=0 for group=entries",
		func() { decodeQuotes(t, 0, 7, 2) })

	// From version 1 the entry also holds side, and the constant field adds no bytes.
	assert.NotPanics(func() { decodeQuotes(t, 1, 9, 2) })
	assert.PanicsWithValue(
		"group blockLength=8 is less than the fields present at actingVersion=1 for group=entries",
		func() { decodeQuotes(t, 1, 8, 2) })

	// An empty group consumes nothing, whatever its blockLength.
	assert.NotPanics(func() { decodeQuotes(t, 1, 0, 0) })
}
