package com.hpe.sb.mobile.app.features.googlepushnotification.notificationPresenters;

import android.content.Context;
import com.hpe.sb.mobile.app.features.googlepushnotification.model.MGGcmMessageData;
import com.hpe.sb.mobile.app.features.googlepushnotification.model.MGGooglePushNotificationData;
import com.hpe.sb.mobile.app.serverModel.user.useritems.UserItem;

public interface GooglePushNotificationPresenter {

    void displayNotification(Context context, MGGcmMessageData gcmMessageData, MGGooglePushNotificationData googlePushNotificationData, UserItem userItem);

    void displayOfflineNotification(Context context, MGGcmMessageData gcmMessageData);

}
