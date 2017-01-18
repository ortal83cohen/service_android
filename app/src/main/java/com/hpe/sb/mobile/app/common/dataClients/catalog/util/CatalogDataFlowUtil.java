package com.hpe.sb.mobile.app.common.dataClients.catalog.util;

import android.content.Context;

import com.hpe.sb.mobile.app.infra.dataClients.ListDbClient;
import com.hpe.sb.mobile.app.serverModel.catalog.CatalogGroup;
import com.hpe.sb.mobile.app.serverModel.catalog.CatalogGroupsByCategory;
import com.hpe.sb.mobile.app.serverModel.catalog.Offering;
import com.hpe.sb.mobile.app.serverModel.catalog.OfferingsByCategory;
import com.hpe.sb.mobile.app.common.dataClients.catalog.restClient.CatalogRestClient;
import com.hpe.sb.mobile.app.serverModel.catalog.OfferingsByCatalogGroupResult;
import com.hpe.sb.mobile.app.serverModel.catalog.OfferingsByCatalogGroupResultDiffResult;
import com.hpe.sb.mobile.app.serverModel.diff.DiffResult;
import com.hpe.sb.mobile.app.infra.dataClients.DataBlob;
import com.hpe.sb.mobile.app.common.utils.ClientDataFlowUtils;
import com.hpe.sb.mobile.app.common.utils.MergeDataUtils;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by salemo on 22/05/2016.
 * util to help catalog client retrieve functions relevant to Data Flow.
 * Data Flow is the flow to get and update data on the mobile
 */
public class CatalogDataFlowUtil {

    public static Func1<OfferingsByCatalogGroupResult, Observable<OfferingsByCatalogGroupResultDiffResult>> getSendRestFunc
            (final Context context, final int offset, final int numberOfOfferingsToReturns,
             final String catalogGroupId, final int level, final CatalogRestClient catalogRestClient) {
        return new Func1<OfferingsByCatalogGroupResult, Observable<OfferingsByCatalogGroupResultDiffResult>>() {
            @Override
            public Observable<OfferingsByCatalogGroupResultDiffResult> call(OfferingsByCatalogGroupResult categories) {
                return categories != null ? catalogRestClient.getOfferingsByCatalogGroup(context, offset, numberOfOfferingsToReturns, catalogGroupId, level, categories) : null;
            }
        };
    }

    public static Func2<OfferingsByCatalogGroupResult, OfferingsByCatalogGroupResultDiffResult, OfferingsByCatalogGroupResult> getOfferingByCatalogGroupMergeFunction(final String tag) {
        return new Func2<OfferingsByCatalogGroupResult, OfferingsByCatalogGroupResultDiffResult, OfferingsByCatalogGroupResult>() {
            @Override
            public OfferingsByCatalogGroupResult call(OfferingsByCatalogGroupResult offeringsByCatalogGroupResult, OfferingsByCatalogGroupResultDiffResult offeringsByCatalogGroupResultDiffResult) {
                List<Offering> mergeOfferings = getMergedOfferings(tag, offeringsByCatalogGroupResult.getOfferings(), offeringsByCatalogGroupResultDiffResult.getOfferings());
                List<CatalogGroup> mergeCatalogGroups = getMergedCatalogGroups(tag, offeringsByCatalogGroupResult.getMgParentCatalogGroups(), offeringsByCatalogGroupResultDiffResult.getCatalogGroups());
                return new OfferingsByCatalogGroupResult(mergeOfferings, mergeCatalogGroups);
            }
        };
    }

    public static Observable<List<Offering>> getListOfOfferingsObservable(Observable<DataBlob<OfferingsByCategory>> fromDbOfferingByCategory,
                                                                          ListDbClient<Offering> offeringsDbClient) {
        Func1<DataBlob<OfferingsByCategory>, OfferingsByCategory> offeringsByCategoryBlobToDataFunction = ClientDataFlowUtils.singleItemBlobToDataFunction();
        Func1<List<DataBlob<Offering>>, List<Offering>> offeringListBlobToDataFunction = ClientDataFlowUtils.listBlobToDataFunction();
        return fromDbOfferingByCategory.map(offeringsByCategoryBlobToDataFunction)
                .cache()
                .flatMap(getOfferingListFromIdsFunc(offeringsDbClient))
                .map(offeringListBlobToDataFunction);
    }

    private static Func1<OfferingsByCategory, Observable<List<DataBlob<Offering>>>> getOfferingListFromIdsFunc(final ListDbClient<Offering> offeringsDbClient) {
        return new Func1<OfferingsByCategory, Observable<List<DataBlob<Offering>>>>() {
            @Override
            public Observable<List<DataBlob<Offering>>> call(OfferingsByCategory offeringByCategory) {
                List<DataBlob<Offering>> emptyOfferingList = new ArrayList<>();
                Observable<List<DataBlob<Offering>>> dummyOfferingListObservable = Observable.just(emptyOfferingList);
                return offeringByCategory != null && offeringByCategory.getOfferingsIds() != null ?
                        offeringsDbClient.getAllByIds(offeringByCategory.getOfferingsIds()) :
                        dummyOfferingListObservable;
            }
        };
    }

