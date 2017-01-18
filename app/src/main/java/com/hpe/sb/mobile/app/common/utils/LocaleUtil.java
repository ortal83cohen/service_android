package com.hpe.sb.mobile.app.common.utils;

import java.util.Locale;

/**
 * Created by salemo on 25/04/2016.
 *
 */
public class LocaleUtil {

    public String getUserLocale() {
        String locale = Locale.getDefault().toString().replace('_', '-');
        if (locale.contains("-") && !isLocaleValid(locale)) {
            locale = locale.substring(0, locale.indexOf('-'));
        }
        return locale;
    }

    private boolean isLocaleValid(String locale) {
        String localePrefix = locale.substring(0, locale.indexOf('-'));
        String localeSuffix = locale.substring(locale.indexOf('-') + 1, locale.length()).toLowerCase();
        return !localePrefix.equals(localeSuffix);
    }
}
