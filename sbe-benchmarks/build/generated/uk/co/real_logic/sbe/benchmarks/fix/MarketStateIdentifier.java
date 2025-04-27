/* Generated SBE (Simple Binary Encoding) message codec */
package uk.co.real_logic.sbe.benchmarks.fix;

public enum MarketStateIdentifier
{
    PRE_OPENING((short)0),

    OPENING_MODE((short)1),

    CONTINUOUS_TRADING_MODE((short)2),

    /**
     * To be used to represent not present or null.
     */
    NULL_VAL((short)255);

    private final short value;

    MarketStateIdentifier(final short value)
    {
        this.value = value;
    }

    public short value()
    {
        return value;
    }

    public static MarketStateIdentifier get(final short value)
    {
        switch (value)
        {
            case 0: return PRE_OPENING;
            case 1: return OPENING_MODE;
            case 2: return CONTINUOUS_TRADING_MODE;
        }

        if ((short)255 == value)
        {
            return NULL_VAL;
        }

        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
