package com.hpe.sb.mobile.app.common.dataClients.userContext;

import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.features.login.services.ActivationUrlService;
import com.hpe.sb.mobile.app.features.login.services.ActivationUrlServiceImpl;
import com.hpe.sb.mobile.app.infra.encryption.EncryptionService;
import com.hpe.sb.mobile.app.features.login.model.DemoHeadersDefinition;
import com.hpe.sb.mobile.app.features.login.model.HeadersDefinition;
import com.hpe.sb.mobile.app.features.login.model.PropelHeadersDefinition;
import com.hpe.sb.mobile.app.features.login.model.SawHeadersDefinition;
import com.hpe.sb.mobile.app.features.login.restClient.AuthenticationClient;
import com.hpe.sb.mobile.app.features.login.restClient.AuthenticationRestClientImpl;
import com.hpe.sb.mobile.app.features.login.restClient.UnauthenticatedRequestFactory;
import com.hpe.sb.mobile.app.features.login.services.ConnectionContextService;
import com.hpe.sb.mobile.app.features.login.services.ConnectionContextServiceImpl;
import com.hpe.sb.mobile.app.features.login.utils.CookieParser;
import com.hpe.sb.mobile.app.features.login.utils.UrlParser;
import com.hpe.sb.mobile.app.infra.restclient.HttpLookupUtils;
import com.hpe.sb.mobile.app.infra.restclient.RestClientQueue;
import com.hpe.sb.mobile.app.infra.restclient.RestService;
import com.hpe.sb.mobile.app.infra.restclient.RetryPolicyUtil;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

@Module
public class ConnectionContextModule {

    private final ServiceBrokerApplication app;

    public ConnectionContextModule(ServiceBrokerApplication app) {
        this.app = app;
    }

    @Provides
    @Singleton
    public SawHeadersDefinition provideSawHeadersDefinition() {
        return new SawHeadersDefinition();
    }

    @Provides
    @Singleton
    public PropelHeadersDefinition providePropelHeadersDefinition() {
        return new PropelHeadersDefinition();
    }

    @Provides
    @Singleton
    public DemoHeadersDefinition provideDemoHeadersDefinition() {
        return new DemoHeadersDefinition();
    }

    @Provides
    @Singleton
    public CookieParser provideCookieParser() {
        return new CookieParser();
    }

    @Provides
    @Singleton
    public UrlParser provideUrlParser() {
        return new UrlParser();
    }

    @Provides
    @Singleton
    public ConnectionContextService provideConnectionContextService(CookieParser cookieParser,
                                                                    SawHeadersDefinition sawHeadersDefinition,
                                                                    PropelHeadersDefinition propelHeadersDefinition,
                                                                    DemoHeadersDefinition demoHeadersDefinition,
                                                                    EncryptionService encryptionService) {
        Map<ApplicationType, HeadersDefinition> headersDefinitionMap = new HashMap<>();
        headersDefinitionMap.put(ApplicationType.SAW, sawHeadersDefinition);
        headersDefinitionMap.put(ApplicationType.PROPEL, propelHeadersDefinition);
        headersDefinitionMap.put(ApplicationType.DEMO, demoHeadersDefinition);
        ConnectionContextServiceImpl connectionContextService = new ConnectionContextServiceImpl(app,
                cookieParser, headersDefinitionMap, encryptionService);
        connectionContextService.initialize();
        return connectionContextService;
    }

    @Provides
    @Singleton
    public ActivationUrlService provideActivationUrlService() {
        return new ActivationUrlServiceImpl(app);
    }

    @Provides
    @Singleton
    public UnauthenticatedRequestFactory provideUnauthenticatedRequestFactory(RetryPolicyUtil retryPolicyUtil) {
        return new UnauthenticatedRequestFactory(retryPolicyUtil);
    }

    @Provides
    @Singleton
    public AuthenticationClient provideAuthenticationClient(RestService restService, RestClientQueue restClientQueue,
                                                            UnauthenticatedRequestFactory unauthenticatedRequestFactory,
                                                            HttpLookupUtils httpLookupUtils) {
        return new AuthenticationRestClientImpl(restService, restClientQueue, unauthenticatedRequestFactory, httpLookupUtils);

    }
}
