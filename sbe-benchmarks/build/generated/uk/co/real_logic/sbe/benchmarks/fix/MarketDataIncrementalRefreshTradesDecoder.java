/* Generated SBE (Simple Binary Encoding) message codec */
package uk.co.real_logic.sbe.benchmarks.fix;

import org.agrona.concurrent.UnsafeBuffer;

/**
 * Trade
 */
@SuppressWarnings("all")
public class MarketDataIncrementalRefreshTradesDecoder
{
    public static final int BLOCK_LENGTH = 11;
    public static final int TEMPLATE_ID = 2;
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 1;
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private final MarketDataIncrementalRefreshTradesDecoder parentMessage = this;
    private UnsafeBuffer buffer;
    protected int offset;
    protected int limit;
    protected int actingBlockLength;
    protected int actingVersion;

    public int sbeBlockLength()
    {
        return BLOCK_LENGTH;
    }

    public int sbeTemplateId()
    {
        return TEMPLATE_ID;
    }

    public int sbeSchemaId()
    {
        return SCHEMA_ID;
    }

    public int sbeSchemaVersion()
    {
        return SCHEMA_VERSION;
    }

    public String sbeSemanticType()
    {
        return "X";
    }

    public UnsafeBuffer buffer()
    {
        return buffer;
    }

    public int offset()
    {
        return offset;
    }

    public MarketDataIncrementalRefreshTradesDecoder wrap(
        final UnsafeBuffer buffer, final int offset, final int actingBlockLength, final int actingVersion)
    {
        if (buffer != this.buffer)
        {
            this.buffer = buffer;
        }
        this.offset = offset;
        this.actingBlockLength = actingBlockLength;
        this.actingVersion = actingVersion;
        limit(offset + actingBlockLength);

        return this;
    }

    public int encodedLength()
    {
        return limit - offset;
    }

    public int limit()
    {
        return limit;
    }

    public void limit(final int limit)
    {
        this.limit = limit;
    }

    public static int transactTimeId()
    {
        return 60;
    }

    public static int transactTimeSinceVersion()
    {
        return 0;
    }

    public static int transactTimeEncodingOffset()
    {
        return 0;
    }

    public static int transactTimeEncodingLength()
    {
        return 8;
    }

