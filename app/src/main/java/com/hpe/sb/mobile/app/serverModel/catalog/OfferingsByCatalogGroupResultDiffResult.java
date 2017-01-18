package com.hpe.sb.mobile.app.serverModel.catalog;

import com.hpe.sb.mobile.app.serverModel.diff.DiffResult;

/**
 * Created by salemo on 17/05/2016.
 *
 */
public class OfferingsByCatalogGroupResultDiffResult {

    private DiffResult<Offering> offerings;

    private DiffResult<CatalogGroup> catalogGroups;

    public OfferingsByCatalogGroupResultDiffResult(DiffResult<Offering> offerings, DiffResult<CatalogGroup> catalogGroups) {
        this.offerings = offerings;
        this.catalogGroups = catalogGroups;
    }

    public DiffResult<Offering> getOfferings() {
        return offerings;
    }

    public void setOfferings(DiffResult<Offering> offerings) {
        this.offerings = offerings;
    }

    public DiffResult<CatalogGroup> getCatalogGroups() {
        return catalogGroups;
    }

    public void setCatalogGroups(DiffResult<CatalogGroup> catalogGroups) {
        this.catalogGroups = catalogGroups;
    }
}
