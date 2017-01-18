package com.hpe.sb.mobile.app.serverModel.catalog;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class OfferingsByCatalogGroupResult implements Parcelable {

    List<Offering> offerings;
    List<CatalogGroup> mgParentCatalogGroups;

    public OfferingsByCatalogGroupResult() {
        offerings = new ArrayList<>();
        mgParentCatalogGroups = new ArrayList<>();
    }

    public OfferingsByCatalogGroupResult(List<Offering> offerings, List<CatalogGroup> mgParentCategories) {
        this.offerings = offerings;
        this.mgParentCatalogGroups = mgParentCategories;
    }


    /*PLEASE NOTE: THE ORDER IS IMPORTANT, IT SHOULD BE THE SAME AS IN writeToParcel*/
    public void offerings(Parcel in) {
        in.readList(offerings, List.class.getClassLoader());
        in.readList(mgParentCatalogGroups, List.class.getClassLoader());
    }

    public List<CatalogGroup> getMgParentCatalogGroups() {
        return mgParentCatalogGroups;
    }

    public void setMgParentCatalogGroups(List<CatalogGroup> mgParentCatalogGroups) {
        this.mgParentCatalogGroups = mgParentCatalogGroups;
    }

    public List<Offering> getOfferings() {
        return offerings;
    }

    public void setOfferings(List<Offering> offerings) {
        this.offerings = offerings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        OfferingsByCatalogGroupResult that = (OfferingsByCatalogGroupResult) o;

        if (offerings != null ? !offerings.equals(that.offerings) : that.offerings != null) {
            return false;
        }
        return mgParentCatalogGroups != null ? mgParentCatalogGroups.equals(that.mgParentCatalogGroups) : that.mgParentCatalogGroups == null;

    }

    @Override
    public int hashCode() {
        int result = offerings != null ? offerings.hashCode() : 0;
        result = 31 * result + (mgParentCatalogGroups != null ? mgParentCatalogGroups.hashCode() : 0);
        return result;
    }

    protected OfferingsByCatalogGroupResult(Parcel in) {
        offerings = in.createTypedArrayList(Offering.CREATOR);
        mgParentCatalogGroups = in.createTypedArrayList(CatalogGroup.CREATOR);
    }

    public static final Creator<OfferingsByCatalogGroupResult> CREATOR = new Creator<OfferingsByCatalogGroupResult>() {
        @Override
        public OfferingsByCatalogGroupResult createFromParcel(Parcel in) {
            return new OfferingsByCatalogGroupResult(in);
        }

        @Override
        public OfferingsByCatalogGroupResult[] newArray(int size) {
            return new OfferingsByCatalogGroupResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(offerings);
        parcel.writeTypedList(mgParentCatalogGroups);
    }

    public boolean isEmpty() {
        return offerings == null || mgParentCatalogGroups == null || (offerings.isEmpty() && mgParentCatalogGroups.isEmpty());
    }
}
