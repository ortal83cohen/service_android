package com.hpe.sb.mobile.app.features.login.utils;

import android.net.Uri;

public class UrlParser {

    /**
     * returns the value of the query param with the name given as a parameter
     *
     * @param uriString - an uri string
     * @param paramName - the name of query param to return (e.g. "queryParamName1")
     * @return
     */
    public String getQueryParam(String uriString, String paramName) {
        return getQueryParam(Uri.parse(uriString), paramName);
    }

    /**
     * returns the value of the query param with the name given as a parameter
     *
     * @param uri       - an uri
     * @param paramName - the name of query param to return (e.g. "queryParamName1")
     * @return
     */
    public String getQueryParam(Uri uri, String paramName) {
        return uri.getQueryParameter(paramName);
    }

    /**
     * Verify if the param with the name given as a parameter exists in uri string
     *
     * @param uriString - an RFC 2396-compliant, encoded URI
     * @param paramName - the name of query param to verify existence
     * @return true if param exist, false if not
     */
    public boolean hasQueryParam(String uriString, String paramName) {
        return getQueryParam(uriString, paramName) != null;
    }

    /**
     * Adds the param with value to the uri string
     *
     * @param uriString  - an RFC 2396-compliant, encoded URI
     * @param paramName  - the param name to add to uri string
     * @param paramValue - the param value to add to uri string
     * @return new query with added param
     */
    public String addQueryParam(String uriString, String paramName, String paramValue) {
        String uriString2 = removeQueryParam(uriString, paramName);
        return Uri.parse(uriString2).buildUpon()
                .appendQueryParameter(paramName, paramValue)
                .build().toString();
    }

    /**
     * Remove given query param from url
     *
     * @param urlString      - the url
     * @param queryParamName the param to remove
     * @return the url without the query param
     */
    public String removeQueryParam(String urlString, String queryParamName) {
        int indexOfQuestionMark = urlString.indexOf('?');
        String urlWithoutParams;
        String params;

        // If we don't have a '?' or we don't have anything after it - simply return the original string
        if (indexOfQuestionMark == -1 || indexOfQuestionMark + 1 >= urlString.length()) {
            return urlString;
        }

        urlWithoutParams = urlString.substring(0, indexOfQuestionMark);
        params = urlString.substring(indexOfQuestionMark + 1);
        StringBuilder sb = new StringBuilder();

        String[] paramArray = params.split("&");
        for (String param : paramArray) {
            String[] keyAndValue = param.split("=");

            // Add to new query only params that not equals to queryParamName
            if(keyAndValue[0] == null || !keyAndValue[0].equalsIgnoreCase(queryParamName)) {
                sb.append(sb.length() == 0 ? "?" : "&");
                sb.append(param);
            }
        }

        return urlWithoutParams + sb.toString();
    }
}
