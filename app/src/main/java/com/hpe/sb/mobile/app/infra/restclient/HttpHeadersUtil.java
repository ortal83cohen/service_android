package com.hpe.sb.mobile.app.infra.restclient;

import java.util.Map;

/**
 * Created by mufler on 07/04/2016.
 *
 */
public interface HttpHeadersUtil {
    
    String COOKIE_KEY = "Cookie";
    String APPLICATION_KEY = "APPLICATION";
    String MOBILE_LOCALE_HEADER_NAME = "MOBILE_X-LOCALE_HEADER";

    Map<String, String> getHttpHeaders();
}
