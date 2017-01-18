package com.hpe.sb.mobile.app.serverModel.user;

/**
 * Created by Sergey Steblin on 10/08/2016.
 */
public class LogoutData {

    public LogoutData(String token,String registrationId) {
        this.token = token;
        this.registrationId = registrationId;
    }

    private String token;

    private String registrationId;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        LogoutData that = (LogoutData) o;

        if (token != null ? !token.equals(that.token) : that.token != null) {
            return false;
        }
        return registrationId != null ? registrationId.equals(that.registrationId) : that.registrationId == null;

    }

    @Override
    public int hashCode() {
        int result = token != null ? token.hashCode() : 0;
        result = 31 * result + (registrationId != null ? registrationId.hashCode() : 0);
        return result;
    }
}
