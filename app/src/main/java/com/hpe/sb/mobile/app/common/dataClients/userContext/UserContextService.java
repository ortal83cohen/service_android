package com.hpe.sb.mobile.app.common.dataClients.userContext;


import com.hpe.sb.mobile.app.serverModel.user.User;

import rx.Observable;

/**
 * Created by salemo on 23/03/2016.
 *
 */
public interface UserContextService {

    Observable<Void> setUserModel(User userData);

    User getUserModel();

    boolean isUserContextValid();

    String getLocale();

    void setLocale(String locale);
}