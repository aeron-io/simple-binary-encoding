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

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CoderResult;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * UTF-16BE under the name {@value #NAME}, registered through {@link SpyCharsetProvider}, whose encoder records
 * the capacity of every output buffer it is handed. Tests use it to prove that the encoder sizes its scratch
 * from the var-data budget rather than from the text length.
 */
final class SpyCharset extends Charset
{
    static final String NAME = "X-SBE-JACKSON-SPY";

    private static final AtomicInteger MAX_OUTPUT_CAPACITY = new AtomicInteger(-1);
    private static final AtomicInteger ENCODE_CALLS = new AtomicInteger();

    SpyCharset()
    {
        super(NAME, new String[0]);
    }

    static void reset()
    {
        MAX_OUTPUT_CAPACITY.set(-1);
        ENCODE_CALLS.set(0);
    }

    /**
     * Largest output buffer any encoder saw since {@link #reset()}, or -1 when no encoder ran.
     *
     * @return the capacity in bytes.
     */
    static int maxOutputCapacity()
    {
        return MAX_OUTPUT_CAPACITY.get();
    }

    static int encodeCalls()
    {
        return ENCODE_CALLS.get();
    }

    public boolean contains(final Charset cs)
    {
        return cs instanceof SpyCharset;
    }

    public CharsetDecoder newDecoder()
    {
        return StandardCharsets.UTF_16BE.newDecoder();
    }

    public CharsetEncoder newEncoder()
    {
        return new SpyEncoder(this);
    }

    private static final class SpyEncoder extends CharsetEncoder
    {
        private final CharsetEncoder inner = StandardCharsets.UTF_16BE.newEncoder();

        SpyEncoder(final Charset charset)
        {
            super(charset, 2.0f, 2.0f, new byte[]{ (byte)0xFF, (byte)0xFD });
        }

        protected CoderResult encodeLoop(final CharBuffer in, final ByteBuffer out)
        {
            ENCODE_CALLS.incrementAndGet();
            MAX_OUTPUT_CAPACITY.accumulateAndGet(out.capacity(), Math::max);

            return inner.encode(in, out, false);
        }

        protected void implReset()
        {
            inner.reset();
        }
    }
}
