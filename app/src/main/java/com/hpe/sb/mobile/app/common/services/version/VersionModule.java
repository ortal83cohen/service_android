package com.hpe.sb.mobile.app.common.services.version;

import dagger.Module;
import dagger.Provides;

import javax.inject.Singleton;

/**
 * Created by alterl on 09/08/2016.
 */

@Module
public class VersionModule {

    @Provides
    @Singleton
    public VersionService versionService() {
        VersionService service = new VersionService();
        return service;
    }
}