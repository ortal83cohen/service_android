package com.hpe.sb.mobile.app.features.googlepushnotification.model;

public class MGGcmSender {

    private String senderId;

    public MGGcmSender() {
    }

    public MGGcmSender(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MGGcmSender that = (MGGcmSender) o;

        return senderId != null ? senderId.equals(that.senderId) : that.senderId == null;

    }

    @Override
    public int hashCode() {
        return senderId != null ? senderId.hashCode() : 0;
    }
}
