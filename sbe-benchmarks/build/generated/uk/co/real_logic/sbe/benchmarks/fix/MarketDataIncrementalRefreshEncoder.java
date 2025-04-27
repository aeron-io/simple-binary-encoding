/* Generated SBE (Simple Binary Encoding) message codec */
package uk.co.real_logic.sbe.benchmarks.fix;

import org.agrona.concurrent.UnsafeBuffer;

@SuppressWarnings("all")
public class MarketDataIncrementalRefreshEncoder
{
    public static final int BLOCK_LENGTH = 2;
    public static final int TEMPLATE_ID = 88;
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 1;
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private final MarketDataIncrementalRefreshEncoder parentMessage = this;
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

    public MarketDataIncrementalRefreshEncoder wrap(final UnsafeBuffer buffer, final int offset)
    {
        if (buffer != this.buffer)
        {
            this.buffer = buffer;
        }
        this.offset = offset;
        limit(offset + BLOCK_LENGTH);

        return this;
    }

    public MarketDataIncrementalRefreshEncoder wrapAndApplyHeader(
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

    public static int tradeDateId()
    {
        return 75;
    }

    public static int tradeDateSinceVersion()
    {
        return 0;
    }

    public static int tradeDateEncodingOffset()
    {
        return 0;
    }

    public static int tradeDateEncodingLength()
    {
        return 2;
    }

    public static String tradeDateMetaAttribute(final MetaAttribute metaAttribute)
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

    public static int tradeDateNullValue()
    {
        return 65535;
    }

    public static int tradeDateMinValue()
    {
        return 0;
    }

    public static int tradeDateMaxValue()
    {
        return 65534;
    }

    public MarketDataIncrementalRefreshEncoder tradeDate(final int value)
    {
        buffer.putShort(offset + 0, (short)value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }


    private final EntriesEncoder entries = new EntriesEncoder(this);

    public static long entriesId()
    {
        return 268;
    }

    public EntriesEncoder entriesCount(final int count)
    {
        entries.wrap(buffer, count);
        return entries;
    }

    public static class EntriesEncoder
    {
        public static final int HEADER_SIZE = 4;
        private final MarketDataIncrementalRefreshEncoder parentMessage;
        private UnsafeBuffer buffer;
        private int count;
        private int index;
        private int offset;

        EntriesEncoder(final MarketDataIncrementalRefreshEncoder parentMessage)
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
            buffer.putShort(limit + 0, (short)(int)82, java.nio.ByteOrder.LITTLE_ENDIAN);
            buffer.putShort(limit + 2, (short)(int)count, java.nio.ByteOrder.LITTLE_ENDIAN);
        }

        public static int sbeHeaderSize()
        {
            return HEADER_SIZE;
        }

        public static int sbeBlockLength()
        {
            return 82;
        }

        public EntriesEncoder next()
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
            return 0;
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

        public EntriesEncoder mdUpdateAction(final MDUpdateAction value)
        {
            buffer.putByte(offset + 0, (byte)value.value());
            return this;
        }

        public static int mdPriceLevelId()
        {
            return 1023;
        }

        public static int mdPriceLevelSinceVersion()
        {
            return 0;
        }

        public static int mdPriceLevelEncodingOffset()
        {
            return 1;
        }

        public static int mdPriceLevelEncodingLength()
        {
            return 1;
        }

        public static String mdPriceLevelMetaAttribute(final MetaAttribute metaAttribute)
        {
            switch (metaAttribute)
            {
                case EPOCH: return "";
                case TIME_UNIT: return "";
                case SEMANTIC_TYPE: return "MDPriceLevel";
                case PRESENCE: return "required";
            }

            return "";
        }

        public static short mdPriceLevelNullValue()
        {
            return (short)255;
        }

        public static short mdPriceLevelMinValue()
        {
            return (short)0;
        }

        public static short mdPriceLevelMaxValue()
        {
            return (short)254;
        }

        public EntriesEncoder mdPriceLevel(final short value)
        {
            buffer.putByte(offset + 1, (byte)value);
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
            return 2;
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
                case PRESENCE: return "required";
            }

            return "";
        }

        public EntriesEncoder mdEntryType(final MDEntryType value)
        {
            buffer.putByte(offset + 2, value.value());
            return this;
        }

        public static int securityIdSourceId()
        {
            return 22;
        }

        public static int securityIdSourceSinceVersion()
        {
            return 0;
        }

        public static int securityIdSourceEncodingOffset()
        {
            return 3;
        }

        public static int securityIdSourceEncodingLength()
        {
            return 1;
        }

        public static String securityIdSourceMetaAttribute(final MetaAttribute metaAttribute)
        {
            switch (metaAttribute)
            {
                case EPOCH: return "";
                case TIME_UNIT: return "";
                case SEMANTIC_TYPE: return "SecurityID";
                case PRESENCE: return "required";
            }

            return "";
        }

        public static byte securityIdSourceNullValue()
        {
            return (byte)0;
        }

        public static byte securityIdSourceMinValue()
        {
            return (byte)32;
        }

        public static byte securityIdSourceMaxValue()
        {
            return (byte)126;
        }

        public EntriesEncoder securityIdSource(final byte value)
        {
            buffer.putByte(offset + 3, value);
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
            return 4;
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
                case SEMANTIC_TYPE: return "InstrumentID";
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

        public EntriesEncoder securityId(final long value)
        {
            buffer.putLong(offset + 4, value, java.nio.ByteOrder.LITTLE_ENDIAN);
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
            return 12;
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
                case SEMANTIC_TYPE: return "SequenceNumber";
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

        public EntriesEncoder rptSeq(final short value)
        {
            buffer.putByte(offset + 12, (byte)value);
            return this;
        }


        public static int quoteConditionId()
        {
            return 276;
        }

        public static int quoteConditionSinceVersion()
        {
            return 0;
        }

        public static int quoteConditionEncodingOffset()
        {
            return 13;
        }

        public static int quoteConditionEncodingLength()
        {
            return 1;
        }

        public static String quoteConditionMetaAttribute(final MetaAttribute metaAttribute)
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

        private final QuoteConditionEncoder quoteCondition = new QuoteConditionEncoder();

        public QuoteConditionEncoder quoteCondition()
        {
            quoteCondition.wrap(buffer, offset + 13);
            return quoteCondition;
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
            return 14;
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
                case SEMANTIC_TYPE: return "Price";
                case PRESENCE: return "required";
            }

            return "";
        }

        private final Decimal64Encoder mdEntryPx = new Decimal64Encoder();

        public Decimal64Encoder mdEntryPx()
        {
            mdEntryPx.wrap(buffer, offset + 14);
            return mdEntryPx;
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
            return 22;
        }

        public static int numberOfOrdersEncodingLength()
        {
            return 4;
        }

        public static String numberOfOrdersMetaAttribute(final MetaAttribute metaAttribute)
        {
            switch (metaAttribute)
            {
                case EPOCH: return "";
                case TIME_UNIT: return "";
                case SEMANTIC_TYPE: return "NumberOfOrders";
                case PRESENCE: return "required";
            }

            return "";
        }

        public static long numberOfOrdersNullValue()
        {
            return 4294967295L;
        }

        public static long numberOfOrdersMinValue()
        {
            return 0L;
        }

        public static long numberOfOrdersMaxValue()
        {
            return 4294967294L;
        }

        public EntriesEncoder numberOfOrders(final long value)
        {
            buffer.putInt(offset + 22, (int)value, java.nio.ByteOrder.LITTLE_ENDIAN);
            return this;
        }


        public static int mdEntryTimeId()
        {
            return 273;
        }

        public static int mdEntryTimeSinceVersion()
        {
            return 0;
        }

        public static int mdEntryTimeEncodingOffset()
        {
            return 26;
        }

        public static int mdEntryTimeEncodingLength()
        {
            return 8;
        }

        public static String mdEntryTimeMetaAttribute(final MetaAttribute metaAttribute)
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

        public static long mdEntryTimeNullValue()
        {
            return 0xffffffffffffffffL;
        }

        public static long mdEntryTimeMinValue()
        {
            return 0x0L;
        }

        public static long mdEntryTimeMaxValue()
        {
            return 0xfffffffffffffffeL;
        }

        public EntriesEncoder mdEntryTime(final long value)
        {
            buffer.putLong(offset + 26, value, java.nio.ByteOrder.LITTLE_ENDIAN);
            return this;
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
            return 34;
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
            mdEntrySize.wrap(buffer, offset + 34);
            return mdEntrySize;
        }

        public static int tradingSessionIdId()
        {
            return 336;
        }

        public static int tradingSessionIdSinceVersion()
        {
            return 0;
        }

        public static int tradingSessionIdEncodingOffset()
        {
            return 38;
        }

        public static int tradingSessionIdEncodingLength()
        {
            return 1;
        }

        public static String tradingSessionIdMetaAttribute(final MetaAttribute metaAttribute)
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

        public EntriesEncoder tradingSessionId(final MarketStateIdentifier value)
        {
            buffer.putByte(offset + 38, (byte)value.value());
            return this;
        }

        public static int netChgPrevDayId()
        {
            return 451;
        }

        public static int netChgPrevDaySinceVersion()
        {
            return 0;
        }

        public static int netChgPrevDayEncodingOffset()
        {
            return 39;
        }

        public static int netChgPrevDayEncodingLength()
        {
            return 8;
        }

        public static String netChgPrevDayMetaAttribute(final MetaAttribute metaAttribute)
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

        private final Decimal64Encoder netChgPrevDay = new Decimal64Encoder();

        public Decimal64Encoder netChgPrevDay()
        {
            netChgPrevDay.wrap(buffer, offset + 39);
            return netChgPrevDay;
        }

        public static int tickDirectionId()
        {
            return 274;
        }

        public static int tickDirectionSinceVersion()
        {
            return 0;
        }

        public static int tickDirectionEncodingOffset()
        {
            return 47;
        }

        public static int tickDirectionEncodingLength()
        {
            return 1;
        }

        public static String tickDirectionMetaAttribute(final MetaAttribute metaAttribute)
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

        public EntriesEncoder tickDirection(final TickDirection value)
        {
            buffer.putByte(offset + 47, (byte)value.value());
            return this;
        }

        public static int openCloseSettleFlagId()
        {
            return 286;
        }

        public static int openCloseSettleFlagSinceVersion()
        {
            return 0;
        }

        public static int openCloseSettleFlagEncodingOffset()
        {
            return 48;
        }

        public static int openCloseSettleFlagEncodingLength()
        {
            return 2;
        }

        public static String openCloseSettleFlagMetaAttribute(final MetaAttribute metaAttribute)
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

        public EntriesEncoder openCloseSettleFlag(final OpenCloseSettleFlag value)
        {
            buffer.putShort(offset + 48, (short)value.value(), java.nio.ByteOrder.LITTLE_ENDIAN);
            return this;
        }

        public static int settleDateId()
        {
            return 64;
        }

        public static int settleDateSinceVersion()
        {
            return 0;
        }

        public static int settleDateEncodingOffset()
        {
            return 50;
        }

        public static int settleDateEncodingLength()
        {
            return 8;
        }

        public static String settleDateMetaAttribute(final MetaAttribute metaAttribute)
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

        public static long settleDateNullValue()
        {
            return 0xffffffffffffffffL;
        }

        public static long settleDateMinValue()
        {
            return 0x0L;
        }

        public static long settleDateMaxValue()
        {
            return 0xfffffffffffffffeL;
        }

        public EntriesEncoder settleDate(final long value)
        {
            buffer.putLong(offset + 50, value, java.nio.ByteOrder.LITTLE_ENDIAN);
            return this;
        }


        public static int tradeConditionId()
        {
            return 277;
        }

        public static int tradeConditionSinceVersion()
        {
            return 0;
        }

        public static int tradeConditionEncodingOffset()
        {
            return 58;
        }

        public static int tradeConditionEncodingLength()
        {
            return 1;
        }

        public static String tradeConditionMetaAttribute(final MetaAttribute metaAttribute)
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

        private final TradeConditionEncoder tradeCondition = new TradeConditionEncoder();

        public TradeConditionEncoder tradeCondition()
        {
            tradeCondition.wrap(buffer, offset + 58);
            return tradeCondition;
        }

        public static int tradeVolumeId()
        {
            return 1020;
        }

        public static int tradeVolumeSinceVersion()
        {
            return 0;
        }

        public static int tradeVolumeEncodingOffset()
        {
            return 59;
        }

        public static int tradeVolumeEncodingLength()
        {
            return 4;
        }

        public static String tradeVolumeMetaAttribute(final MetaAttribute metaAttribute)
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

        private final IntQty32Encoder tradeVolume = new IntQty32Encoder();

        public IntQty32Encoder tradeVolume()
        {
            tradeVolume.wrap(buffer, offset + 59);
            return tradeVolume;
        }

        public static int mdQuoteTypeId()
        {
            return 1070;
        }

        public static int mdQuoteTypeSinceVersion()
        {
            return 0;
        }

        public static int mdQuoteTypeEncodingOffset()
        {
            return 63;
        }

        public static int mdQuoteTypeEncodingLength()
        {
            return 1;
        }

        public static String mdQuoteTypeMetaAttribute(final MetaAttribute metaAttribute)
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

        public EntriesEncoder mdQuoteType(final MDQuoteType value)
        {
            buffer.putByte(offset + 63, (byte)value.value());
            return this;
        }

        public static int fixingBracketId()
        {
            return 5790;
        }

        public static int fixingBracketSinceVersion()
        {
            return 0;
        }

        public static int fixingBracketEncodingOffset()
        {
            return 64;
        }

        public static int fixingBracketEncodingLength()
        {
            return 8;
        }

        public static String fixingBracketMetaAttribute(final MetaAttribute metaAttribute)
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

        public static long fixingBracketNullValue()
        {
            return 0xffffffffffffffffL;
        }

        public static long fixingBracketMinValue()
        {
            return 0x0L;
        }

        public static long fixingBracketMaxValue()
        {
            return 0xfffffffffffffffeL;
        }

        public EntriesEncoder fixingBracket(final long value)
        {
            buffer.putLong(offset + 64, value, java.nio.ByteOrder.LITTLE_ENDIAN);
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
            return 72;
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

        public EntriesEncoder aggressorSide(final Side value)
        {
            buffer.putByte(offset + 72, value.value());
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
            return 73;
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

        public EntriesEncoder matchEventIndicator(final MatchEventIndicator value)
        {
            buffer.putByte(offset + 73, value.value());
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
            return 74;
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
                case SEMANTIC_TYPE: return "ExecID";
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

        public EntriesEncoder tradeId(final long value)
        {
            buffer.putLong(offset + 74, value, java.nio.ByteOrder.LITTLE_ENDIAN);
            return this;
        }

    }


    public String toString()
    {
        return appendTo(new StringBuilder(100)).toString();
    }

    public StringBuilder appendTo(final StringBuilder builder)
    {
        MarketDataIncrementalRefreshDecoder writer = new MarketDataIncrementalRefreshDecoder();
        writer.wrap(buffer, offset, BLOCK_LENGTH, SCHEMA_VERSION);

        return writer.appendTo(builder);
    }
}
