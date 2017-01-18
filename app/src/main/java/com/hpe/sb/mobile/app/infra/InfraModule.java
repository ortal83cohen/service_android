package com.hpe.sb.mobile.app.infra;

import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.utils.TodoCardsEvent;
import com.hpe.sb.mobile.app.infra.baseActivities.SbExceptionsHandler;
import com.hpe.sb.mobile.app.infra.eventbus.EventBus;
import com.hpe.sb.mobile.app.features.request.event.NewRequestFormEvent;
import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * Created by malikdav on 26/04/2016.
 *
 */
@Module
public class InfraModule {
    private final ServiceBrokerApplication app;

    public InfraModule(ServiceBrokerApplication app) {
        this.app = app;
    }

    @Provides
    public EventBus<NewRequestFormEvent> provideNewRequestEventBus() {
        return new EventBus<>();
    }

    @Provides
    public EventBus<TodoCardsEvent> provideEventBus() {
        return new EventBus<>();
    }

    @Provides
    @Singleton
    public SbExceptionsHandler provideSbExceptionsHandler(){
        final SbExceptionsHandler sbExceptionsHandler = new SbExceptionsHandler();
        sbExceptionsHandler.init();
        return sbExceptionsHandler;
    }
}
