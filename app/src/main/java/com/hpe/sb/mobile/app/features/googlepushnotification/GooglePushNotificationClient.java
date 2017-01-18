package com.hpe.sb.mobile.app.features.googlepushnotification;

import com.hpe.sb.mobile.app.features.googlepushnotification.model.GcmRegistrationPerSender;
import com.hpe.sb.mobile.app.features.googlepushnotification.model.MGGcmMessageData;
import com.hpe.sb.mobile.app.features.googlepushnotification.model.MGGcmSender;

import android.content.Context;

import rx.Observable;

public interface GooglePushNotificationClient {

    Observable<Long> getLastSuccessfulRegistrationTime(Context context);

    Observable<Void> register(Context context);

    Observable<GcmRegistrationPerSender> getGcmRegistrationForCurrentSender(final Context context);

    Observable<Void> displayNotification(Context context, String fromSender, MGGcmMessageData gcmMessageData);

    Observable<Void> clearAllRegistration();

    Observable<Void> initializeRegistrationsAfterLogin();
}
