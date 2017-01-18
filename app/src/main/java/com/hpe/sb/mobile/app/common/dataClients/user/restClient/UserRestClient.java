package com.hpe.sb.mobile.app.common.dataClients.user.restClient;

import android.content.Context;

import com.hpe.sb.mobile.app.serverModel.request.RequestForTrackingPage;
import com.hpe.sb.mobile.app.serverModel.user.Feed;
import com.hpe.sb.mobile.app.serverModel.user.useritems.UserItems;
import rx.Observable;

/**
 * Created by malikdav on 14/03/2016.
 *
 */
public interface UserRestClient {

    Observable<UserItems> getUserItems(Context context);

    Observable<Feed> getSmartFeed(Context context);

    Observable<Void> approveTask(Context context, String taskId);

    Observable<Void> denyTask(Context context, String taskId, String comment);

    Observable<RequestForTrackingPage> getApproval(Context context, String taskId, final String userId);
}
