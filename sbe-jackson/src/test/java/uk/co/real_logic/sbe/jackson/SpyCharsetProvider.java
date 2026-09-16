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

import java.nio.charset.Charset;
import java.nio.charset.spi.CharsetProvider;
import java.util.Collections;
import java.util.Iterator;

/**
 * Registers {@link SpyCharset} through {@code META-INF/services} so that {@link Charset#forName} resolves it
 * from a schema {@code characterEncoding}.
 */
public final class SpyCharsetProvider extends CharsetProvider
{
    private static final Charset SPY = new SpyCharset();

    /**
     * Required by {@link java.util.ServiceLoader}.
     */
    public SpyCharsetProvider()
    {
    }

    public Iterator<Charset> charsets()
    {
        return Collections.singletonList(SPY).iterator();
    }

    public Charset charsetForName(final String charsetName)
    {
        return SpyCharset.NAME.equalsIgnoreCase(charsetName) ? SPY : null;
    }
}
