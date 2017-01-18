package com.hpe.sb.mobile.app.common.dataClients.article;

import com.hpe.sb.mobile.app.common.dataClients.article.restClient.ArticleRestClient;
import com.hpe.sb.mobile.app.common.dataClients.article.restClient.ArticleRestClientImpl;
import com.hpe.sb.mobile.app.common.services.assetReader.AssetReaderService;
import com.hpe.sb.mobile.app.infra.restclient.RestService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ArticleDataModule {

    @Singleton
    @Provides
    public ArticleClient provideArticleClient(ArticleRestClient articleRestClient, AssetReaderService assetReaderHelper) {
        return new ArticleClientImpl(articleRestClient, assetReaderHelper);
    }

    @Singleton
    @Provides
    public ArticleRestClient provideArticleRestClient(RestService restService) {
        return new ArticleRestClientImpl(restService);
    }

}
