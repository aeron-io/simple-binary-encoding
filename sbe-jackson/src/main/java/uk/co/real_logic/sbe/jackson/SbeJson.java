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

import org.agrona.collections.Int2ObjectHashMap;
import uk.co.real_logic.sbe.ir.Ir;
import uk.co.real_logic.sbe.ir.Token;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Compiled, immutable, shareable codec for one schema: the {@link Ir} compiled once into a {@link MessagePlan}
 * per message plus the JSON conventions chosen at build time. Hands out thread-confined
 * {@link SbeJsonDecoder}s and {@link SbeJsonEncoder}s.
 *
 * <pre>{@code
 * final SbeJson sbeJson = SbeJson.builder(ir).build();
 * final SbeJsonDecoder decoder = sbeJson.newDecoder();          // one per thread
 * final ObjectNode tree = decoder.decodeCopy(buffer, offset, length);
 * final SbeJsonEncoder encoder = sbeJson.newEncoder("Car");     // one per thread, bound to a template
 * final int written = encoder.encode(tree, dst, 0, dst.capacity());
 * }</pre>
 */
public final class SbeJson
{
    private final Ir ir;
    private final EnumStyle enumStyle;
    private final BitSetStyle bitSetStyle;
    private final CharArrayStyle charArrayStyle;
    private final UnknownProperties unknownProperties;
    private final NewerVersions newerVersions;
    private final boolean exceptionStackTraces;
    private final Limits limits;
    private final HeaderLayout headerLayout;
    private final Int2ObjectHashMap<MessageCodec> codecsById = new Int2ObjectHashMap<>();
    private final Map<String, MessageCodec> codecsByName = new HashMap<>();

    private SbeJson(final Builder builder)
    {
        ir = builder.ir;
        enumStyle = builder.enumStyle;
        bitSetStyle = builder.bitSetStyle;
        charArrayStyle = builder.charArrayStyle;
        unknownProperties = builder.unknownProperties;
        newerVersions = builder.newerVersions;
        exceptionStackTraces = builder.exceptionStackTraces;
        limits = builder.limits;
        headerLayout = new HeaderLayout(ir.headerStructure());

        for (final List<Token> tokens : ir.messages())
        {
            final MessagePlan plan = PlanCompiler.compile(ir, tokens);
            if (plan.maxGroupDepth > limits.maxDepth())
            {
                throw new SbeJsonException(
                    ErrorCode.LIMIT_EXCEEDED, plan.templateId, SbeJsonException.NO_OFFSET, plan.name,
                    "schema group nesting depth " + plan.maxGroupDepth + " exceeds maxDepth " + limits.maxDepth(),
                    exceptionStackTraces);
            }
            final MessageCodec codec = new PlanMessageCodec(plan, new JacksonCaches(plan, enumStyle), this);
            codecsById.put(plan.templateId, codec);
            codecsByName.put(plan.name, codec);
        }
    }

    /**
     * Start building a codec for a schema.
     *
     * @param ir the IR of the schema, from {@code IrDecoder} or {@code IrGenerator}.
     * @return a builder with default policies.
     */
    public static Builder builder(final Ir ir)
    {
        return new Builder(Objects.requireNonNull(ir, "ir"));
    }

    /**
     * Create a thread-confined decoder for any message of the schema.
     *
     * @return a new decoder.
     */
    public SbeJsonDecoder newDecoder()
    {
        return new SbeJsonDecoder(this);
    }

    /**
     * Debug decoder whose borrowed documents detect use after the next decode. Arrives with the borrowed-document
     * path (DESIGN.md section 13 step 5).
     *
     * @return never returns in this release.
     * @throws UnsupportedOperationException always, until step 5 lands.
     */
    public SbeJsonDecoder newCheckedDecoder()
    {
        // TODO(DESIGN.md section 13 step 5): fresh skeleton per decode with poisoning of the previous tree.
        throw new UnsupportedOperationException("newCheckedDecoder arrives with the borrowed-document path");
    }

    /**
     * Create a thread-confined encoder bound to a message template by name.
     *
     * @param messageName message name from the schema.
     * @return a new encoder.
     * @throws SbeJsonException with {@link ErrorCode#UNKNOWN_TEMPLATE} when the name is not a message.
     */
    public SbeJsonEncoder newEncoder(final String messageName)
    {
        final MessageCodec codec = codecsByName.get(messageName);
        if (null == codec)
        {
            throw new SbeJsonException(
                ErrorCode.UNKNOWN_TEMPLATE, SbeJsonException.NO_TEMPLATE_ID, SbeJsonException.NO_OFFSET, null,
                "no message named '" + messageName + "' in schema id " + ir.id(), exceptionStackTraces);
        }

        return new SbeJsonEncoder(this, codec);
    }

    /**
     * Create a thread-confined encoder bound to a message template by id.
     *
     * @param templateId template id from the schema.
     * @return a new encoder.
     * @throws SbeJsonException with {@link ErrorCode#UNKNOWN_TEMPLATE} when the id is not a message.
     */
    public SbeJsonEncoder newEncoder(final int templateId)
    {
        return new SbeJsonEncoder(this, codecForTemplate(templateId));
    }

    /**
     * The IR this codec was compiled from.
     *
     * @return the IR.
     */
    public Ir ir()
    {
        return ir;
    }

