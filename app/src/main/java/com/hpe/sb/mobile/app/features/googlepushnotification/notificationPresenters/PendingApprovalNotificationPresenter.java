package com.hpe.sb.mobile.app.features.googlepushnotification.notificationPresenters;

import android.app.PendingIntent;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.features.googlepushnotification.GooglePushNotificationActionsService;
import com.hpe.sb.mobile.app.features.googlepushnotification.model.MGGcmMessageData;
import com.hpe.sb.mobile.app.features.googlepushnotification.model.MGGooglePushNotificationData;
import com.hpe.sb.mobile.app.serverModel.user.useritems.UserItem;
import com.hpe.sb.mobile.app.features.router.RouteBundleFactory;
import com.hpe.sb.mobile.app.features.router.Routes;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class PendingApprovalNotificationPresenter extends AbstractGooglePushNotificationPresenter {

    public final static String REQUESTED_BY_PERSON = "RequestedByPerson";
    public final static String PARENT_ENTITY_ID = "ParentEntityId";
    public final static String PARENT_ENTITY_TYPE = "ParentEntityType";
    public final static String PARENT_ENTITY_DISPLAY_LABEL = "ParentEntityDisplayLabel";
    public final static String TASK_DISPLAY_LABEL = "TaskDisplayLabel";

    @Inject
    protected RouteBundleFactory routeBundleFactory;

    public PendingApprovalNotificationPresenter(RouteBundleFactory routeBundleFactory) {
        this.routeBundleFactory = routeBundleFactory;
    }

    @Override
    public void displayNotification(Context context, MGGcmMessageData gcmMessageData, MGGooglePushNotificationData googlePushNotificationData, UserItem userItem) {
        String taskId = googlePushNotificationData.getEntityId();
        Map<String, String> entityData = googlePushNotificationData.getEntityData();
        List<String> mandatoryProperties = Arrays.asList(REQUESTED_BY_PERSON, PARENT_ENTITY_ID, PARENT_ENTITY_TYPE, PARENT_ENTITY_DISPLAY_LABEL, TASK_DISPLAY_LABEL);
        if (displayOfflineNotificationIfMissingMandatoryData(context, gcmMessageData, userItem, entityData, mandatoryProperties)) {
            return;
        }

        int notificationId = calcNotificationId(gcmMessageData);

        Bundle requestRouteBundle = routeBundleFactory.createRequestRouteBundle(userItem);
        PendingIntent detailsActivityPendingIntent = createPendingIntentForRouter(context, requestRouteBundle, Routes.REQUEST, notificationId);

        String approveActionText = context.getString(R.string.pending_approval_approve_action_text);
        PendingIntent approveRequestPendingIntent = GooglePushNotificationActionsService.createPendingIntentForApproveRequest(
                context, notificationId, taskId);

        String denyActionText = context.getString(R.string.pending_approval_deny_action_text);
        PendingIntent denyRequestPendingIntent = GooglePushNotificationActionsService.createPendingIntentForDenyRequest(
                context, notificationId, userItem);

        String appNameNotificationTitle = context.getString(R.string.app_name_notification_title);
        String pendingApprovalNotificationTitle = context.getString(R.string.pending_approval_notification_title);
        NotificationCompat.InboxStyle expandedStyle = new NotificationCompat.InboxStyle();
        expandedStyle.setBigContentTitle(appNameNotificationTitle);
        Spannable notificationTitleSpannable = new SpannableString(pendingApprovalNotificationTitle);
        notificationTitleSpannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, pendingApprovalNotificationTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        expandedStyle.addLine(notificationTitleSpannable);
        expandedStyle.addLine(entityData.get(PARENT_ENTITY_DISPLAY_LABEL));
        expandedStyle.addLine(entityData.get(PARENT_ENTITY_ID));
        expandedStyle.addLine(context.getString(R.string.requestedBy) + " " + entityData.get(REQUESTED_BY_PERSON));
        expandedStyle.addLine(context.getString(R.string.approvalTask) + " " + entityData.get(TASK_DISPLAY_LABEL));

        NotificationCompat.Builder notificationBuilder  = getBuilderForPendingApprovalNotification(context)
                .setWhen(googlePushNotificationData.getTimestamp())
                .setContentIntent(detailsActivityPendingIntent)
                .setStyle(expandedStyle)
                .addAction(R.drawable.icon_deny, denyActionText, denyRequestPendingIntent)
                .addAction(R.drawable.icon_approve, approveActionText, approveRequestPendingIntent);

        notify(context, notificationId, notificationBuilder);
    }

    private NotificationCompat.Builder getBuilderForPendingApprovalNotification(Context context) {
        Bitmap notificationIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.push_notif_pending_approval);
        String pendingApprovalNotificationTitle = context.getString(R.string.pending_approval_notification_title);
        return getBuilderForBasicNotification(context, notificationIcon, pendingApprovalNotificationTitle);
    }

    @Override
    public void displayOfflineNotification(Context context, MGGcmMessageData gcmMessageData) {
        int notificationId = calcNotificationId(gcmMessageData);
        NotificationCompat.Builder notificationBuilder = getBuilderForPendingApprovalNotification(context);
        Bundle defaultRouteBundle = routeBundleFactory.createDefaultRouteBundle();
        notificationBuilder.setContentIntent(createPendingIntentForRouter(context, defaultRouteBundle, Routes.DEFAULT, notificationId));
        notify(context, notificationId, notificationBuilder);
    }
}
