package com.digitalbank.common.money;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    private static final Currency AZN = Currency.getInstance("AZN");
    private static final Currency USD = Currency.getInstance("USD");

    @Test
    void amountIsNormalisedToCurrencyScale() {
        assertThat(Money.of("10", "AZN").amount()).isEqualByComparingTo("10.00").hasScaleOf(2);
        assertThat(Money.of("1000", "JPY").amount()).hasScaleOf(0);
    }

    @Test
    void usesBankersRounding() {
        assertThat(Money.of("2.345", "AZN").amount()).isEqualByComparingTo("2.34");
        assertThat(Money.of("2.355", "AZN").amount()).isEqualByComparingTo("2.36");
    }

    @Test
    void addsAndSubtractsInSameCurrency() {
        Money balance = Money.of("100.10", "AZN").add(Money.of("0.20", "AZN")).subtract(Money.of("50", "AZN"));

        assertThat(balance).isEqualTo(Money.of("50.30", "AZN"));
    }

    @Test
    void avoidsFloatingPointErrors() {
        Money sum = Money.of("0.10", "AZN").add(Money.of("0.20", "AZN"));

        assertThat(sum).isEqualTo(Money.of("0.30", "AZN"));
    }

    @Test
    void rejectsMixingCurrencies() {
        assertThatThrownBy(() -> Money.of("1", "AZN").add(Money.of("1", "USD")))
                .isInstanceOf(CurrencyMismatchException.class)
                .hasMessageContaining("AZN").hasMessageContaining("USD");
    }

    @Test
    void convertsWithRate() {
        Money usd = Money.of("100.00", "USD").convert(AZN, new BigDecimal("1.7000"));

        assertThat(usd).isEqualTo(new Money(new BigDecimal("170.00"), AZN));
    }

    @Test
    void rejectsNonPositiveRate() {
        assertThatThrownBy(() -> Money.zero(USD).convert(AZN, BigDecimal.ZERO))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void comparesAndReportsSign() {
        Money ten = Money.of("10", "AZN");

        assertThat(ten.isGreaterThan(Money.of("9.99", "AZN"))).isTrue();
        assertThat(ten.isPositive()).isTrue();
        assertThat(ten.negate().isNegative()).isTrue();
        assertThat(Money.zero(AZN).isPositive()).isFalse();
    }

    @Test
    void printsAmountAndCurrency() {
        assertThat(Money.of("1234.5", "AZN")).hasToString("1234.50 AZN");
    }
}
