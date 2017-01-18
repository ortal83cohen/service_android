package com.hpe.sb.mobile.app.serverModel.catalog;

import java.util.List;

public class CatalogGroupDiffResult {

    private List<CatalogGroup> addedOrUpdated;
    private List<String> deletedIds;
    private List<String> orderedIds;

    public CatalogGroupDiffResult() {
    }

    public List<CatalogGroup> getAddedOrUpdated() {
        return addedOrUpdated;
    }

    public void setAddedOrUpdated(List<CatalogGroup> addedOrUpdated) {
        this.addedOrUpdated = addedOrUpdated;
    }

    public List<String> getDeletedIds() {
        return deletedIds;
    }

    public void setDeletedIds(List<String> deletedIds) {
        this.deletedIds = deletedIds;
    }

    public List<String> getOrderedIds() {
        return orderedIds;
    }

    public void setOrderedIds(List<String> orderedIds) {
        this.orderedIds = orderedIds;
    }

}
