/* Generated SBE (Simple Binary Encoding) message codec */
package uk.co.real_logic.sbe.benchmarks.fix;

public enum TimeInForce
{
    DAY((byte)48),

    GOOD_TILL_CANCEL((byte)49),

    FILL_AND_KILL((byte)51),

    GOOD_TILL_DATE((byte)54),

    /**
     * To be used to represent not present or null.
     */
    NULL_VAL((byte)0);

    private final byte value;

    TimeInForce(final byte value)
    {
        this.value = value;
    }

    public byte value()
    {
        return value;
    }

    public static TimeInForce get(final byte value)
    {
        switch (value)
        {
            case 48: return DAY;
            case 49: return GOOD_TILL_CANCEL;
            case 51: return FILL_AND_KILL;
            case 54: return GOOD_TILL_DATE;
        }

        if ((byte)0 == value)
        {
            return NULL_VAL;
        }

        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
