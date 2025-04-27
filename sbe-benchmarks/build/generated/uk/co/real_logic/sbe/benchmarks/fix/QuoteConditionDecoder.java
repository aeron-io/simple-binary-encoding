/* Generated SBE (Simple Binary Encoding) message codec */
package uk.co.real_logic.sbe.benchmarks.fix;

import org.agrona.concurrent.UnsafeBuffer;

@SuppressWarnings("all")
public class QuoteConditionDecoder
{
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 1;
    public static final int ENCODED_LENGTH = 1;
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private int offset;
    private UnsafeBuffer buffer;

    public QuoteConditionDecoder wrap(final UnsafeBuffer buffer, final int offset)
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

    public boolean implied()
    {
        return 0 != (buffer.getByte(offset) & (1 << 0));
    }

    public static boolean implied(final byte value)
    {
        return 0 != (value & (1 << 0));
    }

    public boolean exchangeBest()
    {
        return 0 != (buffer.getByte(offset) & (1 << 1));
    }

    public static boolean exchangeBest(final byte value)
    {
        return 0 != (value & (1 << 1));
    }

    public String toString()
    {
        return appendTo(new StringBuilder(100)).toString();
    }

    public StringBuilder appendTo(final StringBuilder builder)
    {
        builder.append('{');
        boolean atLeastOne = false;
        if (implied())
        {
            if (atLeastOne)
            {
                builder.append(',');
            }
            builder.append("implied");
            atLeastOne = true;
        }
        if (exchangeBest())
        {
            if (atLeastOne)
            {
                builder.append(',');
            }
            builder.append("exchangeBest");
            atLeastOne = true;
        }
        builder.append('}');

        return builder;
    }
}
