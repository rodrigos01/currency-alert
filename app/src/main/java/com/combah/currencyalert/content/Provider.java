package com.combah.currencyalert.content;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by rodrigo on 3/17/16.
 */
public class Provider extends ContentProvider {

    public static final String AUTHORITY = "com.combah.currencyalert";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final Uri CURRENCY_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/currency");
    public static final Uri ALL_CURRENCIES_CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/all_currencies");

    SharedPreferences preferences;
    ContentResolver contentResolver;

    @Override
    public boolean onCreate() {
        preferences = PreferenceManager.getDefaultSharedPreferences(getContext());
        if (getContext() != null) {
            contentResolver = getContext().getContentResolver();
        }
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        if (uri.equals(CONTENT_URI)) {
            MatrixCursor cursor = new MatrixCursor(new String[]{"symbol", "value"});
            Set<String> currenciesSymbols = preferences.getStringSet("currencies", null);
            if (currenciesSymbols != null) {
                for (String symbol : currenciesSymbols) {
                    cursor.newRow()
                            .add("symbol", symbol);
                }
            }
            return cursor;
        } else if (uri.equals(CURRENCY_CONTENT_URI)) {

            MatrixCursor cursor = new MatrixCursor(new String[]{"symbol", "value"});
            if (selection == null) {
                Set<String> currenciesSymbols = preferences.getStringSet("currencies", null);
                if (currenciesSymbols != null) {
                    for (String symbol : currenciesSymbols) {
                        double value = preferences.getFloat(symbol, 0);

                        cursor.newRow()
                                .add("symbol", symbol)
                                .add("value", value);
                    }
                }
            } else {
                if (selectionArgs.length > 0) {
                    String symbol = selectionArgs[0];

                    double value = preferences.getFloat(symbol, 0);

                    cursor.newRow()
                            .add("symbol", symbol)
                            .add("value", value);
                }
            }

            return cursor;
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (uri.equals(CONTENT_URI)) {
            Set<String> currenciesSymbols = preferences.getStringSet("currencies", null);
            if (currenciesSymbols == null) {
                currenciesSymbols = new HashSet<>();
            }
            String symbol = values.getAsString("symbol");
            currenciesSymbols.add(symbol);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putStringSet("currencies", currenciesSymbols);
            editor.apply();
            if (contentResolver != null) {
                contentResolver.notifyChange(CONTENT_URI, null, false);
            }
        } else if (uri.equals(CURRENCY_CONTENT_URI)) {
            String symbol = values.getAsString("symbol");
            float value = values.getAsFloat("value");
            SharedPreferences.Editor editor = preferences.edit();
            editor.putFloat(symbol, value);
            editor.apply();
            if (contentResolver != null) {
                contentResolver.notifyChange(CONTENT_URI, null, false);
            }
        }
        return uri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (uri.equals(CONTENT_URI)) {
            Set<String> currenciesSymbols = preferences.getStringSet("currencies", null);
            if (currenciesSymbols != null) {
                if (selectionArgs.length > 0) {
                    String symbol = selectionArgs[0];
                    boolean removed = currenciesSymbols.remove(symbol);
                    if (removed) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putStringSet("currencies", currenciesSymbols);
                        editor.apply();
                        if (contentResolver != null) {
                            contentResolver.notifyChange(CONTENT_URI, null, false);
                        }
                        return 1;
                    }
                }
            }
        } else if (uri.equals(CURRENCY_CONTENT_URI)) {
            if (selectionArgs.length > 0) {
                String symbol = selectionArgs[0];
                SharedPreferences.Editor editor = preferences.edit();
                editor.remove(symbol);
                editor.apply();
                if (contentResolver != null) {
                    contentResolver.notifyChange(CONTENT_URI, null, false);
                }
            }
        }
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
