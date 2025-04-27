/* Generated SBE (Simple Binary Encoding) message codec */
package composite.elements;

import org.agrona.DirectBuffer;

@SuppressWarnings("all")
public class InnerDecoder
{
    public static final int SCHEMA_ID = 3;
    public static final int SCHEMA_VERSION = 0;
    public static final int ENCODED_LENGTH = 16;
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private int offset;
    private DirectBuffer buffer;

    public InnerDecoder wrap(final DirectBuffer buffer, final int offset)
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

    public static int firstEncodingOffset()
    {
        return 0;
    }

    public static int firstEncodingLength()
    {
        return 8;
    }

    public static int firstSinceVersion()
    {
        return 0;
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

    public long first()
    {
        return buffer.getLong(offset + 0, java.nio.ByteOrder.LITTLE_ENDIAN);
    }


    public static int secondEncodingOffset()
    {
        return 8;
    }

    public static int secondEncodingLength()
    {
        return 8;
    }

    public static int secondSinceVersion()
    {
        return 0;
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

    public long second()
    {
        return buffer.getLong(offset + 8, java.nio.ByteOrder.LITTLE_ENDIAN);
    }


    public String toString()
    {
        return appendTo(new StringBuilder(100)).toString();
    }

    public StringBuilder appendTo(final StringBuilder builder)
    {
        builder.append('(');
        //Token{signal=ENCODING, name='first', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=8, offset=0, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=INT64, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
        builder.append("first=");
        builder.append(first());
        builder.append('|');
        //Token{signal=ENCODING, name='second', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=8, offset=8, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=INT64, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
        builder.append("second=");
        builder.append(second());
        builder.append(')');

        return builder;
    }
}
