package com.hpe.sb.mobile.app.common.utils.time;

import java.util.concurrent.TimeUnit;

public class TimeAgeUtils {

    public static boolean isTimeOlderThanXHours(long time, long xHours) {
        long now = System.currentTimeMillis();
        return now - time > TimeUnit.MILLISECONDS.convert(xHours, TimeUnit.HOURS);
    }

}
