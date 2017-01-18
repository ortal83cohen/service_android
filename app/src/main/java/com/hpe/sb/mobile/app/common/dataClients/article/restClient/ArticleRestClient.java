package com.hpe.sb.mobile.app.common.dataClients.article.restClient;

import android.content.Context;

import com.hpe.sb.mobile.app.serverModel.article.Article;

import rx.Observable;

/**
 * Created by malikdav on 14/03/2016.
 *
 */
public interface ArticleRestClient {

    Observable<Article> getArticle(Context context, String articleId);

}

