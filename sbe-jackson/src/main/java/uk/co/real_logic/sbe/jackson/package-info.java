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
/**
 * Conversion of SBE messages to and from Jackson 2.x {@code JsonNode} trees driven by
 * {@link uk.co.real_logic.sbe.ir.Ir} loaded at runtime.
 * <p>
 * Entry point is {@link uk.co.real_logic.sbe.jackson.SbeJson}, which compiles the IR once into flat plans and
 * hands out thread-confined {@link uk.co.real_logic.sbe.jackson.SbeJsonDecoder} and
 * {@link uk.co.real_logic.sbe.jackson.SbeJsonEncoder} instances.
 */
package uk.co.real_logic.sbe.jackson;
