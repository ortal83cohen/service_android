package com.hpe.sb.mobile.app.features.router;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

@Module
public class RouterModule {

    @Singleton
    @Provides
    RouteBundleFactory provideRouteBundleFactory() {
        return new RouteBundleFactory();
    }
}
