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
 * Resource budgets enforced while decoding or encoding a single message. Exceeding any of them raises
 * {@link SbeJsonException} with {@link ErrorCode#LIMIT_EXCEEDED}.
 * <p>
 * Group counts and var-data lengths on the wire are attacker controlled; the schema's dimension and length
 * types bound each individually (65535 entries per group, 4 GiB per var-data field) but nested groups multiply,
 * so the budgets here are totals per message.
 */
public final class Limits
{
    /**
     * Default total number of group entries across all groups and nesting levels of one message.
     */
    public static final int DEFAULT_MAX_GROUP_ENTRIES = 100_000;

    /**
     * Default total number of var-data payload bytes across all var-data fields of one message.
     */
    public static final int DEFAULT_MAX_VAR_DATA_BYTES = 16 << 20;

    /**
     * Default maximum group nesting depth. The root block is depth 0; a top level group is depth 1.
     */
    public static final int DEFAULT_MAX_DEPTH = 16;

    /**
     * Default budget for bytes retained per borrowed document (skeleton trees, pools, scratch arrays).
     */
    public static final long DEFAULT_MAX_RETAINED_BYTES = 64L << 20;

    private static final Limits DEFAULTS = builder().build();

    private final int maxGroupEntries;
    private final int maxVarDataBytes;
    private final int maxDepth;
    private final long maxRetainedBytes;

    private Limits(final Builder builder)
    {
        maxGroupEntries = builder.maxGroupEntries;
        maxVarDataBytes = builder.maxVarDataBytes;
        maxDepth = builder.maxDepth;
        maxRetainedBytes = builder.maxRetainedBytes;
    }

    /**
     * Limits with all default values.
     *
     * @return the default limits.
     */
    public static Limits defaults()
    {
        return DEFAULTS;
    }

    /**
     * Create a new builder initialised with the default values.
     *
     * @return a new builder.
     */
    public static Builder builder()
    {
        return new Builder();
    }

    /**
     * Total number of group entries permitted across all groups and nesting levels of one message.
     *
     * @return total number of group entries permitted per message.
     */
    public int maxGroupEntries()
    {
        return maxGroupEntries;
    }

    /**
     * Total number of var-data payload bytes permitted across all var-data fields of one message.
     *
     * @return total var-data bytes permitted per message.
     */
    public int maxVarDataBytes()
    {
        return maxVarDataBytes;
    }

    /**
     * Maximum group nesting depth. The root block is depth 0.
     *
     * @return maximum group nesting depth.
     */
    public int maxDepth()
    {
        return maxDepth;
    }

    /**
     * Budget for bytes retained per borrowed document.
     *
     * @return retained byte budget per document.
     */
    public long maxRetainedBytes()
    {
        return maxRetainedBytes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "Limits{" +
            "maxGroupEntries=" + maxGroupEntries +
            ", maxVarDataBytes=" + maxVarDataBytes +
            ", maxDepth=" + maxDepth +
            ", maxRetainedBytes=" + maxRetainedBytes +
            '}';
    }

    /**
     * Builder for {@link Limits}.
     */
    public static final class Builder
    {
        private int maxGroupEntries = DEFAULT_MAX_GROUP_ENTRIES;
        private int maxVarDataBytes = DEFAULT_MAX_VAR_DATA_BYTES;
        private int maxDepth = DEFAULT_MAX_DEPTH;
        private long maxRetainedBytes = DEFAULT_MAX_RETAINED_BYTES;

        Builder()
        {
        }

        /**
         * Set the total number of group entries permitted per message.
         *
         * @param maxGroupEntries total across all groups and nesting levels; must be non-negative.
         * @return this for a fluent API.
         */
        public Builder maxGroupEntries(final int maxGroupEntries)
        {
            this.maxGroupEntries = requireNonNegative(maxGroupEntries, "maxGroupEntries");
            return this;
        }

        /**
         * Set the total number of var-data payload bytes permitted per message.
         *
         * @param maxVarDataBytes total across all var-data fields; must be non-negative.
         * @return this for a fluent API.
         */
        public Builder maxVarDataBytes(final int maxVarDataBytes)
        {
            this.maxVarDataBytes = requireNonNegative(maxVarDataBytes, "maxVarDataBytes");
            return this;
        }

        /**
         * Set the maximum group nesting depth.
         *
         * @param maxDepth maximum depth; the root block is depth 0; must be non-negative.
         * @return this for a fluent API.
         */
        public Builder maxDepth(final int maxDepth)
        {
            this.maxDepth = requireNonNegative(maxDepth, "maxDepth");
            return this;
        }

        /**
         * Set the budget for bytes retained per borrowed document.
         *
         * @param maxRetainedBytes retained byte budget; must be non-negative.
         * @return this for a fluent API.
         */
        public Builder maxRetainedBytes(final long maxRetainedBytes)
        {
            if (maxRetainedBytes < 0)
            {
                throw new IllegalArgumentException("maxRetainedBytes must be non-negative: " + maxRetainedBytes);
            }
            this.maxRetainedBytes = maxRetainedBytes;
            return this;
        }

        /**
         * Build the immutable {@link Limits}.
         *
         * @return the limits.
         */
        public Limits build()
        {
            return new Limits(this);
        }

        private static int requireNonNegative(final int value, final String name)
        {
            if (value < 0)
            {
                throw new IllegalArgumentException(name + " must be non-negative: " + value);
            }

            return value;
        }
    }
}
