package com.hpe.sb.mobile.app.features.login.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import com.google.zxing.client.android.Intents;
import com.google.zxing.integration.android.IntentIntegrator;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.common.dataClients.userContext.ApplicationType;
import com.hpe.sb.mobile.app.common.uiComponents.dialogs.GeneralErrorDialogFragment;
import com.hpe.sb.mobile.app.features.login.model.ConnectionContext;
import com.hpe.sb.mobile.app.features.login.services.ActivationUrlService;
import com.hpe.sb.mobile.app.features.login.services.ConnectionContextService;
import com.hpe.sb.mobile.app.features.login.utils.UrlParser;
import com.hpe.sb.mobile.app.infra.baseActivities.BaseActivity;

import java.net.MalformedURLException;
import java.net.URL;

import javax.inject.Inject;

public class PreLoginActivity extends BaseActivity implements GeneralErrorDialogFragment.DialogListener{

    private static final String TAG = PreLoginActivity.class.getSimpleName();

    public static final int WEB_VIEW_LOGIN_REQUEST_CODE = 789;

    public static final int PROGRAMMATIC_LOGIN_REQUEST_CODE = 456;

    public static final String APPLICATION_TYPE_QUERY_PARAM_NAME = "APP";

    public static final String TENANT_ID_QUERY_PARAM_NAME = "TENANTID";

    public static final String URL_INTENT_EXTRA_NAME = "url";

    public static final String TENANT_ID_INTENT_EXTRA_NAME = "tenantId";

    public static final String APP_TYPE_INTENT_EXTRA_NAME = "applicationTypeStr";


    @Inject
    UrlParser urlParser;

    @Inject
    ConnectionContextService connectionContextService;

    @Inject
    ActivationUrlService activationUrlService;

    private String urlString = null;

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_login);
        ((ServiceBrokerApplication) getApplicationContext()).getServiceBrokerComponent()
                .inject(this);

        editText = (EditText) findViewById(R.id.urlInput);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_DONE || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    loadUrl(v);
                    handled = true;
                }
                return handled;
            }
        });
        urlString = activationUrlService.getActivationUrl();
        if (urlString != null) {
            editText.setText(urlString);
        }
        if (activationUrlService.isSuccessfulLoginWithActivationUrl()) {
            loadUrl(editText);
        }
        findViewById(R.id.delete_activation_url).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        activationUrlService.setActivationUrl(editText.getText().toString());
    }

    public void scanQR(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setBeepEnabled(false);
        integrator.setCaptureActivity(CaptureActivityAnyOrientation.class);
        integrator.setOrientationLocked(false);
        integrator.setPrompt(getString(R.string.scan_qr_code_prompt));
        integrator.initiateScan();
    }

    public void loadUrl(View view) {

        urlString = extractUrlString();
        if (urlString == null) {
            return;
        }

        loadUrl(urlString);
    }

    private void loadUrl(String urlString) {
        URL url;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            showErrorAlert();
            return;
        }

        ApplicationType applicationType = extractApplicationTypeFromUrl(url);
        if (applicationType == null) {
            return;
        }
        connectionContextService.updateApplicationType(applicationType);

        switch (applicationType) {
            case SAW: {
                urlString = urlParser.removeQueryParam(urlString, APPLICATION_TYPE_QUERY_PARAM_NAME);
                Intent intent = WebViewLoginActivity.createIntent(this, urlString);
                startActivityForResult(intent, WEB_VIEW_LOGIN_REQUEST_CODE);
                break;
            }
            case DEMO:
            case PROPEL: {
                extractHostAndUpdateConnectionContext(url);

                String tenantId = urlParser.getQueryParam(urlString, TENANT_ID_QUERY_PARAM_NAME);
                if (tenantId == null) {
/*                    Toast.makeText(PreLoginActivity.this, R.string.no_tenant_id_error,
                            Toast.LENGTH_LONG).show();*/
                    showErrorAlert();
                    return;
                }

                Intent intent = new Intent(this, ProgrammaticLoginActivity.class);
                intent.putExtra(URL_INTENT_EXTRA_NAME, urlString);
                intent.putExtra(TENANT_ID_INTENT_EXTRA_NAME, tenantId);
                intent.putExtra(APP_TYPE_INTENT_EXTRA_NAME, applicationType.name());
                startActivityForResult(intent, WEB_VIEW_LOGIN_REQUEST_CODE);
                break;
            }
        }
    }

    private String extractUrlString() {

        String urlString = editText.getText().toString();
        if (urlString.isEmpty()) {
/*            Toast.makeText(PreLoginActivity.this, R.string.no_url_error,
                    Toast.LENGTH_LONG).show();*/
            showErrorAlert();
            return null;
        }
        if (!urlString.toLowerCase().startsWith("http://") && !urlString.toLowerCase()
                .startsWith("https://")) {
            urlString = "https://" + urlString;
        }
        return urlString;
    }

    private void showErrorAlert() {
        GeneralErrorDialogFragment errorDialogFragment = new GeneralErrorDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(GeneralErrorDialogFragment.ERROR_DESC_KEY, R.string.malformed_url_error);
        bundle.putInt(GeneralErrorDialogFragment.ERROR_TITLE_KEY, R.string.malformed_url_error_header);
        bundle.putInt(GeneralErrorDialogFragment.POSITIVE_ACTION_KEY, R.string.malformed_url_error_done);
        errorDialogFragment.setArguments(bundle);
        errorDialogFragment.show(getSupportFragmentManager(), PreLoginActivity.class.getSimpleName());
    }

    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        dialog.dismiss();
    }

    private ApplicationType extractApplicationTypeFromUrl(URL url) {
        String applicationTypeStr = urlParser
                .getQueryParam(url.toString(), APPLICATION_TYPE_QUERY_PARAM_NAME);
        if (applicationTypeStr == null) {
/*            Toast.makeText(PreLoginActivity.this, R.string.no_application_err,
                    Toast.LENGTH_LONG).show();*/
            showErrorAlert();
            return null;
        }

        try {
            return ApplicationType.valueOf(applicationTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            //Toast.makeText(PreLoginActivity.this, getString(R.string.unknown_application_error, applicationTypeStr), Toast.LENGTH_LONG).show();
            showErrorAlert();
            return null;
        }
    }

    private void extractHostAndUpdateConnectionContext(URL url) {
        String hostName = url.getHost();
        String protocol = url.getProtocol() != null ? url.getProtocol()
                : ConnectionContext.DEFAULT_PROTOCOL;
        String port = url.getPort() != -1 ? Integer.toString(url.getPort()) : null;
        connectionContextService.updateHost(protocol, hostName, port);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == WEB_VIEW_LOGIN_REQUEST_CODE
                || requestCode == PROGRAMMATIC_LOGIN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                activationUrlService.updateSuccessfulLoginWithActivationUrl(true);
                setResult(RESULT_OK);
                finish();
            } else {
                if(activationUrlService.isSuccessfulLoginWithActivationUrl()){
                    finish();
                }
                Log.e(TAG, "Got Cancelled result from login");
            }
        }
        if (requestCode == IntentIntegrator.REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                handleQRCodeResult(data);
            }
        }
    }

    private void handleQRCodeResult(Intent data) {
        urlString = data.getStringExtra(Intents.Scan.RESULT);
        if (urlString == null) {
            Log.e(TAG, "QRCode result is null");
        }
        editText.setText(urlString);
        loadUrl(urlString);
    }
}
