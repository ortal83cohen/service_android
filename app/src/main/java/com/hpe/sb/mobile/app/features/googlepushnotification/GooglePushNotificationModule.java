package com.hpe.sb.mobile.app.features.googlepushnotification;

import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.common.dataClients.user.UserClient;
import com.hpe.sb.mobile.app.common.dataClients.userContext.UserContextService;
import com.hpe.sb.mobile.app.common.utils.LogTagConstants;
import com.hpe.sb.mobile.app.features.googlepushnotification.model.GcmRegistrations;
import com.hpe.sb.mobile.app.features.googlepushnotification.model.GooglePushNotificationType;
import com.hpe.sb.mobile.app.features.googlepushnotification.notificationPresenters.GooglePushNotificationPresenter;
import com.hpe.sb.mobile.app.features.googlepushnotification.notificationPresenters.PendingApprovalNotificationPresenter;
import com.hpe.sb.mobile.app.features.googlepushnotification.notificationPresenters.ProvideMoreInfoNotificationPresenter;
import com.hpe.sb.mobile.app.features.googlepushnotification.notificationPresenters.RequestResolvedNotificationPresenter;
import com.hpe.sb.mobile.app.features.googlepushnotification.restClient.GooglePushNotificationRestClient;
import com.hpe.sb.mobile.app.features.googlepushnotification.restClient.GooglePushNotificationRestClientImpl;
import com.hpe.sb.mobile.app.features.googlepushnotification.scheduler.GcmSchedulerService;
import com.hpe.sb.mobile.app.features.googlepushnotification.scheduler.GcmSchedulerServiceImpl;
import com.hpe.sb.mobile.app.features.googlepushnotification.sharedPreferences.GooglePushNotificationSharedPrefClient;
import com.hpe.sb.mobile.app.features.googlepushnotification.sharedPreferences.GooglePushNotificationSharedPrefClientImpl;
import com.hpe.sb.mobile.app.features.login.services.ConnectionContextService;
import com.hpe.sb.mobile.app.features.router.RouteBundleFactory;
import com.hpe.sb.mobile.app.infra.dataClients.SingletonDbClient;
import com.hpe.sb.mobile.app.infra.db.DbHelper;
import com.hpe.sb.mobile.app.infra.db.GeneralBlobModel;
import com.hpe.sb.mobile.app.infra.encryption.EncryptionService;
import com.hpe.sb.mobile.app.infra.restclient.RestService;

import android.content.ContentResolver;
import com.hpe.sb.mobile.app.serverModel.user.User;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class GooglePushNotificationModule {
    private final ServiceBrokerApplication app;

    public GooglePushNotificationModule(ServiceBrokerApplication app) {
        this.app = app;
    }
    @Singleton
    @Provides
    GooglePushNotificationRestClient provideGooglePushNotificationRestClient(RestService restService) {
        return new GooglePushNotificationRestClientImpl(restService);
    }

    @Singleton
    @Provides
    GooglePushNotificationSharedPrefClient provideGooglePushNotificationSharedPreferences( EncryptionService encryptionService) {
        return new GooglePushNotificationSharedPrefClientImpl(app, encryptionService);
    }

    @Singleton
    @Provides
    SingletonDbClient<GcmRegistrations> provideGcmRegistrationsDbClient(
            ContentResolver contentResolver,
            @Named("encrypted") DbHelper dbHelper) {
        return new SingletonDbClient<>(GcmRegistrations.class, contentResolver, LogTagConstants.GCM_REGISTRATIONS,
                GcmRegistrations.DB_STATIC_ID, GeneralBlobModel.TYPE_GCM_REGISTRATIONS, dbHelper);
    }

    @Singleton
    @Provides
    RequestResolvedNotificationPresenter provideRequestResolvedNotificationPresenter(RouteBundleFactory routeBundleFactory) {
        return new RequestResolvedNotificationPresenter(routeBundleFactory);
    }

    @Singleton
    @Provides
    ProvideMoreInfoNotificationPresenter provideProvideMoreInfoNotificationPresenter(RouteBundleFactory routeBundleFactory) {
        return new ProvideMoreInfoNotificationPresenter(routeBundleFactory);
    }

    @Singleton
    @Provides
    PendingApprovalNotificationPresenter providePendingApprovalNotificationPresenter(RouteBundleFactory routeBundleFactory) {
        return new PendingApprovalNotificationPresenter(routeBundleFactory);
    }

    @Singleton
    @Provides
    GooglePushNotificationClient provideGooglePushNotificationClient(GooglePushNotificationRestClient googlePushNotificationRestClient,
            GooglePushNotificationSharedPrefClient googlePushNotificationSharedPrefClient,
            ConnectionContextService connectionContextService,
            UserClient userClient,
			SingletonDbClient<User> userDbClient,
            UserContextService userContextService,
            RequestResolvedNotificationPresenter requestResolvedNotificationPresenter,
            ProvideMoreInfoNotificationPresenter provideMoreInfoNotificationPresenter,
            PendingApprovalNotificationPresenter pendingApprovalNotificationPresenter) {
        Map<String, GooglePushNotificationPresenter> notificationPresentersMap = new HashMap<>();
        notificationPresentersMap.put(GooglePushNotificationType.REQUEST_RESOLVED, requestResolvedNotificationPresenter);
        notificationPresentersMap.put(GooglePushNotificationType.PROVIDE_MORE_INFO, provideMoreInfoNotificationPresenter);
        notificationPresentersMap.put(GooglePushNotificationType.PENDING_APPROVAL, pendingApprovalNotificationPresenter);
        return new GooglePushNotificationClientImpl(googlePushNotificationRestClient, googlePushNotificationSharedPrefClient,
                connectionContextService, userContextService, userClient, userDbClient, notificationPresentersMap);
    }

    @Singleton
    @Provides
    GcmSchedulerService provideGCMSchedulerService() {
        return new GcmSchedulerServiceImpl();
    }


}
