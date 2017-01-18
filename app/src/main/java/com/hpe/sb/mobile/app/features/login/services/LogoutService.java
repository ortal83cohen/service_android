package com.hpe.sb.mobile.app.features.login.services;

import android.app.Activity;


/**
 * Service for managing cookies. Used usually for syncing OAuth cookies to webview and clearing
 * previous cookies
 */
public interface LogoutService {

    void logout(final Activity currentActivity);

    void localLogout(final Activity currentActivity);

}
