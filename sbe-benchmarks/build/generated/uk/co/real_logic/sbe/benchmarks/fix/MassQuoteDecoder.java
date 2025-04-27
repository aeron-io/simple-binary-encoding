/* Generated SBE (Simple Binary Encoding) message codec */
package uk.co.real_logic.sbe.benchmarks.fix;

import org.agrona.concurrent.UnsafeBuffer;

@SuppressWarnings("all")
public class MassQuoteDecoder
{
    public static final int BLOCK_LENGTH = 62;
    public static final int TEMPLATE_ID = 105;
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 1;
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private final MassQuoteDecoder parentMessage = this;
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

    public MassQuoteDecoder wrap(
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

    public byte quoteReqID(final int index)
    {
        if (index < 0 || index >= 23)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 0 + (index * 1);

        return buffer.getByte(pos);
    }


    public static String quoteReqIDCharacterEncoding()
    {
        return "US-ASCII";
    }

    public int getQuoteReqID(final byte[] dst, final int dstOffset)
    {
        final int length = 23;
        if (dstOffset < 0 || dstOffset > (dst.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + dstOffset);
        }

        buffer.getBytes(this.offset + 0, dst, dstOffset, length);

        return length;
    }

    public String quoteReqID()
    {
        final byte[] dst = new byte[23];
        buffer.getBytes(this.offset + 0, dst, 0, 23);

        int end = 0;
        for (; end < 23 && dst[end] != 0; ++end);

        return new String(dst, 0, end, java.nio.charset.StandardCharsets.US_ASCII);
    }


    public void getQuoteReqID(final Appendable value)
    {
        for (int i = 0; i < 23 ; ++i)
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

    public byte quoteID(final int index)
    {
        if (index < 0 || index >= 10)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 23 + (index * 1);

        return buffer.getByte(pos);
    }


    public static String quoteIDCharacterEncoding()
    {
        return "US-ASCII";
    }

    public int getQuoteID(final byte[] dst, final int dstOffset)
    {
        final int length = 10;
        if (dstOffset < 0 || dstOffset > (dst.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + dstOffset);
        }

        buffer.getBytes(this.offset + 23, dst, dstOffset, length);

        return length;
    }

    public String quoteID()
    {
        final byte[] dst = new byte[10];
        buffer.getBytes(this.offset + 23, dst, 0, 10);

        int end = 0;
        for (; end < 10 && dst[end] != 0; ++end);

        return new String(dst, 0, end, java.nio.charset.StandardCharsets.US_ASCII);
    }


    public void getQuoteID(final Appendable value)
    {
        for (int i = 0; i < 10 ; ++i)
        {
            final int c = buffer.getByte(this.offset + 23 + i) & 0xFF;
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

    public byte mMAccount(final int index)
    {
        if (index < 0 || index >= 12)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 33 + (index * 1);

        return buffer.getByte(pos);
    }


    public static String mMAccountCharacterEncoding()
    {
        return "US-ASCII";
    }

    public int getMMAccount(final byte[] dst, final int dstOffset)
    {
        final int length = 12;
        if (dstOffset < 0 || dstOffset > (dst.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + dstOffset);
        }

        buffer.getBytes(this.offset + 33, dst, dstOffset, length);

        return length;
    }

    public String mMAccount()
    {
        final byte[] dst = new byte[12];
        buffer.getBytes(this.offset + 33, dst, 0, 12);

        int end = 0;
        for (; end < 12 && dst[end] != 0; ++end);

        return new String(dst, 0, end, java.nio.charset.StandardCharsets.US_ASCII);
    }


    public void getMMAccount(final Appendable value)
    {
        for (int i = 0; i < 12 ; ++i)
        {
            final int c = buffer.getByte(this.offset + 33 + i) & 0xFF;
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

    public BooleanType manualOrderIndicator()
    {
        return BooleanType.get(((short)(buffer.getByte(offset + 45) & 0xFF)));
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

    public CustOrderHandlingInst custOrderHandlingInst()
    {
        return CustOrderHandlingInst.get(buffer.getByte(offset + 46));
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

    public CustomerOrFirm customerOrFirm()
    {
        return CustomerOrFirm.get(((short)(buffer.getByte(offset + 47) & 0xFF)));
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

    public byte selfMatchPreventionID(final int index)
    {
        if (index < 0 || index >= 12)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 48 + (index * 1);

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

        buffer.getBytes(this.offset + 48, dst, dstOffset, length);

        return length;
    }

    public String selfMatchPreventionID()
    {
        final byte[] dst = new byte[12];
        buffer.getBytes(this.offset + 48, dst, 0, 12);

        int end = 0;
        for (; end < 12 && dst[end] != 0; ++end);

        return new String(dst, 0, end, java.nio.charset.StandardCharsets.US_ASCII);
    }


    public void getSelfMatchPreventionID(final Appendable value)
    {
        for (int i = 0; i < 12 ; ++i)
        {
            final int c = buffer.getByte(this.offset + 48 + i) & 0xFF;
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

    public CtiCode ctiCode()
    {
        return CtiCode.get(buffer.getByte(offset + 60));
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

    public MMProtectionReset mMProtectionReset()
    {
        return MMProtectionReset.get(buffer.getByte(offset + 61));
    }


    private final QuoteSetsDecoder quoteSets = new QuoteSetsDecoder(this);

    public static long quoteSetsDecoderId()
    {
        return 296;
    }

    public static int quoteSetsDecoderSinceVersion()
    {
        return 0;
    }

    public QuoteSetsDecoder quoteSets()
    {
        quoteSets.wrap(buffer);
        return quoteSets;
    }

    public static class QuoteSetsDecoder
        implements Iterable<QuoteSetsDecoder>, java.util.Iterator<QuoteSetsDecoder>
    {
        public static final int HEADER_SIZE = 4;
        private final MassQuoteDecoder parentMessage;
        private UnsafeBuffer buffer;
        private int count;
        private int index;
        private int offset;
        private int blockLength;
        private final QuoteEntriesDecoder quoteEntries;

        QuoteSetsDecoder(final MassQuoteDecoder parentMessage)
        {
            this.parentMessage = parentMessage;
            quoteEntries = new QuoteEntriesDecoder(parentMessage);
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
            return 24;
        }

        public int actingBlockLength()
        {
            return blockLength;
        }

        public int count()
        {
            return count;
        }

        public java.util.Iterator<QuoteSetsDecoder> iterator()
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

        public QuoteSetsDecoder next()
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

        public byte quoteSetID(final int index)
        {
            if (index < 0 || index >= 3)
            {
                throw new IndexOutOfBoundsException("index out of range: index=" + index);
            }

            final int pos = this.offset + 0 + (index * 1);

            return buffer.getByte(pos);
        }


        public static String quoteSetIDCharacterEncoding()
        {
            return "US-ASCII";
        }

        public int getQuoteSetID(final byte[] dst, final int dstOffset)
        {
            final int length = 3;
            if (dstOffset < 0 || dstOffset > (dst.length - length))
            {
                throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + dstOffset);
            }

            buffer.getBytes(this.offset + 0, dst, dstOffset, length);

            return length;
        }

        public String quoteSetID()
        {
            final byte[] dst = new byte[3];
            buffer.getBytes(this.offset + 0, dst, 0, 3);

            int end = 0;
            for (; end < 3 && dst[end] != 0; ++end);

            return new String(dst, 0, end, java.nio.charset.StandardCharsets.US_ASCII);
        }


        public void getQuoteSetID(final Appendable value)
        {
            for (int i = 0; i < 3 ; ++i)
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

        public byte underlyingSecurityDesc(final int index)
        {
            if (index < 0 || index >= 20)
            {
                throw new IndexOutOfBoundsException("index out of range: index=" + index);
            }

            final int pos = this.offset + 3 + (index * 1);

            return buffer.getByte(pos);
        }


        public static String underlyingSecurityDescCharacterEncoding()
        {
            return "US-ASCII";
        }

        public int getUnderlyingSecurityDesc(final byte[] dst, final int dstOffset)
        {
            final int length = 20;
            if (dstOffset < 0 || dstOffset > (dst.length - length))
            {
                throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + dstOffset);
            }

            buffer.getBytes(this.offset + 3, dst, dstOffset, length);

            return length;
        }

        public String underlyingSecurityDesc()
        {
            final byte[] dst = new byte[20];
            buffer.getBytes(this.offset + 3, dst, 0, 20);

            int end = 0;
            for (; end < 20 && dst[end] != 0; ++end);

            return new String(dst, 0, end, java.nio.charset.StandardCharsets.US_ASCII);
        }


        public void getUnderlyingSecurityDesc(final Appendable value)
        {
            for (int i = 0; i < 20 ; ++i)
            {
                final int c = buffer.getByte(this.offset + 3 + i) & 0xFF;
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

        public short totQuoteEntries()
        {
            return ((short)(buffer.getByte(offset + 23) & 0xFF));
        }


        public static long quoteEntriesDecoderId()
        {
            return 295;
        }

        public static int quoteEntriesDecoderSinceVersion()
        {
            return 0;
        }

        public QuoteEntriesDecoder quoteEntries()
        {
            quoteEntries.wrap(buffer);
            return quoteEntries;
        }

        public static class QuoteEntriesDecoder
            implements Iterable<QuoteEntriesDecoder>, java.util.Iterator<QuoteEntriesDecoder>
        {
            public static final int HEADER_SIZE = 4;
            private final MassQuoteDecoder parentMessage;
            private UnsafeBuffer buffer;
            private int count;
            private int index;
            private int offset;
            private int blockLength;

            QuoteEntriesDecoder(final MassQuoteDecoder parentMessage)
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
                return 90;
            }

            public int actingBlockLength()
            {
                return blockLength;
            }

            public int count()
            {
                return count;
            }

            public java.util.Iterator<QuoteEntriesDecoder> iterator()
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

            public QuoteEntriesDecoder next()
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

            public byte quoteEntryID(final int index)
            {
                if (index < 0 || index >= 10)
                {
                    throw new IndexOutOfBoundsException("index out of range: index=" + index);
                }

                final int pos = this.offset + 0 + (index * 1);

                return buffer.getByte(pos);
            }


            public static String quoteEntryIDCharacterEncoding()
            {
                return "US-ASCII";
            }

            public int getQuoteEntryID(final byte[] dst, final int dstOffset)
            {
                final int length = 10;
                if (dstOffset < 0 || dstOffset > (dst.length - length))
                {
                    throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + dstOffset);
                }

                buffer.getBytes(this.offset + 0, dst, dstOffset, length);

                return length;
            }

            public String quoteEntryID()
            {
                final byte[] dst = new byte[10];
                buffer.getBytes(this.offset + 0, dst, 0, 10);

                int end = 0;
                for (; end < 10 && dst[end] != 0; ++end);

                return new String(dst, 0, end, java.nio.charset.StandardCharsets.US_ASCII);
            }


            public void getQuoteEntryID(final Appendable value)
            {
                for (int i = 0; i < 10 ; ++i)
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

            public byte symbol(final int index)
            {
                if (index < 0 || index >= 6)
                {
                    throw new IndexOutOfBoundsException("index out of range: index=" + index);
                }

                final int pos = this.offset + 10 + (index * 1);

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

                buffer.getBytes(this.offset + 10, dst, dstOffset, length);

                return length;
            }

            public String symbol()
            {
                final byte[] dst = new byte[6];
                buffer.getBytes(this.offset + 10, dst, 0, 6);

                int end = 0;
                for (; end < 6 && dst[end] != 0; ++end);

                return new String(dst, 0, end, java.nio.charset.StandardCharsets.US_ASCII);
            }


            public void getSymbol(final Appendable value)
            {
                for (int i = 0; i < 6 ; ++i)
                {
                    final int c = buffer.getByte(this.offset + 10 + i) & 0xFF;
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

            public byte securityDesc(final int index)
            {
                if (index < 0 || index >= 20)
                {
                    throw new IndexOutOfBoundsException("index out of range: index=" + index);
                }

                final int pos = this.offset + 16 + (index * 1);

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

                buffer.getBytes(this.offset + 16, dst, dstOffset, length);

                return length;
            }

            public String securityDesc()
            {
                final byte[] dst = new byte[20];
                buffer.getBytes(this.offset + 16, dst, 0, 20);

                int end = 0;
                for (; end < 20 && dst[end] != 0; ++end);

                return new String(dst, 0, end, java.nio.charset.StandardCharsets.US_ASCII);
            }


            public void getSecurityDesc(final Appendable value)
            {
                for (int i = 0; i < 20 ; ++i)
                {
                    final int c = buffer.getByte(this.offset + 16 + i) & 0xFF;
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

            public byte securityType(final int index)
            {
                if (index < 0 || index >= 3)
                {
                    throw new IndexOutOfBoundsException("index out of range: index=" + index);
                }

                final int pos = this.offset + 36 + (index * 1);

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

                buffer.getBytes(this.offset + 36, dst, dstOffset, length);

                return length;
            }

            public String securityType()
            {
                final byte[] dst = new byte[3];
                buffer.getBytes(this.offset + 36, dst, 0, 3);

                int end = 0;
                for (; end < 3 && dst[end] != 0; ++end);

                return new String(dst, 0, end, java.nio.charset.StandardCharsets.US_ASCII);
            }


            public void getSecurityType(final Appendable value)
            {
                for (int i = 0; i < 3 ; ++i)
                {
                    final int c = buffer.getByte(this.offset + 36 + i) & 0xFF;
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

            public long securityID()
            {
                return buffer.getLong(offset + 39, java.nio.ByteOrder.LITTLE_ENDIAN);
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

            public SecurityIDSource securityIDSource()
            {
                return SecurityIDSource.get(buffer.getByte(offset + 47));
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

            public long transactTime()
            {
                return buffer.getLong(offset + 48, java.nio.ByteOrder.LITTLE_ENDIAN);
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

            private final OptionalPriceDecoder bidPx = new OptionalPriceDecoder();

            public OptionalPriceDecoder bidPx()
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

            public long bidSize()
            {
                return buffer.getLong(offset + 65, java.nio.ByteOrder.LITTLE_ENDIAN);
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

            private final OptionalPriceDecoder offerPx = new OptionalPriceDecoder();

            public OptionalPriceDecoder offerPx()
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

            public long offerSize()
            {
                return buffer.getLong(offset + 82, java.nio.ByteOrder.LITTLE_ENDIAN);
            }



            public String toString()
            {
                return appendTo(new StringBuilder(100)).toString();
            }

            public StringBuilder appendTo(final StringBuilder builder)
            {
                builder.append('(');
                //Token{signal=BEGIN_FIELD, name='QuoteEntryID', referencedName='null', description='null', id=299, version=0, deprecated=0, encodedLength=10, offset=0, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='String'}}
                //Token{signal=ENCODING, name='string10', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=10, offset=0, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='US-ASCII', epoch='null', timeUnit=null, semanticType='String'}}
                builder.append("quoteEntryID=");
                for (int i = 0; i < quoteEntryIDLength() && quoteEntryID(i) > 0; i++)
                {
                    builder.append((char)quoteEntryID(i));
                }
                builder.append('|');
                //Token{signal=BEGIN_FIELD, name='Symbol', referencedName='null', description='null', id=55, version=0, deprecated=0, encodedLength=6, offset=10, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='String'}}
                //Token{signal=ENCODING, name='Symbol', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=6, offset=10, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='US-ASCII', epoch='null', timeUnit=null, semanticType='String'}}
                builder.append("symbol=");
                for (int i = 0; i < symbolLength() && symbol(i) > 0; i++)
                {
                    builder.append((char)symbol(i));
                }
                builder.append('|');
                //Token{signal=BEGIN_FIELD, name='SecurityDesc', referencedName='null', description='null', id=107, version=0, deprecated=0, encodedLength=20, offset=16, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='String'}}
                //Token{signal=ENCODING, name='string20', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=20, offset=16, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='US-ASCII', epoch='null', timeUnit=null, semanticType='String'}}
                builder.append("securityDesc=");
                for (int i = 0; i < securityDescLength() && securityDesc(i) > 0; i++)
                {
                    builder.append((char)securityDesc(i));
                }
                builder.append('|');
                //Token{signal=BEGIN_FIELD, name='SecurityType', referencedName='null', description='null', id=167, version=0, deprecated=0, encodedLength=3, offset=36, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='String'}}
                //Token{signal=ENCODING, name='string3', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=3, offset=36, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='US-ASCII', epoch='null', timeUnit=null, semanticType='String'}}
                builder.append("securityType=");
                for (int i = 0; i < securityTypeLength() && securityType(i) > 0; i++)
                {
                    builder.append((char)securityType(i));
                }
                builder.append('|');
                //Token{signal=BEGIN_FIELD, name='SecurityID', referencedName='null', description='null', id=48, version=0, deprecated=0, encodedLength=8, offset=39, componentTokenCount=3, encoding=Encoding{presence=OPTIONAL, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='int'}}
                //Token{signal=ENCODING, name='optionalInt64', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=8, offset=39, componentTokenCount=1, encoding=Encoding{presence=OPTIONAL, primitiveType=INT64, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='int'}}
                builder.append("securityID=");
                builder.append(securityID());
                builder.append('|');
                //Token{signal=BEGIN_FIELD, name='SecurityIDSource', referencedName='null', description='null', id=22, version=0, deprecated=0, encodedLength=1, offset=47, componentTokenCount=5, encoding=Encoding{presence=OPTIONAL, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
                //Token{signal=BEGIN_ENUM, name='SecurityIDSource', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=47, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='char'}}
                builder.append("securityIDSource=");
                builder.append(securityIDSource());
                builder.append('|');
                //Token{signal=BEGIN_FIELD, name='TransactTime', referencedName='null', description='null', id=60, version=0, deprecated=0, encodedLength=8, offset=48, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=nanosecond, semanticType='UTCTimestamp'}}
                //Token{signal=ENCODING, name='timestamp', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=8, offset=48, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=UINT64, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=nanosecond, semanticType='UTCTimestamp'}}
                builder.append("transactTime=");
                builder.append(transactTime());
                builder.append('|');
                //Token{signal=BEGIN_FIELD, name='BidPx', referencedName='null', description='null', id=132, version=0, deprecated=0, encodedLength=9, offset=56, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='Price'}}
                //Token{signal=BEGIN_COMPOSITE, name='OptionalPrice', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=9, offset=56, componentTokenCount=4, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='Price'}}
                builder.append("bidPx=");
                bidPx().appendTo(builder);
                builder.append('|');
                //Token{signal=BEGIN_FIELD, name='BidSize', referencedName='null', description='null', id=134, version=0, deprecated=0, encodedLength=8, offset=65, componentTokenCount=3, encoding=Encoding{presence=OPTIONAL, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='int'}}
                //Token{signal=ENCODING, name='optionalInt64', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=8, offset=65, componentTokenCount=1, encoding=Encoding{presence=OPTIONAL, primitiveType=INT64, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='int'}}
                builder.append("bidSize=");
                builder.append(bidSize());
                builder.append('|');
                //Token{signal=BEGIN_FIELD, name='OfferPx', referencedName='null', description='null', id=133, version=0, deprecated=0, encodedLength=9, offset=73, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='Price'}}
                //Token{signal=BEGIN_COMPOSITE, name='OptionalPrice', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=9, offset=73, componentTokenCount=4, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='Price'}}
                builder.append("offerPx=");
                offerPx().appendTo(builder);
                builder.append('|');
                //Token{signal=BEGIN_FIELD, name='OfferSize', referencedName='null', description='null', id=135, version=0, deprecated=0, encodedLength=8, offset=82, componentTokenCount=3, encoding=Encoding{presence=OPTIONAL, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='int'}}
                //Token{signal=ENCODING, name='optionalInt64', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=8, offset=82, componentTokenCount=1, encoding=Encoding{presence=OPTIONAL, primitiveType=INT64, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='int'}}
                builder.append("offerSize=");
                builder.append(offerSize());
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
            builder.append('(');
            //Token{signal=BEGIN_FIELD, name='QuoteSetID', referencedName='null', description='null', id=302, version=0, deprecated=0, encodedLength=3, offset=0, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='String'}}
            //Token{signal=ENCODING, name='string3', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=3, offset=0, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='US-ASCII', epoch='null', timeUnit=null, semanticType='String'}}
            builder.append("quoteSetID=");
            for (int i = 0; i < quoteSetIDLength() && quoteSetID(i) > 0; i++)
            {
                builder.append((char)quoteSetID(i));
            }
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='UnderlyingSecurityDesc', referencedName='null', description='null', id=307, version=0, deprecated=0, encodedLength=20, offset=3, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='String'}}
            //Token{signal=ENCODING, name='string20', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=20, offset=3, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='US-ASCII', epoch='null', timeUnit=null, semanticType='String'}}
            builder.append("underlyingSecurityDesc=");
            for (int i = 0; i < underlyingSecurityDescLength() && underlyingSecurityDesc(i) > 0; i++)
            {
                builder.append((char)underlyingSecurityDesc(i));
            }
            builder.append('|');
            //Token{signal=BEGIN_FIELD, name='TotQuoteEntries', referencedName='null', description='null', id=304, version=0, deprecated=0, encodedLength=1, offset=23, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='int'}}
            //Token{signal=ENCODING, name='uint8', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=23, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=UINT8, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='int'}}
            builder.append("totQuoteEntries=");
            builder.append(totQuoteEntries());
            builder.append('|');
            //Token{signal=BEGIN_GROUP, name='QuoteEntries', referencedName='null', description='null', id=295, version=0, deprecated=0, encodedLength=90, offset=24, componentTokenCount=47, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
            builder.append("quoteEntries=[");
            QuoteEntriesDecoder quoteEntries = quoteEntries();
            if (quoteEntries.count() > 0)
            {
                while (quoteEntries.hasNext())
                {
                    quoteEntries.next().appendTo(builder);
                    builder.append(',');
                }
                builder.setLength(builder.length() - 1);
            }
            builder.append(']');
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
        builder.append("[MassQuote](sbeTemplateId=");
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
        //Token{signal=BEGIN_FIELD, name='QuoteReqID', referencedName='null', description='null', id=131, version=0, deprecated=0, encodedLength=23, offset=0, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='String'}}
        //Token{signal=ENCODING, name='string23', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=23, offset=0, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='US-ASCII', epoch='null', timeUnit=null, semanticType='String'}}
        builder.append("quoteReqID=");
        for (int i = 0; i < quoteReqIDLength() && quoteReqID(i) > 0; i++)
        {
            builder.append((char)quoteReqID(i));
        }
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='QuoteID', referencedName='null', description='null', id=117, version=0, deprecated=0, encodedLength=10, offset=23, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='String'}}
        //Token{signal=ENCODING, name='string10', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=10, offset=23, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='US-ASCII', epoch='null', timeUnit=null, semanticType='String'}}
        builder.append("quoteID=");
        for (int i = 0; i < quoteIDLength() && quoteID(i) > 0; i++)
        {
            builder.append((char)quoteID(i));
        }
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='MMAccount', referencedName='null', description='null', id=9771, version=0, deprecated=0, encodedLength=12, offset=33, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='String'}}
        //Token{signal=ENCODING, name='string12', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=12, offset=33, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='US-ASCII', epoch='null', timeUnit=null, semanticType='String'}}
        builder.append("mMAccount=");
        for (int i = 0; i < mMAccountLength() && mMAccount(i) > 0; i++)
        {
            builder.append((char)mMAccount(i));
        }
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='ManualOrderIndicator', referencedName='null', description='null', id=1028, version=0, deprecated=0, encodedLength=1, offset=45, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
        //Token{signal=BEGIN_ENUM, name='BooleanType', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=45, componentTokenCount=4, encoding=Encoding{presence=REQUIRED, primitiveType=UINT8, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='Boolean'}}
        builder.append("manualOrderIndicator=");
        builder.append(manualOrderIndicator());
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='CustOrderHandlingInst', referencedName='null', description='null', id=1031, version=0, deprecated=0, encodedLength=1, offset=46, componentTokenCount=17, encoding=Encoding{presence=OPTIONAL, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='char'}}
        //Token{signal=BEGIN_ENUM, name='CustOrderHandlingInst', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=46, componentTokenCount=15, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='char'}}
        builder.append("custOrderHandlingInst=");
        builder.append(custOrderHandlingInst());
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='CustomerOrFirm', referencedName='null', description='null', id=204, version=0, deprecated=0, encodedLength=1, offset=47, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
        //Token{signal=BEGIN_ENUM, name='CustomerOrFirm', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=47, componentTokenCount=4, encoding=Encoding{presence=REQUIRED, primitiveType=UINT8, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='int'}}
        builder.append("customerOrFirm=");
        builder.append(customerOrFirm());
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='SelfMatchPreventionID', referencedName='null', description='null', id=7928, version=0, deprecated=0, encodedLength=12, offset=48, componentTokenCount=3, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='String'}}
        //Token{signal=ENCODING, name='string12', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=12, offset=48, componentTokenCount=1, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='US-ASCII', epoch='null', timeUnit=null, semanticType='String'}}
        builder.append("selfMatchPreventionID=");
        for (int i = 0; i < selfMatchPreventionIDLength() && selfMatchPreventionID(i) > 0; i++)
        {
            builder.append((char)selfMatchPreventionID(i));
        }
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='CtiCode', referencedName='null', description='null', id=9702, version=0, deprecated=0, encodedLength=1, offset=60, componentTokenCount=8, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
        //Token{signal=BEGIN_ENUM, name='CtiCode', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=60, componentTokenCount=6, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='char'}}
        builder.append("ctiCode=");
        builder.append(ctiCode());
        builder.append('|');
        //Token{signal=BEGIN_FIELD, name='MMProtectionReset', referencedName='null', description='null', id=9773, version=0, deprecated=0, encodedLength=1, offset=61, componentTokenCount=6, encoding=Encoding{presence=OPTIONAL, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
        //Token{signal=BEGIN_ENUM, name='MMProtectionReset', referencedName='null', description='null', id=-1, version=0, deprecated=0, encodedLength=1, offset=61, componentTokenCount=4, encoding=Encoding{presence=REQUIRED, primitiveType=CHAR, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='char'}}
        builder.append("mMProtectionReset=");
        builder.append(mMProtectionReset());
        builder.append('|');
        //Token{signal=BEGIN_GROUP, name='QuoteSets', referencedName='null', description='null', id=296, version=0, deprecated=0, encodedLength=24, offset=62, componentTokenCount=62, encoding=Encoding{presence=REQUIRED, primitiveType=null, byteOrder=LITTLE_ENDIAN, minValue=null, maxValue=null, nullValue=null, constValue=null, characterEncoding='null', epoch='null', timeUnit=null, semanticType='null'}}
        builder.append("quoteSets=[");
        QuoteSetsDecoder quoteSets = quoteSets();
        if (quoteSets.count() > 0)
        {
            while (quoteSets.hasNext())
            {
                quoteSets.next().appendTo(builder);
                builder.append(',');
            }
            builder.setLength(builder.length() - 1);
        }
        builder.append(']');

        limit(originalLimit);

        return builder;
    }
}
