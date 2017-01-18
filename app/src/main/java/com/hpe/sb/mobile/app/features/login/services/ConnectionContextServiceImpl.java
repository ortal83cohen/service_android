package com.hpe.sb.mobile.app.features.login.services;

import android.content.Context;
import android.content.SharedPreferences;

import com.hpe.sb.mobile.app.common.dataClients.userContext.ApplicationType;
import com.hpe.sb.mobile.app.features.login.model.SawHeadersDefinition;
import com.hpe.sb.mobile.app.infra.encryption.EncryptionService;
import com.hpe.sb.mobile.app.features.login.model.HeadersDefinition;
import com.hpe.sb.mobile.app.features.login.model.ConnectionContext;
import com.hpe.sb.mobile.app.features.login.utils.CookieParser;

import java.util.*;

public class ConnectionContextServiceImpl implements ConnectionContextService {

    public static final String CONNECTION_CONTEXT_PREFERENCES_FILE = "com.hpe.sb.mobile.app.ConnectionContext";
    public static final String APP_NAME_KEY = "applicationType";
    public static final String HOST_NAME_KEY = "hostName";
    public static final String PORT_KEY = "port";
    public static final String PROTOCOL_NAME_KEY = "protocol";
    public static final String HEADERS_MAP_KEYS_PREFIX = "header_";
    public static final String COOKIES_MAP_KEYS_PREFIX = "cookie_";

    private Context context;
    private CookieParser cookieParser;
    private Map<ApplicationType, HeadersDefinition> headersDefinitionMap;
    private final EncryptionService encryptionService;

    private ConnectionContext connectionContext;
    private String webViewCookie;

    public ConnectionContextServiceImpl(Context context, CookieParser cookieParser,
                                        Map<ApplicationType, HeadersDefinition> headersDefinitionMap,
                                        EncryptionService encryptionService) {
        this.context = context;
        this.cookieParser = cookieParser;
        this.headersDefinitionMap = headersDefinitionMap;
        this.encryptionService = encryptionService;
    }

    public void initialize() {
        readConnectionContext();
        boolean isValid = isConnectionContextValid();
        if (!isValid) {
            connectionContext = new ConnectionContext();
        }
    }

    @Override
    public void updateApplicationType(ApplicationType applicationType) {
        Map<String, String> cookies;
        if (webViewCookie != null) {
            cookies = extractCookiesFromWebViewCookie(webViewCookie, applicationType);
        } else {
            cookies = connectionContext.getCookies();
        }
        connectionContext = new ConnectionContext(applicationType, connectionContext.getProtocol(), connectionContext.getHostName(),
                connectionContext.getPort(), connectionContext.getHeaders(), cookies);
    }

    @Override
    public void updateHost(String protocol, String hostName, String port) {
        connectionContext = new ConnectionContext(connectionContext.getApplicationType(), protocol,
                hostName, port, connectionContext.getHeaders(), connectionContext.getCookies());
    }

    @Override
    public void updateHeaders(Map<String, String> headers) {
        connectionContext = new ConnectionContext(connectionContext.getApplicationType(), connectionContext.getProtocol(),
                connectionContext.getHostName(), connectionContext.getPort(), headers, connectionContext.getCookies());
    }

    @Override
    public void updateCookies(Map<String, String> cookies) {
        connectionContext = new ConnectionContext(connectionContext.getApplicationType(), connectionContext.getProtocol(), connectionContext.getHostName(),
                connectionContext.getPort(), connectionContext.getHeaders(), cookies);
        webViewCookie = cookieParser.buildCookieStringFromCookieMap(cookies);
    }

    @Override
    public void updateCookie(String cookie) {
        ApplicationType applicationType = connectionContext.getApplicationType();
        if (applicationType != null) {
            Map<String, String> cookies = extractCookiesFromWebViewCookie(cookie, applicationType);
            updateCookies(cookies);
        }
        webViewCookie = cookie;
    }

    private Map<String, String> extractCookiesFromWebViewCookie(String cookie, ApplicationType applicationType) {
        Map<String, String> cookies = new HashMap<>();
        HeadersDefinition headersDefinition = headersDefinitionMap.get(applicationType);
        List<String> cookiesKeys = headersDefinition.getCookies();
        for (String cookieKey : cookiesKeys) {
            String cookieValue = cookieParser.getCookieValueByName(cookie, cookieKey);
            if (cookieValue != null && !cookieValue.isEmpty()) {
                cookies.put(cookieKey, cookieValue);
            }
        }
        return cookies;
    }

    @Override
    public boolean isConnectionContextValidForLogin() {
        if (connectionContext.getApplicationType() == null) {
            return false;
        }
        HeadersDefinition headersDefinition = headersDefinitionMap.get(connectionContext.getApplicationType());
        return isConnectionContextValidInternal(headersDefinition.getMandatoryHeadersForLogin(), headersDefinition.getMandatoryCookiesForLogin());
    }

