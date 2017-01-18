package com.hpe.sb.mobile.app.features.googlepushnotification.model;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class GooglePushNotificationType {

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({REQUEST_RESOLVED, PROVIDE_MORE_INFO, PENDING_APPROVAL})
    public @interface Type {}

    public final static String REQUEST_RESOLVED = "REQUEST_RESOLVED";
    public final static String PROVIDE_MORE_INFO = "PROVIDE_MORE_INFO";
    public final static String PENDING_APPROVAL = "PENDING_APPROVAL";

}
