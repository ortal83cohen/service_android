package com.hpe.sb.mobile.app.serverModel.catalog;

import android.os.Parcel;
import android.os.Parcelable;
import com.hpe.sb.mobile.app.infra.dataClients.Persistable;

public class CatalogGroup implements Persistable, Parcelable {

    private String id;
    private String title;
    private String description;
    private String parentCategoryId;
    private String iconImageId;
    private String backgroundImageId;
    private String backgroundColor;
    /**
     * For SAW:
     * true if this is category
     * false if this is service definition
     * For Propel:
     * true if this is the root category
     */
    private boolean isRoot;

	private int checksum;

    private String targetUrl;

    public CatalogGroup() {

    }

    public CatalogGroup(String id, String title, String description, String parentCategoryId, String iconImageId, String backgroundImageId, String backgroundColor, int checksum, boolean isRoot, String targetUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.parentCategoryId = parentCategoryId;
        this.iconImageId = iconImageId;
        this.backgroundImageId = backgroundImageId;
        this.backgroundColor = backgroundColor;
		this.checksum = checksum;
        this.isRoot = isRoot;
        this.targetUrl = targetUrl;
    }

    protected CatalogGroup(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        parentCategoryId = in.readString();
        iconImageId = in.readString();
        backgroundImageId = in.readString();
        backgroundColor = in.readString();
        isRoot = in.readInt() != 0;
        targetUrl = in.readString();
    }

    public static final Creator<CatalogGroup> CREATOR = new Creator<CatalogGroup>() {
        @Override
        public CatalogGroup createFromParcel(Parcel in) {
            return new CatalogGroup(in);
        }

        @Override
        public CatalogGroup[] newArray(int size) {
            return new CatalogGroup[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(parentCategoryId);
        parcel.writeString(iconImageId);
        parcel.writeString(backgroundImageId);
        parcel.writeString(backgroundColor);
        parcel.writeInt(isRoot ? 1 : 0);
        parcel.writeString(targetUrl);
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getBackgroundImageId() {
        return backgroundImageId;
    }

    public void setBackgroundImageId(String backgroundImageId) {
        this.backgroundImageId = backgroundImageId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconImageId() {
        return iconImageId;
    }

    public void setIconImageId(String iconImageId) {
        this.iconImageId = iconImageId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentCategoryId() {
        return parentCategoryId;
    }

    public void setParentCategoryId(String parentCategoryId) {
        this.parentCategoryId = parentCategoryId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getChecksum() {
        return checksum;
    }

    public void setChecksum(int checksum) {
        this.checksum = checksum;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public void setRoot(boolean root) {
        isRoot = root;
    }

    public String getTargetUrl() {
        return targetUrl;
    }

    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CatalogGroup that = (CatalogGroup) o;

        if (isRoot != that.isRoot) {
            return false;
        }
        if (checksum != that.checksum) {
            return false;
        }
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (title != null ? !title.equals(that.title) : that.title != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (parentCategoryId != null ? !parentCategoryId.equals(that.parentCategoryId) : that.parentCategoryId != null) {
            return false;
        }
        if (iconImageId != null ? !iconImageId.equals(that.iconImageId) : that.iconImageId != null) {
            return false;
        }
        if (backgroundImageId != null ? !backgroundImageId.equals(that.backgroundImageId) : that.backgroundImageId != null) {
            return false;
        }
        if (targetUrl != null ? !targetUrl.equals(that.targetUrl) : that.targetUrl != null) {
            return false;
        }
        return backgroundColor != null ? backgroundColor.equals(that.backgroundColor) : that.backgroundColor == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (parentCategoryId != null ? parentCategoryId.hashCode() : 0);
        result = 31 * result + (iconImageId != null ? iconImageId.hashCode() : 0);
        result = 31 * result + (backgroundImageId != null ? backgroundImageId.hashCode() : 0);
        result = 31 * result + (backgroundColor != null ? backgroundColor.hashCode() : 0);
        result = 31 * result + (isRoot ? 1 : 0);
        result = 31 * result + checksum;
        result = 31 * result + (targetUrl != null ? targetUrl.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CatalogGroup{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", parentCategoryId='" + parentCategoryId + '\'' +
                ", iconImageId='" + iconImageId + '\'' +
                ", backgroundImageId='" + backgroundImageId + '\'' +
                ", backgroundColor='" + backgroundColor + '\'' +
                ", isRoot=" + isRoot +
                ", targetUrl=" + targetUrl +
                '}';
    }
}
