/* Generated SBE (Simple Binary Encoding) message codec */
package composite.elements;

import org.agrona.DirectBuffer;

/**
 * set as uint32
 */
@SuppressWarnings("all")
public class SetOneDecoder
{
    public static final int SCHEMA_ID = 3;
    public static final int SCHEMA_VERSION = 0;
    public static final int ENCODED_LENGTH = 4;
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private int offset;
    private DirectBuffer buffer;

    public SetOneDecoder wrap(final DirectBuffer buffer, final int offset)
    {
        if (buffer != this.buffer)
        {
            this.buffer = buffer;
        }
        this.offset = offset;

        return this;
    }

    public DirectBuffer buffer()
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

    public boolean isEmpty()
    {
        return 0 == buffer.getInt(offset);
    }

    public boolean bit0()
    {
        return 0 != (buffer.getInt(offset, java.nio.ByteOrder.LITTLE_ENDIAN) & (1 << 0));
    }

    public static boolean bit0(final int value)
    {
        return 0 != (value & (1 << 0));
    }

    public boolean bit16()
    {
        return 0 != (buffer.getInt(offset, java.nio.ByteOrder.LITTLE_ENDIAN) & (1 << 16));
    }

    public static boolean bit16(final int value)
    {
        return 0 != (value & (1 << 16));
    }

    public boolean bit26()
    {
        return 0 != (buffer.getInt(offset, java.nio.ByteOrder.LITTLE_ENDIAN) & (1 << 26));
    }

    public static boolean bit26(final int value)
    {
        return 0 != (value & (1 << 26));
    }

    public String toString()
    {
        return appendTo(new StringBuilder(100)).toString();
    }

    public StringBuilder appendTo(final StringBuilder builder)
    {
        builder.append('{');
        boolean atLeastOne = false;
        if (bit0())
        {
            if (atLeastOne)
            {
                builder.append(',');
            }
            builder.append("bit0");
            atLeastOne = true;
        }
        if (bit16())
        {
            if (atLeastOne)
            {
                builder.append(',');
            }
            builder.append("bit16");
            atLeastOne = true;
        }
        if (bit26())
        {
            if (atLeastOne)
            {
                builder.append(',');
            }
            builder.append("bit26");
            atLeastOne = true;
        }
        builder.append('}');

        return builder;
    }
}
