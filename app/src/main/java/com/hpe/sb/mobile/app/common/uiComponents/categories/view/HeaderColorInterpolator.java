package com.hpe.sb.mobile.app.common.uiComponents.categories.view;

import android.graphics.Color;
import android.support.v4.view.animation.FastOutLinearInInterpolator;
import android.util.Log;
import android.view.animation.Interpolator;

/**
 * Created by mufler on 25/04/2016.
 */
public class HeaderColorInterpolator  {
    private Interpolator innerInterpolator;
    private int minValue;
    private int maxValue;
    private float delta;
    final float[] from;
    final float[] to;
    private float minOpacity = .0f;

    public HeaderColorInterpolator(int minValue, int maxValue) {
        this.innerInterpolator = new FastOutLinearInInterpolator();//PathInterpolatorCompat.create(0, 1, 0, 1);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.delta = maxValue - minValue;
        from = new float[3];
        to =   new float[3];

        Color.colorToHSV(minValue, from);   // from white
        Color.colorToHSV(maxValue, to);     // to grey
    }

    /**
     * if movingDown is true - the meaning that now the panel goes down,
     * otherwise - up
     * @param slideOffset
     * @param movingDown
     * @return
     */
    public int getTargetColor(float slideOffset, boolean movingDown){
        Log.e(getClass().getName(), "slideOffset="+ slideOffset + "; movingDown=" + movingDown );
        if(slideOffset == 0){
            return minValue;
        }
        if(!movingDown){
            return maxValue;
        }
        final double threshold = 0.01;//100% opacity
        if(slideOffset > threshold){
            return maxValue;
        }
        float interpolation = (float) (slideOffset / threshold);
        float alphaScale = (interpolation < minOpacity) ? minOpacity : interpolation;
        final int alpha = Math.round(255 * alphaScale);
        Log.e(getClass().getName(), "slideOffset="+ slideOffset + "; interpolation=" + interpolation + ";alphaScale=" + alphaScale);
        int backcolor = android.graphics.Color.argb(alpha, 51, 51, 51);
        String hexColor = String.format("#%06X", (0xFFFFFFFF & backcolor));
        Log.e(getClass().getName(), "slideOffset="+ slideOffset + "; backcolor = " + hexColor + "; alpha=" + alpha);

/*        final float[] hsv  = new float[3];                  // transition color
        hsv[0] = from[0] + (to[0] - from[0]) * fraction;
        hsv[1] = from[1] + (to[1] - from[1]) * fraction;
        hsv[2] = from[2] + (to[2] - from[2]) * fraction;

        return Color.HSVToColor(hsv);*/
        return backcolor;
    }

}
