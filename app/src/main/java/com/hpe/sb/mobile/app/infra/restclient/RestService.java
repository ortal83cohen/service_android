package com.hpe.sb.mobile.app.infra.restclient;

import android.content.Context;

import rx.Observable;

public interface RestService {

    <T> Observable<T> createGetRequest(String url, Class<T> resultClass, Context context);

    <T, R> Observable<T> createPostRequest(String url, R body, Class<T> resultClass, Context context);

    <T, R> Observable<T> createPutRequest(String url, R body, Class<T> resultClass, Context context);

}
