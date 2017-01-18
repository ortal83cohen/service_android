package com.hpe.sb.mobile.app.features.googlepushnotification.restClient;

import android.content.Context;
import com.hpe.sb.mobile.app.features.googlepushnotification.model.MGGcmSender;
import com.hpe.sb.mobile.app.features.googlepushnotification.model.MGGooglePushNotificationData;
import com.hpe.sb.mobile.app.features.googlepushnotification.model.MGGooglePushNotificationIdentifier;
import rx.Observable;

public interface GooglePushNotificationRestClient {

    Observable<MGGcmSender> getSender(Context context);

    Observable<Void> addRegistrationId(Context context, String userId, String registrationId, String gcmSenderId);

    Observable<MGGooglePushNotificationData> getNotificationData(Context context, MGGooglePushNotificationIdentifier googlePushNotificationIdentifier, String userId, String registrationId);
}
