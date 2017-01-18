package com.hpe.sb.mobile.app.common.uiComponents.metricFont;

import com.hpe.sb.mobile.app.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;
import android.widget.Toast;


/**
 * Created by gabaysh on 27/04/2016.
 */
public class MetricFontCustomEditView extends EditText {

    public String metricFontStyle = "Regular";

    public static final String PATH_PREFIX = "fonts/Metric-";

    public static final String FONT_FILE_SUFFIX = ".otf";

    public MetricFontCustomEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFilters(new InputFilter[]{new EmojiExcludeFilter()});
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.MetricFontCustomEditView,
                0, 0);

        try {
            int fontStyleIndex = a.getInt(R.styleable.MetricFontCustomEditView_metricFontStyle, 9);
            metricFontStyle = MetricFontCustomTextView.fontStyles[fontStyleIndex];
        } finally {
            a.recycle();
        }
        this.setTypeface(Typeface.createFromAsset(context.getAssets(),
                PATH_PREFIX + metricFontStyle + FONT_FILE_SUFFIX));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (getLineCount() > getMaxLines()) {
            getParent().requestDisallowInterceptTouchEvent(true);
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
        }
        return super.onTouchEvent(event);
    }

    private class EmojiExcludeFilter implements InputFilter {

        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest,
                int dstart, int dend) {
            for (int i = start; i < end; i++) {
                int type = Character.getType(source.charAt(i));
                if (type == Character.SURROGATE) {
                    Toast.makeText(getContext(), "Invalid Character", Toast.LENGTH_SHORT).show();
                    return "";
                }
            }
            return null;
        }
    }
}