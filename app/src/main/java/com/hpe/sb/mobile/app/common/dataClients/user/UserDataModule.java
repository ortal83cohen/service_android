package com.hpe.sb.mobile.app.common.dataClients.user;


import android.content.ContentResolver;

import com.hpe.sb.mobile.app.common.dataClients.user.dbClient.UserItemsDbClient;
import com.hpe.sb.mobile.app.common.dataClients.user.dbClient.UserItemsDbClientImpl;
import com.hpe.sb.mobile.app.common.dataClients.userContext.UserContextService;
import com.hpe.sb.mobile.app.infra.db.DbHelper;
import com.hpe.sb.mobile.app.infra.dataClients.SingletonDbClient;
import com.hpe.sb.mobile.app.serverModel.user.useritems.UserItems;
import com.hpe.sb.mobile.app.infra.db.GeneralBlobModel;
import com.hpe.sb.mobile.app.infra.restclient.HttpLookupUtils;
import com.hpe.sb.mobile.app.infra.restclient.RestClientQueue;
import com.hpe.sb.mobile.app.infra.restclient.RestService;
import com.hpe.sb.mobile.app.infra.restclient.customrequest.RequestFactory;
import com.hpe.sb.mobile.app.common.dataClients.user.restClient.UserRestClient;
import com.hpe.sb.mobile.app.common.dataClients.user.restClient.UserRestClientImpl;
import com.hpe.sb.mobile.app.common.utils.LogTagConstants;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class UserDataModule {

    @Provides
    @Singleton
    UserClient provideUserClient(UserRestClient userRestClient, ContentResolver contentResolver, UserItemsDbClient userItemsDbClient,
                                 UserContextService userContextService) {
        return new UserClientImpl(userRestClient, contentResolver, userItemsDbClient, userContextService);
    }

    @Provides
    @Singleton
    UserRestClient provideUserRestClient(RestClientQueue restClientQueue, RequestFactory requestFactory, HttpLookupUtils httpLookupUtils, RestService restService) {
        return new UserRestClientImpl(restClientQueue, requestFactory, httpLookupUtils, restService);
    }

    @Provides
    @Singleton
    UserItemsDbClient provideUserItemsDbClient(SingletonDbClient<UserItems> userItemsDbClient) {
        return new UserItemsDbClientImpl(userItemsDbClient);
    }

    @Provides
    @Singleton
    SingletonDbClient<UserItems> provideUserItemsSingleItemDbClient(
            ContentResolver contentResolver, DbHelper dbHelper) {
        return new SingletonDbClient<>(UserItems.class,
                contentResolver, LogTagConstants.USER_ITEMS,
                UserItems.USER_ITEMS_STATIC_ID, GeneralBlobModel.TYPE_USER_ITEMS,
                dbHelper);
    }
}
