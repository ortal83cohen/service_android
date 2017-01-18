package com.hpe.sb.mobile.app.infra.db;

import com.hpe.sb.mobile.app.ServiceBrokerApplication;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.Date;

import javax.inject.Inject;

public class ExamplesUse {

    @Inject
    ContentResolver contentResolver;


    public ExamplesUse(Context app) {
        ((ServiceBrokerApplication) app).getServiceBrokerComponent().inject(this);
    }

    public void insertExample(GeneralBlobModel generalBlobModel) {

        ContentValues values = new ContentValues();

        values.put(DbContract.GenericBlobColumns.ITEM_ID, generalBlobModel.getId());
        values.put(DbContract.GenericBlobColumns.TYPE, generalBlobModel.getType());
        values.put(DbContract.GenericBlobColumns.VERSION, generalBlobModel.getVersion());
        values.put(DbContract.GenericBlobColumns.CONTENT, generalBlobModel.getContent());
        values.put(DbContract.GenericBlobColumns.LAST_FETCHED,
                generalBlobModel.getLastFetched());
        values.put(DbContract.GenericBlobColumns.CHECKSUM,
                generalBlobModel.getLastUpdate());
        values.put(DbContract.GenericBlobColumns.LAST_VIEWED,
                generalBlobModel.getLastViewed());

        contentResolver.insert(DbContract.GeneralBlob.CONTENT_URI, values);

    }

    public void selectExample(String where) {

        Cursor cursor = contentResolver
                .query(DbContract.GeneralBlob.CONTENT_URI, null, where, null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            Log.e("ITEM_ID",
                    cursor.getString(cursor.getColumnIndex(DbContract.GenericBlobColumns.ITEM_ID)));
            cursor.moveToNext();
        }
        cursor.close();
    }

    public void test() {

        insertExample(new GeneralBlobModel("e1", GeneralBlobModel.TYPE_OFFERINGS, "1", "1",
                new Date().getTime(), new Date().getTime(), new Date().getTime()));
        insertExample(new GeneralBlobModel("e2", GeneralBlobModel.TYPE_ARTICLES, "2", "2",
                new Date().getTime(), new Date().getTime(), new Date().getTime()));
        insertExample(new GeneralBlobModel("e3", GeneralBlobModel.TYPE_OFFERINGS, "3", "3",
                new Date().getTime(), new Date().getTime(), new Date().getTime()));

        selectExample(null);
        selectExample(
                DbContract.GenericBlobColumns.TYPE + " = '" + GeneralBlobModel.TYPE_ARTICLES + "'");
    }
}
