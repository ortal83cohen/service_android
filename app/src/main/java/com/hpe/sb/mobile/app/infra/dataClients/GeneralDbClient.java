package com.hpe.sb.mobile.app.infra.dataClients;

import rx.Observable;

/**
 * Created by salemo on 31/05/2016.
 */
public interface GeneralDbClient {

    Observable<Integer> deleteUserData();
}
