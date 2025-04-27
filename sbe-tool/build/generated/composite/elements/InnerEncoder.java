/* Generated SBE (Simple Binary Encoding) message codec */
package composite.elements;

import org.agrona.MutableDirectBuffer;

@SuppressWarnings("all")
public class InnerEncoder
{
    public static final int SCHEMA_ID = 3;
    public static final int SCHEMA_VERSION = 0;
    public static final int ENCODED_LENGTH = 16;
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private int offset;
    private MutableDirectBuffer buffer;

    public InnerEncoder wrap(final MutableDirectBuffer buffer, final int offset)
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

    public static int firstEncodingOffset()
    {
        return 0;
    }

    public static int firstEncodingLength()
    {
        return 8;
    }

    public static long firstNullValue()
    {
        return -9223372036854775808L;
    }

    public static long firstMinValue()
    {
        return -9223372036854775807L;
    }

    public static long firstMaxValue()
    {
        return 9223372036854775807L;
    }

    public InnerEncoder first(final long value)
    {
        buffer.putLong(offset + 0, value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }


    public static int secondEncodingOffset()
    {
        return 8;
    }

    public static int secondEncodingLength()
    {
        return 8;
    }

    public static long secondNullValue()
    {
        return -9223372036854775808L;
    }

    public static long secondMinValue()
    {
        return -9223372036854775807L;
    }

    public static long secondMaxValue()
    {
        return 9223372036854775807L;
    }

    public InnerEncoder second(final long value)
    {
        buffer.putLong(offset + 8, value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }


    public String toString()
    {
        return appendTo(new StringBuilder(100)).toString();
    }

    public StringBuilder appendTo(final StringBuilder builder)
    {
        InnerDecoder writer = new InnerDecoder();
        writer.wrap(buffer, offset);

        return writer.appendTo(builder);
    }
}
