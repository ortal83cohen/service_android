package com.hpe.sb.mobile.app.features.router;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Routes {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DEFAULT, REQUEST, COMMENT_ON_REQUEST, REQUEST_WITH_DENY_DIALOG, RELOGIN})
    public @interface RouteCode {}

    public static final int DEFAULT = 0;
    public static final int REQUEST = 1;
    public static final int COMMENT_ON_REQUEST = 2;
    public static final int REQUEST_WITH_DENY_DIALOG = 3;
    public static final int RELOGIN = 4;
}
