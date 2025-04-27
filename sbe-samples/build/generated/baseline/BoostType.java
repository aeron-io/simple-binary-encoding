/* Generated SBE (Simple Binary Encoding) message codec */
package baseline;

public enum BoostType
{
    TURBO((byte)84),

    SUPERCHARGER((byte)83),

    NITROUS((byte)78),

    KERS((byte)75),

    /**
     * To be used to represent not present or null.
     */
    NULL_VAL((byte)0);

    private final byte value;

    BoostType(final byte value)
    {
        this.value = value;
    }

    public byte value()
    {
        return value;
    }

    public static BoostType get(final byte value)
    {
        switch (value)
        {
            case 84: return TURBO;
            case 83: return SUPERCHARGER;
            case 78: return NITROUS;
            case 75: return KERS;
        }

        if ((byte)0 == value)
        {
            return NULL_VAL;
        }

        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
