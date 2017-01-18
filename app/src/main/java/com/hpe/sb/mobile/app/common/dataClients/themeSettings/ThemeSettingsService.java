package com.hpe.sb.mobile.app.common.dataClients.themeSettings;


import com.hpe.sb.mobile.app.serverModel.ThemeSettings;

import rx.Observable;

/**
 * Creator: Sergey Steblin
 * Date:    17/04/2016
 */
public interface ThemeSettingsService {

    Observable<Void> setThemeSettings(ThemeSettings themeSettings);

    ThemeSettings getThemeSettings();
}
