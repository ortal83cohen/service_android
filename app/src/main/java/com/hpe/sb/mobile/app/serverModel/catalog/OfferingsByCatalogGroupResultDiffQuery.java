package com.hpe.sb.mobile.app.serverModel.catalog;

import com.hpe.sb.mobile.app.serverModel.diff.DiffQuery;

/**
 * Created by salemo on 18/05/2016.
 *
 */
public class OfferingsByCatalogGroupResultDiffQuery {

    private DiffQuery offerings;

    private DiffQuery catalogGroups;

    public OfferingsByCatalogGroupResultDiffQuery(DiffQuery offerings, DiffQuery catalogGroups) {
        this.offerings = offerings;
        this.catalogGroups = catalogGroups;
    }

    public DiffQuery getOfferings() {
        return offerings;
    }

    public void setOfferings(DiffQuery offerings) {
        this.offerings = offerings;
    }

    public DiffQuery getCatalogGroups() {
        return catalogGroups;
    }

    public void setCatalogGroups(DiffQuery catalogGroups) {
        this.catalogGroups = catalogGroups;
    }
}
