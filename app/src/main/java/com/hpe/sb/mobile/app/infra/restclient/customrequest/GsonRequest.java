package com.hpe.sb.mobile.app.infra.restclient.customrequest;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonSyntaxException;
import com.hpe.sb.mobile.app.common.utils.JsonTranslation;

import java.io.UnsupportedEncodingException;

/**
 * Created by malikdav on 21/03/2016.
 */
public class GsonRequest<T,R> extends RequestWithBodySupport<T,R> {
    private final Class<T> clazz;
    private final Response.Listener<T> listener;

    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     */
    public GsonRequest(String url, Class<T> clazz,
                       Response.Listener<T> listener, Response.ErrorListener errorListener) {
        this(url, clazz, Method.GET, null, listener, errorListener);
    }
    /**
     * Make a GET request and return a parsed object from JSON.
     *
     * @param url URL of the request to make
     * @param clazz Relevant class object, for Gson's reflection
     */
    public GsonRequest(String url, Class<T> clazz, int method, R body,
                       Response.Listener<T> listener, Response.ErrorListener errorListener) {
        super(method, body, url, errorListener);
        this.clazz = clazz;
        this.listener = listener;
    }

    @Override
    protected void deliverResponse(T response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<T> parseNetworkResponse(NetworkResponse response) {
        try {
            String json = new String(
                    response.data,
                    "utf-8");
            T result = JsonTranslation.jsonString2Object(json, clazz);
            return Response.success(
                    result,
                    HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JsonSyntaxException e) {
            return Response.error(new ParseError(e));
        }
    }
}
