package com.hpe.sb.mobile.app.infra.db;

import com.hpe.sb.mobile.app.common.utils.JsonTranslation;
import com.hpe.sb.mobile.app.infra.dataClients.DataBlob;
import com.hpe.sb.mobile.app.infra.dataClients.Persistable;
import com.hpe.sb.mobile.app.infra.encryption.EncryptionService;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by chovel on 26/04/2016.
 *
 * A util for handling basic common db commands, such has get items by type.
 * Can be optionally constructed with an encryption service, which in that case will encrypt and decrypt
 * entries when saving to the DB.
 */
public class DbHelper {

    private final EncryptionService encryptionService;

    public ContentResolver contentResolver;

    public DbHelper(ContentResolver contentResolver) {
        this(contentResolver, null);
    }

    public DbHelper(ContentResolver contentResolver, EncryptionService encryptionService) {
        this.contentResolver = contentResolver;
        this.encryptionService = encryptionService;
    }

    public <T extends Persistable> List<DataBlob<T>> select(String selection, Class<T> dataClass, String logTag) {
        Cursor cursor = contentResolver.query(DbContract.GeneralBlob.CONTENT_URI, null, selection, null, DbContract.GeneralBlob.ORDER_INDEX + " ASC");
        if (cursor == null) {
            Log.e(logTag, " cursor is null for query " + selection);
            return new ArrayList<>();
        }

        List<DataBlob<T>> results = new ArrayList<>();
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            String id = cursor.getString(cursor.getColumnIndex(DbContract.GenericBlobColumns.ITEM_ID));
            String content = cursor.getString(cursor.getColumnIndex(DbContract.GenericBlobColumns.CONTENT));
            long lastViewed = cursor.getLong(cursor.getColumnIndex(DbContract.GenericBlobColumns.LAST_VIEWED));
            long lastFetched = cursor.getLong(cursor.getColumnIndex(DbContract.GenericBlobColumns.LAST_FETCHED));
            if (encryptionService != null) {
                content = encryptionService.decrypt(content);
            }
            T item = JsonTranslation.jsonString2Object(content, dataClass);
            results.add(new DataBlob<>(id, item, dataClass, lastViewed, lastFetched));
            cursor.moveToNext();
        }
        cursor.close();
        return results;
    }


    public <T extends Persistable> List<DataBlob<T>> getAllByType(Class<T> dataClass, int typeIndex, String logTag) {
        return select(buildTypeCondition(typeIndex), dataClass, logTag);
    }

    public String buildInIdString(List<String> ids) {
        StringBuilder idIn = new StringBuilder(DbContract.GeneralBlob.ITEM_ID + " IN (");
        Iterator<String> iterator = ids.iterator();
        while (iterator.hasNext()) {
            idIn.append("'" + iterator.next() + "'");
            if (iterator.hasNext()) {
                idIn.append(",");
            }
        }
        idIn.append(")");
        return idIn.toString();
    }

    public String buildTypeCondition(int typeIndex) {
        return DbContract.GenericBlobColumns.TYPE + " = '" + typeIndex + "'";
    }

    public <T extends Persistable> List<DataBlob<T>> getAllByIds(Class<T> dataClass, List<String> ids, int dataType, String logTag) {
        return select(buildInIdString(ids), dataClass, logTag);
    }

    public <T extends Persistable> DataBlob<T> getItemById(Class<T> dataClass, String id, int dataType, String logTag) {
        List<DataBlob<T>> allByIds = getAllByIds(dataClass, Arrays.asList(id), dataType, logTag);
        if (allByIds != null && !allByIds.isEmpty()) {
            return allByIds.get(0);
        } else {
            Log.w(logTag, "no data found for id: " + id + "and type: " + dataType);
            return null;
        }
    }

    public <T extends Persistable> ContentValues getContentValues(T dataItem,
            @GeneralBlobModel.DBDataType int typeIndex,
            int order,
            long lastTimeFetched,
            long lastTimeViewed) {
        ContentValues contentValues = new ContentValues();
        String content = JsonTranslation.object2JsonString(dataItem, dataItem.getClass());
        if (encryptionService != null) {
            content = encryptionService.encrypt(content);
        }

        contentValues.put(DbContract.GeneralBlob.ITEM_ID, dataItem.getId());
        contentValues.put(DbContract.GeneralBlob.TYPE, typeIndex);
        contentValues.put(DbContract.GeneralBlob.LAST_FETCHED, lastTimeFetched);
        contentValues.put(DbContract.GeneralBlob.LAST_VIEWED, lastTimeViewed);
        contentValues.put(DbContract.GeneralBlob.CHECKSUM, dataItem.getChecksum());
        contentValues.put(DbContract.GeneralBlob.VERSION, 0);
        contentValues.put(DbContract.GeneralBlob.ORDER_INDEX, order);
        contentValues.put(DbContract.GeneralBlob.CONTENT, content);
        return contentValues;
    }

    public int deleteGeneralBlob() {
        return contentResolver.delete(DbContract.GeneralBlob.CONTENT_URI, "", null);
    }
}
