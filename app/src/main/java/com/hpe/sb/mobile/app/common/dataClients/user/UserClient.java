package com.hpe.sb.mobile.app.common.dataClients.user;

import com.hpe.sb.mobile.app.serverModel.request.RequestForTrackingPage;
import com.hpe.sb.mobile.app.serverModel.user.Feed;
import com.hpe.sb.mobile.app.serverModel.user.useritems.UserItem;
import com.hpe.sb.mobile.app.serverModel.user.useritems.UserItems;

import android.content.Context;

import rx.Observable;

/**
 * Created by chovel on 26/04/2016.
 */
public interface UserClient {

    Observable<UserItems> getUserItems(Context context, boolean forceRefresh);

    void deleteItemAndUpdateUserItems(Context context, UserItem userItem);

    Observable<Feed> getSmartFeed(Context context, boolean forceRefresh);

    Observable<UserItems> approveTask(Context context, String taskId);

    Observable<UserItems> denyTask(Context context, String taskId, String comment);

    Observable<RequestForTrackingPage> getApproval(Context context, String taskId);
}
