package com.hpe.sb.mobile.app.features.googlepushnotification.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class GooglePushNotificationAction {

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ACCEPT_REQUEST, REJECT_REQUEST, MARK_REQUEST_AS_SOLVED, COMMENT_ON_REQUEST,
            APPROVE_REQUEST, DENY_REQUEST})
    public @interface ActionName {}

    public final static String ACCEPT_REQUEST = "ACCEPT_REQUEST";
    public final static String REJECT_REQUEST = "REJECT_REQUEST";
    public final static String MARK_REQUEST_AS_SOLVED = "MARK_REQUEST_AS_SOLVED";
    public final static String COMMENT_ON_REQUEST = "COMMENT_ON_REQUEST";
    public final static String APPROVE_REQUEST = "APPROVE_REQUEST";
    public final static String DENY_REQUEST = "DENY_REQUEST";

}
