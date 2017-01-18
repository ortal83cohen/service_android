package com.hpe.sb.mobile.app.serverModel.user.useritems;

import com.hpe.sb.mobile.app.serverModel.request.RequestType;

import android.os.Parcel;
import android.util.Log;

public class FollowedActiveRequestItem extends RequestActiveUserItem {

    private static final String TAG = "FollowedItem";

    public FollowedActiveRequestItem() {
    }

    public FollowedActiveRequestItem(String id, String title, RequestType requestType, String phase, String metaPhase,
            String requestedForName, String description, long creationTime) {
        this.id = id;
        this.title = title;
        this.requestType = requestType;
        this.phase = phase;
        this.metaPhase = metaPhase;
        this.requestedForName = requestedForName;
        this.creationTime = creationTime;
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        FollowedActiveRequestItem that = (FollowedActiveRequestItem) o;

        if (creationTime != that.creationTime) {
            return false;
        }
        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (title != null ? !title.equals(that.title) : that.title != null) {
            return false;
        }
        if (requestType != that.requestType) {
            return false;
        }
        if (phase != null ? !phase.equals(that.phase) : that.phase != null) {
            return false;
        }
        if (metaPhase != null ? !metaPhase.equals(that.metaPhase) : that.metaPhase != null) {
            return false;
        }
        if (requestedForName != null ? !requestedForName.equals(that.requestedForName) : that.requestedForName != null) {
            return false;
        }
        return description != null ? description.equals(that.description) : that.description == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (requestType != null ? requestType.hashCode() : 0);
        result = 31 * result + (phase != null ? phase.hashCode() : 0);
        result = 31 * result + (metaPhase != null ? metaPhase.hashCode() : 0);
        result = 31 * result + (requestedForName != null ? requestedForName.hashCode() : 0);
        result = 31 * result + (int) (creationTime ^ (creationTime >>> 32));
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    protected FollowedActiveRequestItem(Parcel in) {
        id = in.readString();
        title = in.readString();
        phase = in.readString();
        metaPhase = in.readString();
        requestedForName = in.readString();
        creationTime = in.readLong();
        description = in.readString();
        try {
            requestType = RequestType.valueOf(in.readString());
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Failed get enum value", e);
        }
    }

    public static final Creator<FollowedActiveRequestItem> CREATOR = new Creator<FollowedActiveRequestItem>() {
        @Override
        public FollowedActiveRequestItem createFromParcel(Parcel in) {
            return new FollowedActiveRequestItem(in);
        }

        @Override
        public FollowedActiveRequestItem[] newArray(int size) {
            return new FollowedActiveRequestItem[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(phase);
        dest.writeString(metaPhase);
        dest.writeString(requestedForName);
        dest.writeLong(creationTime);
        dest.writeString(description);
        dest.writeString(requestType.toString());
    }
}
