/* Generated SBE (Simple Binary Encoding) message codec */
package uk.co.real_logic.sbe.benchmarks.fix;

public enum Side
{
    BUY((byte)49),

    SELL((byte)50),

    /**
     * To be used to represent not present or null.
     */
    NULL_VAL((byte)0);

    private final byte value;

    Side(final byte value)
    {
        this.value = value;
    }

    public byte value()
    {
        return value;
    }

    public static Side get(final byte value)
    {
        switch (value)
        {
            case 49: return BUY;
            case 50: return SELL;
        }

        if ((byte)0 == value)
        {
            return NULL_VAL;
        }

        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
