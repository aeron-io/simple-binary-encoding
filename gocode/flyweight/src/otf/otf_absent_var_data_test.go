package otf

import (
	"encoding/binary"
	"github.com/real-logic/simple-binary-encoding/otf"
	"github.com/stretchr/testify/assert"
	"testing"
)

//go:generate java --add-opens java.base/jdk.internal.misc=ALL-UNNAMED -Dsbe.output.dir=. -Dsbe.generate.stubs=false -Dsbe.generate.ir=true -jar ../../../${SBE_JAR} ../../../../sbe-tool/src/test/resources/field-order-check-schema.xml

const FieldOrderCheckSchemaFilename = "field-order-check-schema.sbeir"

// AddVarDataV1 gained its var data field b in version 1, so a version 0 message ends after field a.
func TestDecodesOlderVersionMessageEndingBeforeAbsentVarData(t *testing.T) {
	assert := assert.New(t)

	ir := otf.NewIrDecoder()
	assert.GreaterOrEqual(ir.DecodeFile(FieldOrderCheckSchemaFilename), 0)
	tokens := ir.MessageByID(1019)
	assert.NotEmpty(tokens)

	buffer := make([]byte, 4)
	binary.LittleEndian.PutUint32(buffer, 42)

	listener := &TestListener{}
	assert.NotPanics(func() {
		assert.Equal(4, otf.Decode(buffer, 0, 4, tokens, listener))
	})
	assert.Contains(listener.out.String(), "AddVarDataV1.a=42")
}

func TestRejectsMissingLengthOfPresentVarData(t *testing.T) {
	assert := assert.New(t)

	ir := otf.NewIrDecoder()
	assert.GreaterOrEqual(ir.DecodeFile(FieldOrderCheckSchemaFilename), 0)
	tokens := ir.MessageByID(1019)

	buffer := make([]byte, 4)
	assert.PanicsWithValue("length too short for data length field", func() {
		otf.Decode(buffer, 1, 4, tokens, &TestListener{})
	})
}
