package com.hpe.sb.mobile.app.common.dataClients.article.restClient;

import android.content.Context;
import com.hpe.sb.mobile.app.serverModel.article.Article;
import com.hpe.sb.mobile.app.infra.restclient.RestService;

import rx.Observable;

public class ArticleRestClientImpl implements ArticleRestClient {

    private final RestService restService;

    public ArticleRestClientImpl(RestService restService) {
        this.restService = restService;
    }

    private String getPathPrefix() {
        return "articles/";
    }

    @Override
    public Observable<Article> getArticle(Context context, String articleId) {
        String url = getPathPrefix() + "getArticle/" + articleId;
        return restService.createGetRequest(url, Article.class, context);
    }

}