    @Override
    public boolean isConnectionContextValid() {
        if (connectionContext.getApplicationType() == null) {
            return false;
        }
        HeadersDefinition headersDefinition = headersDefinitionMap.get(connectionContext.getApplicationType());
        return isConnectionContextValidInternal(headersDefinition.getMandatoryHeaders(), headersDefinition.getMandatoryCookies());
    }

    private boolean isConnectionContextValidInternal(List<String> mandatoryHeaders, List<String> mandatoryCookies) {
        if (connectionContext.getHostName() == null || connectionContext.getHostName().isEmpty()) {
            return false;
        }

        if (connectionContext.getProtocol() == null || connectionContext.getProtocol().isEmpty() ||
                (!connectionContext.getProtocol().equals("http") && !connectionContext.getProtocol().equals("https"))) {
            return false;
        }

        for (String headerName : mandatoryHeaders) {
            String headerValue = connectionContext.getHeaderValue(headerName);
            if (headerValue == null || headerValue.isEmpty()) {
                return false;
            }
        }

        for (String cookieKey : mandatoryCookies) {
            String cookieValue = connectionContext.getCookieValue(cookieKey);
            if (cookieValue == null || cookieValue.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    @Override
    public void storeConnectionContext() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CONNECTION_CONTEXT_PREFERENCES_FILE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(APP_NAME_KEY, connectionContext.getApplicationType().name());
        editor.putString(HOST_NAME_KEY, connectionContext.getHostName());
        editor.putString(PROTOCOL_NAME_KEY, connectionContext.getProtocol());

        if (connectionContext.getPort() != null) {
            editor.putString(PORT_KEY, connectionContext.getPort());
        }

        storeFlatMap(editor, connectionContext.getHeaders(), HEADERS_MAP_KEYS_PREFIX, sharedPreferences);
        storeFlatMap(editor, connectionContext.getCookies(), COOKIES_MAP_KEYS_PREFIX, sharedPreferences);

        editor.apply();
    }

    private void storeFlatMap(SharedPreferences.Editor editor, Map<String, String> map, String mapKeysPrefix,
                              SharedPreferences currentSharedPreferences) {
        Set<String> keysToAdd = new HashSet<>();
        for (String key : map.keySet()) {
            String value = map.get(key);
            if (value != null && !value.isEmpty()) {
                value = encryptionService.encrypt(value);
                editor.putString(mapKeysPrefix + key, value);
                keysToAdd.add(mapKeysPrefix + key);
            }
        }
        //remove those which are not in the new
        final Set<String> currentKeys = currentSharedPreferences.getAll().keySet();
        for (String currentKey : currentKeys) {
            if(currentKey.startsWith(mapKeysPrefix) && !keysToAdd.contains(currentKey)){
                editor.remove(currentKey);
            }
        }

    }

    @Override
    public void clearHeadersAndCookies() {
        connectionContext = new ConnectionContext(connectionContext.getApplicationType(), connectionContext.getProtocol(), connectionContext.getHostName(),
                connectionContext.getPort(), new HashMap<String, String>(), new HashMap<String, String>());
        storeConnectionContext();
    }

    @Override
    public ConnectionContext getConnectionContext() {
        return connectionContext;
     }

    private void readConnectionContext() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CONNECTION_CONTEXT_PREFERENCES_FILE,
                Context.MODE_PRIVATE);
        ApplicationType applicationType = null;
        String applicationTypeStr = sharedPreferences.getString(APP_NAME_KEY, null);
        String hostName = sharedPreferences.getString(HOST_NAME_KEY, null);
        String port = sharedPreferences.getString(PORT_KEY, null);
        String protocol = sharedPreferences.getString(PROTOCOL_NAME_KEY, ConnectionContext.DEFAULT_PROTOCOL);

        Map<String, String> headers = new HashMap<>();
        Map<String, String> cookies = new HashMap<>();
        if (applicationTypeStr != null && !applicationTypeStr.isEmpty()) {
            applicationType = ApplicationType.valueOf(applicationTypeStr);
            HeadersDefinition headersDefinition = headersDefinitionMap.get(applicationType);
            headers = readFlatMap(sharedPreferences, headersDefinition.getHeaders(), HEADERS_MAP_KEYS_PREFIX);
            cookies = readFlatMap(sharedPreferences, headersDefinition.getCookies(), COOKIES_MAP_KEYS_PREFIX);
        }

        connectionContext = new ConnectionContext(applicationType, protocol, hostName, port, headers, cookies);
    }

    private Map<String, String> readFlatMap(SharedPreferences sharedPreferences, List<String> mapKeys, String mapKeysPrefix) {
        Map<String, String> map = new HashMap<>();
        for (String key : mapKeys) {
            String value = sharedPreferences.getString(mapKeysPrefix + key, null);
            if (value != null && !value.isEmpty()) {
                value = encryptionService.decrypt(value);
                map.put(key, value);
            }
        }
        return map;
    }
}
