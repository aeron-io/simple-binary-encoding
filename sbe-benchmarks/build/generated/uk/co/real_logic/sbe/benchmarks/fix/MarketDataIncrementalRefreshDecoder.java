/* Generated SBE (Simple Binary Encoding) message codec */
package uk.co.real_logic.sbe.benchmarks.fix;

import org.agrona.concurrent.UnsafeBuffer;

@SuppressWarnings("all")
public class MarketDataIncrementalRefreshDecoder
{
    public static final int BLOCK_LENGTH = 2;
    public static final int TEMPLATE_ID = 88;
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 1;
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private final MarketDataIncrementalRefreshDecoder parentMessage = this;
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

    public MarketDataIncrementalRefreshDecoder wrap(
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

    public int tradeDate()
    {
        return (buffer.getShort(offset + 0, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
    }


    private final EntriesDecoder entries = new EntriesDecoder(this);

    public static long entriesDecoderId()
    {
        return 268;
    }

    public static int entriesDecoderSinceVersion()
    {
        return 0;
    }

    public EntriesDecoder entries()
    {
        entries.wrap(buffer);
        return entries;
    }

    public static class EntriesDecoder
        implements Iterable<EntriesDecoder>, java.util.Iterator<EntriesDecoder>
    {
        public static final int HEADER_SIZE = 4;
        private final MarketDataIncrementalRefreshDecoder parentMessage;
        private UnsafeBuffer buffer;
        private int count;
        private int index;
        private int offset;
        private int blockLength;

        EntriesDecoder(final MarketDataIncrementalRefreshDecoder parentMessage)
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
            return 82;
        }

        public int actingBlockLength()
        {
            return blockLength;
        }

        public int count()
        {
            return count;
        }

        public java.util.Iterator<EntriesDecoder> iterator()
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

        public EntriesDecoder next()
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

        public MDUpdateAction mdUpdateAction()
        {
            return MDUpdateAction.get(((short)(buffer.getByte(offset + 0) & 0xFF)));
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

        public short mdPriceLevel()
        {
            return ((short)(buffer.getByte(offset + 1) & 0xFF));
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

        public MDEntryType mdEntryType()
        {
            return MDEntryType.get(buffer.getByte(offset + 2));
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

        public byte securityIdSource()
        {
            return buffer.getByte(offset + 3);
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

        public long securityId()
        {
            return buffer.getLong(offset + 4, java.nio.ByteOrder.LITTLE_ENDIAN);
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

        public short rptSeq()
        {
            return ((short)(buffer.getByte(offset + 12) & 0xFF));
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

        private final QuoteConditionDecoder quoteCondition = new QuoteConditionDecoder();

        public QuoteConditionDecoder quoteCondition()
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

        private final Decimal64Decoder mdEntryPx = new Decimal64Decoder();

        public Decimal64Decoder mdEntryPx()
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

        public long numberOfOrders()
        {
            return (buffer.getInt(offset + 22, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF_FFFFL);
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

        public long mdEntryTime()
        {
            return buffer.getLong(offset + 26, java.nio.ByteOrder.LITTLE_ENDIAN);
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

        private final IntQty32Decoder mdEntrySize = new IntQty32Decoder();

        public IntQty32Decoder mdEntrySize()
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

        public MarketStateIdentifier tradingSessionId()
        {
            return MarketStateIdentifier.get(((short)(buffer.getByte(offset + 38) & 0xFF)));
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

        private final Decimal64Decoder netChgPrevDay = new Decimal64Decoder();

        public Decimal64Decoder netChgPrevDay()
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

        public TickDirection tickDirection()
        {
            return TickDirection.get(((short)(buffer.getByte(offset + 47) & 0xFF)));
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

        public OpenCloseSettleFlag openCloseSettleFlag()
        {
            return OpenCloseSettleFlag.get((buffer.getShort(offset + 48, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF));
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

        public long settleDate()
        {
            return buffer.getLong(offset + 50, java.nio.ByteOrder.LITTLE_ENDIAN);
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

        private final TradeConditionDecoder tradeCondition = new TradeConditionDecoder();

        public TradeConditionDecoder tradeCondition()
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

        private final IntQty32Decoder tradeVolume = new IntQty32Decoder();

        public IntQty32Decoder tradeVolume()
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

        public MDQuoteType mdQuoteType()
        {
            return MDQuoteType.get(((short)(buffer.getByte(offset + 63) & 0xFF)));
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

        public long fixingBracket()
        {
            return buffer.getLong(offset + 64, java.nio.ByteOrder.LITTLE_ENDIAN);
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

        public Side aggressorSide()
        {
            return Side.get(buffer.getByte(offset + 72));
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

        public MatchEventIndicator matchEventIndicator()
        {
            return MatchEventIndicator.get(buffer.getByte(offset + 73));
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

        public long tradeId()
        {
            return buffer.getLong(offset + 74, java.nio.ByteOrder.LITTLE_ENDIAN);
        }



        public String toString()
        {
            return appendTo(new StringBuilder(100)).toString();
        }

        public StringBuilder appendTo(final StringBuilder builder)
        {
            builder.append('(');
            //Token{signal=BEGIN_FIELD, name='MdUpdateAction', referencedName='null', description='null', id=279, version=0, deprecated=0, encodedLength=1, offset=0, componentTokenCount=8, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
            //Token{signal=BEGIN_ENUM, name='MDUpdateAction', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=0, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=UINT8, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='int'}}
            builder.append("mdUpdateAction=");
            builder.append(mdUpdateAction());
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='MdPriceLevel', referencedName='null', description='null', id=1023, version=0, deprecated=0, encodedLength=1, offset=1, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='MDPriceLevel'}}
            //Token{signal=ENCODING, name='uint8', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=1, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=UINT8, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='MDPriceLevel'}}
            builder.append("mdPriceLevel=");
            builder.append(mdPriceLevel());
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='MdEntryType', referencedName='null', description='null', id=269, version=0, deprecated=0, encodedLength=1, offset=2, componentTokenCount=20, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
            //Token{signal=BEGIN_ENUM, name='MDEntryType', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=2, componentTokenCount=18, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='char'}}
            builder.append("mdEntryType=");
            builder.append(mdEntryType());
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='SecurityIdSource', referencedName='null', description='null', id=22, version=0, deprecated=0, encodedLength=1, offset=3, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='SecurityID'}}
            //Token{signal=ENCODING, name='char', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=3, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='SecurityID'}}
            builder.append("securityIdSource=");
            builder.append(securityIdSource());
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='SecurityId', referencedName='null', description='null', id=48, version=0, deprecated=0, encodedLength=8, offset=4, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='InstrumentID'}}
            //Token{signal=ENCODING, name='uint64', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=8, offset=4, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=UINT64, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='InstrumentID'}}
            builder.append("securityId=");
            builder.append(securityId());
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='RptSeq', referencedName='null', description='null', id=83, version=0, deprecated=0, encodedLength=1, offset=12, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='SequenceNumber'}}
            //Token{signal=ENCODING, name='uint8', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=12, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=UINT8, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='SequenceNumber'}}
            builder.append("rptSeq=");
            builder.append(rptSeq());
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='QuoteCondition', referencedName='null', description='null', id=276, version=0, deprecated=0, encodedLength=1, offset=13, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
            //Token{signal=BEGIN_SET, name='QuoteCondition', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=13, componentTokenCount=4, encoding=Encoding{presence=REQUIRED, primitiveType=UINT8, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='QuoteCondition'}}
            builder.append("quoteCondition=");
            builder.append(quoteCondition());
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='MdEntryPx', referencedName='null', description='null', id=270, version=0, deprecated=0, encodedLength=8, offset=14, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='Price'}}
            //Token{signal=BEGIN_COMPOSITE, name='Decimal64', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=8, offset=14, componentTokenCount=4, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='Price'}}
            builder.append("mdEntryPx=");
            mdEntryPx().appendTo(builder);
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='NumberOfOrders', referencedName='null', description='null', id=346, version=0, deprecated=0, encodedLength=4, offset=22, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='NumberOfOrders'}}
            //Token{signal=ENCODING, name='uint32', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=4, offset=22, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=UINT32, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='NumberOfOrders'}}
            builder.append("numberOfOrders=");
            builder.append(numberOfOrders());
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='MdEntryTime', referencedName='null', description='null', id=273, version=0, deprecated=0, encodedLength=8, offset=26, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=nanosecond, semanticType='null'}}
            //Token{signal=ENCODING, name='timestamp', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=8, offset=26, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=UINT64, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=nanosecond, semanticType='UTCTimestamp'}}
            builder.append("mdEntryTime=");
            builder.append(mdEntryTime());
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='MdEntrySize', referencedName='null', description='null', id=271, version=0, deprecated=0, encodedLength=4, offset=34, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
            //Token{signal=BEGIN_COMPOSITE, name='IntQty32', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=4, offset=34, componentTokenCount=4, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='Qty'}}
            builder.append("mdEntrySize=");
            mdEntrySize().appendTo(builder);
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='TradingSessionId', referencedName='null', description='null', id=336, version=0, deprecated=0, encodedLength=1, offset=38, componentTokenCount=7, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
            //Token{signal=BEGIN_ENUM, name='MarketStateIdentifier', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=38, componentTokenCount=5, encoding=Encoding{presence=REQUIRED, primitiveType=UINT8, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='int'}}
            builder.append("tradingSessionId=");
            builder.append(tradingSessionId());
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='NetChgPrevDay', referencedName='null', description='null', id=451, version=0, deprecated=0, encodedLength=8, offset=39, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
            //Token{signal=BEGIN_COMPOSITE, name='Decimal64', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=8, offset=39, componentTokenCount=4, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
            builder.append("netChgPrevDay=");
            netChgPrevDay().appendTo(builder);
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='TickDirection', referencedName='null', description='null', id=274, version=0, deprecated=0, encodedLength=1, offset=47, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
            //Token{signal=BEGIN_ENUM, name='TickDirection', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=47, componentTokenCount=4, encoding=Encoding{presence=REQUIRED, primitiveType=UINT8, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='int'}}
            builder.append("tickDirection=");
            builder.append(tickDirection());
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='OpenCloseSettleFlag', referencedName='null', description='null', id=286, version=0, deprecated=0, encodedLength=2, offset=48, componentTokenCount=7, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
            //Token{signal=BEGIN_ENUM, name='OpenCloseSettleFlag', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=2, offset=48, componentTokenCount=5, encoding=Encoding{presence=REQUIRED, primitiveType=UINT16, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='int'}}
            builder.append("openCloseSettleFlag=");
            builder.append(openCloseSettleFlag());
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='SettleDate', referencedName='null', description='null', id=64, version=0, deprecated=0, encodedLength=8, offset=50, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=nanosecond, semanticType='null'}}
            //Token{signal=ENCODING, name='timestamp', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=8, offset=50, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=UINT64, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=nanosecond, semanticType='UTCTimestamp'}}
            builder.append("settleDate=");
            builder.append(settleDate());
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='TradeCondition', referencedName='null', description='null', id=277, version=0, deprecated=0, encodedLength=1, offset=58, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
            //Token{signal=BEGIN_SET, name='TradeCondition', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=58, componentTokenCount=4, encoding=Encoding{presence=REQUIRED, primitiveType=UINT8, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='TradeCondition'}}
            builder.append("tradeCondition=");
            builder.append(tradeCondition());
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='TradeVolume', referencedName='null', description='null', id=1020, version=0, deprecated=0, encodedLength=4, offset=59, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
            //Token{signal=BEGIN_COMPOSITE, name='IntQty32', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=4, offset=59, componentTokenCount=4, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='Qty'}}
            builder.append("tradeVolume=");
            tradeVolume().appendTo(builder);
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='MdQuoteType', referencedName='null', description='null', id=1070, version=0, deprecated=0, encodedLength=1, offset=63, componentTokenCount=5, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
            //Token{signal=BEGIN_ENUM, name='MDQuoteType', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=63, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=UINT8, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='int'}}
            builder.append("mdQuoteType=");
            builder.append(mdQuoteType());
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='FixingBracket', referencedName='null', description='null', id=5790, version=0, deprecated=0, encodedLength=8, offset=64, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=nanosecond, semanticType='null'}}
            //Token{signal=ENCODING, name='timestamp', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=8, offset=64, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=UINT64, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=nanosecond, semanticType='UTCTimestamp'}}
            builder.append("fixingBracket=");
            builder.append(fixingBracket());
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='AggressorSide', referencedName='null', description='null', id=5797, version=0, deprecated=0, encodedLength=1, offset=72, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
            //Token{signal=BEGIN_ENUM, name='Side', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=72, componentTokenCount=4, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
            builder.append("aggressorSide=");
            builder.append(aggressorSide());
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='MatchEventIndicator', referencedName='null', description='null', id=5799, version=0, deprecated=0, encodedLength=1, offset=73, componentTokenCount=8, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
            //Token{signal=BEGIN_ENUM, name='MatchEventIndicator', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=73, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='MatchEventIndicator'}}
            builder.append("matchEventIndicator=");
            builder.append(matchEventIndicator());
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='TradeId', referencedName='null', description='null', id=1003, version=0, deprecated=0, encodedLength=8, offset=74, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='ExecID'}}
            //Token{signal=ENCODING, name='uint64', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=8, offset=74, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=UINT64, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='ExecID'}}
            builder.append("tradeId=");
            builder.append(tradeId());
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
        builder.append("[MarketDataIncrementalRefresh](sbeTemplateId=");
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
        //Token{signal=BEGIN_FIELD, name='TradeDate', referencedName='null', description='null', id=75, version=0, deprecated=0, encodedLength=2, offset=0, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
        //Token{signal=ENCODING, name='LocalMktDate', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=2, offset=0, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=UINT16, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='LocalMktDate'}}
        builder.append("tradeDate=");
        builder.append(tradeDate());
        builder.append('|');
        //Token{signal=BEGIN_GROUP, name='Entries', referencedName='null', description='null', id=268, version=0, deprecated=0, encodedLength=82, offset=2, componentTokenCount=136, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
        builder.append("entries=[");
        EntriesDecoder entries = entries();
        if (entries.count() > 0)
        {
            while (entries.hasNext())
            {
                entries.next().appendTo(builder);
                builder.append(',');
            }
            builder.setLength(builder.length() - 1);
        }
        builder.append(']');

        limit(originalLimit);

        return builder;
    }
}
