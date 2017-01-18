package com.hpe.sb.mobile.app.features.googlepushnotification.sharedPreferences;

import com.hpe.sb.mobile.app.features.googlepushnotification.model.GcmRegistrationPerSender;
import com.hpe.sb.mobile.app.features.googlepushnotification.model.MGGcmSender;

import rx.Observable;

public interface GooglePushNotificationSharedPrefClient {

    Observable<MGGcmSender> getSender();

    Observable<Void> setSender(MGGcmSender gcmSender);

    Observable<Long> getLastSuccessfulRegistrationTime();

    Observable<Void> setLastSuccessfulRegistrationTime();

    Observable<GcmRegistrationPerSender> getGcmRegistration(String senderId);

    Observable<Void> setGcmRegistration(String senderId, GcmRegistrationPerSender gcmRegistrationPerSender);

    Observable<Void> clearAllRegistrations();

    Observable<Void> resetIsSentToServerForAllRegistrations();

}
