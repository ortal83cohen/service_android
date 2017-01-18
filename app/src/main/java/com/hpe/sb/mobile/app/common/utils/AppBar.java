package com.hpe.sb.mobile.app.common.utils;

import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.common.uiComponents.metricFont.MetricFontCustomTextView;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.view.View;


public class AppBar extends Toolbar {


    public AppBar(Context context) {
        this(context, null);
    }

    public AppBar(Context context, AttributeSet attrs) {
        this(context, attrs, android.support.v7.appcompat.R.attr.toolbarStyle);
    }

    public AppBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }


    @Override
    public void setTitle(int resId) {
        MetricFontCustomTextView titleView =(MetricFontCustomTextView) findViewById(R.id.title);
        titleView.setText(resId);
    }
}
