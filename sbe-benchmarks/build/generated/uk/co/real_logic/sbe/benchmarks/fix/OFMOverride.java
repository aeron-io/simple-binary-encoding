/* Generated SBE (Simple Binary Encoding) message codec */
package uk.co.real_logic.sbe.benchmarks.fix;

public enum OFMOverride
{
    ENABLED((byte)89),

    DISABLED((byte)78),

    /**
     * To be used to represent not present or null.
     */
    NULL_VAL((byte)0);

    private final byte value;

    OFMOverride(final byte value)
    {
        this.value = value;
    }

    public byte value()
    {
        return value;
    }

    public static OFMOverride get(final byte value)
    {
        switch (value)
        {
            case 89: return ENABLED;
            case 78: return DISABLED;
        }

        if ((byte)0 == value)
        {
            return NULL_VAL;
        }

        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
