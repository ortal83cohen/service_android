package com.hpe.sb.mobile.app.features.request.recycleview.type;

import com.hpe.sb.mobile.app.serverModel.search.EntityItem;

import java.util.List;

/**
 * Created by malikdav on 25/04/2016.
 */
public class ChooseRelatedEntityViewType implements NewRequestViewType {

    private List<EntityItem> relatedEntities;

    public ChooseRelatedEntityViewType(List<EntityItem> relatedEntities) {
        this.relatedEntities = relatedEntities;
    }

    public List<EntityItem> getRelatedEntities() {
        return relatedEntities;
    }
}
