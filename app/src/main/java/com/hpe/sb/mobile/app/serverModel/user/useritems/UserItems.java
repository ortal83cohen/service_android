package com.hpe.sb.mobile.app.serverModel.user.useritems;


import com.hpe.sb.mobile.app.infra.dataClients.Persistable;

import android.os.Parcel;

import java.util.ArrayList;
import java.util.List;

public class UserItems extends UserItem implements Persistable {

    private List<ApprovalUserItem> approvals;

    private List<RequestResolveUserItem> requestResolveItems;

    private List<RequestFeedbackUserItem> requestFeedbackItems;

    private List<RequestActiveUserItem> activeRequests;

    private List<FollowedActiveRequestItem> followedActiveRequests;

    private List<FollowedResolvedRequestItem> followedResolvedRequest;


    public static final String USER_ITEMS_STATIC_ID = "userItems";

    public UserItems() {
    }

    public UserItems(List<RequestActiveUserItem> activeRequests,
            List<ApprovalUserItem> approvals,
            List<RequestFeedbackUserItem> requestFeedbackItems,
            List<RequestResolveUserItem> requestResolveItems,
            List<FollowedActiveRequestItem> followedActiveRequests,
            List<FollowedResolvedRequestItem> followedResolvedRequest) {
        this.activeRequests = activeRequests;
        this.approvals = approvals;
        this.requestFeedbackItems = requestFeedbackItems;
        this.requestResolveItems = requestResolveItems;
        this.followedActiveRequests = followedActiveRequests;
        this.followedResolvedRequest = followedResolvedRequest;
    }

    public List<FollowedActiveRequestItem> getFollowedActiveRequests() {
        return followedActiveRequests;
    }

    public void setFollowedActiveRequests(List<FollowedActiveRequestItem> followedActiveRequests) {
        this.followedActiveRequests = followedActiveRequests;
    }

    public List<FollowedResolvedRequestItem> getFollowedResolvedRequest() {
        return followedResolvedRequest;
    }

    public void setFollowedResolvedRequest(List<FollowedResolvedRequestItem> followedResolvedRequest) {
        this.followedResolvedRequest = followedResolvedRequest;
    }

    protected UserItems(Parcel in) {
        super(in);
        approvals = in.createTypedArrayList(ApprovalUserItem.CREATOR);
        requestResolveItems = in.createTypedArrayList(RequestResolveUserItem.CREATOR);
        requestFeedbackItems = in.createTypedArrayList(RequestFeedbackUserItem.CREATOR);
    }

    public static final Creator<UserItems> CREATOR = new Creator<UserItems>() {
        @Override
        public UserItems createFromParcel(Parcel in) {
            return new UserItems(in);
        }

        @Override
        public UserItems[] newArray(int size) {
            return new UserItems[size];
        }
    };

    public List<RequestActiveUserItem> getActiveRequests() {
        return activeRequests;
    }

    public void setActiveRequests(List<RequestActiveUserItem> activeRequests) {
        this.activeRequests = activeRequests;
    }

    public List<ApprovalUserItem> getApprovals() {
        return approvals;
    }

    public List<UserItem> getAllUserItems() {
        List<UserItem> userItemList = new ArrayList<>();
        userItemList.addAll(getApprovals());
        userItemList.addAll(getRequestFeedbackItems());
        userItemList.addAll(getRequestResolveItems());
        userItemList.addAll(getActiveRequests());
        userItemList.addAll(getFollowedActiveRequests());
        userItemList.addAll(getFollowedResolvedRequest());
        return userItemList;
    }

    public void setApprovals(List<ApprovalUserItem> approvals) {
        this.approvals = approvals;
    }

    public List<RequestResolveUserItem> getRequestResolveItems() {
        return requestResolveItems;
    }

    public void setRequestResolveItems(List<RequestResolveUserItem> requestResolveItems) {
        this.requestResolveItems = requestResolveItems;
    }

    public List<RequestFeedbackUserItem> getRequestFeedbackItems() {
        return requestFeedbackItems;
    }

    public void setRequestFeedbackItems(List<RequestFeedbackUserItem> requestFeedbackItems) {
        this.requestFeedbackItems = requestFeedbackItems;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserItems userItems = (UserItems) o;

        if (approvals != null ? !approvals.equals(userItems.approvals) : userItems.approvals != null) {
            return false;
        }
        if (requestResolveItems != null ? !requestResolveItems.equals(userItems.requestResolveItems) : userItems.requestResolveItems != null) {
            return false;
        }
        if (requestFeedbackItems != null ? !requestFeedbackItems.equals(userItems.requestFeedbackItems) : userItems.requestFeedbackItems != null) {
            return false;
        }
        if (activeRequests != null ? !activeRequests.equals(userItems.activeRequests) : userItems.activeRequests != null) {
            return false;
        }
        if (followedActiveRequests != null ? !followedActiveRequests.equals(userItems.followedActiveRequests) : userItems.followedActiveRequests != null) {
            return false;
        }
        return followedResolvedRequest != null ? followedResolvedRequest.equals(userItems.followedResolvedRequest) : userItems.followedResolvedRequest == null;

    }

    @Override
    public int hashCode() {
        int result = approvals != null ? approvals.hashCode() : 0;
        result = 31 * result + (requestResolveItems != null ? requestResolveItems.hashCode() : 0);
        result = 31 * result + (requestFeedbackItems != null ? requestFeedbackItems.hashCode() : 0);
        result = 31 * result + (activeRequests != null ? activeRequests.hashCode() : 0);
        result = 31 * result + (followedActiveRequests != null ? followedActiveRequests.hashCode() : 0);
        result = 31 * result + (followedResolvedRequest != null ? followedResolvedRequest.hashCode() : 0);
        return result;
    }

    @Override
    public String getId() {
        return USER_ITEMS_STATIC_ID;
    }

    @Override
    public int getChecksum() {
        return 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(approvals);
        dest.writeTypedList(requestResolveItems);
        dest.writeTypedList(requestFeedbackItems);
    }

    @Override
    public String getTitle() {
        return "";
    }


    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public String getEntityType() {
        return "";
    }


    @Override
    public String toString() {
        return "UserItems{" +
                "approvals=" + approvals +
                ", requestResolveItems=" + requestResolveItems +
                ", requestFeedbackItems=" + requestFeedbackItems +
                ", activeRequests=" + activeRequests +
                '}';
    }

}
