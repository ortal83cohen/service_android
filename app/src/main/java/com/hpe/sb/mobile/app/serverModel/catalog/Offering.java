package com.hpe.sb.mobile.app.serverModel.catalog;

import android.os.Parcel;
import android.os.Parcelable;
import com.hpe.sb.mobile.app.serverModel.search.EntityType;
import com.hpe.sb.mobile.app.infra.dataClients.Persistable;

/**
 * A data model for a catalog offering.
 */
public class Offering implements Parcelable, Persistable {

    /**
     * Offering id/
     */
    private String id;

    /**
     * Offering title.
     */
    private String title;

    /**
     * Offering description.
     */
    private String description;

    /**
     * Offering type.
     */
    private EntityType offeringType;

    /**
     * Parent Category Id of the offering
     * for saw it can be the service of the offering
     */
    private String catalogGroupId;

    /**
     * Offering imageId.
     */
    private String imageId;

    /**
     * Offering cost.
     */
    private double cost;

    /**
     * for example $.
     */
    private String costCurrency;

    private boolean isPopular;

    private boolean isSupportableOnMobile;

    public Offering() {
    }

    public Offering(String id, EntityType offeringType, String title, String description, String imageId,
                    String catalogGroupId/*for saw it can be the service of the offering*/, double cost,
                    String costCurrency, boolean isPopular, boolean isSupportableOnMobile) {
        this.description = description;
        this.id = id;
        this.imageId = imageId;
        this.offeringType = offeringType;
        this.title = title;
        this.catalogGroupId = catalogGroupId;
        this.cost = cost;
        this.costCurrency = costCurrency;
        this.isPopular = isPopular;
        this.isSupportableOnMobile = isSupportableOnMobile;
    }

    /*PLEASE NOTE: THE ORDER IS IMPORTANT, IT SHOULD BE THE SAME AS IN writeToParcel*/
    protected Offering(Parcel in) {
        id = in.readString();
        try {
            offeringType = EntityType.valueOf(in.readString());
        } catch (IllegalArgumentException x) {
            offeringType = null;
        }
        title = in.readString();
        description = in.readString();
        catalogGroupId = in.readString();
        imageId = in.readString();
        cost = in.readDouble();
        costCurrency = in.readString();
        isPopular = in.readInt() != 0;
        isSupportableOnMobile = in.readInt() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(((offeringType == null) ? "" : offeringType.name()));
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(catalogGroupId);
        parcel.writeString(imageId);
        parcel.writeDouble(cost);
        parcel.writeString(costCurrency);
        parcel.writeInt(isPopular ? 1 : 0);
        parcel.writeInt(isSupportableOnMobile ? 1 : 0);
    }

    // We need to add a Creator
    public static final Creator<Offering> CREATOR = new Creator<Offering>() {
        @Override
        public Offering createFromParcel(Parcel in) {
            return new Offering(in);
        }

        @Override
        public Offering[] newArray(int size) {
            return new Offering[size];
        }
    };

    public boolean isPopular() {
        return isPopular;
    }

    public void setPopular(boolean popular) {
        isPopular = popular;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public void setCostCurrency(String costCurrency) {
        this.costCurrency = costCurrency;
    }

    public double getCost() {
        return cost;
    }

    public String getCostCurrency() {
        return costCurrency;
    }

    public String getCatalogGroupId() {
        return catalogGroupId;
    }

    public void setCatalogGroupId(String catalogGroupId) {
        this.catalogGroupId = catalogGroupId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    @Override
    public int getChecksum() {
        return hashCode();
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

    public EntityType getOfferingType() {
        return offeringType;
    }

    public void setOfferingType(EntityType offeringType) {
        this.offeringType = offeringType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isSupportableOnMobile() {
        return isSupportableOnMobile;
    }

    public void setSupportableOnMobile(boolean supportableOnMobile) {
        isSupportableOnMobile = supportableOnMobile;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Offering that = (Offering) o;

        if (Double.compare(that.cost, cost) != 0) {
            return false;
        }
        if (isPopular != that.isPopular) {
            return false;
        }
        if (isSupportableOnMobile != that.isSupportableOnMobile) {
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
        if (offeringType != that.offeringType) {
            return false;
        }
        if (catalogGroupId != null ? !catalogGroupId.equals(that.catalogGroupId) : that.catalogGroupId != null) {
            return false;
        }
        if (imageId != null ? !imageId.equals(that.imageId) : that.imageId != null) {
            return false;
        }
        return costCurrency != null ? costCurrency.equals(that.costCurrency) : that.costCurrency == null;

    }

    //PLS NOTE: when getting the offeringType enum hashCode first convert it to string for comparing to server data
    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (offeringType != null ? offeringType.toString().hashCode() : 0);
        result = 31 * result + (catalogGroupId != null ? catalogGroupId.hashCode() : 0);
        result = 31 * result + (imageId != null ? imageId.hashCode() : 0);
        temp = Double.doubleToLongBits(cost);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (costCurrency != null ? costCurrency.hashCode() : 0);
        result = 31 * result + (isPopular ? 1 : 0);
        result = 31 * result + (isSupportableOnMobile ? 1 : 0);
        return result;
    }
}
