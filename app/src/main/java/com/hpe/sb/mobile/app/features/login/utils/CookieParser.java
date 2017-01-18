package com.hpe.sb.mobile.app.features.login.utils;

import java.util.Map;

public class CookieParser {

    /**
     * returns the value of the cookie with the name given as a parameter
     * @param webViewCookie - the cookie from the WebView ("key1=value1;key2=value2;..")
     * @param cookieName - the name of the specific cookie
     * @return the value of the cookie specified by cookie name
     */
    //TODO:Dudi why no return a map? so will have O(n) and not N^2 -
    public String getCookieValueByName(String webViewCookie, String cookieName) {
        if (webViewCookie != null && !webViewCookie.isEmpty()) {
            String[] splitCookie = webViewCookie.split(";");
            for (String cookiePart : splitCookie) {
                cookiePart = cookiePart.trim();
                if (cookiePart.startsWith(cookieName + "=")) {
                    String[] cookieKeyValue = cookiePart.split("=");
                    if (cookieKeyValue.length == 2) {
                        return cookieKeyValue[1];
                    }
                }
            }
        }
        return null;
    }

    /**
     * returns a cookie string ("key1=value1;key2=value2;..") built from a given cookie map
     * @param cookies map containing cookies and their value
     * @return cookie string
     */
    public String buildCookieStringFromCookieMap(Map<String, String> cookies) {
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> cookieEntry : cookies.entrySet()) {
            if(sb.length() > 0) {
                // add a separator
                sb.append(";");
                sb.append(" ");
            }
            sb.append(cookieEntry.getKey());
            sb.append("=");
            sb.append(cookieEntry.getValue());
        }

        return sb.toString();
    }
}
