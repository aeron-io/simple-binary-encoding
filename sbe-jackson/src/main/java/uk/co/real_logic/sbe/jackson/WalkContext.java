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

import com.fasterxml.jackson.databind.node.JsonNodeFactory;

import java.util.Arrays;

/**
 * Per-decoder / per-encoder scratch state for one plan walk: the field path (for error messages), running
 * {@link Limits} counters and the node factory. Thread-confined with its owner; reset at the start of every walk.
 */
final class WalkContext
{
    private static final int INITIAL_DEPTH = 16;

    final JsonNodeFactory factory;
    final boolean writableStackTraces;

    /**
     * Tree encoder retained across calls by the owning thread-confined encoder; created lazily by the codec.
     */
    PlanTreeEncoder treeEncoder;

    private int[] pathFields = new int[INITIAL_DEPTH];
    private int[] pathElements = new int[INITIAL_DEPTH];
    private int depth;
    private long groupEntries;
    private long varDataBytes;

    WalkContext(final JsonNodeFactory factory, final boolean writableStackTraces)
    {
        this.factory = factory;
        this.writableStackTraces = writableStackTraces;
    }

    void reset()
    {
        depth = 0;
        groupEntries = 0;
        varDataBytes = 0;
    }

    int depth()
    {
        return depth;
    }

    void push(final int fieldIndex)
    {
        if (depth == pathFields.length)
        {
            pathFields = Arrays.copyOf(pathFields, depth * 2);
            pathElements = Arrays.copyOf(pathElements, depth * 2);
        }
        pathFields[depth] = fieldIndex;
        pathElements[depth] = -1;
        depth++;
    }

    void element(final int index)
    {
        pathElements[depth - 1] = index;
    }

    void pop()
    {
        depth--;
    }

    long addGroupEntries(final long count)
    {
        groupEntries += count;
        return groupEntries;
    }

    long addVarDataBytes(final long count)
    {
        varDataBytes += count;
        return varDataBytes;
    }

    long varDataBytes()
    {
        return varDataBytes;
    }

    /**
     * Format the current path plus an optional leaf as {@code Message.group[3].composite.field}.
     *
     * @param plan message being walked.
     * @param leaf the failing field, or {@code null} when the failure is at the current container.
     * @return the dotted path.
     */
    String path(final MessagePlan plan, final FieldPlan leaf)
    {
        final StringBuilder sb = new StringBuilder(64);
        sb.append(plan.name);
        for (int i = 0; i < depth; i++)
        {
            sb.append('.').append(plan.fields[pathFields[i]].name);
            if (pathElements[i] >= 0)
            {
                sb.append('[').append(pathElements[i]).append(']');
            }
        }
        if (null != leaf)
        {
            sb.append('.').append(leaf.name);
        }

        return sb.toString();
    }

    SbeJsonException error(
        final MessagePlan plan, final ErrorCode code, final FieldPlan leaf, final int byteOffset, final String detail)
    {
        return new SbeJsonException(
            code, plan.templateId, byteOffset, path(plan, leaf), detail, writableStackTraces);
    }
}
