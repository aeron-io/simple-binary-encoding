# Changelog

## [1.35.6] - 2025-06-27
### Changed
* **Java:** Bump `Agrona` to [2.2.4](https://github.com/aeron-io/agrona/releases/tag/2.2.4).
* **Java:** Bump `JUnit` to 5.13.2.
* **Java:** Bump `Checkstyle` to 10.26.0.
* **Java:** Bump `Shadow` to 8.3.7.

## [1.35.5] - 2025-06-20
### Changed
* **Java/C++/C#:** Fix a bug in overzealous DTO validation. ([#1073](https://github.com/aeron-io/simple-binary-encoding/pull/1073))
* **Java:** Improve JsonPrinter's conformance with the JSON specification (e.g., escaping control characters and using correct delimiters).
* **Java:** Bump `Agrona` to 2.2.3.

## [1.35.4] - 2025-06-12
### Changed
* **Rust:** Return None for optional primite field if its version is less than actingVersion. ([#1067](https://github.com/aeron-io/simple-binary-encoding/pull/1067))
* **Java:** Adjust Java Dto generateComputeEncodedLength to acount for character encoding. ([#1072](https://github.com/aeron-io/simple-binary-encoding/pull/1072))
* **Java:** Bump `JUnit` to 5.13.1.
* **Java:** Bump `jqwik` to 1.9.3.

## [1.35.3] - 2025-06-06
### Changed
* **Java:** Publish release artifacts to Central Portal using OSSRH Staging API service.
* **Java:** Bump `Agrona` to 2.2.2.
* **Java:** Bump `Checkstyle` to 10.25.0.
* **Java:** Bump `Gradle` to 8.14.2.

## [1.35.1] - 2025-06-02
### Changed
* Bump `Agrona` to 2.2.1.
* Bump `JUnit` to 5.13.0.

## [1.35.0] - 2025-05-26
### Changed
* Update `Implementation-Vendor`.
* **Rust:** Elide explicit ActingVersion lifetime. ([#1053](https://github.com/aeron-io/simple-binary-encoding/pull/1053))
* **CI:** Use `gradle/actions/setup-gradle` action for caching Gradle dependencies.
* **CI:** Enable JDK 24 GA build.
* **CI:** Simplify error log upload.
* **CI:**  Remove Ubuntu 20.04 based builds.
* **CI:** Bump Go build to use 1.23.x and 1.24.x.
* Bump `Agrona` to 2.2.0.
* Bump `Gradle` to 8.14.1.
* Bump `Checkstyle` to 10.24.0.
* Bump `json` to 20250517.
* Bump `JUnit` to 5.12.2.
* Bump `Mockito` to 5.18.0.
* Bump `Shadow` to 8.3.6.
* Bump `Versions` to 0.52.

### Fixed
* **Java:** include field 'deprecated' in ir encoding and decoding.
* **Java:** Adjust Java DTO Choice Set Access. ([#1064](https://github.com/aeron-io/simple-binary-encoding/issues/1064))
* **Rust:** get proper version for composite types. ([#1058](https://github.com/aeron-io/simple-binary-encoding/pull/1058))
* **C#:** Read NuGet package version from version file ([#1061](https://github.com/aeron-io/simple-binary-encoding/pull/1061))
* **C#:** Enable dual build for netstandard2.0 & netstandard2.1. ([#1062](https://github.com/aeron-io/simple-binary-encoding/pull/1062))

## [1.34.1] - 2025-01-13
### Changed
* Bump `json` to 20250107.
* Bump `Mockito` to 5.15.2.

### Added
* **Doc:** Add `--add-opens java.base/jdk.internal.misc=ALL-UNNAMED` to the example execution.
* **Rust:** encoding primitive arrays now supports slice instead of array (issue [#1021](https://github.com/aeron-io/simple-binary-encoding/issues/)). ([#1040](https://github.com/aeron-io/simple-binary-encoding/pull/1040))

### Fixed
* **C++:** check for not wrapped state in `decodeLength()` when precedence checks are enabled. ([#1046](https://github.com/aeron-io/simple-binary-encoding/pull/1046))
* **C++:** use `m_actingBlockLength` in `decodeLength()`. ([#1045](https://github.com/aeron-io/simple-binary-encoding/pull/1045))

## [1.34.0] - 2024-12-17
### Changed
* **C++:** hide the m_codecStatePtr behind ifdefs to avoid overhead when precedence checking is disabled. ([#1036](https://github.com/aeron-io/simple-binary-encoding/pull/1036))
* **C++:** use constexpr to define precedence checks lookup tables.
* **Rust:** Enhance enum supporting fromstr and display and into. ([#1020](https://github.com/aeron-io/simple-binary-encoding/pull/1020))
* **Rust:** codegen of primitive enums now implement 'From' instead of 'Into'. ([#1029](https://github.com/aeron-io/simple-binary-encoding/pull/1029))
* **Java:** Update Checkstyle rules and apply them.
* **Breaking:** Bump `Agrona` to 2.0.0.
  _**Note:** `--add-opens java.base/jdk.internal.misc=ALL-UNNAMED` JVM option must be specified in order to generate codecs or use the generated Java classes. For example:_
  ```shell
  $ java --add-opens java.base/jdk.internal.misc=ALL-UNNAMED -Dsbe.generate.ir=true -Dsbe.target.language=Cpp -Dsbe.target.namespace=sbe -Dsbe.output.dir=include/gen -Dsbe.errorLog=yes -jar sbe-all/build/libs/sbe-all-${SBE_TOOL_VERSION}.jar my-sbe-messages.xml
  ```
* Bump `Gradle` to 8.11.1.
* Bump `Checkstyle` to 10.21.0.
* Bump `ByteBuddy` to 1.15.11.
* Bump `JUnit` to 5.11.4.
* Bump `jqwik` to 1.9.2.

### Added
* **C++:** Integrate std::span support for flyweight API. ([#1038](https://github.com/aeron-io/simple-binary-encoding/pull/1038), [#1027](https://github.com/aeron-io/simple-binary-encoding/pull/1027))
* **Rust:** generate message schema level info in lib.rs. ([#1019](https://github.com/aeron-io/simple-binary-encoding/pull/1019))

### Fixed
* **C++:** Fix field precedence check issue [#1031](https://github.com/aeron-io/simple-binary-encoding/issues/1031). ([#1033](https://github.com/aeron-io/simple-binary-encoding/pull/1033))
* **C++:** respect the package override option for C++ codecs and DTOs. ([#1024](https://github.com/aeron-io/simple-binary-encoding/pull/1024))
* **C#:** respect the package override option for C# codecs and DTOs. ([#1024](https://github.com/aeron-io/simple-binary-encoding/pull/1024))
* **Go:** Fix warning about used args in GolangFlyweightGenerator.
* **Rust:** Updated code generator to resolve Issue [#1028](https://github.com/aeron-io/simple-binary-encoding/issues/1028). ([#1037](https://github.com/aeron-io/simple-binary-encoding/pull/1037))
* **Java:** Prevent collision when field name is 'value'.
* **Java:** Preserve byte order throughout IR transformations.

[1.35.6]: https://github.com/aeron-io/simple-binary-encoding/releases/tag/1.35.6
[1.35.5]: https://github.com/aeron-io/simple-binary-encoding/releases/tag/1.35.5
[1.35.4]: https://github.com/aeron-io/simple-binary-encoding/releases/tag/1.35.4
[1.35.3]: https://github.com/aeron-io/simple-binary-encoding/releases/tag/1.35.3
[1.35.1]: https://github.com/aeron-io/simple-binary-encoding/releases/tag/1.35.1
[1.35.0]: https://github.com/aeron-io/simple-binary-encoding/releases/tag/1.35.0
[1.34.1]: https://github.com/aeron-io/simple-binary-encoding/releases/tag/1.34.1
[1.34.0]: https://github.com/aeron-io/simple-binary-encoding/releases/tag/1.34.0
