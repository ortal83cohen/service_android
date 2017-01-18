package com.hpe.sb.mobile.app.common.uiComponents.relatedEntities;

import android.support.v7.widget.LinearLayoutManager;

import com.android.volley.Response;
import com.hpe.sb.mobile.app.serverModel.search.EntityItem;

import java.util.List;

/**
 * Created by salemo on 28/04/2016.
 * Related Entities Endless scroll logic
 */
public class RelatedEntitiesEndlessRecyclerOnScrollListener extends EndlessRecyclerOnScrollListener {
    public static final int NUMBER_OF_OFFERINGS_TO_RETURN = 20;
    private RelatedEntitiesGetItems relatedEntitiesGetItems;
    private int offset;
    private boolean canBringMoreRelatedEntities = true;
    protected RelatedEntitiesAdapter relatedEntitiesAdapter;

    public RelatedEntitiesEndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager,
                                                          RelatedEntitiesAdapter relatedEntitiesAdapter, RelatedEntitiesGetItems relatedEntitiesGetItems) {
        super(linearLayoutManager);
        this.offset = relatedEntitiesAdapter.getItemCount();
        this.relatedEntitiesAdapter = relatedEntitiesAdapter;
        this.relatedEntitiesGetItems = relatedEntitiesGetItems;
    }

    @Override
    public void onLoadMore() {
        if (canBringMoreRelatedEntities) {
            relatedEntitiesGetItems.getItems(new Response.Listener<List<EntityItem>>() {
                @Override
                public void onResponse(List<EntityItem> relatedEntitiesList) {
                    int numberOfMGOfferingsReturned = relatedEntitiesList.size();
                    offset += numberOfMGOfferingsReturned;
                    if (numberOfMGOfferingsReturned != 0) {
                        relatedEntitiesAdapter.addToRelatedEntitiesToList(relatedEntitiesList);
                    }
                    if (numberOfMGOfferingsReturned < NUMBER_OF_OFFERINGS_TO_RETURN) {
                        canBringMoreRelatedEntities = false;
                    }
                }
            }, offset, NUMBER_OF_OFFERINGS_TO_RETURN);
        }
    }
}
