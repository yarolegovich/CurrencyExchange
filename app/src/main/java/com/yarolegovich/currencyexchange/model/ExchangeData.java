package com.yarolegovich.currencyexchange.model;

import java.util.Collections;
import java.util.List;

/**
 * Created by yarolegovich on 22.02.2017.
 */

public class ExchangeData {

    private final Currency from;
    private final Currency to;
    private final List<ExchangeRate> exchangeRates;

    public ExchangeData(Currency to, List<ExchangeRate> exchangeRates) {
        this.from = Currency.USD;
        this.to = to;
        this.exchangeRates = Collections.unmodifiableList(exchangeRates);
    }

    public Currency getFromCurrency() {
        return from;
    }

    public Currency getToCurrency() {
        return to;
    }

    public List<ExchangeRate> getExchangeRates() {
        return exchangeRates;
    }

    @Override
    public String toString() {
        return "ExchangeData{" +
                "from=" + from +
                ", to=" + to +
                ", exchangeRates=" + exchangeRates +
                '}';
    }
}
