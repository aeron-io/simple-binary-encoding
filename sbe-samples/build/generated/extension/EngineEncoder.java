/* Generated SBE (Simple Binary Encoding) message codec */
package extension;

import org.agrona.MutableDirectBuffer;
import org.agrona.sbe.*;

@SuppressWarnings("all")
public class EngineEncoder implements CompositeEncoderFlyweight
{
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 1;
    public static final int ENCODED_LENGTH = 10;
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private int offset;
    private MutableDirectBuffer buffer;

    public EngineEncoder wrap(final MutableDirectBuffer buffer, final int offset)
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

    public static int capacityEncodingOffset()
    {
        return 0;
    }

    public static int capacityEncodingLength()
    {
        return 2;
    }

    public static int capacityNullValue()
    {
        return 65535;
    }

    public static int capacityMinValue()
    {
        return 0;
    }

    public static int capacityMaxValue()
    {
        return 65534;
    }

    public EngineEncoder capacity(final int value)
    {
        buffer.putShort(offset + 0, (short)value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }


    public static int numCylindersEncodingOffset()
    {
        return 2;
    }

    public static int numCylindersEncodingLength()
    {
        return 1;
    }

    public static short numCylindersNullValue()
    {
        return (short)255;
    }

    public static short numCylindersMinValue()
    {
        return (short)0;
    }

    public static short numCylindersMaxValue()
    {
        return (short)254;
    }

    public EngineEncoder numCylinders(final short value)
    {
        buffer.putByte(offset + 2, (byte)value);
        return this;
    }


    public static int maxRpmEncodingOffset()
    {
        return 3;
    }

    public static int maxRpmEncodingLength()
    {
        return 0;
    }

    public static int maxRpmNullValue()
    {
        return 65535;
    }

    public static int maxRpmMinValue()
    {
        return 0;
    }

    public static int maxRpmMaxValue()
    {
        return 65534;
    }

    public int maxRpm()
    {
        return 9000;
    }

    public static int manufacturerCodeEncodingOffset()
    {
        return 3;
    }

    public static int manufacturerCodeEncodingLength()
    {
        return 3;
    }

    public static byte manufacturerCodeNullValue()
    {
        return (byte)0;
    }

    public static byte manufacturerCodeMinValue()
    {
        return (byte)32;
    }

    public static byte manufacturerCodeMaxValue()
    {
        return (byte)126;
    }

    public static int manufacturerCodeLength()
    {
        return 3;
    }

    public EngineEncoder manufacturerCode(final int index, final byte value)
    {
        if (index < 0 || index >= 3)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 3 + (index * 1);
        buffer.putByte(pos, value);

        return this;
    }

    public static String manufacturerCodeCharacterEncoding()
    {
        return "US-ASCII";
    }

    public EngineEncoder putManufacturerCode(final byte[] src, final int srcOffset)
    {
        final int length = 3;
        if (srcOffset < 0 || srcOffset > (src.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
        }

        buffer.putBytes(this.offset + 3, src, srcOffset, length);

        return this;
    }

    public EngineEncoder manufacturerCode(final String src)
    {
        final int length = 3;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("String too large for copy: byte length=" + srcLength);
        }

        buffer.putStringWithoutLengthAscii(this.offset + 3, src);

        for (int start = srcLength; start < length; ++start)
        {
            buffer.putByte(this.offset + 3 + start, (byte)0);
        }

        return this;
    }

    public EngineEncoder manufacturerCode(final CharSequence src)
    {
        final int length = 3;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("CharSequence too large for copy: byte length=" + srcLength);
        }

        for (int i = 0; i < srcLength; ++i)
        {
            final char charValue = src.charAt(i);
            final byte byteValue = charValue > 127 ? (byte)'?' : (byte)charValue;
            buffer.putByte(this.offset + 3 + i, byteValue);
        }

        for (int i = srcLength; i < length; ++i)
        {
            buffer.putByte(this.offset + 3 + i, (byte)0);
        }

        return this;
    }

    public static int fuelEncodingOffset()
    {
        return 6;
    }

    public static int fuelEncodingLength()
    {
        return 0;
    }

    public static byte fuelNullValue()
    {
        return (byte)0;
    }

    public static byte fuelMinValue()
    {
        return (byte)32;
    }

    public static byte fuelMaxValue()
    {
        return (byte)126;
    }

    private static final byte[] FUEL_VALUE = { 80, 101, 116, 114, 111, 108 };

    public static int fuelLength()
    {
        return 6;
    }

    public byte fuel(final int index)
    {
        return FUEL_VALUE[index];
    }

    public int getFuel(final byte[] dst, final int offset, final int length)
    {
        final int bytesCopied = Math.min(length, 6);
        System.arraycopy(FUEL_VALUE, 0, dst, offset, bytesCopied);

        return bytesCopied;
    }

    public String fuel()
    {
        return "Petrol";
    }


    public static int efficiencyEncodingOffset()
    {
        return 6;
    }

    public static int efficiencyEncodingLength()
    {
        return 1;
    }

    public static byte efficiencyNullValue()
    {
        return (byte)-128;
    }

    public static byte efficiencyMinValue()
    {
        return (byte)0;
    }

    public static byte efficiencyMaxValue()
    {
        return (byte)100;
    }

    public EngineEncoder efficiency(final byte value)
    {
        buffer.putByte(offset + 6, value);
        return this;
    }


    public static int boosterEnabledEncodingOffset()
    {
        return 7;
    }

    public static int boosterEnabledEncodingLength()
    {
        return 1;
    }

    public EngineEncoder boosterEnabled(final BooleanType value)
    {
        buffer.putByte(offset + 7, (byte)value.value());
        return this;
    }

    public static int boosterEncodingOffset()
    {
        return 8;
    }

    public static int boosterEncodingLength()
    {
        return 2;
    }

    private final BoosterEncoder booster = new BoosterEncoder();

    public BoosterEncoder booster()
    {
        booster.wrap(buffer, offset + 8);
        return booster;
    }

    public String toString()
    {
        return appendTo(new StringBuilder(100)).toString();
    }

    public StringBuilder appendTo(final StringBuilder builder)
    {
        EngineDecoder writer = new EngineDecoder();
        writer.wrap(buffer, offset);

        return writer.appendTo(builder);
    }
}
