package com.hpe.sb.mobile.app.features.googlepushnotification.model;

import com.hpe.sb.mobile.app.infra.dataClients.Persistable;

import java.util.HashMap;
import java.util.Map;

public class GcmRegistrations implements Persistable {

    public static final String DB_STATIC_ID = "GcmRegistrations";

    private String currentSenderId;
    private Map<String, GcmRegistrationPerSender> senderToGcmRegistrationMap;
    private Long lastSuccessfulRegistrationTime = null;

    public Long getLastSuccessfulRegistrationTime() {
        return lastSuccessfulRegistrationTime;
    }

    public void setLastSuccessfulRegistrationTime(Long time) {
        this.lastSuccessfulRegistrationTime = time;
    }

    public GcmRegistrations() {
        senderToGcmRegistrationMap = new HashMap<>();
    }

    public String getCurrentSenderId() {
        return currentSenderId;
    }

    public void setCurrentSenderId(String currentSenderId) {
        this.currentSenderId = currentSenderId;
    }

    public Map<String, GcmRegistrationPerSender> getSenderToGcmRegistrationMap() {
        return senderToGcmRegistrationMap;
    }

    public void setSenderToGcmRegistrationMap(Map<String, GcmRegistrationPerSender> senderToGcmRegistrationMap) {
        this.senderToGcmRegistrationMap = senderToGcmRegistrationMap;
    }

    public GcmRegistrationPerSender getGcmRegistrationForSender(String senderId) {
        return senderToGcmRegistrationMap.get(senderId);
    }

    public void setGcmRegistrationForSender(String senderId, GcmRegistrationPerSender gcmRegistrationPerSender) {
        senderToGcmRegistrationMap.put(senderId, gcmRegistrationPerSender);
    }

    @Override
    public String getId() {
        return DB_STATIC_ID;
    }

    @Override
    public int getChecksum() {
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        GcmRegistrations that = (GcmRegistrations) o;

        if (currentSenderId != null ? !currentSenderId.equals(that.currentSenderId) : that.currentSenderId != null) {
            return false;
        }
        return senderToGcmRegistrationMap != null ? senderToGcmRegistrationMap.equals(that.senderToGcmRegistrationMap) : that.senderToGcmRegistrationMap == null;

    }

    @Override
    public int hashCode() {
        int result = currentSenderId != null ? currentSenderId.hashCode() : 0;
        result = 31 * result + (senderToGcmRegistrationMap != null ? senderToGcmRegistrationMap.hashCode() : 0);
        return result;
    }
}
