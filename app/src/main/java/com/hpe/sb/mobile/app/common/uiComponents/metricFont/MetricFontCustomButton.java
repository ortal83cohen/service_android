package com.hpe.sb.mobile.app.common.uiComponents.metricFont;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import com.hpe.sb.mobile.app.R;


/**
 * Created by chovel on 05/04/2016.
 *
 */
public class MetricFontCustomButton extends Button {

    public String metricFontStyle = "Regular";

    public MetricFontCustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.MetricFontCustomButton,
                0, 0);

        try {
            int fontStyleIndex = a.getInt(R.styleable.MetricFontCustomButton_metricFontStyle, 9);
            metricFontStyle = MetricFontCustomTextView.fontStyles[fontStyleIndex];
        } finally {
            a.recycle();
        }

        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Metric-" + metricFontStyle + ".otf"));

        if(isInEditMode()) {
            this.setText("Press Button");
        }
    }

}
