package com.combah.currencyalert.network;

import com.android.volley.Response;

/**
 * Created by rodrigo on 3/17/16.
 */
public interface Listener<T> extends Response.Listener<T>, Response.ErrorListener {
}
