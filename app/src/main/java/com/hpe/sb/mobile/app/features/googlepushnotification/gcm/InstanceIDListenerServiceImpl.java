package com.hpe.sb.mobile.app.features.googlepushnotification.gcm;

import android.content.Intent;
import android.util.Log;
import com.google.android.gms.iid.InstanceIDListenerService;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.features.googlepushnotification.GooglePushNotificationClient;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import javax.inject.Inject;

public class InstanceIDListenerServiceImpl extends InstanceIDListenerService {

    private static final String TAG = "InstIDListenerSrv";

    @Inject
    protected GooglePushNotificationClient googlePushNotificationClient;

    @Override
    public void onCreate() {
        super.onCreate();
        ((ServiceBrokerApplication) getApplicationContext()).getServiceBrokerComponent().inject(this);
    }

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. This call is initiated by the
     * InstanceID provider.
     */
    @Override
    public void onTokenRefresh() {
        // clear old registration ids
        Log.i(TAG, "All GCM registrations should be refreshed. Clearing all current registrations.");
        googlePushNotificationClient.clearAllRegistration()
                .observeOn(Schedulers.io())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        Log.i(TAG, "Starting new registration to GCM");
                        // Fetch updated Instance ID registration token.
                        Intent intent = new Intent(InstanceIDListenerServiceImpl.this, GcmRegistrationIntentService.class);
                        startService(intent);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "Failed to clear all GCM registrations", throwable);
                    }
                });
    }
}
