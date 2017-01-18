package com.hpe.sb.mobile.app.features.googlepushnotification.sharedPreferences;

import com.hpe.sb.mobile.app.common.utils.JsonTranslation;
import com.hpe.sb.mobile.app.features.googlepushnotification.model.GcmRegistrationPerSender;
import com.hpe.sb.mobile.app.features.googlepushnotification.model.MGGcmSender;
import com.hpe.sb.mobile.app.infra.encryption.EncryptionService;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Map;
import java.util.concurrent.Callable;

import rx.Observable;

public class GooglePushNotificationSharedPrefClientImpl implements GooglePushNotificationSharedPrefClient {

    private static final String GCM_REGISTRATIONS_FILE_NAME = "com.hpe.sb.mobile.app.GcmRegistrations";

    public static final String LAST_REGISTRATION_TIME = "LastSuccessfulRegistrationTime";

    private static final String SENDER_ID = "senderId";

    private EncryptionService encryptionService;

    private Context context;

    public GooglePushNotificationSharedPrefClientImpl(Context context, EncryptionService encryptionService) {
        this.context = context;
        this.encryptionService = encryptionService;
    }

    @Override
    public Observable<MGGcmSender> getSender() {
        return Observable.fromCallable(new Callable<MGGcmSender>() {
            @Override
            public MGGcmSender call() throws Exception {
                SharedPreferences sharedPreferences = context.getSharedPreferences(GCM_REGISTRATIONS_FILE_NAME,
                        Context.MODE_PRIVATE);
                String senderId = sharedPreferences.getString(SENDER_ID, null);

                if (senderId == null) {
                    return null;
                } else {
                    return new MGGcmSender(senderId);
                }
            }
        });

    }

    @Override
    public Observable<Void> setSender(final MGGcmSender gcmSender) {
        return Observable.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                SharedPreferences sharedPreferences = context.getSharedPreferences(GCM_REGISTRATIONS_FILE_NAME,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(SENDER_ID, gcmSender.getSenderId());
                editor.commit();
                return null;
            }
        });
    }

    @Override
    public Observable<Long> getLastSuccessfulRegistrationTime() {
        return Observable.fromCallable(new Callable<Long>() {


            @Override
            public Long call() throws Exception {
                SharedPreferences sharedPreferences = context.getSharedPreferences(GCM_REGISTRATIONS_FILE_NAME,
                        Context.MODE_PRIVATE);
                long time = sharedPreferences.getLong(LAST_REGISTRATION_TIME, -1);
                return time != -1 ? time : null;
            }
        });

    }

    @Override
    public Observable<Void> setLastSuccessfulRegistrationTime() {
        return Observable.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                SharedPreferences sharedPreferences = context.getSharedPreferences(GCM_REGISTRATIONS_FILE_NAME,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putLong(LAST_REGISTRATION_TIME, System.currentTimeMillis());
                editor.commit();
                return null;
            }
        });

    }


    @Override
    public Observable<GcmRegistrationPerSender> getGcmRegistration(final String senderId) {
        return Observable.fromCallable(new Callable<GcmRegistrationPerSender>() {
            @Override
            public GcmRegistrationPerSender call() throws Exception {
                SharedPreferences sharedPreferences = context.getSharedPreferences(GCM_REGISTRATIONS_FILE_NAME,
                        Context.MODE_PRIVATE);
                String gcmRegistrationPerSender = sharedPreferences.getString(senderId, null);

                if (gcmRegistrationPerSender == null) {
                    return null;
                } else {
                    return JsonTranslation.jsonString2Object(encryptionService.decrypt(gcmRegistrationPerSender), GcmRegistrationPerSender.class);
                }
            }
        });
    }

    @Override
    public Observable<Void> setGcmRegistration(final String senderId, final GcmRegistrationPerSender gcmRegistrationPerSender) {
        return Observable.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                SharedPreferences sharedPreferences = context.getSharedPreferences(GCM_REGISTRATIONS_FILE_NAME,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(senderId, encryptionService.encrypt(JsonTranslation.object2JsonString(gcmRegistrationPerSender)));
                editor.commit();
                return null;
            }
        });
    }

    @Override
    public Observable<Void> clearAllRegistrations() {

        return Observable.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                SharedPreferences sharedPreferences = context.getSharedPreferences(GCM_REGISTRATIONS_FILE_NAME,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear().commit();
                return null;
            }
        });
    }

    @Override
    public Observable<Void> resetIsSentToServerForAllRegistrations() {

        return Observable.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                SharedPreferences sharedPreferences = context.getSharedPreferences(GCM_REGISTRATIONS_FILE_NAME,
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Map<String, ?> keys = sharedPreferences.getAll();
                for (Map.Entry<String, ?> entry : keys.entrySet()) {
                    if (LAST_REGISTRATION_TIME.equals(entry.getKey()) || SENDER_ID.equals(entry.getKey())) {
                        continue;
                    }
                    GcmRegistrationPerSender gcmRegistrationPerSender = JsonTranslation
                            .jsonString2Object(encryptionService.decrypt(entry.getValue().toString()), GcmRegistrationPerSender.class);
                    gcmRegistrationPerSender.setIsSentToServer(false);

                    editor.putString(entry.getKey(), encryptionService.encrypt(JsonTranslation.object2JsonString(gcmRegistrationPerSender)));
                }
                editor.commit();
                return null;
            }
        });
    }

}
