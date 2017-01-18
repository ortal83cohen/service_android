package com.hpe.sb.mobile.app.common.dataClients.search;

import com.hpe.sb.mobile.app.infra.restclient.RestService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class SearchDataModule {

    @Provides
    @Singleton
    SearchClient provideSearchClient(RestService restService) {
        return new SearchClientImpl(restService);
    }

}
