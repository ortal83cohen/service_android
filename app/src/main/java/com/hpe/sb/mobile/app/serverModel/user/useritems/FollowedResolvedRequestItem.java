package com.hpe.sb.mobile.app.serverModel.user.useritems;

import com.hpe.sb.mobile.app.serverModel.comment.Comment;
import com.hpe.sb.mobile.app.serverModel.request.RequestType;

import android.os.Parcel;

public class FollowedResolvedRequestItem extends RequestResolveUserItem {

    private static final String TAG = "FollowedResolvedItem";


    public FollowedResolvedRequestItem() {
    }

    public FollowedResolvedRequestItem(String id, RequestType requestType, String title, String description, String solution, String requestedForName, long creationTime, Comment solutionComment) {
        this.id = id;
        this.requestType = requestType;
        this.title = title;
        this.description = description;
        this.solution = solution;
        this.requestedForName = requestedForName;
        this.creationTime = creationTime;
        this.solutionComment = solutionComment;
    }

    protected FollowedResolvedRequestItem(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        solution = in.readString();
        requestedForName = in.readString();
        creationTime = in.readLong();
        solutionComment = in.readParcelable(Comment.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(solution);
        dest.writeString(requestedForName);
        dest.writeLong(creationTime);
        dest.writeParcelable(solutionComment, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FollowedResolvedRequestItem> CREATOR
            = new Creator<FollowedResolvedRequestItem>() {
        @Override
        public FollowedResolvedRequestItem createFromParcel(Parcel in) {
            return new FollowedResolvedRequestItem(in);
        }

        @Override
        public FollowedResolvedRequestItem[] newArray(int size) {
            return new FollowedResolvedRequestItem[size];
        }
    };

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

    public RequestType getRequestType() {
        return requestType;
    }

    public void setRequestType(RequestType requestType) {
        this.requestType = requestType;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
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

    public Comment getSolutionComment() {
        return solutionComment;
    }

    public void setSolutionComment(Comment solutionComment) {
        this.solutionComment = solutionComment;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FollowedResolvedRequestItem that = (FollowedResolvedRequestItem) o;

        if (creationTime != that.creationTime) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (requestType != that.requestType) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (solution != null ? !solution.equals(that.solution) : that.solution != null) return false;
        if (requestedForName != null ? !requestedForName.equals(that.requestedForName) : that.requestedForName != null)
            return false;
        return solutionComment != null ? solutionComment.equals(that.solutionComment) : that.solutionComment == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (requestType != null ? requestType.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (solution != null ? solution.hashCode() : 0);
        result = 31 * result + (requestedForName != null ? requestedForName.hashCode() : 0);
        result = 31 * result + (int) (creationTime ^ (creationTime >>> 32));
        result = 31 * result + (solutionComment != null ? solutionComment.hashCode() : 0);
        return result;
    }
}
