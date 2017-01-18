package com.hpe.sb.mobile.app.common.dataClients.catalog;


import android.content.ContentResolver;

import com.hpe.sb.mobile.app.common.dataClients.catalog.restClient.CatalogRestClient;
import com.hpe.sb.mobile.app.common.dataClients.catalog.restClient.CatalogRestClientImpl;
import com.hpe.sb.mobile.app.infra.db.DbHelper;
import com.hpe.sb.mobile.app.infra.dataClients.ListDbClient;
import com.hpe.sb.mobile.app.serverModel.catalog.CatalogGroup;
import com.hpe.sb.mobile.app.serverModel.catalog.CatalogGroupsByCategory;
import com.hpe.sb.mobile.app.serverModel.catalog.Offering;
import com.hpe.sb.mobile.app.serverModel.catalog.OfferingsByCategory;
import com.hpe.sb.mobile.app.infra.db.GeneralBlobModel;
import com.hpe.sb.mobile.app.infra.restclient.RestService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class CatalogDataModule {

    @Singleton
    @Provides
    public CatalogClient provideCatalogClient(ListDbClient<CatalogGroup> catalogDbClient, CatalogRestClient restClient,
                                              ListDbClient<Offering> offeringsDbClient, ListDbClient<OfferingsByCategory> offeringsByCategoryDbClient,
                                              ListDbClient<CatalogGroupsByCategory> catalogGroupsByCategoryDbClient,
                                              CatalogDataFlowClient CatalogDataFlowClient) {
        return new CatalogClientImpl(restClient, catalogDbClient, offeringsDbClient, offeringsByCategoryDbClient,
                catalogGroupsByCategoryDbClient, CatalogDataFlowClient);
    }

    @Singleton
    @Provides
    public CatalogRestClient provideCatalogRestClient(RestService restService) {
        return new CatalogRestClientImpl(restService);
    }

    @Singleton
    @Provides
    public ListDbClient<CatalogGroup> provideCatalogDbClient(
            ContentResolver contentResolver, DbHelper dbHelper) {
        return new ListDbClient<>(CatalogGroup.class, contentResolver,
                CatalogClient.CATEGORIES_DB_CLIENT, GeneralBlobModel.TYPE_CATEGORIES,
                dbHelper);
    }

    @Singleton
    @Provides
    public CatalogDataFlowClient provideCatalogDataDbClient() {
        return new CatalogDataFlowClient();
    }

    @Singleton
    @Provides
    public ListDbClient<Offering> provideOfferingsDbClient(
            ContentResolver contentResolver, DbHelper dbHelper) {
        return new ListDbClient<>(Offering.class, contentResolver,
                CatalogClient.OFFERINGS_DB_CLIENT, GeneralBlobModel.TYPE_OFFERINGS, dbHelper);
    }

    @Singleton
    @Provides
    public ListDbClient<OfferingsByCategory> provideOfferingsByCategoryDbClient(
            ContentResolver contentResolver, DbHelper dbHelper) {
        return new ListDbClient<>(OfferingsByCategory.class, contentResolver,
                CatalogClient.OFFERING_BY_CATEGORY_DB_CLIENT, GeneralBlobModel.TYPE_OFFERING_BY_CATEGORY,
                dbHelper);
    }

    @Singleton
    @Provides
    public ListDbClient<CatalogGroupsByCategory> provideCatalogGroupsByCategoryDbClient(
            ContentResolver contentResolver, DbHelper dbHelper) {
        return new ListDbClient<>(CatalogGroupsByCategory.class, contentResolver,
                CatalogClient.CATALOG_GROUP_BY_CATEGORY_DB_CLIENT,
                GeneralBlobModel.TYPE_CATALOG_GROUP_BY_CATEGORY, dbHelper);
    }

}
