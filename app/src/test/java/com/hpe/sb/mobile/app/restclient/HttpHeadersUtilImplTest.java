package com.hpe.sb.mobile.app.restclient;


import com.hpe.sb.mobile.app.common.dataClients.userContext.ApplicationType;
import com.hpe.sb.mobile.app.common.dataClients.userContext.UserContextService;
import com.hpe.sb.mobile.app.features.login.model.ConnectionContext;
import com.hpe.sb.mobile.app.features.login.services.ConnectionContextService;
import com.hpe.sb.mobile.app.features.login.utils.CookieParser;
import com.hpe.sb.mobile.app.infra.restclient.HttpHeadersUtilImpl;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by cohenort on 26/07/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class HttpHeadersUtilImplTest {

    @Mock
    private ConnectionContextService connectionContextService;

    @Mock
    private CookieParser cookieParser;

    @Mock
    private UserContextService userContextService;

    @Mock
    private ConnectionContext connectionContext;

    @Before
    public void setup() {
        connectionContextService = mock(ConnectionContextService.class);
        connectionContext = mock(ConnectionContext.class);
        when(connectionContextService.getConnectionContext()).thenReturn(connectionContext);
        when(connectionContext.getApplicationType()).thenReturn(ApplicationType.SAW);
        when(userContextService.getLocale()).thenReturn("theLocale");
    }

    @Test
    public void testBaseUrl() {
        HttpHeadersUtilImpl HttpHeadersUtilImpl = new HttpHeadersUtilImpl(connectionContextService, cookieParser, userContextService);
        Map<String, String> httpHeaders = new HashMap<>();
        httpHeaders.put("MOBILE_X-LOCALE_HEADER","theLocale");
        httpHeaders.put("APPLICATION","SAW");

        Assert.assertEquals(HttpHeadersUtilImpl.getHttpHeaders(),httpHeaders );
    }
}
