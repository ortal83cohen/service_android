package com.hpe.sb.mobile.app.common.uiComponents.todocards.animations;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.view.View;

import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.utils.TodoCardsEvent;
import com.hpe.sb.mobile.app.infra.eventbus.EventBus;
import com.hpe.sb.mobile.app.common.uiComponents.metricFont.MetricFontCustomTextView;
import com.hpe.sb.mobile.app.common.utils.animation.RevealAnimator;

/**
 * Created by salemo on 27/04/2016.
 *
 */
public class TodoCardsAnimationUtil {
    public static final int SHADOW_ANIMATION_DURATION = 700;
    public static final int SHAPE_ANIMATION_DURATION = 600;
    public static final int V_ICON_ANIMATION_DURATION = 700;
    public static final int X_ICON_ANIMATION_DURATION = 350;
    public static final int TEXT_ANIMATION_DURATION = 1000;

    public void executePositiveActionAnimationsAndSendEvent(final View view,
                                                            final Context context, final EventBus<TodoCardsEvent> eventBus,
                                                            TodoCardsEvent eventToSend) {
        final View animationShadow = view.findViewById(R.id.card_animation_shadow);
        final MetricFontCustomTextView animationText = (MetricFontCustomTextView) view.findViewById(R.id.todo_card_animation_text);
        executeAnimationsAndSendEvent(animationShadow, animationText, context, eventBus, true, eventToSend, view);
    }

    public void executeNegativeActionAnimationsAndSendEvent(final View view,
                                                            final Context context, final EventBus<TodoCardsEvent> eventBus,
                                                            TodoCardsEvent eventToSend) {
        final View animationShadow = view.findViewById(R.id.card_animation_shadow);
        final MetricFontCustomTextView animationText = (MetricFontCustomTextView) view.findViewById(R.id.negative_todo_card_animation_text);
        executeAnimationsAndSendEvent(animationShadow, animationText, context, eventBus, false, eventToSend, view);
    }

    public void removeNegativeAnimation(View view, final Context context) {
        final View animationShadow = view.findViewById(R.id.card_animation_shadow);
        final MetricFontCustomTextView animationText = (MetricFontCustomTextView) view.findViewById(R.id.negative_todo_card_animation_text);
        final View animationShape = view.findViewById(R.id.todo_card_animation_shape);
        final View animationIconRight = view.findViewById(R.id.todo_card_animation_icon_right);
        final View animationIconLeft = view.findViewById(R.id.todo_card_animation_icon_left);
        animationShadow.setVisibility(View.INVISIBLE);
        animationShape.setVisibility(View.INVISIBLE);
        animationIconRight.setVisibility(View.INVISIBLE);
        animationIconLeft.setVisibility(View.INVISIBLE);
        animationText.setTextColor(context.getResources().getColor(R.color.card_negative_animation_text_invisible_color));
    }

    public void removePositiveAnimation(View view, final Context context) {
        final View animationShadow = view.findViewById(R.id.card_animation_shadow);
        final MetricFontCustomTextView animationText = (MetricFontCustomTextView) view.findViewById(R.id.todo_card_animation_text);
        final View animationShape = view.findViewById(R.id.positive_todo_card_animation_shape);
        final View animationIcon = view.findViewById(R.id.positive_animation_icon);
        animationShadow.setVisibility(View.INVISIBLE);
        animationShape.setVisibility(View.INVISIBLE);
        animationIcon.setVisibility(View.INVISIBLE);
        animationText.setTextColor(context.getResources().getColor(R.color.card_negative_animation_text_invisible_color));
    }

