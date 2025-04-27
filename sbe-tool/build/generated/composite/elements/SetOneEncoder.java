/* Generated SBE (Simple Binary Encoding) message codec */
package composite.elements;

import org.agrona.MutableDirectBuffer;

/**
 * set as uint32
 */
@SuppressWarnings("all")
public class SetOneEncoder
{
    public static final int SCHEMA_ID = 3;
    public static final int SCHEMA_VERSION = 0;
    public static final int ENCODED_LENGTH = 4;
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private int offset;
    private MutableDirectBuffer buffer;

    public SetOneEncoder wrap(final MutableDirectBuffer buffer, final int offset)
    {
        if (buffer != this.buffer)
        {
            this.buffer = buffer;
        }
        this.offset = offset;

        return this;
    }

    public MutableDirectBuffer buffer()
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

    public SetOneEncoder clear()
    {
        buffer.putInt(offset, (int)0L, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    public SetOneEncoder bit0(final boolean value)
    {
        int bits = buffer.getInt(offset, java.nio.ByteOrder.LITTLE_ENDIAN);
        bits = value ? bits | (1 << 0) : bits & ~(1 << 0);
        buffer.putInt(offset, bits, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    public static int bit0(final int bits, final boolean value)
    {
        return value ? bits | (1 << 0) : bits & ~(1 << 0);
    }

    public SetOneEncoder bit16(final boolean value)
    {
        int bits = buffer.getInt(offset, java.nio.ByteOrder.LITTLE_ENDIAN);
        bits = value ? bits | (1 << 16) : bits & ~(1 << 16);
        buffer.putInt(offset, bits, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    public static int bit16(final int bits, final boolean value)
    {
        return value ? bits | (1 << 16) : bits & ~(1 << 16);
    }

    public SetOneEncoder bit26(final boolean value)
    {
        int bits = buffer.getInt(offset, java.nio.ByteOrder.LITTLE_ENDIAN);
        bits = value ? bits | (1 << 26) : bits & ~(1 << 26);
        buffer.putInt(offset, bits, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }

    public static int bit26(final int bits, final boolean value)
    {
        return value ? bits | (1 << 26) : bits & ~(1 << 26);
    }
}
