package com.hpe.sb.mobile.app.features.login.services;

import android.webkit.JavascriptInterface;

public class WebAppConnector {

    private ConnectionContextService connectionContextService;
    private Runnable afterConnectionContextUpdateRunnable;

    public WebAppConnector(ConnectionContextService connectionContextService, Runnable afterConnectionContextUpdateRunnable) {
        this.connectionContextService = connectionContextService;
        this.afterConnectionContextUpdateRunnable = afterConnectionContextUpdateRunnable;
    }

    @JavascriptInterface
    public void sendAppDetails(String protocol, String hostName, String port) {
        connectionContextService.updateHost(protocol, hostName, port);
        afterConnectionContextUpdateRunnable.run();
    }
}
