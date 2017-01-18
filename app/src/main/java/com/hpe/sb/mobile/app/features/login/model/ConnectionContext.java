package com.hpe.sb.mobile.app.features.login.model;

import com.hpe.sb.mobile.app.common.dataClients.userContext.ApplicationType;

import java.util.HashMap;
import java.util.Map;

public class ConnectionContext {

    public static final String DEFAULT_PROTOCOL = "https";

    private ApplicationType applicationType;
    private String hostName;
    private String port;
    private Map<String, String> headers;
    private Map<String, String> cookies;
    private String protocol;

    public ConnectionContext() {
        this(null, DEFAULT_PROTOCOL, null, null);
    }

    public ConnectionContext(ApplicationType applicationType, String protocol, String hostName, String port) {
        this(applicationType, protocol, hostName, port, new HashMap<String, String>(), new HashMap<String, String>());
    }

    public ConnectionContext(ApplicationType applicationType, String protocol, String hostName, String port, Map<String, String> headers, Map<String, String> cookies) {
        this.applicationType = applicationType;
        this.hostName = hostName;
        this.port = port;
        this.headers = headers;
        this.cookies = cookies;
        this.protocol = protocol;
    }

    public ApplicationType getApplicationType() {
        return applicationType;
    }

    public String getHostName() {
        return hostName;
    }

    public String getPort() {
        return port;
    }

    public Map<String, String> getHeaders() {
        Map<String, String> headersCopy = new HashMap<>();
        headersCopy.putAll(headers);
        return headersCopy;
    }

    public String getHeaderValue(String headerName) {
        return headers.get(headerName);
    }

    public Map<String, String> getCookies() {
        Map<String, String> cookiesCopy = new HashMap<>();
        cookiesCopy.putAll(cookies);
        return cookiesCopy;
    }

    public String getCookieValue(String cookieName) {
        return cookies.get(cookieName);
    }

    public String getProtocol() {
        return protocol;
    }

    public String getLongToken(){
        return headers.get(SawHeadersDefinition.OAUTH2_TOKEN_HEADER).replace( SawHeadersDefinition.OAUTH2_TOKEN_PREFIX,"");
    }

}