    public static String transactTimeMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "";
            case TIME_UNIT: return "nanosecond";
            case SEMANTIC_TYPE: return "";
            case PRESENCE: return "required";
        }

        return "";
    }

    public static long transactTimeNullValue()
    {
        return 0xffffffffffffffffL;
    }

    public static long transactTimeMinValue()
    {
        return 0x0L;
    }

    public static long transactTimeMaxValue()
    {
        return 0xfffffffffffffffeL;
    }

    public long transactTime()
    {
        return buffer.getLong(offset + 0, java.nio.ByteOrder.LITTLE_ENDIAN);
    }


    public static int eventTimeDeltaId()
    {
        return 37704;
    }

    public static int eventTimeDeltaSinceVersion()
    {
        return 0;
    }

    public static int eventTimeDeltaEncodingOffset()
    {
        return 8;
    }

    public static int eventTimeDeltaEncodingLength()
    {
        return 2;
    }

    public static String eventTimeDeltaMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "";
            case TIME_UNIT: return "";
            case SEMANTIC_TYPE: return "";
            case PRESENCE: return "required";
        }

        return "";
    }

    public static int eventTimeDeltaNullValue()
    {
        return 65535;
    }

    public static int eventTimeDeltaMinValue()
    {
        return 0;
    }

    public static int eventTimeDeltaMaxValue()
    {
        return 65534;
    }

    public int eventTimeDelta()
    {
        return (buffer.getShort(offset + 8, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
    }


    public static int matchEventIndicatorId()
    {
        return 5799;
    }

    public static int matchEventIndicatorSinceVersion()
    {
        return 0;
    }

    public static int matchEventIndicatorEncodingOffset()
    {
        return 10;
    }

    public static int matchEventIndicatorEncodingLength()
    {
        return 1;
    }

    public static String matchEventIndicatorMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "";
            case TIME_UNIT: return "";
            case SEMANTIC_TYPE: return "";
            case PRESENCE: return "required";
        }

        return "";
    }

    public MatchEventIndicator matchEventIndicator()
    {
        return MatchEventIndicator.get(buffer.getByte(offset + 10));
    }


    private final MdIncGrpDecoder mdIncGrp = new MdIncGrpDecoder(this);

    public static long mdIncGrpDecoderId()
    {
        return 268;
    }

    public static int mdIncGrpDecoderSinceVersion()
    {
        return 0;
    }

    public MdIncGrpDecoder mdIncGrp()
    {
        mdIncGrp.wrap(buffer);
        return mdIncGrp;
    }

    public static class MdIncGrpDecoder
        implements Iterable<MdIncGrpDecoder>, java.util.Iterator<MdIncGrpDecoder>
    {
        public static final int HEADER_SIZE = 4;
        private final MarketDataIncrementalRefreshTradesDecoder parentMessage;
        private UnsafeBuffer buffer;
        private int count;
        private int index;
        private int offset;
        private int blockLength;

        MdIncGrpDecoder(final MarketDataIncrementalRefreshTradesDecoder parentMessage)
        {
            this.parentMessage = parentMessage;
        }

        public void wrap(final UnsafeBuffer buffer)
        {
            if (buffer != this.buffer)
            {
                this.buffer = buffer;
            }
            index = -1;
            final int limit = parentMessage.limit();
            parentMessage.limit(limit + HEADER_SIZE);
            blockLength = (int)(buffer.getShort(limit + 0, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
            count = (int)(buffer.getShort(limit + 2, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        }

        public static int sbeHeaderSize()
        {
            return HEADER_SIZE;
        }

        public static int sbeBlockLength()
        {
            return 33;
        }

        public int actingBlockLength()
        {
            return blockLength;
        }

        public int count()
        {
            return count;
        }

        public java.util.Iterator<MdIncGrpDecoder> iterator()
        {
            return this;
        }

        public void remove()
        {
            throw new UnsupportedOperationException();
        }

        public boolean hasNext()
        {
            return (index + 1) < count;
        }

        public MdIncGrpDecoder next()
        {
            if (index + 1 >= count)
            {
                throw new java.util.NoSuchElementException();
            }

            offset = parentMessage.limit();
            parentMessage.limit(offset + blockLength);
            ++index;

            return this;
        }

        public static int tradeIdId()
        {
            return 1003;
        }

        public static int tradeIdSinceVersion()
        {
            return 0;
        }

        public static int tradeIdEncodingOffset()
        {
            return 0;
        }

        public static int tradeIdEncodingLength()
        {
            return 8;
        }

        public static String tradeIdMetaAttribute(final MetaAttribute metaAttribute)
        {
            switch (metaAttribute)
            {
                case EPOCH: return "";
                case TIME_UNIT: return "";
                case SEMANTIC_TYPE: return "";
                case PRESENCE: return "required";
            }

            return "";
        }

        public static long tradeIdNullValue()
        {
            return 0xffffffffffffffffL;
        }

        public static long tradeIdMinValue()
        {
            return 0x0L;
        }

        public static long tradeIdMaxValue()
        {
            return 0xfffffffffffffffeL;
        }

        public long tradeId()
        {
            return buffer.getLong(offset + 0, java.nio.ByteOrder.LITTLE_ENDIAN);
        }


        public static int securityIdId()
        {
            return 48;
        }

        public static int securityIdSinceVersion()
        {
            return 0;
        }

        public static int securityIdEncodingOffset()
        {
            return 8;
        }

        public static int securityIdEncodingLength()
        {
            return 8;
        }

        public static String securityIdMetaAttribute(final MetaAttribute metaAttribute)
        {
            switch (metaAttribute)
            {
                case EPOCH: return "";
                case TIME_UNIT: return "";
                case SEMANTIC_TYPE: return "";
                case PRESENCE: return "required";
            }

            return "";
        }

        public static long securityIdNullValue()
        {
            return 0xffffffffffffffffL;
        }

        public static long securityIdMinValue()
        {
            return 0x0L;
        }

        public static long securityIdMaxValue()
        {
            return 0xfffffffffffffffeL;
        }

        public long securityId()
        {
            return buffer.getLong(offset + 8, java.nio.ByteOrder.LITTLE_ENDIAN);
        }


        public static int mdEntryPxId()
        {
            return 270;
        }

        public static int mdEntryPxSinceVersion()
        {
            return 0;
        }

        public static int mdEntryPxEncodingOffset()
        {
            return 16;
        }

        public static int mdEntryPxEncodingLength()
        {
            return 8;
        }

        public static String mdEntryPxMetaAttribute(final MetaAttribute metaAttribute)
        {
            switch (metaAttribute)
            {
                case EPOCH: return "";
                case TIME_UNIT: return "";
                case SEMANTIC_TYPE: return "";
                case PRESENCE: return "required";
            }

            return "";
        }

        private final Decimal64Decoder mdEntryPx = new Decimal64Decoder();

        public Decimal64Decoder mdEntryPx()
        {
            mdEntryPx.wrap(buffer, offset + 16);
            return mdEntryPx;
        }

        public static int mdEntrySizeId()
        {
            return 271;
        }

        public static int mdEntrySizeSinceVersion()
        {
            return 0;
        }

        public static int mdEntrySizeEncodingOffset()
        {
            return 24;
        }

        public static int mdEntrySizeEncodingLength()
        {
            return 4;
        }

        public static String mdEntrySizeMetaAttribute(final MetaAttribute metaAttribute)
        {
            switch (metaAttribute)
            {
                case EPOCH: return "";
                case TIME_UNIT: return "";
                case SEMANTIC_TYPE: return "";
                case PRESENCE: return "required";
            }

            return "";
        }

        private final IntQty32Decoder mdEntrySize = new IntQty32Decoder();

        public IntQty32Decoder mdEntrySize()
        {
            mdEntrySize.wrap(buffer, offset + 24);
            return mdEntrySize;
        }

        public static int numberOfOrdersId()
        {
            return 346;
        }

        public static int numberOfOrdersSinceVersion()
        {
            return 0;
        }

        public static int numberOfOrdersEncodingOffset()
        {
            return 28;
        }

        public static int numberOfOrdersEncodingLength()
        {
            return 2;
        }

        public static String numberOfOrdersMetaAttribute(final MetaAttribute metaAttribute)
        {
            switch (metaAttribute)
            {
                case EPOCH: return "";
                case TIME_UNIT: return "";
                case SEMANTIC_TYPE: return "";
                case PRESENCE: return "required";
            }

            return "";
        }

        public static int numberOfOrdersNullValue()
        {
            return 65535;
        }

        public static int numberOfOrdersMinValue()
        {
            return 0;
        }

        public static int numberOfOrdersMaxValue()
        {
            return 65534;
        }

        public int numberOfOrders()
        {
            return (buffer.getShort(offset + 28, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
        }


        public static int mdUpdateActionId()
        {
            return 279;
        }

        public static int mdUpdateActionSinceVersion()
        {
            return 0;
        }

        public static int mdUpdateActionEncodingOffset()
        {
            return 30;
        }

        public static int mdUpdateActionEncodingLength()
        {
            return 1;
        }

        public static String mdUpdateActionMetaAttribute(final MetaAttribute metaAttribute)
        {
            switch (metaAttribute)
            {
                case EPOCH: return "";
                case TIME_UNIT: return "";
                case SEMANTIC_TYPE: return "";
                case PRESENCE: return "required";
            }

            return "";
        }

        public MDUpdateAction mdUpdateAction()
        {
            return MDUpdateAction.get(((short)(buffer.getByte(offset + 30) & 0xFF)));
        }


        public static int rptSeqId()
        {
            return 83;
        }

        public static int rptSeqSinceVersion()
        {
            return 0;
        }

        public static int rptSeqEncodingOffset()
        {
            return 31;
        }

        public static int rptSeqEncodingLength()
        {
            return 1;
        }

        public static String rptSeqMetaAttribute(final MetaAttribute metaAttribute)
        {
            switch (metaAttribute)
            {
                case EPOCH: return "";
                case TIME_UNIT: return "";
                case SEMANTIC_TYPE: return "";
                case PRESENCE: return "required";
            }

            return "";
        }

        public static short rptSeqNullValue()
        {
            return (short)255;
        }

        public static short rptSeqMinValue()
        {
            return (short)0;
        }

        public static short rptSeqMaxValue()
        {
            return (short)254;
        }

        public short rptSeq()
        {
            return ((short)(buffer.getByte(offset + 31) & 0xFF));
        }


        public static int aggressorSideId()
        {
            return 5797;
        }

        public static int aggressorSideSinceVersion()
        {
            return 0;
        }

        public static int aggressorSideEncodingOffset()
        {
            return 32;
        }

        public static int aggressorSideEncodingLength()
        {
            return 1;
        }

        public static String aggressorSideMetaAttribute(final MetaAttribute metaAttribute)
        {
            switch (metaAttribute)
            {
                case EPOCH: return "";
                case TIME_UNIT: return "";
                case SEMANTIC_TYPE: return "";
                case PRESENCE: return "required";
            }

            return "";
        }

        public Side aggressorSide()
        {
            return Side.get(buffer.getByte(offset + 32));
        }


        public static int mdEntryTypeId()
        {
            return 269;
        }

        public static int mdEntryTypeSinceVersion()
        {
            return 0;
        }

        public static int mdEntryTypeEncodingOffset()
        {
            return 33;
        }

        public static int mdEntryTypeEncodingLength()
        {
            return 1;
        }

        public static String mdEntryTypeMetaAttribute(final MetaAttribute metaAttribute)
        {
            switch (metaAttribute)
            {
                case EPOCH: return "";
                case TIME_UNIT: return "";
                case SEMANTIC_TYPE: return "";
                case PRESENCE: return "constant";
            }

            return "";
        }

        public MDEntryType mdEntryType()
        {
            return MDEntryType.TRADE;
        }



        public String toString()
        {
            return appendTo(new StringBuilder(100)).toString();
        }

        public StringBuilder appendTo(final StringBuilder builder)
        {
            builder.append('(');
            //Token{signal=BEGIN_FIELD, name='TradeId', referencedName='null', description='null', id=1003, version=0, deprecated=0, encodedLength=8, offset=0, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
            //Token{signal=ENCODING, name='uint64', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=8, offset=0, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=UINT64, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
            builder.append("tradeId=");
            builder.append(tradeId());
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='SecurityId', referencedName='null', description='null', id=48, version=0, deprecated=0, encodedLength=8, offset=8, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
            //Token{signal=ENCODING, name='uint64', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=8, offset=8, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=UINT64, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
            builder.append("securityId=");
            builder.append(securityId());
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='MdEntryPx', referencedName='null', description='null', id=270, version=0, deprecated=0, encodedLength=8, offset=16, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
            //Token{signal=BEGIN_COMPOSITE, name='Decimal64', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=8, offset=16, componentTokenCount=4, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
            builder.append("mdEntryPx=");
            mdEntryPx().appendTo(builder);
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='MdEntrySize', referencedName='null', description='null', id=271, version=0, deprecated=0, encodedLength=4, offset=24, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
            //Token{signal=BEGIN_COMPOSITE, name='IntQty32', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=4, offset=24, componentTokenCount=4, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='Qty'}}
            builder.append("mdEntrySize=");
            mdEntrySize().appendTo(builder);
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='NumberOfOrders', referencedName='null', description='null', id=346, version=0, deprecated=0, encodedLength=2, offset=28, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
            //Token{signal=ENCODING, name='uint16', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=2, offset=28, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=UINT16, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
            builder.append("numberOfOrders=");
            builder.append(numberOfOrders());
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='MdUpdateAction', referencedName='null', description='null', id=279, version=0, deprecated=0, encodedLength=1, offset=30, componentTokenCount=8, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
            //Token{signal=BEGIN_ENUM, name='MDUpdateAction', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=30, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=UINT8, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='int'}}
            builder.append("mdUpdateAction=");
            builder.append(mdUpdateAction());
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='RptSeq', referencedName='null', description='null', id=83, version=0, deprecated=0, encodedLength=1, offset=31, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
            //Token{signal=ENCODING, name='uint8', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=31, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=UINT8, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
            builder.append("rptSeq=");
            builder.append(rptSeq());
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='AggressorSide', referencedName='null', description='null', id=5797, version=0, deprecated=0, encodedLength=1, offset=32, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
            //Token{signal=BEGIN_ENUM, name='Side', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=32, componentTokenCount=4, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
            builder.append("aggressorSide=");
            builder.append(aggressorSide());
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='MdEntryType', referencedName='null', description='null', id=269, version=0, deprecated=0, encodedLength=0, offset=33, componentTokenCount=20, encoding=Encoding{presence=CONSTANT, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=MDEntryType.TRADE, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
            //Token{signal=BEGIN_ENUM, name='MDEntryType', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=33, componentTokenCount=18, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='char'}}
            builder.append("mdEntryType=");
            builder.append(mdEntryType());
            builder.append(')');
            return builder;
        }
    }


    public String toString()
    {
        return appendTo(new StringBuilder(100)).toString();
    }

    public StringBuilder appendTo(final StringBuilder builder)
    {
        final int originalLimit = limit();
        limit(offset + actingBlockLength);
        builder.append("[MarketDataIncrementalRefreshTrades](sbeTemplateId=");
        builder.append(TEMPLATE_ID);
        builder.append("|sbeSchemaId=");
        builder.append(SCHEMA_ID);
        builder.append("|sbeSchemaVersion=");
        if (parentMessage.actingVersion != SCHEMA_VERSION)
        {
            builder.append(parentMessage.actingVersion);
            builder.append('/');
        }
        builder.append(SCHEMA_VERSION);
        builder.append("|sbeBlockLength=");
        if (actingBlockLength != BLOCK_LENGTH)
        {
            builder.append(actingBlockLength);
            builder.append('/');
        }
        builder.append(BLOCK_LENGTH);
        builder.append("):");
        //Token{signal=BEGIN_FIELD, name='TransactTime', referencedName='null', description='null', id=60, version=0, deprecated=0, encodedLength=8, offset=0, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=nanosecond, semanticType='null'}}
        //Token{signal=ENCODING, name='timestamp', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=8, offset=0, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=UINT64, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=nanosecond, semanticType='UTCTimestamp'}}
        builder.append("transactTime=");
        builder.append(transactTime());
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='EventTimeDelta', referencedName='null', description='null', id=37704, version=0, deprecated=0, encodedLength=2, offset=8, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
        //Token{signal=ENCODING, name='uint16', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=2, offset=8, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=UINT16, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
        builder.append("eventTimeDelta=");
        builder.append(eventTimeDelta());
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='MatchEventIndicator', referencedName='null', description='null', id=5799, version=0, deprecated=0, encodedLength=1, offset=10, componentTokenCount=8, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
        //Token{signal=BEGIN_ENUM, name='MatchEventIndicator', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=10, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='MatchEventIndicator'}}
        builder.append("matchEventIndicator=");
        builder.append(matchEventIndicator());
        builder.append('|');
        //Token{signal=BEGIN_GROUP, name='MdIncGrp', referencedName='null', description='null', id=268, version=0, deprecated=0, encodedLength=33, offset=11, componentTokenCount=64, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
        builder.append("mdIncGrp=[");
        MdIncGrpDecoder mdIncGrp = mdIncGrp();
        if (mdIncGrp.count() > 0)
        {
            while (mdIncGrp.hasNext())
            {
                mdIncGrp.next().appendTo(builder);
                builder.append(',');
            }
            builder.setLength(builder.length() - 1);
        }
        builder.append(']');

        limit(originalLimit);

        return builder;
    }
}
