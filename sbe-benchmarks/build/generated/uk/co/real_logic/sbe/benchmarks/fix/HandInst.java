/* Generated SBE (Simple Binary Encoding) message codec */
package uk.co.real_logic.sbe.benchmarks.fix;

public enum HandInst
{
    AUTOMATED_EXECUTION((byte)49),

    /**
     * To be used to represent not present or null.
     */
    NULL_VAL((byte)0);

    private final byte value;

    HandInst(final byte value)
    {
        this.value = value;
    }

    public byte value()
    {
        return value;
    }

    public static HandInst get(final byte value)
    {
        switch (value)
        {
            case 49: return AUTOMATED_EXECUTION;
        }

        if ((byte)0 == value)
        {
            return NULL_VAL;
        }

        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
