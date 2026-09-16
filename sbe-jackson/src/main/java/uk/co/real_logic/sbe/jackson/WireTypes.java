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
import org.agrona.MutableDirectBuffer;
import uk.co.real_logic.sbe.PrimitiveType;

import java.nio.ByteOrder;

/**
 * Integer reads and writes for every SBE primitive type, widened to {@code long}. Unsigned 8/16/32-bit values
 * are zero-extended; uint64 is returned raw and must be interpreted unsigned by the caller. No bounds checks;
 * callers check against the frame first.
 */
final class WireTypes
{
    private WireTypes()
    {
    }

    static long getLong(final DirectBuffer buffer, final int index, final PrimitiveType type, final ByteOrder order)
    {
        switch (type)
        {
            case CHAR:
            case INT8:
                return buffer.getByte(index);

            case UINT8:
                return buffer.getByte(index) & 0xFF;

            case INT16:
                return buffer.getShort(index, order);

            case UINT16:
                return buffer.getShort(index, order) & 0xFFFF;

            case INT32:
                return buffer.getInt(index, order);

            case UINT32:
                return buffer.getInt(index, order) & 0xFFFF_FFFFL;

            case INT64:
            case UINT64:
                return buffer.getLong(index, order);

            default:
                throw new IllegalArgumentException("not an integer type: " + type);
        }
    }

    static void putLong(
        final MutableDirectBuffer buffer,
        final int index,
        final PrimitiveType type,
        final ByteOrder order,
        final long value)
    {
        switch (type)
        {
            case CHAR:
            case INT8:
            case UINT8:
                buffer.putByte(index, (byte)value);
                break;

            case INT16:
            case UINT16:
                buffer.putShort(index, (short)value, order);
                break;

            case INT32:
            case UINT32:
                buffer.putInt(index, (int)value, order);
                break;

            case INT64:
            case UINT64:
                buffer.putLong(index, value, order);
                break;

            default:
                throw new IllegalArgumentException("not an integer type: " + type);
        }
    }

    /**
     * Whether a primitive type is represented with {@code int} precision in JSON trees (fits in a Java int).
     *
     * @param type primitive type.
     * @return true for int8 / int16 / int32 / uint8 / uint16.
     */
    static boolean fitsInt(final PrimitiveType type)
    {
        switch (type)
        {
            case INT8:
            case INT16:
            case INT32:
            case UINT8:
            case UINT16:
                return true;

            default:
                return false;
        }
    }
}
