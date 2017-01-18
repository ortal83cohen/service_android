package com.hpe.sb.mobile.app.features.login.services;

import android.os.Build;
import android.os.Handler;
import android.webkit.CookieManager;
import android.webkit.ValueCallback;

import com.hpe.sb.mobile.app.features.login.model.SawHeadersDefinition;
import com.hpe.sb.mobile.app.features.login.utils.CookieParser;
import com.hpe.sb.mobile.app.infra.restclient.HttpLookupUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.schedulers.HandlerScheduler;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class SessionCookieServiceImpl implements SessionCookieService {

    private final ConnectionContextService connectionContextService;

    private final HttpLookupUtils httpLookupUtils;

    public SessionCookieServiceImpl(ConnectionContextService connectionContextService,
                                    HttpLookupUtils httpLookupUtils) {
        this.connectionContextService = connectionContextService;
        this.httpLookupUtils = httpLookupUtils;
    }

    @Override
    public Observable<Void> clearWebViewCookies() {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(final Subscriber<? super Void> subscriber) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    CookieManager.getInstance().removeAllCookies(new ValueCallback<Boolean>() {
                        @Override
                        public void onReceiveValue(Boolean value) {
                            subscriber.onNext(null);
                            subscriber.onCompleted();
                        }
                    });
                } else {
                    CookieManager.getInstance().removeAllCookie();
                    subscriber.onNext(null);
                    subscriber.onCompleted();
                }
            }
        }).subscribeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Void> initWebViewSession() {
        Observable<Void> deletePreviousCookies = clearWebViewCookies().cache();

        Observable<Void> oauthTokenUpdate = deletePreviousCookies.flatMap(new Func1<Void, Observable<Map<String, String>>>() {
            @Override
            public Observable<Map<String, String>> call(Void aVoid) {
                return Observable.fromCallable(new Callable<Map<String, String>>() {
                    @Override
                    public Map<String, String> call() throws Exception {
                        String oauthHeaderValue = connectionContextService.getConnectionContext()
                                .getHeaderValue(SawHeadersDefinition.OAUTH2_TOKEN_HEADER);
                        if (oauthHeaderValue == null) {
                            throw new RuntimeException("No OAuth token present");
                        }
                        String oauthCookieValue;
                        String[] parsed = oauthHeaderValue.split(SawHeadersDefinition.OAUTH2_TOKEN_PREFIX);
                        if (parsed.length == 2) {
                            oauthCookieValue = parsed[1];
                        } else {
                            throw new RuntimeException("OAuth token parsing exception. Token after split length was " + parsed.length);
                        }
                        Map<String, String> cookieMap = new HashMap<>();
                        cookieMap.put(SawHeadersDefinition.OAUTH2_COOKIE_KEY, oauthCookieValue);
                        return cookieMap;
                    }
                });
            }
        }).map(mapToCookieStringFunction())
        .flatMap(mapToUpdateCookieManager());

        Observable<Void> tenantIdUpdate = deletePreviousCookies.flatMap(new Func1<Void, Observable<Map<String, String>>>() {
            @Override
            public Observable<Map<String, String>> call(Void aVoid) {
                return Observable.fromCallable(new Callable<Map<String, String>>() {
                    @Override
                    public Map<String, String> call() throws Exception {
                        String tenantCookieValue = connectionContextService.getConnectionContext()
                                .getCookieValue(SawHeadersDefinition.TENANT_ID_COOKIE_KEY);

                        if (tenantCookieValue == null) {
                            throw new RuntimeException("No tenant cookie token present");
                        }

                        Map<String, String> cookieMap = new HashMap<>();
                        cookieMap.put(SawHeadersDefinition.TENANT_ID_COOKIE_KEY, tenantCookieValue);
                        return cookieMap;
                    }
                });
            }
        }).map(mapToCookieStringFunction())
                .flatMap(mapToUpdateCookieManager());

        return Observable.zip(tenantIdUpdate, oauthTokenUpdate, new Func2<Void, Void, Void>() {
            @Override
            public Void call(Void aVoid, Void aVoid2) {
                return null;
            }
        }).subscribeOn(HandlerScheduler.from(new Handler()));
    }

    private Func1<Map<String, String>, String> mapToCookieStringFunction() {
        return new Func1<Map<String, String>, String>() {
            @Override
            public String call(Map<String, String> cookies) {
                return new CookieParser().buildCookieStringFromCookieMap(cookies);
            }
        };
    }

    private Func1<String, Observable<Void>> mapToUpdateCookieManager() {
        return new Func1<String, Observable<Void>>() {
            @Override
            public Observable<Void> call(final String cookieString) {
                return Observable.create(new Observable.OnSubscribe<Void>() {
                    @Override
                    public void call(final Subscriber<? super Void> subscriber) {
                        String url = httpLookupUtils.getBaseUrl();
                        String secureCookieString = cookieString + "; HttpOnly";
                        if (connectionContextService.getConnectionContext().getProtocol().equalsIgnoreCase("https")) {
                            secureCookieString += "; Secure";
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            CookieManager.getInstance().setCookie(url, secureCookieString, new ValueCallback<Boolean>() {
                                @Override
                                public void onReceiveValue(Boolean value) {
                                    subscriber.onNext(null);
                                    subscriber.onCompleted();
                                }
                            });
                        } else {
                            CookieManager.getInstance().setCookie(url, secureCookieString);
                            subscriber.onNext(null);
                            subscriber.onCompleted();
                        }
                    }
                });
            }
        };
    }


}


