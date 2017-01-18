package com.hpe.sb.mobile.app.common.services.dateTime;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Creator: Sergey Steblin
 * Date:    18/07/2016
 */
@IntDef({PartOfDay.MORNING, PartOfDay.AFTERNOON, PartOfDay.EVENING, PartOfDay.NIGHT})
@Retention(RetentionPolicy.SOURCE)
public @interface PartOfDay {
    int MORNING = 1;
    int AFTERNOON = 2;
    int EVENING = 3;
    int NIGHT = 4;
}
