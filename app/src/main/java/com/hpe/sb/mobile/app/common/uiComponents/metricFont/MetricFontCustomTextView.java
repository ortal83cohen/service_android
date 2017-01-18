package com.hpe.sb.mobile.app.common.uiComponents.metricFont;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.common.utils.HtmlUtil;


/**
 * Created by chovel on 05/04/2016.
 *
 */
public class MetricFontCustomTextView extends TextView {

    public String metricFontStyle = "Regular";
    public boolean cleanHtmlTags = true;
    public static final String PATH_PREFIX = "fonts/Metric-";
    public static final String FONT_FILE_SUFFIX = ".otf";

    public static String[] fontStyles = {"Black", "BlackItalic", "Bold", "Bold0", "BoldItalic", "Light", "LightItalic", "Medium", "MediumItalic",
            "Regular", "Regular_0", "RegularItalic", "Semibold", "SemiboldItalic", "Thin", "ThinItalic_0"};


    public MetricFontCustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.MetricFontCustomTextView,
                0, 0);

        try {
            int fontStyleIndex = a.getInt(R.styleable.MetricFontCustomTextView_metricFontStyle, 9);
            metricFontStyle = fontStyles[fontStyleIndex];
            cleanHtmlTags = a.getBoolean(R.styleable.MetricFontCustomTextView_cleanHtmlTags, true);
        } finally {
            a.recycle();
        }
        this.setTypeface(Typeface.createFromAsset(context.getAssets(), PATH_PREFIX + metricFontStyle + FONT_FILE_SUFFIX));

        if(isInEditMode()) {
            this.setText("Example text for preview");
        }
    }

    @Override
    public void setText(CharSequence text, BufferType type) {
        super.setText(cleanHtmlTags && text != null ? HtmlUtil.fromHtml(text.toString(), null).toString() : text, type);
    }

    public void setMetricFontStyle(String metricFontStyle) {
        this.metricFontStyle = metricFontStyle;
        this.setTypeface(Typeface.createFromAsset(getContext().getAssets(), PATH_PREFIX + metricFontStyle + FONT_FILE_SUFFIX));
    }
}
