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

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
public class SbeJsonEncodeBenchmark
{
    @Benchmark
    public void encode(final SbeJsonCarState state, final Blackhole blackhole)
    {
        blackhole.consume(state.encoder.encode(state.tree, state.output, 0, state.output.capacity()));
        blackhole.consume(state.output);
    }

    @Benchmark
    public void encodedLengthAndEncode(final SbeJsonCarState state, final Blackhole blackhole)
    {
        final int length = state.encoder.encodedLength(state.tree);
        blackhole.consume(state.encoder.encode(state.tree, state.output, 0, length));
        blackhole.consume(state.output);
    }

    @Benchmark
    public void generatedFromTree(final SbeJsonCarState state, final Blackhole blackhole)
    {
        blackhole.consume(state.encodeGenerated(state.output));
        blackhole.consume(state.output);
    }
}
