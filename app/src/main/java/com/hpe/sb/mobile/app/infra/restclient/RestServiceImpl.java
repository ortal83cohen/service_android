package com.hpe.sb.mobile.app.infra.restclient;

import android.content.Context;
import com.android.volley.*;
import com.android.volley.toolbox.RequestFuture;
import com.hpe.sb.mobile.app.infra.exception.*;
import com.hpe.sb.mobile.app.infra.restclient.customrequest.GsonRequest;
import com.hpe.sb.mobile.app.infra.restclient.customrequest.RequestFactory;
import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Wrapper for creating REST requests to mobile gateway.
 * Assumes authentication and json responses.
 */
public class RestServiceImpl implements RestService {

    private final RestClientQueue restClientQueue;
    private final HttpLookupUtils httpLookupUtils;
    private final RequestFactory requestFactory;

    public RestServiceImpl(RestClientQueue restClientQueue, HttpLookupUtils httpLookupUtils, RequestFactory requestFactory) {
        this.restClientQueue = restClientQueue;
        this.httpLookupUtils = httpLookupUtils;
        this.requestFactory = requestFactory;
    }

    @Override
    public <T> Observable<T> createGetRequest(final String url, final Class<T> resultClass, final Context context) {
        return Observable.defer(new Func0<Observable<T>>() {
            @Override
            public Observable<T> call() {
                final RequestFuture<T> requestFuture = RequestFuture.newFuture();
                GsonRequest<T, ?> request = requestFactory.createGsonRequest(httpLookupUtils.getBaseRestUrl() + url, resultClass, context, requestFuture, requestFuture);
                restClientQueue.addToRequestQueue(request);
                return Observable.from(requestFuture)
                        .compose(transformObservable(resultClass))
                        .subscribeOn(Schedulers.io());
            }
        });
    }

    @Override
    public <T, R> Observable<T> createPostRequest(final String url, final R body, final Class<T> resultClass, final Context context) {
        return Observable.defer(new Func0<Observable<T>>() {
            @Override
            public Observable<T> call() {
                final RequestFuture<T> requestFuture = RequestFuture.newFuture();
                GsonRequest<T, ?> request = requestFactory.createGsonRequest(httpLookupUtils.getBaseRestUrl() + url, body, resultClass, Request.Method.POST, context, requestFuture, requestFuture);
                restClientQueue.addToRequestQueue(request);
                return Observable.from(requestFuture)
                        .compose(transformObservable(resultClass))
                        .subscribeOn(Schedulers.io());
            }
        });
    }

    @Override
    public <T, R> Observable<T> createPutRequest(final String url, final R body, final Class<T> resultClass, final Context context) {
        return Observable.defer(new Func0<Observable<T>>() {
            @Override
            public Observable<T> call() {
                final RequestFuture<T> requestFuture = RequestFuture.newFuture();
                GsonRequest<T, ?> request = requestFactory.createGsonRequest(httpLookupUtils.getBaseRestUrl() + url, body, resultClass, Request.Method.PUT, context, requestFuture, requestFuture);
                restClientQueue.addToRequestQueue(request);
                return Observable.from(requestFuture)
                        .compose(transformObservable(resultClass))
                        .subscribeOn(Schedulers.io());
            }
        });
    }

    private static <T> Observable.Transformer<T, T> transformObservable(Class<T> clazz) {
        return new Observable.Transformer<T, T>() {
            @Override
            public Observable<T> call(Observable<T> tObservable) {
                return tObservable.onErrorResumeNext(new Func1<Throwable, Observable<? extends T>>() {
                    @Override
                    public Observable<? extends T> call(Throwable throwable) {
                        if (throwable.getCause() != null && throwable.getCause() instanceof VolleyError) {
                            VolleyError error = (VolleyError) throwable.getCause();
                            if (error instanceof TimeoutError) {
                                return Observable.error(new RequestTimeoutException(throwable));
                            } else if (error instanceof NoConnectionError || error instanceof NetworkError) {
                                return Observable.error(new NoConnectionException(throwable));
                            } else if (error instanceof AuthFailureError) {
                               return handleServerError(throwable, error);
                            }else if(error instanceof ServerError){
                                return handleServerError(throwable, error);
                            }
                            else {
                                return Observable.error(throwable);
                            }
                        }
                        else {
                            return Observable.error(throwable);
                        }
                    }
                });
            }
        };
    }

    private static Observable handleServerError(Throwable throwable, VolleyError error) {
        NetworkResponse response = error.networkResponse;
        if(response != null){
            switch (response.statusCode) {
                case 401: return Observable.error(new AuthenticationException(throwable));
                case 403: return Observable.error(new AuthorizationException(throwable));
                case 404: return Observable.error(new NotFoundException(throwable));
                default: return Observable.error(throwable);
            }
        }
        return Observable.error(throwable);
    }
}
