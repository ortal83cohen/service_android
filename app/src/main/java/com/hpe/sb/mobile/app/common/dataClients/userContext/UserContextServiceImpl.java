package com.hpe.sb.mobile.app.common.dataClients.userContext;


import com.hpe.sb.mobile.app.infra.dataClients.SingletonDbClient;
import com.hpe.sb.mobile.app.serverModel.user.User;
import com.hpe.sb.mobile.app.infra.dataClients.DataBlob;
import com.hpe.sb.mobile.app.common.utils.LocaleUtil;

import rx.Observable;
import rx.functions.Func0;

/**
 * Created by salemo on 30/03/2016.
 */
public class UserContextServiceImpl implements UserContextService {

    private User userModel;
    private String locale;
    private final SingletonDbClient<User> userDbClient;

    public UserContextServiceImpl(SingletonDbClient<User> userDbClient) {
        this.userDbClient = userDbClient;
    }

    public void initialize() {
        DataBlob<User> userDataBlob = userDbClient.getItemSync();
        if (userDataBlob != null) {
            userModel = userDataBlob.getData();
        }
        this.setLocale(new LocaleUtil().getUserLocale());
    }

    @Override
    public String getLocale() {
        return locale;
    }

    @Override
    public void setLocale(String locale) {
        this.locale = locale;
    }

    @Override
    public Observable<Void> setUserModel(final User userModel) {
        return Observable.defer(new Func0<Observable<Void>>() {
            @Override
            public Observable<Void> call() {
                UserContextServiceImpl.this.userModel = userModel;
                return userDbClient.set(userModel);
            }
        });
    }

    @Override
    public User getUserModel() {
        return userModel;
    }

    @Override
    public boolean isUserContextValid() {
        return (userModel != null && userModel.getId() != null);
    }
}