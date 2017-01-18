package com.hpe.sb.mobile.app.features.login.services;

public interface ActivationUrlService {


    void setActivationUrl(String text);

    String getActivationUrl();

    void updateSuccessfulLoginWithActivationUrl(boolean b);

    boolean isSuccessfulLoginWithActivationUrl();
}
