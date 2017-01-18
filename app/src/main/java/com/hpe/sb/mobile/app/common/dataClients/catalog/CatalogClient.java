package com.hpe.sb.mobile.app.common.dataClients.catalog;

import android.content.Context;

import com.hpe.sb.mobile.app.serverModel.catalog.CatalogGroup;
import com.hpe.sb.mobile.app.serverModel.catalog.Offering;
import com.hpe.sb.mobile.app.serverModel.catalog.OfferingsByCatalogGroupResult;

import rx.Observable;

import java.util.List;

public interface CatalogClient {

    /**
     * use to get offerings by category
     * **/
    int CATALOG_GROUP_CATEGORY_LEVEL = 2;
    /**
     * use to get offerings by service
     * **/
    int CATALOG_GROUP_SEVICE_LEVEL = 1;
    int MAX_NUMBER_OF_OFFERINGS = 4000;
    String CATALOG_GROUP_BY_CATEGORY_DB_PREFIX = "catalog_group_by_category_";
    String OFFERING_BY_CATEGORY_DB_PREFIX = "offering_by_category_";
    String GET_CATEGORIES_TAG = "getCategories";
    String GET_OFFERINGS_BY_CATEGORY_TAG = "offerings_by_category";
    String CATEGORIES_DB_CLIENT = "categories_db_client";
    String OFFERING_BY_CATEGORY_DB_CLIENT = "offering_by_category_db_client";
    String CATALOG_GROUP_BY_CATEGORY_DB_CLIENT = "catalog_group_by_category_db_client";
    String OFFERINGS_DB_CLIENT = "offerings_db_client";
	String GET_DEFAULT_OFFERING_TAG = "getDefaultOffering";

    Observable<List<CatalogGroup>> getCategories(Context context, boolean forceRefresh);
	Observable<Offering> getDefaultOffering(Context context);
    Observable<OfferingsByCatalogGroupResult> getOfferingsByCatalogGroup(Context context, boolean forceRefresh, int offset, int numberOfOfferingsToReturns, String catalogGroupId, int level);

}
