/* Generated SBE (Simple Binary Encoding) message codec */
package composite.elements;

import org.agrona.MutableDirectBuffer;

@SuppressWarnings("all")
public class OuterEncoder
{
    public static final int SCHEMA_ID = 3;
    public static final int SCHEMA_VERSION = 0;
    public static final int ENCODED_LENGTH = 22;
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private int offset;
    private MutableDirectBuffer buffer;

    public OuterEncoder wrap(final MutableDirectBuffer buffer, final int offset)
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

    public static int enumOneEncodingOffset()
    {
        return 0;
    }

    public static int enumOneEncodingLength()
    {
        return 1;
    }

    public OuterEncoder enumOne(final EnumOne value)
    {
        buffer.putByte(offset + 0, (byte)value.value());
        return this;
    }

    public static int zerothEncodingOffset()
    {
        return 1;
    }

    public static int zerothEncodingLength()
    {
        return 1;
    }

    public static short zerothNullValue()
    {
        return (short)255;
    }

    public static short zerothMinValue()
    {
        return (short)0;
    }

    public static short zerothMaxValue()
    {
        return (short)254;
    }

    public OuterEncoder zeroth(final short value)
    {
        buffer.putByte(offset + 1, (byte)value);
        return this;
    }


    public static int setOneEncodingOffset()
    {
        return 2;
    }

    public static int setOneEncodingLength()
    {
        return 4;
    }

    private final SetOneEncoder setOne = new SetOneEncoder();

    /**
     * set as uint32
     *
     * @return SetOneEncoder : set as uint32
     */
    public SetOneEncoder setOne()
    {
        setOne.wrap(buffer, offset + 2);
        return setOne;
    }

    public static int innerEncodingOffset()
    {
        return 6;
    }

    public static int innerEncodingLength()
    {
        return 16;
    }

    private final InnerEncoder inner = new InnerEncoder();

    public InnerEncoder inner()
    {
        inner.wrap(buffer, offset + 6);
        return inner;
    }

    public String toString()
    {
        return appendTo(new StringBuilder(100)).toString();
    }

    public StringBuilder appendTo(final StringBuilder builder)
    {
        OuterDecoder writer = new OuterDecoder();
        writer.wrap(buffer, offset);

        return writer.appendTo(builder);
    }
}
