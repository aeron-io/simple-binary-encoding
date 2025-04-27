/* Generated SBE (Simple Binary Encoding) message codec */
package uk.co.real_logic.sbe.benchmarks;

import org.agrona.concurrent.UnsafeBuffer;

/**
 * Repeating group dimensions
 */
@SuppressWarnings("all")
public class GroupSizeEncodingEncoder
{
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 1;
    public static final int ENCODED_LENGTH = 4;
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private int offset;
    private UnsafeBuffer buffer;

    public GroupSizeEncodingEncoder wrap(final UnsafeBuffer buffer, final int offset)
    {
        if (buffer != this.buffer)
        {
            this.buffer = buffer;
        }
        this.offset = offset;

        return this;
    }

    public UnsafeBuffer buffer()
    {
        return buffer;
    }

    public int offset()
    {
        return offset;
    }

    public int encodedLength()
    {
        return ENCODED_LENGTH;
    }

    public int sbeSchemaId()
    {
        return SCHEMA_ID;
    }

    public int sbeSchemaVersion()
    {
        return SCHEMA_VERSION;
    }

    public static int blockLengthEncodingOffset()
    {
        return 0;
    }

    public static int blockLengthEncodingLength()
    {
        return 2;
    }

    public static int blockLengthNullValue()
    {
        return 65535;
    }

    public static int blockLengthMinValue()
    {
        return 0;
    }

    public static int blockLengthMaxValue()
    {
        return 65534;
    }

    public GroupSizeEncodingEncoder blockLength(final int value)
    {
        buffer.putShort(offset + 0, (short)value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }


    public static int numInGroupEncodingOffset()
    {
        return 2;
    }

    public static int numInGroupEncodingLength()
    {
        return 2;
    }

    public static int numInGroupNullValue()
    {
        return 65535;
    }

    public static int numInGroupMinValue()
    {
        return 0;
    }

    public static int numInGroupMaxValue()
    {
        return 65534;
    }

    public GroupSizeEncodingEncoder numInGroup(final int value)
    {
        buffer.putShort(offset + 2, (short)value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }


    public String toString()
    {
        return appendTo(new StringBuilder(100)).toString();
    }

    public StringBuilder appendTo(final StringBuilder builder)
    {
        GroupSizeEncodingDecoder writer = new GroupSizeEncodingDecoder();
        writer.wrap(buffer, offset);

        return writer.appendTo(builder);
    }
}
