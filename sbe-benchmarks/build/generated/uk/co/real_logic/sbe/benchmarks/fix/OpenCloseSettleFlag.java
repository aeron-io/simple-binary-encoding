/* Generated SBE (Simple Binary Encoding) message codec */
package uk.co.real_logic.sbe.benchmarks.fix;

public enum OpenCloseSettleFlag
{
    THEORETICAL_PRICE_VALUE(5),

    ACTUAL_PRELIMINARY_NOT_ROUNDED(100),

    ACTUAL_PRELIMINARY_ROUNDED(101),

    /**
     * To be used to represent not present or null.
     */
    NULL_VAL(65535);

    private final int value;

    OpenCloseSettleFlag(final int value)
    {
        this.value = value;
    }

    public int value()
    {
        return value;
    }

    public static OpenCloseSettleFlag get(final int value)
    {
        switch (value)
        {
            case 5: return THEORETICAL_PRICE_VALUE;
            case 100: return ACTUAL_PRELIMINARY_NOT_ROUNDED;
            case 101: return ACTUAL_PRELIMINARY_ROUNDED;
        }

        if (65535 == value)
        {
            return NULL_VAL;
        }

        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
