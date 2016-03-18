package com.combah.currencyalert.viewmodel;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.databinding.Bindable;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;

import com.combah.currencyalert.content.Provider;
import com.combah.currencyalert.content.SyncManager;
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

    private SyncManager manager;
    private ContentResolver resolver;

    public MainViewModel(Context context) {

        manager = SyncManager.getInstance(context);
        resolver = context.getContentResolver();

        context.getContentResolver()
                .registerContentObserver(Provider.CONTENT_URI, true, currenciesObserver);
    }

    public List<Currency> getCurrencies() {
        List<Currency> currencyList = new ArrayList<>();
        Cursor cursor = resolver.query(Provider.CURRENCY_CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Currency currency = new Currency();
                currency.symbol = cursor.getString(cursor.getColumnIndex("symbol"));
                currency.value = cursor.getFloat(cursor.getColumnIndex("value"));
                currencyList.add(currency);
                cursor.moveToNext();
            }
            cursor.close();
        }
        return currencyList;
    }

    public void sync() {
        manager.sync();
    }

    public void addSymbol(String symbol) {
        ContentValues values = new ContentValues();
        values.put("symbol", symbol);
        resolver.insert(Provider.CONTENT_URI, values);
        sync();
    }

    private ContentObserver currenciesObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            setChanged();
            notifyObservers(getCurrencies());
        }
    };
}
