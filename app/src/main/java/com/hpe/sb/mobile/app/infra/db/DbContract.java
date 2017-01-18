package com.hpe.sb.mobile.app.infra.db;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import com.hpe.sb.mobile.app.BuildConfig;


public class DbContract {
    public static final String CONTENT_AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    private static final String PATH_GENERIC_BLOB = "generic_blob";


    interface Tables {
        String TABLE_GENERIC_BLOB = "generic_blob";
    }

    public interface GenericBlobColumns extends BaseColumns {
        String ITEM_ID = "id";
        String TYPE = "type";
        String CONTENT = "content";
        String VERSION = "version";
        String LAST_VIEWED = "last_viewed";
        String LAST_FETCHED = "last_fetched";
        String CHECKSUM = "checksum";
        String ORDER_INDEX = "order_index";

    }


    public static class GeneralBlob implements GenericBlobColumns, BaseColumns {
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_GENERIC_BLOB).build();
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/int";

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

}
