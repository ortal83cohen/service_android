package com.hpe.sb.mobile.app.features.googlepushnotification.model;

public class MGGooglePushNotificationIdentifier {

    private String pushNotificationType;
    private String pushNotificationId;
    private String entityType;
    private String entityId;

    public MGGooglePushNotificationIdentifier() {
    }

    public MGGooglePushNotificationIdentifier(String pushNotificationType, String pushNotificationId,
                                              String entityType, String entityId) {
        this.pushNotificationType = pushNotificationType;
        this.pushNotificationId = pushNotificationId;
        this.entityType = entityType;
        this.entityId = entityId;
    }

    public String getPushNotificationType() {
        return pushNotificationType;
    }

    public void setPushNotificationType(String pushNotificationType) {
        this.pushNotificationType = pushNotificationType;
    }

    public String getPushNotificationId() {
        return pushNotificationId;
    }

    public void setPushNotificationId(String pushNotificationId) {
        this.pushNotificationId = pushNotificationId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MGGooglePushNotificationIdentifier that = (MGGooglePushNotificationIdentifier) o;

        if (pushNotificationType != null ? !pushNotificationType.equals(that.pushNotificationType) : that.pushNotificationType != null) {
            return false;
        }
        if (pushNotificationId != null ? !pushNotificationId.equals(that.pushNotificationId) : that.pushNotificationId != null) {
            return false;
        }
        if (entityType != null ? !entityType.equals(that.entityType) : that.entityType != null) {
            return false;
        }
        if (entityId != null ? !entityId.equals(that.entityId) : that.entityId != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = pushNotificationType != null ? pushNotificationType.hashCode() : 0;
        result = 31 * result + (pushNotificationId != null ? pushNotificationId.hashCode() : 0);
        result = 31 * result + (entityType != null ? entityType.hashCode() : 0);
        result = 31 * result + (entityId != null ? entityId.hashCode() : 0);
        return result;
    }
}
