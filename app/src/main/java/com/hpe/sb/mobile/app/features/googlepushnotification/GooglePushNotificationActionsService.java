package com.hpe.sb.mobile.app.features.googlepushnotification;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.features.googlepushnotification.model.GooglePushNotificationAction;
import com.hpe.sb.mobile.app.serverModel.user.useritems.UserItem;
import com.hpe.sb.mobile.app.serverModel.user.useritems.UserItems;
import com.hpe.sb.mobile.app.features.request.RequestClient;
import com.hpe.sb.mobile.app.features.router.RouteBundleFactory;
import com.hpe.sb.mobile.app.features.router.RouterActivity;
import com.hpe.sb.mobile.app.common.dataClients.user.UserClient;
import rx.Subscriber;

import javax.inject.Inject;

public class GooglePushNotificationActionsService extends IntentService {

    public static final String TAG = "NotifActionsService";

    public final static String NOTIFICATION_ID_EXTRA = "NotificationId";
    public final static String REQUEST_ID_EXTRA = "RequestId";
    public final static String TASK_ID_EXTRA = "TaskId";
    public final static String MG_ITEM_EXTRA = "Item";

    @Inject
    protected RequestClient requestClient;

    @Inject
    protected UserClient userClient;

    @Inject
    protected RouteBundleFactory routeBundleFactory;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * name - Used to name the worker thread, important only for debugging.
     */
    public GooglePushNotificationActionsService() {
        super(GooglePushNotificationActionsService.class.getName());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((ServiceBrokerApplication) getApplicationContext()).getServiceBrokerComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int notificationId = intent.getIntExtra(NOTIFICATION_ID_EXTRA, -1);
        if (notificationId != -1) {
            notificationManager.cancel(notificationId);
        }
        
        // in order to close the notification drawer:
        sendBroadcast(new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS));

        String action = intent.getAction();
        switch (action) {
            case GooglePushNotificationAction.MARK_REQUEST_AS_SOLVED: // same as ACCEPT_REQUEST
            case GooglePushNotificationAction.ACCEPT_REQUEST: {
                String requestId = intent.getStringExtra(REQUEST_ID_EXTRA);
                if (requestId == null) {
                    Log.e(TAG, "Request id is missing. Unable to perform: " + action);
                    return;
                }
                requestClient.acceptRequest(this, requestId).subscribe(createSubscriber(action));
                break;
            }
            case GooglePushNotificationAction.REJECT_REQUEST: {
                String requestId = intent.getStringExtra(REQUEST_ID_EXTRA);
                if (requestId == null) {
                    Log.e(TAG, "Request id is missing. Unable to perform: " + action);
                    return;
                }
                requestClient.rejectRequest(this, requestId).subscribe(createSubscriber(action));
                break;
            }
            case GooglePushNotificationAction.COMMENT_ON_REQUEST: {
                UserItem userItem = intent.getParcelableExtra(MG_ITEM_EXTRA);
                if (userItem == null) {
                    Log.e(TAG, "MG item is missing. Unable to perform: " + action);
                    return;
                }
                Bundle commentOnRequestRouteBundle = routeBundleFactory.createCommentOnRequestRouteBundle(userItem);
                Intent routerActivityIntent = RouterActivity.createIntent(this, commentOnRequestRouteBundle);
                routerActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(routerActivityIntent);
                break;
            }
            case GooglePushNotificationAction.APPROVE_REQUEST: {
                String taskId = intent.getStringExtra(TASK_ID_EXTRA);
                if (taskId == null) {
                    Log.e(TAG, "Task id is missing. Unable to perform: " + action);
                    return;
                }
                userClient.approveTask(this, taskId).subscribe(createSubscriber(action));
                break;
            }
            case GooglePushNotificationAction.DENY_REQUEST: {
                UserItem userItem = intent.getParcelableExtra(MG_ITEM_EXTRA);
                if (userItem == null) {
                    Log.e(TAG, "MG item is missing. Unable to perform: " + action);
                    return;
                }
                Bundle requestWithDenyDialogRouteBundle = routeBundleFactory.createRequestWithDenyDialogRouteBundle(userItem);
                Intent routerActivityIntent = RouterActivity.createIntent(this, requestWithDenyDialogRouteBundle);
                routerActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(routerActivityIntent);
                break;
            }
            default: {
                Log.e(TAG, "Error: Unidentified notification action: " + action);
                break;
            }
        }
    }

    private Subscriber<UserItems> createSubscriber(final String action) {
        return new Subscriber<UserItems>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, action + " has failed", e);
            }

            @Override
            public void onNext(UserItems userItems) {
                Log.i(TAG, action + " was performed successfully");
            }
        };
    }

    public static PendingIntent createPendingIntentForAcceptRequest(Context context, int notificationId, String requestId) {
        Bundle bundle = new Bundle();
        bundle.putString(REQUEST_ID_EXTRA, requestId);
        return createPendingIntent(context, notificationId, bundle, GooglePushNotificationAction.ACCEPT_REQUEST);
    }

    public static PendingIntent createPendingIntentForRejectRequest(Context context, int notificationId, String requestId) {
        Bundle bundle = new Bundle();
        bundle.putString(REQUEST_ID_EXTRA, requestId);
        return createPendingIntent(context, notificationId, bundle, GooglePushNotificationAction.REJECT_REQUEST);
    }

    public static PendingIntent createPendingIntentForMarkRequestAsSolved(Context context, int notificationId, String requestId) {
        Bundle bundle = new Bundle();
        bundle.putString(REQUEST_ID_EXTRA, requestId);
        return createPendingIntent(context, notificationId, bundle, GooglePushNotificationAction.MARK_REQUEST_AS_SOLVED);
    }

    public static PendingIntent createPendingIntentForCommentOnRequest(Context context, int notificationId, UserItem userItem) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(MG_ITEM_EXTRA, userItem);
        return createPendingIntent(context, notificationId, bundle, GooglePushNotificationAction.COMMENT_ON_REQUEST);
    }

    public static PendingIntent createPendingIntentForApproveRequest(Context context, int notificationId, String taskId) {
        Bundle bundle = new Bundle();
        bundle.putString(TASK_ID_EXTRA, taskId);
        return createPendingIntent(context, notificationId, bundle, GooglePushNotificationAction.APPROVE_REQUEST);
    }

    public static PendingIntent createPendingIntentForDenyRequest(Context context, int notificationId, UserItem userItem) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(MG_ITEM_EXTRA, userItem);
        return createPendingIntent(context, notificationId, bundle, GooglePushNotificationAction.DENY_REQUEST);
    }

    private static PendingIntent createPendingIntent(Context context, int notificationId, Bundle bundle,
                                                     @GooglePushNotificationAction.ActionName String action) {
        bundle.putInt(NOTIFICATION_ID_EXTRA, notificationId);
        Intent intent = new Intent(context, GooglePushNotificationActionsService.class);
        intent.setAction(action);
        intent.putExtras(bundle);
        return PendingIntent.getService(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
