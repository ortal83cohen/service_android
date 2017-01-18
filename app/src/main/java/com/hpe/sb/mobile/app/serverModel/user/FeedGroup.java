package com.hpe.sb.mobile.app.serverModel.user;

import com.hpe.sb.mobile.app.serverModel.search.EntityItem;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

/**
 * Created by cohenort on 21/04/2016.
 */
public class FeedGroup implements Parcelable {

    private String title;

    public List<EntityItem> getEntityItems() {
        return entityItems;
    }

    public FeedGroup(String title,
                     List<EntityItem> entityItems) {
        this.title = title;
        this.entityItems = entityItems;
    }

    /**
     * Feed group items.
     */
    private List<EntityItem> entityItems;

    public String getTitle() {
        return title;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(entityItems);
        parcel.writeString(title);
    }

    public void offerings(Parcel in) {
        in.readList(entityItems, List.class.getClassLoader());
        title = in.readString();
    }
}
