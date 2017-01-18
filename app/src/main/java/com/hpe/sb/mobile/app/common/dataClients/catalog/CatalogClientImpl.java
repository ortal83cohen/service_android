package com.hpe.sb.mobile.app.common.dataClients.catalog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import com.hpe.sb.mobile.app.common.dataClients.catalog.restClient.CatalogRestClient;
import com.hpe.sb.mobile.app.common.dataClients.catalog.util.CatalogDataFlowUtil;
import com.hpe.sb.mobile.app.common.uiComponents.categories.adapter.ColorParsingUtil;
import com.hpe.sb.mobile.app.infra.dataClients.ListDbClient;
import com.hpe.sb.mobile.app.serverModel.catalog.*;
import com.hpe.sb.mobile.app.serverModel.diff.DiffResult;
import com.hpe.sb.mobile.app.infra.dataClients.DataBlob;
import com.hpe.sb.mobile.app.common.utils.ClientDataFlowUtils;
import com.hpe.sb.mobile.app.common.utils.MergeDataUtils;
import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class CatalogClientImpl implements CatalogClient {

    private static String[] defaultColors = new String[]{
            "#718ea6",
            "#8c6694",
            "#4f4f4f",
            "#dc882f"
    };

   public static final int DB_MAX_AGE = 30;
    private final ListDbClient<Offering> offeringsDbClient;
    private final CatalogDataFlowClient catalogDataFlowClient;
    private ListDbClient<CatalogGroup> catalogGroupsDbClient;
    private final ListDbClient<OfferingsByCategory> offeringsByCategoryDbClient;
    private final ListDbClient<CatalogGroupsByCategory> catalogGroupsByCategoryDbClient;
    private CatalogRestClient catalogRestClient;

    private Random random = new Random();

    private ColorParsingUtil colorParsingUtil = new ColorParsingUtil();

    private TimedCache defaultOfferingCache = new TimedCache();


    public CatalogClientImpl(CatalogRestClient catalogRestClient, ListDbClient<CatalogGroup> catalogGroupsDbClient,
                             ListDbClient<Offering> offeringsDbClient, ListDbClient<OfferingsByCategory> offeringsByCategoryDbClient,
                             ListDbClient<CatalogGroupsByCategory> catalogGroupsByCategoryDbClient, CatalogDataFlowClient catalogDataFlowClient) {
        this.catalogGroupsDbClient = catalogGroupsDbClient;
        this.offeringsDbClient = offeringsDbClient;
        this.catalogRestClient = catalogRestClient;
        this.offeringsByCategoryDbClient = offeringsByCategoryDbClient;
        this.catalogGroupsByCategoryDbClient = catalogGroupsByCategoryDbClient;
        this.catalogDataFlowClient = catalogDataFlowClient;
    }

    public Observable<List<CatalogGroup>> getCategories(final Context context, boolean forceRefresh) {
        // TODO (HAIM): Separate categories and services to different objects
        Observable<List<DataBlob<CatalogGroup>>> fromDb = catalogGroupsDbClient.getAllByType().map(new Func1<List<DataBlob<CatalogGroup>>, List<DataBlob<CatalogGroup>>>() {
            @Override
            public List<DataBlob<CatalogGroup>> call(List<DataBlob<CatalogGroup>> catalogGroups) {
                List<DataBlob<CatalogGroup>> result = new ArrayList<>();
                for (DataBlob<CatalogGroup> catalogGroup : catalogGroups) {
                    if (catalogGroup.getData().isRoot()) {
                        result.add(catalogGroup);
                    }
                }
                return result;
            }
        });
        Func1<List<CatalogGroup>, Observable<DiffResult<CatalogGroup>>> sendRest = new Func1<List<CatalogGroup>, Observable<DiffResult<CatalogGroup>>>() {
            @Override
            public Observable<DiffResult<CatalogGroup>> call(List<CatalogGroup> categories) {
                return catalogRestClient.getCategories(context, categories);
            }
        };
        Action1<DiffResult<CatalogGroup>> updateDb = new Action1<DiffResult<CatalogGroup>>() {
            @Override
            public void call(DiffResult<CatalogGroup> diff) {
                catalogGroupsDbClient.update(diff.getAddedOrUpdated(), diff.getDeletedIds(), diff.getOrderedIds());
            }
        };
        if (forceRefresh) {
            return ClientDataFlowUtils.getFromCacheSendDiffAndMerge(GET_CATEGORIES_TAG,
                    fromDb,
                    sendRest,
                    MergeDataUtils.createMergeFunction(CatalogClient.GET_CATEGORIES_TAG, CatalogGroup.class),
                    updateDb).map(postProcessCategoriesFunction());
        } else {
            return ClientDataFlowUtils.getFromCacheSendDiffAndMerge(GET_CATEGORIES_TAG,
                    fromDb,
                    sendRest,
                    MergeDataUtils.createMergeFunction(CatalogClient.GET_CATEGORIES_TAG, CatalogGroup.class),
                    updateDb, 30, TimeUnit.MINUTES).map(postProcessCategoriesFunction());
        }
    }

    public Observable<OfferingsByCatalogGroupResult> getOfferingsByCatalogGroup(final Context context,
                                                                                boolean forceRefresh, final int offset,
                                                                                final int numberOfOfferingsToReturns,
                                                                                final String catalogGroupId, final int level) {
        Log.i(getClass().getName(), "getOfferingsByCatalogGroup start for category id: " + catalogGroupId);
        Log.d(getClass().getName(), "get the list of offering Observable");
        Observable<DataBlob<OfferingsByCategory>> fromDbOfferingByCategory = offeringsByCategoryDbClient.getItemById(OFFERING_BY_CATEGORY_DB_PREFIX + catalogGroupId);
        Observable<List<Offering>> offeringListFromDb = CatalogDataFlowUtil.getListOfOfferingsObservable(fromDbOfferingByCategory, offeringsDbClient);

        Log.d(getClass().getName(), "get the list of CatalogGroup Observable");
        Observable<DataBlob<CatalogGroupsByCategory>> fromDbCatalogGroupByCategory = catalogGroupsByCategoryDbClient.getItemById(CATALOG_GROUP_BY_CATEGORY_DB_PREFIX + catalogGroupId);
        Observable<List<CatalogGroup>> catalogGroupListFromDb = CatalogDataFlowUtil.getListOfCatalogGroupsObservable(fromDbCatalogGroupByCategory, catalogGroupsDbClient);

        Log.d(getClass().getName(), "zip all the data from DB to one OfferingsByCatalogGroupResult");
        Observable<OfferingsByCatalogGroupResult> fromDb = CatalogDataFlowUtil.getMGOfferingsByCatalogGroupResultFromDbData(offeringListFromDb, catalogGroupListFromDb);

        Func1<OfferingsByCatalogGroupResult, Observable<OfferingsByCatalogGroupResultDiffResult>> sendRestFunction = CatalogDataFlowUtil.getSendRestFunc(context, offset,
                numberOfOfferingsToReturns, catalogGroupId, level, catalogRestClient);

        Log.d(getClass().getName(), "get all DB updates");
        Action1<OfferingsByCatalogGroupResultDiffResult> updateDb = CatalogDataFlowUtil.getActionToUpdateDbOfferings(offeringsDbClient, catalogGroupsDbClient,
                offeringsByCategoryDbClient, catalogGroupsByCategoryDbClient, catalogGroupId);

        if (forceRefresh) {
            Log.d(getClass().getName(), "got forceRefresh");
            return catalogDataFlowClient.getFromCacheSendDiffAndMergeMultipleItems(GET_OFFERINGS_BY_CATEGORY_TAG,
                    fromDb,
                    sendRestFunction,
                    CatalogDataFlowUtil.getOfferingByCatalogGroupMergeFunction(getClass().getName() + " --> CatalogDataFlowUtil"),
                    updateDb,
                    fromDbOfferingByCategory, fromDbCatalogGroupByCategory);
        } else {
            return catalogDataFlowClient.getFromCacheSendDiffAndMergeMultipleItems(GET_OFFERINGS_BY_CATEGORY_TAG,
                    fromDb,
                    sendRestFunction,
                    CatalogDataFlowUtil.getOfferingByCatalogGroupMergeFunction(getClass().getName() + " --> CatalogDataFlowUtil"),
                    updateDb, DB_MAX_AGE, TimeUnit.MINUTES,
                    fromDbOfferingByCategory, fromDbCatalogGroupByCategory);
        }
    }

    @NonNull
    private Func1<List<CatalogGroup>, List<CatalogGroup>> postProcessCategoriesFunction() {
        return new Func1<List<CatalogGroup>, List<CatalogGroup>>() {
            @Override
            public List<CatalogGroup> call(List<CatalogGroup> catalogGroups) {
                for(CatalogGroup catalogGroup : catalogGroups) {
                    if(catalogGroup.getBackgroundColor() == null) {
                        setRandomColor(catalogGroup);
                    } else {
                        String colorStr = colorParsingUtil.convertColorToHexIfNeeded(catalogGroup.getBackgroundColor());
                        if(colorStr == null){
                            setRandomColor(catalogGroup);
                        }else {
                            catalogGroup.setBackgroundColor(colorStr);
                        }
                    }
                }

                return catalogGroups;
            }
        };
    }

    private void setRandomColor(CatalogGroup catalogGroup) {
        final int randomColorIndex = random.nextInt(defaultColors.length - 1);
        catalogGroup.setBackgroundColor(defaultColors[randomColorIndex]);
    }

    @Override
    public Observable<Offering> getDefaultOffering(final Context context) {
        if(defaultOfferingCache.isRelevant()) {
            return Observable.just(defaultOfferingCache.get());
        } else {
            return catalogRestClient.getDefaultOffering(context)
                    .doOnNext(new Action1<Offering>() {
                        @Override
                        public void call(Offering offering) {
                            defaultOfferingCache.set(offering);
                        }
                    });
        }
    }

    private class TimedCache {
        private static final long MAX_TIME_IN_CACHE = 30 * 60 * 1000; // min in millis

        private long updatedTime;
        private Offering data;

        public boolean isRelevant() {
            long timeInCache = System.currentTimeMillis() - updatedTime;
            if(timeInCache > MAX_TIME_IN_CACHE) {
                data = null;
                return false;
            }
            return true;
        }

        public Offering get() {
            return data;
        }

        public void set(Offering data) {
            this.data = data;
            updatedTime = System.currentTimeMillis();
        }
    }
}
