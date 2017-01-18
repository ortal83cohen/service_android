package com.hpe.sb.mobile.app.common.utils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.assertEquals;

/**
 * Created by salemo on 26/04/2016.
 *
 */


public class LocaleUtilTest {
    LocaleUtil  localeUtil = new LocaleUtil();

    Locale defaultLocale;
    @Before
    public void setUp() {
        defaultLocale = Locale.getDefault();
    }

    @After
    public void reset() {
        Locale.setDefault(defaultLocale);
    }

    @Test
    public void testResolveLocaleWithDefaultLocale() {
        setLocale("en", "US");
        String locale = localeUtil.getUserLocale();
        assertEquals("en-US",locale);
    }

    @Test
    public void testResolveLocaleWithDefaultCountry() {
        setLocale("fr", "FR");
        String locale = localeUtil.getUserLocale();
        assertEquals("fr",locale);
    }

    @Test
    public void testResolveLocaleWithNotDefaultCountry() {
        setLocale("fr", "GB");
        String locale = localeUtil.getUserLocale();
        assertEquals("fr-GB",locale);
    }

    private void setLocale(String language, String country) {
        Locale locale = new Locale(language, country);
        Locale.setDefault(locale);
    }

}
