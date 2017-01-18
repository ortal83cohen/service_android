package com.hpe.sb.mobile.app.features.home;

import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.hpe.sb.mobile.app.R;

import java.util.List;

/**
 * Created by cohenort on 05/05/2016.
 */
public class MainActivityBehavior extends CoordinatorLayout.Behavior<View> {
        private static final boolean SNACKBAR_BEHAVIOR_ENABLED = Build.VERSION.SDK_INT >= 11;

    //    private ValueAnimatorCompat mFabTranslationYAnimator;
    private float mFabTranslationY;

    private Rect mTmpRect;
    private float lastY=0;

    public MainActivityBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, View child, View dependency) {
        return dependency instanceof Snackbar.SnackbarLayout;
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {

        updateFabTranslationForSnackbar( parent,
                child,  dependency);
        return  false;
    }
    private void updateFabTranslationForSnackbar(CoordinatorLayout parent,
            final View view, View snackbar) {
        final float targetTransY = getFabTranslationYForSnackbar(parent, view);
        if (mFabTranslationY == targetTransY) {
            // We're already at (or currently animating to) the target value, return...
            return;
        }

        final float currentTransY = ViewCompat.getTranslationY(view);

        // Make sure that any current animation is cancelled

        if (view.isShown()
                && Math.abs(currentTransY - targetTransY) > (view.getHeight() * 0.667f)) {
            // If the FAB will be travelling by more than 2/3 of it's height, let's animate
            // it instead

        } else {
            // Now update the translation Y
            LinearLayout categories = (LinearLayout)view.findViewById(R.id.categories_slide_pane);
            if (categories==null){
                ViewCompat.setTranslationY(view, targetTransY);
            }else {
                ViewCompat.setTranslationY(categories, targetTransY);
            }

        }

        mFabTranslationY = targetTransY;
    }
    private float getFabTranslationYForSnackbar(CoordinatorLayout parent,
            View fab) {
        float minOffset = 0;
        final List<View> dependencies = parent.getDependencies(fab);
        for (int i = 0, z = dependencies.size(); i < z; i++) {
            final View view = dependencies.get(i);
            if (view instanceof Snackbar.SnackbarLayout && parent.doViewsOverlap(fab, view)) {
                minOffset = Math.min(minOffset,
                        ViewCompat.getTranslationY(view) - view.getHeight());
            }
        }

        return minOffset;
    }
}
