package com.hpe.sb.mobile.app.common.dataClients.user.restClient;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import com.hpe.sb.mobile.app.serverModel.request.RequestForTrackingPage;
import com.hpe.sb.mobile.app.serverModel.user.Feed;
import com.hpe.sb.mobile.app.serverModel.user.useritems.UserItems;
import com.hpe.sb.mobile.app.infra.restclient.BaseRestClient;
import com.hpe.sb.mobile.app.infra.restclient.HttpLookupUtils;
import com.hpe.sb.mobile.app.infra.restclient.RestClientQueue;
import com.hpe.sb.mobile.app.infra.restclient.RestService;
import com.hpe.sb.mobile.app.infra.restclient.customrequest.RequestFactory;
import com.hpe.sb.mobile.app.common.utils.LogTagConstants;

import rx.Observable;
import rx.functions.Action1;

public class UserRestClientImpl extends BaseRestClient implements UserRestClient {

    public static final String USER_ITEMS_REST_ERROR = "Loading todo cards (user items) from REST failed.";
    public static final String SMART_FREED_REST_ERROR = "Loading SMART FREED from REST failed.";
    public static final String USER_ITEMS_REST_RETURNED = "User items from REST: %s active requests, %s approvals, %s feedbackItems, %s resolveItems";
    public static final String SMART_FREED_REST_RETURNED = "SMART_FREED_REST_RETURNED";
    private final RestService restService;
    private final RequestFactory requestFactory;

    public UserRestClientImpl(RestClientQueue restClientQueue, RequestFactory requestFactory, HttpLookupUtils httpLookupUtils, RestService restService) {
        super(restClientQueue, httpLookupUtils);
        this.requestFactory = requestFactory;
        this.restService = restService;
    }

    @Override
    public String getClientPrefix() {
        return "user";
    }

    @Override
    public Observable<UserItems> getUserItems(Context context) {
        Observable<UserItems> getMGUserItemsRequest = restService.createGetRequest(getClientPrefix() + "/getUserItems", UserItems.class, context);
        return getMGUserItemsRequest.doOnNext(new Action1<UserItems>() {
            @Override
            public void call(UserItems userItems) {
                if (userItems != null) {
                    Log.d(LogTagConstants.USER_ITEMS, String.format(USER_ITEMS_REST_RETURNED, userItems.getActiveRequests().size(), userItems.getApprovals().size(),
                            userItems.getRequestFeedbackItems().size(), userItems.getRequestResolveItems().size()));
                }
                else {
                    Log.d(LogTagConstants.USER_ITEMS, USER_ITEMS_REST_ERROR);
                }
            }
        });
    }

    @Override
    public Observable<Feed> getSmartFeed(Context context) {
        Observable<Feed> getMGUserItemsRequest = restService.createGetRequest(getClientPrefix() + "/getSmartFeed", Feed.class, context);
        return getMGUserItemsRequest.doOnNext(new Action1<Feed>() {
            @Override
            public void call(Feed feed) {
                if (feed != null) {
                    Log.d(LogTagConstants.SMART_FEED, String.format(SMART_FREED_REST_RETURNED));
                }
                else {
                    Log.d(LogTagConstants.SMART_FEED, SMART_FREED_REST_ERROR);
                }
            }
        });
    }

    @Override
    public Observable<Void> approveTask(Context context, final String taskId) {
        return restService.createPutRequest(getClientPrefix() + "/approveOrDenyTask/" + taskId + "/true", null, Void.class, context).doOnError(new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e(LogTagConstants.REQUEST_CLIENT,
                        "unable to approve task with id:" + taskId, throwable);
            }
        }).cache();
    }

    @Override
    public Observable<Void> denyTask(Context context, final String taskId, String comment) {
        Uri.Builder builder = new Uri.Builder();
        builder.appendQueryParameter("comment", comment);
        String url = getClientPrefix() + "/approveOrDenyTask/" + taskId + "/false" + builder.build();
        return restService.createPutRequest(url, null, Void.class, context).doOnError(new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e(LogTagConstants.REQUEST_CLIENT,
                        "unable to deny task with id:" + taskId, throwable);
            }
        }).cache();
    }

    @Override
    public Observable<RequestForTrackingPage> getApproval(Context context,
                                                          final String taskId, final String userId) {
        String url = getClientPrefix() + "/approval/parentEntity/" + taskId + "/" + userId;
        return restService.createGetRequest(url, RequestForTrackingPage.class, context).doOnError(new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e(LogTagConstants.REQUEST_CLIENT,
                        "unable to get User Approval Parent Entity with taskId:" + taskId + ", userId:" + userId, throwable);
            }
        }).cache();
    }
}

