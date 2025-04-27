/* Generated SBE (Simple Binary Encoding) message codec */
package uk.co.real_logic.sbe.benchmarks.fix;

public enum MatchEventIndicator
{
    MID_EVENT((byte)48),

    BEGINNING_EVENT((byte)49),

    END_EVENT((byte)50),

    BEGINNING_AND_END_EVENT((byte)51),

    /**
     * To be used to represent not present or null.
     */
    NULL_VAL((byte)0);

    private final byte value;

    MatchEventIndicator(final byte value)
    {
        this.value = value;
    }

    public byte value()
    {
        return value;
    }

    public static MatchEventIndicator get(final byte value)
    {
        switch (value)
        {
            case 48: return MID_EVENT;
            case 49: return BEGINNING_EVENT;
            case 50: return END_EVENT;
            case 51: return BEGINNING_AND_END_EVENT;
        }

        if ((byte)0 == value)
        {
            return NULL_VAL;
        }

        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
