package com.hpe.sb.mobile.app.features.login.utils;


import com.hpe.sb.mobile.app.features.login.activities.PreLoginActivity;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

@RunWith(MockitoJUnitRunner.class)
public class UrlParserTest {

    private UrlParser urlParser = new UrlParser();

    @Test
    public void testRemoveSawAppType() throws Exception {
        String url = "http://saw.com";
        String urlWithoutAppType = urlParser.removeQueryParam(url, PreLoginActivity.APPLICATION_TYPE_QUERY_PARAM_NAME);
        assertEquals("http://saw.com", urlWithoutAppType);

        url = "http://saw.com?";
        urlWithoutAppType = urlParser.removeQueryParam(url, PreLoginActivity.APPLICATION_TYPE_QUERY_PARAM_NAME);
        assertEquals("http://saw.com?", urlWithoutAppType);

        url = "http://saw.com?app=saw";
        urlWithoutAppType = urlParser.removeQueryParam(url, PreLoginActivity.APPLICATION_TYPE_QUERY_PARAM_NAME);
        assertEquals("http://saw.com", urlWithoutAppType);

        url = "http://saw.com?param1=value1";
        urlWithoutAppType = urlParser.removeQueryParam(url, PreLoginActivity.APPLICATION_TYPE_QUERY_PARAM_NAME);
        assertEquals("http://saw.com?param1=value1", urlWithoutAppType);

        url = "http://saw.com?app=saw&param2=value2";
        urlWithoutAppType = urlParser.removeQueryParam(url, PreLoginActivity.APPLICATION_TYPE_QUERY_PARAM_NAME);
        assertEquals("http://saw.com?param2=value2", urlWithoutAppType);

        url = "http://saw.com?param1=value1&app=saw";
        urlWithoutAppType = urlParser.removeQueryParam(url, PreLoginActivity.APPLICATION_TYPE_QUERY_PARAM_NAME);
        assertEquals("http://saw.com?param1=value1", urlWithoutAppType);

        url = "http://saw.com?param1=value1&app=saw&param3=value3";
        urlWithoutAppType = urlParser.removeQueryParam(url, PreLoginActivity.APPLICATION_TYPE_QUERY_PARAM_NAME);
        assertEquals("http://saw.com?param1=value1&param3=value3", urlWithoutAppType);

        // Check also capital letters query param
        url = "http://saw.com?APP=SAW";
        urlWithoutAppType = urlParser.removeQueryParam(url, PreLoginActivity.APPLICATION_TYPE_QUERY_PARAM_NAME);
        assertEquals("http://saw.com", urlWithoutAppType);

        url = "http://saw.com?APP=SAW&param2=value2";
        urlWithoutAppType = urlParser.removeQueryParam(url, PreLoginActivity.APPLICATION_TYPE_QUERY_PARAM_NAME);
        assertEquals("http://saw.com?param2=value2", urlWithoutAppType);

        url = "http://saw.com?param1=value1&APP=SAW";
        urlWithoutAppType = urlParser.removeQueryParam(url, PreLoginActivity.APPLICATION_TYPE_QUERY_PARAM_NAME);
        assertEquals("http://saw.com?param1=value1", urlWithoutAppType);

        url = "http://saw.com?param1=value1&APP=SAW&param3=value3";
        urlWithoutAppType = urlParser.removeQueryParam(url, PreLoginActivity.APPLICATION_TYPE_QUERY_PARAM_NAME);
        assertEquals("http://saw.com?param1=value1&param3=value3", urlWithoutAppType);
    }
}