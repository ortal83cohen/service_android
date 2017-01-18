package com.hpe.sb.mobile.app.features.request.scroll;

import com.hpe.sb.mobile.app.features.request.SoftKeyboardStateHolder;
import com.hpe.sb.mobile.app.features.request.recycleview.CurrentPositionHolder;
import com.hpe.sb.mobile.app.features.request.recycleview.SnappyRecyclerView;
import com.hpe.sb.mobile.app.features.request.recycleview.type.NewRequestViewType;

import java.util.List;

/**
 * Created by malikdav on 15/05/2016.
 */
public class NewRequestScroller {

    private final SnappyRecyclerView recyclerView;
    private final List<NewRequestViewType> viewTypes;
    private SoftKeyboardStateHolder softKeyboardStateHolder;
    private CurrentPositionHolder currentPositionHolder;

    public NewRequestScroller(SnappyRecyclerView recyclerView, List<NewRequestViewType> viewTypes, SoftKeyboardStateHolder softKeyboardStateHolder, CurrentPositionHolder currentPositionHolder) {

        this.recyclerView = recyclerView;
        this.viewTypes = viewTypes;
        this.softKeyboardStateHolder = softKeyboardStateHolder;
        this.currentPositionHolder = currentPositionHolder;
    }

    public void scrollToViewType(final int position) {

        if(softKeyboardStateHolder.isKeyboardOpen()) {
            softKeyboardStateHolder.setOnHideKeyboardListener(new SoftKeyboardStateHolder.OnHideKeyboardListener() {
                @Override
                public void invoke() {
                    if(recyclerView != null) {
                        recyclerView.smoothScrollToPosition(position);
                    }

                    // prevent scrolling to occur when you close the keyboard
                    softKeyboardStateHolder.setOnHideKeyboardListener(null);
                }
            });

        } else {
            recyclerView.smoothScrollToPosition(position);
        }
    }

    public void scrollToLast() {
        scrollToViewType(viewTypes.size() - 1);
    }

    public void scrollToNext() {
        recyclerView.smoothScrollToPosition(currentPositionHolder.getCurrentPosition() + 1);
    }

    public boolean isBackAvailable() {
        int currentPosition = currentPositionHolder.getCurrentPosition();
        return currentPosition > 0;
    }

    public void scrollToPrevious() {
        recyclerView.smoothScrollToPosition(currentPositionHolder.getCurrentPosition() - 1);
    }
}
