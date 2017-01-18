package com.hpe.sb.mobile.app.common.services.dateTime;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;

import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.common.utils.LogTagConstants;

import java.text.DateFormat;
import java.util.Date;

/**
 * Created by chovel on 17/04/2016.
 *
 */
public class DateTimeService {

    public static final String NEGATIVE_ELAPSED_TIME_ERROR_MESSAGE = "Elapsed time was calculated to a negative number! elapsed time: %s, origin time: %s. Returning empty string result.";

    Context context;

    public DateTimeService(Context context) {
        this.context = context;
    }

    @PartOfDay
    public int getPartOfDay(int hourOfDay) {
        if (hourOfDay >= 6 && hourOfDay < 12) {
            return PartOfDay.MORNING;
        } else if (hourOfDay >= 12 && hourOfDay < 18) {
            return PartOfDay.AFTERNOON;
        } else if (hourOfDay >= 18 && hourOfDay < 22) {
            return PartOfDay.EVENING;
        } else {
            return PartOfDay.NIGHT;
        }
    }

    /**
     * Gets time in milliseconds and returns a LOCALIZED (by device local) time in the format of the example:
     * Aug 22, 1988
     *
     * @param timeMillis time in millis
     * @return localized time in above format
     */
    public String getDate(long timeMillis) {
        DateFormat dateFormat = DateFormat.getDateInstance();
        return dateFormat.format(new Date(timeMillis));
    }

    /**
     * returns elapsed time by device local, in the format MM:SS or H:MM:SS
     * (result is similar to that used on the call-in-progress screen).
     * @param timeMillis timeMillis
     * @return elapsedTime
     */
    public String getElapsedTimeHoursMinutesSeconds(long timeMillis) {
        return DateUtils.formatElapsedTime((System.currentTimeMillis() - timeMillis)/1000);
    }

    /**
     * returns elapsed time by device local, in the format of "[num] [timeUnit] ago"
     * will return the time with only the highest unit that exists.
     * @param timeMillis timeMillis
     * @return elapsedTime in text form, empty string ("") in case of bad result (negative elapsed time value)
     */
    public String getElapsedTimeInTextForm(long timeMillis) {
        long elapsedTime = System.currentTimeMillis() - timeMillis;
        long seconds = elapsedTime / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;
        String elapsedTimeStr;
        if (days > 0) {
            elapsedTimeStr = String.format(days == 1 ? context.getString(R.string.date_time_format_single_day_ago) : context.getString(R.string.date_time_format_days_ago), days);
        }
        else if (hours > 0) {
            elapsedTimeStr = String.format(hours == 1 ? context.getString(R.string.date_time_format_single_hour_ago) : context.getString(R.string.date_time_format_hours_ago), hours);
        }
        else if (minutes > 0) {
            elapsedTimeStr = String.format(context.getString(R.string.date_time_format_min_ago), minutes);
        }
        else {
            elapsedTimeStr = elapsedTime > 0 ? String.format(context.getString(R.string.date_time_format_sec_ago), seconds) :
                    handleNegativeElapsedTime(timeMillis, elapsedTime);
        }
        return elapsedTimeStr;
    }

    private String handleNegativeElapsedTime(long timeMillis, long elapsedTime) {
        Log.w(LogTagConstants.UTILS, String.format(NEGATIVE_ELAPSED_TIME_ERROR_MESSAGE, elapsedTime, timeMillis));
        return "";
    }
}
