package com.hpe.sb.mobile.app.common.uiComponents.todocards;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.common.utils.RuntimeViewIdGenerator;

/**
 * Created by chovel on 10/04/2016.
 *
 */
public class DotsIndexIndicatorView extends LinearLayout {

    private static final String START_ARROW = "<";
    private static final String END_ARROW = ">";
    private static final String DOT = "•";
    private static final float DEFAULT_DOT_SIZE = 40;
    private static final int DEFAULT_MAX_DOTS = 7;
    private static final int DEFAULT_UNSELECTED_COLOR_RES_ID = R.color.dotsUnselectedDefault;
    private static final int DEFAULT_SELECTED_COLOR_RES_ID = R.color.dotsSelectedDefault;

    private int maxDots = DEFAULT_MAX_DOTS;
    private float dotSize = DEFAULT_DOT_SIZE;
    private int cardAmount;
    private int selectedColor;
    private int unselectedColor;
    private int lastSelected;
    private boolean partInvisible;
    private TextView startArrow;
    private TextView endArrow;
    private TextView[] dots;

    public DotsIndexIndicatorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.DotsIndexIndicatorView,
                0, 0);

        try {
            dotSize = a.getFloat(R.styleable.DotsIndexIndicatorView_textSize, DEFAULT_DOT_SIZE);
            maxDots = a.getInt(R.styleable.DotsIndexIndicatorView_maxDots, DEFAULT_MAX_DOTS);
            unselectedColor = a.getColor(R.styleable.DotsIndexIndicatorView_unselectedColor, context.getResources().getColor(DEFAULT_UNSELECTED_COLOR_RES_ID));
            selectedColor = a.getColor(R.styleable.DotsIndexIndicatorView_selectedColor, context.getResources().getColor(DEFAULT_SELECTED_COLOR_RES_ID));
            partInvisible = false;
            initTextViews(context);
        } finally {
            a.recycle();
        }

    }

    public DotsIndexIndicatorView(Context context, int cardAmount, int maxDots, int unselectedColorResId, int selectedColorResId) {
        super(context);
        this.cardAmount = cardAmount;
        this.maxDots = maxDots;
        unselectedColor = context.getResources().getColor(unselectedColorResId);
        selectedColor = context.getResources().getColor(selectedColorResId);
        partInvisible = false;
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(layoutParams);
        initTextViews(context);
    }

    public DotsIndexIndicatorView(Context context, int cardAmount, int maxDots) {
        this(context, cardAmount, maxDots, DEFAULT_UNSELECTED_COLOR_RES_ID, DEFAULT_SELECTED_COLOR_RES_ID);
    }

    private void initTextViews(Context context) {
        endArrow = new TextView(context, null);
        endArrow.setId(RuntimeViewIdGenerator.generateViewId());
        startArrow = new TextView(context, null);
        startArrow.setId(RuntimeViewIdGenerator.generateViewId());
        initArrow(startArrow, START_ARROW);
        initArrow(endArrow, END_ARROW);
        initDotsAndAttach(context);
    }

    public void onCardSelected(int index) {
        dots[lastSelected % maxDots].setTextColor(unselectedColor);

        int lastPart = cardAmount == maxDots ? 0 : cardAmount / maxDots;
        int firstPart = 0;
        int currentPart = index / maxDots;

        if (lastPart != firstPart) {
            if (currentPart == lastPart) {
                selectedInLastPage();
            } else {
                selectedBeforeLastPage(currentPart == firstPart);
            }
        }
        else {
            startArrow.setVisibility(View.INVISIBLE);
            endArrow.setVisibility(View.INVISIBLE);
        }

        dots[index % maxDots].setTextColor(selectedColor);
        dots[index % maxDots].setVisibility(View.VISIBLE);
        lastSelected = index;
    }

    public void setCardAmount(int cardAmount) {
        this.cardAmount = cardAmount;
        removeAllViews();
        initDotsAndAttach(this.getContext());
        lastSelected = 0;
    }

    private void selectedInLastPage() {
        for (int i = cardAmount % maxDots; i < maxDots; i++) {
            dots[i].setVisibility(View.INVISIBLE);
        }
        partInvisible = true;
        startArrow.setVisibility(View.VISIBLE);
        endArrow.setVisibility(View.INVISIBLE);
    }

    private void selectedBeforeLastPage(boolean isFirstPage) {
        if (isFirstPage) {
            startArrow.setVisibility(View.INVISIBLE);
        }
        else {
            startArrow.setVisibility(View.VISIBLE);
        }
        endArrow.setVisibility(View.VISIBLE);
        if (partInvisible) {
            for (int i = 0; i < maxDots; i++) {
                dots[i].setVisibility(View.VISIBLE);
                partInvisible = false;
            }
        }
    }

    private void initArrow(TextView arrow, String arrowSign) {
        arrow.setText(arrowSign);
        arrow.setVisibility(View.INVISIBLE);
        arrow.setTextColor(unselectedColor);
        arrow.setTextSize(TypedValue.COMPLEX_UNIT_SP, dotSize / 2);
    }

    private void initDotsAndAttach(Context context) {
        dots = new TextView[Math.min(cardAmount, maxDots)];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER_VERTICAL;
        addView(startArrow, layoutParams);
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(context, null);
            dots[i].setText(DOT);
            dots[i].setTextColor(unselectedColor);
            dots[i].setTextSize(TypedValue.COMPLEX_UNIT_SP, dotSize);
            addView(dots[i], layoutParams);
        }
        addView(endArrow, layoutParams);
    }

    public TextView getStartArrow() {
        return startArrow;
    }

    public TextView getEndArrow() {
        return endArrow;
    }

    public TextView[] getDots() {
        return dots;
    }
}
