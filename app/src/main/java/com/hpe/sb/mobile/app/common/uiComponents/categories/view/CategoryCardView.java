package com.hpe.sb.mobile.app.common.uiComponents.categories.view;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by mufler on 16/06/2016.
 */
public class CategoryCardView extends android.support.v7.widget.CardView {
    public CategoryCardView(Context context) {
        super(context);
    }

    public CategoryCardView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CategoryCardView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        final int measuredWidth = getMeasuredWidth();
        super.onMeasure(MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(measuredWidth, MeasureSpec.EXACTLY));
    }
}
