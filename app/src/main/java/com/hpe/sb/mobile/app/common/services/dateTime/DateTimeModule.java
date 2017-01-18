package com.hpe.sb.mobile.app.common.services.dateTime;

import android.app.Application;


import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by oded on 28/07/2016.
 *
 */
@Module
public class DateTimeModule {
    private final Application app;

    public DateTimeModule(Application app) {
        this.app = app;
    }

    @Provides
    @Singleton
    public DateTimeService provideDateTimeService() {
        return new DateTimeService(app);
    }
}
