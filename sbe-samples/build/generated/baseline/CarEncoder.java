/* Generated SBE (Simple Binary Encoding) message codec */
package baseline;

import org.agrona.MutableDirectBuffer;
import org.agrona.DirectBuffer;
import org.agrona.sbe.*;

/**
 * Description of a basic Car
 */
@SuppressWarnings("all")
public class CarEncoder implements MessageEncoderFlyweight
{
    public static final int BLOCK_LENGTH = 49;
    public static final int TEMPLATE_ID = 1;
    public static final int SCHEMA_ID = 1;
    public static final int SCHEMA_VERSION = 0;
    public static final java.nio.ByteOrder BYTE_ORDER = java.nio.ByteOrder.LITTLE_ENDIAN;

    private final CarEncoder parentMessage = this;
    private MutableDirectBuffer buffer;
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
        return "";
    }

    public MutableDirectBuffer buffer()
    {
        return buffer;
    }

    public int offset()
    {
        return offset;
    }

    public CarEncoder wrap(final MutableDirectBuffer buffer, final int offset)
    {
        if (buffer != this.buffer)
        {
            this.buffer = buffer;
        }
        this.offset = offset;
        limit(offset + BLOCK_LENGTH);

        return this;
    }

    public CarEncoder wrapAndApplyHeader(
        final MutableDirectBuffer buffer, final int offset, final MessageHeaderEncoder headerEncoder)
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

    public static int serialNumberId()
    {
        return 1;
    }

    public static int serialNumberSinceVersion()
    {
        return 0;
    }

    public static int serialNumberEncodingOffset()
    {
        return 0;
    }

    public static int serialNumberEncodingLength()
    {
        return 8;
    }

    public static String serialNumberMetaAttribute(final MetaAttribute metaAttribute)
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

    public static long serialNumberNullValue()
    {
        return 0xffffffffffffffffL;
    }

    public static long serialNumberMinValue()
    {
        return 0x0L;
    }

    public static long serialNumberMaxValue()
    {
        return 0xfffffffffffffffeL;
    }

    public CarEncoder serialNumber(final long value)
    {
        buffer.putLong(offset + 0, value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }


    public static int modelYearId()
    {
        return 2;
    }

    public static int modelYearSinceVersion()
    {
        return 0;
    }

    public static int modelYearEncodingOffset()
    {
        return 8;
    }

    public static int modelYearEncodingLength()
    {
        return 2;
    }

    public static String modelYearMetaAttribute(final MetaAttribute metaAttribute)
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

    public static int modelYearNullValue()
    {
        return 65535;
    }

    public static int modelYearMinValue()
    {
        return 0;
    }

    public static int modelYearMaxValue()
    {
        return 65534;
    }

    public CarEncoder modelYear(final int value)
    {
        buffer.putShort(offset + 8, (short)value, java.nio.ByteOrder.LITTLE_ENDIAN);
        return this;
    }


    public static int availableId()
    {
        return 3;
    }

    public static int availableSinceVersion()
    {
        return 0;
    }

    public static int availableEncodingOffset()
    {
        return 10;
    }

    public static int availableEncodingLength()
    {
        return 1;
    }

    public static String availableMetaAttribute(final MetaAttribute metaAttribute)
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

    public CarEncoder available(final BooleanType value)
    {
        buffer.putByte(offset + 10, (byte)value.value());
        return this;
    }

    public static int codeId()
    {
        return 4;
    }

    public static int codeSinceVersion()
    {
        return 0;
    }

    public static int codeEncodingOffset()
    {
        return 11;
    }

    public static int codeEncodingLength()
    {
        return 1;
    }

    public static String codeMetaAttribute(final MetaAttribute metaAttribute)
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

    public CarEncoder code(final Model value)
    {
        buffer.putByte(offset + 11, value.value());
        return this;
    }

    public static int someNumbersId()
    {
        return 5;
    }

    public static int someNumbersSinceVersion()
    {
        return 0;
    }

    public static int someNumbersEncodingOffset()
    {
        return 12;
    }

    public static int someNumbersEncodingLength()
    {
        return 20;
    }

    public static String someNumbersMetaAttribute(final MetaAttribute metaAttribute)
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

    public static long someNumbersNullValue()
    {
        return 4294967295L;
    }

    public static long someNumbersMinValue()
    {
        return 0L;
    }

    public static long someNumbersMaxValue()
    {
        return 4294967294L;
    }

    public static int someNumbersLength()
    {
        return 5;
    }

    public CarEncoder someNumbers(final int index, final long value)
    {
        if (index < 0 || index >= 5)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 12 + (index * 4);
        buffer.putInt(pos, (int)value, java.nio.ByteOrder.LITTLE_ENDIAN);

        return this;
    }

    public static int vehicleCodeId()
    {
        return 6;
    }

    public static int vehicleCodeSinceVersion()
    {
        return 0;
    }

    public static int vehicleCodeEncodingOffset()
    {
        return 32;
    }

    public static int vehicleCodeEncodingLength()
    {
        return 6;
    }

    public static String vehicleCodeMetaAttribute(final MetaAttribute metaAttribute)
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

    public static byte vehicleCodeNullValue()
    {
        return (byte)0;
    }

    public static byte vehicleCodeMinValue()
    {
        return (byte)32;
    }

    public static byte vehicleCodeMaxValue()
    {
        return (byte)126;
    }

    public static int vehicleCodeLength()
    {
        return 6;
    }

    public CarEncoder vehicleCode(final int index, final byte value)
    {
        if (index < 0 || index >= 6)
        {
            throw new IndexOutOfBoundsException("index out of range: index=" + index);
        }

        final int pos = this.offset + 32 + (index * 1);
        buffer.putByte(pos, value);

        return this;
    }

    public static String vehicleCodeCharacterEncoding()
    {
        return "ASCII";
    }

    public CarEncoder putVehicleCode(final byte[] src, final int srcOffset)
    {
        final int length = 6;
        if (srcOffset < 0 || srcOffset > (src.length - length))
        {
            throw new IndexOutOfBoundsException("Copy will go out of range: offset=" + srcOffset);
        }

        buffer.putBytes(this.offset + 32, src, srcOffset, length);

        return this;
    }

    public CarEncoder vehicleCode(final String src)
    {
        final int length = 6;
        final int srcLength = null == src ? 0 : src.length();
        if (srcLength > length)
        {
            throw new IndexOutOfBoundsException("String too large for copy: byte length=" + srcLength);
        }

        buffer.putStringWithoutLengthAscii(this.offset + 32, src);

        for (int start = srcLength; start < length; ++start)
        {
            buffer.putByte(this.offset + 32 + start, (byte)0);
        }

        return this;
    }

    public CarEncoder vehicleCode(final CharSequence src)
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
            buffer.putByte(this.offset + 32 + i, byteValue);
        }

        for (int i = srcLength; i < length; ++i)
        {
            buffer.putByte(this.offset + 32 + i, (byte)0);
        }

        return this;
    }

    public static int extrasId()
    {
        return 7;
    }

    public static int extrasSinceVersion()
    {
        return 0;
    }

    public static int extrasEncodingOffset()
    {
        return 38;
    }

    public static int extrasEncodingLength()
    {
        return 1;
    }

    public static String extrasMetaAttribute(final MetaAttribute metaAttribute)
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

    private final OptionalExtrasEncoder extras = new OptionalExtrasEncoder();

    public OptionalExtrasEncoder extras()
    {
        extras.wrap(buffer, offset + 38);
        return extras;
    }

    public static int discountedModelId()
    {
        return 8;
    }

    public static int discountedModelSinceVersion()
    {
        return 0;
    }

    public static int discountedModelEncodingOffset()
    {
        return 39;
    }

    public static int discountedModelEncodingLength()
    {
        return 1;
    }

    public static String discountedModelMetaAttribute(final MetaAttribute metaAttribute)
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

    public CarEncoder discountedModel(final Model value)
    {
        buffer.putByte(offset + 39, value.value());
        return this;
    }

    public static int engineId()
    {
        return 9;
    }

    public static int engineSinceVersion()
    {
        return 0;
    }

    public static int engineEncodingOffset()
    {
        return 39;
    }

    public static int engineEncodingLength()
    {
        return 10;
    }

    public static String engineMetaAttribute(final MetaAttribute metaAttribute)
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

    private final EngineEncoder engine = new EngineEncoder();

    public EngineEncoder engine()
    {
        engine.wrap(buffer, offset + 39);
        return engine;
    }

    private final FuelFiguresEncoder fuelFigures = new FuelFiguresEncoder(this);

    public static long fuelFiguresId()
    {
        return 10;
    }

    public FuelFiguresEncoder fuelFiguresCount(final int count)
    {
        fuelFigures.wrap(buffer, count);
        return fuelFigures;
    }

    public static class FuelFiguresEncoder
    {
        public static final int HEADER_SIZE = 4;
        private final CarEncoder parentMessage;
        private MutableDirectBuffer buffer;
        private int count;
        private int index;
        private int offset;

        FuelFiguresEncoder(final CarEncoder parentMessage)
        {
            this.parentMessage = parentMessage;
        }

        public void wrap(final MutableDirectBuffer buffer, final int count)
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
            buffer.putShort(limit + 0, (short)(int)6, java.nio.ByteOrder.LITTLE_ENDIAN);
            buffer.putShort(limit + 2, (short)(int)count, java.nio.ByteOrder.LITTLE_ENDIAN);
        }

        public static int sbeHeaderSize()
        {
            return HEADER_SIZE;
        }

        public static int sbeBlockLength()
        {
            return 6;
        }

        public FuelFiguresEncoder next()
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

        public static int speedId()
        {
            return 11;
        }

        public static int speedSinceVersion()
        {
            return 0;
        }

        public static int speedEncodingOffset()
        {
            return 0;
        }

        public static int speedEncodingLength()
        {
            return 2;
        }

        public static String speedMetaAttribute(final MetaAttribute metaAttribute)
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

        public static int speedNullValue()
        {
            return 65535;
        }

        public static int speedMinValue()
        {
            return 0;
        }

        public static int speedMaxValue()
        {
            return 65534;
        }

        public FuelFiguresEncoder speed(final int value)
        {
            buffer.putShort(offset + 0, (short)value, java.nio.ByteOrder.LITTLE_ENDIAN);
            return this;
        }


        public static int mpgId()
        {
            return 12;
        }

        public static int mpgSinceVersion()
        {
            return 0;
        }

        public static int mpgEncodingOffset()
        {
            return 2;
        }

        public static int mpgEncodingLength()
        {
            return 4;
        }

        public static String mpgMetaAttribute(final MetaAttribute metaAttribute)
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

        public static float mpgNullValue()
        {
            return Float.NaN;
        }

        public static float mpgMinValue()
        {
            return 1.401298464324817E-45f;
        }

        public static float mpgMaxValue()
        {
            return 3.4028234663852886E38f;
        }

        public FuelFiguresEncoder mpg(final float value)
        {
            buffer.putFloat(offset + 2, value, java.nio.ByteOrder.LITTLE_ENDIAN);
            return this;
        }


        public static int usageDescriptionId()
        {
            return 200;
        }

        public static String usageDescriptionCharacterEncoding()
        {
            return "UTF-8";
        }

        public static String usageDescriptionMetaAttribute(final MetaAttribute metaAttribute)
        {
            switch (metaAttribute)
            {
                case EPOCH: return "unix";
                case TIME_UNIT: return "nanosecond";
                case SEMANTIC_TYPE: return "";
                case PRESENCE: return "required";
            }

            return "";
        }

        public static int usageDescriptionHeaderLength()
        {
            return 4;
        }

        public FuelFiguresEncoder putUsageDescription(final DirectBuffer src, final int srcOffset, final int length)
        {
            if (length > 1073741824)
            {
                throw new IllegalStateException("length > maxValue for type: " + length);
            }

            final int headerLength = 4;
            final int limit = parentMessage.limit();
            parentMessage.limit(limit + headerLength + length);
            buffer.putInt(limit, (int)length, java.nio.ByteOrder.LITTLE_ENDIAN);
            buffer.putBytes(limit + headerLength, src, srcOffset, length);

            return this;
        }

        public FuelFiguresEncoder putUsageDescription(final byte[] src, final int srcOffset, final int length)
        {
            if (length > 1073741824)
            {
                throw new IllegalStateException("length > maxValue for type: " + length);
            }

            final int headerLength = 4;
            final int limit = parentMessage.limit();
            parentMessage.limit(limit + headerLength + length);
            buffer.putInt(limit, (int)length, java.nio.ByteOrder.LITTLE_ENDIAN);
            buffer.putBytes(limit + headerLength, src, srcOffset, length);

            return this;
        }

        public FuelFiguresEncoder usageDescription(final String value)
        {
            final byte[] bytes;
            try
            {
                bytes = null == value || value.isEmpty() ? org.agrona.collections.ArrayUtil.EMPTY_BYTE_ARRAY : value.getBytes("UTF-8");
            }
            catch (final java.io.UnsupportedEncodingException ex)
            {
                throw new RuntimeException(ex);
            }

            final int length = bytes.length;
            if (length > 1073741824)
            {
                throw new IllegalStateException("length > maxValue for type: " + length);
            }

            final int headerLength = 4;
            final int limit = parentMessage.limit();
            parentMessage.limit(limit + headerLength + length);
            buffer.putInt(limit, (int)length, java.nio.ByteOrder.LITTLE_ENDIAN);
            buffer.putBytes(limit + headerLength, bytes, 0, length);

            return this;
        }
    }

    private final PerformanceFiguresEncoder performanceFigures = new PerformanceFiguresEncoder(this);

    public static long performanceFiguresId()
    {
        return 13;
    }

    public PerformanceFiguresEncoder performanceFiguresCount(final int count)
    {
        performanceFigures.wrap(buffer, count);
        return performanceFigures;
    }

    public static class PerformanceFiguresEncoder
    {
        public static final int HEADER_SIZE = 4;
        private final CarEncoder parentMessage;
        private MutableDirectBuffer buffer;
        private int count;
        private int index;
        private int offset;
        private final AccelerationEncoder acceleration;

        PerformanceFiguresEncoder(final CarEncoder parentMessage)
        {
            this.parentMessage = parentMessage;
            acceleration = new AccelerationEncoder(parentMessage);
        }

        public void wrap(final MutableDirectBuffer buffer, final int count)
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
            buffer.putShort(limit + 0, (short)(int)1, java.nio.ByteOrder.LITTLE_ENDIAN);
            buffer.putShort(limit + 2, (short)(int)count, java.nio.ByteOrder.LITTLE_ENDIAN);
        }

        public static int sbeHeaderSize()
        {
            return HEADER_SIZE;
        }

        public static int sbeBlockLength()
        {
            return 1;
        }

        public PerformanceFiguresEncoder next()
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

        public static int octaneRatingId()
        {
            return 14;
        }

        public static int octaneRatingSinceVersion()
        {
            return 0;
        }

        public static int octaneRatingEncodingOffset()
        {
            return 0;
        }

        public static int octaneRatingEncodingLength()
        {
            return 1;
        }

        public static String octaneRatingMetaAttribute(final MetaAttribute metaAttribute)
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

        public static short octaneRatingNullValue()
        {
            return (short)255;
        }

        public static short octaneRatingMinValue()
        {
            return (short)90;
        }

        public static short octaneRatingMaxValue()
        {
            return (short)110;
        }

        public PerformanceFiguresEncoder octaneRating(final short value)
        {
            buffer.putByte(offset + 0, (byte)value);
            return this;
        }


        public static long accelerationId()
        {
            return 15;
        }

        public AccelerationEncoder accelerationCount(final int count)
        {
            acceleration.wrap(buffer, count);
            return acceleration;
        }

        public static class AccelerationEncoder
        {
            public static final int HEADER_SIZE = 4;
            private final CarEncoder parentMessage;
            private MutableDirectBuffer buffer;
            private int count;
            private int index;
            private int offset;

            AccelerationEncoder(final CarEncoder parentMessage)
            {
                this.parentMessage = parentMessage;
            }

            public void wrap(final MutableDirectBuffer buffer, final int count)
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
                buffer.putShort(limit + 0, (short)(int)6, java.nio.ByteOrder.LITTLE_ENDIAN);
                buffer.putShort(limit + 2, (short)(int)count, java.nio.ByteOrder.LITTLE_ENDIAN);
            }

            public static int sbeHeaderSize()
            {
                return HEADER_SIZE;
            }

            public static int sbeBlockLength()
            {
                return 6;
            }

            public AccelerationEncoder next()
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

            public static int mphId()
            {
                return 16;
            }

            public static int mphSinceVersion()
            {
                return 0;
            }

            public static int mphEncodingOffset()
            {
                return 0;
            }

            public static int mphEncodingLength()
            {
                return 2;
            }

            public static String mphMetaAttribute(final MetaAttribute metaAttribute)
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

            public static int mphNullValue()
            {
                return 65535;
            }

            public static int mphMinValue()
            {
                return 0;
            }

            public static int mphMaxValue()
            {
                return 65534;
            }

            public AccelerationEncoder mph(final int value)
            {
                buffer.putShort(offset + 0, (short)value, java.nio.ByteOrder.LITTLE_ENDIAN);
                return this;
            }


            public static int secondsId()
            {
                return 17;
            }

            public static int secondsSinceVersion()
            {
                return 0;
            }

            public static int secondsEncodingOffset()
            {
                return 2;
            }

            public static int secondsEncodingLength()
            {
                return 4;
            }

            public static String secondsMetaAttribute(final MetaAttribute metaAttribute)
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

            public static float secondsNullValue()
            {
                return Float.NaN;
            }

            public static float secondsMinValue()
            {
                return 1.401298464324817E-45f;
            }

            public static float secondsMaxValue()
            {
                return 3.4028234663852886E38f;
            }

            public AccelerationEncoder seconds(final float value)
            {
                buffer.putFloat(offset + 2, value, java.nio.ByteOrder.LITTLE_ENDIAN);
                return this;
            }

        }
    }

    public static int manufacturerId()
    {
        return 18;
    }

    public static String manufacturerCharacterEncoding()
    {
        return "UTF-8";
    }

    public static String manufacturerMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "unix";
            case TIME_UNIT: return "nanosecond";
            case SEMANTIC_TYPE: return "";
            case PRESENCE: return "required";
        }

        return "";
    }

    public static int manufacturerHeaderLength()
    {
        return 4;
    }

    public CarEncoder putManufacturer(final DirectBuffer src, final int srcOffset, final int length)
    {
        if (length > 1073741824)
        {
            throw new IllegalStateException("length > maxValue for type: " + length);
        }

        final int headerLength = 4;
        final int limit = parentMessage.limit();
        parentMessage.limit(limit + headerLength + length);
        buffer.putInt(limit, (int)length, java.nio.ByteOrder.LITTLE_ENDIAN);
        buffer.putBytes(limit + headerLength, src, srcOffset, length);

        return this;
    }

    public CarEncoder putManufacturer(final byte[] src, final int srcOffset, final int length)
    {
        if (length > 1073741824)
        {
            throw new IllegalStateException("length > maxValue for type: " + length);
        }

        final int headerLength = 4;
        final int limit = parentMessage.limit();
        parentMessage.limit(limit + headerLength + length);
        buffer.putInt(limit, (int)length, java.nio.ByteOrder.LITTLE_ENDIAN);
        buffer.putBytes(limit + headerLength, src, srcOffset, length);

        return this;
    }

    public CarEncoder manufacturer(final String value)
    {
        final byte[] bytes;
        try
        {
            bytes = null == value || value.isEmpty() ? org.agrona.collections.ArrayUtil.EMPTY_BYTE_ARRAY : value.getBytes("UTF-8");
        }
        catch (final java.io.UnsupportedEncodingException ex)
        {
            throw new RuntimeException(ex);
        }

        final int length = bytes.length;
        if (length > 1073741824)
        {
            throw new IllegalStateException("length > maxValue for type: " + length);
        }

        final int headerLength = 4;
        final int limit = parentMessage.limit();
        parentMessage.limit(limit + headerLength + length);
        buffer.putInt(limit, (int)length, java.nio.ByteOrder.LITTLE_ENDIAN);
        buffer.putBytes(limit + headerLength, bytes, 0, length);

        return this;
    }

    public static int modelId()
    {
        return 19;
    }

    public static String modelCharacterEncoding()
    {
        return "UTF-8";
    }

    public static String modelMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "unix";
            case TIME_UNIT: return "nanosecond";
            case SEMANTIC_TYPE: return "";
            case PRESENCE: return "required";
        }

        return "";
    }

    public static int modelHeaderLength()
    {
        return 4;
    }

    public CarEncoder putModel(final DirectBuffer src, final int srcOffset, final int length)
    {
        if (length > 1073741824)
        {
            throw new IllegalStateException("length > maxValue for type: " + length);
        }

        final int headerLength = 4;
        final int limit = parentMessage.limit();
        parentMessage.limit(limit + headerLength + length);
        buffer.putInt(limit, (int)length, java.nio.ByteOrder.LITTLE_ENDIAN);
        buffer.putBytes(limit + headerLength, src, srcOffset, length);

        return this;
    }

    public CarEncoder putModel(final byte[] src, final int srcOffset, final int length)
    {
        if (length > 1073741824)
        {
            throw new IllegalStateException("length > maxValue for type: " + length);
        }

        final int headerLength = 4;
        final int limit = parentMessage.limit();
        parentMessage.limit(limit + headerLength + length);
        buffer.putInt(limit, (int)length, java.nio.ByteOrder.LITTLE_ENDIAN);
        buffer.putBytes(limit + headerLength, src, srcOffset, length);

        return this;
    }

    public CarEncoder model(final String value)
    {
        final byte[] bytes;
        try
        {
            bytes = null == value || value.isEmpty() ? org.agrona.collections.ArrayUtil.EMPTY_BYTE_ARRAY : value.getBytes("UTF-8");
        }
        catch (final java.io.UnsupportedEncodingException ex)
        {
            throw new RuntimeException(ex);
        }

        final int length = bytes.length;
        if (length > 1073741824)
        {
            throw new IllegalStateException("length > maxValue for type: " + length);
        }

        final int headerLength = 4;
        final int limit = parentMessage.limit();
        parentMessage.limit(limit + headerLength + length);
        buffer.putInt(limit, (int)length, java.nio.ByteOrder.LITTLE_ENDIAN);
        buffer.putBytes(limit + headerLength, bytes, 0, length);

        return this;
    }

    public static int activationCodeId()
    {
        return 20;
    }

    public static String activationCodeCharacterEncoding()
    {
        return "UTF-8";
    }

    public static String activationCodeMetaAttribute(final MetaAttribute metaAttribute)
    {
        switch (metaAttribute)
        {
            case EPOCH: return "unix";
            case TIME_UNIT: return "nanosecond";
            case SEMANTIC_TYPE: return "";
            case PRESENCE: return "required";
        }

        return "";
    }

    public static int activationCodeHeaderLength()
    {
        return 4;
    }

    public CarEncoder putActivationCode(final DirectBuffer src, final int srcOffset, final int length)
    {
        if (length > 1073741824)
        {
            throw new IllegalStateException("length > maxValue for type: " + length);
        }

        final int headerLength = 4;
        final int limit = parentMessage.limit();
        parentMessage.limit(limit + headerLength + length);
        buffer.putInt(limit, (int)length, java.nio.ByteOrder.LITTLE_ENDIAN);
        buffer.putBytes(limit + headerLength, src, srcOffset, length);

        return this;
    }

    public CarEncoder putActivationCode(final byte[] src, final int srcOffset, final int length)
    {
        if (length > 1073741824)
        {
            throw new IllegalStateException("length > maxValue for type: " + length);
        }

        final int headerLength = 4;
        final int limit = parentMessage.limit();
        parentMessage.limit(limit + headerLength + length);
        buffer.putInt(limit, (int)length, java.nio.ByteOrder.LITTLE_ENDIAN);
        buffer.putBytes(limit + headerLength, src, srcOffset, length);

        return this;
    }

    public CarEncoder activationCode(final String value)
    {
        final byte[] bytes;
        try
        {
            bytes = null == value || value.isEmpty() ? org.agrona.collections.ArrayUtil.EMPTY_BYTE_ARRAY : value.getBytes("UTF-8");
        }
        catch (final java.io.UnsupportedEncodingException ex)
        {
            throw new RuntimeException(ex);
        }

        final int length = bytes.length;
        if (length > 1073741824)
        {
            throw new IllegalStateException("length > maxValue for type: " + length);
        }

        final int headerLength = 4;
        final int limit = parentMessage.limit();
        parentMessage.limit(limit + headerLength + length);
        buffer.putInt(limit, (int)length, java.nio.ByteOrder.LITTLE_ENDIAN);
        buffer.putBytes(limit + headerLength, bytes, 0, length);

        return this;
    }


    public String toString()
    {
        return appendTo(new StringBuilder(100)).toString();
    }

    public StringBuilder appendTo(final StringBuilder builder)
    {
        CarDecoder writer = new CarDecoder();
        writer.wrap(buffer, offset, BLOCK_LENGTH, SCHEMA_VERSION);

        return writer.appendTo(builder);
    }
}
