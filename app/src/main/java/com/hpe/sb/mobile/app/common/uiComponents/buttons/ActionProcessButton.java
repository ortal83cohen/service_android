package com.hpe.sb.mobile.app.common.uiComponents.buttons;

import com.hpe.sb.mobile.app.R;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.util.AttributeSet;

public class ActionProcessButton extends ProcessButton {

    private ProgressBar mProgressBar;

    private Mode mMode;

    private int mColor1;
    private int mColor2;
    private int mColor3;
    private int mColor4;

    public enum Mode {
        PROGRESS, ENDLESS
    }

    public ActionProcessButton(Context context) {
        super(context);
        init(context);
    }

    public ActionProcessButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ActionProcessButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        Resources res = context.getResources();
        mMode = Mode.ENDLESS;
        mColor1 = res.getColor(R.color.disable_color1);
        mColor2 = res.getColor(R.color.disable_color2);
        mColor3 = res.getColor(R.color.disable_color1);
        mColor4 = res.getColor(R.color.disable_color2);
    }

    public void setMode(Mode mode) {
        mMode = mode;
    }


    @Override
    public void drawProgress(Canvas canvas) {
        if (getBackground() != getNormalDrawable()) {
            setBackgroundDrawable(getNormalDrawable());
        }

        switch (mMode) {
            case ENDLESS:
                drawEndlessProgress(canvas);
                break;
            case PROGRESS:
                drawLineProgress(canvas);
                break;
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mProgressBar != null) {
            setupProgressBarBounds();
        }
    }

    private void drawLineProgress(Canvas canvas) {
        float scale = (float) getProgress() / (float) getMaxProgress();
        float indicatorWidth = (float) getMeasuredWidth() * scale;

        double indicatorHeightPercent = 0.05; // 5%
        int bottom = (int) (getMeasuredHeight() - getMeasuredHeight() * indicatorHeightPercent);
        getProgressDrawable().setBounds(0, bottom, (int) indicatorWidth, getMeasuredHeight());
        getProgressDrawable().draw(canvas);
    }

    private void drawEndlessProgress(Canvas canvas) {
        if (mProgressBar == null) {
            mProgressBar = new ProgressBar(this);
            setupProgressBarBounds();
            mProgressBar.setColorScheme(mColor1, mColor2, mColor3, mColor4);
            mProgressBar.start();
        }

        if (getProgress() > 0) {
            mProgressBar.draw(canvas);
        }
    }

    private void setupProgressBarBounds() {
        double indicatorHeight = getDimension(R.dimen.minimum_margin);
        int bottom = (int) (getMeasuredHeight() - indicatorHeight);
        mProgressBar.setBounds(0, bottom, getMeasuredWidth(), getMeasuredHeight());
    }

}
