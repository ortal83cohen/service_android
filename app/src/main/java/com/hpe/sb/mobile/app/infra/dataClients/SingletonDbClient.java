package com.hpe.sb.mobile.app.infra.dataClients;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.util.Log;

import com.hpe.sb.mobile.app.infra.db.DbContract;
import com.hpe.sb.mobile.app.infra.db.GeneralBlobModel;
import com.hpe.sb.mobile.app.infra.db.DbHelper;

import java.util.List;
import java.util.concurrent.Callable;

import rx.Observable;

/**
 * This class exposes database access for data types which only one instance of them can exist at a given time.
 */
public class SingletonDbClient<T extends Persistable> {

    private final Class<T> dataClass;
    private String logTag;
    private ContentResolver contentResolver;
    private DbHelper dbHelper;
    String itemId;
    int typeIndex;

    public SingletonDbClient(Class<T> dataClass, ContentResolver contentResolver, String logTag,
                             String itemId, @GeneralBlobModel.DBDataType int typeIndex,
                             DbHelper dbHelper) {
        this.contentResolver = contentResolver;
        this.dataClass = dataClass;
        this.logTag = logTag;
        this.itemId = itemId;
        this.typeIndex = typeIndex;
        this.dbHelper = dbHelper;
    }

    public Observable<Void> set(final T item) {
        return Observable.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Log.d(logTag, "SingletonDbClient: Updating db with itemId = " + itemId + ", typeIndex = " + typeIndex);
                ContentValues contentValues = dbHelper.getContentValues(item, typeIndex, 0, System.currentTimeMillis(), System.currentTimeMillis());
                contentResolver.insert(DbContract.GeneralBlob.CONTENT_URI, contentValues);
                return null;
            }
        });
    }

    public Observable<DataBlob<T>> getItem() {
        return Observable.fromCallable(new Callable<DataBlob<T>>() {
            @Override
            public DataBlob<T> call() throws Exception {
                return getItemSync();
            }
        });
    }

    /**
     * Returns data in synchronously. Beware not to run on main thread!
     * Use only if getItem is not applicable.
     * @return item from database, null if not found.
     */
    public DataBlob<T> getItemSync() {
        Log.d(logTag, "Getting from db");
        List<DataBlob<T>> results = dbHelper.getAllByType(dataClass, typeIndex, logTag);
        Log.d(logTag, "Found " + results.size() + " in cache");
        if (!results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }

    public Observable<Void> clear() {
        return Observable.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Log.d(logTag, "deleting  from db typeIndex:" + typeIndex);
                contentResolver.delete(DbContract.GeneralBlob.CONTENT_URI, dbHelper.buildTypeCondition(typeIndex), null);
                return null;
            }
        });
    }
}
