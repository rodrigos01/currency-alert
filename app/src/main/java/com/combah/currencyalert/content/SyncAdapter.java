package com.combah.currencyalert.content;

import android.accounts.Account;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncResult;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import com.android.volley.Response;
import com.combah.currencyalert.R;
import com.combah.currencyalert.model.Currency;
import com.combah.currencyalert.network.CurrencyConnector;
import com.combah.currencyalert.network.YahooConnector;
import com.combah.currencyalert.network.YahooResponse;
import com.combah.currencyalert.view.MainActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Created by rodrigo on 12/14/15.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        CurrencyConnector connector = new YahooConnector(getContext());
        connector.getAllCurrencies(allCurrenciesListener);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        Set<String> currenciesSymbols = preferences.getStringSet("currencies", null);
        if (currenciesSymbols != null) {
            List<String> symbolsList = new ArrayList<>(currenciesSymbols);
            connector.getCurrencies(symbolsList, exchangeListener);
        }


    }

    private Response.Listener<YahooResponse> allCurrenciesListener = new Response.Listener<YahooResponse>() {
        @Override
        public void onResponse(YahooResponse response) {

            Set<String> currencies = new HashSet<>();
            for (YahooResponse.ResourceWrapper resourceWrapper : response.list.resources) {
                Currency currency = resourceWrapper.resource.currency;
                currencies.add(currency.symbol);
            }

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            SharedPreferences.Editor editor = preferences.edit();
            editor.putStringSet("all_currencies", currencies);
            editor.apply();
            getContext().getContentResolver().notifyChange(Provider.ALL_CURRENCIES_CONTENT_URI, null, false);

        }
    };

    private Response.Listener<YahooResponse> exchangeListener = new Response.Listener<YahooResponse>() {
        @Override
        public void onResponse(YahooResponse response) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
            Set<String> symbols = preferences.getStringSet("currencies", null);
            SharedPreferences.Editor editor = preferences.edit();
            for (YahooResponse.ResourceWrapper resourceWrapper : response.list.resources) {
                Currency currency = resourceWrapper.resource.currency;

                double oldValue = preferences.getFloat(currency.symbol, 0);

                if (oldValue - currency.value > 0.001) {
                    int messageId = 0;
                    if (symbols != null && symbols.contains(currency.symbol)) {
                        messageId = new ArrayList<>(symbols).indexOf(currency.symbol);
                    }
                    sendNotification(currency.symbol, currency.value, messageId);
                }

                editor.putFloat(currency.symbol, (float) currency.value);

            }
            editor.apply();
            getContext().getContentResolver().notifyChange(Provider.CONTENT_URI, null, false);
        }
    };

    private void sendNotification(String symbol, double value, int messageId) {
        Intent intent = new Intent(getContext(), MainActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(getContext(), 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        String message = String.format(Locale.getDefault(), "%s: %.3f", symbol, value);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getContext())
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Currency Down!")
                        .setStyle(new NotificationCompat
                                .BigTextStyle()
                                .bigText(message))
                        .setContentText(message)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setAutoCancel(true);

        mBuilder.setContentIntent(contentIntent);
        android.app.NotificationManager notificationManager =
                (android.app.NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(messageId, mBuilder.build());
    }
}
