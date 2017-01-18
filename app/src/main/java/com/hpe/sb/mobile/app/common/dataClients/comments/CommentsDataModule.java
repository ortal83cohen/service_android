package com.hpe.sb.mobile.app.common.dataClients.comments;

import com.hpe.sb.mobile.app.infra.restclient.RestService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class CommentsDataModule {

    @Provides
    @Singleton
    CommentsRestClient provideCommentsService(RestService restService) {
        return new CommentsRestClientImpl(restService);
    }

}
