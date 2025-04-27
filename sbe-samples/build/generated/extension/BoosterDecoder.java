/* Generated SBE (Simple Binary Encoding) message codec */
package extension;

import org.agrona.DirectBuffer;
import org.agrona.sbe.*;

@SuppressWarnings("all")
public class BoosterDecoder implements CompositeDecoderFlyweight
{
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 1;
    public static final int ENCODED_LENGTH = 2;
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private int offset;
    private DirectBuffer buffer;

    public BoosterDecoder wrap(final DirectBuffer buffer, final int offset)
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

    public static int boostTypeEncodingOffset()
    {
        return 0;
    }

    public static int boostTypeEncodingLength()
    {
        return 1;
    }

    public static int boostTypeSinceVersion()
    {
        return 0;
    }

    public BoostType boostType()
    {
        return BoostType.get(buffer.getByte(offset + 0));
    }


    public static int horsePowerEncodingOffset()
    {
        return 1;
    }

    public static int horsePowerEncodingLength()
    {
        return 1;
    }

    public static int horsePowerSinceVersion()
    {
        return 0;
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

    public short horsePower()
    {
        return ((short)(buffer.getByte(offset + 1) & 0xFF));
    }


    public String toString()
    {
        return appendTo(new StringBuilder(100)).toString();
    }

    public StringBuilder appendTo(final StringBuilder builder)
    {
        builder.append('(');
        //Token{signal=BEGIN_ENUM, name='BoostType', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=0, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
        builder.append("boostType=");
        builder.append(boostType());
        builder.append('|');
        //Token{signal=ENCODING, name='horsePower', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=1, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=UINT8, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
        builder.append("horsePower=");
        builder.append(horsePower());
        builder.append(')');

        return builder;
    }
}
