package com.hpe.sb.mobile.app.features.login.model;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractHeadersDefinition implements HeadersDefinition {

    @Override
    public List<String> getMandatoryHeaders() {
        return new ArrayList<>();
    }

    @Override
    public List<String> getOptionalHeaders() {
        return new ArrayList<>();
    }

    @Override
    public List<String> getHeaders() {
        List<String> headers = new ArrayList<>();
        headers.addAll(getMandatoryHeaders());
        headers.addAll(getOptionalHeaders());
        return headers;
    }

    @Override
    public List<String> getMandatoryCookies() {
        return new ArrayList<>();
    }

    @Override
    public List<String> getOptionalCookies() {
        return new ArrayList<>();
    }

    @Override
    public List<String> getCookies() {
        List<String> cookies = new ArrayList<>();
        cookies.addAll(getMandatoryCookies());
        cookies.addAll(getOptionalCookies());
        return cookies;
    }

    @Override
    public List<String> getMandatoryHeadersForLogin() {
        return getMandatoryHeaders();
    }

    @Override
    public List<String> getMandatoryCookiesForLogin() {
        return getMandatoryCookies();
    }
}
