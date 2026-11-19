/*
 * Copyright 2026 Real Logic Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.real_logic.sbe.otf;

import uk.co.real_logic.sbe.ir.Token;

import java.util.List;

import static uk.co.real_logic.sbe.ir.Signal.BEGIN_COMPOSITE;
import static uk.co.real_logic.sbe.ir.Signal.BEGIN_FIELD;

/**
 * Shared computation of the minimum block length an OTF decoder must accept at a given acting version.
 */
public final class OtfBlockLengthUtil
{
    private OtfBlockLengthUtil()
    {
    }

    // End of the bytes a type occupies at the acting version; composite members added later do not count.
    private static int typeEnd(
        final List<Token> tokens, final int typeIdx, final int baseOffset, final int actingVersion)
    {
        final Token typeToken = tokens.get(typeIdx);
        if (typeToken.isConstantEncoding() || typeToken.version() > actingVersion)
        {
            return 0;
        }

        final int offset = baseOffset + typeToken.offset();
        if (BEGIN_COMPOSITE != typeToken.signal())
        {
            return offset + typeToken.encodedLength();
        }

        int end = 0;
        final int endIdx = typeIdx + typeToken.componentTokenCount() - 1;
        for (int i = typeIdx + 1; i < endIdx; )
        {
            end = Math.max(end, typeEnd(tokens, i, offset, actingVersion));
            i += tokens.get(i).componentTokenCount();
        }

        return end;
    }

    /**
     * Compute the smallest block length that holds every non-constant field present at the acting version.
     *
     * @param tokens         of the message or group block.
     * @param beginFieldsIdx index of the first field token in the block.
     * @param numTokens      number of tokens in the message.
     * @param actingVersion  version from the wire header.
     * @return the minimum block length, or zero if no fixed fields occupy the block.
     */
    public static int minBlockLength(
        final List<Token> tokens, final int beginFieldsIdx, final int numTokens, final int actingVersion)
    {
        int minBlockLength = 0;
        for (int i = beginFieldsIdx; i < numTokens; )
        {
            final Token fieldToken = tokens.get(i);
            if (BEGIN_FIELD != fieldToken.signal())
            {
                break;
            }

            // Constant fields occupy no bytes on the wire, so they must not raise the minimum block length.
            if (fieldToken.version() <= actingVersion && !fieldToken.isConstantEncoding())
            {
                minBlockLength = Math.max(minBlockLength, typeEnd(tokens, i + 1, 0, actingVersion));
            }

            i += fieldToken.componentTokenCount();
        }

        return minBlockLength;
    }
}
