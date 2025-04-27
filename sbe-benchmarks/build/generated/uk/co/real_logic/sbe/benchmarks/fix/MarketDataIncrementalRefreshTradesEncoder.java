/* Generated SBE (Simple Binary Encoding) message codec */
package uk.co.real_logic.sbe.benchmarks.fix;

import org.agrona.concurrent.UnsafeBuffer;

/**
 * Trade
 */
@SuppressWarnings("all")
public class MarketDataIncrementalRefreshTradesEncoder
{
    public static final int BLOCK_LENGTH = 11;
    public static final int TEMPLATE_ID = 2;
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 1;
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private final MarketDataIncrementalRefreshTradesEncoder parentMessage = this;
    private UnsafeBuffer buffer;
    protected int offset;
    protected int limit;

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

    public MarketDataIncrementalRefreshTradesEncoder wrap(final UnsafeBuffer buffer, final int offset)
    {
        if (buffer != this.buffer)
        {
            this.buffer = buffer;
        }
        this.offset = offset;
        limit(offset + BLOCK_LENGTH);

        return this;
    }

    public MarketDataIncrementalRefreshTradesEncoder wrapAndApplyHeader(
        final UnsafeBuffer buffer, final int offset, final MessageHeaderEncoder headerEncoder)
    {
        headerEncoder
            .wrap(buffer, offset)
            .blockLength(BLOCK_LENGTH)
            .templateId(TEMPLATE_ID)
            .schemaId(SCHEMA_ID)
            .version(SCHEMA_VERSION);

        return wrap(buffer, offset + MessageHeaderEncoder.ENCODED_LENGTH);
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

    public MarketDataIncrementalRefreshTradesEncoder transactTime(final long value)
    {
        buffer.putLong(offset + 0, value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
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

    public MarketDataIncrementalRefreshTradesEncoder eventTimeDelta(final int value)
    {
        buffer.putShort(offset + 8, (short)value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
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

    public MarketDataIncrementalRefreshTradesEncoder matchEventIndicator(final MatchEventIndicator value)
    {
        buffer.putByte(offset + 10, value.value());
        return this;
    }

    private final MdIncGrpEncoder mdIncGrp = new MdIncGrpEncoder(this);

    public static long mdIncGrpId()
    {
        return 268;
    }

    public MdIncGrpEncoder mdIncGrpCount(final int count)
    {
        mdIncGrp.wrap(buffer, count);
        return mdIncGrp;
    }

    public static class MdIncGrpEncoder
    {
        public static final int HEADER_SIZE = 4;
        private final MarketDataIncrementalRefreshTradesEncoder parentMessage;
        private UnsafeBuffer buffer;
        private int count;
        private int index;
        private int offset;

        MdIncGrpEncoder(final MarketDataIncrementalRefreshTradesEncoder parentMessage)
        {
            this.parentMessage = parentMessage;
        }

        public void wrap(final UnsafeBuffer buffer, final int count)
        {
            if (count < 0 || count > 65534)
            {
                throw new IllegalArgumentException("count outside allowed range: count=" + count);
            }

            if (buffer != this.buffer)
            {
                this.buffer = buffer;
            }

            index = -1;
            this.count = count;
            final int limit = parentMessage.limit();
            parentMessage.limit(limit + HEADER_SIZE);
            buffer.putShort(limit + 0, (short)(int)33, java.nio.ByteOrder.LITTLE_ENDIAN);
            buffer.putShort(limit + 2, (short)(int)count, java.nio.ByteOrder.LITTLE_ENDIAN);
        }

        public static int sbeHeaderSize()
        {
            return HEADER_SIZE;
        }

        public static int sbeBlockLength()
        {
            return 33;
        }

        public MdIncGrpEncoder next()
        {
            if (index + 1 >= count)
            {
                throw new java.util.NoSuchElementException();
            }

            offset = parentMessage.limit();
            parentMessage.limit(offset + sbeBlockLength());
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

        public MdIncGrpEncoder tradeId(final long value)
        {
            buffer.putLong(offset + 0, value, java.nio.ByteOrder.LITTLE_ENDIAN);
            return this;
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

        public MdIncGrpEncoder securityId(final long value)
        {
            buffer.putLong(offset + 8, value, java.nio.ByteOrder.LITTLE_ENDIAN);
            return this;
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

        private final Decimal64Encoder mdEntryPx = new Decimal64Encoder();

        public Decimal64Encoder mdEntryPx()
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

        private final IntQty32Encoder mdEntrySize = new IntQty32Encoder();

        public IntQty32Encoder mdEntrySize()
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

        public MdIncGrpEncoder numberOfOrders(final int value)
        {
            buffer.putShort(offset + 28, (short)value, java.nio.ByteOrder.LITTLE_ENDIAN);
            return this;
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

        public MdIncGrpEncoder mdUpdateAction(final MDUpdateAction value)
        {
            buffer.putByte(offset + 30, (byte)value.value());
            return this;
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

        public MdIncGrpEncoder rptSeq(final short value)
        {
            buffer.putByte(offset + 31, (byte)value);
            return this;
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

        public MdIncGrpEncoder aggressorSide(final Side value)
        {
            buffer.putByte(offset + 32, value.value());
            return this;
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

        public MdIncGrpEncoder mdEntryType(final MDEntryType value)
        {
            buffer.putByte(offset + 33, value.value());
            return this;
        }
    }


    public String toString()
    {
        return appendTo(new StringBuilder(100)).toString();
    }

    public StringBuilder appendTo(final StringBuilder builder)
    {
        MarketDataIncrementalRefreshTradesDecoder writer = new MarketDataIncrementalRefreshTradesDecoder();
        writer.wrap(buffer, offset, BLOCK_LENGTH, SCHEMA_VERSION);

        return writer.appendTo(builder);
    }
}
