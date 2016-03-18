package com.combah.currencyalert.content;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

/**
 * Created by rodrigo on 3/17/16.
 */
public class SyncManager {

    public static final String ACCOUNT_TYPE = "currencyalert.combah.com";
    public static final String DUMMY_ACCOUNT_NAME = "Currencies";

    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 60L;
    public static final long SYNC_INTERVAL =
            SYNC_INTERVAL_IN_MINUTES *
                    SECONDS_PER_MINUTE;

    Context context;
    AccountManager accountManager;

    public SyncManager(Context context) {
        this.context = context;
        accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
    }

    public Account getSyncAccount() {
        Account[] accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
        if (accounts.length > 0) {
            return accounts[0];
        }
        return createSyncAccount();
    }

    private Account createSyncAccount() {

        Account account = new Account(DUMMY_ACCOUNT_NAME, ACCOUNT_TYPE);
        accountManager.addAccountExplicitly(account, null, null);
        ContentResolver.setSyncAutomatically(account, Provider.AUTHORITY, true);
        ContentResolver.addPeriodicSync(account, Provider.AUTHORITY, Bundle.EMPTY, SYNC_INTERVAL);
        return account;
    }

    public void requestSync() {
        Account account = getSyncAccount();
        ContentResolver.requestSync(account, Provider.AUTHORITY, Bundle.EMPTY);
    }

}
