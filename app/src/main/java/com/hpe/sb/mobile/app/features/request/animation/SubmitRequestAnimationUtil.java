package com.hpe.sb.mobile.app.features.request.animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.common.utils.animation.RevealAnimator;

/**
 * Created by malikdav on 08/05/2016.
 */
public class SubmitRequestAnimationUtil {

    public static final int REVEAL_DURATION = 750;
    public static final int SUBMITTED_FADE_IN_DURATION = 850;

    public static Animator createRequestAnimation(Activity activity) {

        Animator revealPageAnimation = getRevealPageAnimation(activity);
        Animator submittedContentFadeInAnimation = getSubmittedContentFadeIn(activity);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(revealPageAnimation, submittedContentFadeInAnimation);

        return animatorSet;
    }

    private static Animator getSubmittedContentFadeIn(Activity activity) {
        ViewGroup requestSubmitted = (ViewGroup) activity.findViewById(R.id.request_submitted);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(requestSubmitted, View.ALPHA, 0, 1);
        objectAnimator
                .setDuration(SUBMITTED_FADE_IN_DURATION)
                .setStartDelay(REVEAL_DURATION - 250);

        return objectAnimator;
    }

    private static Animator getRevealPageAnimation(Activity activity) {
        final View background = activity.findViewById(R.id.request_submitted_background);
        final Button submitButton = (Button) activity.findViewById(R.id.submit_button);
        int x, y;
        if(submitButton != null) {
            // Make the circle reveal start from the submit button center
            x = ((submitButton.getRight() - submitButton.getLeft()) / 2) + submitButton.getLeft();
            y = ((submitButton.getTop() - submitButton.getBottom()) / 2) + submitButton.getBottom();
        }else{//in the case we came from the webview, let start reveal from the right bottom of the screen
            DisplayMetrics displayMetrics = new DisplayMetrics();
            activity.getWindowManager()
                    .getDefaultDisplay()
                    .getMetrics(displayMetrics);
            x = displayMetrics.widthPixels;
            y = displayMetrics.heightPixels;
        }
        return RevealAnimator.showCircularReveal(background, x, y, new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                background.setVisibility(View.VISIBLE);
            }
        }, 0, REVEAL_DURATION, 1.8f);
    }
}
