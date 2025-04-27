/* Generated SBE (Simple Binary Encoding) message codec */
package uk.co.real_logic.sbe.benchmarks.fix;

public enum NoAllocs
{
    ONE((byte)49),

    /**
     * To be used to represent not present or null.
     */
    NULL_VAL((byte)0);

    private final byte value;

    NoAllocs(final byte value)
    {
        this.value = value;
    }

    public byte value()
    {
        return value;
    }

    public static NoAllocs get(final byte value)
    {
        switch (value)
        {
            case 49: return ONE;
        }

        if ((byte)0 == value)
        {
            return NULL_VAL;
        }

        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
