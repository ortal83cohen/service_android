package com.hpe.sb.mobile.app.common.dataClients.user.dbClient;

import android.util.Log;

import com.hpe.sb.mobile.app.infra.dataClients.SingletonDbClient;
import com.hpe.sb.mobile.app.infra.dataClients.Identifiable;
import com.hpe.sb.mobile.app.serverModel.user.useritems.UserItems;
import com.hpe.sb.mobile.app.infra.dataClients.DataBlob;
import com.hpe.sb.mobile.app.common.utils.ClientDataFlowUtils;
import com.hpe.sb.mobile.app.common.utils.LogTagConstants;

import java.util.List;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

/**
 * Created by salemo on 31/05/2016.
 *
 */
public class UserItemsDbClientImpl implements UserItemsDbClient {
    private SingletonDbClient<UserItems> userItemsSingletonDbClient;

    public UserItemsDbClientImpl(SingletonDbClient<UserItems> userItemsDbClient) {
        this.userItemsSingletonDbClient = userItemsDbClient;
    }

    @Override
    public Observable<DataBlob<UserItems>> getUserItems() {
        return userItemsSingletonDbClient
                .getItem().doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(LogTagConstants.USER_ITEMS,
                                "unable to get user items", throwable);
                    }
                });
    }

    @Override
    public Observable<UserItems> removeUserItemFromDb(String itemId) {
        Func1<DataBlob<UserItems>, UserItems> blobToDataFunction = ClientDataFlowUtils
                .singleItemBlobToDataFunction();
        //create get user items Observable
        Observable<UserItems> userItemsAfterRemove = getUserItems()
                .filter(getNotNullFilter())
                .map(blobToDataFunction)
                .map(getFunctionToRemoveUserItemFromUserItems(itemId))
                .cache();

        //get the function to remove user items from DB
        Observable<Void> setDbUserItems = userItemsAfterRemove
                .flatMap(getFunctionToSetDbUserItems())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(LogTagConstants.USER_ITEMS, "error when updating db", throwable);
                    }
                }).cache();

        // zip the observables in order to enable one subscribe on them
        return Observable.zip(userItemsAfterRemove, setDbUserItems, new Func2<UserItems, Void, UserItems>() {
            @Override
            public UserItems call(UserItems userItems, Void aVoid) {
                return userItems;
            }
        }).doOnError(new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e(LogTagConstants.USER_ITEMS,
                        "unable to remove user items", throwable);
            }
        }).subscribeOn(Schedulers.computation());
    }

    @Override
    public Observable<Void> setDbUserItems(final UserItems updatedUserItems) {
        return userItemsSingletonDbClient.set(updatedUserItems).doOnError(new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e(LogTagConstants.USER_ITEMS,
                        "unable to set user items with updatedUserItems: " + updatedUserItems, throwable);
            }
        });
    }

    private Func1<UserItems, Observable<Void>> getFunctionToSetDbUserItems() {
        return new Func1<UserItems, Observable<Void>>() {
            @Override
            public Observable<Void> call(UserItems updatedUserItems) {
                Log.d(LogTagConstants.USER_ITEMS,
                        "updating db UserItems with:" + updatedUserItems.toString());
                return setDbUserItems(updatedUserItems);
            }
        };
    }

    //get the Func to Remove UserItem from the UserItems
    private Func1<UserItems, UserItems> getFunctionToRemoveUserItemFromUserItems(
            final String itemId) {
        return new Func1<UserItems, UserItems>() {
            @Override
            public UserItems call(UserItems userItems) {
                if (userItems != null) {
                    removeItemByIdFromUserItems(userItems, itemId);
                }
                return userItems;
            }
        };
    }

    private boolean removeItemByIdFromUserItems(UserItems userItems, String itemId) {
        return locateAndRemove(itemId, userItems.getApprovals()) || locateAndRemove(itemId,
                userItems.getRequestFeedbackItems()) ||
                locateAndRemove(itemId, userItems.getRequestResolveItems()) || locateAndRemove(
                itemId, userItems.getActiveRequests());
    }

    private boolean locateAndRemove(String itemId, List<? extends Identifiable> approvals) {
        for (Identifiable item : approvals) {
            if (itemId.equals(item.getId())) {
                approvals.remove(item);
                return true;
            }
        }
        return false;
    }

    private Func1<DataBlob<UserItems>, Boolean> getNotNullFilter() {
        return new Func1<DataBlob<UserItems>, Boolean>() {
            @Override
            public Boolean call(DataBlob<UserItems> mgUserItemsDataBlob) {
                return mgUserItemsDataBlob != null;
            }
        };
    }
}
