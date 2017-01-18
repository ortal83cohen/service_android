package com.hpe.sb.mobile.app.common.dataClients.catalog;

import android.util.Log;

import com.hpe.sb.mobile.app.serverModel.catalog.CatalogGroupsByCategory;
import com.hpe.sb.mobile.app.serverModel.catalog.OfferingsByCatalogGroupResult;
import com.hpe.sb.mobile.app.serverModel.catalog.OfferingsByCategory;
import com.hpe.sb.mobile.app.serverModel.catalog.OfferingsByCatalogGroupResultDiffResult;
import com.hpe.sb.mobile.app.infra.dataClients.DataBlob;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by salemo on 22/05/2016.
 * Data Flow is the flow to get and update data on the mobile
 */
public class CatalogDataFlowClient {

    public Observable<OfferingsByCatalogGroupResult> getFromCacheSendDiffAndMergeMultipleItems(String tag,
                                                                                               Observable<OfferingsByCatalogGroupResult> fromDb,
                                                                                               Func1<OfferingsByCatalogGroupResult, Observable<OfferingsByCatalogGroupResultDiffResult>> sendRestFunction,
                                                                                               Func2<OfferingsByCatalogGroupResult, OfferingsByCatalogGroupResultDiffResult, OfferingsByCatalogGroupResult> offeringByCatalogGroupMergeFunction,
                                                                                               Action1<OfferingsByCatalogGroupResultDiffResult> updateDb,
                                                                                               Observable<DataBlob<OfferingsByCategory>> fromDbOfferingByCategory,
                                                                                               Observable<DataBlob<CatalogGroupsByCategory>> fromDbCatalogGroupByCategory) {
        return getFromCacheSendDiffAndMergeMultipleItems(tag, fromDb, sendRestFunction, offeringByCatalogGroupMergeFunction,
                updateDb, 0, TimeUnit.SECONDS, fromDbOfferingByCategory, fromDbCatalogGroupByCategory);
    }

    public Observable<OfferingsByCatalogGroupResult> getFromCacheSendDiffAndMergeMultipleItems(final String tag, Observable<OfferingsByCatalogGroupResult> fromDb,
                                                                                               Func1<OfferingsByCatalogGroupResult, Observable<OfferingsByCatalogGroupResultDiffResult>> sendRestFunction,
                                                                                               Func2<OfferingsByCatalogGroupResult, OfferingsByCatalogGroupResultDiffResult, OfferingsByCatalogGroupResult> mergeFunction,
                                                                                               Action1<OfferingsByCatalogGroupResultDiffResult> updateDb, final int dbMaxAge,
                                                                                               final TimeUnit timeUnit,
                                                                                               Observable<DataBlob<OfferingsByCategory>> fromDbOfferingByCategory,
                                                                                               Observable<DataBlob<CatalogGroupsByCategory>> fromDbCatalogGroupByCategory) {
        Log.d(tag, "Creating get from cache, send diff and merge request");
        //run observable function to get from db on computation thread
        final Observable<OfferingsByCatalogGroupResult> fromDbCalcOnce = fromDb
                .subscribeOn(Schedulers.computation())
                .cache();
        Log.d(getClass().getName(), "create rest filter to determine if a rest is needed");
        final Observable<Boolean> restFilter = Observable.zip(fromDbOfferingByCategory, fromDbCatalogGroupByCategory,
                new Func2<DataBlob<OfferingsByCategory>, DataBlob<CatalogGroupsByCategory>, Boolean>() {
                    @Override
                    public Boolean call(DataBlob<OfferingsByCategory> offeringsByCategoryDataBlob, DataBlob<CatalogGroupsByCategory> catalogGroupsByCategoryDataBlob) {
                        if (offeringsByCategoryDataBlob != null && catalogGroupsByCategoryDataBlob != null) {
                            long lastTimeFetched = getLastTimeFetched(offeringsByCategoryDataBlob, catalogGroupsByCategoryDataBlob);
                            long timeNow = System.currentTimeMillis();
                            return timeNow - lastTimeFetched > TimeUnit.MILLISECONDS.convert(dbMaxAge, timeUnit);
                        } else {
                            return true;
                        }
                    }
                });

        Log.d(getClass().getName(), "create rest Observable that is calculated once if a rest is needed");
        Observable<OfferingsByCatalogGroupResultDiffResult> fromRestCalcOnce = restFilter.filter(new Func1<Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean isOutdated) {
                return isOutdated;
            }
        }).flatMap(new Func1<Boolean, Observable<OfferingsByCatalogGroupResult>>() {
            @Override
            public Observable<OfferingsByCatalogGroupResult> call(Boolean aBoolean) {
                return fromDbCalcOnce;
            }
        }).subscribeOn(Schedulers.io())
                .flatMap(sendRestFunction)
                .doOnNext(updateDb)
                .cache();
        Log.d(getClass().getName(), "merge data from db with data from rest using given merge function");
        Observable<OfferingsByCatalogGroupResult> mergedCalcOnce = Observable.zip(fromDbCalcOnce, fromRestCalcOnce, mergeFunction)
                .cache();

        Log.d(tag, "Creation of get from cache, send diff and merge request done");
        return Observable.concatEager(fromDbCalcOnce, mergedCalcOnce).distinct();


    }

    private long getLastTimeFetched(DataBlob<OfferingsByCategory> offeringsByCategoryDataBlob, DataBlob<CatalogGroupsByCategory> catalogGroupsByCategoryDataBlob) {
        long offeringsLastFetched = offeringsByCategoryDataBlob.getLastFetched();
        long catalogGroupsLastFetched = catalogGroupsByCategoryDataBlob.getLastFetched();
        Log.d(getClass().getName(), "offeringsLastFetched : " + offeringsLastFetched + " and catalogGroupsLastFetched: " + catalogGroupsLastFetched);
        return offeringsLastFetched < catalogGroupsLastFetched ?
                offeringsLastFetched :
                catalogGroupsLastFetched;
    }
}
