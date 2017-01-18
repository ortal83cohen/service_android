package com.hpe.sb.mobile.app.common.services.assetReader;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AssetReaderModule {

    private final Application app;

    public AssetReaderModule(Application app) {
        this.app = app;
    }

    @Provides
    @Singleton
    public AssetReaderService provideAssetReaderHelper() {
        return new AssetReaderService(app);
    }
}
