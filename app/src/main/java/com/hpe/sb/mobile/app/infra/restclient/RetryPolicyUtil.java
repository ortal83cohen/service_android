package com.hpe.sb.mobile.app.infra.restclient;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RetryPolicy;

import java.util.concurrent.TimeUnit;

/**
 * Created by malikdav on 24/03/2016.
 */
public class RetryPolicyUtil {

    public static final int TIMEOUT = (int) TimeUnit.SECONDS.toMillis(7);
    public static final int RETRY_COUNT = 1;
    public static final int BACKOFF_MULTIPLIER = 2;

    public RetryPolicy getRetryPolicy() {

        return new DefaultRetryPolicy(TIMEOUT, RETRY_COUNT, BACKOFF_MULTIPLIER);
    }
}
