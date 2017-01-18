package com.hpe.sb.mobile.app.features.login.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SawHeadersDefinition extends AbstractHeadersDefinition {

    public static final String TENANT_ID_COOKIE_KEY = "TENANTID";
    public static final String LWSSO_COOKIE_KEY = "LWSSO_COOKIE_KEY";
    public static final String XSRF_TOKEN_COOKIE_KEY = "XSRF-TOKEN";
    public static final String OAUTH2_TOKEN_HEADER = "Authorization";
    public static final String OAUTH2_TOKEN_PREFIX = "Bearer ";
    public static final String OAUTH2_COOKIE_KEY = "OAUTH2_COOKIE_KEY";

    @Override
    public List<String> getMandatoryCookies() {
        List<String> mandatoryCookies = new ArrayList<>();
        mandatoryCookies.add(TENANT_ID_COOKIE_KEY);
        return mandatoryCookies;
    }

    @Override
    public List<String> getMandatoryHeaders() {
        List<String> optionalHeaders = new ArrayList<>();
        optionalHeaders.add(OAUTH2_TOKEN_HEADER);
        return optionalHeaders;
    }

    @Override
    public List<String> getOptionalCookies() {
        List<String> optionalCookies = new ArrayList<>();
        optionalCookies.add(XSRF_TOKEN_COOKIE_KEY);
        optionalCookies.add(LWSSO_COOKIE_KEY);
        return optionalCookies;
    }

    @Override
    public List<String> getMandatoryHeadersForLogin() {
        return Collections.emptyList();
    }

    @Override
    public List<String> getMandatoryCookiesForLogin() {
        List<String> mandatoryCookies = new ArrayList<>();
        mandatoryCookies.add(TENANT_ID_COOKIE_KEY);
        mandatoryCookies.add(LWSSO_COOKIE_KEY);
        return mandatoryCookies;
    }
}
