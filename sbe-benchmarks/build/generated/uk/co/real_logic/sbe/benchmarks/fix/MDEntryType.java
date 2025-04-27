/* Generated SBE (Simple Binary Encoding) message codec */
package uk.co.real_logic.sbe.benchmarks.fix;

public enum MDEntryType
{
    BID((byte)48),

    OFFER((byte)49),

    TRADE((byte)50),

    OPENING_PRICE((byte)52),

    SETTLEMENT_PRICE((byte)54),

    TRADING_SESSION_HIGH_PRICE((byte)55),

    TRADING_SESSION_LOW_PRICE((byte)56),

    TRADE_VOLUME((byte)66),

    OPEN_INTEREST((byte)67),

    SIMULATED_SELL((byte)69),

    SIMULATED_BUY((byte)70),

    EMPTY_THE_BOOK((byte)74),

    SESSION_HIGH_BID((byte)78),

    SESSION_LOW_OFFER((byte)79),

    FIXING_PRICE((byte)87),

    CASH_NOTE((byte)88),

    /**
     * To be used to represent not present or null.
     */
    NULL_VAL((byte)0);

    private final byte value;

    MDEntryType(final byte value)
    {
        this.value = value;
    }

    public byte value()
    {
        return value;
    }

    public static MDEntryType get(final byte value)
    {
        switch (value)
        {
            case 48: return BID;
            case 49: return OFFER;
            case 50: return TRADE;
            case 52: return OPENING_PRICE;
            case 54: return SETTLEMENT_PRICE;
            case 55: return TRADING_SESSION_HIGH_PRICE;
            case 56: return TRADING_SESSION_LOW_PRICE;
            case 66: return TRADE_VOLUME;
            case 67: return OPEN_INTEREST;
            case 69: return SIMULATED_SELL;
            case 70: return SIMULATED_BUY;
            case 74: return EMPTY_THE_BOOK;
            case 78: return SESSION_HIGH_BID;
            case 79: return SESSION_LOW_OFFER;
            case 87: return FIXING_PRICE;
            case 88: return CASH_NOTE;
        }

        if ((byte)0 == value)
        {
            return NULL_VAL;
        }

        throw new IllegalArgumentException("Unknown value: " + value);
    }
}
