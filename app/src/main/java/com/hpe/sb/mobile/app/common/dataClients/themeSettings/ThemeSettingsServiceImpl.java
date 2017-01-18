package com.hpe.sb.mobile.app.common.dataClients.themeSettings;


import android.util.Log;
import com.hpe.sb.mobile.app.serverModel.ThemeSettings;
import com.hpe.sb.mobile.app.infra.dataClients.DataBlob;
import com.hpe.sb.mobile.app.infra.dataClients.SingletonDbClient;
import com.hpe.sb.mobile.app.common.utils.LogTagConstants;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;

/**
 * Creator: Sergey Steblin
 * Date:    17/04/2016
 */
public class ThemeSettingsServiceImpl implements ThemeSettingsService {

    private ThemeSettings themeSettings;
    private final SingletonDbClient<ThemeSettings> dbClient;

    public ThemeSettingsServiceImpl(SingletonDbClient<ThemeSettings> dbClient) {
        this.dbClient = dbClient;
    }

    public void initialize() {
        DataBlob<ThemeSettings> dataBlob = dbClient.getItemSync();
        if (dataBlob != null) {
            themeSettings = dataBlob.getData();
        }
    }

    public Observable<Void> setThemeSettings(final ThemeSettings themeSettings) {
        return Observable.defer(new Func0<Observable<Void>>() {
            @Override
            public Observable<Void> call() {
                ThemeSettingsServiceImpl.this.themeSettings = themeSettings;
                return dbClient.set(themeSettings);
            }
        });
    }

    public ThemeSettings getThemeSettings() {
        return themeSettings;
    }
}