    public static Action1<OfferingsByCatalogGroupResultDiffResult> getActionToUpdateDbOfferings(final ListDbClient<Offering> offeringsDbClient,
                                                                                                final ListDbClient<CatalogGroup> catalogGroupsDbClient,
                                                                                                final ListDbClient<OfferingsByCategory> offeringsByCategoryDbClient,
                                                                                                final ListDbClient<CatalogGroupsByCategory> catalogGroupsByCategoryDbClient,
                                                                                                final String catalogGroupId) {
        return new Action1<OfferingsByCatalogGroupResultDiffResult>() {
            @Override
            public void call(OfferingsByCatalogGroupResultDiffResult diff) {
                if (diff != null) {
                    offeringsByCategoryDbClient.update(new OfferingsByCategory(diff.getOfferings().getOrderedIds(), catalogGroupId));
                    catalogGroupsByCategoryDbClient.update(new CatalogGroupsByCategory(diff.getCatalogGroups().getOrderedIds(), catalogGroupId));
                    catalogGroupsDbClient.update(diff.getCatalogGroups().getAddedOrUpdated(), diff.getCatalogGroups().getDeletedIds(), diff.getCatalogGroups().getOrderedIds());
                    offeringsDbClient.update(diff.getOfferings().getAddedOrUpdated(), diff.getOfferings().getDeletedIds(), diff.getOfferings().getOrderedIds());
                }
            }
        };
    }

    private static List<CatalogGroup> getMergedCatalogGroups(String tag, List<CatalogGroup> mgParentCatalogGroupsFromDb,
                                                             DiffResult<CatalogGroup> catalogGroupsFromServer) {
        return MergeDataUtils.merge(tag, catalogGroupsFromServer.getOrderedIds(), catalogGroupsFromServer.getAddedOrUpdated(),
                mgParentCatalogGroupsFromDb);
    }

    private static List<Offering> getMergedOfferings(String tag, List<Offering> offeringsByCatalogGroupResultFromDb,
                                                     DiffResult<Offering> offeringsByCatalogGroupResultDiffResultFromServer) {
        return MergeDataUtils.merge(tag, offeringsByCatalogGroupResultDiffResultFromServer.getOrderedIds(),
                offeringsByCatalogGroupResultDiffResultFromServer.getAddedOrUpdated(),
                offeringsByCatalogGroupResultFromDb);
    }

    public static Observable<List<CatalogGroup>> getListOfCatalogGroupsObservable
            (Observable<DataBlob<CatalogGroupsByCategory>> fromDbCatalogGroupByCategory, ListDbClient<CatalogGroup> catalogGroupsDbClient) {
        Func1<DataBlob<CatalogGroupsByCategory>, CatalogGroupsByCategory> catalogGroupsByCategoryBlobToDataFunction = ClientDataFlowUtils.singleItemBlobToDataFunction();
        Func1<List<DataBlob<CatalogGroup>>, List<CatalogGroup>> MGCatalogGroupListBlobToDataFunction = ClientDataFlowUtils.listBlobToDataFunction();
        return fromDbCatalogGroupByCategory.map(catalogGroupsByCategoryBlobToDataFunction)
                .cache()
                .flatMap(getCatalogGroupListFromIdsFunc(catalogGroupsDbClient))
                .map(MGCatalogGroupListBlobToDataFunction);
    }

    private static Func1<CatalogGroupsByCategory, Observable<List<DataBlob<CatalogGroup>>>> getCatalogGroupListFromIdsFunc
            (final ListDbClient<CatalogGroup> catalogGroupsDbClient) {
        return new Func1<CatalogGroupsByCategory, Observable<List<DataBlob<CatalogGroup>>>>() {
            @Override
            public Observable<List<DataBlob<CatalogGroup>>> call(CatalogGroupsByCategory catalogGroupByCategory) {
                List<DataBlob<CatalogGroup>> emptyCatalogGroupList = new ArrayList<>();
                Observable<List<DataBlob<CatalogGroup>>> dummyCatalogGroupListObservable = Observable.just(emptyCatalogGroupList);
                return catalogGroupByCategory != null && catalogGroupByCategory.getCatalogGroupsIds() != null ?
                        catalogGroupsDbClient.getAllByIds(catalogGroupByCategory.getCatalogGroupsIds()) :
                        dummyCatalogGroupListObservable;
            }
        };
    }

    public static Observable<OfferingsByCatalogGroupResult> getMGOfferingsByCatalogGroupResultFromDbData(Observable<List<Offering>> offeringListFromDb, Observable<List<CatalogGroup>> catalogGroupListFromDb) {
        return Observable.zip(offeringListFromDb, catalogGroupListFromDb, new Func2<List<Offering>, List<CatalogGroup>, OfferingsByCatalogGroupResult>() {

            @Override
            public OfferingsByCatalogGroupResult call(List<Offering> offerings, List<CatalogGroup> catalogGroups) {
                List<Offering> offeringsToAdd = offerings != null ? offerings : new ArrayList<Offering>();
                List<CatalogGroup> catalogGroupsToAdd = catalogGroups != null ? catalogGroups : new ArrayList<CatalogGroup>();
                return new OfferingsByCatalogGroupResult(offeringsToAdd, catalogGroupsToAdd);
            }
        });
    }
}
