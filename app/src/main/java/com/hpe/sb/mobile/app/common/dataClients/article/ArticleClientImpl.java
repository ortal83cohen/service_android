package com.hpe.sb.mobile.app.common.dataClients.article;

import android.content.Context;

import com.hpe.sb.mobile.app.common.dataClients.article.restClient.ArticleRestClient;
import com.hpe.sb.mobile.app.common.services.assetReader.AssetReaderService;
import com.hpe.sb.mobile.app.serverModel.article.Article;
import rx.Observable;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

public class ArticleClientImpl implements ArticleClient {

    private ArticleRestClient articleRestClient;
    private AssetReaderService assetReaderHelper;

    private static final String HTML_TEMPLATE = "<html>\n" +
            "<head>\n" +
            "<style>\n" +
            "%s" +
            "</style>" +
            "</head>\n" +
            "<body>\n" +
            "<div id=\"article-content\"></div>\n" +
            "<script>%s</script>\n" +
            "</body>\n" +
            "</html>\n";

    public ArticleClientImpl(ArticleRestClient articleRestClient, AssetReaderService assetReaderHelper) {
        this.articleRestClient = articleRestClient;
        this.assetReaderHelper = assetReaderHelper;
    }

    @Override
    public Observable<ArticleDisplayData> getArticleForDisplay(Context context, String articleId) {
        Observable<Article> articleObservable = articleRestClient.getArticle(context, articleId)
                .subscribeOn(Schedulers.io());
        Observable<String> articleCSS = assetReaderHelper.getAssetAsString("article/article-style.css");
        Observable<String> articleJS = assetReaderHelper.getAssetAsString("article/article.js");
        return Observable.zip(articleObservable, articleCSS, articleJS, new Func3<Article, String, String, ArticleDisplayData>() {
            @Override
            public ArticleDisplayData call(Article article, String css, String js) {
                String articleHtml = String.format(HTML_TEMPLATE, css, js);
                return new ArticleDisplayData(article, articleHtml);
            }
        });
    }
}
