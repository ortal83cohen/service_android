package com.hpe.sb.mobile.app.features.login.restClient;

import android.content.Context;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.hpe.sb.mobile.app.common.dataClients.userContext.ApplicationType;
import com.hpe.sb.mobile.app.infra.restclient.HttpHeadersUtil;
import com.hpe.sb.mobile.app.infra.restclient.RetryPolicyUtil;
import com.hpe.sb.mobile.app.infra.restclient.customrequest.MapRequest;
import com.hpe.sb.mobile.app.common.utils.JsonTranslation;

import java.util.HashMap;
import java.util.Map;

public class UnauthenticatedRequestFactory {

    private RetryPolicyUtil retryPolicyUtil;

    public UnauthenticatedRequestFactory(RetryPolicyUtil retryPolicyUtil) {
        this.retryPolicyUtil = retryPolicyUtil;
    }

    public <K,V,T> MapRequest<K,V> createMapRequest(String url, final ApplicationType applicationType,
                                                  final T body, Context context,
                                                  Response.Listener<Map<K,V>> successListener,
                                                  Response.ErrorListener errorListener) {
        MapRequest<K,V> mapRequest = new MapRequest<K,V>(Request.Method.POST, url, successListener, errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> httpHeaders = new HashMap<>();
                httpHeaders.put(HttpHeadersUtil.APPLICATION_KEY, applicationType.name());
                return httpHeaders;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                String jsonStringBody = JsonTranslation.object2JsonString(body);
                return jsonStringBody.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json; charset=utf-8";
            }
        };
        mapRequest.setRetryPolicy(retryPolicyUtil.getRetryPolicy());
        mapRequest.setTag(context);

        return mapRequest;
    }
}
