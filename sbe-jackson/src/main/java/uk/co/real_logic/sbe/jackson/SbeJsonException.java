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
 * Unchecked exception raised for every validation or framing failure in this module.
 * <p>
 * Carries the {@link ErrorCode}, the template id of the message being processed (or {@link #NO_TEMPLATE_ID}
 * when the header could not be read), the byte offset into the buffer at which the failure was detected (or
 * {@link #NO_OFFSET} when not applicable, e.g. a JSON shape error) and the dotted field path, e.g.
 * {@code Car.performanceFigures[1].acceleration[2].seconds}. The message is formatted at throw time.
 * <p>
 * When {@code SbeJson.Builder#exceptionStackTraces(false)} is set the stack trace is not writable, which makes
 * the exception cheap enough for gateways that reject hostile input routinely.
 */
public final class SbeJsonException extends RuntimeException
{
    /**
     * Template id value when the header could not be read.
     */
    public static final int NO_TEMPLATE_ID = -1;

    /**
     * Byte offset value when no buffer position applies.
     */
    public static final int NO_OFFSET = -1;

    private static final long serialVersionUID = 4130712906152366229L;

    private final ErrorCode code;
    private final int templateId;
    private final int byteOffset;
    private final String path;

    /**
     * Create an exception with a writable stack trace.
     *
     * @param code       error classification.
     * @param templateId template id of the message, or {@link #NO_TEMPLATE_ID}.
     * @param byteOffset buffer offset of the failure, or {@link #NO_OFFSET}.
     * @param path       dotted field path, or {@code null} when not applicable.
     * @param detail     human readable detail appended to the message.
     */
    public SbeJsonException(
        final ErrorCode code,
        final int templateId,
        final int byteOffset,
        final String path,
        final String detail)
    {
        this(code, templateId, byteOffset, path, detail, true);
    }

    /**
     * Create an exception, optionally without a writable stack trace.
     *
     * @param code               error classification.
     * @param templateId         template id of the message, or {@link #NO_TEMPLATE_ID}.
     * @param byteOffset         buffer offset of the failure, or {@link #NO_OFFSET}.
     * @param path               dotted field path, or {@code null} when not applicable.
     * @param detail             human readable detail appended to the message.
     * @param writableStackTrace whether the stack trace should be captured.
     */
    public SbeJsonException(
        final ErrorCode code,
        final int templateId,
        final int byteOffset,
        final String path,
        final String detail,
        final boolean writableStackTrace)
    {
        super(formatMessage(code, templateId, byteOffset, path, detail), null, false, writableStackTrace);
        this.code = code;
        this.templateId = templateId;
        this.byteOffset = byteOffset;
        this.path = path;
    }

    /**
     * Error classification.
     *
     * @return the error code.
     */
    public ErrorCode code()
    {
        return code;
    }

    /**
     * Template id of the message being processed.
     *
     * @return the template id, or {@link #NO_TEMPLATE_ID}.
     */
    public int templateId()
    {
        return templateId;
    }

    /**
     * Byte offset into the buffer at which the failure was detected.
     *
     * @return the byte offset, or {@link #NO_OFFSET}.
     */
    public int byteOffset()
    {
        return byteOffset;
    }

    /**
     * Dotted field path of the failing field.
     *
     * @return the path, or {@code null} when not applicable.
     */
    public String path()
    {
        return path;
    }

    private static String formatMessage(
        final ErrorCode code,
        final int templateId,
        final int byteOffset,
        final String path,
        final String detail)
    {
        final StringBuilder sb = new StringBuilder(96);
        sb.append(code);
        if (NO_TEMPLATE_ID != templateId)
        {
            sb.append(" templateId=").append(templateId);
        }
        if (null != path)
        {
            sb.append(" path=").append(path);
        }
        if (NO_OFFSET != byteOffset)
        {
            sb.append(" offset=").append(byteOffset);
        }
        if (null != detail)
        {
            sb.append(": ").append(detail);
        }

        return sb.toString();
    }
}
