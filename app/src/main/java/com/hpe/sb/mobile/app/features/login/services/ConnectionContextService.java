package com.hpe.sb.mobile.app.features.login.services;

import com.hpe.sb.mobile.app.common.dataClients.userContext.ApplicationType;
import com.hpe.sb.mobile.app.features.login.model.ConnectionContext;

import android.text.Editable;

import java.util.Map;

public interface ConnectionContextService {

    /**
     * Sets the application type of the connection context
     * @param applicationType - the application type
     */
    void updateApplicationType(ApplicationType applicationType);

    /**
     * Sets the protocol, host name and port of the connection context
     * @param protocol - protocol
     * @param hostName - host name
     * @param port - port
     */
    void updateHost(String protocol, String hostName, String port);

    /**
     * Sets the headers of the connection context
     * @param headers - http/https headers
     */
    void updateHeaders(Map<String, String> headers);


    /**
     * Sets the cookies for the connection context. The new cookies in the map
     * replace the existing cookies.
     * @param cookies
     */
    void updateCookies(Map<String, String> cookies);

    /**
     * Sets the cookie of the connection context. The new cookie replaces the old one.
     * Given an application type, parse the cookie string and update the connection
     * context with the relevant cookies.
     * @param cookie - cookie of the format: key1=value1;key2=value2;..
     */
    void updateCookie(String cookie);

    /**
     * Validates if the connection context is initialized for the first step in login.
     * (in SAW: creating short-live token)
     * @return true if the connection context contains all mandatory properties fro initial login. Otherwise, return false.
     */
    boolean isConnectionContextValidForLogin();

    /**
     * Validates the connection context.
     * @return true if the connection context contains all mandatory properties. Otherwise, return false.
     */
    boolean isConnectionContextValid();

    /**
     * Store the connection context in the shared preferences.
     */
    void storeConnectionContext();

    /**
     * Deletes the Headers and Cookies.
     */
    void clearHeadersAndCookies();

    /**
     * Get the ConnectionContext of the application.
     * @return the ConnectionContext of the application if it contains all mandatory properties. Otherwise, return null.
     */
    ConnectionContext getConnectionContext();

}
