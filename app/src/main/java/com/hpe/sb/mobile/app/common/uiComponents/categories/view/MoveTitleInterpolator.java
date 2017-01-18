package com.hpe.sb.mobile.app.common.uiComponents.categories.view;

import android.util.Log;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

/**
 * Created by mufler on 25/04/2016.
 */
public class MoveTitleInterpolator implements Interpolator {
    private Interpolator moveTitleFunc;
    private float minValuePx;
    private float delta;

    public MoveTitleInterpolator(float minValuePx, float maxValuePx) {
        this.moveTitleFunc = new LinearInterpolator();//PathInterpolatorCompat.create(0, 1, 0, 1);
        this.minValuePx = minValuePx;
        this.delta = maxValuePx - minValuePx;
    }

    @Override
    public float getInterpolation(float slideOffset) {
        final float scale = moveTitleFunc.getInterpolation(slideOffset);
        float interpolation = minValuePx + delta * scale;
        Log.d(getClass().getName(), "slideOffset=" + slideOffset + "; scale=" + scale+ "; interpolation=" + interpolation);
        return interpolation;
    }

}
