/* Generated SBE (Simple Binary Encoding) message codec */
package extension;

import org.agrona.MutableDirectBuffer;
import org.agrona.sbe.*;

@SuppressWarnings("all")
public class BoosterEncoder implements CompositeEncoderFlyweight
{
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 1;
    public static final int ENCODED_LENGTH = 2;
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private int offset;
    private MutableDirectBuffer buffer;

    public BoosterEncoder wrap(final MutableDirectBuffer buffer, final int offset)
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

    public static int boostTypeEncodingOffset()
    {
        return 0;
    }

    public static int boostTypeEncodingLength()
    {
        return 1;
    }

    public BoosterEncoder boostType(final BoostType value)
    {
        buffer.putByte(offset + 0, value.value());
        return this;
    }

    public static int horsePowerEncodingOffset()
    {
        return 1;
    }

    public static int horsePowerEncodingLength()
    {
        return 1;
    }

    public static short horsePowerNullValue()
    {
        return (short)255;
    }

    public static short horsePowerMinValue()
    {
        return (short)0;
    }

    public static short horsePowerMaxValue()
    {
        return (short)254;
    }

    public BoosterEncoder horsePower(final short value)
    {
        buffer.putByte(offset + 1, (byte)value);
        return this;
    }


    public String toString()
    {
        return appendTo(new StringBuilder(100)).toString();
    }

    public StringBuilder appendTo(final StringBuilder builder)
    {
        BoosterDecoder writer = new BoosterDecoder();
        writer.wrap(buffer, offset);

        return writer.appendTo(builder);
    }
}
