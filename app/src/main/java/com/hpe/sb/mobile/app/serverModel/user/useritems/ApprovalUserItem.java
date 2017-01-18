package com.hpe.sb.mobile.app.serverModel.user.useritems;


import android.os.Parcel;

public class ApprovalUserItem extends UserItem {

    private String taskId;
    private String taskTitle;
    private String entityType;
    private String entityId;
    private String title;
    private String description;
    private String approveForName;
    private String approveForAvatarId;
    private long creationTime;
    private String cost;
    private String recurringCost;

    public ApprovalUserItem() {
    }

    public ApprovalUserItem(String description, String entityId, String entityType, String approveForName, String approveForAvatarId, String taskId, String taskTitle, String title, long creationTime, String cost, String recurringCost) {
        this.description = description;
        this.entityId = entityId;
        this.entityType = entityType;
        this.approveForName = approveForName;
        this.approveForAvatarId = approveForAvatarId;
        this.taskId = taskId;
        this.taskTitle = taskTitle;
        this.title = title;
        this.creationTime = creationTime;
        this.cost = cost;
        this.recurringCost = recurringCost;
    }

    public ApprovalUserItem(String description, String entityId, String entityType, String approveForName, String approveForAvatarId, String taskId, String taskTitle, String title, long creationTime) {
        this(description, entityId, entityType, approveForName, approveForAvatarId, taskId, taskTitle, title, creationTime, null, null);
    }

    protected ApprovalUserItem(Parcel in) {
        taskId = in.readString();
        entityType = in.readString();
        entityId = in.readString();
        title = in.readString();
        description = in.readString();
        approveForName = in.readString();
        approveForAvatarId = in.readString();
        creationTime = in.readLong();
        cost = in.readString();
    }

    public static final Creator<ApprovalUserItem> CREATOR = new Creator<ApprovalUserItem>() {
        @Override
        public ApprovalUserItem createFromParcel(Parcel in) {
            return new ApprovalUserItem(in);
        }

        @Override
        public ApprovalUserItem[] newArray(int size) {
            return new ApprovalUserItem[size];
        }
    };

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getApproveForName() {
        return approveForName;
    }

    public void setApproveForName(String approveForName) {
        this.approveForName = approveForName;
    }

    public String getApproveForAvatarId() {
        return approveForAvatarId;
    }

    public void setApproveForAvatarId(String approveForAvatarId) {
        this.approveForAvatarId = approveForAvatarId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String getId() {
        return getTaskId();
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

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getRecurringCost() {
        return recurringCost;
    }

    public void setRecurringCost(String recurringCost) {
        this.recurringCost = recurringCost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ApprovalUserItem that = (ApprovalUserItem) o;

        if (creationTime != that.creationTime) {
            return false;
        }
        if (taskId != null ? !taskId.equals(that.taskId) : that.taskId != null) {
            return false;
        }
        if (taskTitle != null ? !taskTitle.equals(that.taskTitle) : that.taskTitle != null) {
            return false;
        }
        if (entityType != null ? !entityType.equals(that.entityType) : that.entityType != null) {
            return false;
        }
        if (entityId != null ? !entityId.equals(that.entityId) : that.entityId != null) {
            return false;
        }
        if (title != null ? !title.equals(that.title) : that.title != null) {
            return false;
        }
        if (description != null ? !description.equals(that.description) : that.description != null) {
            return false;
        }
        if (approveForName != null ? !approveForName.equals(that.approveForName) : that.approveForName != null) {
            return false;
        }
        if (approveForAvatarId != null ? !approveForAvatarId.equals(that.approveForAvatarId) : that.approveForAvatarId != null) {
            return false;
        }
        if (cost != null ? !cost.equals(that.cost) : that.cost != null) {
            return false;
        }
        return recurringCost != null ? recurringCost.equals(that.recurringCost) : that.recurringCost == null;

    }

    @Override
    public int hashCode() {
        int result = taskId != null ? taskId.hashCode() : 0;
        result = 31 * result + (taskTitle != null ? taskTitle.hashCode() : 0);
        result = 31 * result + (entityType != null ? entityType.hashCode() : 0);
        result = 31 * result + (entityId != null ? entityId.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (approveForName != null ? approveForName.hashCode() : 0);
        result = 31 * result + (approveForAvatarId != null ? approveForAvatarId.hashCode() : 0);
        result = 31 * result + (int) (creationTime ^ (creationTime >>> 32));
        result = 31 * result + (cost != null ? cost.hashCode() : 0);
        result = 31 * result + (recurringCost != null ? recurringCost.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(taskId);
        dest.writeString(entityType);
        dest.writeString(entityId);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(approveForName);
        dest.writeString(approveForAvatarId);
        dest.writeLong(creationTime);
        dest.writeString(cost);
    }

}
