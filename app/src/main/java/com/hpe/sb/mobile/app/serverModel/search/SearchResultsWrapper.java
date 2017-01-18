package com.hpe.sb.mobile.app.serverModel.search;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by malikdav on 27/04/2016.
 */
public class SearchResultsWrapper implements Parcelable {

    private List<EntityItem> results;

    public SearchResultsWrapper(List<EntityItem> entityItemList) {
        this.results = entityItemList;
    }

    public List<EntityItem> getResults() {
        return results;
    }

    public void setResults(List<EntityItem> results) {
        this.results = results;
    }

    protected SearchResultsWrapper(Parcel in) {
        results = in.createTypedArrayList(EntityItem.CREATOR);
    }

    public static final Creator<SearchResultsWrapper> CREATOR = new Creator<SearchResultsWrapper>() {
        @Override
        public SearchResultsWrapper createFromParcel(Parcel in) {
            return new SearchResultsWrapper(in);
        }

        @Override
        public SearchResultsWrapper[] newArray(int size) {
            return new SearchResultsWrapper[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(results);
    }
}
