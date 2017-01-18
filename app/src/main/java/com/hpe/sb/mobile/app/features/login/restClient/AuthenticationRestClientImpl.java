package com.hpe.sb.mobile.app.features.login.restClient;

import com.android.volley.Request;
import com.android.volley.Response;
import com.hpe.sb.mobile.app.common.dataClients.userContext.ApplicationType;
import com.hpe.sb.mobile.app.infra.restclient.BaseRestClient;
import com.hpe.sb.mobile.app.infra.restclient.HttpLookupUtils;
import com.hpe.sb.mobile.app.infra.restclient.RestClientQueue;
import com.hpe.sb.mobile.app.infra.restclient.RestService;
import com.hpe.sb.mobile.app.infra.restclient.customrequest.MapRequest;
import com.hpe.sb.mobile.app.serverModel.user.Credentials;
import com.hpe.sb.mobile.app.serverModel.user.LogoutData;

import android.content.Context;

import java.util.Map;

import rx.Observable;

public class AuthenticationRestClientImpl extends BaseRestClient implements AuthenticationClient {

    private UnauthenticatedRequestFactory unauthenticatedRequestFactory;

    private RestService restService;

    public AuthenticationRestClientImpl(RestService restService, RestClientQueue restClientQueue, UnauthenticatedRequestFactory unauthenticatedRequestFactory,
            HttpLookupUtils httpLookupUtils) {
        super(restClientQueue, httpLookupUtils);
        this.unauthenticatedRequestFactory = unauthenticatedRequestFactory;
        this.restService = restService;
    }

    @Override
    public String getClientPrefix() {
        return "authentication";
    }

    @Override
    public Request<Map<String, String>> authenticateUserWithPassword(String url, ApplicationType applicationType,
            Credentials credentials, Context context,
            Response.Listener<Map<String, String>> successListener,
            Response.ErrorListener errorListener) {
        MapRequest<String, String> request = unauthenticatedRequestFactory.createMapRequest(url,
                applicationType, credentials, context, successListener, errorListener);
        return restClientQueue.addToRequestQueue(request);
    }

    @Override
    public Observable<String> createAccessToken(Context context) {
        return restService.createPostRequest(getClientPrefix() + "/createAccessToken", null, String.class,
                context);
    }

    @Override
    public Observable<Void> logout(Context context, LogoutData LogoutData) {
        return restService.createPostRequest(getClientPrefix() + "/logout", LogoutData, Void.class,
                context);
    }

}
