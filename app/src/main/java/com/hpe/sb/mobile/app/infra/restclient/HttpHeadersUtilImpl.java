package com.hpe.sb.mobile.app.infra.restclient;

import com.hpe.sb.mobile.app.common.dataClients.userContext.ApplicationType;
import com.hpe.sb.mobile.app.common.dataClients.userContext.UserContextService;
import com.hpe.sb.mobile.app.features.login.model.ConnectionContext;
import com.hpe.sb.mobile.app.features.login.services.ConnectionContextService;
import com.hpe.sb.mobile.app.features.login.utils.CookieParser;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by malikdav on 25/03/2016.
 *
 */
public class HttpHeadersUtilImpl implements HttpHeadersUtil {

    private ConnectionContextService connectionContextService;
    private CookieParser cookieParser;
    private UserContextService userContextService;

    public HttpHeadersUtilImpl(ConnectionContextService connectionContextService, CookieParser cookieParser, UserContextService userContextService) {
        this.connectionContextService = connectionContextService;
        this.cookieParser = cookieParser;
        this.userContextService = userContextService;
    }

    @Override
    public Map<String, String> getHttpHeaders() {
        ConnectionContext connectionContext = connectionContextService.getConnectionContext();

        Map<String, String> httpHeaders = new HashMap<>();

        ApplicationType applicationType = connectionContext.getApplicationType();
        httpHeaders.put(APPLICATION_KEY, applicationType.name());

        Map<String, String> headers = connectionContext.getHeaders();
        httpHeaders.putAll(headers);

        httpHeaders.put(MOBILE_LOCALE_HEADER_NAME, userContextService.getLocale());

        Map<String, String> cookies = connectionContext.getCookies();
        if (!cookies.isEmpty()) {
            httpHeaders.put(COOKIE_KEY, cookieParser.buildCookieStringFromCookieMap(cookies));
        }

        return httpHeaders;
    }
}
