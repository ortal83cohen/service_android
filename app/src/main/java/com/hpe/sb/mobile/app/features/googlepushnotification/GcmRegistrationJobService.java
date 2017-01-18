package com.hpe.sb.mobile.app.features.googlepushnotification;

import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.features.googlepushnotification.gcm.GcmRegistrationIntentService;

import android.content.Intent;
import android.util.Log;

import javax.inject.Inject;

import me.tatarka.support.job.JobParameters;
import me.tatarka.support.job.JobService;
import rx.functions.Action1;

/**
 * Created by cohenort on 20/06/2016.
 */
public class GcmRegistrationJobService extends JobService {

    public static final int GCM_REGISTRATION_JOB_ID = 1;

    private static final String TAG = "Gcm JobScheduler";

    @Inject
    protected GooglePushNotificationClient googlePushNotificationClient;

    public GcmRegistrationJobService() {
    }

    @Override
    public boolean onStartJob(JobParameters params) {

        ((ServiceBrokerApplication) getApplicationContext()).getServiceBrokerComponent()
                .inject(this);
        googlePushNotificationClient.getLastSuccessfulRegistrationTime(this)
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long lastSuccessfulRegistrationTime) {
                        if (lastSuccessfulRegistrationTime == null) {
                            Intent intent = new Intent(getBaseContext(),
                                    GcmRegistrationIntentService.class);
                            getBaseContext().startService(intent);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "ERROR getLastSuccessfulRegistrationTime",throwable);
                    }
                });

        return false;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
