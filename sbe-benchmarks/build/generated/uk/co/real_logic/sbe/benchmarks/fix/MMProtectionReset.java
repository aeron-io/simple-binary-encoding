/* Generated SBE (Simple Binary Encoding) message codec */
package uk.co.real_logic.sbe.benchmarks.fix;

public enum MMProtectionReset
{
    RESET((byte)89),

    DO_NOT_RESET((byte)78),

    /**
     * To be used to represent not present or null.
     */
    NULL_VAL((byte)0);

    private final byte value;

    MMProtectionReset(final byte value)
    {
        this.value = value;
    }

    public byte value()
    {
        return value;
    }

    public static MMProtectionReset get(final byte value)
    {
        switch (value)
        {
            case 89: return RESET;
            case 78: return DO_NOT_RESET;
        }

        if ((byte)0 == value)
        {
            return NULL_VAL;
        }

        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
