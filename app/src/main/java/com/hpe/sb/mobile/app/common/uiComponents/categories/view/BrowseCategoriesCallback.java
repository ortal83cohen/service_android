package com.hpe.sb.mobile.app.common.uiComponents.categories.view;

import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.common.uiComponents.metricFont.MetricFontCustomTextView;
import com.hpe.sb.mobile.app.features.home.MainActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.res.Resources;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.animation.LinearOutSlowInInterpolator;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by mufler on 24/04/2016.
 */
public class BrowseCategoriesCallback extends BottomSheetBehavior.BottomSheetCallback
        implements View.OnClickListener {

    private static final String TAG = BrowseCategoriesCallback.class.getSimpleName();

    private final FrameLayout statusBarContainer;

    private MainActivity parentActivity;

    private final FloatingActionButton fab;

    private boolean mExpanded;

    private ViewGroup bottomSheet;

    private BottomSheetBehavior behavior;

    private ViewGroup categoriesHeader;

    private ImageView backCategoriesButton;

    private View shadow;

    private MetricFontCustomTextView categoriesTitle;

    private int collapsedCategoriesHeaderColor;

    private int expandedCategoriesHeaderColor;

    private int expandedCategoriesTitleColor;

    private int collapsedCategoriesTitleColor;

    private Interpolator fabInterpolator;

    private Interpolator moveTitleInterpolator;

    private HeaderColorInterpolator headerColorInterpolator;

    private boolean backIsShown;

    public BrowseCategoriesCallback(MainActivity parentActivity, ViewGroup bottomSheet,
            BottomSheetBehavior behavior, FloatingActionButton fab) {
        this.parentActivity = parentActivity;
        this.fab = fab;
        this.fabInterpolator = new LinearOutSlowInInterpolator();
        this.behavior = behavior;
        behavior.setBottomSheetCallback(this);
        this.bottomSheet = bottomSheet;
        this.bottomSheet.setOnClickListener(this);

        this.categoriesHeader = (ViewGroup) parentActivity.findViewById(R.id.categories_header);
        this.backCategoriesButton = (ImageView) parentActivity
                .findViewById(R.id.back_categories_button);
        this.backCategoriesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.back_categories_button) {
                    if (mExpanded) {
                        BrowseCategoriesCallback.this.behavior
                                .setState(BottomSheetBehavior.STATE_COLLAPSED);
                    } else {
                        BrowseCategoriesCallback.this.behavior
                                .setState(BottomSheetBehavior.STATE_EXPANDED);
                    }
                }
            }
        });

        this.categoriesTitle = (MetricFontCustomTextView) parentActivity
                .findViewById(R.id.browse_categories_pane_title);
        final Resources resources = parentActivity.getResources();
        collapsedCategoriesHeaderColor = resources.getColor(
                R.color.browse_categories_collapsed_header_background_color);//android:color/white
        expandedCategoriesHeaderColor = resources
                .getColor(R.color.browse_categories_expanded_header_background_color);
        expandedCategoriesTitleColor = resources
                .getColor(R.color.browse_categories_expanded_title_text_color);
        collapsedCategoriesTitleColor = resources
                .getColor(R.color.browse_categories_collapsed_title_text_color);

        this.moveTitleInterpolator = new MoveTitleInterpolator(dpToPx(16),
                dpToPx(44));//todo:mufler remove hardcoded - > take from style
        this.headerColorInterpolator = new HeaderColorInterpolator(collapsedCategoriesHeaderColor,
                expandedCategoriesHeaderColor);
        this.shadow = parentActivity.findViewById(R.id.fakeShadow);
        statusBarContainer = (FrameLayout) parentActivity
                .findViewById(R.id.status_bar_container);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.categories_slide_pane) {
            if (mExpanded) {
                behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else {
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }
    }

    public boolean handleHardwareBackButton() {
        if (mExpanded) {
            this.behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            return true;
        }
        return false;
    }

    @Override
    public void onStateChanged(@NonNull View bottomSheet, int newState) {

        switch (newState) {

            case BottomSheetBehavior.STATE_SETTLING:
            case BottomSheetBehavior.STATE_DRAGGING:
                break;
            case BottomSheetBehavior.STATE_COLLAPSED:
                parentActivity.setRefreshable();
                mExpanded = false;
                break;
            case BottomSheetBehavior.STATE_EXPANDED:
                parentActivity.setRefreshable(false);
                mExpanded = true;
                break;
        }
        statusBarContainer.setBackgroundColor(
                parentActivity.getResources().getColor(R.color.colorPrimaryDark));
    }


    @Override
    public void onSlide(View bottomSheet, float slideOffset) {
        handleFab(slideOffset);
        handleBackButton(slideOffset);
        handleHeader(slideOffset);
        handleTitle(slideOffset);
        handleStatusBar(slideOffset);
    }

    private void handleStatusBar(float slideOffset) {// TODO: 10/05/2016  
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            statusBarContainer.setAlpha(slideOffset);
        }
    }

    private void handleHeader(float slideOffset) {
        if (slideOffset == 1) {
            categoriesHeader.setBackgroundColor(expandedCategoriesHeaderColor);
        } else if (slideOffset != 0) {
            categoriesHeader.setBackgroundColor(expandedCategoriesHeaderColor);

        }
        if (slideOffset == 0) {
            categoriesHeader.setBackgroundColor(collapsedCategoriesHeaderColor);
        }
    }

    private void handleBackButton(float slideOffset) {
        Log.d(TAG, "slideOffset=" + slideOffset + "; backIsShown=" + backIsShown);
        if (slideOffset == 1 && !backIsShown) {
            backCategoriesButton.animate().setDuration(50)
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            backCategoriesButton.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            backIsShown = true;
                            Log.d(TAG, "backIsShown=>rue");
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {
                            backIsShown = true;
                            Log.d(TAG, "on cancel backIsShown=>true");
                        }
                    });
        } else if (backIsShown) {
            backCategoriesButton.animate().setDuration(100)
                    .scaleX(0f)
                    .scaleY(0f)
                    .alpha(0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationCancel(Animator animation) {
                            backIsShown = false;
                            Log.d(TAG, "on cancel backIsShown=>false");
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            backCategoriesButton.setVisibility(View.GONE);
                            backIsShown = false;
                            Log.d(TAG, "backIsShown=>false");
                        }
                    });
        }
    }

    private void handleTitle(final float slideOffset) {
        float targetTitleMargin = this.moveTitleInterpolator.getInterpolation(slideOffset);
        Log.d(TAG, "slideOffset=" + slideOffset + "; targetTitleMargin=" + targetTitleMargin
                + "before change=" + categoriesTitle.getLeft());
        //setMargins(categoriesTitle, (int) targetTitleMargin);
        categoriesTitle.setX(targetTitleMargin);

        if (slideOffset == 0) {
            categoriesTitle.setTextColor(collapsedCategoriesTitleColor);
            categoriesTitle.setMetricFontStyle(
                    categoriesTitle.fontStyles[12]);//todo:mufler lookup from styles,
        } else {
            categoriesTitle.setTextColor(expandedCategoriesTitleColor);
            categoriesTitle.setMetricFontStyle(categoriesTitle.fontStyles[9]);
        }
    }

    private float dpToPx(int dp) {
        Resources r = parentActivity.getResources();
        float px = TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return px;
    }

    private void handleFab(float slideOffset) {
        float scaleInterpolated = fabInterpolator.getInterpolation(slideOffset);
        if (slideOffset > 0.15) {
            scaleInterpolated = 1;
        }
        try {
            final float scale = 1 - scaleInterpolated;
            fab.setEnabled(scale == 1f);
            fab.setScaleX(scale);
            fab.setScaleY(scale);
            //fab.setAlpha(scale);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }


    //stash
    private void setMargins(View view, int left) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, 0, 0, 0);
            view.setLeft(left);
            //// TODO: 28/04/2016 :mufler
            // i needed to call setMargin and setLeft because for unknown to me reasons setLeft actually moves the title,
            // but the new margin is not preserved during parent activity lifetime, i.e. pressing home button resets the title to left.
            // while setMargin doesnt move title bit saves the new margin.
        }
    }

}
