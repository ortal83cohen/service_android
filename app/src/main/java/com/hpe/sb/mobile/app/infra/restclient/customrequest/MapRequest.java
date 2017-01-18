package com.hpe.sb.mobile.app.infra.restclient.customrequest;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.reflect.TypeToken;
import com.hpe.sb.mobile.app.common.utils.JsonTranslation;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.Map;

public class MapRequest<K,V> extends Request<Map<K,V>> {

    private final Response.Listener<Map<K,V>> listener;

    /**
     * Make a GET request and return a parsed map from JSON.
     *
     * @param url URL of the request to make
     */
    public MapRequest(int method, String url, Response.Listener<Map<K,V>> listener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = listener;
    }

    @Override
    protected void deliverResponse(Map<K,V> response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<Map<K,V>> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(
                    response.data,
                    HttpHeaderParser.parseCharset(response.headers));
            Type listType = new TypeToken<Map<K,V>>(){}.getType();
            Map<K,V> map = JsonTranslation.jsonString2Object(jsonString, listType);
            return Response.success(
                    map,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        }
    }
}
