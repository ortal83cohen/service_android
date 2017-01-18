package com.hpe.sb.mobile.app.features.login.model;

import java.util.ArrayList;
import java.util.List;

public class PropelHeadersDefinition extends AbstractHeadersDefinition {

    public static final String AUTH_TOKEN_HEADER_NAME = "X-Auth-Token";
    public static final String REFRESH_TOKEN_HEADER_NAME = "X-Refresh-Token";
    public static final String TOKEN_EXPIRES_HEADER_NAME = "X-Token-Expires";

    @Override
    public List<String> getMandatoryHeaders() {
        List<String> mandatoryHeaders = new ArrayList<>();
        mandatoryHeaders.add(AUTH_TOKEN_HEADER_NAME);
        mandatoryHeaders.add(REFRESH_TOKEN_HEADER_NAME);
        mandatoryHeaders.add(TOKEN_EXPIRES_HEADER_NAME);
        return mandatoryHeaders;
    }
}
