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

import org.agrona.DirectBuffer;
import uk.co.real_logic.sbe.otf.Types;

import java.util.ArrayList;
import java.util.List;

/**
 * Test-side walk of a {@link MessagePlan} over an encoded message producing the same event record as
 * {@link SpyTokenListener}, so the compiled offsets, sizes and dimension layouts can be compared with what
 * {@link uk.co.real_logic.sbe.otf.OtfMessageDecoder} visits.
 */
final class PlanWalker
{
    private final MessagePlan plan;
    private final DirectBuffer buffer;
    private final int actingVersion;
    private final List<String> events = new ArrayList<>();

    private PlanWalker(final MessagePlan plan, final DirectBuffer buffer, final int actingVersion)
    {
        this.plan = plan;
        this.buffer = buffer;
        this.actingVersion = actingVersion;
    }

    static List<String> walk(
        final MessagePlan plan,
        final DirectBuffer buffer,
        final int bodyOffset,
        final int actingBlockLength,
        final int actingVersion)
    {
        final PlanWalker walker = new PlanWalker(plan, buffer, actingVersion);
        walker.events.add("beginMessage " + plan.name);
        walker.walkScope(plan.rootStart, plan.rootEnd, bodyOffset, actingBlockLength);
        walker.events.add("endMessage");

        return walker.events;
    }

    private int walkScope(final int childStart, final int childEnd, final int entryBase, final int actingBlockLength)
    {
        final FieldPlan[] fields = plan.fields;
        int cursor = entryBase + actingBlockLength;

        for (int i = childStart; i < childEnd; i++)
        {
            final FieldPlan f = fields[i];
            if (f.isBlockField())
            {
                if (f.sinceVersion <= actingVersion)
                {
                    walkBlockField(f, entryBase);
                }
            }
            else if (FieldPlan.KIND_GROUP == f.kind)
            {
                cursor = walkGroup(f, cursor);
            }
            else
            {
                cursor = walkVarData(f, cursor);
            }
        }

        return cursor;
    }

    private void walkBlockField(final FieldPlan f, final int entryBase)
    {
        final int index = entryBase + f.offset;
        switch (f.kind)
        {
            case FieldPlan.KIND_COMPOSITE:
                events.add("beginComposite " + f.name);
                for (int i = f.childStart; i < f.childEnd; i++)
                {
                    final FieldPlan member = plan.fields[i];
                    if (member.sinceVersion <= actingVersion)
                    {
                        walkBlockField(member, entryBase);
                    }
                }
                events.add("endComposite");
                break;

            case FieldPlan.KIND_ENUM:
                events.add("enum " + f.name + "@" + index + " len=" + f.encodedLength);
                break;

            case FieldPlan.KIND_BIT_SET:
                events.add("bitSet " + f.name + "@" + index + " len=" + f.encodedLength);
                break;

            default:
                events.add("encoding " + f.name + "@" + index + " len=" + f.encodedLength);
                break;
        }
    }

    private int walkGroup(final FieldPlan g, final int start)
    {
        int cursor = start;
        if (g.sinceVersion > actingVersion)
        {
            events.add("group " + g.name + " n=0");
            return cursor;
        }

        final int blockLength = Types.getInt(buffer, cursor + g.blockLengthOffset, g.blockLengthType, g.byteOrder);
        final int numInGroup = Types.getInt(buffer, cursor + g.numInGroupOffset, g.numInGroupType, g.byteOrder);
        cursor += g.dimensionSize;
        events.add("group " + g.name + " n=" + numInGroup);

        for (int i = 0; i < numInGroup; i++)
        {
            events.add("beginGroup " + i);
            cursor = walkScope(g.childStart, g.childEnd, cursor, blockLength);
            events.add("endGroup " + i);
        }

        return cursor;
    }

    private int walkVarData(final FieldPlan v, final int start)
    {
        if (v.sinceVersion > actingVersion)
        {
            events.add("varData " + v.name + "@" + start + " len=0");
            return start;
        }

        final int length = Types.getInt(buffer, start + v.lengthOffset, v.lengthType, v.byteOrder);
        final int dataIndex = start + v.dataOffset;
        events.add("varData " + v.name + "@" + dataIndex + " len=" + length);

        return dataIndex + length;
    }
}
