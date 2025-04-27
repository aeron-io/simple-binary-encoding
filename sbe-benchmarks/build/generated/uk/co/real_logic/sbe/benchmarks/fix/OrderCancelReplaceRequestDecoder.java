/* Generated SBE (Simple Binary Encoding) message codec */
package uk.co.real_logic.sbe.benchmarks.fix;

import org.agrona.concurrent.UnsafeBuffer;

@SuppressWarnings("all")
public class OrderCancelReplaceRequestDecoder
{
    public static final int BLOCK_LENGTH = 204;
    public static final int TEMPLATE_ID = 71;
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 1;
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private final OrderCancelReplaceRequestDecoder parentMessage = this;
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
        return "G";
    }

    public UnsafeBuffer buffer()
    {
        return buffer;
    }

    public int offset()
    {
        return offset;
    }

    public OrderCancelReplaceRequestDecoder wrap(
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

    public static int accountId()
    {
        return 1;
    }

    public static int accountSinceVersion()
    {
        return 0;
    }

    public static int accountEncodingOffset()
    {
        return 0;
    }

    public static int accountEncodingLength()
    {
        return 12;
    }

    public static String accountMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "";
            case TIME_UNIT: return "";
            case SEMANTIC_TYPE: return "String";
            case PRESENCE: return "required";
        }

        return "";
    }

    public static byte accountNullValue()
    {
        return (byte)0;
    }

    public static byte accountMinValue()
    {
        return (byte)32;
    }

    public static byte accountMaxValue()
    {
        return (byte)126;
    }

    public static int accountLength()
    {
        return 12;
    }

    public byte account(final int index)
    {
        if (index < 0 || index >= 12)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 0 + (index * 1);

        return buffer.getByte(pos);
    }


    public static String accountCharacterEncoding()
    {
        return "US-ASCII";
    }

    public int getAccount(final byte[] dst, final int dstOffset)
    {
        final int length = 12;
        if (dstOffset < 0 || dstOffset > (dst.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + dstOffset);
        }

        buffer.getBytes(this.offset + 0, dst, dstOffset, length);

        return length;
    }

    public String account()
    {
        final byte[] dst = new byte[12];
        buffer.getBytes(this.offset + 0, dst, 0, 12);

        int end = 0;
        for (; end < 12 && dst[end] != 0; ++end);

        return new String(dst, 0, end, java.nio.charset.StandardCharsets.US_ASCII);
    }


    public void getAccount(final Appendable value)
    {
        for (int i = 0; i < 12 ; ++i)
        {
            final int c = buffer.getByte(this.offset + 0 + i) & 0xFF;
            if (c == 0)
            {
                break;
            }
            try
            {
                value.append(c > 127 ? '?' : (char)c);
            }
            catch (final java.io.IOException e)
            {
                throw new java.io.UncheckedIOException(e);
            }
        }
    }


    public static int clOrdIDId()
    {
        return 11;
    }

    public static int clOrdIDSinceVersion()
    {
        return 0;
    }

    public static int clOrdIDEncodingOffset()
    {
        return 12;
    }

    public static int clOrdIDEncodingLength()
    {
        return 20;
    }

    public static String clOrdIDMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "";
            case TIME_UNIT: return "";
            case SEMANTIC_TYPE: return "String";
            case PRESENCE: return "required";
        }

        return "";
    }

    public static byte clOrdIDNullValue()
    {
        return (byte)0;
    }

    public static byte clOrdIDMinValue()
    {
        return (byte)32;
    }

    public static byte clOrdIDMaxValue()
    {
        return (byte)126;
    }

    public static int clOrdIDLength()
    {
        return 20;
    }

    public byte clOrdID(final int index)
    {
        if (index < 0 || index >= 20)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 12 + (index * 1);

        return buffer.getByte(pos);
    }


    public static String clOrdIDCharacterEncoding()
    {
        return "US-ASCII";
    }

    public int getClOrdID(final byte[] dst, final int dstOffset)
    {
        final int length = 20;
        if (dstOffset < 0 || dstOffset > (dst.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + dstOffset);
        }

        buffer.getBytes(this.offset + 12, dst, dstOffset, length);

        return length;
    }

    public String clOrdID()
    {
        final byte[] dst = new byte[20];
        buffer.getBytes(this.offset + 12, dst, 0, 20);

        int end = 0;
        for (; end < 20 && dst[end] != 0; ++end);

        return new String(dst, 0, end, java.nio.charset.StandardCharsets.US_ASCII);
    }


    public void getClOrdID(final Appendable value)
    {
        for (int i = 0; i < 20 ; ++i)
        {
            final int c = buffer.getByte(this.offset + 12 + i) & 0xFF;
            if (c == 0)
            {
                break;
            }
            try
            {
                value.append(c > 127 ? '?' : (char)c);
            }
            catch (final java.io.IOException e)
            {
                throw new java.io.UncheckedIOException(e);
            }
        }
    }


    public static int orderIDId()
    {
        return 37;
    }

    public static int orderIDSinceVersion()
    {
        return 0;
    }

    public static int orderIDEncodingOffset()
    {
        return 32;
    }

    public static int orderIDEncodingLength()
    {
        return 8;
    }

    public static String orderIDMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "";
            case TIME_UNIT: return "";
            case SEMANTIC_TYPE: return "int";
            case PRESENCE: return "required";
        }

        return "";
    }

    public static long orderIDNullValue()
    {
        return -9223372036854775808L;
    }

    public static long orderIDMinValue()
    {
        return -9223372036854775807L;
    }

    public static long orderIDMaxValue()
    {
        return 9223372036854775807L;
    }

    public long orderID()
    {
        return buffer.getLong(offset + 32, java.nio.ByteOrder.LITTLE_ENDIAN);
    }


    public static int handInstId()
    {
        return 21;
    }

    public static int handInstSinceVersion()
    {
        return 0;
    }

    public static int handInstEncodingOffset()
    {
        return 40;
    }

    public static int handInstEncodingLength()
    {
        return 1;
    }

    public static String handInstMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "";
            case TIME_UNIT: return "";
            case SEMANTIC_TYPE: return "char";
            case PRESENCE: return "required";
        }

        return "";
    }

    public HandInst handInst()
    {
        return HandInst.get(buffer.getByte(offset + 40));
    }


    public static int orderQtyId()
    {
        return 38;
    }

    public static int orderQtySinceVersion()
    {
        return 0;
    }

    public static int orderQtyEncodingOffset()
    {
        return 41;
    }

    public static int orderQtyEncodingLength()
    {
        return 4;
    }

    public static String orderQtyMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "";
            case TIME_UNIT: return "";
            case SEMANTIC_TYPE: return "Qty";
            case PRESENCE: return "required";
        }

        return "";
    }

    private final IntQty32Decoder orderQty = new IntQty32Decoder();

    public IntQty32Decoder orderQty()
    {
        orderQty.wrap(buffer, offset + 41);
        return orderQty;
    }

    public static int custOrderHandlingInstId()
    {
        return 1031;
    }

    public static int custOrderHandlingInstSinceVersion()
    {
        return 0;
    }

    public static int custOrderHandlingInstEncodingOffset()
    {
        return 45;
    }

    public static int custOrderHandlingInstEncodingLength()
    {
        return 1;
    }

    public static String custOrderHandlingInstMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "";
            case TIME_UNIT: return "";
            case SEMANTIC_TYPE: return "char";
            case PRESENCE: return "optional";
        }

        return "";
    }

    public CustOrderHandlingInst custOrderHandlingInst()
    {
        return CustOrderHandlingInst.get(buffer.getByte(offset + 45));
    }


    public static int ordTypeId()
    {
        return 40;
    }

    public static int ordTypeSinceVersion()
    {
        return 0;
    }

    public static int ordTypeEncodingOffset()
    {
        return 46;
    }

    public static int ordTypeEncodingLength()
    {
        return 1;
    }

    public static String ordTypeMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "";
            case TIME_UNIT: return "";
            case SEMANTIC_TYPE: return "char";
            case PRESENCE: return "required";
        }

        return "";
    }

    public OrdType ordType()
    {
        return OrdType.get(buffer.getByte(offset + 46));
    }


    public static int origClOrdIDId()
    {
        return 41;
    }

    public static int origClOrdIDSinceVersion()
    {
        return 0;
    }

    public static int origClOrdIDEncodingOffset()
    {
        return 47;
    }

    public static int origClOrdIDEncodingLength()
    {
        return 20;
    }

    public static String origClOrdIDMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "";
            case TIME_UNIT: return "";
            case SEMANTIC_TYPE: return "String";
            case PRESENCE: return "required";
        }

        return "";
    }

    public static byte origClOrdIDNullValue()
    {
        return (byte)0;
    }

    public static byte origClOrdIDMinValue()
    {
        return (byte)32;
    }

    public static byte origClOrdIDMaxValue()
    {
        return (byte)126;
    }

    public static int origClOrdIDLength()
    {
        return 20;
    }

    public byte origClOrdID(final int index)
    {
        if (index < 0 || index >= 20)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 47 + (index * 1);

        return buffer.getByte(pos);
    }


    public static String origClOrdIDCharacterEncoding()
    {
        return "US-ASCII";
    }

    public int getOrigClOrdID(final byte[] dst, final int dstOffset)
    {
        final int length = 20;
        if (dstOffset < 0 || dstOffset > (dst.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + dstOffset);
        }

        buffer.getBytes(this.offset + 47, dst, dstOffset, length);

        return length;
    }

    public String origClOrdID()
    {
        final byte[] dst = new byte[20];
        buffer.getBytes(this.offset + 47, dst, 0, 20);

        int end = 0;
        for (; end < 20 && dst[end] != 0; ++end);

        return new String(dst, 0, end, java.nio.charset.StandardCharsets.US_ASCII);
    }


    public void getOrigClOrdID(final Appendable value)
    {
        for (int i = 0; i < 20 ; ++i)
        {
            final int c = buffer.getByte(this.offset + 47 + i) & 0xFF;
            if (c == 0)
            {
                break;
            }
            try
            {
                value.append(c > 127 ? '?' : (char)c);
            }
            catch (final java.io.IOException e)
            {
                throw new java.io.UncheckedIOException(e);
            }
        }
    }


    public static int priceId()
    {
        return 44;
    }

    public static int priceSinceVersion()
    {
        return 0;
    }

    public static int priceEncodingOffset()
    {
        return 67;
    }

    public static int priceEncodingLength()
    {
        return 9;
    }

    public static String priceMetaAttribute(final MetaAttribute metaAttribute)
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

    private final OptionalPriceDecoder price = new OptionalPriceDecoder();

    public OptionalPriceDecoder price()
    {
        price.wrap(buffer, offset + 67);
        return price;
    }

    public static int sideId()
    {
        return 54;
    }

    public static int sideSinceVersion()
    {
        return 0;
    }

    public static int sideEncodingOffset()
    {
        return 76;
    }

    public static int sideEncodingLength()
    {
        return 1;
    }

    public static String sideMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "";
            case TIME_UNIT: return "";
            case SEMANTIC_TYPE: return "char";
            case PRESENCE: return "required";
        }

        return "";
    }

    public Side side()
    {
        return Side.get(buffer.getByte(offset + 76));
    }


    public static int symbolId()
    {
        return 55;
    }

    public static int symbolSinceVersion()
    {
        return 0;
    }

    public static int symbolEncodingOffset()
    {
        return 77;
    }

    public static int symbolEncodingLength()
    {
        return 6;
    }

    public static String symbolMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "";
            case TIME_UNIT: return "";
            case SEMANTIC_TYPE: return "String";
            case PRESENCE: return "required";
        }

        return "";
    }

    public static byte symbolNullValue()
    {
        return (byte)0;
    }

    public static byte symbolMinValue()
    {
        return (byte)32;
    }

    public static byte symbolMaxValue()
    {
        return (byte)126;
    }

    public static int symbolLength()
    {
        return 6;
    }

    public byte symbol(final int index)
    {
        if (index < 0 || index >= 6)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 77 + (index * 1);

        return buffer.getByte(pos);
    }


    public static String symbolCharacterEncoding()
    {
        return "US-ASCII";
    }

    public int getSymbol(final byte[] dst, final int dstOffset)
    {
        final int length = 6;
        if (dstOffset < 0 || dstOffset > (dst.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + dstOffset);
        }

        buffer.getBytes(this.offset + 77, dst, dstOffset, length);

        return length;
    }

    public String symbol()
    {
        final byte[] dst = new byte[6];
        buffer.getBytes(this.offset + 77, dst, 0, 6);

        int end = 0;
        for (; end < 6 && dst[end] != 0; ++end);

        return new String(dst, 0, end, java.nio.charset.StandardCharsets.US_ASCII);
    }


    public void getSymbol(final Appendable value)
    {
        for (int i = 0; i < 6 ; ++i)
        {
            final int c = buffer.getByte(this.offset + 77 + i) & 0xFF;
            if (c == 0)
            {
                break;
            }
            try
            {
                value.append(c > 127 ? '?' : (char)c);
            }
            catch (final java.io.IOException e)
            {
                throw new java.io.UncheckedIOException(e);
            }
        }
    }


    public static int testId()
    {
        return 58;
    }

    public static int testSinceVersion()
    {
        return 0;
    }

    public static int testEncodingOffset()
    {
        return 83;
    }

    public static int testEncodingLength()
    {
        return 18;
    }

    public static String testMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "";
            case TIME_UNIT: return "";
            case SEMANTIC_TYPE: return "String";
            case PRESENCE: return "required";
        }

        return "";
    }

    public static byte testNullValue()
    {
        return (byte)0;
    }

    public static byte testMinValue()
    {
        return (byte)32;
    }

    public static byte testMaxValue()
    {
        return (byte)126;
    }

    public static int testLength()
    {
        return 18;
    }

    public byte test(final int index)
    {
        if (index < 0 || index >= 18)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 83 + (index * 1);

        return buffer.getByte(pos);
    }


    public static String testCharacterEncoding()
    {
        return "US-ASCII";
    }

    public int getTest(final byte[] dst, final int dstOffset)
    {
        final int length = 18;
        if (dstOffset < 0 || dstOffset > (dst.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + dstOffset);
        }

        buffer.getBytes(this.offset + 83, dst, dstOffset, length);

        return length;
    }

    public String test()
    {
        final byte[] dst = new byte[18];
        buffer.getBytes(this.offset + 83, dst, 0, 18);

        int end = 0;
        for (; end < 18 && dst[end] != 0; ++end);

        return new String(dst, 0, end, java.nio.charset.StandardCharsets.US_ASCII);
    }


    public void getTest(final Appendable value)
    {
        for (int i = 0; i < 18 ; ++i)
        {
            final int c = buffer.getByte(this.offset + 83 + i) & 0xFF;
            if (c == 0)
            {
                break;
            }
            try
            {
                value.append(c > 127 ? '?' : (char)c);
            }
            catch (final java.io.IOException e)
            {
                throw new java.io.UncheckedIOException(e);
            }
        }
    }


    public static int timeInForceId()
    {
        return 59;
    }

    public static int timeInForceSinceVersion()
    {
        return 0;
    }

    public static int timeInForceEncodingOffset()
    {
        return 101;
    }

    public static int timeInForceEncodingLength()
    {
        return 1;
    }

    public static String timeInForceMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "";
            case TIME_UNIT: return "";
            case SEMANTIC_TYPE: return "char";
            case PRESENCE: return "optional";
        }

        return "";
    }

    public TimeInForce timeInForce()
    {
        return TimeInForce.get(buffer.getByte(offset + 101));
    }


    public static int manualOrderIndicatorId()
    {
        return 1028;
    }

    public static int manualOrderIndicatorSinceVersion()
    {
        return 0;
    }

    public static int manualOrderIndicatorEncodingOffset()
    {
        return 102;
    }

    public static int manualOrderIndicatorEncodingLength()
    {
        return 1;
    }

    public static String manualOrderIndicatorMetaAttribute(final MetaAttribute metaAttribute)
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

    public BooleanType manualOrderIndicator()
    {
        return BooleanType.get(((short)(buffer.getByte(offset + 102) & 0xFF)));
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
        return 103;
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
            case SEMANTIC_TYPE: return "UTCTimestamp";
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
        return buffer.getLong(offset + 103, java.nio.ByteOrder.LITTLE_ENDIAN);
    }


    public static int noAllocsId()
    {
        return 78;
    }

    public static int noAllocsSinceVersion()
    {
        return 0;
    }

    public static int noAllocsEncodingOffset()
    {
        return 111;
    }

    public static int noAllocsEncodingLength()
    {
        return 1;
    }

    public static String noAllocsMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "";
            case TIME_UNIT: return "";
            case SEMANTIC_TYPE: return "";
            case PRESENCE: return "optional";
        }

        return "";
    }

    public NoAllocs noAllocs()
    {
        return NoAllocs.get(buffer.getByte(offset + 111));
    }


    public static int allocAccountId()
    {
        return 79;
    }

    public static int allocAccountSinceVersion()
    {
        return 0;
    }

    public static int allocAccountEncodingOffset()
    {
        return 112;
    }

    public static int allocAccountEncodingLength()
    {
        return 10;
    }

    public static String allocAccountMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "";
            case TIME_UNIT: return "";
            case SEMANTIC_TYPE: return "String";
            case PRESENCE: return "required";
        }

        return "";
    }

    public static byte allocAccountNullValue()
    {
        return (byte)0;
    }

    public static byte allocAccountMinValue()
    {
        return (byte)32;
    }

    public static byte allocAccountMaxValue()
    {
        return (byte)126;
    }

    public static int allocAccountLength()
    {
        return 10;
    }

    public byte allocAccount(final int index)
    {
        if (index < 0 || index >= 10)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 112 + (index * 1);

        return buffer.getByte(pos);
    }


    public static String allocAccountCharacterEncoding()
    {
        return "US-ASCII";
    }

    public int getAllocAccount(final byte[] dst, final int dstOffset)
    {
        final int length = 10;
        if (dstOffset < 0 || dstOffset > (dst.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + dstOffset);
        }

        buffer.getBytes(this.offset + 112, dst, dstOffset, length);

        return length;
    }

    public String allocAccount()
    {
        final byte[] dst = new byte[10];
        buffer.getBytes(this.offset + 112, dst, 0, 10);

        int end = 0;
        for (; end < 10 && dst[end] != 0; ++end);

        return new String(dst, 0, end, java.nio.charset.StandardCharsets.US_ASCII);
    }


    public void getAllocAccount(final Appendable value)
    {
        for (int i = 0; i < 10 ; ++i)
        {
            final int c = buffer.getByte(this.offset + 112 + i) & 0xFF;
            if (c == 0)
            {
                break;
            }
            try
            {
                value.append(c > 127 ? '?' : (char)c);
            }
            catch (final java.io.IOException e)
            {
                throw new java.io.UncheckedIOException(e);
            }
        }
    }


    public static int stopPxId()
    {
        return 99;
    }

    public static int stopPxSinceVersion()
    {
        return 0;
    }

    public static int stopPxEncodingOffset()
    {
        return 122;
    }

    public static int stopPxEncodingLength()
    {
        return 9;
    }

    public static String stopPxMetaAttribute(final MetaAttribute metaAttribute)
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

    private final OptionalPriceDecoder stopPx = new OptionalPriceDecoder();

    public OptionalPriceDecoder stopPx()
    {
        stopPx.wrap(buffer, offset + 122);
        return stopPx;
    }

    public static int securityDescId()
    {
        return 107;
    }

    public static int securityDescSinceVersion()
    {
        return 0;
    }

    public static int securityDescEncodingOffset()
    {
        return 131;
    }

    public static int securityDescEncodingLength()
    {
        return 20;
    }

    public static String securityDescMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "";
            case TIME_UNIT: return "";
            case SEMANTIC_TYPE: return "String";
            case PRESENCE: return "required";
        }

        return "";
    }

    public static byte securityDescNullValue()
    {
        return (byte)0;
    }

    public static byte securityDescMinValue()
    {
        return (byte)32;
    }

    public static byte securityDescMaxValue()
    {
        return (byte)126;
    }

    public static int securityDescLength()
    {
        return 20;
    }

    public byte securityDesc(final int index)
    {
        if (index < 0 || index >= 20)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 131 + (index * 1);

        return buffer.getByte(pos);
    }


    public static String securityDescCharacterEncoding()
    {
        return "US-ASCII";
    }

    public int getSecurityDesc(final byte[] dst, final int dstOffset)
    {
        final int length = 20;
        if (dstOffset < 0 || dstOffset > (dst.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + dstOffset);
        }

        buffer.getBytes(this.offset + 131, dst, dstOffset, length);

        return length;
    }

    public String securityDesc()
    {
        final byte[] dst = new byte[20];
        buffer.getBytes(this.offset + 131, dst, 0, 20);

        int end = 0;
        for (; end < 20 && dst[end] != 0; ++end);

        return new String(dst, 0, end, java.nio.charset.StandardCharsets.US_ASCII);
    }


    public void getSecurityDesc(final Appendable value)
    {
        for (int i = 0; i < 20 ; ++i)
        {
            final int c = buffer.getByte(this.offset + 131 + i) & 0xFF;
            if (c == 0)
            {
                break;
            }
            try
            {
                value.append(c > 127 ? '?' : (char)c);
            }
            catch (final java.io.IOException e)
            {
                throw new java.io.UncheckedIOException(e);
            }
        }
    }


    public static int minQtyId()
    {
        return 110;
    }

    public static int minQtySinceVersion()
    {
        return 0;
    }

    public static int minQtyEncodingOffset()
    {
        return 151;
    }

    public static int minQtyEncodingLength()
    {
        return 4;
    }

    public static String minQtyMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "";
            case TIME_UNIT: return "";
            case SEMANTIC_TYPE: return "Qty";
            case PRESENCE: return "required";
        }

        return "";
    }

    private final IntQty32Decoder minQty = new IntQty32Decoder();

    public IntQty32Decoder minQty()
    {
        minQty.wrap(buffer, offset + 151);
        return minQty;
    }

    public static int securityTypeId()
    {
        return 167;
    }

    public static int securityTypeSinceVersion()
    {
        return 0;
    }

    public static int securityTypeEncodingOffset()
    {
        return 155;
    }

    public static int securityTypeEncodingLength()
    {
        return 3;
    }

    public static String securityTypeMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "";
            case TIME_UNIT: return "";
            case SEMANTIC_TYPE: return "String";
            case PRESENCE: return "required";
        }

        return "";
    }

    public static byte securityTypeNullValue()
    {
        return (byte)0;
    }

    public static byte securityTypeMinValue()
    {
        return (byte)32;
    }

    public static byte securityTypeMaxValue()
    {
        return (byte)126;
    }

    public static int securityTypeLength()
    {
        return 3;
    }

    public byte securityType(final int index)
    {
        if (index < 0 || index >= 3)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 155 + (index * 1);

        return buffer.getByte(pos);
    }


    public static String securityTypeCharacterEncoding()
    {
        return "US-ASCII";
    }

    public int getSecurityType(final byte[] dst, final int dstOffset)
    {
        final int length = 3;
        if (dstOffset < 0 || dstOffset > (dst.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + dstOffset);
        }

        buffer.getBytes(this.offset + 155, dst, dstOffset, length);

        return length;
    }

    public String securityType()
    {
        final byte[] dst = new byte[3];
        buffer.getBytes(this.offset + 155, dst, 0, 3);

        int end = 0;
        for (; end < 3 && dst[end] != 0; ++end);

        return new String(dst, 0, end, java.nio.charset.StandardCharsets.US_ASCII);
    }


    public void getSecurityType(final Appendable value)
    {
        for (int i = 0; i < 3 ; ++i)
        {
            final int c = buffer.getByte(this.offset + 155 + i) & 0xFF;
            if (c == 0)
            {
                break;
            }
            try
            {
                value.append(c > 127 ? '?' : (char)c);
            }
            catch (final java.io.IOException e)
            {
                throw new java.io.UncheckedIOException(e);
            }
        }
    }


    public static int customerOrFirmId()
    {
        return 204;
    }

    public static int customerOrFirmSinceVersion()
    {
        return 0;
    }

    public static int customerOrFirmEncodingOffset()
    {
        return 158;
    }

    public static int customerOrFirmEncodingLength()
    {
        return 1;
    }

    public static String customerOrFirmMetaAttribute(final MetaAttribute metaAttribute)
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

    public CustomerOrFirm customerOrFirm()
    {
        return CustomerOrFirm.get(((short)(buffer.getByte(offset + 158) & 0xFF)));
    }


    public static int maxShowId()
    {
        return 210;
    }

    public static int maxShowSinceVersion()
    {
        return 0;
    }

    public static int maxShowEncodingOffset()
    {
        return 159;
    }

    public static int maxShowEncodingLength()
    {
        return 4;
    }

    public static String maxShowMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "";
            case TIME_UNIT: return "";
            case SEMANTIC_TYPE: return "Qty";
            case PRESENCE: return "required";
        }

        return "";
    }

    private final IntQty32Decoder maxShow = new IntQty32Decoder();

    public IntQty32Decoder maxShow()
    {
        maxShow.wrap(buffer, offset + 159);
        return maxShow;
    }

    public static int expireDateId()
    {
        return 432;
    }

    public static int expireDateSinceVersion()
    {
        return 0;
    }

    public static int expireDateEncodingOffset()
    {
        return 163;
    }

    public static int expireDateEncodingLength()
    {
        return 2;
    }

    public static String expireDateMetaAttribute(final MetaAttribute metaAttribute)
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

    public static int expireDateNullValue()
    {
        return 65535;
    }

    public static int expireDateMinValue()
    {
        return 0;
    }

    public static int expireDateMaxValue()
    {
        return 65534;
    }

    public int expireDate()
    {
        return (buffer.getShort(offset + 163, java.nio.ByteOrder.LITTLE_ENDIAN) & 0xFFFF);
    }


    public static int selfMatchPreventionIDId()
    {
        return 7928;
    }

    public static int selfMatchPreventionIDSinceVersion()
    {
        return 0;
    }

    public static int selfMatchPreventionIDEncodingOffset()
    {
        return 165;
    }

    public static int selfMatchPreventionIDEncodingLength()
    {
        return 12;
    }

    public static String selfMatchPreventionIDMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "";
            case TIME_UNIT: return "";
            case SEMANTIC_TYPE: return "String";
            case PRESENCE: return "required";
        }

        return "";
    }

    public static byte selfMatchPreventionIDNullValue()
    {
        return (byte)0;
    }

    public static byte selfMatchPreventionIDMinValue()
    {
        return (byte)32;
    }

    public static byte selfMatchPreventionIDMaxValue()
    {
        return (byte)126;
    }

    public static int selfMatchPreventionIDLength()
    {
        return 12;
    }

    public byte selfMatchPreventionID(final int index)
    {
        if (index < 0 || index >= 12)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 165 + (index * 1);

        return buffer.getByte(pos);
    }


    public static String selfMatchPreventionIDCharacterEncoding()
    {
        return "US-ASCII";
    }

    public int getSelfMatchPreventionID(final byte[] dst, final int dstOffset)
    {
        final int length = 12;
        if (dstOffset < 0 || dstOffset > (dst.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + dstOffset);
        }

        buffer.getBytes(this.offset + 165, dst, dstOffset, length);

        return length;
    }

    public String selfMatchPreventionID()
    {
        final byte[] dst = new byte[12];
        buffer.getBytes(this.offset + 165, dst, 0, 12);

        int end = 0;
        for (; end < 12 && dst[end] != 0; ++end);

        return new String(dst, 0, end, java.nio.charset.StandardCharsets.US_ASCII);
    }


    public void getSelfMatchPreventionID(final Appendable value)
    {
        for (int i = 0; i < 12 ; ++i)
        {
            final int c = buffer.getByte(this.offset + 165 + i) & 0xFF;
            if (c == 0)
            {
                break;
            }
            try
            {
                value.append(c > 127 ? '?' : (char)c);
            }
            catch (final java.io.IOException e)
            {
                throw new java.io.UncheckedIOException(e);
            }
        }
    }


    public static int ctiCodeId()
    {
        return 9702;
    }

    public static int ctiCodeSinceVersion()
    {
        return 0;
    }

    public static int ctiCodeEncodingOffset()
    {
        return 177;
    }

    public static int ctiCodeEncodingLength()
    {
        return 1;
    }

    public static String ctiCodeMetaAttribute(final MetaAttribute metaAttribute)
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

    public CtiCode ctiCode()
    {
        return CtiCode.get(buffer.getByte(offset + 177));
    }


    public static int giveUpFirmId()
    {
        return 9707;
    }

    public static int giveUpFirmSinceVersion()
    {
        return 0;
    }

    public static int giveUpFirmEncodingOffset()
    {
        return 178;
    }

    public static int giveUpFirmEncodingLength()
    {
        return 3;
    }

    public static String giveUpFirmMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "";
            case TIME_UNIT: return "";
            case SEMANTIC_TYPE: return "String";
            case PRESENCE: return "required";
        }

        return "";
    }

    public static byte giveUpFirmNullValue()
    {
        return (byte)0;
    }

    public static byte giveUpFirmMinValue()
    {
        return (byte)32;
    }

    public static byte giveUpFirmMaxValue()
    {
        return (byte)126;
    }

    public static int giveUpFirmLength()
    {
        return 3;
    }

    public byte giveUpFirm(final int index)
    {
        if (index < 0 || index >= 3)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 178 + (index * 1);

        return buffer.getByte(pos);
    }


    public static String giveUpFirmCharacterEncoding()
    {
        return "US-ASCII";
    }

    public int getGiveUpFirm(final byte[] dst, final int dstOffset)
    {
        final int length = 3;
        if (dstOffset < 0 || dstOffset > (dst.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + dstOffset);
        }

        buffer.getBytes(this.offset + 178, dst, dstOffset, length);

        return length;
    }

    public String giveUpFirm()
    {
        final byte[] dst = new byte[3];
        buffer.getBytes(this.offset + 178, dst, 0, 3);

        int end = 0;
        for (; end < 3 && dst[end] != 0; ++end);

        return new String(dst, 0, end, java.nio.charset.StandardCharsets.US_ASCII);
    }


    public void getGiveUpFirm(final Appendable value)
    {
        for (int i = 0; i < 3 ; ++i)
        {
            final int c = buffer.getByte(this.offset + 178 + i) & 0xFF;
            if (c == 0)
            {
                break;
            }
            try
            {
                value.append(c > 127 ? '?' : (char)c);
            }
            catch (final java.io.IOException e)
            {
                throw new java.io.UncheckedIOException(e);
            }
        }
    }


    public static int cmtaGiveupCDId()
    {
        return 9708;
    }

    public static int cmtaGiveupCDSinceVersion()
    {
        return 0;
    }

    public static int cmtaGiveupCDEncodingOffset()
    {
        return 181;
    }

    public static int cmtaGiveupCDEncodingLength()
    {
        return 2;
    }

    public static String cmtaGiveupCDMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "";
            case TIME_UNIT: return "";
            case SEMANTIC_TYPE: return "String";
            case PRESENCE: return "required";
        }

        return "";
    }

    public static byte cmtaGiveupCDNullValue()
    {
        return (byte)0;
    }

    public static byte cmtaGiveupCDMinValue()
    {
        return (byte)32;
    }

    public static byte cmtaGiveupCDMaxValue()
    {
        return (byte)126;
    }

    public static int cmtaGiveupCDLength()
    {
        return 2;
    }

    public byte cmtaGiveupCD(final int index)
    {
        if (index < 0 || index >= 2)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 181 + (index * 1);

        return buffer.getByte(pos);
    }


    public static String cmtaGiveupCDCharacterEncoding()
    {
        return "US-ASCII";
    }

    public int getCmtaGiveupCD(final byte[] dst, final int dstOffset)
    {
        final int length = 2;
        if (dstOffset < 0 || dstOffset > (dst.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + dstOffset);
        }

        buffer.getBytes(this.offset + 181, dst, dstOffset, length);

        return length;
    }

    public String cmtaGiveupCD()
    {
        final byte[] dst = new byte[2];
        buffer.getBytes(this.offset + 181, dst, 0, 2);

        int end = 0;
        for (; end < 2 && dst[end] != 0; ++end);

        return new String(dst, 0, end, java.nio.charset.StandardCharsets.US_ASCII);
    }


    public void getCmtaGiveupCD(final Appendable value)
    {
        for (int i = 0; i < 2 ; ++i)
        {
            final int c = buffer.getByte(this.offset + 181 + i) & 0xFF;
            if (c == 0)
            {
                break;
            }
            try
            {
                value.append(c > 127 ? '?' : (char)c);
            }
            catch (final java.io.IOException e)
            {
                throw new java.io.UncheckedIOException(e);
            }
        }
    }


    public static int correlationClOrdIDId()
    {
        return 9717;
    }

    public static int correlationClOrdIDSinceVersion()
    {
        return 0;
    }

    public static int correlationClOrdIDEncodingOffset()
    {
        return 183;
    }

    public static int correlationClOrdIDEncodingLength()
    {
        return 20;
    }

    public static String correlationClOrdIDMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "";
            case TIME_UNIT: return "";
            case SEMANTIC_TYPE: return "String";
            case PRESENCE: return "required";
        }

        return "";
    }

    public static byte correlationClOrdIDNullValue()
    {
        return (byte)0;
    }

    public static byte correlationClOrdIDMinValue()
    {
        return (byte)32;
    }

    public static byte correlationClOrdIDMaxValue()
    {
        return (byte)126;
    }

    public static int correlationClOrdIDLength()
    {
        return 20;
    }

    public byte correlationClOrdID(final int index)
    {
        if (index < 0 || index >= 20)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 183 + (index * 1);

        return buffer.getByte(pos);
    }


    public static String correlationClOrdIDCharacterEncoding()
    {
        return "US-ASCII";
    }

    public int getCorrelationClOrdID(final byte[] dst, final int dstOffset)
    {
        final int length = 20;
        if (dstOffset < 0 || dstOffset > (dst.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + dstOffset);
        }

        buffer.getBytes(this.offset + 183, dst, dstOffset, length);

        return length;
    }

    public String correlationClOrdID()
    {
        final byte[] dst = new byte[20];
        buffer.getBytes(this.offset + 183, dst, 0, 20);

        int end = 0;
        for (; end < 20 && dst[end] != 0; ++end);

        return new String(dst, 0, end, java.nio.charset.StandardCharsets.US_ASCII);
    }


    public void getCorrelationClOrdID(final Appendable value)
    {
        for (int i = 0; i < 20 ; ++i)
        {
            final int c = buffer.getByte(this.offset + 183 + i) & 0xFF;
            if (c == 0)
            {
                break;
            }
            try
            {
                value.append(c > 127 ? '?' : (char)c);
            }
            catch (final java.io.IOException e)
            {
                throw new java.io.UncheckedIOException(e);
            }
        }
    }


    public static int oFMOverrideId()
    {
        return 9768;
    }

    public static int oFMOverrideSinceVersion()
    {
        return 0;
    }

    public static int oFMOverrideEncodingOffset()
    {
        return 203;
    }

    public static int oFMOverrideEncodingLength()
    {
        return 1;
    }

    public static String oFMOverrideMetaAttribute(final MetaAttribute metaAttribute)
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

    public OFMOverride oFMOverride()
    {
        return OFMOverride.get(buffer.getByte(offset + 203));
    }



    public String toString()
    {
        return appendTo(new StringBuilder(100)).toString();
    }

    public StringBuilder appendTo(final StringBuilder builder)
    {
        final int originalLimit = limit();
        limit(offset + actingBlockLength);
        builder.append("[OrderCancelReplaceRequest](sbeTemplateId=");
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
        //Token{signal=BEGIN_FIELD, name='Account', referencedName='null', description='null', id=1, version=0, deprecated=0, encodedLength=12, offset=0, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='String'}}
        //Token{signal=ENCODING, name='string12', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=12, offset=0, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='US-ASCII', epoch='null', timeUnit=null, semanticType='String'}}
        builder.append("account=");
        for (int i = 0; i < accountLength() && account(i) > 0; i++)
        {
            builder.append((char)account(i));
        }
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='ClOrdID', referencedName='null', description='null', id=11, version=0, deprecated=0, encodedLength=20, offset=12, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='String'}}
        //Token{signal=ENCODING, name='string20', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=20, offset=12, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='US-ASCII', epoch='null', timeUnit=null, semanticType='String'}}
        builder.append("clOrdID=");
        for (int i = 0; i < clOrdIDLength() && clOrdID(i) > 0; i++)
        {
            builder.append((char)clOrdID(i));
        }
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='OrderID', referencedName='null', description='null', id=37, version=0, deprecated=0, encodedLength=8, offset=32, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='int'}}
        //Token{signal=ENCODING, name='int64', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=8, offset=32, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=INT64, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='int'}}
        builder.append("orderID=");
        builder.append(orderID());
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='HandInst', referencedName='null', description='null', id=21, version=0, deprecated=0, encodedLength=1, offset=40, componentTokenCount=5, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='char'}}
        //Token{signal=BEGIN_ENUM, name='HandInst', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=40, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='char'}}
        builder.append("handInst=");
        builder.append(handInst());
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='OrderQty', referencedName='null', description='null', id=38, version=0, deprecated=0, encodedLength=4, offset=41, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='Qty'}}
        //Token{signal=BEGIN_COMPOSITE, name='IntQty32', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=4, offset=41, componentTokenCount=4, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='Qty'}}
        builder.append("orderQty=");
        orderQty().appendTo(builder);
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='CustOrderHandlingInst', referencedName='null', description='null', id=1031, version=0, deprecated=0, encodedLength=1, offset=45, componentTokenCount=17, encoding=Encoding{presence=OPTIONAL, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='char'}}
        //Token{signal=BEGIN_ENUM, name='CustOrderHandlingInst', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=45, componentTokenCount=15, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='char'}}
        builder.append("custOrderHandlingInst=");
        builder.append(custOrderHandlingInst());
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='OrdType', referencedName='null', description='null', id=40, version=0, deprecated=0, encodedLength=1, offset=46, componentTokenCount=9, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='char'}}
        //Token{signal=BEGIN_ENUM, name='OrdType', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=46, componentTokenCount=7, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='char'}}
        builder.append("ordType=");
        builder.append(ordType());
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='OrigClOrdID', referencedName='null', description='null', id=41, version=0, deprecated=0, encodedLength=20, offset=47, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='String'}}
        //Token{signal=ENCODING, name='string20', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=20, offset=47, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='US-ASCII', epoch='null', timeUnit=null, semanticType='String'}}
        builder.append("origClOrdID=");
        for (int i = 0; i < origClOrdIDLength() && origClOrdID(i) > 0; i++)
        {
            builder.append((char)origClOrdID(i));
        }
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='Price', referencedName='null', description='null', id=44, version=0, deprecated=0, encodedLength=9, offset=67, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='Price'}}
        //Token{signal=BEGIN_COMPOSITE, name='OptionalPrice', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=9, offset=67, componentTokenCount=4, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='Price'}}
        builder.append("price=");
        price().appendTo(builder);
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='Side', referencedName='null', description='null', id=54, version=0, deprecated=0, encodedLength=1, offset=76, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='char'}}
        //Token{signal=BEGIN_ENUM, name='Side', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=76, componentTokenCount=4, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='char'}}
        builder.append("side=");
        builder.append(side());
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='Symbol', referencedName='null', description='null', id=55, version=0, deprecated=0, encodedLength=6, offset=77, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='String'}}
        //Token{signal=ENCODING, name='Symbol', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=6, offset=77, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='US-ASCII', epoch='null', timeUnit=null, semanticType='String'}}
        builder.append("symbol=");
        for (int i = 0; i < symbolLength() && symbol(i) > 0; i++)
        {
            builder.append((char)symbol(i));
        }
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='Test', referencedName='null', description='null', id=58, version=0, deprecated=0, encodedLength=18, offset=83, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='String'}}
        //Token{signal=ENCODING, name='string18', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=18, offset=83, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='US-ASCII', epoch='null', timeUnit=null, semanticType='String'}}
        builder.append("test=");
        for (int i = 0; i < testLength() && test(i) > 0; i++)
        {
            builder.append((char)test(i));
        }
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='TimeInForce', referencedName='null', description='null', id=59, version=0, deprecated=0, encodedLength=1, offset=101, componentTokenCount=8, encoding=Encoding{presence=OPTIONAL, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='char'}}
        //Token{signal=BEGIN_ENUM, name='TimeInForce', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=101, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='char'}}
        builder.append("timeInForce=");
        builder.append(timeInForce());
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='ManualOrderIndicator', referencedName='null', description='null', id=1028, version=0, deprecated=0, encodedLength=1, offset=102, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
        //Token{signal=BEGIN_ENUM, name='BooleanType', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=102, componentTokenCount=4, encoding=Encoding{presence=REQUIRED, primitiveType=UINT8, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='Boolean'}}
        builder.append("manualOrderIndicator=");
        builder.append(manualOrderIndicator());
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='TransactTime', referencedName='null', description='null', id=60, version=0, deprecated=0, encodedLength=8, offset=103, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=nanosecond, semanticType='UTCTimestamp'}}
        //Token{signal=ENCODING, name='timestamp', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=8, offset=103, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=UINT64, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=nanosecond, semanticType='UTCTimestamp'}}
        builder.append("transactTime=");
        builder.append(transactTime());
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='NoAllocs', referencedName='null', description='null', id=78, version=0, deprecated=0, encodedLength=1, offset=111, componentTokenCount=5, encoding=Encoding{presence=OPTIONAL, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
        //Token{signal=BEGIN_ENUM, name='NoAllocs', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=111, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='char'}}
        builder.append("noAllocs=");
        builder.append(noAllocs());
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='AllocAccount', referencedName='null', description='null', id=79, version=0, deprecated=0, encodedLength=10, offset=112, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='String'}}
        //Token{signal=ENCODING, name='string10', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=10, offset=112, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='US-ASCII', epoch='null', timeUnit=null, semanticType='String'}}
        builder.append("allocAccount=");
        for (int i = 0; i < allocAccountLength() && allocAccount(i) > 0; i++)
        {
            builder.append((char)allocAccount(i));
        }
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='StopPx', referencedName='null', description='null', id=99, version=0, deprecated=0, encodedLength=9, offset=122, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='Price'}}
        //Token{signal=BEGIN_COMPOSITE, name='OptionalPrice', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=9, offset=122, componentTokenCount=4, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='Price'}}
        builder.append("stopPx=");
        stopPx().appendTo(builder);
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='SecurityDesc', referencedName='null', description='null', id=107, version=0, deprecated=0, encodedLength=20, offset=131, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='String'}}
        //Token{signal=ENCODING, name='string20', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=20, offset=131, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='US-ASCII', epoch='null', timeUnit=null, semanticType='String'}}
        builder.append("securityDesc=");
        for (int i = 0; i < securityDescLength() && securityDesc(i) > 0; i++)
        {
            builder.append((char)securityDesc(i));
        }
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='MinQty', referencedName='null', description='null', id=110, version=0, deprecated=0, encodedLength=4, offset=151, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='Qty'}}
        //Token{signal=BEGIN_COMPOSITE, name='IntQty32', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=4, offset=151, componentTokenCount=4, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='Qty'}}
        builder.append("minQty=");
        minQty().appendTo(builder);
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='SecurityType', referencedName='null', description='null', id=167, version=0, deprecated=0, encodedLength=3, offset=155, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='String'}}
        //Token{signal=ENCODING, name='string3', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=3, offset=155, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='US-ASCII', epoch='null', timeUnit=null, semanticType='String'}}
        builder.append("securityType=");
        for (int i = 0; i < securityTypeLength() && securityType(i) > 0; i++)
        {
            builder.append((char)securityType(i));
        }
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='CustomerOrFirm', referencedName='null', description='null', id=204, version=0, deprecated=0, encodedLength=1, offset=158, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
        //Token{signal=BEGIN_ENUM, name='CustomerOrFirm', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=158, componentTokenCount=4, encoding=Encoding{presence=REQUIRED, primitiveType=UINT8, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='int'}}
        builder.append("customerOrFirm=");
        builder.append(customerOrFirm());
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='MaxShow', referencedName='null', description='null', id=210, version=0, deprecated=0, encodedLength=4, offset=159, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='Qty'}}
        //Token{signal=BEGIN_COMPOSITE, name='IntQty32', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=4, offset=159, componentTokenCount=4, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='Qty'}}
        builder.append("maxShow=");
        maxShow().appendTo(builder);
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='ExpireDate', referencedName='null', description='null', id=432, version=0, deprecated=0, encodedLength=2, offset=163, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
        //Token{signal=ENCODING, name='LocalMktDate', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=2, offset=163, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=UINT16, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='LocalMktDate'}}
        builder.append("expireDate=");
        builder.append(expireDate());
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='SelfMatchPreventionID', referencedName='null', description='null', id=7928, version=0, deprecated=0, encodedLength=12, offset=165, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='String'}}
        //Token{signal=ENCODING, name='string12', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=12, offset=165, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='US-ASCII', epoch='null', timeUnit=null, semanticType='String'}}
        builder.append("selfMatchPreventionID=");
        for (int i = 0; i < selfMatchPreventionIDLength() && selfMatchPreventionID(i) > 0; i++)
        {
            builder.append((char)selfMatchPreventionID(i));
        }
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='CtiCode', referencedName='null', description='null', id=9702, version=0, deprecated=0, encodedLength=1, offset=177, componentTokenCount=8, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
        //Token{signal=BEGIN_ENUM, name='CtiCode', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=177, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='char'}}
        builder.append("ctiCode=");
        builder.append(ctiCode());
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='GiveUpFirm', referencedName='null', description='null', id=9707, version=0, deprecated=0, encodedLength=3, offset=178, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='String'}}
        //Token{signal=ENCODING, name='string3', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=3, offset=178, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='US-ASCII', epoch='null', timeUnit=null, semanticType='String'}}
        builder.append("giveUpFirm=");
        for (int i = 0; i < giveUpFirmLength() && giveUpFirm(i) > 0; i++)
        {
            builder.append((char)giveUpFirm(i));
        }
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='CmtaGiveupCD', referencedName='null', description='null', id=9708, version=0, deprecated=0, encodedLength=2, offset=181, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='String'}}
        //Token{signal=ENCODING, name='string2', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=2, offset=181, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='US-ASCII', epoch='null', timeUnit=null, semanticType='String'}}
        builder.append("cmtaGiveupCD=");
        for (int i = 0; i < cmtaGiveupCDLength() && cmtaGiveupCD(i) > 0; i++)
        {
            builder.append((char)cmtaGiveupCD(i));
        }
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='CorrelationClOrdID', referencedName='null', description='null', id=9717, version=0, deprecated=0, encodedLength=20, offset=183, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='String'}}
        //Token{signal=ENCODING, name='string20', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=20, offset=183, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='US-ASCII', epoch='null', timeUnit=null, semanticType='String'}}
        builder.append("correlationClOrdID=");
        for (int i = 0; i < correlationClOrdIDLength() && correlationClOrdID(i) > 0; i++)
        {
            builder.append((char)correlationClOrdID(i));
        }
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='OFMOverride', referencedName='null', description='null', id=9768, version=0, deprecated=0, encodedLength=1, offset=203, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
        //Token{signal=BEGIN_ENUM, name='OFMOverride', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=203, componentTokenCount=4, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='char'}}
        builder.append("oFMOverride=");
        builder.append(oFMOverride());

        limit(originalLimit);

        return builder;
    }
}
