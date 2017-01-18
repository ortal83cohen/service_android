package com.hpe.sb.mobile.app.infra.dataClients;

import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.os.RemoteException;
import android.util.Log;

import com.hpe.sb.mobile.app.infra.db.DbHelper;
import com.hpe.sb.mobile.app.infra.db.DbContract;
import com.hpe.sb.mobile.app.infra.db.GeneralBlobModel;

import rx.Observable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * This class exposes database access for data types which can have one instance or more.
 */
public class ListDbClient<T extends Persistable> {

    private final Class<T> dataClass;
    private String logTag;
    private ContentResolver contentResolver;
    private DbHelper dbHelper;

    @GeneralBlobModel.DBDataType
    private int dataType;

    public ListDbClient(Class<T> dataClass, ContentResolver contentResolver, String logTag,
                        @GeneralBlobModel.DBDataType int dataType, DbHelper dbHelper) {
        this.contentResolver = contentResolver;
        this.dataClass = dataClass;
        this.logTag = logTag;
        this.dataType = dataType;
        this.dbHelper = dbHelper;
    }

    public Observable<List<DataBlob<T>>> getAllByType() {
        return Observable.fromCallable(new Callable<List<DataBlob<T>>>() {
            @Override
            public List<DataBlob<T>> call() throws Exception {
                Log.d(logTag, "Getting from db");
                List<DataBlob<T>> results = dbHelper.getAllByType(dataClass, dataType, logTag);
                Log.d(logTag, "Found " + results.size() + " in cache");
                return results;
            }
        });
    }

    public Observable<List<DataBlob<T>>> getAllByIds(final List<String> ids) {
        return Observable.fromCallable(new Callable<List<DataBlob<T>>>() {
            @Override
            public List<DataBlob<T>> call() throws Exception {
                Log.d(logTag, "Getting from db with ids" + ids);
                List<DataBlob<T>> results = dbHelper.getAllByIds(dataClass, ids, dataType, logTag);
                Log.d(logTag, "Found " + results.size() + " in cache");
                return results;
            }
        });
    }

    public Observable<DataBlob<T>> getItemById(final String id) {
        return Observable.fromCallable(new Callable<DataBlob<T>>() {
            @Override
            public DataBlob<T> call() throws Exception {
                Log.d(logTag, "Getting from db with id: " + id);
                DataBlob<T> result = dbHelper.getItemById(dataClass, id, dataType, logTag);
                Log.d(logTag, "Found " + result + " in cache");
                return result;
            }
        });
    }

    public void update(List<T> addedOrUpdated, List<String> deleted, List<String> orderedIds) {
        Log.d(logTag, "Updating db with " + addedOrUpdated.size() + " updated and " + deleted.size() + " deleted");
        List<String> idsToAddUpdateOrDelete = toIdList(addedOrUpdated);
        idsToAddUpdateOrDelete.addAll(deleted);
        List<String> unchangedIds = getUnchangedIds(orderedIds, idsToAddUpdateOrDelete); new ArrayList<>(orderedIds);
        ArrayList<ContentProviderOperation> operations = new ArrayList<>();
        long lastTimeFetched = System.currentTimeMillis();

        if (idsToAddUpdateOrDelete.size() > 0) {
            String idInString = dbHelper.buildInIdString(idsToAddUpdateOrDelete);
            ContentProviderOperation operation = ContentProviderOperation
                    .newDelete(DbContract.GeneralBlob.CONTENT_URI)
                    .withSelection(idInString, null)
                    .build();
            operations.add(operation);
        }

        for (T item : addedOrUpdated) {
            int orderIndex = orderedIds.indexOf(item.getId());
            ContentValues values = dbHelper.getContentValues(item, dataType, orderIndex, lastTimeFetched, lastTimeFetched);
            ContentProviderOperation operation = ContentProviderOperation
                    .newInsert(DbContract.GeneralBlob.CONTENT_URI)
                    .withValues(values)
                    .build();
            operations.add(operation);
        }

        if (unchangedIds.size() > 0) {
            String idInString = dbHelper.buildInIdString(unchangedIds);
            ContentProviderOperation operation = ContentProviderOperation.newUpdate(DbContract.GeneralBlob.CONTENT_URI)
                    .withSelection(idInString, null)
                    .withValue(DbContract.GenericBlobColumns.LAST_FETCHED, lastTimeFetched)
                    .withValue(DbContract.GenericBlobColumns.LAST_VIEWED, lastTimeFetched)
                    .build();
            operations.add(operation);
        }

        try {
            contentResolver.applyBatch(DbContract.CONTENT_AUTHORITY, operations);
            Log.d(logTag, "Updating DB success");
        } catch (OperationApplicationException | RemoteException | IllegalStateException e) {
            Log.e(logTag, "Updating DB failed", e);
        }
    }

    private List<String> getUnchangedIds(List<String> orderedIds, List<String> idsToAddUpdateOrDelete) {
        List<String> unchangedIds = new ArrayList<>(orderedIds);
        unchangedIds.removeAll(idsToAddUpdateOrDelete);
        return unchangedIds;
    }

    public void update(T item) {
        Log.d(logTag, "ListDbClient: Updating db by deleting and inserting with itemId = " + item.getId() + ", typeIndex = " + dataType);
        long lastTimeFetched = System.currentTimeMillis();
        ContentValues contentValues = dbHelper.getContentValues(item, dataType, 0, lastTimeFetched,
                lastTimeFetched);
        String idInString = dbHelper.buildInIdString(Arrays.asList(item.getId()));
        Log.d(logTag, "ListDbClient: delete db with itemId = " + item.getId() + ", typeIndex = " + dataType);
        contentResolver.delete(DbContract.GeneralBlob.CONTENT_URI, idInString, null);
        Log.d(logTag, "ListDbClient: insert db with itemId = " + item.getId() + ", typeIndex = " + dataType);
        contentResolver.insert(DbContract.GeneralBlob.CONTENT_URI, contentValues);
    }

    public void clear() {
        contentResolver.delete(DbContract.GeneralBlob.CONTENT_URI, dbHelper.buildTypeCondition(dataType), null);
    }

    public <T extends Identifiable> List<String> toIdList(List<T> itemList) {
        List<String> idList = new ArrayList<>();
        for (T item : itemList) {
            idList.add(item.getId());
        }
        return idList;
    }

}
