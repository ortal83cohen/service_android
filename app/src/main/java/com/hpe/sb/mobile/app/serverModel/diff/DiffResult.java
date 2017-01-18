package com.hpe.sb.mobile.app.serverModel.diff;

import java.util.List;

public class DiffResult<T> {

    private List<T> addedOrUpdated;
    private List<String> deletedIds;
    private List<String> orderedIds;

    public DiffResult() {
    }

    public DiffResult(List<T> addedOrUpdated, List<String> deletedIds, List<String> orderedIds) {
        this.addedOrUpdated = addedOrUpdated;
        this.deletedIds = deletedIds;
        this.orderedIds = orderedIds;
    }

    public List<T> getAddedOrUpdated() {
        return addedOrUpdated;
    }

    public void setAddedOrUpdated(List<T> addedOrUpdated) {
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
