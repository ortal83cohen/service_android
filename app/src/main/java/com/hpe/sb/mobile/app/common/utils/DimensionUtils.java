package com.hpe.sb.mobile.app.common.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

/**
 * Created by cohenort on 08/05/2016.
 */
public class DimensionUtils {

    public static float convertDpToPx(Context context, float dp) {
        Resources res = context.getResources();

        return dp * (res.getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        Resources res = context.getResources();
        DisplayMetrics metrics = res.getDisplayMetrics();

        float dp = px / ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
        return dp;
    }

}
