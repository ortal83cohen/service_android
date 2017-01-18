package com.hpe.sb.mobile.app.infra.restclient.customrequest;

import android.content.Context;
import android.util.Log;
import com.android.volley.*;
import com.hpe.sb.mobile.app.infra.restclient.HttpHeadersUtil;
import com.hpe.sb.mobile.app.infra.restclient.RetryPolicyUtil;
import com.hpe.sb.mobile.app.common.utils.LogTagConstants;

import java.util.Map;

/**
 * Created by malikdav on 24/03/2016.
 */
public class RequestFactory {

    private final HttpHeadersUtil httpHeadersUtil;
    private RetryPolicyUtil retryPolicyUtil;

    public RequestFactory(RetryPolicyUtil retryPolicyUtil, HttpHeadersUtil httpHeadersUtil) {
        this.httpHeadersUtil = httpHeadersUtil;
        this.retryPolicyUtil = retryPolicyUtil;
    }

    public <T> GsonRequest<T, ?> createGsonRequest(final String url,
                                                Class<T> clazz,
                                                Context context,
                                                final Response.Listener<T> successListener,
                                                final Response.ErrorListener errorListener) {
        return createGsonRequest(url, null, clazz, Request.Method.GET, context, successListener, errorListener);
    }

    public <T, R> GsonRequest<T, R> createGsonRequest(String url,
                                                   final R body,
                                                   Class<T> clazz,
                                                   final int method,
                                                   Context context,
                                                   Response.Listener<T> successListener,
                                                   Response.ErrorListener errorListener) {
        GsonRequest<T, R> gsonRequest = new GsonRequest<T, R>(url, clazz, method, body, new BasicSuccessListener<>(successListener, url),
                new BasicErrorListener(errorListener, url)) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return httpHeadersUtil.getHttpHeaders();
            }

        };
        gsonRequest.setRetryPolicy(retryPolicyUtil.getRetryPolicy());
        gsonRequest.setTag(context);

        return gsonRequest;
    }

    public <R> com.hpe.sb.mobile.app.infra.restclient.customrequest.StringRequest createStringRequest(int method,
                                                                                                      String url,
                                                                                                      final R body,
                                                                                                      Context context,
                                                                                                      Response.Listener<String> successListener,
                                                                                                      Response.ErrorListener error) {
        com.hpe.sb.mobile.app.infra.restclient.customrequest.StringRequest stringRequest = new com.hpe.sb.mobile.app.infra.restclient.customrequest.StringRequest<R>(method,
                url,
                body,
                new BasicSuccessListener<>(successListener, url),
                new BasicErrorListener(error, url)) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return httpHeadersUtil.getHttpHeaders();
            }
        };
        stringRequest.setRetryPolicy(retryPolicyUtil.getRetryPolicy());
        stringRequest.setTag(context);

        return stringRequest;
    }

    private class BasicErrorListener implements Response.ErrorListener {

        private Response.ErrorListener errorListener;
        private String url;

        public BasicErrorListener(Response.ErrorListener errorListener, String url) {
            this.errorListener = errorListener;
            this.url = url;
        }

        @Override
        public void onErrorResponse(VolleyError error) {
            NetworkResponse networkResponse = error.networkResponse;
            int statusCode = networkResponse != null ? networkResponse.statusCode :  -1;
            String msg = "Rest to " + url + "returned with failed response, error: " + error.getMessage() + " status code: " + statusCode;
            //also prints stack trace
            Log.e(getClass().getName(), msg, error);
            if (networkResponse != null && networkResponse.data != null) {
                Log.e(getClass().getName(), new String(networkResponse.data));
            }
            if (errorListener != null) {
                errorListener.onErrorResponse(error);
            }
        }
    }

    private class BasicSuccessListener<T> implements Response.Listener<T> {

        private final Response.Listener<T> listener;
        private final String url;

        public BasicSuccessListener(Response.Listener<T> listener, String url) {
            this.listener = listener;
            this.url = url;
        }

        @Override
        public void onResponse(T t) {
            if (url != null) {
                Log.d(LogTagConstants.REST, url + " was successful");
            }
            if (listener != null) {
                listener.onResponse(t);
            }
        }
    }

}
