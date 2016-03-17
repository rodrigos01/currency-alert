package com.combah.currencyalert.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import java.util.List;

/**
 * Created by rodrigo on 3/17/16.
 */
public class YahooConnector implements CurrencyConnector {

    private RequestQueue queue;
    private String baseURL;


    public YahooConnector(Context context) {

        queue = Volley.newRequestQueue(context);
        baseURL = "http://finance.yahoo.com/webservice/v1/symbols";

    }

    @Override
    public void getAllCurrencies(Response.Listener<YahooResponse> listener) {
        String url = String.format("%s/allcurrencies/quote?format=json", baseURL);
        GsonRequest<YahooResponse> request = new GsonRequest<>(Request.Method.GET, url, YahooResponse.class, listener, null);
        queue.add(request);
    }

    @Override
    public void getCurrencies(List<String> symbols, Response.Listener<YahooResponse> listener) {

        StringBuilder queryBuilder = new StringBuilder();
        for (int i = 0; i < symbols.size(); i++) {
            if (i > 0) {
                queryBuilder.append(",");
            }
            queryBuilder.append(symbols.get(i));
        }

        String url = String.format("%s/%s/quote?format=json", baseURL, queryBuilder.toString());
        GsonRequest<YahooResponse> request = new GsonRequest<>(Request.Method.GET, url, YahooResponse.class, listener, null);
        queue.add(request);
    }

    @Override
    public void getCurrency(String symbol, Response.Listener<YahooResponse> listener) {
        String url = String.format("%s/%s/quote?format=json", baseURL, symbol);
        GsonRequest<YahooResponse> request = new GsonRequest<>(Request.Method.GET, url, YahooResponse.class, listener, null);
        queue.add(request);
    }
}
