package com.hpe.sb.mobile.app.features.login.services;

import android.content.Context;
import android.content.SharedPreferences;

public class ActivationUrlServiceImpl implements ActivationUrlService {

    private static final String ACTIVATION_URL = "activationUrl";

    private static final String SUCCESSFUL_LOGIN_ACTIVATION_URL = "passedActivationUrl";

    private static final String ACTIVATION_URL_PREFERENCES_FILE = "com.hpe.sb.mobile.app.ActivationUrl";


    private final Context context;


    public ActivationUrlServiceImpl(Context context) {
        this.context = context;
    }

    @Override
    public void setActivationUrl(String text) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ACTIVATION_URL_PREFERENCES_FILE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(ACTIVATION_URL, text);

        editor.apply();
    }

    @Override
    public String getActivationUrl() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ACTIVATION_URL_PREFERENCES_FILE,
                Context.MODE_PRIVATE);

        return sharedPreferences.getString(ACTIVATION_URL, null);
    }

    @Override
    public void updateSuccessfulLoginWithActivationUrl(boolean b) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ACTIVATION_URL_PREFERENCES_FILE,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(SUCCESSFUL_LOGIN_ACTIVATION_URL, b);

        editor.apply();
    }

    @Override
    public boolean isSuccessfulLoginWithActivationUrl() {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ACTIVATION_URL_PREFERENCES_FILE,
                Context.MODE_PRIVATE);

        return sharedPreferences.getBoolean(SUCCESSFUL_LOGIN_ACTIVATION_URL, false);
    }

}
