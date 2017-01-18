package com.hpe.sb.mobile.app.common.uiComponents.categories;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;

import com.hpe.sb.mobile.app.common.uiComponents.categories.adapter.CategoriesViewAdapter;
import com.hpe.sb.mobile.app.common.uiComponents.categories.adapter.SpacingDecoration;
import com.hpe.sb.mobile.app.serverModel.catalog.CatalogGroup;

import java.util.List;

/**
 * Created by salemo on 28/04/2016.
 * use this class to initialize RecyclerView of Related Entities
 */
public class CategoryPageRecyclerViewHelper {

    private final Context context;

    public CategoryPageRecyclerViewHelper(Context context) {
        this.context = context;
    }


    /**
     * initialize a RecyclerView of RelatedEntities list
     *
     * @param recyclerView          - the RecyclerView to initialize
     * @param isHightlightable      - if true we highlight indication upon selection
     * @param itemSelectedListener  - listener that should implement the logic while clicking on EntityItem
     */
    public void initRecyclerView(RecyclerView recyclerView, boolean isHightlightable,
                                 OnCategorySelectListener itemSelectedListener) {
        recyclerView.setLayoutManager(new GridLayoutManager(context, 2/*spanCount*/));

        CategoriesViewAdapter rcAdapter = new CategoriesViewAdapter(context);
        rcAdapter.setItemSelectedListener(itemSelectedListener);
        rcAdapter.setIsHightlightable(isHightlightable);
        recyclerView.setAdapter(rcAdapter);

        recyclerView.addItemDecoration(new SpacingDecoration(dpToPx(8), dpToPx(16)));
    }

    public void updateItems(RecyclerView recyclerView, List<CatalogGroup> categories) {
        ((CategoriesViewAdapter)recyclerView.getAdapter()).setItemList(categories);
        (recyclerView.getAdapter()).notifyDataSetChanged();
    }

    private int dpToPx(int dp) {
        Resources r = context.getResources();
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
}
