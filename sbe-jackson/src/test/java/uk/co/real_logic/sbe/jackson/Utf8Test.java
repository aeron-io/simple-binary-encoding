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

import org.agrona.concurrent.UnsafeBuffer;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Utf8Test
{
    @Test
    void encodedLengthMatchesTheJdkForMixedWidthText()
    {
        final String text = "aé中🚗z";
        assertEquals(text.getBytes(StandardCharsets.UTF_8).length, Utf8.encodedLength(text));
    }

    @Test
    void loneSurrogateLengthMatchesReplacementCharacterEncoding()
    {
        final String text = "aé中🚗\uDC00z";
        final byte[] expected = "aé中🚗\uFFFDz".getBytes(StandardCharsets.UTF_8);
        final UnsafeBuffer buffer = TestMessages.newBuffer(expected.length);
        assertEquals(expected.length, Utf8.encodedLength(text));
        assertEquals(expected.length, Utf8.encode(text, buffer, 0));
        assertArrayEquals(expected, buffer.byteArray());
    }

    @Test
    void encodedLengthDoesNotWrapWhenTheEncodingExceedsIntRange()
    {
        // Just enough three-byte characters for the byte count to pass Integer.MAX_VALUE.
        final int chars = Integer.MAX_VALUE / 3 + 2;
        final CharSequence huge = new CharSequence()
        {
            public int length()
            {
                return chars;
            }

            public char charAt(final int index)
            {
                return '中';
            }

            public CharSequence subSequence(final int start, final int end)
            {
                throw new UnsupportedOperationException();
            }
        };

        final long length = Utf8.encodedLength(huge);
        assertEquals(3L * chars, length);
        assertTrue(length > Integer.MAX_VALUE);
    }
}
