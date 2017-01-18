package com.hpe.sb.mobile.app.features.login.activities;

import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.common.uiComponents.metricFont.MetricFontCustomButton;
import com.hpe.sb.mobile.app.features.home.MainActivity;
import com.hpe.sb.mobile.app.features.login.services.ActivationUrlService;
import com.hpe.sb.mobile.app.features.login.services.LogoutService;
import com.hpe.sb.mobile.app.features.router.RouterActivity;
import com.hpe.sb.mobile.app.infra.baseActivities.BaseActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import javax.inject.Inject;

/**
 * Created by cohenort on 05/09/2016.
 */
public class ChangingDestinationActivity extends BaseActivity {

    @Inject
    public LogoutService logoutService;

    @Inject
    public ActivationUrlService activationUrlService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changing_destination);
        ((ServiceBrokerApplication) getApplicationContext()).getServiceBrokerComponent()
                .inject(this);
        MetricFontCustomButton changeButton = (MetricFontCustomButton)findViewById(R.id.change);
        MetricFontCustomButton keepButton = (MetricFontCustomButton) findViewById(R.id.keep);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activationUrlService.updateSuccessfulLoginWithActivationUrl(false);
                activationUrlService.setActivationUrl("");
                logoutService.logout(ChangingDestinationActivity.this);
            }
        });
        keepButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChangingDestinationActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

    }


}
