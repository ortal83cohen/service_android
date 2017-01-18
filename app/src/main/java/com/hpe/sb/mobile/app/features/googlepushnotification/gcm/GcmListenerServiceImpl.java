package com.hpe.sb.mobile.app.features.googlepushnotification.gcm;

import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.gcm.GcmListenerService;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.features.googlepushnotification.GooglePushNotificationClient;
import com.hpe.sb.mobile.app.features.googlepushnotification.model.MGGcmMessageData;
import com.hpe.sb.mobile.app.features.googlepushnotification.model.MGGooglePushNotificationIdentifier;
import com.hpe.sb.mobile.app.common.utils.JsonTranslation;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import javax.inject.Inject;

public class GcmListenerServiceImpl extends GcmListenerService {

    private static final String TAG = "GcmListenerService";

    @Inject
    protected GooglePushNotificationClient googlePushNotificationClient;

    @Override
    public void onCreate() {
        super.onCreate();
        ((ServiceBrokerApplication) getApplicationContext()).getServiceBrokerComponent().inject(this);
    }

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String offlineText = data.getString("offlineText");
        String googlePushNotificationIdentifierJSONString = data.getString("googlePushNotificationIdentifierJSON");
        MGGooglePushNotificationIdentifier googlePushNotificationIdentifier = JsonTranslation.jsonString2Object(googlePushNotificationIdentifierJSONString, MGGooglePushNotificationIdentifier.class);
        MGGcmMessageData gcmMessageData = new MGGcmMessageData(googlePushNotificationIdentifier, offlineText);

        googlePushNotificationClient.displayNotification(this, from, gcmMessageData)
                .observeOn(Schedulers.io())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Log.i(TAG, "Successfully displayed notification");
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "Failed to display notification", throwable);
                    }
                });

    }
}
