/*
 * Copyright 2013-2025 Real Logic Limited.
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
package uk.co.real_logic.sbe.jackson;

import org.agrona.DirectBuffer;
import uk.co.real_logic.sbe.ir.Token;
import uk.co.real_logic.sbe.otf.TokenListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Records what {@link uk.co.real_logic.sbe.otf.OtfMessageDecoder} visits: property names (resolved the way
 * {@code JsonTokenListener} resolves them), absolute buffer indices, encoded lengths, group counts and var-data
 * lengths. Fields not present in the acting version are skipped so the record describes the effective layout.
 */
final class SpyTokenListener implements TokenListener
{
    private final List<String> events = new ArrayList<>();
    private final int actingVersion;
    private int compositeLevel;

    SpyTokenListener(final int actingVersion)
    {
        this.actingVersion = actingVersion;
    }

    List<String> events()
    {
        return events;
    }

    public void onBeginMessage(final Token token)
    {
        events.add("beginMessage " + token.name());
    }

    public void onEndMessage(final Token token)
    {
        events.add("endMessage");
    }

    public void onEncoding(
        final Token fieldToken,
        final DirectBuffer buffer,
        final int bufferIndex,
        final Token typeToken,
        final int actingVersion)
    {
        if (fieldToken.version() > this.actingVersion)
        {
            return;
        }
        final String name = compositeLevel > 0 ? typeToken.name() : fieldToken.name();
        events.add("encoding " + name + "@" + bufferIndex + " len=" + typeToken.encodedLength());
    }

    public void onEnum(
        final Token fieldToken,
        final DirectBuffer buffer,
        final int bufferIndex,
        final List<Token> tokens,
        final int fromIndex,
        final int toIndex,
        final int actingVersion)
    {
        if (fieldToken.version() > this.actingVersion)
        {
            return;
        }
        final String name = compositeLevel > 0 ? tokens.get(fromIndex).name() : fieldToken.name();
        events.add("enum " + name + "@" + bufferIndex + " len=" + tokens.get(fromIndex).encodedLength());
    }

    public void onBitSet(
        final Token fieldToken,
        final DirectBuffer buffer,
        final int bufferIndex,
        final List<Token> tokens,
        final int fromIndex,
        final int toIndex,
        final int actingVersion)
    {
        if (fieldToken.version() > this.actingVersion)
        {
            return;
        }
        final String name = compositeLevel > 0 ? tokens.get(fromIndex).name() : fieldToken.name();
        events.add("bitSet " + name + "@" + bufferIndex + " len=" + tokens.get(fromIndex).encodedLength());
    }

    public void onBeginComposite(
        final Token fieldToken, final List<Token> tokens, final int fromIndex, final int toIndex)
    {
        ++compositeLevel;
        if (fieldToken.version() > this.actingVersion)
        {
            return;
        }
        final String name = compositeLevel > 1 ? tokens.get(fromIndex).name() : fieldToken.name();
        events.add("beginComposite " + name);
    }

    public void onEndComposite(
        final Token fieldToken, final List<Token> tokens, final int fromIndex, final int toIndex)
    {
        --compositeLevel;
        if (fieldToken.version() > this.actingVersion)
        {
            return;
        }
        events.add("endComposite");
    }

    public void onGroupHeader(final Token token, final int numInGroup)
    {
        events.add("group " + token.name() + " n=" + numInGroup);
    }

    public void onBeginGroup(final Token token, final int groupIndex, final int numInGroup)
    {
        events.add("beginGroup " + groupIndex);
    }

    public void onEndGroup(final Token token, final int groupIndex, final int numInGroup)
    {
        events.add("endGroup " + groupIndex);
    }

    public void onVarData(
        final Token fieldToken,
        final DirectBuffer buffer,
        final int bufferIndex,
        final int length,
        final Token typeToken)
    {
        events.add("varData " + fieldToken.name() + "@" + bufferIndex + " len=" + length);
    }
}
