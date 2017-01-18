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

public class ProvideMoreInfoNotificationPresenter extends AbstractGooglePushNotificationPresenter {

    public final static String REQUEST_DISPLAY_LABEL = "DisplayLabel";
    public final static String REQUEST_LAST_COMMENT = "LastComment";

    @Inject
    protected RouteBundleFactory routeBundleFactory;

    public ProvideMoreInfoNotificationPresenter(RouteBundleFactory routeBundleFactory) {
        this.routeBundleFactory = routeBundleFactory;
    }

    @Override
    public void displayNotification(Context context, MGGcmMessageData gcmMessageData, MGGooglePushNotificationData googlePushNotificationData, UserItem userItem) {
        String requestId = googlePushNotificationData.getEntityId();
        Map<String, String> entityData = googlePushNotificationData.getEntityData();
        List<String> mandatoryProperties = Arrays.asList(REQUEST_DISPLAY_LABEL, REQUEST_LAST_COMMENT);
        if (displayOfflineNotificationIfMissingMandatoryData(context, gcmMessageData, userItem, entityData, mandatoryProperties)) {
            return;
        }

        int notificationId = calcNotificationId(gcmMessageData);

        Bundle requestRouteBundle = routeBundleFactory.createRequestRouteBundle(userItem);
        PendingIntent detailsActivityPendingIntent = createPendingIntentForRouter(context, requestRouteBundle, Routes.REQUEST, notificationId);

        String markAsSolvedActionText = context.getString(R.string.provide_more_information_mark_as_solved_action_text);
        PendingIntent markRequestAsSolvedPendingIntent = GooglePushNotificationActionsService.createPendingIntentForMarkRequestAsSolved(
                context, notificationId, requestId);

        String commentActionText = context.getString(R.string.provide_more_information_comment_action_text);
        PendingIntent commentOnRequestPendingIntent = GooglePushNotificationActionsService.createPendingIntentForCommentOnRequest(
                context, notificationId, userItem);

        String appNameNotificationTitle = context.getString(R.string.app_name_notification_title);
        String provideMoreInfoNotificationTitle = context.getString(R.string.provide_more_information_notification_title);
        NotificationCompat.InboxStyle expandedStyle = new NotificationCompat.InboxStyle();
        expandedStyle.setBigContentTitle(appNameNotificationTitle);
        Spannable notificationTitleSpannable = new SpannableString(provideMoreInfoNotificationTitle);
        notificationTitleSpannable.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, provideMoreInfoNotificationTitle.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        expandedStyle.addLine(notificationTitleSpannable);
        expandedStyle.addLine(entityData.get(REQUEST_DISPLAY_LABEL));
        expandedStyle.addLine(requestId);
        expandedStyle.addLine(HtmlUtil.fromHtml(entityData.get(REQUEST_LAST_COMMENT), null));

        NotificationCompat.Builder notificationBuilder  = getBuilderForProvideMoreInfoNotification(context)
                .setWhen(googlePushNotificationData.getTimestamp())
                .setContentIntent(detailsActivityPendingIntent)
                .setStyle(expandedStyle)
                .addAction(R.drawable.icon_message, commentActionText, commentOnRequestPendingIntent)
                .addAction(R.drawable.icon_approve, markAsSolvedActionText, markRequestAsSolvedPendingIntent);

        notify(context, notificationId, notificationBuilder);
    }

    private NotificationCompat.Builder getBuilderForProvideMoreInfoNotification(Context context) {
        Bitmap notificationIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.push_notif_more_info);
        String provideMoreInfoNotificationTitle = context.getString(R.string.provide_more_information_notification_title);
        return getBuilderForBasicNotification(context, notificationIcon, provideMoreInfoNotificationTitle);
    }

    @Override
    public void displayOfflineNotification(Context context, MGGcmMessageData gcmMessageData) {
        int notificationId = calcNotificationId(gcmMessageData);
        NotificationCompat.Builder notificationBuilder = getBuilderForProvideMoreInfoNotification(context);
        Bundle defaultRouteBundle = routeBundleFactory.createDefaultRouteBundle();
        notificationBuilder.setContentIntent(createPendingIntentForRouter(context, defaultRouteBundle, Routes.DEFAULT, notificationId));
        notify(context, notificationId, notificationBuilder);
    }
}
