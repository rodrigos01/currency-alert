package com.combah.currencyalert.content;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v4.app.NotificationCompat;

import com.android.volley.Response;
import com.combah.currencyalert.R;
import com.combah.currencyalert.model.Currency;
import com.combah.currencyalert.network.CurrencyConnector;
import com.combah.currencyalert.network.YahooConnector;
import com.combah.currencyalert.network.YahooResponse;
import com.combah.currencyalert.view.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by rodrigo on 3/17/16.
 */
public class SyncManager {

    public static final long MILLISECONDS_PER_SECOND = 1000;
    public static final long SECONDS_PER_MINUTE = 60;
    public static final long SYNC_INTERVAL_IN_MINUTES = 1;
    public static final long SYNC_INTERVAL =
            MILLISECONDS_PER_SECOND *
                    SYNC_INTERVAL_IN_MINUTES *
                    SECONDS_PER_MINUTE;

    ContentResolver resolver;
    Context context;

    private static SyncManager instance;

    public static SyncManager getInstance(Context context) {
        if (instance == null) {
            instance = new SyncManager(context);
        }
        return instance;
    }

    private SyncManager(Context context) {
        this.context = context;
        resolver = context.getContentResolver();

        JobScheduler jobScheduler = (JobScheduler)
                context.getSystemService(Context.JOB_SCHEDULER_SERVICE);

        JobInfo jobInfo = new JobInfo.Builder(1,
                new ComponentName(context.getPackageName(), SyncService.class.getName()))
                .setPersisted(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPeriodic(SYNC_INTERVAL)
                .build();
        jobScheduler.schedule(jobInfo);
    }

    public void sync() {
        CurrencyConnector connector = new YahooConnector(context);

        Cursor cursor = resolver.query(Provider.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.getCount() > 0) {

            List<String> symbolList = new ArrayList<>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                symbolList.add(cursor.getString(cursor.getColumnIndex("symbol")));
                cursor.moveToNext();
            }
            connector.getCurrencies(symbolList, exchangeListener);
            cursor.close();
        }
    }

    private Response.Listener<YahooResponse> exchangeListener = new Response.Listener<YahooResponse>() {
        @Override
        public void onResponse(YahooResponse response) {
            Map<String, Float> map = new HashMap<>();
            for (YahooResponse.ResourceWrapper resourceWrapper : response.list.resources) {
                Currency currency = resourceWrapper.resource.currency;

                Cursor cursor = resolver.query(Provider.CURRENCY_CONTENT_URI,
                        null,
                        "symbol=?",
                        new String[]{currency.symbol},
                        null);
                if (cursor != null && cursor.getCount() > 0) {
                    cursor.moveToFirst();
                    float oldValue = cursor.getFloat(cursor.getColumnIndex("value"));
                    if (oldValue - currency.value > 0.001) {
                        map.put(currency.symbol, (float) currency.value);
                    }
                    cursor.close();
                }

                ContentValues values = new ContentValues();
                values.put("symbol", currency.symbol);
                values.put("value", currency.value);
                resolver.insert(Provider.CURRENCY_CONTENT_URI, values);

            }
            if (!map.isEmpty()) {
                sendNotification(map);
            }
        }
    };

    private void sendNotification(Map<String, Float> values) {
        Intent intent = new Intent(context, MainActivity.class);

        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        StringBuilder messageBuilder = new StringBuilder();
        for (String symbol : values.keySet()) {
            float value = values.get(symbol);
            messageBuilder.append(String.format(Locale.getDefault(), "%s: %.3f\n", symbol, value));
        }

        String message = messageBuilder.toString();

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
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
                (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, mBuilder.build());
    }

}
