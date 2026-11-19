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
package uk.co.real_logic.sbe.generation.common;

import uk.co.real_logic.sbe.ir.Signal;
import uk.co.real_logic.sbe.ir.Token;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Shared computation of the smallest block length a generated Java decoder must accept at a given acting version.
 */
public final class BlockLengthUtil
{
    private BlockLengthUtil()
    {
    }

    // A composite member added in a later version than its field only counts from that version on, since a block
    // encoded by an older version ends before it.
    private static void addEndBySinceVersion(
        final List<Token> tokens,
        final int typeIndex,
        final int sinceVersion,
        final int baseOffset,
        final TreeMap<Integer, Integer> endBySinceVersion)
    {
        final Token typeToken = tokens.get(typeIndex);
        if (typeToken.isConstantEncoding())
        {
            return;
        }

        final int version = Math.max(sinceVersion, typeToken.version());
        final int offset = baseOffset + typeToken.offset();
        if (Signal.BEGIN_COMPOSITE == typeToken.signal())
        {
            final int endIndex = typeIndex + typeToken.componentTokenCount() - 1;
            for (int i = typeIndex + 1; i < endIndex; )
            {
                addEndBySinceVersion(tokens, i, version, offset, endBySinceVersion);
                i += tokens.get(i).componentTokenCount();
            }
        }
        else
        {
            endBySinceVersion.merge(version, offset + typeToken.encodedLength(), Math::max);
        }
    }

    /**
     * Generate an expression for the smallest block length that holds every non-constant field present at the acting
     * version, or null when the block has no such fields.
     *
     * @param fieldTokens   of the message or group block.
     * @param actingVersion expression evaluating to the acting version in the generated source.
     * @return the generated expression, or null if no fixed fields occupy the block.
     */
    public static String minBlockLengthExpression(final List<Token> fieldTokens, final String actingVersion)
    {
        final TreeMap<Integer, Integer> endBySinceVersion = new TreeMap<>();
        for (int i = 0, size = fieldTokens.size(); i < size; )
        {
            final Token fieldToken = fieldTokens.get(i);
            if (Signal.BEGIN_FIELD != fieldToken.signal())
            {
                i++;
                continue;
            }

            // Constant fields occupy no bytes on the wire, so they must not raise the minimum block length.
            if (!fieldToken.isConstantEncoding())
            {
                addEndBySinceVersion(fieldTokens, i + 1, fieldToken.version(), 0, endBySinceVersion);
            }

            i += fieldToken.componentTokenCount();
        }

        if (endBySinceVersion.isEmpty())
        {
            return null;
        }

        // A field added in a later version must not lower the minimum set by an earlier one.
        int runningEnd = 0;
        final TreeMap<Integer, Integer> minByVersion = new TreeMap<>();
        for (final Map.Entry<Integer, Integer> entry : endBySinceVersion.entrySet())
        {
            runningEnd = Math.max(runningEnd, entry.getValue());
            minByVersion.put(entry.getKey(), runningEnd);
        }

        final StringBuilder expression = new StringBuilder();
        String baseValue = "0";
        int closing = 0;
        for (final Map.Entry<Integer, Integer> entry : minByVersion.descendingMap().entrySet())
        {
            if (entry.getKey() <= 0)
            {
                baseValue = Integer.toString(entry.getValue());
                break;
            }

            expression.append('(').append(actingVersion).append(" >= ").append(entry.getKey())
                .append(" ? ").append(entry.getValue()).append(" : ");
            closing++;
        }

        expression.append(baseValue);
        for (int i = 0; i < closing; i++)
        {
            expression.append(')');
        }

        return expression.toString();
    }
}
