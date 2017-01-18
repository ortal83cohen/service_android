package com.hpe.sb.mobile.app.features.googlepushnotification.model;

public class GcmRegistrationPerSender {

    private String registrationId;
    private boolean isSentToServer;

    public GcmRegistrationPerSender() {
    }

    public GcmRegistrationPerSender(String registrationId, boolean isSentToServer) {
        this.registrationId = registrationId;
        this.isSentToServer = isSentToServer;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public boolean isSentToServer() {
        return isSentToServer;
    }

    public void setIsSentToServer(boolean sentToServer) {
        isSentToServer = sentToServer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GcmRegistrationPerSender that = (GcmRegistrationPerSender) o;

        if (isSentToServer != that.isSentToServer) {
            return false;
        }
        return registrationId != null ? registrationId.equals(that.registrationId) : that.registrationId == null;

    }

    @Override
    public int hashCode() {
        int result = registrationId != null ? registrationId.hashCode() : 0;
        result = 31 * result + (isSentToServer ? 1 : 0);
        return result;
    }
}
