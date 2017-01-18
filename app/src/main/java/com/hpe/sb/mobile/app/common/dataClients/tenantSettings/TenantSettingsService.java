package com.hpe.sb.mobile.app.common.dataClients.tenantSettings;


import com.hpe.sb.mobile.app.serverModel.TenantSettings;

import rx.Observable;

/**
 * Creator: Sergey Steblin
 * Date:    17/04/2016
 */
public interface TenantSettingsService {

    Observable<Void> setTenantSettings(TenantSettings tenantSettings);

    TenantSettings getTenantSettings();
}
