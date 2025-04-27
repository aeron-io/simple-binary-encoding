/* Generated SBE (Simple Binary Encoding) message codec */
package uk.co.real_logic.sbe.benchmarks.fix;

import org.agrona.concurrent.UnsafeBuffer;

@SuppressWarnings("all")
public class MassQuoteEncoder
{
    public static final int BLOCK_LENGTH = 62;
    public static final int TEMPLATE_ID = 105;
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 1;
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private final MassQuoteEncoder parentMessage = this;
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
        return "i";
    }

    public UnsafeBuffer buffer()
    {
        return buffer;
    }

    public int offset()
    {
        return offset;
    }

    public MassQuoteEncoder wrap(final UnsafeBuffer buffer, final int offset)
    {
        if (buffer != this.buffer)
        {
            this.buffer = buffer;
        }
        this.offset = offset;
        limit(offset + BLOCK_LENGTH);

        return this;
    }

    public MassQuoteEncoder wrapAndApplyHeader(
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

    public static int quoteReqIDId()
    {
        return 131;
    }

    public static int quoteReqIDSinceVersion()
    {
        return 0;
    }

    public static int quoteReqIDEncodingOffset()
    {
        return 0;
    }

    public static int quoteReqIDEncodingLength()
    {
        return 23;
    }

    public static String quoteReqIDMetaAttribute(final MetaAttribute metaAttribute)
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

    public static byte quoteReqIDNullValue()
    {
        return (byte)0;
    }

    public static byte quoteReqIDMinValue()
    {
        return (byte)32;
    }

    public static byte quoteReqIDMaxValue()
    {
        return (byte)126;
    }

    public static int quoteReqIDLength()
    {
        return 23;
    }

    public MassQuoteEncoder quoteReqID(final int index, final byte value)
    {
        if (index < 0 || index >= 23)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 0 + (index * 1);
        buffer.putByte(pos, value);

        return this;
    }

    public static String quoteReqIDCharacterEncoding()
    {
        return "US-ASCII";
    }

    public MassQuoteEncoder putQuoteReqID(final byte[] src, final int srcOffset)
    {
        final int length = 23;
        if (srcOffset < 0 || srcOffset > (src.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
        }

        buffer.putBytes(this.offset + 0, src, srcOffset, length);

        return this;
    }

    public MassQuoteEncoder quoteReqID(final String src)
    {
        final int length = 23;
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

    public MassQuoteEncoder quoteReqID(final CharSequence src)
    {
        final int length = 23;
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

    public static int quoteIDId()
    {
        return 117;
    }

    public static int quoteIDSinceVersion()
    {
        return 0;
    }

    public static int quoteIDEncodingOffset()
    {
        return 23;
    }

    public static int quoteIDEncodingLength()
    {
        return 10;
    }

    public static String quoteIDMetaAttribute(final MetaAttribute metaAttribute)
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

    public static byte quoteIDNullValue()
    {
        return (byte)0;
    }

    public static byte quoteIDMinValue()
    {
        return (byte)32;
    }

    public static byte quoteIDMaxValue()
    {
        return (byte)126;
    }

    public static int quoteIDLength()
    {
        return 10;
    }

    public MassQuoteEncoder quoteID(final int index, final byte value)
    {
        if (index < 0 || index >= 10)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 23 + (index * 1);
        buffer.putByte(pos, value);

        return this;
    }

    public static String quoteIDCharacterEncoding()
    {
        return "US-ASCII";
    }

    public MassQuoteEncoder putQuoteID(final byte[] src, final int srcOffset)
    {
        final int length = 10;
        if (srcOffset < 0 || srcOffset > (src.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
        }

        buffer.putBytes(this.offset + 23, src, srcOffset, length);

        return this;
    }

    public MassQuoteEncoder quoteID(final String src)
    {
        final int length = 10;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("String too large for copy: byte length=" + srcLength);
        }

        buffer.putStringWithoutLengthAscii(this.offset + 23, src);

        for (int start = srcLength; start < length; ++start)
        {
            buffer.putByte(this.offset + 23 + start, (byte)0);
        }

        return this;
    }

    public MassQuoteEncoder quoteID(final CharSequence src)
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
            buffer.putByte(this.offset + 23 + i, byteValue);
        }

        for (int i = srcLength; i < length; ++i)
        {
            buffer.putByte(this.offset + 23 + i, (byte)0);
        }

        return this;
    }

    public static int mMAccountId()
    {
        return 9771;
    }

    public static int mMAccountSinceVersion()
    {
        return 0;
    }

    public static int mMAccountEncodingOffset()
    {
        return 33;
    }

    public static int mMAccountEncodingLength()
    {
        return 12;
    }

    public static String mMAccountMetaAttribute(final MetaAttribute metaAttribute)
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

    public static byte mMAccountNullValue()
    {
        return (byte)0;
    }

    public static byte mMAccountMinValue()
    {
        return (byte)32;
    }

    public static byte mMAccountMaxValue()
    {
        return (byte)126;
    }

    public static int mMAccountLength()
    {
        return 12;
    }

    public MassQuoteEncoder mMAccount(final int index, final byte value)
    {
        if (index < 0 || index >= 12)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 33 + (index * 1);
        buffer.putByte(pos, value);

        return this;
    }

    public static String mMAccountCharacterEncoding()
    {
        return "US-ASCII";
    }

    public MassQuoteEncoder putMMAccount(final byte[] src, final int srcOffset)
    {
        final int length = 12;
        if (srcOffset < 0 || srcOffset > (src.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
        }

        buffer.putBytes(this.offset + 33, src, srcOffset, length);

        return this;
    }

    public MassQuoteEncoder mMAccount(final String src)
    {
        final int length = 12;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("String too large for copy: byte length=" + srcLength);
        }

        buffer.putStringWithoutLengthAscii(this.offset + 33, src);

        for (int start = srcLength; start < length; ++start)
        {
            buffer.putByte(this.offset + 33 + start, (byte)0);
        }

        return this;
    }

    public MassQuoteEncoder mMAccount(final CharSequence src)
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
            buffer.putByte(this.offset + 33 + i, byteValue);
        }

        for (int i = srcLength; i < length; ++i)
        {
            buffer.putByte(this.offset + 33 + i, (byte)0);
        }

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
        return 45;
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

    public MassQuoteEncoder manualOrderIndicator(final BooleanType value)
    {
        buffer.putByte(offset + 45, (byte)value.value());
        return this;
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
        return 46;
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

    public MassQuoteEncoder custOrderHandlingInst(final CustOrderHandlingInst value)
    {
        buffer.putByte(offset + 46, value.value());
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
        return 47;
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

    public MassQuoteEncoder customerOrFirm(final CustomerOrFirm value)
    {
        buffer.putByte(offset + 47, (byte)value.value());
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
        return 48;
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

    public MassQuoteEncoder selfMatchPreventionID(final int index, final byte value)
    {
        if (index < 0 || index >= 12)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 48 + (index * 1);
        buffer.putByte(pos, value);

        return this;
    }

    public static String selfMatchPreventionIDCharacterEncoding()
    {
        return "US-ASCII";
    }

    public MassQuoteEncoder putSelfMatchPreventionID(final byte[] src, final int srcOffset)
    {
        final int length = 12;
        if (srcOffset < 0 || srcOffset > (src.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
        }

        buffer.putBytes(this.offset + 48, src, srcOffset, length);

        return this;
    }

    public MassQuoteEncoder selfMatchPreventionID(final String src)
    {
        final int length = 12;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("String too large for copy: byte length=" + srcLength);
        }

        buffer.putStringWithoutLengthAscii(this.offset + 48, src);

        for (int start = srcLength; start < length; ++start)
        {
            buffer.putByte(this.offset + 48 + start, (byte)0);
        }

        return this;
    }

    public MassQuoteEncoder selfMatchPreventionID(final CharSequence src)
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
            buffer.putByte(this.offset + 48 + i, byteValue);
        }

        for (int i = srcLength; i < length; ++i)
        {
            buffer.putByte(this.offset + 48 + i, (byte)0);
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
        return 60;
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

    public MassQuoteEncoder ctiCode(final CtiCode value)
    {
        buffer.putByte(offset + 60, value.value());
        return this;
    }

    public static int mMProtectionResetId()
    {
        return 9773;
    }

    public static int mMProtectionResetSinceVersion()
    {
        return 0;
    }

    public static int mMProtectionResetEncodingOffset()
    {
        return 61;
    }

    public static int mMProtectionResetEncodingLength()
    {
        return 1;
    }

    public static String mMProtectionResetMetaAttribute(final MetaAttribute metaAttribute)
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

    public MassQuoteEncoder mMProtectionReset(final MMProtectionReset value)
    {
        buffer.putByte(offset + 61, value.value());
        return this;
    }

    private final QuoteSetsEncoder quoteSets = new QuoteSetsEncoder(this);

    public static long quoteSetsId()
    {
        return 296;
    }

    public QuoteSetsEncoder quoteSetsCount(final int count)
    {
        quoteSets.wrap(buffer, count);
        return quoteSets;
    }

    public static class QuoteSetsEncoder
    {
        public static final int HEADER_SIZE = 4;
        private final MassQuoteEncoder parentMessage;
        private UnsafeBuffer buffer;
        private int count;
        private int index;
        private int offset;
        private final QuoteEntriesEncoder quoteEntries;

        QuoteSetsEncoder(final MassQuoteEncoder parentMessage)
        {
            this.parentMessage = parentMessage;
            quoteEntries = new QuoteEntriesEncoder(parentMessage);
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
            buffer.putShort(limit + 0, (short)(int)24, java.nio.ByteOrder.LITTLE_ENDIAN);
            buffer.putShort(limit + 2, (short)(int)count, java.nio.ByteOrder.LITTLE_ENDIAN);
        }

        public static int sbeHeaderSize()
        {
            return HEADER_SIZE;
        }

        public static int sbeBlockLength()
        {
            return 24;
        }

        public QuoteSetsEncoder next()
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

        public static int quoteSetIDId()
        {
            return 302;
        }

        public static int quoteSetIDSinceVersion()
        {
            return 0;
        }

        public static int quoteSetIDEncodingOffset()
        {
            return 0;
        }

        public static int quoteSetIDEncodingLength()
        {
            return 3;
        }

        public static String quoteSetIDMetaAttribute(final MetaAttribute metaAttribute)
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

        public static byte quoteSetIDNullValue()
        {
            return (byte)0;
        }

        public static byte quoteSetIDMinValue()
        {
            return (byte)32;
        }

        public static byte quoteSetIDMaxValue()
        {
            return (byte)126;
        }

        public static int quoteSetIDLength()
        {
            return 3;
        }

        public QuoteSetsEncoder quoteSetID(final int index, final byte value)
        {
            if (index < 0 || index >= 3)
            {
                throw new IndexOutOfBoundsException("index out of range: index=" + index);
            }

            final int pos = this.offset + 0 + (index * 1);
            buffer.putByte(pos, value);

            return this;
        }

        public static String quoteSetIDCharacterEncoding()
        {
            return "US-ASCII";
        }

        public QuoteSetsEncoder putQuoteSetID(final byte[] src, final int srcOffset)
        {
            final int length = 3;
            if (srcOffset < 0 || srcOffset > (src.length - length))
            {
                throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
            }

            buffer.putBytes(this.offset + 0, src, srcOffset, length);

            return this;
        }

        public QuoteSetsEncoder quoteSetID(final String src)
        {
            final int length = 3;
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

        public QuoteSetsEncoder quoteSetID(final CharSequence src)
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
                buffer.putByte(this.offset + 0 + i, byteValue);
            }

            for (int i = srcLength; i < length; ++i)
            {
                buffer.putByte(this.offset + 0 + i, (byte)0);
            }

            return this;
        }

        public static int underlyingSecurityDescId()
        {
            return 307;
        }

        public static int underlyingSecurityDescSinceVersion()
        {
            return 0;
        }

        public static int underlyingSecurityDescEncodingOffset()
        {
            return 3;
        }

        public static int underlyingSecurityDescEncodingLength()
        {
            return 20;
        }

        public static String underlyingSecurityDescMetaAttribute(final MetaAttribute metaAttribute)
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

        public static byte underlyingSecurityDescNullValue()
        {
            return (byte)0;
        }

        public static byte underlyingSecurityDescMinValue()
        {
            return (byte)32;
        }

        public static byte underlyingSecurityDescMaxValue()
        {
            return (byte)126;
        }

        public static int underlyingSecurityDescLength()
        {
            return 20;
        }

        public QuoteSetsEncoder underlyingSecurityDesc(final int index, final byte value)
        {
            if (index < 0 || index >= 20)
            {
                throw new IndexOutOfBoundsException("index out of range: index=" + index);
            }

            final int pos = this.offset + 3 + (index * 1);
            buffer.putByte(pos, value);

            return this;
        }

        public static String underlyingSecurityDescCharacterEncoding()
        {
            return "US-ASCII";
        }

        public QuoteSetsEncoder putUnderlyingSecurityDesc(final byte[] src, final int srcOffset)
        {
            final int length = 20;
            if (srcOffset < 0 || srcOffset > (src.length - length))
            {
                throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
            }

            buffer.putBytes(this.offset + 3, src, srcOffset, length);

            return this;
        }

        public QuoteSetsEncoder underlyingSecurityDesc(final String src)
        {
            final int length = 20;
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

        public QuoteSetsEncoder underlyingSecurityDesc(final CharSequence src)
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
                buffer.putByte(this.offset + 3 + i, byteValue);
            }

            for (int i = srcLength; i < length; ++i)
            {
                buffer.putByte(this.offset + 3 + i, (byte)0);
            }

            return this;
        }

        public static int totQuoteEntriesId()
        {
            return 304;
        }

        public static int totQuoteEntriesSinceVersion()
        {
            return 0;
        }

        public static int totQuoteEntriesEncodingOffset()
        {
            return 23;
        }

        public static int totQuoteEntriesEncodingLength()
        {
            return 1;
        }

        public static String totQuoteEntriesMetaAttribute(final MetaAttribute metaAttribute)
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

        public static short totQuoteEntriesNullValue()
        {
            return (short)255;
        }

        public static short totQuoteEntriesMinValue()
        {
            return (short)0;
        }

        public static short totQuoteEntriesMaxValue()
        {
            return (short)254;
        }

        public QuoteSetsEncoder totQuoteEntries(final short value)
        {
            buffer.putByte(offset + 23, (byte)value);
            return this;
        }


        public static long quoteEntriesId()
        {
            return 295;
        }

        public QuoteEntriesEncoder quoteEntriesCount(final int count)
        {
            quoteEntries.wrap(buffer, count);
            return quoteEntries;
        }

        public static class QuoteEntriesEncoder
        {
            public static final int HEADER_SIZE = 4;
            private final MassQuoteEncoder parentMessage;
            private UnsafeBuffer buffer;
            private int count;
            private int index;
            private int offset;

            QuoteEntriesEncoder(final MassQuoteEncoder parentMessage)
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
                buffer.putShort(limit + 0, (short)(int)90, java.nio.ByteOrder.LITTLE_ENDIAN);
                buffer.putShort(limit + 2, (short)(int)count, java.nio.ByteOrder.LITTLE_ENDIAN);
            }

            public static int sbeHeaderSize()
            {
                return HEADER_SIZE;
            }

            public static int sbeBlockLength()
            {
                return 90;
            }

            public QuoteEntriesEncoder next()
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

            public static int quoteEntryIDId()
            {
                return 299;
            }

            public static int quoteEntryIDSinceVersion()
            {
                return 0;
            }

            public static int quoteEntryIDEncodingOffset()
            {
                return 0;
            }

            public static int quoteEntryIDEncodingLength()
            {
                return 10;
            }

            public static String quoteEntryIDMetaAttribute(final MetaAttribute metaAttribute)
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

            public static byte quoteEntryIDNullValue()
            {
                return (byte)0;
            }

            public static byte quoteEntryIDMinValue()
            {
                return (byte)32;
            }

            public static byte quoteEntryIDMaxValue()
            {
                return (byte)126;
            }

            public static int quoteEntryIDLength()
            {
                return 10;
            }

            public QuoteEntriesEncoder quoteEntryID(final int index, final byte value)
            {
                if (index < 0 || index >= 10)
                {
                    throw new IndexOutOfBoundsException("index out of range: index=" + index);
                }

                final int pos = this.offset + 0 + (index * 1);
                buffer.putByte(pos, value);

                return this;
            }

            public static String quoteEntryIDCharacterEncoding()
            {
                return "US-ASCII";
            }

            public QuoteEntriesEncoder putQuoteEntryID(final byte[] src, final int srcOffset)
            {
                final int length = 10;
                if (srcOffset < 0 || srcOffset > (src.length - length))
                {
                    throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
                }

                buffer.putBytes(this.offset + 0, src, srcOffset, length);

                return this;
            }

            public QuoteEntriesEncoder quoteEntryID(final String src)
            {
                final int length = 10;
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

            public QuoteEntriesEncoder quoteEntryID(final CharSequence src)
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
                    buffer.putByte(this.offset + 0 + i, byteValue);
                }

                for (int i = srcLength; i < length; ++i)
                {
                    buffer.putByte(this.offset + 0 + i, (byte)0);
                }

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
                return 10;
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

            public QuoteEntriesEncoder symbol(final int index, final byte value)
            {
                if (index < 0 || index >= 6)
                {
                    throw new IndexOutOfBoundsException("index out of range: index=" + index);
                }

                final int pos = this.offset + 10 + (index * 1);
                buffer.putByte(pos, value);

                return this;
            }

            public static String symbolCharacterEncoding()
            {
                return "US-ASCII";
            }

            public QuoteEntriesEncoder putSymbol(final byte[] src, final int srcOffset)
            {
                final int length = 6;
                if (srcOffset < 0 || srcOffset > (src.length - length))
                {
                    throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
                }

                buffer.putBytes(this.offset + 10, src, srcOffset, length);

                return this;
            }

            public QuoteEntriesEncoder symbol(final String src)
            {
                final int length = 6;
                final int srcLength = null == src ? 0 : src.length();
                if (srcLength > length)
                {
                    throw new IndexOutOfBoundsException("String too large for copy: byte length=" + srcLength);
                }

                buffer.putStringWithoutLengthAscii(this.offset + 10, src);

                for (int start = srcLength; start < length; ++start)
                {
                    buffer.putByte(this.offset + 10 + start, (byte)0);
                }

                return this;
            }

            public QuoteEntriesEncoder symbol(final CharSequence src)
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
                    buffer.putByte(this.offset + 10 + i, byteValue);
                }

                for (int i = srcLength; i < length; ++i)
                {
                    buffer.putByte(this.offset + 10 + i, (byte)0);
                }

                return this;
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
                return 16;
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

            public QuoteEntriesEncoder securityDesc(final int index, final byte value)
            {
                if (index < 0 || index >= 20)
                {
                    throw new IndexOutOfBoundsException("index out of range: index=" + index);
                }

                final int pos = this.offset + 16 + (index * 1);
                buffer.putByte(pos, value);

                return this;
            }

            public static String securityDescCharacterEncoding()
            {
                return "US-ASCII";
            }

            public QuoteEntriesEncoder putSecurityDesc(final byte[] src, final int srcOffset)
            {
                final int length = 20;
                if (srcOffset < 0 || srcOffset > (src.length - length))
                {
                    throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
                }

                buffer.putBytes(this.offset + 16, src, srcOffset, length);

                return this;
            }

            public QuoteEntriesEncoder securityDesc(final String src)
            {
                final int length = 20;
                final int srcLength = null == src ? 0 : src.length();
                if (srcLength > length)
                {
                    throw new IndexOutOfBoundsException("String too large for copy: byte length=" + srcLength);
                }

                buffer.putStringWithoutLengthAscii(this.offset + 16, src);

                for (int start = srcLength; start < length; ++start)
                {
                    buffer.putByte(this.offset + 16 + start, (byte)0);
                }

                return this;
            }

            public QuoteEntriesEncoder securityDesc(final CharSequence src)
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
                    buffer.putByte(this.offset + 16 + i, byteValue);
                }

                for (int i = srcLength; i < length; ++i)
                {
                    buffer.putByte(this.offset + 16 + i, (byte)0);
                }

                return this;
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
                return 36;
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

            public QuoteEntriesEncoder securityType(final int index, final byte value)
            {
                if (index < 0 || index >= 3)
                {
                    throw new IndexOutOfBoundsException("index out of range: index=" + index);
                }

                final int pos = this.offset + 36 + (index * 1);
                buffer.putByte(pos, value);

                return this;
            }

            public static String securityTypeCharacterEncoding()
            {
                return "US-ASCII";
            }

            public QuoteEntriesEncoder putSecurityType(final byte[] src, final int srcOffset)
            {
                final int length = 3;
                if (srcOffset < 0 || srcOffset > (src.length - length))
                {
                    throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
                }

                buffer.putBytes(this.offset + 36, src, srcOffset, length);

                return this;
            }

            public QuoteEntriesEncoder securityType(final String src)
            {
                final int length = 3;
                final int srcLength = null == src ? 0 : src.length();
                if (srcLength > length)
                {
                    throw new IndexOutOfBoundsException("String too large for copy: byte length=" + srcLength);
                }

                buffer.putStringWithoutLengthAscii(this.offset + 36, src);

                for (int start = srcLength; start < length; ++start)
                {
                    buffer.putByte(this.offset + 36 + start, (byte)0);
                }

                return this;
            }

            public QuoteEntriesEncoder securityType(final CharSequence src)
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
                    buffer.putByte(this.offset + 36 + i, byteValue);
                }

                for (int i = srcLength; i < length; ++i)
                {
                    buffer.putByte(this.offset + 36 + i, (byte)0);
                }

                return this;
            }

            public static int securityIDId()
            {
                return 48;
            }

            public static int securityIDSinceVersion()
            {
                return 0;
            }

            public static int securityIDEncodingOffset()
            {
                return 39;
            }

            public static int securityIDEncodingLength()
            {
                return 8;
            }

            public static String securityIDMetaAttribute(final MetaAttribute metaAttribute)
            {
                switch (metaAttribute)
                {
                    case EPOCH: return "";
                    case TIME_UNIT: return "";
                    case SEMANTIC_TYPE: return "int";
                    case PRESENCE: return "optional";
                }

                return "";
            }

            public static long securityIDNullValue()
            {
                return -9223372036854775808L;
            }

            public static long securityIDMinValue()
            {
                return -9223372036854775807L;
            }

            public static long securityIDMaxValue()
            {
                return 9223372036854775807L;
            }

            public QuoteEntriesEncoder securityID(final long value)
            {
                buffer.putLong(offset + 39, value, java.nio.ByteOrder.LITTLE_ENDIAN);
                return this;
            }


            public static int securityIDSourceId()
            {
                return 22;
            }

            public static int securityIDSourceSinceVersion()
            {
                return 0;
            }

            public static int securityIDSourceEncodingOffset()
            {
                return 47;
            }

            public static int securityIDSourceEncodingLength()
            {
                return 1;
            }

            public static String securityIDSourceMetaAttribute(final MetaAttribute metaAttribute)
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

            public QuoteEntriesEncoder securityIDSource(final SecurityIDSource value)
            {
                buffer.putByte(offset + 47, value.value());
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
                return 48;
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

            public QuoteEntriesEncoder transactTime(final long value)
            {
                buffer.putLong(offset + 48, value, java.nio.ByteOrder.LITTLE_ENDIAN);
                return this;
            }


            public static int bidPxId()
            {
                return 132;
            }

            public static int bidPxSinceVersion()
            {
                return 0;
            }

            public static int bidPxEncodingOffset()
            {
                return 56;
            }

            public static int bidPxEncodingLength()
            {
                return 9;
            }

            public static String bidPxMetaAttribute(final MetaAttribute metaAttribute)
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

            private final OptionalPriceEncoder bidPx = new OptionalPriceEncoder();

            public OptionalPriceEncoder bidPx()
            {
                bidPx.wrap(buffer, offset + 56);
                return bidPx;
            }

            public static int bidSizeId()
            {
                return 134;
            }

            public static int bidSizeSinceVersion()
            {
                return 0;
            }

            public static int bidSizeEncodingOffset()
            {
                return 65;
            }

            public static int bidSizeEncodingLength()
            {
                return 8;
            }

            public static String bidSizeMetaAttribute(final MetaAttribute metaAttribute)
            {
                switch (metaAttribute)
                {
                    case EPOCH: return "";
                    case TIME_UNIT: return "";
                    case SEMANTIC_TYPE: return "int";
                    case PRESENCE: return "optional";
                }

                return "";
            }

            public static long bidSizeNullValue()
            {
                return -9223372036854775808L;
            }

            public static long bidSizeMinValue()
            {
                return -9223372036854775807L;
            }

            public static long bidSizeMaxValue()
            {
                return 9223372036854775807L;
            }

            public QuoteEntriesEncoder bidSize(final long value)
            {
                buffer.putLong(offset + 65, value, java.nio.ByteOrder.LITTLE_ENDIAN);
                return this;
            }


            public static int offerPxId()
            {
                return 133;
            }

            public static int offerPxSinceVersion()
            {
                return 0;
            }

            public static int offerPxEncodingOffset()
            {
                return 73;
            }

            public static int offerPxEncodingLength()
            {
                return 9;
            }

            public static String offerPxMetaAttribute(final MetaAttribute metaAttribute)
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

            private final OptionalPriceEncoder offerPx = new OptionalPriceEncoder();

            public OptionalPriceEncoder offerPx()
            {
                offerPx.wrap(buffer, offset + 73);
                return offerPx;
            }

            public static int offerSizeId()
            {
                return 135;
            }

            public static int offerSizeSinceVersion()
            {
                return 0;
            }

            public static int offerSizeEncodingOffset()
            {
                return 82;
            }

            public static int offerSizeEncodingLength()
            {
                return 8;
            }

            public static String offerSizeMetaAttribute(final MetaAttribute metaAttribute)
            {
                switch (metaAttribute)
                {
                    case EPOCH: return "";
                    case TIME_UNIT: return "";
                    case SEMANTIC_TYPE: return "int";
                    case PRESENCE: return "optional";
                }

                return "";
            }

            public static long offerSizeNullValue()
            {
                return -9223372036854775808L;
            }

            public static long offerSizeMinValue()
            {
                return -9223372036854775807L;
            }

            public static long offerSizeMaxValue()
            {
                return 9223372036854775807L;
            }

            public QuoteEntriesEncoder offerSize(final long value)
            {
                buffer.putLong(offset + 82, value, java.nio.ByteOrder.LITTLE_ENDIAN);
                return this;
            }

        }
    }


    public String toString()
    {
        return appendTo(new StringBuilder(100)).toString();
    }

    public StringBuilder appendTo(final StringBuilder builder)
    {
        MassQuoteDecoder writer = new MassQuoteDecoder();
        writer.wrap(buffer, offset, BLOCK_LENGTH, SCHEMA_VERSION);

        return writer.appendTo(builder);
    }
}
