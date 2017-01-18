package com.hpe.sb.mobile.app.infra.db;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.NonNull;

import java.io.FileNotFoundException;
import java.util.ArrayList;


public class DbProvider extends ContentProvider {

    private static final int GENERIC_BLOB = 100;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private DbDatabase mOpenHelper;

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = DbContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, "generic_blob", GENERIC_BLOB);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DbDatabase(getContext());
        return true;
    }

    private void deleteDatabase() {
        mOpenHelper.close();
        Context context = getContext();
        DbDatabase.deleteDatabase(context);
        mOpenHelper = new DbDatabase(getContext());
    }

    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case GENERIC_BLOB:
                return DbContract.GeneralBlob.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs,
            String sortOrder) {
        final SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);
        final DbSelectionBuilder builder = new DbSelectionBuilder();
        switch (match) {
            case GENERIC_BLOB:
                builder.table(DbContract.Tables.TABLE_GENERIC_BLOB);
                if (selection == null) {
                    return builder.query(db, false, projection, "", "");
                } else {
                    return builder.where(selection).query(db, false, projection, sortOrder, "");
                }

            default: {
                throw new UnsupportedOperationException("Unknown insert uri: " + uri);
            }
        }

    }


    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case GENERIC_BLOB:
                long _id = db.insert(DbContract.Tables.TABLE_GENERIC_BLOB, null, values);
                if (_id > 0) {
                    returnUri = DbContract.GeneralBlob.buildUri(_id);
                } else {
                    throw new SQLException("Failed to insert row into " + uri);
                }
                break;
            default: {
                throw new UnsupportedOperationException("Unknown insert uri: " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(returnUri, null);
        return returnUri;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int affectedRows;
        switch (match) {
            case GENERIC_BLOB:
                final DbSelectionBuilder builder = new DbSelectionBuilder();
                affectedRows = builder.where(selection).table(DbContract.Tables.TABLE_GENERIC_BLOB).update(db, values);
                break;
            default: {
                throw new UnsupportedOperationException("Unknown insert uri: " + uri);
            }
        }

        return affectedRows;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        if (uri.equals(DbContract.BASE_CONTENT_URI)) {
            deleteDatabase();
            return 1;
        }
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final DbSelectionBuilder builder = new DbSelectionBuilder();
        final int match = sUriMatcher.match(uri);

        if (match == GENERIC_BLOB) {
            builder.table(DbContract.Tables.TABLE_GENERIC_BLOB);
        }

        return builder.where(selection, selectionArgs).delete(db);
    }

    @Override
    public @NonNull ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations)
            throws OperationApplicationException {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            final int numOperations = operations.size();
            final ContentProviderResult[] results = new ContentProviderResult[numOperations];
            for (int i = 0; i < numOperations; i++) {
                results[i] = operations.get(i).apply(this, results, i);
            }
            db.setTransactionSuccessful();
            return results;
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode) throws FileNotFoundException {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            default: {
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }
    }


}
