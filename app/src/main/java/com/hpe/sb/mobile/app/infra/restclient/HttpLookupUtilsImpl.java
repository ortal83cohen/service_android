package com.hpe.sb.mobile.app.infra.restclient;

import com.hpe.sb.mobile.app.common.dataClients.userContext.ApplicationType;
import com.hpe.sb.mobile.app.features.login.model.ConnectionContext;
import com.hpe.sb.mobile.app.features.login.model.SawHeadersDefinition;
import com.hpe.sb.mobile.app.features.login.services.ConnectionContextService;

/**
 * Created by mufler on 06/04/2016.
 */
public class HttpLookupUtilsImpl implements HttpLookupUtils{
    private static final String REST = "rest";
    private static final String MOBILE = "mobile";
    private static final String MOBILE_WEBAPP = "mobile-webapp";

    private ConnectionContextService connectionContextService;

    public HttpLookupUtilsImpl(ConnectionContextService connectionContextService){
        this.connectionContextService = connectionContextService;
    }

    @Override
    public String getBaseRestUrl(String clientPrefix) {
        ConnectionContext connectionContext = connectionContextService.getConnectionContext();
        String hostName = connectionContext.getHostName();
        String port = connectionContext.getPort();
        String protocol = connectionContext.getProtocol();
        ApplicationType application = connectionContext.getApplicationType();

        return buildRestUrl(protocol, hostName, port, clientPrefix,application);
    }

    @Override
    public String getBaseRestUrl() {
        return getBaseRestUrl("");
    }

    @Override
    public String getBaseUrl() {
        ConnectionContext connectionContext = connectionContextService.getConnectionContext();
        String protocol = connectionContext.getProtocol();
        String hostName = connectionContext.getHostName();
        String port = connectionContext.getPort();
        return buildUrl(protocol, hostName, port);
    }

    private String buildRestUrl(String protocol, String hostName, String port, String clientPrefix, ApplicationType applicationType) {
        if (applicationType.equals(ApplicationType.DEMO)){
            return  buildUrl(protocol, hostName, port) + "/" + MOBILE_WEBAPP + "/" + REST + "/" + "1" + "/" + clientPrefix;
        }else {
            String tenantId = connectionContextService.getConnectionContext().getCookieValue(SawHeadersDefinition.TENANT_ID_COOKIE_KEY);
            return buildUrl(protocol, hostName, port) + "/" + REST + "/" + tenantId + "/" + MOBILE + "/" + clientPrefix;
        }
    }

    private String buildUrl(String protocol, String hostName, String port) {
        String url = protocol + "://" + hostName;
        if (port != null) {
            url = url + ":" + port;
        }

        return url;
    }
}
