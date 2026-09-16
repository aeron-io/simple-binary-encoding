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

import org.agrona.MutableDirectBuffer;

/**
 * Hand-rolled UTF-8 encoding of a {@link CharSequence} straight into a buffer, without an intermediate
 * {@code byte[]}. Surrogate pairs become four-byte sequences; an unpaired surrogate is malformed input, reported
 * by {@link #encodedLength} (-1) and refused by {@link #encode}, never replaced. {@link #encodedLength} and
 * {@link #encode} agree byte for byte. No Jackson import.
 */
final class Utf8
{
    private Utf8()
    {
    }

    /**
     * Number of bytes {@link #encode} will write for a sequence, or -1 when the sequence contains an unpaired
     * surrogate and so cannot be encoded.
     *
     * Returned as a {@code long} so that a sequence whose encoding exceeds {@code Integer.MAX_VALUE} bytes is
     * reported rather than wrapped.
     *
     * @param chars source characters.
     * @return encoded byte count, or -1 for malformed input.
     */
    static long encodedLength(final CharSequence chars)
    {
        final int length = chars.length();
        long bytes = 0;
        for (int i = 0; i < length; i++)
        {
            final char c = chars.charAt(i);
            if (c < 0x80)
            {
                bytes++;
            }
            else if (c < 0x800)
            {
                bytes += 2;
            }
            else if (Character.isSurrogate(c))
            {
                if (!Character.isHighSurrogate(c) || i + 1 >= length || !Character.isLowSurrogate(chars.charAt(i + 1)))
                {
                    return -1;
                }
                bytes += 4;
                i++;
            }
            else
            {
                bytes += 3;
            }
        }

        return bytes;
    }

    /**
     * Encode a sequence into a buffer. The caller has already checked that {@link #encodedLength} bytes fit,
     * which also establishes that the sequence is well-formed.
     *
     * @param chars  source characters.
     * @param buffer destination.
     * @param index  where the first byte goes.
     * @return bytes written.
     * @throws IllegalArgumentException on an unpaired surrogate; {@link #encodedLength} reports those first.
     */
    static int encode(final CharSequence chars, final MutableDirectBuffer buffer, final int index)
    {
        final int length = chars.length();
        int pos = index;
        for (int i = 0; i < length; i++)
        {
            final char c = chars.charAt(i);
            if (c < 0x80)
            {
                buffer.putByte(pos++, (byte)c);
            }
            else if (c < 0x800)
            {
                buffer.putByte(pos++, (byte)(0xC0 | (c >> 6)));
                buffer.putByte(pos++, (byte)(0x80 | (c & 0x3F)));
            }
            else if (Character.isHighSurrogate(c) && i + 1 < length && Character.isLowSurrogate(chars.charAt(i + 1)))
            {
                final int codePoint = Character.toCodePoint(c, chars.charAt(++i));
                buffer.putByte(pos++, (byte)(0xF0 | (codePoint >> 18)));
                buffer.putByte(pos++, (byte)(0x80 | ((codePoint >> 12) & 0x3F)));
                buffer.putByte(pos++, (byte)(0x80 | ((codePoint >> 6) & 0x3F)));
                buffer.putByte(pos++, (byte)(0x80 | (codePoint & 0x3F)));
            }
            else if (Character.isSurrogate(c))
            {
                throw new IllegalArgumentException("unpaired surrogate U+" + Integer.toHexString(c) + " at " + i);
            }
            else
            {
                buffer.putByte(pos++, (byte)(0xE0 | (c >> 12)));
                buffer.putByte(pos++, (byte)(0x80 | ((c >> 6) & 0x3F)));
                buffer.putByte(pos++, (byte)(0x80 | (c & 0x3F)));
            }
        }

        return pos - index;
    }

    /**
     * Whether every character is 7-bit ASCII.
     *
     * @param chars source characters.
     * @return true when all characters are below 0x80.
     */
    static boolean isAscii(final CharSequence chars)
    {
        for (int i = 0, length = chars.length(); i < length; i++)
        {
            if (chars.charAt(i) >= 0x80)
            {
                return false;
            }
        }

        return true;
    }
}
