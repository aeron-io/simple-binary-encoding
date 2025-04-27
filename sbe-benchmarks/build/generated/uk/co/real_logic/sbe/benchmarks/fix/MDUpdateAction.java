/* Generated SBE (Simple Binary Encoding) message codec */
package uk.co.real_logic.sbe.benchmarks.fix;

public enum MDUpdateAction
{
    NEW((short)0),

    CHANGE((short)1),

    DELETE((short)2),

    OVERLAY((short)5),

    /**
     * To be used to represent not present or null.
     */
    NULL_VAL((short)255);

    private final short value;

    MDUpdateAction(final short value)
    {
        this.value = value;
    }

    public short value()
    {
        return value;
    }

    public static MDUpdateAction get(final short value)
    {
        switch (value)
        {
            case 0: return NEW;
            case 1: return CHANGE;
            case 2: return DELETE;
            case 5: return OVERLAY;
        }

        if ((short)255 == value)
        {
            return NULL_VAL;
        }

        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
