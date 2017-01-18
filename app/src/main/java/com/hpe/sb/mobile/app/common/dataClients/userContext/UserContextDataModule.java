package com.hpe.sb.mobile.app.common.dataClients.userContext;

import com.hpe.sb.mobile.app.common.utils.LogTagConstants;
import com.hpe.sb.mobile.app.features.googlepushnotification.GooglePushNotificationClient;
import com.hpe.sb.mobile.app.features.login.restClient.AuthenticationClient;
import com.hpe.sb.mobile.app.features.login.services.ConnectionContextService;
import com.hpe.sb.mobile.app.features.login.services.LogoutService;
import com.hpe.sb.mobile.app.features.login.services.LogoutServiceImpl;
import com.hpe.sb.mobile.app.features.login.services.SessionCookieService;
import com.hpe.sb.mobile.app.infra.dataClients.GeneralDbClient;
import com.hpe.sb.mobile.app.infra.dataClients.SingletonDbClient;
import com.hpe.sb.mobile.app.infra.db.DbHelper;
import com.hpe.sb.mobile.app.infra.db.GeneralBlobModel;
import com.hpe.sb.mobile.app.serverModel.user.User;

import android.content.ContentResolver;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by salemo on 30/03/2016.
 */
@Module
public class UserContextDataModule {

    @Provides
    @Singleton
    public UserContextService provideUserContextService(SingletonDbClient<User> userDbClient) {
        UserContextServiceImpl userContextService = new UserContextServiceImpl(userDbClient);
        userContextService.initialize();
        return userContextService;
    }

    @Provides
    @Singleton
    public SingletonDbClient<User> provideUserContextDbClient(ContentResolver contentResolver,
            DbHelper dbHelper) {
        return new SingletonDbClient<>(User.class, contentResolver,
                LogTagConstants.USER_CONTEXT_CLIENT, "UserDataId", GeneralBlobModel.TYPE_USER_CONTEXT, dbHelper);
    }


    @Provides
    @Singleton
    public LogoutService provideLogoutService(AuthenticationClient authenticationClient,
            GooglePushNotificationClient googlePushNotificationClient, GeneralDbClient generalDbClient,
            ConnectionContextService connectionContextService, SessionCookieService sessionCookieService) {
        return new LogoutServiceImpl(authenticationClient, googlePushNotificationClient, generalDbClient,
                connectionContextService, sessionCookieService);
    }
}
