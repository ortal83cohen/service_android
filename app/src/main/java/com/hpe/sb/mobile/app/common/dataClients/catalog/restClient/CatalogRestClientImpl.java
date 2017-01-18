package com.hpe.sb.mobile.app.common.dataClients.catalog.restClient;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.hpe.sb.mobile.app.common.dataClients.catalog.CatalogClient;
import com.hpe.sb.mobile.app.serverModel.catalog.CatalogGroup;
import com.hpe.sb.mobile.app.serverModel.catalog.CatalogGroupDiffResult;
import com.hpe.sb.mobile.app.serverModel.catalog.Offering;
import com.hpe.sb.mobile.app.serverModel.catalog.OfferingsByCatalogGroupResult;
import com.hpe.sb.mobile.app.serverModel.catalog.OfferingsByCatalogGroupResultDiffQuery;
import com.hpe.sb.mobile.app.serverModel.catalog.OfferingsByCatalogGroupResultDiffResult;
import com.hpe.sb.mobile.app.serverModel.diff.DiffQuery;
import com.hpe.sb.mobile.app.serverModel.diff.DiffResult;
import com.hpe.sb.mobile.app.infra.restclient.RestService;
import com.hpe.sb.mobile.app.common.utils.DiffQueryUtils;

import rx.Observable;
import rx.functions.Func1;

import java.util.List;

public class CatalogRestClientImpl implements CatalogRestClient {

    private final RestService restService;

    public CatalogRestClientImpl(RestService restService) {
        this.restService = restService;
    }

    private String getPathPrefix() {
        return "catalog/";
    }

    @Override
    public Observable<OfferingsByCatalogGroupResultDiffResult> getOfferingsByCatalogGroup(Context context, int offset, int numberOfOfferingsToReturns, String catalogGroupId, int level,
                                                                                          OfferingsByCatalogGroupResult dbOfferingsByCatalogGroupResult) {
        Log.d(CatalogClient.GET_OFFERINGS_BY_CATEGORY_TAG, "Creating rest request to get offerings by category, category id:" + catalogGroupId);
        DiffQuery diffQueryMgOfferings = DiffQueryUtils.createDiffQuery(dbOfferingsByCatalogGroupResult.getOfferings());
        DiffQuery diffQueryCatalogGroups = DiffQueryUtils.createDiffQuery(dbOfferingsByCatalogGroupResult.getMgParentCatalogGroups());
        OfferingsByCatalogGroupResultDiffQuery offeringsByCatalogGroupResultDiffQuery = new OfferingsByCatalogGroupResultDiffQuery(diffQueryMgOfferings, diffQueryCatalogGroups);

        Uri.Builder builder = new Uri.Builder();
        builder.appendQueryParameter("offset", String.valueOf(offset));
        builder.appendQueryParameter("numberOfOfferingsToReturn", String.valueOf(numberOfOfferingsToReturns));
        builder.appendQueryParameter("sortBy", "popularity");
        builder.appendQueryParameter("level", String.valueOf(level));
        String url = getPathPrefix() + "catalog-group/" + catalogGroupId + "/offering-list/" + builder.build();
        return restService.createPostRequest(url, offeringsByCatalogGroupResultDiffQuery, OfferingsByCatalogGroupResultDiffResult.class, context)
                .map(new Func1<OfferingsByCatalogGroupResultDiffResult, OfferingsByCatalogGroupResultDiffResult>() {
                    @Override
                    public OfferingsByCatalogGroupResultDiffResult call(OfferingsByCatalogGroupResultDiffResult diff) {
                        DiffResult<Offering> offerings = diff.getOfferings();
                        Log.d(CatalogClient.GET_OFFERINGS_BY_CATEGORY_TAG, "for offerings Rest returned with " + offerings.getAddedOrUpdated().size() +
                                " updated offerings and " + offerings.getDeletedIds().size() + " deleted.");
                        DiffResult<CatalogGroup> catalogGroups = diff.getCatalogGroups();
                        Log.d(CatalogClient.GET_OFFERINGS_BY_CATEGORY_TAG, "for Catalog Groups Rest returned with " + catalogGroups.getAddedOrUpdated() +
                                " updated getCatalogGroups and " + catalogGroups.getDeletedIds() + " deleted.");
                        return new OfferingsByCatalogGroupResultDiffResult(offerings, catalogGroups);
                    }
                });
    }

    @Override
    public Observable<Offering> getOffering(Context context, String offeringId) {
        String url = getPathPrefix() + "offering/" + offeringId;
        return restService.createGetRequest(url, Offering.class, context);
    }

    @Override
    public Observable<Offering> getDefaultOffering(Context context) {
        String url = getPathPrefix() + "defaultOffering";
        return restService.createGetRequest(url, Offering.class, context);
    }

    @Override
    public Observable<DiffResult<CatalogGroup>> getCategories(Context context, List<CatalogGroup> categories) {
        Log.d(CatalogClient.GET_CATEGORIES_TAG, "Creating rest request with " + categories.size() + " from db");
        DiffQuery diffQuery = DiffQueryUtils.createDiffQuery(categories);

        return restService.createPostRequest(getPathPrefix() + "category-list", diffQuery, CatalogGroupDiffResult.class, context)
        .map(new Func1<CatalogGroupDiffResult, DiffResult<CatalogGroup>>() {
            @Override
            public DiffResult<CatalogGroup> call(CatalogGroupDiffResult diff) {
                Log.d(CatalogClient.GET_CATEGORIES_TAG, "Rest returned with " + diff.getAddedOrUpdated().size() +
                        " updated categories and " + diff.getDeletedIds().size() + " deleted.");
                return new DiffResult<>(diff.getAddedOrUpdated(), diff.getDeletedIds(), diff.getOrderedIds());
            }
        });
    }

}

