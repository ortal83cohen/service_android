package com.hpe.sb.mobile.app.common.utils;

import android.widget.TextView;

/**
 * Created by malikdav on 28/04/2016.
 */
public class ViewUtil {
    public static boolean isEmptyText(TextView textView) {
        return textView.getText().toString().trim().length() == 0;
    }
}
