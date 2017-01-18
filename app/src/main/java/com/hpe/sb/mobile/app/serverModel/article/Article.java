package com.hpe.sb.mobile.app.serverModel.article;

/**
 * Created by salemo on 26/05/2016.
 *
 */
public class Article {

    /**
     * Offering id/
     */
    private String id;

    /**
     * Offering title.
     */
    private String title;

    /**
     * Offering content.
     */
    private String content;

    private String subType;

    public Article() {
    }

    public Article(String id, String title, String content, String subType) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.subType = subType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Article article = (Article) o;

        if (id != null ? !id.equals(article.id) : article.id != null) {
            return false;
        }
        if (title != null ? !title.equals(article.title) : article.title != null) {
            return false;
        }
        return content != null ? content.equals(article.content) : article.content == null;

    }

    public String getSubType() {
        return subType;
    }

    public void setSubType(String subType) {
        this.subType = subType;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }
}
