package com.hpe.sb.mobile.app.features.home;

import android.support.annotation.IntDef;
import android.support.annotation.StringRes;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by cohenort on 08/08/2016.
 */
public interface DisplayMessage {

    @IntDef({LENGTH_INDEFINITE, LENGTH_LONG, LENGTH_SHORT})
    @Retention(RetentionPolicy.SOURCE)
    @interface Duration {

    }

    int LENGTH_INDEFINITE = -2;
    int LENGTH_SHORT = -1;
    int LENGTH_LONG = 0;

    void show(@StringRes int resIdg, @Duration int length);
}
