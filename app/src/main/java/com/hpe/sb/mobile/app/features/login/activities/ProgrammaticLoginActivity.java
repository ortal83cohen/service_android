package com.hpe.sb.mobile.app.features.login.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.hpe.sb.mobile.app.infra.baseActivities.BaseHttpActivity;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.common.dataClients.userContext.ApplicationType;
import com.hpe.sb.mobile.app.features.login.restClient.AuthenticationClient;
import com.hpe.sb.mobile.app.features.login.services.ConnectionContextService;
import com.hpe.sb.mobile.app.features.login.utils.UrlParser;
import com.hpe.sb.mobile.app.serverModel.user.Credentials;

import javax.inject.Inject;
import java.util.Map;

public class ProgrammaticLoginActivity extends BaseHttpActivity {

    @Inject
    public AuthenticationClient authenticationClient;

    @Inject
    ConnectionContextService connectionContextService;

    @Inject
    UrlParser urlParser;

    private String urlString;
    private String tenantId;
    private String applicationTypeStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_programmatic_login);
        ((ServiceBrokerApplication) getApplicationContext()).getServiceBrokerComponent().inject(this);

        Intent intent = getIntent();
        urlString = intent.getStringExtra(PreLoginActivity.URL_INTENT_EXTRA_NAME);
        tenantId = intent.getStringExtra(PreLoginActivity.TENANT_ID_INTENT_EXTRA_NAME);
        applicationTypeStr = intent.getStringExtra(PreLoginActivity.APP_TYPE_INTENT_EXTRA_NAME);
        if (urlString == null || urlString.isEmpty()
                || tenantId == null || tenantId.isEmpty()
                || applicationTypeStr == null || applicationTypeStr.isEmpty()) {
            Toast.makeText(ProgrammaticLoginActivity.this, R.string.missing_mandatory_values_error, Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    public void submitCredentials(View view) {
        EditText usernameValueEditText = (EditText) findViewById(R.id.usernameValueEditText);
        String username = usernameValueEditText.getText().toString();

        EditText passwordValueEditText = (EditText) findViewById(R.id.passwordValueEditText);
        String password = passwordValueEditText.getText().toString();

        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(ProgrammaticLoginActivity.this, R.string.username_or_password_empty_err, Toast.LENGTH_SHORT).show();
            return;
        }

        Credentials credentials = new Credentials(username, password, tenantId);

        String[] splitUrlString = urlString.split("\\?");
        ApplicationType applicationType = ApplicationType.valueOf(this.applicationTypeStr);
        String authenticationUrlString = splitUrlString[0]
                + (!splitUrlString[0].endsWith("/") ? "/" : "")
                + (applicationType == ApplicationType.DEMO ? "mobile-webapp/" : "")
                + "rest/" + tenantId
                + "/authentication/authenticate?"
                + splitUrlString[1];


        authenticationClient.authenticateUserWithPassword(authenticationUrlString, applicationType, credentials, this,
                new Response.Listener<Map<String, String>>() {
            @Override
            public void onResponse(Map<String, String> headersAfterAuthentication) {
                connectionContextService.updateHeaders(headersAfterAuthentication);
                validateConnectionContextAndFinishIfValid();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                int statusCode = networkResponse != null ? networkResponse.statusCode :  -1;
                String msg = "Failed to authenticate user with password, error: " + error.getMessage() + " status code " + statusCode;
                Log.e(getClass().getName(), msg);
            }
        });
    }

    private void validateConnectionContextAndFinishIfValid() {
        if (connectionContextService.isConnectionContextValid()) {
            connectionContextService.storeConnectionContext();
            setResult(RESULT_OK);
            finish();
        }
    }



}
