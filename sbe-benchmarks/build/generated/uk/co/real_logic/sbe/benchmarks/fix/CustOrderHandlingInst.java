/* Generated SBE (Simple Binary Encoding) message codec */
package uk.co.real_logic.sbe.benchmarks.fix;

public enum CustOrderHandlingInst
{
    PHONE_SIMPLE((byte)65),

    PHONE_COMPLEX((byte)66),

    FCM_PROVIDED_SCREEN((byte)67),

    OTHER_PROVIDED_SCREEN((byte)68),

    CLIENT_PROVIDED_PLATFORM_CONTROLLED_BY_FCM((byte)69),

    CLIENT_PROVIDED_PLATFORM_DIRECT_TO_EXCHANGE((byte)70),

    FCM_API_OR_FIX((byte)71),

    ALGO_ENGINE((byte)72),

    PRICE_AT_EXECUTION((byte)74),

    DESK_ELECTRONIC((byte)87),

    DESK_PIT((byte)88),

    CLIENT_ELECTRONIC((byte)89),

    CLIENT_PIT((byte)90),

    /**
     * To be used to represent not present or null.
     */
    NULL_VAL((byte)0);

    private final byte value;

    CustOrderHandlingInst(final byte value)
    {
        this.value = value;
    }

    public byte value()
    {
        return value;
    }

    public static CustOrderHandlingInst get(final byte value)
    {
        switch (value)
        {
            case 65: return PHONE_SIMPLE;
            case 66: return PHONE_COMPLEX;
            case 67: return FCM_PROVIDED_SCREEN;
            case 68: return OTHER_PROVIDED_SCREEN;
            case 69: return CLIENT_PROVIDED_PLATFORM_CONTROLLED_BY_FCM;
            case 70: return CLIENT_PROVIDED_PLATFORM_DIRECT_TO_EXCHANGE;
            case 71: return FCM_API_OR_FIX;
            case 72: return ALGO_ENGINE;
            case 74: return PRICE_AT_EXECUTION;
            case 87: return DESK_ELECTRONIC;
            case 88: return DESK_PIT;
            case 89: return CLIENT_ELECTRONIC;
            case 90: return CLIENT_PIT;
        }

        if ((byte)0 == value)
        {
            return NULL_VAL;
        }

        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
