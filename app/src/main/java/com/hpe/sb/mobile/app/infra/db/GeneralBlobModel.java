package com.hpe.sb.mobile.app.infra.db;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class GeneralBlobModel {

    @IntDef({TYPE_OFFERINGS,
            TYPE_ARTICLES,
            TYPE_CATEGORIES,
            TYPE_USER_ITEMS,
            TYPE_SMART_FEED,
            TYPE_TEST,
            TYPE_OFFERING_BY_CATEGORY,
            TYPE_CATALOG_GROUP_BY_CATEGORY,
            TYPE_GCM_REGISTRATIONS,
            TYPE_USER_CONTEXT,
            TYPE_DISPLAY_LABELS,
            TENANT_SETTINGS,
            THEME_SETTINGS})
    @Retention(RetentionPolicy.SOURCE)
    public @interface DBDataType {}

    public static final int TYPE_OFFERINGS = 1;
    public static final int TYPE_ARTICLES = 2;
    public static final int TYPE_CATEGORIES = 3;
    public static final int TYPE_USER_ITEMS = 4;
    public static final int TYPE_SMART_FEED = 5;
    public static final int TYPE_OFFERING_BY_CATEGORY = 6;
    public static final int TYPE_CATALOG_GROUP_BY_CATEGORY = 7;
	public static final int TYPE_GCM_REGISTRATIONS = 8;
	public static final int TYPE_USER_CONTEXT = 9;
	public static final int TENANT_SETTINGS = 10;
	public static final int THEME_SETTINGS = 11;
	public static final int TYPE_DISPLAY_LABELS = 12;

    public static final int TYPE_TEST = -1;

    public GeneralBlobModel(String id, int type, String content, String version,
            long lastViewed, long lastFetched, long lastUpdate) {
        this.id = id;
        this.type = type;
        this.content = content;
        this.version = version;
        this.lastViewed = lastViewed;
        this.lastFetched = lastFetched;
        this.lastUpdate = lastUpdate;
    }

    private String id;
    private int type;
    private String content;
    private String version;
    private long lastViewed;
    private long lastFetched;
    private long lastUpdate;

    public String getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public String getContent() {
        return content;
    }

    public String getVersion() {
        return version;
    }

    public long getLastViewed() {
        return lastViewed;
    }

    public long getLastFetched() {
        return lastFetched;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }
}