    private void executeAnimationsAndSendEvent(final View animationShadow, final MetricFontCustomTextView animationText,
                                               final Context context, final EventBus<TodoCardsEvent> eventBus,
                                               final boolean isPositive, final TodoCardsEvent eventToSend,
                                               final View view) {
        view.post(new Runnable()
        {
            @Override
            public void run()
            {
                int animationShadowX;
                if (isPositive) {
                    animationShadowX = animationShadow.getRight();
                } else {
                    animationShadowX = animationShadow.getLeft();
                }
                int animationShadowY = animationShadow.getBottom();
                Animator animationShadowShow = RevealAnimator.showCircularReveal(animationShadow, animationShadowX, animationShadowY,
                        new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationStart(Animator animation) {
                                animationShadow.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animator animation) {
                                executeActionTextAndShapeAnimationsAndSendEvent(context, animationText,
                                        eventBus, isPositive, eventToSend, view);
                            }
                        }, 0, SHADOW_ANIMATION_DURATION, 2.5f);
                animationShadowShow.start();
            }
        });

    }

    private void executeActionTextAndShapeAnimationsAndSendEvent(Context context, final MetricFontCustomTextView animationText,
                                                                 EventBus<TodoCardsEvent> eventBus, boolean isPositive,
                                                                 TodoCardsEvent eventToSend, View view) {
        Animator animationShapeShow;
        Integer colorFrom;
        Integer colorTo;
        if (isPositive) {
            final View animationShape = view.findViewById(R.id.positive_todo_card_animation_shape);
            animationShapeShow = getPositiveActionShapeAnimationWithSendEventWhenDone(context,
                    eventBus, animationShape, eventToSend, view);
            colorFrom = getColor(context, R.color.request_positive_animation_text_invisible_color);
            colorTo = getColor(context, R.color.request_resolved_card_accept_text_visible_color);
        } else {
            final View animationShape = view.findViewById(R.id.todo_card_animation_shape);
            animationShapeShow = getNegativeActionShapeAnimationWithSendEventWhenDone(context,
                    eventBus, animationShape, eventToSend, view);
            colorFrom = getColor(context, R.color.card_negative_animation_text_invisible_color);
            colorTo = getColor(context, R.color.card_negative_animation__text_visible_color);
        }
        ValueAnimator textColorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        textColorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                animationText.setTextColor((Integer) animator.getAnimatedValue());
            }
        });
        textColorAnimation.setDuration(TEXT_ANIMATION_DURATION);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animationShapeShow, textColorAnimation);
        animatorSet.start();
    }

    private int getColor(Context context, int request_positive_animation_text_invisible_color) {
        return context.getResources().getColor(request_positive_animation_text_invisible_color);
    }

    private Animator getPositiveActionShapeAnimationWithSendEventWhenDone(Context context, EventBus<TodoCardsEvent> eventBus,
                                                                          final View animationShape, TodoCardsEvent eventToSend, View view) {
        final View animationIcon = view.findViewById(R.id.positive_animation_icon);
        AnimationIconWrapper animationIconWrapper = new AnimationIconWrapper(animationIcon).invoke();
        int animationIconCenterX = animationIconWrapper.getAnimationIconCenterX();
        int animationIconCenterY = animationIconWrapper.getAnimationIconCenterY();
        final Animator animationIconShow = getPositiveActionIconAnimationWithSendEventWhenDone(context, animationIconWrapper, eventBus, eventToSend);
        AnimatorListenerAdapter animationShapeListenerAdapter = getVisibleAnimatorListenerAdapter(animationShape, animationIconShow);
        return RevealAnimator.showCircularReveal(animationShape, animationIconCenterX,
                animationIconCenterY, animationShapeListenerAdapter, 0, SHAPE_ANIMATION_DURATION, 1f);
    }

    private Animator getNegativeActionShapeAnimationWithSendEventWhenDone(Context context, EventBus<TodoCardsEvent> eventBus,
                                                                          final View animationShape, TodoCardsEvent eventToSend, View view) {
        final View animationIconRight = view.findViewById(R.id.todo_card_animation_icon_right);
        final View animationIconLeft = view.findViewById(R.id.todo_card_animation_icon_left);
        AnimationIconWrapper animationIconWrapper = new AnimationIconWrapper(animationIconRight).invoke();
        int animationIconCenterX = animationIconWrapper.getAnimationIconCenterX();
        int animationIconCenterY = animationIconWrapper.getAnimationIconCenterY();
        final Animator animationIconShow = getNegativeActionIconAnimationWithSendEventWhenDone(context, animationIconRight, animationIconLeft, eventBus, eventToSend); //continue here
        AnimatorListenerAdapter animationShapeListenerAdapter = getVisibleAnimatorListenerAdapter(animationShape, animationIconShow);
        return RevealAnimator.showCircularReveal(animationShape, animationIconCenterX,
                animationIconCenterY, animationShapeListenerAdapter, 0, SHAPE_ANIMATION_DURATION, 1f);
    }

    private Animator getNegativeActionIconAnimationWithSendEventWhenDone(Context context, View animationIconRight,
                                                                         final View animationIconLeft, EventBus<TodoCardsEvent> eventBus, TodoCardsEvent eventToSend) {
        Animator negativeSolutionAnimationRightIconShow = getNegativeActionAnimationRightIconShow(context, animationIconRight, eventBus, eventToSend);
        AnimatorListenerAdapter animationLeftIconListenerAdapter = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                animationIconLeft.setVisibility(View.VISIBLE);
            }
        };
        //fix the start place of the animation to start at the start of the "x" icon
        int animationIconX = animationIconLeft.getLeft() - getDimensionPixelSize(context, R.dimen.request_resolved_icon_dimen_fix);
        int animationIconY = animationIconLeft.getTop() - getDimensionPixelSize(context, R.dimen.request_resolved_icon_dimen_fix);
        Animator negativeSolutionAnimationLeftIconShow = RevealAnimator.showCircularReveal(animationIconLeft, animationIconX, animationIconY,
                animationLeftIconListenerAdapter, 0, X_ICON_ANIMATION_DURATION, 2f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(negativeSolutionAnimationLeftIconShow, negativeSolutionAnimationRightIconShow);
        return animatorSet;
    }

    private Animator getNegativeActionAnimationRightIconShow(Context context, View animationIconRight, EventBus<TodoCardsEvent> eventBus, TodoCardsEvent eventToSend) {
        AnimatorListenerAdapter animationRightIconListenerAdapter = getActionIconAnimatorListenerAdapterWithSendEvent(eventBus, eventToSend, animationIconRight);
        //fix the start place of the animation to start at the start of the "x" icon
        int animationIconX = animationIconRight.getRight() - getDimensionPixelSize(context, R.dimen.request_resolved_icon_dimen_fix);
        int animationIconY = animationIconRight.getTop() - getDimensionPixelSize(context, R.dimen.request_resolved_icon_dimen_fix);
        return RevealAnimator.showCircularReveal(animationIconRight, animationIconX, animationIconY,
                animationRightIconListenerAdapter, X_ICON_ANIMATION_DURATION-200, X_ICON_ANIMATION_DURATION, 2f);
    }

    private AnimatorListenerAdapter getVisibleAnimatorListenerAdapter(final View animationIconLeft,
                                                                             final Animator animationIconRightShow) {
        return new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                animationIconLeft.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                animationIconRightShow.start();
            }
        };
    }

    private Animator getPositiveActionIconAnimationWithSendEventWhenDone(Context context, AnimationIconWrapper animationIconWrapper,
                                                                         final EventBus<TodoCardsEvent> eventBus, final TodoCardsEvent eventToSend) {
        final View animationIcon = animationIconWrapper.getAnimationIcon();
        final int animationIconCenterY = (animationIconWrapper.getAnimationIconTop() + animationIconWrapper.getAnimationIconBottom()) / 2;
        AnimatorListenerAdapter animationIconListenerAdapter = getActionIconAnimatorListenerAdapterWithSendEvent(eventBus, eventToSend, animationIcon);
        //fix the start place of the animation to start at the start of the "v" icon
        int dimensionPixelFixSize = getDimensionPixelSize(context, R.dimen.request_resolved_icon_dimen_fix);
        int fixedAnimationIconCenterX = animationIconWrapper.getAnimationIconLeft() - dimensionPixelFixSize;
        int fixedAnimationIconCenterY = animationIconCenterY - dimensionPixelFixSize;
        return RevealAnimator.showCircularReveal(animationIcon, fixedAnimationIconCenterX, fixedAnimationIconCenterY,
                animationIconListenerAdapter, 0, V_ICON_ANIMATION_DURATION, 2f);
    }

    private AnimatorListenerAdapter getActionIconAnimatorListenerAdapterWithSendEvent(final EventBus<TodoCardsEvent> eventBus, final TodoCardsEvent eventToSend, final View animationIcon) {
        return new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                animationIcon.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                eventBus.send(eventToSend);
            }
        };
    }

    private int getDimensionPixelSize(Context context, int id) {
        return context.getResources().getDimensionPixelSize(id);
    }

    private class AnimationIconWrapper {
        private View animationIcon;
        private int animationIconLeft;
        private int animationIconTop;
        private int animationIconBottom;
        private int animationIconCenterX;
        private int animationIconCenterY;

        public AnimationIconWrapper(View animationIcon) {
            this.animationIcon = animationIcon;
        }

        public int getAnimationIconLeft() {
            return animationIconLeft;
        }

        public int getAnimationIconTop() {
            return animationIconTop;
        }

        public int getAnimationIconBottom() {
            return animationIconBottom;
        }

        public int getAnimationIconCenterX() {
            return animationIconCenterX;
        }

        public int getAnimationIconCenterY() {
            return animationIconCenterY;
        }

        public AnimationIconWrapper invoke() {
            int animationIconRight = animationIcon.getRight();
            animationIconLeft = animationIcon.getLeft();
            animationIconTop = animationIcon.getTop();
            animationIconBottom = animationIcon.getBottom();
            animationIconCenterX = (animationIconLeft + animationIconRight) / 2;
            animationIconCenterY = (animationIconTop + animationIconBottom) / 2;
            return this;
        }

        public View getAnimationIcon() {
            return animationIcon;
        }
    }
}
