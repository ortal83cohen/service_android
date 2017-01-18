package com.hpe.sb.mobile.app.features.googlepushnotification.scheduler;


import com.hpe.sb.mobile.app.features.googlepushnotification.GcmRegistrationJobService;

import android.content.ComponentName;
import android.content.Context;

import me.tatarka.support.job.JobInfo;
import me.tatarka.support.job.JobScheduler;

public class GcmSchedulerServiceImpl implements GcmSchedulerService {

    public static final int SCHEDULER_MAX_TIME = 3600000 * 24 * 7;// max 7 days
    public static final int SCHEDULER_MIN_TIME = 3600000 * 24;// min 24 hours

    @Override
    public void scheduleGCMJob(Context context) {
        JobScheduler jobScheduler = JobScheduler.getInstance(context);

        JobInfo.Builder builder = new JobInfo.Builder(GcmRegistrationJobService.GCM_REGISTRATION_JOB_ID,
                new ComponentName(context, GcmRegistrationJobService.class));
        builder
                .setOverrideDeadline(SCHEDULER_MAX_TIME)
                .setMinimumLatency(SCHEDULER_MIN_TIME)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)// prefer wifi
                .setRequiresCharging(true)
                .setPersisted(true);

        jobScheduler.schedule(builder.build());
    }
}
