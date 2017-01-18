package com.hpe.sb.mobile.app.serverModel.user;

import com.hpe.sb.mobile.app.infra.dataClients.Persistable;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cohenort on 21/04/2016.
 */
public  class Feed implements Parcelable , Persistable {
    public static final String SMART_FEED_STATIC_ID = "smartFeed";
    private List<FeedGroup> feedGroups;

    public List<FeedGroup> getFeedGroups() {
        return feedGroups;
    }

    public Feed() {
        this.feedGroups = new ArrayList<>();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(feedGroups);
    }
    public void offerings(Parcel in) {
        in.readList(feedGroups, List.class.getClassLoader());
    }

    @Override
    public String getId() {
        return SMART_FEED_STATIC_ID;
    }

    @Override
    public int getChecksum() {
        return 0;
    }
}
