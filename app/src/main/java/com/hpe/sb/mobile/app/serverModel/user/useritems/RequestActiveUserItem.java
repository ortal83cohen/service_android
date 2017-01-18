package com.hpe.sb.mobile.app.serverModel.user.useritems;

import com.hpe.sb.mobile.app.serverModel.request.RequestType;

import android.os.Parcel;
import android.util.Log;

public class RequestActiveUserItem extends UserItem {

    protected static final String TAG = RequestActiveUserItem.class.getSimpleName();

    protected String id;

    protected String title;

    protected RequestType requestType;

    protected String phase;

    protected String metaPhase;

    protected String requestedForName;

    protected long creationTime;

    protected String description;

    public RequestActiveUserItem() {
    }

    public RequestActiveUserItem(String id, String title, RequestType requestType, String phase, String metaPhase,
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhase() {
        return phase;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getRequestedForName() {
        return requestedForName;
    }

    public void setRequestedForName(String requestedForName) {
        this.requestedForName = requestedForName;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String getEntityType() {
        return "Request";
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public String getMetaPhase() {
        return metaPhase;
    }

    public void setMetaPhase(String metaPhase) {
        this.metaPhase = metaPhase;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RequestActiveUserItem that = (RequestActiveUserItem) o;

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

    protected RequestActiveUserItem(Parcel in) {
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

    public static final Creator<RequestActiveUserItem> CREATOR = new Creator<RequestActiveUserItem>() {
        @Override
        public RequestActiveUserItem createFromParcel(Parcel in) {
            return new RequestActiveUserItem(in);
        }

        @Override
        public RequestActiveUserItem[] newArray(int size) {
            return new RequestActiveUserItem[size];
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
