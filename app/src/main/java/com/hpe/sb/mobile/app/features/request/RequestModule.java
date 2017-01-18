package com.hpe.sb.mobile.app.features.request;


import com.hpe.sb.mobile.app.common.dataClients.user.dbClient.UserItemsDbClient;
import com.hpe.sb.mobile.app.features.request.restClient.RequestRestClient;
import com.hpe.sb.mobile.app.features.request.restClient.RequestRestClientImpl;
import com.hpe.sb.mobile.app.infra.restclient.HttpLookupUtils;
import com.hpe.sb.mobile.app.infra.restclient.RestClientQueue;
import com.hpe.sb.mobile.app.infra.restclient.RestService;
import com.hpe.sb.mobile.app.infra.restclient.customrequest.RequestFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RequestModule {

    @Provides
    @Singleton
    RequestClient provideRequestClient(RestClientQueue restClientQueue, HttpLookupUtils httpLookupUtils,
                                       RequestRestClient requestRestClient, UserItemsDbClient userItemsDbClient) {
        return new RequestClientImpl(restClientQueue, httpLookupUtils, requestRestClient, userItemsDbClient);
    }

    @Provides
    @Singleton
    RequestRestClient provideRequestRestClient(RestClientQueue restClientQueue, RequestFactory requestFactory, HttpLookupUtils httpLookupUtils, RestService restService) {
        return new RequestRestClientImpl(restClientQueue, requestFactory, httpLookupUtils, restService);
    }

}
