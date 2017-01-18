package com.hpe.sb.mobile.app.features.login.services;

import rx.Observable;

/**
 * Service for managing cookies. Used usually for syncing OAuth cookies to webview and clearing
 * previous cookies
 */
public interface SessionCookieService {

    /**
     * On subscription, clears all app web views Cookies
     */
    Observable<Void> clearWebViewCookies();

    /**
     * On subscription, initializes a web view session to have the recent token on its cookies,
     * with no cookie leftovers from previous webviews.
     */
    Observable<Void> initWebViewSession();

}
