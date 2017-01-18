package com.hpe.sb.mobile.app.features.googlepushnotification.gcm;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.features.googlepushnotification.GooglePushNotificationClient;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

import javax.inject.Inject;

public class GcmRegistrationIntentService extends IntentService {

    private static final String TAG = "GcmRegIntentService";

    @Inject
    protected GooglePushNotificationClient googlePushNotificationClient;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     * The name passed to super is used to name the worker thread, important only for debugging.
     */
    public GcmRegistrationIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ((ServiceBrokerApplication) getApplicationContext()).getServiceBrokerComponent().inject(this);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (isGooglePlayServicesAvailable()) {
            Log.i(TAG, "Starting registration to GCM");
            googlePushNotificationClient.register(this)
                    .observeOn(Schedulers.io())
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            Log.i(TAG, "Successful registration to GCM");
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Log.e(TAG, "Failed to register to GCM", throwable);
                        }
                    });
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, don't register to GCM and the device wouldn't get push notifications.
     */
    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            Log.i(TAG, "Push notification would not be available for this device. Result code: " + resultCode);
            return false;
        }
        return true;
    }
}
