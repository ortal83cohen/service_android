package com.hpe.sb.mobile.app.common.dataClients.themeSettings;

import android.content.ContentResolver;

import com.hpe.sb.mobile.app.infra.db.DbHelper;
import com.hpe.sb.mobile.app.infra.dataClients.SingletonDbClient;
import com.hpe.sb.mobile.app.serverModel.ThemeSettings;
import com.hpe.sb.mobile.app.infra.db.GeneralBlobModel;
import com.hpe.sb.mobile.app.common.utils.LogTagConstants;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * Creator: Sergey Steblin
 * Date:    17/04/2016
 */
@Module
public class ThemeSettingsDataModule {

    @Provides
    @Singleton
    public ThemeSettingsService themeSettingsService(SingletonDbClient<ThemeSettings> dbClient) {
        ThemeSettingsServiceImpl service = new ThemeSettingsServiceImpl(dbClient);
        service.initialize();
        return service;
    }

    @Provides
    @Singleton
    public SingletonDbClient<ThemeSettings> themeSettingsDbClient(
            ContentResolver contentResolver, DbHelper dbHelper) {
        return new SingletonDbClient<>(ThemeSettings.class, contentResolver,
                LogTagConstants.THEME_SETTINGS, "ThemeSettings", GeneralBlobModel.THEME_SETTINGS, dbHelper);
    }
}
