package com.hpe.sb.mobile.app.infra.baseActivities;

import android.os.Bundle;

import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.infra.restclient.RestClientQueue;

import javax.inject.Inject;

/**
 * Created by malikdav on 20/03/2016.
 */
public abstract class BaseHttpActivity extends BaseActivity {

    @Inject
    protected RestClientQueue restClientQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ServiceBrokerApplication) getApplicationContext()).getServiceBrokerComponent().inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(restClientQueue !=null) {
            restClientQueue.cancelAll(this);
        }
    }
}
