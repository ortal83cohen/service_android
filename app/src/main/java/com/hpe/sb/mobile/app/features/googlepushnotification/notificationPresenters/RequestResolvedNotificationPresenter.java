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
import com.hpe.sb.mobile.app.common.utils.HtmlUtil;
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

public class RequestResolvedNotificationPresenter extends AbstractGooglePushNotificationPresenter {

    public final static String REQUEST_DISPLAY_LABEL = "DisplayLabel";
    public final static String REQUEST_SOLUTION = "Solution";

    @Inject
    protected RouteBundleFactory routeBundleFactory;

    public RequestResolvedNotificationPresenter(RouteBundleFactory routeBundleFactory) {
        this.routeBundleFactory = routeBundleFactory;
    }

    @Override
    public void displayNotification(Context context, MGGcmMessageData gcmMessageData, MGGooglePushNotificationData googlePushNotificationData, UserItem userItem) {
        String requestId = googlePushNotificationData.getEntityId();
        Map<String, String> entityData = googlePushNotificationData.getEntityData();
        List<String> mandatoryProperties = Arrays.asList(REQUEST_DISPLAY_LABEL, REQUEST_SOLUTION);
        if (displayOfflineNotificationIfMissingMandatoryData(context, gcmMessageData, userItem, entityData, mandatoryProperties)) {
            return;
        }

        int notificationId = calcNotificationId(gcmMessageData);

        Bundle requestRouteBundle = routeBundleFactory.createRequestRouteBundle(userItem);
        PendingIntent detailsActivityPendingIntent = createPendingIntentForRouter(context, requestRouteBundle, Routes.REQUEST, notificationId);

        String acceptActionText = context.getString(R.string.request_resolved_accept_action_text);
        PendingIntent acceptRequestPendingIntent = GooglePushNotificationActionsService.createPendingIntentForAcceptRequest(
                context, notificationId, requestId);

        String rejectActionText = context.getString(R.string.request_resolved_reject_action_text);
        PendingIntent rejectRequestPendingIntent = GooglePushNotificationActionsService.createPendingIntentForRejectRequest(
                context, notificationId, requestId);

        String appNameNotificationTitle = context.getString(R.string.app_name_notification_title);
        String requestResolvedNotificationTitle = context.getString(R.string.request_resolved_notification_title);
        NotificationCompat.InboxStyle expandedStyle = new NotificationCompat.InboxStyle();
        expandedStyle.setBigContentTitle(appNameNotificationTitle);
        Spannable notificationTitleSpannable = new SpannableString(requestResolvedNotificationTitle);
        notificationTitleSpannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, requestResolvedNotificationTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        expandedStyle.addLine(notificationTitleSpannable);
        expandedStyle.addLine(entityData.get(REQUEST_DISPLAY_LABEL));
        expandedStyle.addLine(requestId);
        expandedStyle.addLine(HtmlUtil.fromHtml(entityData.get(REQUEST_SOLUTION), null));

        NotificationCompat.Builder notificationBuilder  = getBuilderForRequestResolvedNotification(context)
                .setWhen(googlePushNotificationData.getTimestamp())
                .setContentIntent(detailsActivityPendingIntent)
                .setStyle(expandedStyle)
                .addAction(R.drawable.icon_deny, rejectActionText, rejectRequestPendingIntent)
                .addAction(R.drawable.icon_approve, acceptActionText, acceptRequestPendingIntent);

        notify(context, notificationId, notificationBuilder);
    }

    private NotificationCompat.Builder getBuilderForRequestResolvedNotification(Context context) {
        Bitmap notificationIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.push_notif_done);
        String requestResolvedNotificationTitle = context.getString(R.string.request_resolved_notification_title);
        return getBuilderForBasicNotification(context, notificationIcon, requestResolvedNotificationTitle);
    }

    @Override
    public void displayOfflineNotification(Context context, MGGcmMessageData gcmMessageData) {
        int notificationId = calcNotificationId(gcmMessageData);
        NotificationCompat.Builder notificationBuilder = getBuilderForRequestResolvedNotification(context);
        Bundle defaultRouteBundle = routeBundleFactory.createDefaultRouteBundle();
        notificationBuilder.setContentIntent(createPendingIntentForRouter(context, defaultRouteBundle, Routes.DEFAULT, notificationId));
        notify(context, notificationId, notificationBuilder);
    }
}
