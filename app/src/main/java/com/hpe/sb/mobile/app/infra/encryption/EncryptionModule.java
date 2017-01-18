package com.hpe.sb.mobile.app.infra.encryption;

import com.hpe.sb.mobile.app.ServiceBrokerApplication;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class EncryptionModule {

    private final ServiceBrokerApplication app;

    public EncryptionModule(ServiceBrokerApplication app) {
        this.app = app;
    }


    @Provides
    @Singleton
    public EncryptionService provideEncryptionService(SecretKeyWrapper secretKeyWrapper) {
        EncryptionServiceImpl encryptionService = new EncryptionServiceImpl(secretKeyWrapper);
        encryptionService.init(app);
        return encryptionService;
    }

    @Provides
    public SecretKeyWrapper provideSecretKeyWrapper() {
        SecretKeyWrapper secretKeyWrapper = new SecretKeyWrapper();
        secretKeyWrapper.init(app);
        return secretKeyWrapper;
    }
}
