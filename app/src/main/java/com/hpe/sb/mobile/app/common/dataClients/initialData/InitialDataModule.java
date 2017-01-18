package com.hpe.sb.mobile.app.common.dataClients.initialData;

import com.hpe.sb.mobile.app.common.dataClients.displaylabels.DisplayLabelService;
import com.hpe.sb.mobile.app.common.dataClients.tenantSettings.TenantSettingsService;
import com.hpe.sb.mobile.app.common.dataClients.themeSettings.ThemeSettingsService;
import com.hpe.sb.mobile.app.common.dataClients.userContext.UserContextService;
import com.hpe.sb.mobile.app.common.services.version.VersionService;
import com.hpe.sb.mobile.app.features.googlepushnotification.scheduler.GcmSchedulerService;
import com.hpe.sb.mobile.app.features.login.services.ConnectionContextService;
import com.hpe.sb.mobile.app.infra.dataClients.ListDbClient;
import com.hpe.sb.mobile.app.infra.image.ImageService;
import com.hpe.sb.mobile.app.infra.restclient.RestService;
import com.hpe.sb.mobile.app.serverModel.catalog.CatalogGroup;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class InitialDataModule {

    @Provides
    @Singleton
    InitialDataClient provideInitialDataClient(RestService restService, ListDbClient<CatalogGroup> catalogDbClient,
                                               UserContextService userContextService, ThemeSettingsService themeSettingsService,
                                               TenantSettingsService tenantSettingsService, ImageService imageService,
                                               ConnectionContextService connectionContextService, DisplayLabelService displayLabelService,
                                               GcmSchedulerService gcmSchedulerService, VersionService versionService) {
        return new InitialDataClientImpl(restService, catalogDbClient, userContextService, themeSettingsService, tenantSettingsService, imageService,
                connectionContextService, displayLabelService, gcmSchedulerService, versionService);
    }

}
