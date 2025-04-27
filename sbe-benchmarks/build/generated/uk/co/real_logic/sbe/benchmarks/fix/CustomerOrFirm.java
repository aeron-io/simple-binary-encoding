/* Generated SBE (Simple Binary Encoding) message codec */
package uk.co.real_logic.sbe.benchmarks.fix;

public enum CustomerOrFirm
{
    CUSTOMER((short)0),

    FIRM((short)1),

    /**
     * To be used to represent not present or null.
     */
    NULL_VAL((short)255);

    private final short value;

    CustomerOrFirm(final short value)
    {
        this.value = value;
    }

    public short value()
    {
        return value;
    }

    public static CustomerOrFirm get(final short value)
    {
        switch (value)
        {
            case 0: return CUSTOMER;
            case 1: return FIRM;
        }

        if ((short)255 == value)
        {
            return NULL_VAL;
        }

        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
