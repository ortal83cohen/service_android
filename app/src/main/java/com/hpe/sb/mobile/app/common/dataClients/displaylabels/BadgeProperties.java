package com.hpe.sb.mobile.app.common.dataClients.displaylabels;

import android.graphics.drawable.Drawable;

/**
 * Created by salemo on 14/06/2016.
 *
 */
public class BadgeProperties {
    String text;
    int backgroundColor;
    Drawable drawable;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }
}
