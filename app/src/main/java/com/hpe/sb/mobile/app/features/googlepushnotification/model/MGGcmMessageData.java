package com.hpe.sb.mobile.app.features.googlepushnotification.model;

public class MGGcmMessageData {

    private MGGooglePushNotificationIdentifier googlePushNotificationIdentifier;
    private String offlineText;

    public MGGcmMessageData() {
    }

    public MGGcmMessageData(MGGooglePushNotificationIdentifier MGGooglePushNotificationIdentifier, String offlineText) {
        this.googlePushNotificationIdentifier = MGGooglePushNotificationIdentifier;
        this.offlineText = offlineText;
    }

    public MGGooglePushNotificationIdentifier getGooglePushNotificationIdentifier() {
        return googlePushNotificationIdentifier;
    }

    public void setGooglePushNotificationIdentifier(MGGooglePushNotificationIdentifier googlePushNotificationIdentifier) {
        this.googlePushNotificationIdentifier = googlePushNotificationIdentifier;
    }

    public String getOfflineText() {
        return offlineText;
    }

    public void setOfflineText(String offlineText) {
        this.offlineText = offlineText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MGGcmMessageData that = (MGGcmMessageData) o;

        if (googlePushNotificationIdentifier != null ? !googlePushNotificationIdentifier.equals(that.googlePushNotificationIdentifier) : that.googlePushNotificationIdentifier != null) {
            return false;
        }
        if (offlineText != null ? !offlineText.equals(that.offlineText) : that.offlineText != null) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = googlePushNotificationIdentifier != null ? googlePushNotificationIdentifier.hashCode() : 0;
        result = 31 * result + (offlineText != null ? offlineText.hashCode() : 0);
        return result;
    }
}
