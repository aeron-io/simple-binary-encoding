/* Generated SBE (Simple Binary Encoding) message codec */
package uk.co.real_logic.sbe.benchmarks.fix;

public enum SecurityIDSource
{
    EXCHANGE_SYMBOL((byte)56),

    /**
     * To be used to represent not present or null.
     */
    NULL_VAL((byte)0);

    private final byte value;

    SecurityIDSource(final byte value)
    {
        this.value = value;
    }

    public byte value()
    {
        return value;
    }

    public static SecurityIDSource get(final byte value)
    {
        switch (value)
        {
            case 56: return EXCHANGE_SYMBOL;
        }

        if ((byte)0 == value)
        {
            return NULL_VAL;
        }

        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
