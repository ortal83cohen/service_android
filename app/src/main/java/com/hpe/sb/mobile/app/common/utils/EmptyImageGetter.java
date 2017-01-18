package com.hpe.sb.mobile.app.common.utils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;

/**
 * Created by cohenort on 04/07/2016.
 */
public class EmptyImageGetter implements Html.ImageGetter {
    private static final Drawable TRANSPARENT_DRAWABLE = new ColorDrawable(Color.TRANSPARENT);

    @Override
    public Drawable getDrawable(String source) {
        return TRANSPARENT_DRAWABLE;
    }
}