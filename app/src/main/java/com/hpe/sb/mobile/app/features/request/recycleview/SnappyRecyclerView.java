package com.hpe.sb.mobile.app.features.request.recycleview;

import com.hpe.sb.mobile.app.R;

import android.content.Context;
import android.os.Handler;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_FLING;
import static android.widget.AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL;

/**
 * Created by malikdav on 16/05/2016.
 */
public class SnappyRecyclerView extends RecyclerView {

    public static final int FLING_VELOCITY_THRESHOLD = 400;

    private CurrentPositionHolder currentPositionHolder = null;

    private boolean snapEnabled = true;

    private boolean isUserScrolling = false;

    private boolean scrolling = false;

    private int scrollState;

    private long lastScrollTime = 0;

    private Handler handler = new Handler();

    private boolean mScaleUnfocusedViews = false;

    private final static int MINIMUM_SCROLL_EVENT_OFFSET_MS = 20;

    private int mLastMotionY;

    public SnappyRecyclerView(Context context) {
        super(context);
    }

    public SnappyRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Enable snapping behaviour for this recyclerView
     *
     * @param enabled enable or disable the snapping behaviour
     */
    public void setSnapEnabled(boolean enabled) {
        snapEnabled = enabled;

        if (enabled) {
            addOnLayoutChangeListener(new OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View v, int left, int top, int right, int bottom,
                        int oldLeft, int oldTop, int oldRight, int oldBottom) {
                    if (left == oldLeft && right == oldRight && top == oldTop
                            && bottom == oldBottom) {
                        removeOnLayoutChangeListener(this);
                        updateViews();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                scrollToView(getChildAt(0));
                            }
                        }, 20);
                    }
                }
            });

            setOnScrollListener(new OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    updateViews();
                    super.onScrolled(recyclerView, dx, dy);
                }

                @Override
                public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    /** if scroll is caused by a touch (scroll touch, not any touch) **/
                    if (newState == SCROLL_STATE_TOUCH_SCROLL) {
                        /** if scroll was initiated already, this is not a user scrolling, but probably a tap, else set userScrolling **/
                        if (!scrolling) {
                            isUserScrolling = true;
                        }
                    } else if (newState == SCROLL_STATE_IDLE) {
                        if (isUserScrolling) {
                            scrollToView(findViewClosestToCenter());
                        }

                        isUserScrolling = false;
                        scrolling = false;
                    } else if (newState == SCROLL_STATE_FLING) {
                        scrolling = true;
                    }

                    scrollState = newState;
                }
            });
        } else {
            setOnScrollListener(null);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (scrolling) {
            // If we are already in a scroll, we want to prevent the user from stopping the ongoing scroll
            return true;
        }

        return super.onTouchEvent(e);
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        if (Math.abs(velocityY) < FLING_VELOCITY_THRESHOLD) {
            return false;
        }
        if (isUserScrolling) {
            int currentPosition = currentPositionHolder.getCurrentPosition();
            updateCurrentPosition(velocityY);
            int afterUpdatePosition = currentPositionHolder.getCurrentPosition();
            if (afterUpdatePosition != currentPosition) {
                super.smoothScrollToPosition(currentPositionHolder.getCurrentPosition());
                isUserScrolling = false;
                scrolling = false;

                return true;
            }
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {

        if (scrolling) {
            // If we are already in a scroll, we want to prevent the user from stopping the ongoing scroll
            return true;
        }

        final NestedScrollView nestedScroll = (NestedScrollView) findViewById(R.id.nested_scroll);
        if (nestedScroll == null) {
            return super.onInterceptTouchEvent(e);
        }

        View nestedScrollLastChild = nestedScroll.getChildAt(nestedScroll.getChildCount() - 1);
        FrameLayout.LayoutParams layoutParams = (ScrollView.LayoutParams) nestedScrollLastChild
                .getLayoutParams();
        ViewGroup.LayoutParams layoutParams2 = nestedScrollLastChild
                .getLayoutParams();

        final int actionMasked = MotionEventCompat.getActionMasked(e);

        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN: {
                // Remember where the motion event started
                mLastMotionY = (int) e.getY();
            }
            break;
            case MotionEvent.ACTION_MOVE: {
                final int activePointerIndex = MotionEventCompat.findPointerIndex(e,
                        MotionEventCompat.getPointerId(e, 0));
                final int y = (int) MotionEventCompat.getY(e, activePointerIndex);
                int deltaY = mLastMotionY - y;
                boolean atTheEnd = nestedScrollLastChild.getBottom()
                        <= nestedScroll.getHeight() + nestedScroll.getScrollY()
                        + layoutParams.topMargin;

                if ((deltaY > 0 && !atTheEnd) // not at the end and pull down
                        || (nestedScroll.getScrollY() != 0
                        && deltaY < 0))// not at the beginning and pull up
                {
                    return false;
                }
            }
            break;
        }

        return super.onInterceptTouchEvent(e);
    }

    private void updateCurrentPosition(int direction) {
        if (currentPositionHolder == null) {
            throw new RuntimeException(String.format("You must set a %s, for %s",
                    CurrentPositionHolder.class.getName(), this.getClass().getName()));
        }

        currentPositionHolder.updatePositionByDirection(direction, getAdapter().getItemCount());
    }


    /**
     * Enable snapping behaviour for this recyclerView
     *
     * @param enabled             enable or disable the snapping behaviour
     * @param scaleUnfocusedViews downScale the views which are not focused based on how far away
     *                            they are from the center
     */
    public void setSnapEnabled(boolean enabled, boolean scaleUnfocusedViews) {
        this.mScaleUnfocusedViews = scaleUnfocusedViews;
        setSnapEnabled(enabled);
    }

    private void updateViews() {
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
//            setMarginsForChild(child);

            if (mScaleUnfocusedViews) {
                float percentage = getPercentageFromCenter(child);
                float scale = 1f - (0.7f * percentage);

                child.setScaleX(scale);
                child.setScaleY(scale);
            }
        }
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!snapEnabled) {
            return super.dispatchTouchEvent(event);
        }

        long currentTime = System.currentTimeMillis();

        /** if touch events are being spammed, this is due to user scrolling right after a tap,
         * so set userScrolling to true **/
        if (scrolling && scrollState == SCROLL_STATE_TOUCH_SCROLL) {
            if ((currentTime - lastScrollTime) < MINIMUM_SCROLL_EVENT_OFFSET_MS) {
                isUserScrolling = true;
            }
        }

        lastScrollTime = currentTime;

