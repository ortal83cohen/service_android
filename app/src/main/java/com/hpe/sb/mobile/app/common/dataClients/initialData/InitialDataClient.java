package com.hpe.sb.mobile.app.common.dataClients.initialData;

import android.content.Context;

import com.hpe.sb.mobile.app.serverModel.InitialData;

import rx.Observable;


public interface InitialDataClient {

    Observable<InitialData> initializeData(final Context context);
}
