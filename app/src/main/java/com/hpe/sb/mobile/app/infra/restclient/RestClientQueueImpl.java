package com.hpe.sb.mobile.app.infra.restclient;

import android.content.Context;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

import java.io.File;

/**
 * Google volley request queue rest client.
 */
public class RestClientQueueImpl implements RestClientQueue {

    private static final String DEFAULT_CACHE_DIR = "serviceBrokerCache";
    // Default disk cache size
    private static final int DEFAULT_DISK_USAGE_BYTES = 5 * 1024 * 1024;

    private RequestQueue volleyRequestQueue;
    private Context context;

    public RestClientQueueImpl(Context context) {
        this.context = context;
        volleyRequestQueue =  getRequestQueue();
    }

    /**
     * Get Volley request Queue with the following attributes
     * Disk size cache = {@value #DEFAULT_DISK_USAGE_BYTES} Bytes
     * The underlying thread pool size is the Volley library default (4 for version 1.0)
     * @return RequestQueue
     */
    @Override
    public RequestQueue getRequestQueue() {
        //TODO: synchronize or eager initialisation!
        if(volleyRequestQueue == null) {

            File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);
            DiskBasedCache cache = new DiskBasedCache(cacheDir, DEFAULT_DISK_USAGE_BYTES);

            HurlStack httpStack = new HurlStack();
            Network network = new BasicNetwork(httpStack);

            volleyRequestQueue = new RequestQueue(cache, network);
            volleyRequestQueue.start();
        }

        return volleyRequestQueue;
    }

    /**
     * Adds a Request to the dispatch queue.
     * @param req The request to service
     */
    @Override
    public <T> Request<T> addToRequestQueue(Request<T> req) {
        return getRequestQueue().add(req);
    }

    /**
     * Cancels all requests in this queue with the given tag. Tag must be non-null
     * and equality is by identity.
     */
    @Override
    public void cancelAll(final Object tag) {
        if(volleyRequestQueue != null) {
            volleyRequestQueue.cancelAll(tag);
        }
    }
}
