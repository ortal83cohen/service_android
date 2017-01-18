package com.hpe.sb.mobile.app.features.login.services;

import android.content.Context;
import android.util.Log;

import com.hpe.sb.mobile.app.common.dataClients.userContext.ApplicationType;
import com.hpe.sb.mobile.app.features.googlepushnotification.GooglePushNotificationClient;
import com.hpe.sb.mobile.app.features.login.model.SawHeadersDefinition;
import com.hpe.sb.mobile.app.features.login.restClient.AuthenticationClient;

import java.util.Map;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class PostLoginServiceImpl implements PostLoginService {

    private ConnectionContextService connectionContextService;
    private AuthenticationClient authenticationClient;
    private GooglePushNotificationClient googlePushNotificationClient;

    private final String TAG = this.getClass().getSimpleName();

    public PostLoginServiceImpl(ConnectionContextService connectionContextService,
                                AuthenticationClient authenticationClient,
                                GooglePushNotificationClient googlePushNotificationClient) {
        this.connectionContextService = connectionContextService;
        this.authenticationClient = authenticationClient;
        this.googlePushNotificationClient = googlePushNotificationClient;
    }

    @Override
    public Observable<Void> initializePostLogin(Context context) {
        Observable<Void> createAccessTokenIfNeeded = createAccessTokenIfNeeded(context);
        Observable<Void> initializeRegistrationsAfterLogin = initializeRegistrationsAfterLogin();

        return Observable.zip(createAccessTokenIfNeeded, initializeRegistrationsAfterLogin, new Func2<Void, Void, Void>() {
            @Override
            public Void call(Void aVoid, Void aVoid2) {
                return null;
            }
        }).subscribeOn(Schedulers.io());
    }

    private Observable<Void> createAccessTokenIfNeeded(Context context) {
        ApplicationType applicationType = connectionContextService.getConnectionContext().getApplicationType();
        if (applicationType == ApplicationType.SAW) {
            return authenticationClient.createAccessToken(context)
                    .doOnNext(new Action1<String>() {
                        @Override
                        public void call(String longToken) {
                            Log.d(TAG, "Token received successfully");
                            saveOAuthTokenInConnectionContext(longToken);
                            removeLWSSOTokenConnectionContext();
                            connectionContextService.storeConnectionContext();
                        }
                    }).map(new Func1<String, Void>() {
                        @Override
                        public Void call(String s) {
                            return null;
                        }
                    });
        } else {
            return Observable.just(null);
        }
    }

    private Observable<Void> initializeRegistrationsAfterLogin() {
        return googlePushNotificationClient.initializeRegistrationsAfterLogin()
                .doOnNext(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Log.i(TAG, "Successfully initialized GCM registrations after login");
                    }
                });
    }

    private void removeLWSSOTokenConnectionContext() {
        final Map<String, String> cookies = connectionContextService.getConnectionContext().getCookies();
        cookies.remove(SawHeadersDefinition.LWSSO_COOKIE_KEY);
        connectionContextService.updateCookies(cookies);
    }

    private void saveOAuthTokenInConnectionContext(String longToken) {
        final Map<String, String> headers = connectionContextService.getConnectionContext().getHeaders();
        headers.put(SawHeadersDefinition.OAUTH2_TOKEN_HEADER, SawHeadersDefinition.OAUTH2_TOKEN_PREFIX + longToken);
        connectionContextService.updateHeaders(headers);
    }
}
