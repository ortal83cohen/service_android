package com.hpe.sb.mobile.app.infra.restclient;

import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.common.dataClients.userContext.UserContextService;
import com.hpe.sb.mobile.app.features.googlepushnotification.GooglePushNotificationClient;
import com.hpe.sb.mobile.app.infra.image.ImageService;
import com.hpe.sb.mobile.app.infra.image.ImageServiceImpl;
import com.hpe.sb.mobile.app.infra.image.ImageServiceUtil;
import com.hpe.sb.mobile.app.features.login.restClient.AuthenticationClient;
import com.hpe.sb.mobile.app.features.login.services.ConnectionContextService;
import com.hpe.sb.mobile.app.features.login.services.PostLoginService;
import com.hpe.sb.mobile.app.features.login.services.PostLoginServiceImpl;
import com.hpe.sb.mobile.app.features.login.services.SessionCookieService;
import com.hpe.sb.mobile.app.features.login.services.SessionCookieServiceImpl;
import com.hpe.sb.mobile.app.features.login.utils.CookieParser;
import com.hpe.sb.mobile.app.infra.restclient.customrequest.RequestFactory;
import com.hpe.sb.mobile.app.common.dataClients.search.SearchClient;
import com.hpe.sb.mobile.app.common.dataClients.search.SearchClientImpl;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * Created by malikdav on 20/03/2016.
 *
 */
@Module
public class RestClientModule {

    private final ServiceBrokerApplication app;

    public RestClientModule(ServiceBrokerApplication app) {
        this.app = app;
    }

    @Provides
    @Singleton
    RequestFactory provideRequestFactory(RetryPolicyUtil retryPolicyUtil,
                                         HttpHeadersUtil httpHeadersUtil) {
        return new RequestFactory(retryPolicyUtil, httpHeadersUtil);
    }

    @Provides
    @Singleton
    RetryPolicyUtil provideRetryPolicyUtil() {
        return new RetryPolicyUtil();
    }

    @Provides
    @Singleton
    HttpHeadersUtil provideHttpHeadersUtil(ConnectionContextService connectionContextService,
                                           CookieParser cookieParser, UserContextService userContextService) {
        return new HttpHeadersUtilImpl(connectionContextService, cookieParser, userContextService);
    }

    @Provides
    @Singleton
    RestClientQueue provideRestClientQueue() {
        return new RestClientQueueImpl(app);
    }

    @Provides
    @Singleton
    RestService provideRestService(HttpLookupUtils httpLookupUtils, RestClientQueue restClientQueue, RequestFactory requestFactory) {
        return new RestServiceImpl(restClientQueue, httpLookupUtils, requestFactory);
    }

    @Provides
    @Singleton
    HttpLookupUtils provideHttpLookupUtils(ConnectionContextService connectionContextService) {
        return new HttpLookupUtilsImpl(connectionContextService);
    }

    @Provides
    @Singleton
    SessionCookieService provideSessionCookieService(ConnectionContextService connectionContextService,
                                                     HttpLookupUtils httpLookupUtils) {
        return new SessionCookieServiceImpl(connectionContextService, httpLookupUtils);
    }

    @Provides
    @Singleton
    PostLoginService postLoginService(ConnectionContextService connectionContextService,
                                      AuthenticationClient authenticationClient,
                                      GooglePushNotificationClient googlePushNotificationClient) {
        return new PostLoginServiceImpl(connectionContextService, authenticationClient, googlePushNotificationClient);
    }

    @Provides
    @Singleton
    ImageService provideImageService(HttpLookupUtils httpLookupUtils, HttpHeadersUtil httpHeadersUtil) {
        return new ImageServiceImpl(app.getApplicationContext(), httpLookupUtils, httpHeadersUtil);
    }

    @Provides
    @Singleton
    ImageServiceUtil provideImageServiceUtil(ImageService imageService) {
        return new ImageServiceUtil(imageService);
    }

}
