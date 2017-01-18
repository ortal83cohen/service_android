package com.hpe.sb.mobile.app.infra.restclient;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

/**
 * Created by malikdav on 13/03/2016.
 */
public interface RestClientQueue {

    RequestQueue getRequestQueue();

    <T> Request<T> addToRequestQueue(Request<T> req);

    void cancelAll(Object tag);
}
