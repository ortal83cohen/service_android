package com.hpe.sb.mobile.app.features.login.services;

import android.content.Context;
import android.content.SharedPreferences;
import com.hpe.sb.mobile.app.common.dataClients.userContext.ApplicationType;
import com.hpe.sb.mobile.app.infra.encryption.EncryptionService;
import com.hpe.sb.mobile.app.features.login.model.AbstractHeadersDefinition;
import com.hpe.sb.mobile.app.features.login.model.ConnectionContext;
import com.hpe.sb.mobile.app.features.login.model.HeadersDefinition;
import com.hpe.sb.mobile.app.features.login.utils.CookieParser;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.*;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ConnectionContextServiceImplTest {

    public static final String MANDATORY_HEADER = "mandatoryHeader";
    public static final String OPTIONAL_HEADER = "optionalHeader";
    public static final String MANDATORY_COOKIE = "mandatoryCookie";
    public static final String OPTIONAL_COOKIE = "optionalCookie";

    public static final String SAW_MANDATORY_HEADER_NAME = ApplicationType.SAW + MANDATORY_HEADER;
    public static final String SAW_OPTIONAL_HEADER_NAME = ApplicationType.SAW + OPTIONAL_HEADER;
    public static final String SAW_MANDATORY_COOKIE_NAME = ApplicationType.SAW + MANDATORY_COOKIE;
    public static final String SAW_OPTIONAL_COOKIE_NAME = ApplicationType.SAW + OPTIONAL_COOKIE;

    public static final String PROPEL_MANDATORY_HEADER_NAME = ApplicationType.PROPEL + MANDATORY_HEADER;
    public static final String PROPEL_OPTIONAL_HEADER_NAME = ApplicationType.PROPEL + OPTIONAL_HEADER;
    public static final String PROPEL_MANDATORY_COOKIE_NAME = ApplicationType.PROPEL + MANDATORY_COOKIE;
    public static final String PROPEL_OPTIONAL_COOKIE_NAME = ApplicationType.PROPEL + OPTIONAL_COOKIE;

    public static final String SAW_MANDATORY_HEADER_PREF_KEY = ConnectionContextServiceImpl.HEADERS_MAP_KEYS_PREFIX + SAW_MANDATORY_HEADER_NAME;
    public static final String SAW_OPTIONAL_HEADER_PREF_KEY = ConnectionContextServiceImpl.HEADERS_MAP_KEYS_PREFIX + SAW_OPTIONAL_HEADER_NAME;
    public static final String SAW_MANDATORY_COOKIE_PREF_KEY = ConnectionContextServiceImpl.COOKIES_MAP_KEYS_PREFIX + SAW_MANDATORY_COOKIE_NAME;
    public static final String SAW_OPTIONAL_COOKIE_PREF_KEY = ConnectionContextServiceImpl.COOKIES_MAP_KEYS_PREFIX + SAW_OPTIONAL_COOKIE_NAME;

    public static final String PROPEL_MANDATORY_HEADER_PREF_KEY = ConnectionContextServiceImpl.HEADERS_MAP_KEYS_PREFIX + PROPEL_MANDATORY_HEADER_NAME;
    public static final String PROPEL_OPTIONAL_HEADER_PREF_KEY = ConnectionContextServiceImpl.HEADERS_MAP_KEYS_PREFIX + PROPEL_OPTIONAL_HEADER_NAME;
    public static final String PROPEL_MANDATORY_COOKIE_PREF_KEY = ConnectionContextServiceImpl.COOKIES_MAP_KEYS_PREFIX + PROPEL_MANDATORY_COOKIE_NAME;
    public static final String PROPEL_OPTIONAL_COOKIE_PREF_KEY = ConnectionContextServiceImpl.COOKIES_MAP_KEYS_PREFIX + PROPEL_OPTIONAL_COOKIE_NAME;

    public static final String PROTOCOL = "http";
    public static final String HOST_NAME = "hostName";
    public static final String PORT = "8080";

    public static final String SAW_MANDATORY_HEADER_VALUE = "VALUE_SAW_mandatoryHeader";
    public static final String SAW_OPTIONAL_HEADER_VALUE = "VALUE_SAW_optionalHeader";
    public static final String SAW_MANDATORY_COOKIE_VALUE = "VALUE_SAW_mandatoryCookie";
    public static final String SAW_OPTIONAL_COOKIE_VALUE = "VALUE_SAW_optionalCookie";

    public static final String PROPEL_MANDATORY_HEADER_VALUE = "VALUE_PROPEL_mandatoryHeader";
    public static final String PROPEL_OPTIONAL_HEADER_VALUE = "VALUE_PROPEL_optionalHeader";
    public static final String PROPEL_MANDATORY_COOKIE_VALUE = "VALUE_PROPEL_mandatoryCookie";
    public static final String PROPEL_OPTIONAL_COOKIE_VALUE = "VALUE_PROPEL_optionalCookie";

    private Context contextMock;
    private CookieParser cookieParser;
    private EncryptionService encryptionService;

    @Before
    public void setUp() throws Exception {
        contextMock = mock(Context.class);
        cookieParser = new CookieParser();
        setupEncryptionServiceMock();
    }

    private void setupEncryptionServiceMock() {
        encryptionService = mock(EncryptionService.class);
        when(encryptionService.encrypt(anyString())).then(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return (String) invocation.getArguments()[0];
            }
        });
        when(encryptionService.decrypt(anyString())).then(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                return (String) invocation.getArguments()[0];
            }
        });
    }

    @Test
    public void testInitialize_readEmptyConnectionContext() throws Exception {
        ConnectionContextServiceImpl connectionContextService = new ConnectionContextServiceImpl(contextMock, cookieParser, createHeadersDefinitionMap(ApplicationType.SAW), encryptionService);

        SharedPreferences sharedPreferencesMock = mock(SharedPreferences.class);
        when(contextMock.getSharedPreferences(ConnectionContextServiceImpl.CONNECTION_CONTEXT_PREFERENCES_FILE,
                Context.MODE_PRIVATE)).thenReturn(sharedPreferencesMock);

        connectionContextService.initialize();

        verify(sharedPreferencesMock).getString(ConnectionContextServiceImpl.APP_NAME_KEY, null);
        verify(sharedPreferencesMock).getString(ConnectionContextServiceImpl.HOST_NAME_KEY, null);
        verify(sharedPreferencesMock).getString(ConnectionContextServiceImpl.PORT_KEY, null);
        verify(sharedPreferencesMock, never()).getString(SAW_MANDATORY_HEADER_PREF_KEY, null);
        verify(sharedPreferencesMock, never()).getString(SAW_OPTIONAL_HEADER_PREF_KEY, null);
        verify(sharedPreferencesMock, never()).getString(SAW_MANDATORY_COOKIE_PREF_KEY, null);
        verify(sharedPreferencesMock, never()).getString(SAW_OPTIONAL_COOKIE_PREF_KEY, null);

        ConnectionContext connectionContext = connectionContextService.getConnectionContext();
        Assert.assertNotNull(connectionContext);
        Assert.assertFalse(connectionContextService.isConnectionContextValidForLogin());
        Assert.assertFalse(connectionContextService.isConnectionContextValid());
    }

    @Test
    public void testInitialize_readPartialConnectionContext_missingMandatoryHeader() throws Exception {
        ConnectionContextServiceImpl connectionContextService = new ConnectionContextServiceImpl(contextMock, cookieParser, createHeadersDefinitionMap(ApplicationType.SAW), encryptionService);

        SharedPreferences sharedPreferencesMock = mock(SharedPreferences.class);
        when(contextMock.getSharedPreferences(ConnectionContextServiceImpl.CONNECTION_CONTEXT_PREFERENCES_FILE,
                Context.MODE_PRIVATE)).thenReturn(sharedPreferencesMock);

        when(sharedPreferencesMock.getString(ConnectionContextServiceImpl.APP_NAME_KEY, null)).thenReturn(ApplicationType.SAW.name());
        when(sharedPreferencesMock.getString(ConnectionContextServiceImpl.HOST_NAME_KEY, null)).thenReturn(HOST_NAME);
        when(sharedPreferencesMock.getString(ConnectionContextServiceImpl.PORT_KEY, null)).thenReturn(null);
        when(sharedPreferencesMock.getString(SAW_MANDATORY_HEADER_PREF_KEY, null)).thenReturn(null);
        when(sharedPreferencesMock.getString(SAW_OPTIONAL_HEADER_PREF_KEY, null)).thenReturn(null);
        when(sharedPreferencesMock.getString(SAW_MANDATORY_COOKIE_PREF_KEY, null)).thenReturn(SAW_MANDATORY_COOKIE_VALUE);
        when(sharedPreferencesMock.getString(SAW_OPTIONAL_COOKIE_PREF_KEY, null)).thenReturn(null);

        connectionContextService.initialize();

        verify(sharedPreferencesMock).getString(ConnectionContextServiceImpl.APP_NAME_KEY, null);
        verify(sharedPreferencesMock).getString(ConnectionContextServiceImpl.HOST_NAME_KEY, null);
        verify(sharedPreferencesMock).getString(ConnectionContextServiceImpl.PORT_KEY, null);
        verify(sharedPreferencesMock).getString(SAW_MANDATORY_HEADER_PREF_KEY, null);
        verify(sharedPreferencesMock).getString(SAW_OPTIONAL_HEADER_PREF_KEY, null);
        verify(sharedPreferencesMock).getString(SAW_MANDATORY_COOKIE_PREF_KEY, null);
        verify(sharedPreferencesMock).getString(SAW_OPTIONAL_COOKIE_PREF_KEY, null);

        ConnectionContext connectionContext = connectionContextService.getConnectionContext();
        Assert.assertNotNull(connectionContext);
        Assert.assertFalse(connectionContextService.isConnectionContextValidForLogin());
        Assert.assertFalse(connectionContextService.isConnectionContextValid());
    }

    @Test
    public void testInitialize_readPartialConnectionContext_missingMandatoryCookie() throws Exception {
        ConnectionContextServiceImpl connectionContextService = new ConnectionContextServiceImpl(contextMock, cookieParser, createHeadersDefinitionMap(ApplicationType.SAW), encryptionService);

        SharedPreferences sharedPreferencesMock = mock(SharedPreferences.class);
        when(contextMock.getSharedPreferences(ConnectionContextServiceImpl.CONNECTION_CONTEXT_PREFERENCES_FILE,
                Context.MODE_PRIVATE)).thenReturn(sharedPreferencesMock);

        when(sharedPreferencesMock.getString(ConnectionContextServiceImpl.APP_NAME_KEY, null)).thenReturn(ApplicationType.SAW.name());
        when(sharedPreferencesMock.getString(ConnectionContextServiceImpl.HOST_NAME_KEY, null)).thenReturn(HOST_NAME);
        when(sharedPreferencesMock.getString(ConnectionContextServiceImpl.PORT_KEY, null)).thenReturn(PORT);
        when(sharedPreferencesMock.getString(SAW_MANDATORY_HEADER_PREF_KEY, null)).thenReturn(SAW_MANDATORY_HEADER_VALUE);
        when(sharedPreferencesMock.getString(SAW_OPTIONAL_HEADER_PREF_KEY, null)).thenReturn(null);
        when(sharedPreferencesMock.getString(SAW_MANDATORY_COOKIE_PREF_KEY, null)).thenReturn(null);
        when(sharedPreferencesMock.getString(SAW_OPTIONAL_COOKIE_PREF_KEY, null)).thenReturn(null);

        connectionContextService.initialize();

        verify(sharedPreferencesMock).getString(ConnectionContextServiceImpl.APP_NAME_KEY, null);
        verify(sharedPreferencesMock).getString(ConnectionContextServiceImpl.HOST_NAME_KEY, null);
        verify(sharedPreferencesMock).getString(ConnectionContextServiceImpl.PORT_KEY, null);
        verify(sharedPreferencesMock).getString(SAW_MANDATORY_HEADER_PREF_KEY, null);
        verify(sharedPreferencesMock).getString(SAW_OPTIONAL_HEADER_PREF_KEY, null);
        verify(sharedPreferencesMock).getString(SAW_MANDATORY_COOKIE_PREF_KEY, null);
        verify(sharedPreferencesMock).getString(SAW_OPTIONAL_COOKIE_PREF_KEY, null);

        ConnectionContext connectionContext = connectionContextService.getConnectionContext();
        Assert.assertNotNull(connectionContext);
        Assert.assertFalse(connectionContextService.isConnectionContextValidForLogin());
        Assert.assertFalse(connectionContextService.isConnectionContextValid());
    }

    @Test
    public void testInitialize_readValidConnectionContext() throws Exception {
        ConnectionContextServiceImpl connectionContextService = new ConnectionContextServiceImpl(contextMock, cookieParser, createHeadersDefinitionMap(ApplicationType.SAW), encryptionService);

        SharedPreferences sharedPreferencesMock = mock(SharedPreferences.class);
        when(contextMock.getSharedPreferences(ConnectionContextServiceImpl.CONNECTION_CONTEXT_PREFERENCES_FILE,
                Context.MODE_PRIVATE)).thenReturn(sharedPreferencesMock);

        when(sharedPreferencesMock.getString(ConnectionContextServiceImpl.APP_NAME_KEY, null)).thenReturn(ApplicationType.SAW.name());
        when(sharedPreferencesMock.getString(ConnectionContextServiceImpl.HOST_NAME_KEY, null)).thenReturn(HOST_NAME);
        when(sharedPreferencesMock.getString(ConnectionContextServiceImpl.PORT_KEY, null)).thenReturn(PORT);
        when(sharedPreferencesMock.getString(ConnectionContextServiceImpl.PROTOCOL_NAME_KEY, ConnectionContext.DEFAULT_PROTOCOL)).thenReturn(PROTOCOL);
        when(sharedPreferencesMock.getString(SAW_MANDATORY_HEADER_PREF_KEY, null)).thenReturn(SAW_MANDATORY_HEADER_VALUE);
        when(sharedPreferencesMock.getString(SAW_OPTIONAL_HEADER_PREF_KEY, null)).thenReturn(null);
        when(sharedPreferencesMock.getString(SAW_MANDATORY_COOKIE_PREF_KEY, null)).thenReturn(SAW_MANDATORY_COOKIE_VALUE);
        when(sharedPreferencesMock.getString(SAW_OPTIONAL_COOKIE_PREF_KEY, null)).thenReturn(SAW_OPTIONAL_COOKIE_VALUE);

        connectionContextService.initialize();

        verify(sharedPreferencesMock).getString(ConnectionContextServiceImpl.APP_NAME_KEY, null);
        verify(sharedPreferencesMock).getString(ConnectionContextServiceImpl.HOST_NAME_KEY, null);
        verify(sharedPreferencesMock).getString(ConnectionContextServiceImpl.PORT_KEY, null);
        verify(sharedPreferencesMock).getString(ConnectionContextServiceImpl.PROTOCOL_NAME_KEY, ConnectionContext.DEFAULT_PROTOCOL);
        verify(sharedPreferencesMock).getString(SAW_MANDATORY_HEADER_PREF_KEY, null);
        verify(sharedPreferencesMock).getString(SAW_OPTIONAL_HEADER_PREF_KEY, null);
        verify(sharedPreferencesMock).getString(SAW_MANDATORY_COOKIE_PREF_KEY, null);
        verify(sharedPreferencesMock).getString(SAW_OPTIONAL_COOKIE_PREF_KEY, null);

        ConnectionContext connectionContext = connectionContextService.getConnectionContext();
        Assert.assertNotNull(connectionContext);
        Assert.assertEquals(ApplicationType.SAW, connectionContext.getApplicationType());
        Assert.assertEquals(HOST_NAME, connectionContext.getHostName());
        Assert.assertEquals(PORT, connectionContext.getPort());
        Assert.assertEquals(PROTOCOL, connectionContext.getProtocol());

        Map<String, String> headers = connectionContext.getHeaders();
        Assert.assertEquals(1, headers.size());
        Assert.assertEquals(SAW_MANDATORY_HEADER_VALUE, headers.get(SAW_MANDATORY_HEADER_NAME));

        Map<String, String> cookies = connectionContext.getCookies();
        Assert.assertEquals(2, cookies.size());
        Assert.assertEquals(SAW_MANDATORY_COOKIE_VALUE, cookies.get(SAW_MANDATORY_COOKIE_NAME));
        Assert.assertEquals(SAW_OPTIONAL_COOKIE_VALUE, cookies.get(SAW_OPTIONAL_COOKIE_NAME));
    }

    @Test
    public void testIsConnectionContextValid_withoutApplicationType() throws Exception {
        Map<String, String> headers = Collections.singletonMap(SAW_MANDATORY_HEADER_NAME, SAW_MANDATORY_HEADER_VALUE);
        String cookie = SAW_MANDATORY_COOKIE_NAME + "=" + SAW_MANDATORY_COOKIE_VALUE;
        ConnectionContextServiceImpl connectionContextService = buildConnectionContextServiceImpl(null, PROTOCOL, HOST_NAME, PORT, headers, cookie);
        Assert.assertFalse(connectionContextService.isConnectionContextValid());
    }

    @Test
    public void testIsConnectionContextValid_withoutHost() throws Exception {
        Map<String, String> headers = Collections.singletonMap(SAW_MANDATORY_HEADER_NAME, SAW_MANDATORY_HEADER_VALUE);
        String cookie = SAW_MANDATORY_COOKIE_NAME + "=" + SAW_MANDATORY_COOKIE_VALUE;
        ConnectionContextServiceImpl connectionContextService = buildConnectionContextServiceImpl(ApplicationType.SAW, PROTOCOL, null, PORT, headers, cookie);
        Assert.assertFalse(connectionContextService.isConnectionContextValid());
    }

    @Test
    public void testIsConnectionContextValid_withEmptyHost() throws Exception {
        Map<String, String> headers = Collections.singletonMap(SAW_MANDATORY_HEADER_NAME, SAW_MANDATORY_HEADER_VALUE);
        String cookie = SAW_MANDATORY_COOKIE_NAME + "=" + SAW_MANDATORY_COOKIE_VALUE + "; " + SAW_OPTIONAL_COOKIE_NAME + "=" + SAW_OPTIONAL_COOKIE_VALUE;
        ConnectionContextServiceImpl connectionContextService = buildConnectionContextServiceImpl(ApplicationType.SAW, PROTOCOL, "", PORT, headers, cookie);
        Assert.assertFalse(connectionContextService.isConnectionContextValid());
    }

    @Test
    public void testIsConnectionContextValid_withoutProtocol() throws Exception {
        Map<String, String> headers = Collections.singletonMap(SAW_MANDATORY_HEADER_NAME, SAW_MANDATORY_HEADER_VALUE);
        String cookie = SAW_MANDATORY_COOKIE_NAME + "=" + SAW_MANDATORY_COOKIE_VALUE;
        ConnectionContextServiceImpl connectionContextService = buildConnectionContextServiceImpl(ApplicationType.SAW, null, HOST_NAME, PORT, headers, cookie);
        Assert.assertFalse(connectionContextService.isConnectionContextValid());
    }

    @Test
    public void testIsConnectionContextValid_withEmptyProtocol() throws Exception {
        Map<String, String> headers = Collections.singletonMap(SAW_MANDATORY_HEADER_NAME, SAW_MANDATORY_HEADER_VALUE);
        String cookie = SAW_MANDATORY_COOKIE_NAME + "=" + SAW_MANDATORY_COOKIE_VALUE + "; " + SAW_OPTIONAL_COOKIE_NAME + "=" + SAW_OPTIONAL_COOKIE_VALUE;
        ConnectionContextServiceImpl connectionContextService = buildConnectionContextServiceImpl(ApplicationType.SAW, "", HOST_NAME, PORT, headers, cookie);
        Assert.assertFalse(connectionContextService.isConnectionContextValid());
    }


    @Test
    public void testIsConnectionContextValid_withUnsupportedProtocol() throws Exception {
        Map<String, String> headers = Collections.singletonMap(SAW_MANDATORY_HEADER_NAME, SAW_MANDATORY_HEADER_VALUE);
        String cookie = SAW_MANDATORY_COOKIE_NAME + "=" + SAW_MANDATORY_COOKIE_VALUE + "; " + SAW_OPTIONAL_COOKIE_NAME + "=" + SAW_OPTIONAL_COOKIE_VALUE;
        ConnectionContextServiceImpl connectionContextService = buildConnectionContextServiceImpl(ApplicationType.SAW, "age ti ti pi", HOST_NAME, PORT, headers, cookie);
        Assert.assertFalse(connectionContextService.isConnectionContextValid());
    }

    @Test
    public void testIsConnectionContextValid_withoutMandatoryHeader() throws Exception {
        Map<String, String> headers = Collections.singletonMap(SAW_OPTIONAL_HEADER_NAME, SAW_OPTIONAL_HEADER_VALUE);
        String cookie = SAW_MANDATORY_COOKIE_NAME + "=" + SAW_MANDATORY_COOKIE_VALUE;
        ConnectionContextServiceImpl connectionContextService = buildConnectionContextServiceImpl(ApplicationType.SAW, PROTOCOL, HOST_NAME, PORT, headers, cookie);
        Assert.assertFalse(connectionContextService.isConnectionContextValid());
    }

    @Test
    public void testIsConnectionContextValid_withEmptyMandatoryHeader() throws Exception {
        Map<String, String> headers = Collections.singletonMap(SAW_MANDATORY_HEADER_NAME, "");
        String cookie = SAW_MANDATORY_COOKIE_NAME + "=" + SAW_MANDATORY_COOKIE_VALUE + "; " + SAW_OPTIONAL_COOKIE_NAME + "=" + SAW_OPTIONAL_COOKIE_VALUE;
        ConnectionContextServiceImpl connectionContextService = buildConnectionContextServiceImpl(ApplicationType.SAW, PROTOCOL, HOST_NAME, PORT, headers, cookie);
        Assert.assertFalse(connectionContextService.isConnectionContextValid());
    }

    @Test
    public void testIsConnectionContextValid_withoutCookie() throws Exception {
        Map<String, String> headers = Collections.singletonMap(SAW_MANDATORY_HEADER_NAME, SAW_MANDATORY_HEADER_VALUE);
        ConnectionContextServiceImpl connectionContextService = buildConnectionContextServiceImpl(ApplicationType.SAW, PROTOCOL, HOST_NAME, PORT, headers, null);
        Assert.assertFalse(connectionContextService.isConnectionContextValid());
    }

    @Test
    public void testIsConnectionContextValid_withEmptyCookie() throws Exception {
        Map<String, String> headers = Collections.singletonMap(SAW_MANDATORY_HEADER_NAME, SAW_MANDATORY_HEADER_VALUE);
        ConnectionContextServiceImpl connectionContextService = buildConnectionContextServiceImpl(ApplicationType.SAW, PROTOCOL, HOST_NAME, PORT, headers, "");
        Assert.assertFalse(connectionContextService.isConnectionContextValid());
    }

    @Test
    public void testIsConnectionContextValid_withoutMandatoryCookie() throws Exception {
        Map<String, String> headers = Collections.singletonMap(SAW_MANDATORY_HEADER_NAME, SAW_MANDATORY_HEADER_VALUE);
        String cookie = SAW_OPTIONAL_COOKIE_NAME + "=" + SAW_OPTIONAL_COOKIE_VALUE;
        ConnectionContextServiceImpl connectionContextService = buildConnectionContextServiceImpl(ApplicationType.SAW, PROTOCOL, HOST_NAME, PORT, headers, cookie);
        Assert.assertFalse(connectionContextService.isConnectionContextValid());
    }

    @Test
    public void testIsConnectionContextValid_withEmptyMandatoryCookie() throws Exception {
        Map<String, String> headers = Collections.singletonMap(SAW_MANDATORY_HEADER_NAME, SAW_MANDATORY_HEADER_VALUE);
        String cookie = SAW_OPTIONAL_COOKIE_NAME + "=" + SAW_OPTIONAL_COOKIE_VALUE + "; " + SAW_MANDATORY_COOKIE_NAME + "=";
        ConnectionContextServiceImpl connectionContextService = buildConnectionContextServiceImpl(ApplicationType.SAW, PROTOCOL, HOST_NAME, PORT, headers, cookie);
        Assert.assertFalse(connectionContextService.isConnectionContextValid());
    }

    @Test
    public void testIsConnectionContextValid_valid() throws Exception {
        Map<String, String> headers = Collections.singletonMap(SAW_MANDATORY_HEADER_NAME, SAW_MANDATORY_HEADER_VALUE);
        String cookie = SAW_MANDATORY_COOKIE_NAME + "=" + SAW_MANDATORY_COOKIE_VALUE;
        ConnectionContextServiceImpl connectionContextService = buildConnectionContextServiceImpl(ApplicationType.SAW, PROTOCOL, HOST_NAME, PORT, headers, cookie);
        Assert.assertTrue(connectionContextService.isConnectionContextValid());
    }

    @Test
    public void testStoreConnectionContext() throws Exception {
        Map<String, String> headers = new HashMap<>();
        headers.put(SAW_MANDATORY_HEADER_NAME, SAW_MANDATORY_HEADER_VALUE);
        headers.put(SAW_OPTIONAL_HEADER_NAME, SAW_OPTIONAL_HEADER_VALUE);
        String cookie = SAW_OPTIONAL_COOKIE_NAME + "=" + SAW_OPTIONAL_COOKIE_VALUE + "; " + SAW_MANDATORY_COOKIE_NAME + "=" + SAW_MANDATORY_COOKIE_VALUE;
        ConnectionContextServiceImpl connectionContextService = buildConnectionContextServiceImpl(ApplicationType.SAW, PROTOCOL, HOST_NAME, PORT, headers, cookie);

        SharedPreferences sharedPreferencesMock = mock(SharedPreferences.class);
        when(contextMock.getSharedPreferences(ConnectionContextServiceImpl.CONNECTION_CONTEXT_PREFERENCES_FILE,
                Context.MODE_PRIVATE)).thenReturn(sharedPreferencesMock);

        SharedPreferences.Editor editorMock = mock(SharedPreferences.Editor.class);
        when(sharedPreferencesMock.edit()).thenReturn(editorMock);

        connectionContextService.storeConnectionContext();

        verify(editorMock).putString(ConnectionContextServiceImpl.APP_NAME_KEY, ApplicationType.SAW.name());
        verify(editorMock).putString(ConnectionContextServiceImpl.HOST_NAME_KEY, HOST_NAME);
        verify(editorMock).putString(ConnectionContextServiceImpl.PORT_KEY, PORT);
        verify(editorMock).putString(ConnectionContextServiceImpl.PROTOCOL_NAME_KEY, PROTOCOL);
        verify(editorMock).putString(SAW_MANDATORY_HEADER_PREF_KEY, SAW_MANDATORY_HEADER_VALUE);
        verify(editorMock).putString(SAW_OPTIONAL_HEADER_PREF_KEY, SAW_OPTIONAL_HEADER_VALUE);
        verify(editorMock).putString(SAW_MANDATORY_COOKIE_PREF_KEY, SAW_MANDATORY_COOKIE_VALUE);
        verify(editorMock).putString(SAW_OPTIONAL_COOKIE_PREF_KEY, SAW_OPTIONAL_COOKIE_VALUE);
        verify(editorMock).apply();
    }

    @Test
    public void testFillConnectionContext_buildConnectionContextFlow_cookieBeforeAppType() throws Exception {
        ConnectionContextServiceImpl connectionContextService = new ConnectionContextServiceImpl(contextMock,
                cookieParser, createHeadersDefinitionMap(ApplicationType.SAW, ApplicationType.PROPEL), encryptionService);

        SharedPreferences sharedPreferencesMock = mock(SharedPreferences.class);
        when(contextMock.getSharedPreferences(ConnectionContextServiceImpl.CONNECTION_CONTEXT_PREFERENCES_FILE,
                Context.MODE_PRIVATE)).thenReturn(sharedPreferencesMock);

        connectionContextService.initialize();

        connectionContextService.updateCookie(PROPEL_MANDATORY_COOKIE_NAME + "=" + PROPEL_MANDATORY_COOKIE_VALUE + "; " + PROPEL_OPTIONAL_COOKIE_NAME + "=" + PROPEL_OPTIONAL_COOKIE_VALUE);
        connectionContextService.updateApplicationType(ApplicationType.PROPEL);
        connectionContextService.updateHost(PROTOCOL, HOST_NAME, PORT);

        Map<String, String> headers = new HashMap<>();
        headers.put(PROPEL_MANDATORY_HEADER_NAME, PROPEL_MANDATORY_HEADER_VALUE);
        headers.put(PROPEL_OPTIONAL_HEADER_NAME, PROPEL_OPTIONAL_HEADER_VALUE);
        connectionContextService.updateHeaders(headers);

        ConnectionContext connectionContext = connectionContextService.getConnectionContext();

        Assert.assertEquals(ApplicationType.PROPEL, connectionContext.getApplicationType());
        Assert.assertEquals(HOST_NAME, connectionContext.getHostName());
        Assert.assertEquals(PORT, connectionContext.getPort());
        Assert.assertEquals(2, connectionContext.getHeaders().size());
        Assert.assertEquals(PROPEL_MANDATORY_HEADER_VALUE, connectionContext.getHeaderValue(PROPEL_MANDATORY_HEADER_NAME));
        Assert.assertEquals(PROPEL_OPTIONAL_HEADER_VALUE, connectionContext.getHeaderValue(PROPEL_OPTIONAL_HEADER_NAME));
        Assert.assertEquals(2, connectionContext.getCookies().size());
        Assert.assertEquals(PROPEL_MANDATORY_COOKIE_VALUE, connectionContext.getCookieValue(PROPEL_MANDATORY_COOKIE_NAME));
        Assert.assertEquals(PROPEL_OPTIONAL_COOKIE_VALUE, connectionContext.getCookieValue(PROPEL_OPTIONAL_COOKIE_NAME));
    }

    private ConnectionContextServiceImpl buildConnectionContextServiceImpl(ApplicationType applicationType,
                                                                           String protocol, String hostName, String port,
                                                                           Map<String, String> headers,
                                                                           String cookie) {
        ConnectionContextServiceImpl connectionContextService = new ConnectionContextServiceImpl(contextMock,
                cookieParser, createHeadersDefinitionMap(ApplicationType.SAW, ApplicationType.PROPEL), encryptionService);

        SharedPreferences sharedPreferencesMock = mock(SharedPreferences.class);
        when(contextMock.getSharedPreferences(ConnectionContextServiceImpl.CONNECTION_CONTEXT_PREFERENCES_FILE,
                Context.MODE_PRIVATE)).thenReturn(sharedPreferencesMock);

        connectionContextService.initialize();

        if (applicationType != null) {
            connectionContextService.updateApplicationType(applicationType);
        }

        if (hostName != null) {
            connectionContextService.updateHost(protocol, hostName, port);
        }

        if (headers != null) {
            connectionContextService.updateHeaders(headers);
        }

        if (cookie != null) {
            connectionContextService.updateCookie(cookie);
        }

        return connectionContextService;
    }

    public Map<ApplicationType, HeadersDefinition> createHeadersDefinitionMap(ApplicationType ... applicationTypes) {
        List<ApplicationType> applicationTypeList = Arrays.asList(applicationTypes);
        Map<ApplicationType, HeadersDefinition> headersDefinitionMap = new HashMap<>();
        if (applicationTypeList.contains(ApplicationType.SAW)) {
            List<String> mandatoryHeaders = Collections.singletonList(SAW_MANDATORY_HEADER_NAME);
            List<String> optionalHeaders = Collections.singletonList(SAW_OPTIONAL_HEADER_NAME);
            List<String> mandatoryCookies = Collections.singletonList(SAW_MANDATORY_COOKIE_NAME);
            List<String> optionalCookies = Collections.singletonList(SAW_OPTIONAL_COOKIE_NAME);
            headersDefinitionMap.put(ApplicationType.SAW, new TestyHeadersDefinition(mandatoryHeaders, optionalHeaders, mandatoryCookies, optionalCookies));
        }
        if (applicationTypeList.contains(ApplicationType.PROPEL)) {
            List<String> mandatoryHeaders = Collections.singletonList(PROPEL_MANDATORY_HEADER_NAME);
            List<String> optionalHeaders = Collections.singletonList(PROPEL_OPTIONAL_HEADER_NAME);
            List<String> mandatoryCookies = Collections.singletonList(PROPEL_MANDATORY_COOKIE_NAME);
            List<String> optionalCookies = Collections.singletonList(PROPEL_OPTIONAL_COOKIE_NAME);
            headersDefinitionMap.put(ApplicationType.PROPEL, new TestyHeadersDefinition(mandatoryHeaders, optionalHeaders, mandatoryCookies, optionalCookies));
        }
        return headersDefinitionMap;
    }

    public class TestyHeadersDefinition extends AbstractHeadersDefinition {
        
        private List<String> mandatoryHeaders;
        private List<String> optionalHeaders;
        private List<String> mandatoryCookies;
        private List<String> optionalCookies;

        public TestyHeadersDefinition(List<String> mandatoryHeaders, List<String> optionalHeaders, List<String> mandatoryCookies, List<String> optionalCookies) {
            this.mandatoryHeaders = mandatoryHeaders;
            this.optionalHeaders = optionalHeaders;
            this.mandatoryCookies = mandatoryCookies;
            this.optionalCookies = optionalCookies;
        }

        @Override
        public List<String> getMandatoryHeaders() {
            return mandatoryHeaders;
        }

        @Override
        public List<String> getOptionalHeaders() {
            return optionalHeaders;
        }

        @Override
        public List<String> getMandatoryCookies() {
            return mandatoryCookies;
        }

        @Override
        public List<String> getOptionalCookies() {
            return optionalCookies;
        }
    }
}