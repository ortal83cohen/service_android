package com.hpe.sb.mobile.app;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.hpe.sb.mobile.app.common.dataClients.entityDisplay.EntityDisplayDataModule;
import com.hpe.sb.mobile.app.features.googlepushnotification.GooglePushNotificationModule;
import com.hpe.sb.mobile.app.common.services.dateTime.DateTimeModule;
import com.hpe.sb.mobile.app.infra.encryption.EncryptionModule;
import com.hpe.sb.mobile.app.common.services.assetReader.AssetReaderModule;
import com.hpe.sb.mobile.app.infra.InfraModule;
import com.hpe.sb.mobile.app.infra.db.ProviderModule;
import com.hpe.sb.mobile.app.common.dataClients.userContext.ConnectionContextModule;
import com.hpe.sb.mobile.app.infra.restclient.RestClientModule;

public class ServiceBrokerApplication extends Application {

    private ServiceBrokerComponent serviceBrokerComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        AppCompatDelegate.setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);

        initDaggerComponents();
    }

    private void initDaggerComponents() {
        serviceBrokerComponent = DaggerServiceBrokerComponent.builder()
                .restClientModule(getRestClientModule())
                .connectionContextModule(getConnectionContextModule())
                .providerModule(getProviderModule())
                .googlePushNotificationModule(new GooglePushNotificationModule(this))
				.infraModule(new InfraModule(this))
				.entityDisplayDataModule(new EntityDisplayDataModule(this))
				.providerModule(getProviderModule())
                .encryptionModule(new EncryptionModule(this))
                .dateTimeModule(new DateTimeModule(this))
                .assetReaderModule(new AssetReaderModule(this))
                .build();
    }

    protected RestClientModule getRestClientModule() {
        return new RestClientModule(this);

    }
    protected ConnectionContextModule getConnectionContextModule() {
        return new ConnectionContextModule(this);
    }
    protected ProviderModule getProviderModule() {
        return new ProviderModule(this);
    }

    public ServiceBrokerComponent getServiceBrokerComponent() {
        return serviceBrokerComponent;
    }
}
