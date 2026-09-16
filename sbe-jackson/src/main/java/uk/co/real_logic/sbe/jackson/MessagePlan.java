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

import java.util.Arrays;

/**
 * Compiled layout of one message template: a flat {@link FieldPlan} array in decode order with the root child
 * range, plus the effective-layout table (sorted distinct {@code sinceVersion} thresholds). Immutable and shared
 * between threads. Carries no Jackson types.
 */
final class MessagePlan
{
    final int templateId;
    final String name;
    final int blockLength;
    final int schemaId;
    final int schemaVersion;
    final FieldPlan[] fields;
    final int rootStart;
    final int rootEnd;
    final int[] versionThresholds;
    final int maxGroupDepth;

    MessagePlan(
        final int templateId,
        final String name,
        final int blockLength,
        final int schemaId,
        final int schemaVersion,
        final FieldPlan[] fields,
        final int rootStart,
        final int rootEnd,
        final int[] versionThresholds,
        final int maxGroupDepth)
    {
        this.templateId = templateId;
        this.name = name.intern();
        this.blockLength = blockLength;
        this.schemaId = schemaId;
        this.schemaVersion = schemaVersion;
        this.fields = fields;
        this.rootStart = rootStart;
        this.rootEnd = rootEnd;
        this.versionThresholds = versionThresholds;
        this.maxGroupDepth = maxGroupDepth;
    }

    /**
     * Index of the effective layout for an acting version: the number of thresholds less than or equal to the
     * version. Two acting versions that admit the same set of fields share a layout index.
     *
     * @param actingVersion version from the message header.
     * @return layout index in {@code [0, layoutCount())}.
     */
    int layoutIndex(final int actingVersion)
    {
        final int[] thresholds = versionThresholds;
        int low = 0;
        int high = thresholds.length;
        while (low < high)
        {
            final int mid = (low + high) >>> 1;
            if (thresholds[mid] <= actingVersion)
            {
                low = mid + 1;
            }
            else
            {
                high = mid;
            }
        }

        return low;
    }

    /**
     * Number of distinct effective layouts: one more than the number of thresholds.
     *
     * @return the bound on layout indices.
     */
    int layoutCount()
    {
        return versionThresholds.length + 1;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {
        return "MessagePlan{" +
            "templateId=" + templateId +
            ", name='" + name + '\'' +
            ", blockLength=" + blockLength +
            ", schemaId=" + schemaId +
            ", schemaVersion=" + schemaVersion +
            ", fields=" + fields.length +
            ", rootStart=" + rootStart +
            ", rootEnd=" + rootEnd +
            ", versionThresholds=" + Arrays.toString(versionThresholds) +
            ", maxGroupDepth=" + maxGroupDepth +
            '}';
    }
}
