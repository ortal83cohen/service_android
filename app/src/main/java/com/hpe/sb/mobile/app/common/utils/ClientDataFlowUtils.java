package com.hpe.sb.mobile.app.common.utils;

import android.support.annotation.NonNull;
import android.util.Log;

import com.hpe.sb.mobile.app.infra.dataClients.DataBlob;
import com.hpe.sb.mobile.app.infra.dataClients.Persistable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

public class ClientDataFlowUtils {


    /**
     * Creates an observable for the following flow:
     * <p/>
     * - Gets T from db and emits its value.
     * - Maps T to send to rest function, which returns R (usually diff object).
     * - Merges results from db.
     * - Update db with new value.
     * - Emits value of merge (completing emission of values).
     *
     * @param tag               - log tag to use
     * @param fromDb            - observable which emits a value from db
     * @param restAction        - function which maps a value from db to an observable from rest
     * @param mergeFunction     - merges results from db and rest
     * @param updateDb          - updates db with results from merge
     * @param dbMaxAge - will only call the rest function if at least one of the items in the least passed the max age.
     * @param timeUnit          - the time unit for dbMaxAge
     * @param <T>               - data type
     * @param <R>               - diff data type
     * @return Observable
     */
    public static <T extends Persistable, R> Observable<List<T>> getFromCacheSendDiffAndMerge(final String tag, Observable<List<DataBlob<T>>> fromDb, Func1<List<T>, Observable<R>> restAction, Func2<List<T>, R, List<T>> mergeFunction, Action1<R> updateDb, final long dbMaxAge, final TimeUnit timeUnit) {
        Log.d(tag, "Creating get from cache, send diff and merge request");
        //run observable function to get from db on computation thread
        Observable<List<DataBlob<T>>> fromDbCalcOnce = fromDb
                .subscribeOn(Schedulers.computation())
                .cache();

        //run observable function that runs REST command on io thread
        Observable<R> fromRestCalcOnce = fromDbCalcOnce
                .filter(new Func1<List<DataBlob<T>>, Boolean>() {
                    //filter results whose last time fetch is bigger than max age
                    @Override
                    public Boolean call(List<DataBlob<T>> blobList) {
                        if (blobList.isEmpty() || dbMaxAge == 0) {
                            //in cases where data from db is empty (could be on initialization)
                            // if max age is 0, always send to rest
                            return true;
                        }
                        //extract the minimal last time fetched of all the items
                        long lastTimeFetched = Long.MAX_VALUE;
                        for (DataBlob<T> blob : blobList) {
                            if (blob.getLastFetched() < lastTimeFetched) {
                                lastTimeFetched = blob.getLastFetched();
                            }
                        }

                        long timeNow = System.currentTimeMillis();
                        return timeNow - lastTimeFetched > TimeUnit.MILLISECONDS.convert(dbMaxAge, timeUnit);
                    }
                })
                .map(ClientDataFlowUtils.<T>listBlobToDataFunction())
                .subscribeOn(Schedulers.io())
                .flatMap(restAction)
                .doOnNext(updateDb)
                .cache();

        Observable<List<T>> getDataFromDbBlob = fromDbCalcOnce.map(ClientDataFlowUtils.<T>listBlobToDataFunction());
        //merge data from db with data from rest using given merge function
        Observable<List<T>> mergedCalcOnce = Observable.zip(getDataFromDbBlob, fromRestCalcOnce, mergeFunction)
                .cache();

        Log.d(tag, "Creation of get from cache, send diff and merge request done");
        return Observable.concatEager(getDataFromDbBlob, mergedCalcOnce).distinct();
    }

    /**
     * @param <T>
     * @return function which removes blob wrapper from data
     */
    public static <T extends Persistable> Func1<List<DataBlob<T>>, List<T>> listBlobToDataFunction() {
        return new Func1<List<DataBlob<T>>, List<T>>() {
            @Override
            public List<T> call(List<DataBlob<T>> blobList) {
                List<T> result = new ArrayList<>();
                for (DataBlob<T> blob : blobList) {
                    result.add(blob.getData());
                }
                return result;
            }
        };
    }

    //function to remove blob wrapper from data
    public static <T extends Persistable> Func1<DataBlob<T>, T> singleItemBlobToDataFunction() {
        return new Func1<DataBlob<T>, T>() {
            @Override
            public T call(DataBlob<T> blob) {
                if (blob == null) {
                    return null;
                }
                return blob.getData();
            }
        };
    }

