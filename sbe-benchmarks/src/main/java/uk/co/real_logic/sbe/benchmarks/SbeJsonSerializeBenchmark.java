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
public class SbeJsonSerializeBenchmark
{
    @Benchmark
    public void writeValueAsBytes(final SbeJsonCarState state, final Blackhole blackhole) throws IOException
    {
        blackhole.consume(state.mapper.writeValueAsBytes(state.tree));
    }

    @Benchmark
    public void writeValueToReusableStream(final SbeJsonCarState state, final Blackhole blackhole) throws IOException
    {
        state.jsonOutput.reset();
        state.mapper.writeValue(state.jsonOutput, state.tree);
        blackhole.consume(state.jsonOutput.size());
        blackhole.consume(state.jsonOutput);
    }

    // The tree is decoded once at setup. Add the direct writeJson path alongside these baselines later.
}
