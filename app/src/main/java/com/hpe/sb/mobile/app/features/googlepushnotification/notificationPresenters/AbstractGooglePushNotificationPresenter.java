package com.hpe.sb.mobile.app.features.googlepushnotification.notificationPresenters;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.features.googlepushnotification.model.MGGcmMessageData;
import com.hpe.sb.mobile.app.features.googlepushnotification.model.MGGooglePushNotificationIdentifier;
import com.hpe.sb.mobile.app.serverModel.user.useritems.UserItem;
import com.hpe.sb.mobile.app.features.router.RouterActivity;
import com.hpe.sb.mobile.app.features.router.Routes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractGooglePushNotificationPresenter implements GooglePushNotificationPresenter {

    public static final String TAG = "AbstractNotifPresenter";

    protected boolean displayOfflineNotificationIfMissingMandatoryData(Context context, MGGcmMessageData gcmMessageData,
                                                                       UserItem userItem, Map<String, String> entityData, List<String> mandatoryProperties) {
        if (entityData == null || !isEntityDataValid(entityData, mandatoryProperties) || userItem == null) {
            String error = getErrorsForMissingMandatoryData(userItem, entityData, mandatoryProperties);
            Log.w(TAG, "Displaying offline notification because not all mandatory data was provided. Errors: " + error);
            displayOfflineNotification(context, gcmMessageData);
            return true;
        }
        return false;
    }

    private boolean isEntityDataValid(Map<String, String> entityData, List<String> mandatoryProperties) {
        for (String property : mandatoryProperties) {
            String value = entityData.get(property);
            if (value == null || value.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    protected int calcNotificationId(MGGcmMessageData gcmMessageData) {
        MGGooglePushNotificationIdentifier googlePushNotificationIdentifier = gcmMessageData.getGooglePushNotificationIdentifier();
        return googlePushNotificationIdentifier.hashCode();
    }

    protected PendingIntent createPendingIntentForRouter(Context context, Bundle routeBundle,
                                                         @Routes.RouteCode int routeCode,
                                                         int notificationId) {
        Intent intent = RouterActivity.createIntent(context, routeBundle);
        intent.setAction(Integer.toString(routeCode));
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(RouterActivity.class);
        stackBuilder.addNextIntent(intent);
        return stackBuilder.getPendingIntent(notificationId, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    protected NotificationCompat.Builder getBuilderForBasicNotification(Context context, Bitmap notificationIcon,
                                                                        String notificationTitle) {
        return new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.push_notif_propel)
                .setColor(context.getResources().getColor(R.color.background_color))
                .setLargeIcon(notificationIcon)
                .setContentTitle(context.getString(R.string.app_name_notification_title))
                .setContentText(notificationTitle)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
    }

    protected void notify(Context context, int notificationId, NotificationCompat.Builder notificationBuilder) {
        Notification notification = notificationBuilder.build();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notification);
    }

    private String getErrorsForMissingMandatoryData(UserItem userItem, Map<String, String> entityData, List<String> mandatoryProperties) {
        List<String> errors = new ArrayList<>();
        if (userItem == null) {
            errors.add("MG item is null");
        }
        if (entityData == null) {
            errors.add("Entity data is null");
        } else {
            for (String property : mandatoryProperties) {
                String value = entityData.get(property);
                if (value == null || value.isEmpty()) {
                    errors.add(property + " in entity data is empty");
                }
            }
        }
        String errorsForLog = "";
        for (String error : errors) {
            errorsForLog += error + "; ";
        }
        return errorsForLog;
    }
}
