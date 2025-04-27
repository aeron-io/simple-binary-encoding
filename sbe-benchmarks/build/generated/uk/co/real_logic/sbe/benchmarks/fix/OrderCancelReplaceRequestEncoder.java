/* Generated SBE (Simple Binary Encoding) message codec */
package uk.co.real_logic.sbe.benchmarks.fix;

import org.agrona.concurrent.UnsafeBuffer;

@SuppressWarnings("all")
public class OrderCancelReplaceRequestEncoder
{
    public static final int BLOCK_LENGTH = 204;
    public static final int TEMPLATE_ID = 71;
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 1;
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private final OrderCancelReplaceRequestEncoder parentMessage = this;
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

    public OrderCancelReplaceRequestEncoder wrap(final UnsafeBuffer buffer, final int offset)
    {
        if (buffer != this.buffer)
        {
            this.buffer = buffer;
        }
        this.offset = offset;
        limit(offset + BLOCK_LENGTH);

        return this;
    }

    public OrderCancelReplaceRequestEncoder wrapAndApplyHeader(
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

    public OrderCancelReplaceRequestEncoder account(final int index, final byte value)
    {
        if (index < 0 || index >= 12)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 0 + (index * 1);
        buffer.putByte(pos, value);

        return this;
    }

    public static String accountCharacterEncoding()
    {
        return "US-ASCII";
    }

    public OrderCancelReplaceRequestEncoder putAccount(final byte[] src, final int srcOffset)
    {
        final int length = 12;
        if (srcOffset < 0 || srcOffset > (src.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
        }

        buffer.putBytes(this.offset + 0, src, srcOffset, length);

        return this;
    }

    public OrderCancelReplaceRequestEncoder account(final String src)
    {
        final int length = 12;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("String too large for copy: byte length=" + srcLength);
        }

        buffer.putStringWithoutLengthAscii(this.offset + 0, src);

        for (int start = srcLength; start < length; ++start)
        {
            buffer.putByte(this.offset + 0 + start, (byte)0);
        }

        return this;
    }

    public OrderCancelReplaceRequestEncoder account(final CharSequence src)
    {
        final int length = 12;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("CharSequence too large for copy: byte length=" + srcLength);
        }

        for (int i = 0; i < srcLength; ++i)
        {
            final char charValue = src.charAt(i);
            final byte byteValue = charValue > 127 ? (byte)'?' : (byte)charValue;
            buffer.putByte(this.offset + 0 + i, byteValue);
        }

        for (int i = srcLength; i < length; ++i)
        {
            buffer.putByte(this.offset + 0 + i, (byte)0);
        }

        return this;
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

    public OrderCancelReplaceRequestEncoder clOrdID(final int index, final byte value)
    {
        if (index < 0 || index >= 20)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 12 + (index * 1);
        buffer.putByte(pos, value);

        return this;
    }

    public static String clOrdIDCharacterEncoding()
    {
        return "US-ASCII";
    }

    public OrderCancelReplaceRequestEncoder putClOrdID(final byte[] src, final int srcOffset)
    {
        final int length = 20;
        if (srcOffset < 0 || srcOffset > (src.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
        }

        buffer.putBytes(this.offset + 12, src, srcOffset, length);

        return this;
    }

    public OrderCancelReplaceRequestEncoder clOrdID(final String src)
    {
        final int length = 20;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("String too large for copy: byte length=" + srcLength);
        }

        buffer.putStringWithoutLengthAscii(this.offset + 12, src);

        for (int start = srcLength; start < length; ++start)
        {
            buffer.putByte(this.offset + 12 + start, (byte)0);
        }

        return this;
    }

    public OrderCancelReplaceRequestEncoder clOrdID(final CharSequence src)
    {
        final int length = 20;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("CharSequence too large for copy: byte length=" + srcLength);
        }

        for (int i = 0; i < srcLength; ++i)
        {
            final char charValue = src.charAt(i);
            final byte byteValue = charValue > 127 ? (byte)'?' : (byte)charValue;
            buffer.putByte(this.offset + 12 + i, byteValue);
        }

        for (int i = srcLength; i < length; ++i)
        {
            buffer.putByte(this.offset + 12 + i, (byte)0);
        }

        return this;
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

    public OrderCancelReplaceRequestEncoder orderID(final long value)
    {
        buffer.putLong(offset + 32, value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
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

    public OrderCancelReplaceRequestEncoder handInst(final HandInst value)
    {
        buffer.putByte(offset + 40, value.value());
        return this;
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

    private final IntQty32Encoder orderQty = new IntQty32Encoder();

    public IntQty32Encoder orderQty()
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

    public OrderCancelReplaceRequestEncoder custOrderHandlingInst(final CustOrderHandlingInst value)
    {
        buffer.putByte(offset + 45, value.value());
        return this;
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

    public OrderCancelReplaceRequestEncoder ordType(final OrdType value)
    {
        buffer.putByte(offset + 46, value.value());
        return this;
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

    public OrderCancelReplaceRequestEncoder origClOrdID(final int index, final byte value)
    {
        if (index < 0 || index >= 20)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 47 + (index * 1);
        buffer.putByte(pos, value);

        return this;
    }

    public static String origClOrdIDCharacterEncoding()
    {
        return "US-ASCII";
    }

    public OrderCancelReplaceRequestEncoder putOrigClOrdID(final byte[] src, final int srcOffset)
    {
        final int length = 20;
        if (srcOffset < 0 || srcOffset > (src.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
        }

        buffer.putBytes(this.offset + 47, src, srcOffset, length);

        return this;
    }

    public OrderCancelReplaceRequestEncoder origClOrdID(final String src)
    {
        final int length = 20;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("String too large for copy: byte length=" + srcLength);
        }

        buffer.putStringWithoutLengthAscii(this.offset + 47, src);

        for (int start = srcLength; start < length; ++start)
        {
            buffer.putByte(this.offset + 47 + start, (byte)0);
        }

        return this;
    }

    public OrderCancelReplaceRequestEncoder origClOrdID(final CharSequence src)
    {
        final int length = 20;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("CharSequence too large for copy: byte length=" + srcLength);
        }

        for (int i = 0; i < srcLength; ++i)
        {
            final char charValue = src.charAt(i);
            final byte byteValue = charValue > 127 ? (byte)'?' : (byte)charValue;
            buffer.putByte(this.offset + 47 + i, byteValue);
        }

        for (int i = srcLength; i < length; ++i)
        {
            buffer.putByte(this.offset + 47 + i, (byte)0);
        }

        return this;
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

    private final OptionalPriceEncoder price = new OptionalPriceEncoder();

    public OptionalPriceEncoder price()
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

    public OrderCancelReplaceRequestEncoder side(final Side value)
    {
        buffer.putByte(offset + 76, value.value());
        return this;
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

    public OrderCancelReplaceRequestEncoder symbol(final int index, final byte value)
    {
        if (index < 0 || index >= 6)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 77 + (index * 1);
        buffer.putByte(pos, value);

        return this;
    }

    public static String symbolCharacterEncoding()
    {
        return "US-ASCII";
    }

    public OrderCancelReplaceRequestEncoder putSymbol(final byte[] src, final int srcOffset)
    {
        final int length = 6;
        if (srcOffset < 0 || srcOffset > (src.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
        }

        buffer.putBytes(this.offset + 77, src, srcOffset, length);

        return this;
    }

    public OrderCancelReplaceRequestEncoder symbol(final String src)
    {
        final int length = 6;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("String too large for copy: byte length=" + srcLength);
        }

        buffer.putStringWithoutLengthAscii(this.offset + 77, src);

        for (int start = srcLength; start < length; ++start)
        {
            buffer.putByte(this.offset + 77 + start, (byte)0);
        }

        return this;
    }

    public OrderCancelReplaceRequestEncoder symbol(final CharSequence src)
    {
        final int length = 6;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("CharSequence too large for copy: byte length=" + srcLength);
        }

        for (int i = 0; i < srcLength; ++i)
        {
            final char charValue = src.charAt(i);
            final byte byteValue = charValue > 127 ? (byte)'?' : (byte)charValue;
            buffer.putByte(this.offset + 77 + i, byteValue);
        }

        for (int i = srcLength; i < length; ++i)
        {
            buffer.putByte(this.offset + 77 + i, (byte)0);
        }

        return this;
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

    public OrderCancelReplaceRequestEncoder test(final int index, final byte value)
    {
        if (index < 0 || index >= 18)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 83 + (index * 1);
        buffer.putByte(pos, value);

        return this;
    }

    public static String testCharacterEncoding()
    {
        return "US-ASCII";
    }

    public OrderCancelReplaceRequestEncoder putTest(final byte[] src, final int srcOffset)
    {
        final int length = 18;
        if (srcOffset < 0 || srcOffset > (src.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
        }

        buffer.putBytes(this.offset + 83, src, srcOffset, length);

        return this;
    }

    public OrderCancelReplaceRequestEncoder test(final String src)
    {
        final int length = 18;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("String too large for copy: byte length=" + srcLength);
        }

        buffer.putStringWithoutLengthAscii(this.offset + 83, src);

        for (int start = srcLength; start < length; ++start)
        {
            buffer.putByte(this.offset + 83 + start, (byte)0);
        }

        return this;
    }

    public OrderCancelReplaceRequestEncoder test(final CharSequence src)
    {
        final int length = 18;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("CharSequence too large for copy: byte length=" + srcLength);
        }

        for (int i = 0; i < srcLength; ++i)
        {
            final char charValue = src.charAt(i);
            final byte byteValue = charValue > 127 ? (byte)'?' : (byte)charValue;
            buffer.putByte(this.offset + 83 + i, byteValue);
        }

        for (int i = srcLength; i < length; ++i)
        {
            buffer.putByte(this.offset + 83 + i, (byte)0);
        }

        return this;
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

    public OrderCancelReplaceRequestEncoder timeInForce(final TimeInForce value)
    {
        buffer.putByte(offset + 101, value.value());
        return this;
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

    public OrderCancelReplaceRequestEncoder manualOrderIndicator(final BooleanType value)
    {
        buffer.putByte(offset + 102, (byte)value.value());
        return this;
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

    public OrderCancelReplaceRequestEncoder transactTime(final long value)
    {
        buffer.putLong(offset + 103, value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
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

    public OrderCancelReplaceRequestEncoder noAllocs(final NoAllocs value)
    {
        buffer.putByte(offset + 111, value.value());
        return this;
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

    public OrderCancelReplaceRequestEncoder allocAccount(final int index, final byte value)
    {
        if (index < 0 || index >= 10)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 112 + (index * 1);
        buffer.putByte(pos, value);

        return this;
    }

    public static String allocAccountCharacterEncoding()
    {
        return "US-ASCII";
    }

    public OrderCancelReplaceRequestEncoder putAllocAccount(final byte[] src, final int srcOffset)
    {
        final int length = 10;
        if (srcOffset < 0 || srcOffset > (src.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
        }

        buffer.putBytes(this.offset + 112, src, srcOffset, length);

        return this;
    }

    public OrderCancelReplaceRequestEncoder allocAccount(final String src)
    {
        final int length = 10;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("String too large for copy: byte length=" + srcLength);
        }

        buffer.putStringWithoutLengthAscii(this.offset + 112, src);

        for (int start = srcLength; start < length; ++start)
        {
            buffer.putByte(this.offset + 112 + start, (byte)0);
        }

        return this;
    }

    public OrderCancelReplaceRequestEncoder allocAccount(final CharSequence src)
    {
        final int length = 10;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("CharSequence too large for copy: byte length=" + srcLength);
        }

        for (int i = 0; i < srcLength; ++i)
        {
            final char charValue = src.charAt(i);
            final byte byteValue = charValue > 127 ? (byte)'?' : (byte)charValue;
            buffer.putByte(this.offset + 112 + i, byteValue);
        }

        for (int i = srcLength; i < length; ++i)
        {
            buffer.putByte(this.offset + 112 + i, (byte)0);
        }

        return this;
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

    private final OptionalPriceEncoder stopPx = new OptionalPriceEncoder();

    public OptionalPriceEncoder stopPx()
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

    public OrderCancelReplaceRequestEncoder securityDesc(final int index, final byte value)
    {
        if (index < 0 || index >= 20)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 131 + (index * 1);
        buffer.putByte(pos, value);

        return this;
    }

    public static String securityDescCharacterEncoding()
    {
        return "US-ASCII";
    }

    public OrderCancelReplaceRequestEncoder putSecurityDesc(final byte[] src, final int srcOffset)
    {
        final int length = 20;
        if (srcOffset < 0 || srcOffset > (src.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
        }

        buffer.putBytes(this.offset + 131, src, srcOffset, length);

        return this;
    }

    public OrderCancelReplaceRequestEncoder securityDesc(final String src)
    {
        final int length = 20;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("String too large for copy: byte length=" + srcLength);
        }

        buffer.putStringWithoutLengthAscii(this.offset + 131, src);

        for (int start = srcLength; start < length; ++start)
        {
            buffer.putByte(this.offset + 131 + start, (byte)0);
        }

        return this;
    }

    public OrderCancelReplaceRequestEncoder securityDesc(final CharSequence src)
    {
        final int length = 20;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("CharSequence too large for copy: byte length=" + srcLength);
        }

        for (int i = 0; i < srcLength; ++i)
        {
            final char charValue = src.charAt(i);
            final byte byteValue = charValue > 127 ? (byte)'?' : (byte)charValue;
            buffer.putByte(this.offset + 131 + i, byteValue);
        }

        for (int i = srcLength; i < length; ++i)
        {
            buffer.putByte(this.offset + 131 + i, (byte)0);
        }

        return this;
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

    private final IntQty32Encoder minQty = new IntQty32Encoder();

    public IntQty32Encoder minQty()
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

    public OrderCancelReplaceRequestEncoder securityType(final int index, final byte value)
    {
        if (index < 0 || index >= 3)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 155 + (index * 1);
        buffer.putByte(pos, value);

        return this;
    }

    public static String securityTypeCharacterEncoding()
    {
        return "US-ASCII";
    }

    public OrderCancelReplaceRequestEncoder putSecurityType(final byte[] src, final int srcOffset)
    {
        final int length = 3;
        if (srcOffset < 0 || srcOffset > (src.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
        }

        buffer.putBytes(this.offset + 155, src, srcOffset, length);

        return this;
    }

    public OrderCancelReplaceRequestEncoder securityType(final String src)
    {
        final int length = 3;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("String too large for copy: byte length=" + srcLength);
        }

        buffer.putStringWithoutLengthAscii(this.offset + 155, src);

        for (int start = srcLength; start < length; ++start)
        {
            buffer.putByte(this.offset + 155 + start, (byte)0);
        }

        return this;
    }

    public OrderCancelReplaceRequestEncoder securityType(final CharSequence src)
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
            buffer.putByte(this.offset + 155 + i, byteValue);
        }

        for (int i = srcLength; i < length; ++i)
        {
            buffer.putByte(this.offset + 155 + i, (byte)0);
        }

        return this;
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

    public OrderCancelReplaceRequestEncoder customerOrFirm(final CustomerOrFirm value)
    {
        buffer.putByte(offset + 158, (byte)value.value());
        return this;
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

    private final IntQty32Encoder maxShow = new IntQty32Encoder();

    public IntQty32Encoder maxShow()
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

    public OrderCancelReplaceRequestEncoder expireDate(final int value)
    {
        buffer.putShort(offset + 163, (short)value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
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

    public OrderCancelReplaceRequestEncoder selfMatchPreventionID(final int index, final byte value)
    {
        if (index < 0 || index >= 12)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 165 + (index * 1);
        buffer.putByte(pos, value);

        return this;
    }

    public static String selfMatchPreventionIDCharacterEncoding()
    {
        return "US-ASCII";
    }

    public OrderCancelReplaceRequestEncoder putSelfMatchPreventionID(final byte[] src, final int srcOffset)
    {
        final int length = 12;
        if (srcOffset < 0 || srcOffset > (src.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
        }

        buffer.putBytes(this.offset + 165, src, srcOffset, length);

        return this;
    }

    public OrderCancelReplaceRequestEncoder selfMatchPreventionID(final String src)
    {
        final int length = 12;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("String too large for copy: byte length=" + srcLength);
        }

        buffer.putStringWithoutLengthAscii(this.offset + 165, src);

        for (int start = srcLength; start < length; ++start)
        {
            buffer.putByte(this.offset + 165 + start, (byte)0);
        }

        return this;
    }

    public OrderCancelReplaceRequestEncoder selfMatchPreventionID(final CharSequence src)
    {
        final int length = 12;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("CharSequence too large for copy: byte length=" + srcLength);
        }

        for (int i = 0; i < srcLength; ++i)
        {
            final char charValue = src.charAt(i);
            final byte byteValue = charValue > 127 ? (byte)'?' : (byte)charValue;
            buffer.putByte(this.offset + 165 + i, byteValue);
        }

        for (int i = srcLength; i < length; ++i)
        {
            buffer.putByte(this.offset + 165 + i, (byte)0);
        }

        return this;
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

    public OrderCancelReplaceRequestEncoder ctiCode(final CtiCode value)
    {
        buffer.putByte(offset + 177, value.value());
        return this;
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

    public OrderCancelReplaceRequestEncoder giveUpFirm(final int index, final byte value)
    {
        if (index < 0 || index >= 3)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 178 + (index * 1);
        buffer.putByte(pos, value);

        return this;
    }

    public static String giveUpFirmCharacterEncoding()
    {
        return "US-ASCII";
    }

    public OrderCancelReplaceRequestEncoder putGiveUpFirm(final byte[] src, final int srcOffset)
    {
        final int length = 3;
        if (srcOffset < 0 || srcOffset > (src.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
        }

        buffer.putBytes(this.offset + 178, src, srcOffset, length);

        return this;
    }

    public OrderCancelReplaceRequestEncoder giveUpFirm(final String src)
    {
        final int length = 3;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("String too large for copy: byte length=" + srcLength);
        }

        buffer.putStringWithoutLengthAscii(this.offset + 178, src);

        for (int start = srcLength; start < length; ++start)
        {
            buffer.putByte(this.offset + 178 + start, (byte)0);
        }

        return this;
    }

    public OrderCancelReplaceRequestEncoder giveUpFirm(final CharSequence src)
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
            buffer.putByte(this.offset + 178 + i, byteValue);
        }

        for (int i = srcLength; i < length; ++i)
        {
            buffer.putByte(this.offset + 178 + i, (byte)0);
        }

        return this;
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

    public OrderCancelReplaceRequestEncoder cmtaGiveupCD(final int index, final byte value)
    {
        if (index < 0 || index >= 2)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 181 + (index * 1);
        buffer.putByte(pos, value);

        return this;
    }

    public static String cmtaGiveupCDCharacterEncoding()
    {
        return "US-ASCII";
    }

    public OrderCancelReplaceRequestEncoder putCmtaGiveupCD(final byte[] src, final int srcOffset)
    {
        final int length = 2;
        if (srcOffset < 0 || srcOffset > (src.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
        }

        buffer.putBytes(this.offset + 181, src, srcOffset, length);

        return this;
    }

    public OrderCancelReplaceRequestEncoder cmtaGiveupCD(final String src)
    {
        final int length = 2;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("String too large for copy: byte length=" + srcLength);
        }

        buffer.putStringWithoutLengthAscii(this.offset + 181, src);

        for (int start = srcLength; start < length; ++start)
        {
            buffer.putByte(this.offset + 181 + start, (byte)0);
        }

        return this;
    }

    public OrderCancelReplaceRequestEncoder cmtaGiveupCD(final CharSequence src)
    {
        final int length = 2;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("CharSequence too large for copy: byte length=" + srcLength);
        }

        for (int i = 0; i < srcLength; ++i)
        {
            final char charValue = src.charAt(i);
            final byte byteValue = charValue > 127 ? (byte)'?' : (byte)charValue;
            buffer.putByte(this.offset + 181 + i, byteValue);
        }

        for (int i = srcLength; i < length; ++i)
        {
            buffer.putByte(this.offset + 181 + i, (byte)0);
        }

        return this;
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

    public OrderCancelReplaceRequestEncoder correlationClOrdID(final int index, final byte value)
    {
        if (index < 0 || index >= 20)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 183 + (index * 1);
        buffer.putByte(pos, value);

        return this;
    }

    public static String correlationClOrdIDCharacterEncoding()
    {
        return "US-ASCII";
    }

    public OrderCancelReplaceRequestEncoder putCorrelationClOrdID(final byte[] src, final int srcOffset)
    {
        final int length = 20;
        if (srcOffset < 0 || srcOffset > (src.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
        }

        buffer.putBytes(this.offset + 183, src, srcOffset, length);

        return this;
    }

    public OrderCancelReplaceRequestEncoder correlationClOrdID(final String src)
    {
        final int length = 20;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("String too large for copy: byte length=" + srcLength);
        }

        buffer.putStringWithoutLengthAscii(this.offset + 183, src);

        for (int start = srcLength; start < length; ++start)
        {
            buffer.putByte(this.offset + 183 + start, (byte)0);
        }

        return this;
    }

    public OrderCancelReplaceRequestEncoder correlationClOrdID(final CharSequence src)
    {
        final int length = 20;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("CharSequence too large for copy: byte length=" + srcLength);
        }

        for (int i = 0; i < srcLength; ++i)
        {
            final char charValue = src.charAt(i);
            final byte byteValue = charValue > 127 ? (byte)'?' : (byte)charValue;
            buffer.putByte(this.offset + 183 + i, byteValue);
        }

        for (int i = srcLength; i < length; ++i)
        {
            buffer.putByte(this.offset + 183 + i, (byte)0);
        }

        return this;
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

    public OrderCancelReplaceRequestEncoder oFMOverride(final OFMOverride value)
    {
        buffer.putByte(offset + 203, value.value());
        return this;
    }


    public String toString()
    {
        return appendTo(new StringBuilder(100)).toString();
    }

    public StringBuilder appendTo(final StringBuilder builder)
    {
        OrderCancelReplaceRequestDecoder writer = new OrderCancelReplaceRequestDecoder();
        writer.wrap(buffer, offset, BLOCK_LENGTH, SCHEMA_VERSION);

        return writer.appendTo(builder);
    }
}
