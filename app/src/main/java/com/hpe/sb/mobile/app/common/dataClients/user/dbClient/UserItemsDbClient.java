package com.hpe.sb.mobile.app.common.dataClients.user.dbClient;

import com.hpe.sb.mobile.app.serverModel.user.useritems.UserItems;
import com.hpe.sb.mobile.app.infra.dataClients.DataBlob;

import rx.Observable;

/**
 * Created by salemo on 31/05/2016.
 *
 */
public interface UserItemsDbClient {

    Observable<DataBlob<UserItems>> getUserItems();

    Observable<UserItems> removeUserItemFromDb(String itemId);

    Observable<Void> setDbUserItems(UserItems updatedUserItems);
}
