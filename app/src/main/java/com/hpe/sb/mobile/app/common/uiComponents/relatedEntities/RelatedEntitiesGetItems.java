package com.hpe.sb.mobile.app.common.uiComponents.relatedEntities;

import com.android.volley.Response;
import com.hpe.sb.mobile.app.serverModel.search.EntityItem;

import java.util.List;

/**
 * Created by salemo on 28/04/2016.
 * implement this method to define the way of retrieving items for your related entities list
 * pls call listener.onResponse with the data returned (for the flow to work).
 */
public interface RelatedEntitiesGetItems {

    void getItems(Response.Listener<List<EntityItem>> listener, int offset, int numberOfItemsToReturn);

}
