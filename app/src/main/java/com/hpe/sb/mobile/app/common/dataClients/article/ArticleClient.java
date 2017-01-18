package com.hpe.sb.mobile.app.common.dataClients.article;

import android.content.Context;

import rx.Observable;

public interface ArticleClient {

    Observable<ArticleDisplayData> getArticleForDisplay(Context context, String articleId);

}
