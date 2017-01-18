package com.hpe.sb.mobile.app.serverModel.catalog;

import com.hpe.sb.mobile.app.common.dataClients.catalog.CatalogClient;
import com.hpe.sb.mobile.app.infra.dataClients.Persistable;

import java.util.List;

/**
 * Created by salemo on 17/05/2016.
 *
 */
public class OfferingsByCategory implements Persistable {
    List<String> offeringsIds;
    String CategoryId;

    public OfferingsByCategory(List<String> offeringsIds, String categoryId) {
        this.offeringsIds = offeringsIds;
        CategoryId = categoryId;
    }

    public List<String> getOfferingsIds() {
        return offeringsIds;
    }

    public void setOfferingsIds(List<String> offeringsIds) {
        this.offeringsIds = offeringsIds;
    }

    @Override
    public String getId() {
        return CatalogClient.OFFERING_BY_CATEGORY_DB_PREFIX + CategoryId;
    }

    @Override
    public int getChecksum() {
        return hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OfferingsByCategory that = (OfferingsByCategory) o;

        if (offeringsIds != null ? !offeringsIds.equals(that.offeringsIds) : that.offeringsIds != null) {
            return false;
        }
        return CategoryId != null ? CategoryId.equals(that.CategoryId) : that.CategoryId == null;

    }

    @Override
    public int hashCode() {
        int result = offeringsIds != null ? offeringsIds.hashCode() : 0;
        result = 31 * result + (CategoryId != null ? CategoryId.hashCode() : 0);
        return result;
    }
}
