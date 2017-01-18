package com.hpe.sb.mobile.app.common.utils;

import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;


public class SearchAppBar extends AppBar {

    public SearchAppBar(Context context) {
        this(context, null);
    }

    public SearchAppBar(Context context, AttributeSet attrs) {
        this(context, attrs, android.support.v7.appcompat.R.attr.toolbarStyle);
    }

    public SearchAppBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

}
