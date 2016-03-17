package com.combah.currencyalert.network;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rodrigo.santos on 7/14/15.
 */
public class GsonRequest<T> extends Request<T> {

    private Response.Listener<T> mListener;
    private Response.ErrorListener mErrorListener;
    private HashMap<String, String> mHeaders;
    private Map<String, String> mParams;

    private Gson mGson;
    private Class<T> mClazz;

    public GsonRequest(int method, String url, Class<T> clazz, Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);

        mGson = new Gson();
        mListener = listener;
        mErrorListener = errorListener;
        mClazz = clazz;

        mHeaders = new HashMap<>();
        mParams = new HashMap<>();
    }

    @Override
    public Map<String, String> getHeaders() throws AuthFailureError {
        return mHeaders;
    }

    public void addHeader(String key, String value) {
        mHeaders.put(key, value);
    }

    @Override
    protected Map<String, String> getParams() {
        return mParams;
    }

    public void addParam(String key, String value) {
        mParams.put(key, value);
    }

    public void setParams(Map<String, String> mParams) {
        this.mParams = mParams;
    }

    public void setHeaders(HashMap<String, String> mHeaders) {
        this.mHeaders = mHeaders;
    }

    public void setErrorListener(Response.ErrorListener mErrorListener) {
        this.mErrorListener = mErrorListener;
    }

    public Response.ErrorListener getErrorListener() {
        return mErrorListener;
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            return Response.success(mGson.fromJson(jsonString, mClazz),
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(T response) {
        mListener.onResponse(response);
    }

    @Override
    public void deliverError(VolleyError error) {
        if (mErrorListener != null) {
            mErrorListener.onErrorResponse(error);
        }
    }
}
