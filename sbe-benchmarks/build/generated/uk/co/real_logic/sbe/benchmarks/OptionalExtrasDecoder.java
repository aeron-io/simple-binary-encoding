/* Generated SBE (Simple Binary Encoding) message codec */
package uk.co.real_logic.sbe.benchmarks;

import org.agrona.concurrent.UnsafeBuffer;

@SuppressWarnings("all")
public class OptionalExtrasDecoder
{
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 1;
    public static final int ENCODED_LENGTH = 1;
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private int offset;
    private UnsafeBuffer buffer;

    public OptionalExtrasDecoder wrap(final UnsafeBuffer buffer, final int offset)
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

    public boolean isEmpty()
    {
        return 0 == buffer.getByte(offset);
    }

    public boolean sunRoof()
    {
        return 0 != (buffer.getByte(offset) & (1 << 0));
    }

    public static boolean sunRoof(final byte value)
    {
        return 0 != (value & (1 << 0));
    }

    public boolean sportsPack()
    {
        return 0 != (buffer.getByte(offset) & (1 << 1));
    }

    public static boolean sportsPack(final byte value)
    {
        return 0 != (value & (1 << 1));
    }

    public boolean cruiseControl()
    {
        return 0 != (buffer.getByte(offset) & (1 << 2));
    }

    public static boolean cruiseControl(final byte value)
    {
        return 0 != (value & (1 << 2));
    }

    public String toString()
    {
        return appendTo(new StringBuilder(100)).toString();
    }

    public StringBuilder appendTo(final StringBuilder builder)
    {
        builder.append('{');
        boolean atLeastOne = false;
        if (sunRoof())
        {
            if (atLeastOne)
            {
                builder.append(',');
            }
            builder.append("sunRoof");
            atLeastOne = true;
        }
        if (sportsPack())
        {
            if (atLeastOne)
            {
                builder.append(',');
            }
            builder.append("sportsPack");
            atLeastOne = true;
        }
        if (cruiseControl())
        {
            if (atLeastOne)
            {
                builder.append(',');
            }
            builder.append("cruiseControl");
            atLeastOne = true;
        }
        builder.append('}');

        return builder;
    }
}
