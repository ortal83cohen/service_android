package com.hpe.sb.mobile.app.features.request.recycleview;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by malikdav on 05/05/2016.
 */
public class NewRequestLayoutManager extends LinearLayoutManager {
    private CurrentPositionHolder currentPositionHolder;

    private boolean isEnabled = true;

    public NewRequestLayoutManager(Context context, CurrentPositionHolder currentPositionHolder) {
        super(context);
        this.currentPositionHolder = currentPositionHolder;
    }

    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        int updatedPosition = currentPositionHolder.updatePosition(position, getItemCount());
        super.smoothScrollToPosition(recyclerView, state, updatedPosition);
    }

    @Override
    public void scrollToPosition(int position) {
        int updatedPosition = currentPositionHolder.updatePosition(position, getItemCount());
        super.scrollToPosition(updatedPosition);
    }

    @Override
    public void scrollToPositionWithOffset(int position, int offset) {
        throw new UnsupportedOperationException();
//        super.scrollToPositionWithOffset(position, offset);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
//        currentPositionHolder.updatePositionByDirection(dy, getChildCount());
        return super.scrollVerticallyBy(dy, recycler, state);
    }

    public void setScrollEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    @Override
    public boolean canScrollVertically() {
        return isEnabled && super.canScrollVertically();
    }
}
