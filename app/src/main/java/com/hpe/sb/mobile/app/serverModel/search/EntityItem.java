package com.hpe.sb.mobile.app.serverModel.search;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.SerializedName;

/**
 * represents also related entity for views that display EntityItems
 * **/
public class EntityItem implements Parcelable {

    private String id;
    @SerializedName("type")
    private EntityType type;
    @SerializedName("title")
    private String title;
    @SerializedName("preview")
    private String preview;
    @SerializedName("imageId")
    private String imageId;
    @SerializedName("popularity")
    private boolean popularity;
    @SerializedName("isSupportableOnMobile")
    private boolean isSupportableOnMobile;


    /**
     * if equals 0 it will not be displayed
     */
    @SerializedName("cost")
    private double cost;
    /**
     * for example $.
     */
    @SerializedName("costCurrency")
    private String costCurrency;
    public EntityItem() {
    }

    public EntityItem(String id, EntityType type, String title, String preview, String imageId, boolean popularity, double cost, String costCurrency, boolean isSupportableOnMobile) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.preview = preview;
        this.imageId = imageId;
        this.popularity = popularity;
        this.cost = cost;
        this.costCurrency = costCurrency;
        this.isSupportableOnMobile = isSupportableOnMobile;
    }

    protected EntityItem(Parcel in) {
        id = in.readString();
        try {
            type = EntityType.valueOf(in.readString());
        } catch (IllegalArgumentException x) {
            type = null;
        }
        title = in.readString();
        preview = in.readString();
        imageId = in.readString();
        cost = in.readDouble();
        costCurrency = in.readString();
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(((type == null) ? "" : type.name()));
        parcel.writeString(title);
        parcel.writeString(preview);
        parcel.writeString(imageId);
        parcel.writeDouble(cost);
        parcel.writeString(costCurrency);
    }

    public static final Creator<EntityItem> CREATOR = new Creator<EntityItem>() {
        @Override
        public EntityItem createFromParcel(Parcel in) {
            return new EntityItem(in);
        }

        @Override
        public EntityItem[] newArray(int size) {
            return new EntityItem[size];
        }
    };

    public String getCostCurrency() {
        return costCurrency;
    }

    public void setCostCurrency(String costCurrency) {
        this.costCurrency = costCurrency;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public EntityType getType() {
        return type;
    }

    public void setType(EntityType type) {
        this.type = type;
    }

    public boolean isPopularity() {
        return popularity;
    }

    public void setPopularity(boolean popularity) {
        this.popularity = popularity;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public boolean isSupportableOnMobile() {
        return isSupportableOnMobile;
    }

    public void setSupportableOnMobile(boolean supportableOnMobile) {
        this.isSupportableOnMobile = supportableOnMobile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EntityItem)) {
            return false;
        }

        EntityItem that = (EntityItem) o;

        if (popularity != that.popularity) {
            return false;
        }
        if (isSupportableOnMobile != that.isSupportableOnMobile) {
            return false;
        }
        if (Double.compare(that.cost, cost) != 0) {
            return false;
        }
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (type != that.type) {
            return false;
        }
        if (title != null ? !title.equals(that.title) : that.title != null) {
            return false;
        }
        if (preview != null ? !preview.equals(that.preview) : that.preview != null) {
            return false;
        }
        if (imageId != null ? !imageId.equals(that.imageId) : that.imageId != null) {
            return false;
        }
        return costCurrency != null ? costCurrency.equals(that.costCurrency) : that.costCurrency == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (preview != null ? preview.hashCode() : 0);
        result = 31 * result + (imageId != null ? imageId.hashCode() : 0);
        result = 31 * result + (popularity ? 1 : 0);
        result = 31 * result + (isSupportableOnMobile ? 1 : 0);
        temp = Double.doubleToLongBits(cost);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (costCurrency != null ? costCurrency.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }


}
