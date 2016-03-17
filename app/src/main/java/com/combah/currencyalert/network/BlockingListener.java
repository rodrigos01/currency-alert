package com.combah.currencyalert.network;

import com.android.volley.Response;
import com.android.volley.VolleyError;

/**
 * Created by rodrigo on 12/17/15.
 */
public class BlockingListener<T> implements Listener<T> {

    T mResponse;

    @Override
    public synchronized void onResponse(T response) {
        mResponse = response;
        notify();
    }

    public synchronized T getResponse() throws InterruptedException {
        wait();
        return mResponse;
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        mResponse = null;
        notify();
    }
}
