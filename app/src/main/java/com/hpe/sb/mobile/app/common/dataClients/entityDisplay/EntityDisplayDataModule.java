package com.hpe.sb.mobile.app.common.dataClients.entityDisplay;


import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.common.dataClients.displaylabels.DisplayLabelService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by salemo on 30/03/2016.
 *entitydisplaymodule
 */
@Module
public class EntityDisplayDataModule {
    private final ServiceBrokerApplication app;

    public EntityDisplayDataModule(ServiceBrokerApplication app) {
        this.app = app;
    }

    @Provides
    @Singleton
    public EntityBadgeService provideEntityBadgeService(DisplayLabelService displayLabelService) {
        return new EntityBadgeServiceImpl(displayLabelService, app);
    }
}
