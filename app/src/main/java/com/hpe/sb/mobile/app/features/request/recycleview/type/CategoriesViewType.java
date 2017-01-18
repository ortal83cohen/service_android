package com.hpe.sb.mobile.app.features.request.recycleview.type;

import com.hpe.sb.mobile.app.serverModel.catalog.CatalogGroup;

import java.util.List;

/**
 * Created by malikdav on 25/04/2016.
 */
public class CategoriesViewType implements NewRequestViewType {

    private List<CatalogGroup> categories;
    private boolean isShowStillNotFoundMessage;

    public CategoriesViewType(List<CatalogGroup> categories, boolean isShowStillNotFoundMessage) {
        this.categories = categories;
        this.isShowStillNotFoundMessage = isShowStillNotFoundMessage;
    }

    public List<CatalogGroup> getCategories() {
        return categories;
    }

    public boolean isShowStillNotFoundMessage() {
        return isShowStillNotFoundMessage;
    }
}
