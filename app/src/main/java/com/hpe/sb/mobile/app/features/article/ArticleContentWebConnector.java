package com.hpe.sb.mobile.app.features.article;


import android.webkit.JavascriptInterface;

public class ArticleContentWebConnector {

    private final String articleContent;

    public ArticleContentWebConnector(String articleContent) {
        this.articleContent = articleContent;
    }

    @JavascriptInterface
    public String getArticleContent() {
        return articleContent;
    }

}
