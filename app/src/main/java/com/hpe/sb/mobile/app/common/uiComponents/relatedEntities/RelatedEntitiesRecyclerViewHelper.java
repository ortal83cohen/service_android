package com.hpe.sb.mobile.app.common.uiComponents.relatedEntities;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.hpe.sb.mobile.app.serverModel.search.EntityItem;

import java.util.List;

/**
 * Created by salemo on 28/04/2016.
 * use this class to initialize RecyclerView of Related Entities
 */
public class RelatedEntitiesRecyclerViewHelper {

    private final Context context;

    public RelatedEntitiesRecyclerViewHelper(Context context) {
        this.context = context;
    }

    /**
     * initialize a RecyclerView of RelatedEntities list
     *
     * @param recyclerView              - the RecyclerView to initialize
     * @param isHightlightable          - if true we highlight indication upon selection
     * @param relatedEntitiesGetItems   - a class that indicates the why of retrieving data to the RecyclerView list.
     * @param listener                  - listener that should implement the logic while clicking on EntityItem
     */
    public void initRecyclerView(RecyclerView recyclerView, boolean isHightlightable,
                                 RelatedEntitiesGetItems relatedEntitiesGetItems, OnEntityItemSelectedListener listener) {
        recyclerView.setHasFixedSize(true);

        RelatedEntitiesAdapter relatedEntitiesAdapter = new RelatedEntitiesAdapter(context);
        relatedEntitiesAdapter.setItemSelectedListener(listener);
        relatedEntitiesAdapter.setIsHightlightable(isHightlightable);
        recyclerView.setAdapter(relatedEntitiesAdapter);

        recyclerView.addItemDecoration(new RelatedEntitiesListDividerDecoration(context, LinearLayoutManager.VERTICAL));
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(linearLayoutManager);
        if (relatedEntitiesGetItems != null) {
            recyclerView.addOnScrollListener(new RelatedEntitiesEndlessRecyclerOnScrollListener(
                    linearLayoutManager, relatedEntitiesAdapter, relatedEntitiesGetItems));
        }
    }

    public void updateItems(RecyclerView recyclerView, List<EntityItem> entityItems) {
        ((RelatedEntitiesAdapter)recyclerView.getAdapter()).setItemList(entityItems);
        (recyclerView.getAdapter()).notifyDataSetChanged();
    }
}
