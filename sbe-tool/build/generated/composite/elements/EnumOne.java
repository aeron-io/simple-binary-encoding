/* Generated SBE (Simple Binary Encoding) message codec */
package composite.elements;

/**
 * enum as uint8
 */
public enum EnumOne
{
    Value1((short)1),

    Value10((short)10),

    /**
     * To be used to represent not present or null.
     */
    NULL_VAL((short)255);

    private final short value;

    EnumOne(final short value)
    {
        this.value = value;
    }

    public short value()
    {
        return value;
    }

    public static EnumOne get(final short value)
    {
        switch (value)
        {
            case 1: return Value1;
            case 10: return Value10;
        }

        if ((short)255 == value)
        {
            return NULL_VAL;
        }

        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
