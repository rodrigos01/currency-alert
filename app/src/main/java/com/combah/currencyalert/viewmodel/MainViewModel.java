package com.combah.currencyalert.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.os.Handler;
import android.preference.PreferenceManager;

import com.combah.currencyalert.content.Provider;
import com.combah.currencyalert.model.Currency;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

/**
 * Created by rodrigo on 3/17/16.
 */
public class MainViewModel extends Observable {

    private Context context;
    private Map<String, Currency> currencies;

    public MainViewModel(Context context) {

        this.context = context;
        currencies = new HashMap<>();

        refreshCurrencies(context);
        context.getContentResolver()
                .registerContentObserver(Provider.CONTENT_URI, true, currenciesObserver);
    }

    private void refreshCurrencies(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> currenciesSymbols = preferences.getStringSet("currencies", null);
        if (currenciesSymbols != null) {
            for (String symbol : currenciesSymbols) {

                Currency currency = new Currency();
                currency.symbol = symbol;
                currency.value = preferences.getFloat(symbol, 0);

                currencies.put(currency.symbol, currency);
            }
            setChanged();
            notifyObservers(getCurrencies());
        }
    }

    public List<Currency> getCurrencies() {
        List<Currency> currencyList = new ArrayList<>();
        for (String symbol: currencies.keySet()) {
            Currency currency = currencies.get(symbol);
            currencyList.add(currency);
        }
        return currencyList;
    }

    private ContentObserver currenciesObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            refreshCurrencies(context);
        }
    };
}