    /**
     * A variation of getFromCacheSendDiffAndMerge with no max age, meaning that rest action will be forcefully invoked.
     */
    public static <T extends Persistable, R> Observable<List<T>> getFromCacheSendDiffAndMerge(final String tag, Observable<List<DataBlob<T>>> fromDb, Func1<List<T>, Observable<R>> restAction, Func2<List<T>, R, List<T>> mergeFunction, Action1<R> updateDb) {
        return getFromCacheSendDiffAndMerge(tag, fromDb, restAction, mergeFunction, updateDb, 0, TimeUnit.SECONDS);
    }


    /**
     * Creates an observable for the following flow:
     * <p/>
     * - Gets T from db and check if it's recent
     * - If T is outdated, get from REST and update db.
     *
     * @param tag               - log tag to use
     * @param fromDb            - observable which emits a value from db
     * @param fromRest          - observable which emits a value from rest
     * @param updateDb          - updates db with results from merge
     * @param dbMaxAge          - will only call the rest function if at least one of the items in the least passed the max age.
     * @param timeUnit          - the time unit for dbMaxAge
     * @param <T>               - data type
     * @return Observable
     */
    public static <T extends Persistable> Observable<T> getFromCacheAndFromRestIfOutdated(final String tag, Observable<DataBlob<T>> fromDb, final Observable<T> fromRest, final Func1<T, Observable<Void>> updateDb, final long dbMaxAge, final TimeUnit timeUnit) {
        Log.d(tag, "Creating get from db, and call rest if outdated");

        //function to remove blob wrapper from data
        Func1<DataBlob<T>, T> blobToDataFunction = new Func1<DataBlob<T>, T>() {
            @Override
            public T call(DataBlob<T> blob) {
                return blob != null ? blob.getData() : null;
            }
        };
        Observable<DataBlob<T>> fromDbCached = fromDb.cache();
        final Observable<T> fromRestCached = fromRest.cache();
        final Observable<T> fromRestAndUpdateDb = Observable.zip(fromRestCached, fromRestCached.flatMap(updateDb), new Func2<T, Void, T>() {
            @Override
            public T call(T t, Void aVoid) {
                return t;
            }
        });

        //run observable function that runs update db from rest
        Observable<T> fromRestIfOutdated = fromDbCached.filter(new Func1<DataBlob<T>, Boolean>() {
            @Override
            public Boolean call(DataBlob<T> blob) {
                return isOutdated(blob, dbMaxAge, timeUnit);
            }
        })
                .switchMap(new Func1<DataBlob<T>, Observable<? extends T>>() {
                    @Override
                    public Observable<? extends T> call(DataBlob<T> tDataBlob) {
                        return fromRestAndUpdateDb;
                    }
                }).doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(tag, "error when updating db", throwable);
                    }
                }).subscribeOn(Schedulers.io());

        Observable<T> fromDbUnwrapped = fromDbCached.map(blobToDataFunction)
                .subscribeOn(Schedulers.computation())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(tag, "Getting from db failed", throwable);
                    }
                })
                .onExceptionResumeNext(fromRestAndUpdateDb);

        Log.d(tag, "Creation of get from db, and call rest if outdated is done");
        return Observable.concatEager(fromDbUnwrapped, fromRestIfOutdated).filter(new Func1<T, Boolean>() {
            @Override
            public Boolean call(T t) {
                return t != null;
            }
        }).distinct();
    }

    @NonNull
    private static <T extends Persistable> Boolean isOutdated(DataBlob<T> blob, long dbMaxAge, TimeUnit timeUnit) {
        boolean isOutdated;
        if (blob == null) {
            //in cases where data from db is empty (could be on initialization)
            isOutdated = true;
        } else {
            long timeNow = System.currentTimeMillis();
            isOutdated = timeNow - blob.getLastFetched() > TimeUnit.MILLISECONDS.convert(dbMaxAge, timeUnit);
        }

        return isOutdated;
    }

    /**
     * A variation of getFromCacheSendDiffAndMerge with no max age, meaning that rest action will be forcefully invoked.
     */
    public static <T extends Persistable> Observable<T> getFromRestAndUpdateDb(final String tag, Observable<T> fromRest, Func1<T, Observable<Void>> updateDb) {
        fromRest = fromRest.cache();
        return Observable.zip(fromRest, fromRest.flatMap(updateDb), new Func2<T, Void, T>() {
            @Override
            public T call(T t, Void aVoid) {
                return t;
            }
        }).subscribeOn(Schedulers.io());
    }

}