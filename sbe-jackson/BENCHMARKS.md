# SbeJson benchmark harness

These JMH benchmarks implement the harness portion of DESIGN.md §13 step 7. They make no
performance or zero-allocation claims. Generated codecs still come from the existing
`sbe-benchmarks/src/main/resources/car.xml` Gradle generation task.

Build and list from the repository root:

```sh
./gradlew :sbe-benchmarks:build :sbe-jackson:check
java -jar sbe-benchmarks/build/libs/sbe-benchmarks.jar -l
```

Run each axis separately (JMH filters match the fully qualified class names):

```sh
java --add-opens=java.base/jdk.internal.misc=ALL-UNNAMED -jar sbe-benchmarks/build/libs/sbe-benchmarks.jar '.*SbeJsonDecodeBenchmark.*' -f 3 -wi 5 -i 10 -prof gc
java --add-opens=java.base/jdk.internal.misc=ALL-UNNAMED -jar sbe-benchmarks/build/libs/sbe-benchmarks.jar '.*SbeJsonEncodeBenchmark.*' -f 3 -wi 5 -i 10 -prof gc
java --add-opens=java.base/jdk.internal.misc=ALL-UNNAMED -jar sbe-benchmarks/build/libs/sbe-benchmarks.jar '.*SbeJsonSerializeBenchmark.*' -f 3 -wi 5 -i 10 -prof gc
```

For a short wiring/correctness smoke run of every method and corpus:

```sh
java --add-opens=java.base/jdk.internal.misc=ALL-UNNAMED -jar sbe-benchmarks/build/libs/sbe-benchmarks.jar '.*SbeJson.*Benchmark.*' -f 1 -wi 1 -i 1 -w 1s -r 1s -t 1 -prof gc -jvmArgsAppend '-Xms256m -Xmx256m' -foe true
```

Use `-p corpus=BASELINE` or `-p corpus=HEAVY` to select data; otherwise both run. Full runs should
use an idle machine and record JVM, CPU, heap, GC and flags. The fixed smoke heap keeps the smoke
run modest; choose and record an appropriate heap for real runs. Do not infer rankings from smoke
results or compare measurements collected under different conditions.

| Axis | Methods | Measured work |
| --- | --- | --- |
| Decode | `decodeCopy`, `jsonPrinterReadTree`, `naiveOtfTree` | Wire to a fresh stock Jackson tree; printer includes text production and parsing. |
| Encode | `encode`, `encodedLengthAndEncode`, `generatedFromTree` | The same predecoded tree to a reused SBE destination, including header. Sizing variant measures both passes. |
| Serialize | `writeValueAsBytes`, `writeValueToReusableStream` | The predecoded stock tree to JSON; decode is excluded. |

Every method uses `SbeJsonCarState` (`Scope.Thread`), average nanoseconds per operation, and a
`Blackhole`. Trial setup parses IR, builds SbeJson/decoder/encoder/ObjectMapper, prepares direct
input/output buffers, warms printer/serializer buffers, and verifies the adapters. The state holds
the generated CarEncoder and header, the reusable OTF listener, a StringBuilder, and a 64 KiB
ByteArrayOutputStream. There is no invocation-level setup or harness buffer allocation.

BASELINE is exactly `CarBenchmark.encode`: 3 fuel entries, 2 performance entries, 3 acceleration
entries per performance entry, and the original short strings. HEAVY uses the same schema with
32 fuel entries, 8 performance entries, 16 acceleration entries each, and Latin-1 manufacturer/model
strings of 4096/4098 bytes. Speeds exceed the small integer cache. This is a bounded stress corpus,
not an extension-version schema or a schema-maximum corpus: uint16 maximum nested counts would
exceed the default total group budget and dominate routine runs. Empty groups, version transitions,
uint64 high-bit values, binary data and hostile inputs remain future corpus work.

Trial setup checks exact equality of the naive OTF and decodeCopy trees, and byte equality of
both encoders against the generated corpus. JsonPrinter output is re-encoded and compared after
normalizing its extras object outside the measured method. Both serialization outputs are parsed
and checked outside measurement. A failed check aborts the trial.

Interpretation and limitations:

- `gc.alloc.rate.norm` is allocated bytes per operation (B/op), not live/retained memory and not
  allocation throughput (`gc.alloc.rate`, MB/s). Stock-tree decoding and byte-array serialization
  allocate by design. Tiny nonzero normalized values can include measurement overhead.
- C2 escape analysis can eliminate allocations whose results do not escape. Blackholes help keep
  results observable but do not establish a zero-allocation guarantee. Cross-check real runs with
  `-prof jfr` allocation events and the `sbe-jackson` ThreadMXBean allocation guard tests described
  in DESIGN.md §12 when the borrowed-path branch supplies them (they are absent on this harness's
  base). JFR events are sampled, so an absence of events is not proof. A separate run with
  `-jvmArgsAppend '-XX:-DoEscapeAnalysis'` is diagnostic, not a replacement production result.
- JsonPrinter emits extras as named booleans; decodeCopy and the naive listener emit a numeric
  mask. readTree also uses different numeric node types (notably DoubleNode versus FloatNode).
  Outside this corpus, unsigned uint64, unknown enums, absent fields, binary and NaN differ too;
  see DESIGN.md §9. Printer results include pretty text, a String copy, and a second parse.
- The naive listener is deliberately limited to this Car corpus. It allocates fresh stock nodes
  and text buffers, walks enum tokens on each call, and does not implement SbeJson validation,
  bounds/limit checks or general optional/version semantics.
- The generated encoder walks the same tree on every invocation, including enum name resolution,
  string access, nested groups and var-data. It uses generated String setters (including their
  charset conversion allocations), skips wire-absent constants, and does not perform SbeJson's
  full validation. It is a schema-specific baseline, not an equivalent validation engine.
- `writeValueAsBytes` always returns a new byte array and has no OutputStream overload.
  `writeValueToReusableStream` instead calls `ObjectMapper.writeValue` after resetting the warmed
  stream; it consumes the stream and size without `toByteArray()`. Both include Jackson generator
  creation/closing and float formatting; neither includes tree construction.

When borrowed APIs land, add a document/generator to the shared state and provision them in trial
setup, then add `decodeInto(BorrowedDocument)` and `writeJson(JsonGenerator)` methods at the marked
slots. Specify whether generator lifecycle/flush is measured, make bytes observable, and report
`retainedBytes()` alongside allocation. The existing state and corpus need no restructuring.
No implementation tuning is part of this scaffold.
