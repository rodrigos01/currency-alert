package com.combah.currencyalert.network;

import com.android.volley.Response;

import java.util.List;

/**
 * Created by rodrigo on 3/17/16.
 */
public interface CurrencyConnector {

    void getAllCurrencies(Response.Listener<YahooResponse> listener);

    void getCurrencies(List<String> symbols, Response.Listener<YahooResponse> listener);

    void getCurrency(String symbol, Response.Listener<YahooResponse> listener);

}
