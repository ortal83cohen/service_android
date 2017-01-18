package com.hpe.sb.mobile.app.features.googlepushnotification.model;

import java.util.List;
import java.util.Map;

public class MGGooglePushNotificationData {

    private String pushNotificationType;
    private List<String> usersIds;
    private String entityType;
    private String entityId;
    private Map<String, String> entityData;
    private String offlineText;
    private long timestamp;

    public MGGooglePushNotificationData() {
    }

    public MGGooglePushNotificationData(String pushNotificationType, List<String> usersIds,
                                        String entityType, String entityId, Map<String, String> entityData,
                                        String offlineText, long timestamp) {
        this.pushNotificationType = pushNotificationType;
        this.usersIds = usersIds;
        this.entityType = entityType;
        this.entityId = entityId;
        this.entityData = entityData;
        this.offlineText = offlineText;
        this.timestamp = timestamp;
    }

    public String getPushNotificationType() {
        return pushNotificationType;
    }

    public void setPushNotificationType(String pushNotificationType) {
        this.pushNotificationType = pushNotificationType;
    }

    public List<String> getUsersIds() {
        return usersIds;
    }

    public void setUsersIds(List<String> usersIds) {
        this.usersIds = usersIds;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public Map<String, String> getEntityData() {
        return entityData;
    }

    public void setEntityData(Map<String, String> entityData) {
        this.entityData = entityData;
    }

    public String getOfflineText() {
        return offlineText;
    }

    public void setOfflineText(String offlineText) {
        this.offlineText = offlineText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MGGooglePushNotificationData that = (MGGooglePushNotificationData) o;

        if (timestamp != that.timestamp) {
            return false;
        }
        if (pushNotificationType != null ? !pushNotificationType.equals(that.pushNotificationType) : that.pushNotificationType != null) {
            return false;
        }
        if (usersIds != null ? !usersIds.equals(that.usersIds) : that.usersIds != null) {
            return false;
        }
        if (entityType != null ? !entityType.equals(that.entityType) : that.entityType != null) {
            return false;
        }
        if (entityId != null ? !entityId.equals(that.entityId) : that.entityId != null) {
            return false;
        }
        if (entityData != null ? !entityData.equals(that.entityData) : that.entityData != null) {
            return false;
        }
        if (offlineText != null ? !offlineText.equals(that.offlineText) : that.offlineText != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = pushNotificationType != null ? pushNotificationType.hashCode() : 0;
        result = 31 * result + (usersIds != null ? usersIds.hashCode() : 0);
        result = 31 * result + (entityType != null ? entityType.hashCode() : 0);
        result = 31 * result + (entityId != null ? entityId.hashCode() : 0);
        result = 31 * result + (entityData != null ? entityData.hashCode() : 0);
        result = 31 * result + (offlineText != null ? offlineText.hashCode() : 0);
        result = 31 * result + (int) (timestamp ^ (timestamp >>> 32));
        return result;
    }

}
