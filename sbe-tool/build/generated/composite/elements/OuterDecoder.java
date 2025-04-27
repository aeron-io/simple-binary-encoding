/* Generated SBE (Simple Binary Encoding) message codec */
package composite.elements;

import org.agrona.DirectBuffer;

@SuppressWarnings("all")
public class OuterDecoder
{
    public static final int SCHEMA_ID = 3;
    public static final int SCHEMA_VERSION = 0;
    public static final int ENCODED_LENGTH = 22;
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private int offset;
    private DirectBuffer buffer;

    public OuterDecoder wrap(final DirectBuffer buffer, final int offset)
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

    public static int enumOneEncodingOffset()
    {
        return 0;
    }

    public static int enumOneEncodingLength()
    {
        return 1;
    }

    public static int enumOneSinceVersion()
    {
        return 0;
    }

    public EnumOne enumOne()
    {
        return EnumOne.get(((short)(buffer.getByte(offset + 0) & 0xFF)));
    }


    public static int zerothEncodingOffset()
    {
        return 1;
    }

    public static int zerothEncodingLength()
    {
        return 1;
    }

    public static int zerothSinceVersion()
    {
        return 0;
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

    public short zeroth()
    {
        return ((short)(buffer.getByte(offset + 1) & 0xFF));
    }


    public static int setOneEncodingOffset()
    {
        return 2;
    }

    public static int setOneEncodingLength()
    {
        return 4;
    }

    public static int setOneSinceVersion()
    {
        return 0;
    }

    private final SetOneDecoder setOne = new SetOneDecoder();

    /**
     * set as uint32
     *
     * @return SetOneDecoder : set as uint32
     */
    public SetOneDecoder setOne()
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

    public static int innerSinceVersion()
    {
        return 0;
    }

    private final InnerDecoder inner = new InnerDecoder();

    public InnerDecoder inner()
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
        builder.append('(');
        //Token{signal=BEGIN_ENUM, name='enumOne', referencedName='null', description='enum as uint8', id=-1, version=0, deprecated=0, encodedLength=1, offset=0, componentTokenCount=4, encoding=Encoding{presence=REQUIRED, primitiveType=UINT8, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='int'}}
        builder.append("enumOne=");
        builder.append(enumOne());
        builder.append('|');
        //Token{signal=ENCODING, name='zeroth', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=1, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=UINT8, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
        builder.append("zeroth=");
        builder.append(zeroth());
        builder.append('|');
        //Token{signal=BEGIN_SET, name='setOne', referencedName='null', description='set as uint32', id=-1, version=0, deprecated=0, encodedLength=4, offset=2, componentTokenCount=5, encoding=Encoding{presence=REQUIRED, primitiveType=UINT32, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='int'}}
        builder.append("setOne=");
        builder.append(setOne());
        builder.append('|');
        //Token{signal=BEGIN_COMPOSITE, name='inner', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=16, offset=6, componentTokenCount=4, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
        builder.append("inner=");
        inner().appendTo(builder);
        builder.append(')');

        return builder;
    }
}
