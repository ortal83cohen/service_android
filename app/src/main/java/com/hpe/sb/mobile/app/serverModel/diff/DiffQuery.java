package com.hpe.sb.mobile.app.serverModel.diff;

import java.util.List;

public class DiffQuery {

    private List<DiffQueryItem> items;

    public DiffQuery() {

    }

    public DiffQuery(List<DiffQueryItem> items) {
        this.items = items;
    }

    public List<DiffQueryItem> getItems() {
        return items;
    }

    public void setItems(List<DiffQueryItem> items) {
        this.items = items;
    }

}
