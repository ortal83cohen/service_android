package com.hpe.sb.mobile.app.serverModel;

import com.hpe.sb.mobile.app.serverModel.diff.DiffQuery;

public class InitialDataQuery {

    private DiffQuery categoriesDiffQuery;

    public InitialDataQuery() {
    }

    public DiffQuery getCategoriesDiffQuery() {
        return categoriesDiffQuery;
    }

    public void setCategoriesDiffQuery(DiffQuery categoriesDiffQuery) {
        this.categoriesDiffQuery = categoriesDiffQuery;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        InitialDataQuery that = (InitialDataQuery) o;

        return categoriesDiffQuery != null ? categoriesDiffQuery.equals(that.categoriesDiffQuery) : that.categoriesDiffQuery == null;

    }

    @Override
    public int hashCode() {
        return categoriesDiffQuery != null ? categoriesDiffQuery.hashCode() : 0;
    }

}
