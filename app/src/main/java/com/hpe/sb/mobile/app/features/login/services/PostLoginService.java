package com.hpe.sb.mobile.app.features.login.services;

import android.content.Context;

import rx.Observable;

public interface PostLoginService {

    /**
     * When subscribed, does initializations that are required after logging in for the first time.
     */
    Observable<Void> initializePostLogin(Context context);

}
