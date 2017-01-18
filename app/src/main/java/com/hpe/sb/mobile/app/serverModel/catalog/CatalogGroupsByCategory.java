package com.hpe.sb.mobile.app.serverModel.catalog;

import com.hpe.sb.mobile.app.common.dataClients.catalog.CatalogClient;
import com.hpe.sb.mobile.app.infra.dataClients.Persistable;

import java.util.List;

/**
 * Created by salemo on 18/05/2016.
 *
 */
public class CatalogGroupsByCategory implements Persistable {
    List<String> catalogGroupsIds;
    String CategoryId;

    public CatalogGroupsByCategory(List<String> catalogGroupsIds, String categoryId) {
        this.catalogGroupsIds = catalogGroupsIds;
        CategoryId = categoryId;
    }

    public List<String> getCatalogGroupsIds() {
        return catalogGroupsIds;
    }

    public void setCatalogGroupsIds(List<String> catalogGroupsIds) {
        this.catalogGroupsIds = catalogGroupsIds;
    }

    @Override
    public String getId() {
        return CatalogClient.CATALOG_GROUP_BY_CATEGORY_DB_PREFIX + CategoryId;
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

        CatalogGroupsByCategory that = (CatalogGroupsByCategory) o;

        if (catalogGroupsIds != null ? !catalogGroupsIds.equals(that.catalogGroupsIds) : that.catalogGroupsIds != null) {
            return false;
        }
        return CategoryId != null ? CategoryId.equals(that.CategoryId) : that.CategoryId == null;

    }

    @Override
    public int hashCode() {
        int result = catalogGroupsIds != null ? catalogGroupsIds.hashCode() : 0;
        result = 31 * result + (CategoryId != null ? CategoryId.hashCode() : 0);
        return result;
    }
}
