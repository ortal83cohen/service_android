package com.hpe.sb.mobile.app.infra.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class DbDatabase extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "ServiceDB";

    public DbDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public static void deleteDatabase(Context context) {
        context.deleteDatabase(DATABASE_NAME);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL statement to create  table

        final String createBlob = "CREATE TABLE " + DbContract.Tables.TABLE_GENERIC_BLOB + " (" +
                DbContract.GenericBlobColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                DbContract.GenericBlobColumns.ITEM_ID+ " STRING NOT NULL ," +
                DbContract.GenericBlobColumns.TYPE + " SHORT INTEGER NOT NULL ," +
                DbContract.GenericBlobColumns.VERSION + " STRING NOT NULL DEFAULT '' ," +
                DbContract.GenericBlobColumns.CONTENT + " TEXT NOT NULL, " +
                DbContract.GenericBlobColumns.LAST_FETCHED + " INTEGER NOT NULL , " +//An INTEGER column will handle long values
                DbContract.GenericBlobColumns.CHECKSUM + " INTEGER , " +
                DbContract.GenericBlobColumns.LAST_VIEWED + " INTEGER NOT NULL , " +
                DbContract.GenericBlobColumns.ORDER_INDEX + " INTEGER NOT NULL ," +
                " UNIQUE (" + DbContract.GenericBlobColumns.ITEM_ID + ") ON CONFLICT REPLACE);";

        // create  table
        db.execSQL(createBlob);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DbContract.Tables.TABLE_GENERIC_BLOB);
        }
        this.onCreate(db);
    }

}