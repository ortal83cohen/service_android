package com.hpe.sb.mobile.app.restclient;


import com.hpe.sb.mobile.app.common.dataClients.userContext.ApplicationType;
import com.hpe.sb.mobile.app.features.login.model.ConnectionContext;
import com.hpe.sb.mobile.app.features.login.model.SawHeadersDefinition;
import com.hpe.sb.mobile.app.features.login.services.ConnectionContextService;
import com.hpe.sb.mobile.app.infra.restclient.HttpLookupUtilsImpl;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * Created by cohenort on 26/07/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class HttpLookupUtilsImplTest {

    @Mock
    private ConnectionContextService connectionContextService;

    @Mock
    private ConnectionContext connectionContext;

    @Before
    public void setup() {
        connectionContextService = mock(ConnectionContextService.class);
        connectionContext = mock(ConnectionContext.class);
        when(connectionContextService.getConnectionContext()).thenReturn(connectionContext);
        when(connectionContext.getHostName()).thenReturn("theHost");
        when(connectionContext.getPort()).thenReturn("thePort");
        when(connectionContext.getProtocol()).thenReturn("theProtocol");
        when(connectionContext.getApplicationType()).thenReturn(ApplicationType.SAW);
        when(connectionContext.getCookieValue(SawHeadersDefinition.TENANT_ID_COOKIE_KEY)).thenReturn("theTenantId");
    }

    @Test
    public void testBaseRestUrl() {
        HttpLookupUtilsImpl httpLookupUtilsImpl = new HttpLookupUtilsImpl(connectionContextService);
        Assert.assertEquals(httpLookupUtilsImpl.getBaseRestUrl(), "theProtocol://theHost:thePort/rest/theTenantId/mobile/");
    }

    @Test
    public void testBaseRestUrlWithPrefix() {
        HttpLookupUtilsImpl httpLookupUtilsImpl = new HttpLookupUtilsImpl(connectionContextService);
        Assert.assertEquals(httpLookupUtilsImpl.getBaseRestUrl("thePrefix"), "theProtocol://theHost:thePort/rest/theTenantId/mobile/thePrefix");
    }

    @Test
    public void testBaseUrl() {
        HttpLookupUtilsImpl httpLookupUtilsImpl = new HttpLookupUtilsImpl(connectionContextService);
        Assert.assertEquals(httpLookupUtilsImpl.getBaseUrl(), "theProtocol://theHost:thePort");
    }
}