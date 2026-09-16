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

/**
 * Classification of a failure raised as an {@link SbeJsonException}.
 */
public enum ErrorCode
{
    /**
     * The header template id (or a name passed to {@link SbeJson#newEncoder(String)}) does not identify a message
     * in the IR, or the header schema id differs from the IR's schema id.
     */
    UNKNOWN_TEMPLATE,

    /**
     * The message acting version is newer than the schema version the codec was built from.
     */
    UNSUPPORTED_VERSION,

    /**
     * A read would extend past {@code offset + length} of the supplied frame.
     */
    FRAME_OVERFLOW,

    /**
     * A field present in the acting version does not fit inside the acting block length of its scope.
     */
    FIELD_OUTSIDE_BLOCK,

    /**
     * A {@link Limits} budget was exceeded.
     */
    LIMIT_EXCEEDED,

    /**
     * A required property is missing or {@code null} on encode.
     */
    MISSING_REQUIRED,

    /**
     * A JSON node has the wrong shape for the SBE field, e.g. a floating point number into an integer field.
     */
    TYPE_MISMATCH,

    /**
     * A value is outside the range permitted by the schema: numeric min / max, dimension type range, string or
     * array length, or a bit set mask wider than its encoding type.
     */
    OUT_OF_RANGE,

    /**
     * An enum name or raw value is not a valid value of the enum.
     */
    UNKNOWN_ENUM,

    /**
     * A bit set choice name is not declared by the set.
     */
    UNKNOWN_CHOICE,

    /**
     * A JSON property does not correspond to any field in the acting layout and
     * {@link UnknownProperties#ERROR} is in force.
     */
    UNKNOWN_PROPERTY,

    /**
     * A constant field was supplied with a value different from the schema constant.
     */
    CONSTANT_MISMATCH,

    /**
     * On the streaming encode path a group or var-data section arrived out of schema order.
     */
    SECTION_OUT_OF_ORDER,

    /**
     * The destination buffer region is too small for the encoded message.
     */
    DESTINATION_OVERFLOW
}
