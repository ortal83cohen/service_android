package com.hpe.sb.mobile.app.common.dataClients.tenantSettings;


import android.util.Log;

import com.hpe.sb.mobile.app.infra.dataClients.SingletonDbClient;
import com.hpe.sb.mobile.app.serverModel.TenantSettings;
import com.hpe.sb.mobile.app.infra.dataClients.DataBlob;
import com.hpe.sb.mobile.app.common.utils.LogTagConstants;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;

/**
 * Creator: Sergey Steblin
 * Date:    17/04/2016
 */
public class TenantSettingsServiceImpl implements TenantSettingsService {

    private TenantSettings tenantSettings;
    private final SingletonDbClient<TenantSettings> dbClient;

    public TenantSettingsServiceImpl(SingletonDbClient<TenantSettings> dbClient) {
        this.dbClient = dbClient;
    }

    public void initialize() {
        DataBlob<TenantSettings> dataBlob = dbClient.getItemSync();
        if (dataBlob != null) {
            tenantSettings = dataBlob.getData();
        }
    }

    public Observable<Void> setTenantSettings(final TenantSettings tenantSettings) {

        return Observable.defer(new Func0<Observable<Void>>() {
            @Override
            public Observable<Void> call() {
                TenantSettingsServiceImpl.this.tenantSettings = tenantSettings;
                return dbClient.set(tenantSettings);
            }
        });
    }

    public TenantSettings getTenantSettings() {
        return tenantSettings;
    }
}
