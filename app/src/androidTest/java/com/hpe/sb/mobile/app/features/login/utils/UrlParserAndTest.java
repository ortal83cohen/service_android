package com.hpe.sb.mobile.app.features.login.utils;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by Sergey Steblin on 08/09/2016.
 */
@RunWith(AndroidJUnit4.class)
public class UrlParserAndTest {

    private final String param1 = "param1";
    private final String param2 = "param2";
    private final String param3 = "param3";
    private final String value1 = "value1";
    private final String value2 = "value2";
    private final String value3 = "value3";

    private UrlParser urlParser = new UrlParser();


    @Test
    public void testGetQueryParam() {
        String query = "http://saw.com?" + param1 + "=" + value1 + "&" + param2 + "=" + value2 + "&" + param3 + "=" + value3;

        String valueForParam1 = urlParser.getQueryParam(query, param1);
        String valueForParam2 = urlParser.getQueryParam(query, param2);
        String valueForParam3 = urlParser.getQueryParam(query, param3);

        assertEquals(value1, valueForParam1);
        assertEquals(value2, valueForParam2);
        assertEquals(value3, valueForParam3);
    }

    @Test
    public void testGetQueryParam_NonExistingQueryParam() {
        String query = "http://saw.com?" + param1 + "=" + value1 + "&" + param2 + "=" + value2 + "&" + param3 + "=" + value3;
        String value = urlParser.getQueryParam(query, "param4");
        assertEquals(null, value);
    }

    @Test
    public void testGetQueryParam_QueryWithWrongFormat() {
        String query = "http://saw.com?" + param1 + "=" + value1 + "&" + param2 + "=" + "&" + param3 + "=" + value3;
        String value = urlParser.getQueryParam(query, param2);
        assertEquals("", value);
    }

    @Test
    public void testGetQueryParam_QueryWithWrongFormat2() {
        String query = "http://saw.com?" + param1 + "=" + value1 + "&" + param2 + "=" + "&" + param3 + "=" + value3;
        String value = urlParser.getQueryParam(query, param2 + "=");
        assertEquals(null, value);
    }

    @Test
    public void testHasQueryParam() {
        String query = "http://www.cnn.com?" + param1 + "&" + param2 + "=" + value2;
        assertTrue(urlParser.hasQueryParam(query, param1));
        assertTrue(urlParser.hasQueryParam(query, param2));
        assertFalse(urlParser.hasQueryParam(query, param3));
    }

    @Test
    public void testHasQueryParam_urlWithoutParams() {
        String query = "http://www.cnn.com";
        assertFalse(urlParser.hasQueryParam(query, param1));
    }

    @Test
    public void testAddQueryParam() {
        validateAddQueryParam("http'www.cnn.com");
    }

    @Test
    public void testAddQueryParamQueryWithParam() {
        validateAddQueryParam("http://www.cnn.com?" + param1 + "&" + param2 + "=" + value2);
    }

    @Test
    public void testAddQueryParamQueryWithExistParam() {
        validateAddQueryParam("http://www.cnn.com?p1");
        validateAddQueryParam("http://www.cnn.com?p1=");
        validateAddQueryParam("http://www.cnn.com?p1=false");
        validateAddQueryParam("http://www.cnn.com?p1=true");
    }

    @Test
    public void testAddQueryParamQueryWithFragment() {
        validateAddQueryParam("http://www.cnn.com#fragment1");
    }

    @Test
    public void testAddQueryParamQueryWithParamAndFragment() {
        validateAddQueryParam("http://www.cnn.com?" + param1 + "&" + param2 + "=" + value2 + "#fragment1");
    }

    private void validateAddQueryParam(String uriString) {
        String uriString2 = urlParser.addQueryParam(uriString, "p1", String.valueOf(true));
        assertTrue(urlParser.hasQueryParam(uriString2, "p1"));
        assertEquals(String.valueOf(true), urlParser.getQueryParam(uriString2, "p1"));
    }
}
