/* Generated SBE (Simple Binary Encoding) message codec */
package uk.co.real_logic.sbe.benchmarks.fix;

public enum OrdType
{
    MARKET_ORDER((byte)49),

    LIMIT_ORDER((byte)50),

    STOP_ORDER((byte)51),

    STOP_LIMIT_ORDER((byte)52),

    MARKET_LIMIT_ORDER((byte)75),

    /**
     * To be used to represent not present or null.
     */
    NULL_VAL((byte)0);

    private final byte value;

    OrdType(final byte value)
    {
        this.value = value;
    }

    public byte value()
    {
        return value;
    }

    public static OrdType get(final byte value)
    {
        switch (value)
        {
            case 49: return MARKET_ORDER;
            case 50: return LIMIT_ORDER;
            case 51: return STOP_ORDER;
            case 52: return STOP_LIMIT_ORDER;
            case 75: return MARKET_LIMIT_ORDER;
        }

        if ((byte)0 == value)
        {
            return NULL_VAL;
        }

        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
