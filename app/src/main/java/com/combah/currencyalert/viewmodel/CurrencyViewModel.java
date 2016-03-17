package com.combah.currencyalert.viewmodel;

import com.combah.currencyalert.model.Currency;

import java.util.Locale;

/**
 * Created by rodrigo on 3/17/16.
 */
public class CurrencyViewModel {

    public String symbol;
    public double value;

    public CurrencyViewModel(Currency currency) {
        symbol = currency.symbol;
        value = currency.value;
    }

    public String getValue() {
        return String.format(Locale.getDefault(), "%.3f", value);
    }
}
