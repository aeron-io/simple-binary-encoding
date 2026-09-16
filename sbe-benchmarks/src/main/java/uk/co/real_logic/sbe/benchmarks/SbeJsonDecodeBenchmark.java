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
package uk.co.real_logic.sbe.benchmarks;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class SbeJsonDecodeBenchmark
{
    @Benchmark
    public void decodeCopy(final SbeJsonCarState state, final Blackhole blackhole)
    {
        blackhole.consume(state.decoder.decodeCopy(state.input, 0, state.length));
    }

    @Benchmark
    public void jsonPrinterReadTree(final SbeJsonCarState state, final Blackhole blackhole) throws IOException
    {
        state.printed.setLength(0);
        state.printer.print(state.printed, state.input, 0);
        blackhole.consume(state.mapper.readTree(state.printed.toString()));
    }

    @Benchmark
    public void naiveOtfTree(final SbeJsonCarState state, final Blackhole blackhole)
    {
        blackhole.consume(state.decodeOtf());
    }

    // Add decodeInto(BorrowedDocument) and writeJson(JsonGenerator) benchmarks here when those APIs land.
    // Keep the document/generator in SbeJsonCarState and provision/warm them in its trial setup.
}
