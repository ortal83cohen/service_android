package com.hpe.sb.mobile.app.features.request.recycleview.type;

/**
 * Created by malikdav on 25/04/2016.
 *
 */
public class ChooseRelatedEntityInCategoryViewType implements NewRequestViewType {

    private String catalogGroupId;


    public ChooseRelatedEntityInCategoryViewType(String catalogGroupId) {
        this.catalogGroupId = catalogGroupId;
    }

    public String getCatalogGroupId() {
        return catalogGroupId;
    }
}
