package com.hpe.sb.mobile.app.infra.restclient;

/**
 * Created by mufler on 06/04/2016.
 */
public interface HttpLookupUtils {
    String getBaseRestUrl(String clientPrefix);

    String getBaseRestUrl();

    String getBaseUrl();
}
