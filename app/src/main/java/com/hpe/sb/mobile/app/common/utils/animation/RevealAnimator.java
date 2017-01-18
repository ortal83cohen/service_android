package com.hpe.sb.mobile.app.common.utils.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.os.Build;
import android.view.View;
import android.view.ViewAnimationUtils;


/**
 * Created by salemo on 26/04/2016.
 *
 */

public class RevealAnimator {
    private static final int ANIM_DURATION = 1550;

    public static Animator showCircularReveal(final View viewRoot, int x, int y, AnimatorListenerAdapter listener, int delay, int duration, float radiusFix) {
        Animator anim;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int width = viewRoot.getWidth() / 2;
            int height = viewRoot.getHeight() / 2;

            // get the final radius for the clipping circle
            float  finalRadius = (float) Math.hypot(width, height);
            anim = ViewAnimationUtils.createCircularReveal(viewRoot, x, y, 0, finalRadius*radiusFix);
            anim.setDuration(duration);
        } else {
            // Kitkat compatibility
            anim = ValueAnimator.ofInt(0, 1);
            anim.setDuration(10);
        }
        anim.addListener(listener);
        anim.setStartDelay(delay);
        return anim;
    }

    public static Animator hideCircularReveal(final View viewRoot, int x, int y, int delay) {
        Animator anim;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int initialRadius = viewRoot.getWidth();
            anim = ViewAnimationUtils.createCircularReveal(viewRoot, x, y, initialRadius, 0);
            anim.setDuration(ANIM_DURATION);
        } else {
            // Kitkat compatibility
            anim = ValueAnimator.ofInt(0, 1);
            anim.setDuration(10);
        }
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                viewRoot.setVisibility(View.INVISIBLE);
            }
        });
        anim.setDuration(ANIM_DURATION);
        anim.setStartDelay(delay);
        return anim;
    }
}
