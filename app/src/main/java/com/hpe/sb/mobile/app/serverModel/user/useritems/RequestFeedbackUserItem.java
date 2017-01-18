package com.hpe.sb.mobile.app.serverModel.user.useritems;

import com.hpe.sb.mobile.app.serverModel.comment.Comment;
import com.hpe.sb.mobile.app.serverModel.request.RequestType;

import android.os.Parcel;
import android.util.Log;

public class RequestFeedbackUserItem extends UserItem {

    private static final String TAG = RequestFeedbackUserItem.class.getSimpleName();
    private String id;
    private String title;
    private RequestType requestType;
    private String description;
    private String metaPhase;
    private long creationTime;
    private String requestedForName;
    private Comment lastComment;

    public RequestFeedbackUserItem() {
    }

    public RequestFeedbackUserItem(String id, String title, RequestType requestType, String description, String metaPhase, Comment lastComment, long creationTime, String requestedForName) {
        this.id = id;
        this.title = title;
        this.requestType = requestType;
        this.description = description;
        this.metaPhase = metaPhase;
        this.lastComment = lastComment;
        this.creationTime = creationTime;
        this.requestedForName = requestedForName;
    }

    protected RequestFeedbackUserItem(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        metaPhase = in.readString();
        creationTime = in.readLong();
        requestedForName = in.readString();
        try {
            requestType = RequestType.valueOf(in.readString());
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Failed get enum value", e);
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(metaPhase);
        dest.writeLong(creationTime);
        dest.writeString(requestedForName);
        dest.writeString(requestType.toString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RequestFeedbackUserItem> CREATOR
            = new Creator<RequestFeedbackUserItem>() {
        @Override
        public RequestFeedbackUserItem createFromParcel(Parcel in) {
            return new RequestFeedbackUserItem(in);
        }

        @Override
        public RequestFeedbackUserItem[] newArray(int size) {
            return new RequestFeedbackUserItem[size];
        }
    };

    public String getMetaPhase() {
        return metaPhase;
    }

    public void setMetaPhase(String metaPhase) {
        this.metaPhase = metaPhase;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Comment getLastComment() {
        return lastComment;
    }

    public void setLastComment(Comment lastComment) {
        this.lastComment = lastComment;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public String getRequestedForName() {
        return requestedForName;
    }

    public void setRequestedForName(String requestedForName) {
        this.requestedForName = requestedForName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RequestFeedbackUserItem that = (RequestFeedbackUserItem) o;

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
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (metaPhase != null ? !metaPhase.equals(that.metaPhase) : that.metaPhase != null) {
            return false;
        }
        if (requestedForName != null ? !requestedForName.equals(that.requestedForName) : that.requestedForName != null) {
            return false;
        }
        return lastComment != null ? lastComment.equals(that.lastComment) : that.lastComment == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (requestType != null ? requestType.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (metaPhase != null ? metaPhase.hashCode() : 0);
        result = 31 * result + (int) (creationTime ^ (creationTime >>> 32));
        result = 31 * result + (requestedForName != null ? requestedForName.hashCode() : 0);
        result = 31 * result + (lastComment != null ? lastComment.hashCode() : 0);
        return result;
    }
}
