package com.hpe.sb.mobile.app.common.utils;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by steflin on 19/07/2016.
 */
public class LinkResolverUtilsTest {
    private static final String BASE_URL = "http://steflin1.emea.hpqcorp.net:8000/";

    @Test
    public void getArticleIdIfExists() throws Exception {
        // saw article
        assertEquals("wrong article id from saw url", "13671", LinkResolverUtils.getArticleIdFromUrl(BASE_URL+"saw/Article/13671/general"));

        // saw news, NOT article
        assertNull(LinkResolverUtils.getArticleIdFromUrl(BASE_URL+"saw/News/16229/general"));

        // ess article
        assertEquals("wrong article id from ess url", "16428", LinkResolverUtils.getArticleIdFromUrl(BASE_URL+"saw/ess/viewResult/16428?query=windows"));

        // ess question, NOT article
        assertNull(LinkResolverUtils.getArticleIdFromUrl(BASE_URL+"saw/ess/question/578e0b2d32516356a33dc8b2?query=windows"));
    }
}