//        if (!isUserScrolling) {
//            if (event.getAction() == MotionEvent.ACTION_UP) {
//                View targetView = findViewClosestToCenter();
//                scrollToView(targetView);
////                if (targetView != findViewClosestToCenter()) {
////                    scrollToView(targetView);
////                    return true;
////                }
//            }
//        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    public void setLayoutManager(LayoutManager layout) {
        if (!(layout instanceof LinearLayoutManager)) {
            throw new RuntimeException(
                    String.format("%s support only %s", this.getClass().getSimpleName(),
                            LinearLayoutManager.class.getSimpleName()));
        }
        super.setLayoutManager(layout);
    }


    private View findViewClosestToCenter() {
        LinearLayoutManager lm = (LinearLayoutManager) getLayoutManager();
        int centerYRecycleView = getHeight() / 2;

        int minDistance = 0;
        View view;
        View returnView = null;
        boolean notFound = true;

        int firstVisibleItemPosition = lm.findFirstVisibleItemPosition();
        int lastVisibleItemPosition = lm.findLastVisibleItemPosition();

        if (firstVisibleItemPosition != RecyclerView.NO_POSITION) {
            for (int i = firstVisibleItemPosition;
                 i <= lastVisibleItemPosition && notFound; i++) {

                view = lm.findViewByPosition(i);

                int center = (view.getHeight() / 2) + view.getTop();
                int leastDifference = Math.abs(centerYRecycleView - center);

                if (leastDifference <= minDistance || i == firstVisibleItemPosition) {
                    minDistance = leastDifference;
                    returnView = view;
                } else {
                    notFound = false;
                }
            }
        }
        return returnView;
    }

    private void scrollToView(View child) {
        if (child == null) {
            return;
        }

        stopScroll();

        int scrollDistance = getScrollDistance(child);
        if (scrollDistance != 0) {
            smoothScrollBy(0, scrollDistance);

            // update position only if the view target is different then the current
            if (currentPositionHolder.getCurrentPosition() != getLayoutManager()
                    .getPosition(child)) {
                updateCurrentPosition(scrollDistance);
            }
        }
    }

    private int getScrollDistance(View child) {
        int childHeight = child.getHeight();
        int centerY = getHeight() / 2;

        int childCenterY = (child.getTop() + (childHeight / 2));

        return childCenterY - centerY;
    }

    private float getPercentageFromCenter(View child) {
        float centerY = (getHeight() / 2);
        float childCenterY = child.getTop() + (child.getHeight() / 2);

        float offSet = Math.max(centerY, childCenterY) - Math.min(centerY, childCenterY);

        int maxOffset = (getHeight() / 2) + child.getHeight();

        return (offSet / maxOffset);
    }


    public int getHorizontalScrollOffset() {
        return computeHorizontalScrollOffset();
    }

    public int getVerticalScrollOffset() {
        return computeVerticalScrollOffset();
    }

    public void smoothUserScrollBy(int x, int y) {
        isUserScrolling = true;
        smoothScrollBy(x, y);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        handler.removeCallbacksAndMessages(null);
    }

    public void setCurrentPositionHolder(CurrentPositionHolder currentPositionHolder) {
        this.currentPositionHolder = currentPositionHolder;
    }
}
