package com.hpe.sb.mobile.app.common.dataClients.tenantSettings;

import android.content.ContentResolver;

import com.hpe.sb.mobile.app.common.utils.LogTagConstants;
import com.hpe.sb.mobile.app.infra.db.DbHelper;
import com.hpe.sb.mobile.app.infra.dataClients.SingletonDbClient;
import com.hpe.sb.mobile.app.infra.db.GeneralBlobModel;
import com.hpe.sb.mobile.app.serverModel.TenantSettings;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Creator: Sergey Steblin
 * Date:    17/04/2016
 */
@Module
public class TenantSettingsDataModule {

    @Provides
    @Singleton
    public TenantSettingsService tenantSettingsService(SingletonDbClient<TenantSettings> dbClient) {
        TenantSettingsServiceImpl service = new TenantSettingsServiceImpl(dbClient);
        service.initialize();
        return service;
    }

    @Provides
    @Singleton
    public SingletonDbClient<TenantSettings> tenantSettingsDbClient(
            ContentResolver contentResolver, DbHelper dbHelper) {
        return new SingletonDbClient<>(TenantSettings.class, contentResolver,
                LogTagConstants.TENANT_SETTINGS, "TenantSettings", GeneralBlobModel.TENANT_SETTINGS, dbHelper);
    }

}
