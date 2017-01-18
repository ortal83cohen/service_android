package com.hpe.sb.mobile.app.common.utils;

import android.util.Log;

/**
 * Created by salemo on 19/06/2016.
 */
public class LinkResolverUtils {
    private static final String TAG = LinkResolverUtils.class.getSimpleName();

    private static final String SAW_ARTICLE_PREFIX = "saw/Article/";
    private static final String SAW_AFTER_ID_CHAR  = "/";
    private static final String ESS_ARTICLE_PREFIX = "saw/ess/viewResult/";
    private static final String ESS_AFTER_ID_CHAR  = "?";


    public static String getArticleIdFromUrl(String url) {
        String articleId = getArticleIdFromUrl(url, SAW_ARTICLE_PREFIX, SAW_AFTER_ID_CHAR);
        if(articleId != null) {
            Log.i(TAG, "found article id in ess link: " + articleId);
            return articleId;
        }
        articleId = getArticleIdFromUrl(url, ESS_ARTICLE_PREFIX, ESS_AFTER_ID_CHAR);
        if(articleId != null) {
            Log.i(TAG, "found article id in saw link: " + articleId);
            return articleId;
        }
        return null;
    }

    private static String getArticleIdFromUrl(String url, String articleIdPrefix, String afterIdChar) {
        int articleIdPrefixStart = url.lastIndexOf(articleIdPrefix);
        if(articleIdPrefixStart == -1) {
            return null;
        }
        String articleIdStart = url.substring(articleIdPrefixStart + articleIdPrefix.length());
        return articleIdStart.substring(0, articleIdStart.indexOf(afterIdChar));
    }
}
