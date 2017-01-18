package com.hpe.sb.mobile.app.infra.baseActivities;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.common.utils.AppBar;

import javax.inject.Inject;

/**
 * Created by malikdav on 26/04/2016.
 */
public class BaseActivity extends AppCompatActivity {
    public static final String FLAG_REMOVE_FROM_BACK = "FLAG_REMOVE_FROM_BACK";
    protected Toolbar toolbar;
    protected Snackbar snackbar;
    @Inject
    protected SbExceptionsHandler sbExceptionsHandler;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((ServiceBrokerApplication) getApplicationContext()).getServiceBrokerComponent()
                .inject(this);
        if(getResources().getBoolean(R.bool.is_release_mode)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
        }
    }

    public void showNoConnectionSnackbar(){
        snackbar = Snackbar.make(findViewById(android.R.id.content), R.string.no_internet_connection_no_refresh, Snackbar.LENGTH_INDEFINITE).
                setAction(R.string.no_internet_connection_dismiss, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
        snackbar.show();
    }

    protected void setupToolbar() {
        toolbar = (AppBar) findViewById(R.id.app_bar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public SbExceptionsHandler getSbExceptionsHandler() {
        return sbExceptionsHandler;
    }

    /*
    need because if I cant call for it in the Manin activity where snackbar is managed by coordinator layout
     */
    public void dismissSnackbarIfNeeded(){
        if(snackbar != null && snackbar.isShownOrQueued()){
            snackbar.dismiss();
        }
    }

    public boolean isNetworkConnectionAvailable(){
        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();
        return netInfo != null;
    }

}
