package com.hpe.sb.mobile.app;

import android.app.Instrumentation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;
import com.hpe.sb.mobile.app.infra.db.DbContract;
import com.hpe.sb.mobile.app.infra.db.GeneralBlobModel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

/**
 * Created by ortalcohen on 31/03/2016.
 */
@RunWith(AndroidJUnit4.class)
public class ProviderTest extends InstrumentationTestCase {


    Instrumentation instrumentation;

    ContentResolver contentResolver;

    @Before
    public void setUp() throws Exception {
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        instrumentation = getInstrumentation();
        contentResolver = instrumentation.getContext().getContentResolver();
        super.setUp();

    }


    @Test
    public void objectIsNotNull() {

        Assert.assertNotNull(contentResolver);
    }

    @Test
    public void insertTest() {

        insertExample(new GeneralBlobModel("e1", GeneralBlobModel.TYPE_OFFERINGS, "1", "1",
                new Date().getTime(), new Date().getTime(), new Date().getTime()));
        insertExample(new GeneralBlobModel("e2", GeneralBlobModel.TYPE_ARTICLES, "2", "2",
                new Date().getTime(), new Date().getTime(), new Date().getTime()));

        Cursor cursor = contentResolver
                .query(DbContract.GeneralBlob.CONTENT_URI, null,
                        DbContract.GenericBlobColumns.TYPE + " = '" + GeneralBlobModel.TYPE_ARTICLES
                                + "'", null, null);

        cursor.moveToFirst();

        while (!cursor.isAfterLast()) {
            assertEquals(
                    cursor.getString(cursor.getColumnIndex(DbContract.GenericBlobColumns.ITEM_ID)),
                    "e2");
            cursor.moveToNext();
        }
        cursor.close();

    }

    @Test
    public void deleteTest() {
        contentResolver.delete(DbContract.GeneralBlob.CONTENT_URI,
                DbContract.GenericBlobColumns.ITEM_ID + " = 'e1'", null);
        contentResolver.delete(DbContract.GeneralBlob.CONTENT_URI,
                DbContract.GenericBlobColumns.ITEM_ID + " = 'e2'", null);

        Cursor cursor = contentResolver
                .query(DbContract.GeneralBlob.CONTENT_URI, null,
                        DbContract.GenericBlobColumns.ITEM_ID + " = 'e1'", null, null);

        assertEquals(cursor.getCount(), 0);

        cursor = contentResolver
                .query(DbContract.GeneralBlob.CONTENT_URI, null,
                        DbContract.GenericBlobColumns.ITEM_ID + " = 'e1'", null, null);

        assertEquals(cursor.getCount(), 0);
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


}