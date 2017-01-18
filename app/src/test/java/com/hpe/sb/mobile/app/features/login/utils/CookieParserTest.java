package com.hpe.sb.mobile.app.features.login.utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Map;
import java.util.TreeMap;


/**
 * Created by malikdav on 26/03/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class CookieParserTest {

    private final String key1 = "key1";
    private final String key2 = "key2";
    private final String key3 = "key3";
    private final String value1 = "value1";
    private final String value2 = "value2";
    private final String value3 = "value3";

    private CookieParser cookieParser = new CookieParser();

    @Test
    public void testGetCookieValueByName() {
        String cookie = key1 + "=" + value1 + "; " + key2 + "=" + value2 + "; " + key3 + "=" + value3;

        String valueForKey1 = cookieParser.getCookieValueByName(cookie, key1);
        String valueForKey2 = cookieParser.getCookieValueByName(cookie, key2);
        String valueForKey3 = cookieParser.getCookieValueByName(cookie, key3);

        Assert.assertEquals(value1, valueForKey1);
        Assert.assertEquals(value2, valueForKey2);
        Assert.assertEquals(value3, valueForKey3);
    }

    @Test
    public void testGetCookieValueByName_NonExistingCookie() {
        String cookie = key1 + "=" + value1 + "; " + key2 + "=" + value2 + "; " + key3 + "=" + value3;
        String value = cookieParser.getCookieValueByName(cookie, "key4");
        Assert.assertEquals(null, value);
    }

    @Test
    public void testGetCookieValueByName_CookieWithWrongFormat() {
        String cookie = key1 + "=" + value1 + "; " + key2 + "=" + "; " + key3 + "=" + value3;
        String value = cookieParser.getCookieValueByName(cookie, key2);
        Assert.assertEquals(null, value);
    }

    @Test
    public void testGetCookieValueByName_CookieWithWrongFormat2() {
        String cookie = key1 + "=" + value1 + "; " + key2 + "=" + "; " + key3 + "=" + value3;
        String value = cookieParser.getCookieValueByName(cookie, key2 + "=");
        Assert.assertEquals(null, value);
    }

    @Test
    public void testGetCookieValueByName_CookieWithComplexValidFormat() {
        String cookie = key1 + "=" + value1 + "; secure; " + key3 + "=" + value3;

        String valueForKey1 = cookieParser.getCookieValueByName(cookie, key1);
        String valueForKeySecure = cookieParser.getCookieValueByName(cookie, "secure");
        String valueForKey3 = cookieParser.getCookieValueByName(cookie, key3);

        Assert.assertEquals(value1, valueForKey1);
        Assert.assertEquals(null, valueForKeySecure);
        Assert.assertEquals(value3, valueForKey3);
    }

    @Test
    public void testBuildCookieStringFromCookieMap()  {
        Map<String, String> cookieMap = new TreeMap<String, String>() {{
            put(key1, value1);
            put(key2, value2);
        }
        };

        String cookieString = cookieParser.buildCookieStringFromCookieMap(cookieMap);
        String expectedString = key1 + "=" + value1 + "; " + key2 + "=" + value2;
        Assert.assertEquals(cookieString, expectedString);
    }
}