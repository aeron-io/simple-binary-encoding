package otf

import (
	"encoding/binary"
	"github.com/real-logic/simple-binary-encoding/otf/test/flyweight_group_block_length"
	"github.com/stretchr/testify/assert"
	"testing"
)

//go:generate java --add-opens java.base/jdk.internal.misc=ALL-UNNAMED -Dsbe.output.dir=. -Dsbe.target.language=golang -Dsbe.go.generate.generate.flyweights=true -Dsbe.generate.ir=true -Dsbe.keyword.append.token=X -jar ../../../${SBE_JAR} ../../../../sbe-tool/src/test/resources/flyweight-group-block-length-schema.xml

const groupBlockLengthMessage = "group blockLength is less than the fields present at the acting version [E108]"

// wrapQuotes wraps a Quotes message whose group header carries the given blockLength and count.
func wrapQuotes(actingVersion uint64, blockLength uint16, count uint16) *flyweight_group_block_length.Quotes {
	buffer := make([]byte, 8+4+int(count)*16)
	binary.LittleEndian.PutUint16(buffer[8:], blockLength)
	binary.LittleEndian.PutUint16(buffer[10:], count)

	var quotes flyweight_group_block_length.Quotes
	return quotes.WrapForDecode(buffer, 8, 0, actingVersion, uint64(len(buffer)))
}

func TestGroupBlockLengthMustCoverFieldsAtActingVersion(t *testing.T) {
	assert := assert.New(t)

	// A version 0 entry holds only the 8 byte mantissa: side was added to the composite in version 1.
	assert.NotPanics(func() { wrapQuotes(0, 8, 2).Entries() })
	assert.PanicsWithValue(groupBlockLengthMessage, func() { wrapQuotes(0, 7, 2).Entries() })
	assert.PanicsWithValue(groupBlockLengthMessage, func() { wrapQuotes(0, 0, 3).Entries() })

	// From version 1 the entry also holds side, and the constant field adds no bytes.
	assert.NotPanics(func() { wrapQuotes(1, 9, 2).Entries() })
	assert.PanicsWithValue(groupBlockLengthMessage, func() { wrapQuotes(1, 8, 2).Entries() })

	// An empty group consumes nothing, whatever its blockLength.
	assert.NotPanics(func() { wrapQuotes(1, 0, 0).Entries() })
}
