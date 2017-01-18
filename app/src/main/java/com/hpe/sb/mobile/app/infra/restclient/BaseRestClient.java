package com.hpe.sb.mobile.app.infra.restclient;

/**
 * Created by malikdav on 20/03/2016.
 */
public abstract class BaseRestClient {

    protected HttpLookupUtils httpLookupUtils;

    protected RestClientQueue restClientQueue;

    public BaseRestClient(RestClientQueue restClientQueue, HttpLookupUtils httpLookupUtils) {
        this.restClientQueue = restClientQueue;
        this.httpLookupUtils = httpLookupUtils;
    }

    protected String getBaseRestUrl() {
        return httpLookupUtils.getBaseRestUrl(getClientPrefix());
    }

    public abstract String getClientPrefix();
}
