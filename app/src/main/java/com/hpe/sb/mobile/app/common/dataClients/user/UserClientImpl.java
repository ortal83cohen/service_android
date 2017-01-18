package com.hpe.sb.mobile.app.common.dataClients.user;

import com.hpe.sb.mobile.app.common.dataClients.user.dbClient.UserItemsDbClient;
import com.hpe.sb.mobile.app.common.dataClients.user.restClient.UserRestClient;
import com.hpe.sb.mobile.app.common.utils.ClientDataFlowUtils;
import com.hpe.sb.mobile.app.common.utils.LogTagConstants;
import com.hpe.sb.mobile.app.infra.dataClients.DataBlob;
import com.hpe.sb.mobile.app.infra.dataClients.SingletonDbClient;
import com.hpe.sb.mobile.app.infra.db.DbHelper;
import com.hpe.sb.mobile.app.infra.db.GeneralBlobModel;
import com.hpe.sb.mobile.app.serverModel.user.Feed;
import com.hpe.sb.mobile.app.serverModel.user.useritems.ApprovalUserItem;
import com.hpe.sb.mobile.app.serverModel.request.RequestForTrackingPage;
import com.hpe.sb.mobile.app.common.dataClients.userContext.UserContextService;
import com.hpe.sb.mobile.app.serverModel.user.useritems.FollowedActiveRequestItem;
import com.hpe.sb.mobile.app.serverModel.user.useritems.FollowedResolvedRequestItem;
import com.hpe.sb.mobile.app.serverModel.user.useritems.RequestActiveUserItem;
import com.hpe.sb.mobile.app.serverModel.user.useritems.RequestFeedbackUserItem;
import com.hpe.sb.mobile.app.serverModel.user.useritems.RequestResolveUserItem;
import com.hpe.sb.mobile.app.serverModel.user.useritems.UserItem;
import com.hpe.sb.mobile.app.serverModel.user.useritems.UserItems;

import android.content.ContentResolver;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by chovel on 26/04/2016.
 */
public class UserClientImpl implements UserClient {

    public static final TimeUnit DB_MAX_AGE_TIME_UNIT = TimeUnit.MINUTES;

    public static final int DB_MAX_AGE = 30;

    private UserRestClient userRestClient;

    private UserItemsDbClient userItemsDbClient;
    private SingletonDbClient<Feed> singleItemSmartFeedDbClient;
    private final UserContextService userContextService;

    public UserClientImpl(UserRestClient userRestClient, ContentResolver contentResolver, UserItemsDbClient userItemsDbClient,
                          UserContextService userContextService) {
        this.userItemsDbClient = userItemsDbClient;
        this.singleItemSmartFeedDbClient = new SingletonDbClient<>(Feed.class,
                contentResolver, LogTagConstants.SMART_FEED,
                Feed.SMART_FEED_STATIC_ID, GeneralBlobModel.TYPE_SMART_FEED, new DbHelper(contentResolver));
        this.userRestClient = userRestClient;
        this.userContextService = userContextService;
    }

    @Override
    public Observable<UserItems> getUserItems(final Context context, boolean forceRefresh) {
        Observable<DataBlob<UserItems>> userItemsFromDb = userItemsDbClient.getUserItems();
        Observable<UserItems> sendRest = userRestClient.getUserItems(context);
        Func1<UserItems, Observable<Void>> functionToSetDbUserItems = getFunctionToSetDbUserItems();
        if (forceRefresh) {
            return ClientDataFlowUtils
                    .getFromRestAndUpdateDb(LogTagConstants.USER_ITEMS, sendRest,
                            functionToSetDbUserItems).
                            map(postProcessUserItemsResponse());
        } else {
            return ClientDataFlowUtils
                    .getFromCacheAndFromRestIfOutdated(LogTagConstants.USER_ITEMS,
                            userItemsFromDb, sendRest,
                            functionToSetDbUserItems, DB_MAX_AGE,
                            DB_MAX_AGE_TIME_UNIT).
                            map(postProcessUserItemsResponse());
        }
    }

