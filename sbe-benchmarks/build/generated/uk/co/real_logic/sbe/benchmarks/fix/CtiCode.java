/* Generated SBE (Simple Binary Encoding) message codec */
package uk.co.real_logic.sbe.benchmarks.fix;

public enum CtiCode
{
    OWN((byte)49),

    HOUSE((byte)50),

    ON_FLOOR((byte)51),

    NOT_ON_FLOOR((byte)52),

    /**
     * To be used to represent not present or null.
     */
    NULL_VAL((byte)0);

    private final byte value;

    CtiCode(final byte value)
    {
        this.value = value;
    }

    public byte value()
    {
        return value;
    }

    public static CtiCode get(final byte value)
    {
        switch (value)
        {
            case 49: return OWN;
            case 50: return HOUSE;
            case 51: return ON_FLOOR;
            case 52: return NOT_ON_FLOOR;
        }

        if ((byte)0 == value)
        {
            return NULL_VAL;
        }

        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