    /**
     * Enum representation policy.
     *
     * @return the enum style.
     */
    public EnumStyle enumStyle()
    {
        return enumStyle;
    }

    /**
     * Bit set representation policy.
     *
     * @return the bit set style.
     */
    public BitSetStyle bitSetStyle()
    {
        return bitSetStyle;
    }

    /**
     * Fixed-length char array decoding policy.
     *
     * @return the char array style.
     */
    public CharArrayStyle charArrayStyle()
    {
        return charArrayStyle;
    }

    /**
     * Unknown JSON property policy on encode.
     *
     * @return the unknown property policy.
     */
    public UnknownProperties unknownProperties()
    {
        return unknownProperties;
    }

    /**
     * Policy for messages newer than the schema.
     *
     * @return the newer version policy.
     */
    public NewerVersions newerVersions()
    {
        return newerVersions;
    }

    /**
     * Whether thrown {@link SbeJsonException}s capture a stack trace.
     *
     * @return true when stack traces are writable.
     */
    public boolean exceptionStackTraces()
    {
        return exceptionStackTraces;
    }

    /**
     * Resource budgets enforced per message.
     *
     * @return the limits.
     */
    public Limits limits()
    {
        return limits;
    }

    HeaderLayout headerLayout()
    {
        return headerLayout;
    }

    MessageCodec codecForTemplate(final int templateId)
    {
        final MessageCodec codec = codecsById.get(templateId);
        if (null == codec)
        {
            throw new SbeJsonException(
                ErrorCode.UNKNOWN_TEMPLATE, templateId, SbeJsonException.NO_OFFSET, null,
                "no message with template id " + templateId + " in schema id " + ir.id(), exceptionStackTraces);
        }

        return codec;
    }

    /**
     * Builder for {@link SbeJson}. Defaults: {@link EnumStyle#NAME}, {@link BitSetStyle#MASK},
     * {@link CharArrayStyle#NUL_TERMINATED}, {@link UnknownProperties#ERROR}, {@link NewerVersions#REJECT},
     * writable stack traces, {@link Limits#defaults()}.
     */
    public static final class Builder
    {
        private final Ir ir;
        private EnumStyle enumStyle = EnumStyle.NAME;
        private BitSetStyle bitSetStyle = BitSetStyle.MASK;
        private CharArrayStyle charArrayStyle = CharArrayStyle.NUL_TERMINATED;
        private UnknownProperties unknownProperties = UnknownProperties.ERROR;
        private NewerVersions newerVersions = NewerVersions.REJECT;
        private boolean exceptionStackTraces = true;
        private Limits limits = Limits.defaults();

        Builder(final Ir ir)
        {
            this.ir = ir;
        }

        /**
         * Set the enum representation.
         *
         * @param enumStyle policy.
         * @return this for a fluent API.
         */
        public Builder enumStyle(final EnumStyle enumStyle)
        {
            this.enumStyle = Objects.requireNonNull(enumStyle);
            return this;
        }

        /**
         * Set the bit set representation.
         *
         * @param bitSetStyle policy.
         * @return this for a fluent API.
         */
        public Builder bitSetStyle(final BitSetStyle bitSetStyle)
        {
            this.bitSetStyle = Objects.requireNonNull(bitSetStyle);
            return this;
        }

        /**
         * Set the fixed-length char array decoding.
         *
         * @param charArrayStyle policy.
         * @return this for a fluent API.
         */
        public Builder charArrayStyle(final CharArrayStyle charArrayStyle)
        {
            this.charArrayStyle = Objects.requireNonNull(charArrayStyle);
            return this;
        }

        /**
         * Set the treatment of unknown JSON properties on encode.
         *
         * @param unknownProperties policy.
         * @return this for a fluent API.
         */
        public Builder unknownProperties(final UnknownProperties unknownProperties)
        {
            this.unknownProperties = Objects.requireNonNull(unknownProperties);
            return this;
        }

        /**
         * Set the treatment of messages newer than the schema.
         *
         * @param newerVersions policy.
         * @return this for a fluent API.
         */
        public Builder newerVersions(final NewerVersions newerVersions)
        {
            this.newerVersions = Objects.requireNonNull(newerVersions);
            return this;
        }

        /**
         * Whether thrown {@link SbeJsonException}s capture a stack trace. Set false for gateways that reject
         * hostile input routinely.
         *
         * @param exceptionStackTraces true to capture stack traces.
         * @return this for a fluent API.
         */
        public Builder exceptionStackTraces(final boolean exceptionStackTraces)
        {
            this.exceptionStackTraces = exceptionStackTraces;
            return this;
        }

        /**
         * Set the resource budgets.
         *
         * @param limits budgets.
         * @return this for a fluent API.
         */
        public Builder limits(final Limits limits)
        {
            this.limits = Objects.requireNonNull(limits);
            return this;
        }

        /**
         * Compile the plans and build the immutable codec.
         *
         * @return the codec.
         * @throws SbeJsonException with {@link ErrorCode#LIMIT_EXCEEDED} when a message nests groups deeper than
         *                          {@link Limits#maxDepth()}.
         */
        public SbeJson build()
        {
            return new SbeJson(this);
        }
    }
}