    @Override
    public void deleteItemAndUpdateUserItems(final Context context, UserItem userItem) {
        userItemsDbClient.removeUserItemFromDb(userItem.getId()).
                observeOn(AndroidSchedulers.mainThread()).subscribe();
        getUserItems(context, true).observeOn(AndroidSchedulers.mainThread()).subscribe();
    }

    @Override
    public Observable<Feed> getSmartFeed(final Context context, boolean forceRefresh) {
        Observable<Feed> sendRest = userRestClient.getSmartFeed(context);
        Observable<DataBlob<Feed>> smartFeedFromDb = singleItemSmartFeedDbClient
                .getItem();
        Func1<Feed, Observable<Void>> functionToSetDbSmartFeed = getFunctionToSetDbSmartFeed();

        if (forceRefresh) {
            return ClientDataFlowUtils.getFromRestAndUpdateDb(LogTagConstants.SMART_FEED,
                    sendRest, functionToSetDbSmartFeed);
        } else {
            return ClientDataFlowUtils.getFromCacheAndFromRestIfOutdated(LogTagConstants.SMART_FEED, smartFeedFromDb, sendRest,
                    functionToSetDbSmartFeed, DB_MAX_AGE, DB_MAX_AGE_TIME_UNIT).
                    map(postProcessSmartFeedResponse());
        }
    }

    @NonNull
    private Func1<Feed, Feed> postProcessSmartFeedResponse() {
        return new Func1<Feed, Feed>() {
            @Override
            public Feed call(Feed mgUserItemsList) {
                return mgUserItemsList != null ? mgUserItemsList :
                        new Feed();
            }
        };
    }

    @NonNull
    private Func1<UserItems, UserItems> postProcessUserItemsResponse() {
        return new Func1<UserItems, UserItems>() {
            @Override
            public UserItems call(UserItems userItemsList) {
                return userItemsList != null ? userItemsList :
                        new UserItems(new ArrayList<RequestActiveUserItem>(),
                                new ArrayList<ApprovalUserItem>(),
                                new ArrayList<RequestFeedbackUserItem>(),
                                new ArrayList<RequestResolveUserItem>(),
                                new ArrayList<FollowedActiveRequestItem>(),
                                new ArrayList<FollowedResolvedRequestItem>());
            }
        };
    }

    private Func1<UserItems, Observable<Void>> getFunctionToSetDbUserItems() {
        return new Func1<UserItems, Observable<Void>>() {
            @Override
            public Observable<Void> call(UserItems updatedUserItems) {
                Log.d(LogTagConstants.USER_ITEMS,
                        "updating db UserItems with:" + updatedUserItems.toString());
                return userItemsDbClient.setDbUserItems(updatedUserItems);
            }
        };
    }

    private Func1<Feed, Observable<Void>> getFunctionToSetDbSmartFeed() {
        return new Func1<Feed, Observable<Void>>() {
            @Override
            public Observable<Void> call(Feed updatedSmartFeed) {
                Log.d(LogTagConstants.SMART_FEED,
                        "updating Db Feed with:" + updatedSmartFeed.toString());
                return singleItemSmartFeedDbClient.set(updatedSmartFeed);
            }
        };
    }

    @Override
    public Observable<UserItems> approveTask(Context context, final String taskId) {
        return userRestClient.approveTask(context, taskId)
                .flatMap(new Func1<Void, Observable<UserItems>>() {
                    @Override
                    public Observable<UserItems> call(Void aVoid) {
                        return userItemsDbClient.removeUserItemFromDb(taskId);
                    }
                }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<UserItems> denyTask(Context context, final String taskId, String comment) {
        return userRestClient.denyTask(context, taskId, comment)
                .flatMap(new Func1<Void, Observable<UserItems>>() {
                    @Override
                    public Observable<UserItems> call(Void aVoid) {
                        return userItemsDbClient.removeUserItemFromDb(taskId);
                    }
                }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<RequestForTrackingPage> getApproval(Context context, String taskId) {
        return userRestClient.getApproval(context, taskId, userContextService.getUserModel().getId())
                .subscribeOn(Schedulers.io());
    }
}
