package com.hpe.sb.mobile.app.features.login.model;

import java.util.List;

public interface HeadersDefinition {

    List<String> getMandatoryHeaders();

    List<String> getMandatoryHeadersForLogin();

    List<String> getMandatoryCookiesForLogin();

    List<String> getOptionalHeaders();

    List<String> getHeaders();

    List<String> getMandatoryCookies();

    List<String> getOptionalCookies();

    List<String> getCookies();

}
