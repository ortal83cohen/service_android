package com.hpe.sb.mobile.app.common.dataClients.article;

import com.hpe.sb.mobile.app.serverModel.article.Article;

/**
 * contains all relevant data for displaying article
 **/
public class ArticleDisplayData {
    Article article;
    String html;

    public ArticleDisplayData(Article article, String html) {
        this.article = article;
        this.html = html;
    }

    public Article getArticle() {
        return article;
    }

    public String getArticleHTML() {
        return html;
    }
}
