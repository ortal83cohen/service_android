package com.hpe.sb.mobile.app.infra.dataClients;

import com.hpe.sb.mobile.app.infra.db.DbHelper;

import java.util.concurrent.Callable;

import rx.Observable;


public class GeneralDbClientImpl implements GeneralDbClient {


    private DbHelper dbHelper;

    public GeneralDbClientImpl(DbHelper dbHelper) {
        this.dbHelper = dbHelper;

    }

    @Override
    public Observable<Integer> deleteUserData() {
        return Observable.fromCallable(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return  dbHelper.deleteGeneralBlob();
            }
        });
    }
}
