package com.digitalbank.common.money;

import java.util.Currency;

/** Thrown when two amounts in different currencies are combined without an explicit conversion. */
public class CurrencyMismatchException extends IllegalArgumentException {

    public CurrencyMismatchException(Currency expected, Currency actual) {
        super("Currency mismatch: expected " + expected.getCurrencyCode() + " but was " + actual.getCurrencyCode());
    }
}
