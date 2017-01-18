package com.hpe.sb.mobile.app.common.uiComponents.categories.adapter;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;
public class SpacingDecoration extends RecyclerView.ItemDecoration {

    private int horizontalSpacing = 0;
    private int verticalSpacing = 0;

    public SpacingDecoration(int hSpacing, int vSpacing) {
        horizontalSpacing = hSpacing;
        verticalSpacing = vSpacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if ((parent.getChildAdapterPosition(view) % 2) == 0) {
            outRect.right = horizontalSpacing;
        }else {
            outRect.left = horizontalSpacing;
        }
        outRect.bottom = verticalSpacing;
    }
}