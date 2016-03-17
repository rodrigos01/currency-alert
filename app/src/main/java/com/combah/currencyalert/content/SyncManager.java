package com.combah.currencyalert.content;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;

/**
 * Created by rodrigo on 3/17/16.
 */
public class SyncManager {

    public static final String ACCOUNT_TYPE = "currencyalert.combah.com";
    public static final String DUMMY_ACCOUNT_NAME = "Currencies";

    AccountManager accountManager;

    public SyncManager(Context context) {
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
        return account;
    }

}
