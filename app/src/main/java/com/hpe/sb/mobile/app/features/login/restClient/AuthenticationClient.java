package com.hpe.sb.mobile.app.features.login.restClient;

import com.android.volley.Request;
import com.android.volley.Response;
import com.hpe.sb.mobile.app.common.dataClients.userContext.ApplicationType;
import com.hpe.sb.mobile.app.serverModel.user.Credentials;
import com.hpe.sb.mobile.app.serverModel.user.LogoutData;

import android.content.Context;

import java.util.Map;

import rx.Observable;

public interface AuthenticationClient {

    Request<Map<String, String>> authenticateUserWithPassword(String url, ApplicationType applicationType, Credentials credentials, Context context,
            Response.Listener<Map<String, String>> successListener, Response.ErrorListener errorListener);

    Observable<String> createAccessToken(Context context);

    Observable<Void> logout(Context context, LogoutData logoutData);
}
