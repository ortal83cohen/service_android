package com.hpe.sb.mobile.app.infra.db;

import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.infra.dataClients.GeneralDbClient;
import com.hpe.sb.mobile.app.infra.dataClients.GeneralDbClientImpl;
import com.hpe.sb.mobile.app.infra.encryption.EncryptionService;


import android.content.ContentResolver;


import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ProviderModule {

    private final ServiceBrokerApplication app;

    public ProviderModule(ServiceBrokerApplication app) {
        this.app = app;
    }

    @Provides
    @Singleton
    public ContentResolver provideContentResolver() {
        return app.getContentResolver();
    }

    @Provides
    @Singleton
    public DbHelper provideDbHelper(ContentResolver contentResolver) {
        return new DbHelper(contentResolver);
    }

    @Provides
    @Singleton
    public GeneralDbClient provideGeneralDbClientImpl(DbHelper dbHelper) {
        return new GeneralDbClientImpl(dbHelper);
    }

    @Provides
    @Singleton
    @Named("encrypted")
    public DbHelper provideEncryptedDbHelper(ContentResolver contentResolver,
                                             EncryptionService encryptionService) {
        return new DbHelper(contentResolver, encryptionService);
    }

}
