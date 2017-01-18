package com.hpe.sb.mobile.app.common.uiComponents.metricFont;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.common.utils.DimensionUtils;


/**
 * Created by malik david on 05/04/2016.
 *
 */
public class MetricFontCustomRadioButton extends RadioButton {

    public String metricFontStyle = "Regular";

    public MetricFontCustomRadioButton(Context context) {
        this(context, null);
    }

    public MetricFontCustomRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = context.getTheme().obtainStyledAttributes(
                null,
                R.styleable.MetricFontCustomRadioButton,
                0, 0);

        try {
            int fontStyleIndex = a.getInt(R.styleable.MetricFontCustomButton_metricFontStyle, 9);
            metricFontStyle = MetricFontCustomTextView.fontStyles[fontStyleIndex];
        } finally {
            a.recycle();
        }

        this.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Metric-" + metricFontStyle + ".otf"));
        this.setTextColor(getResources().getColorStateList(R.color.radio_button_text_color));
        this.setPadding((int) DimensionUtils.convertDpToPx(context, 12),0,0,0);
        this.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);

        RadioGroup.LayoutParams lp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
        lp.setMargins(0, 0, 0, (int) DimensionUtils.convertDpToPx(context, 22f));
        this.setLayoutParams(lp);

        if(isInEditMode()) {
            this.setText("Example radio button");
        }
    }
}
