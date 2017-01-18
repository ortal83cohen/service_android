package com.hpe.sb.mobile.app.features.request;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Toast;

import com.hpe.sb.mobile.app.infra.baseActivities.AbstractUnsupportedActivity;
import com.hpe.sb.mobile.app.features.request.dialog.UnsavedChangesDialog;
import com.hpe.sb.mobile.app.features.request.helper.NewRequestHeadersHelper;

public class UnsupportedOfferingActivity extends AbstractUnsupportedActivity implements FinishWithDirtyWarning {
    private static final String TAG = UnsupportedOfferingActivity.class.getSimpleName();

    public static final String OFFERING_ID = "offeringId";
    public static final String REQUEST_DESCRIPTION = "requestDescription";
    public static final String CREATED_REQUEST_ID = "requestId";
    public static final String RELATIVE_PATH = "offeringPage/";

    private String requestDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NewRequestHeadersHelper.initHeaders(this);
        Intent intent = getIntent();
        requestDescription = "";
        if (intent != null && intent.getStringExtra(REQUEST_DESCRIPTION) != null) {
            requestDescription = intent.getStringExtra(REQUEST_DESCRIPTION);
        }
    }

    @Override
    protected String getRelativeUrl(Intent intent) {
        String offeringId = intent.getStringExtra(OFFERING_ID);
        if (offeringId == null || offeringId.isEmpty()) {
            Toast.makeText(UnsupportedOfferingActivity.this, "Error loading offering, offering id was not received.", Toast.LENGTH_SHORT).show();
            return null;
        }
        return RELATIVE_PATH + offeringId;
    }

    @Override
    public void finishWithDirtyWarning() {
        UnsavedChangesDialog unsavedChangesDialog = new UnsavedChangesDialog();
        unsavedChangesDialog.show(getFragmentManager(), NewRequestActivity.class.getName());
    }

    protected String getAdditionalCss() {
        return ".ess-offering-toolbar .ess-service-offering-add-to-cart-button { display: none !important;} " +
                ".ess-request-attachment_section { display: none !important;} " +
                ".ess-offering-page-comments { display: none !important;";
    }

    protected void onPagePostStarted(WebView view){
        injectWebAppConnectorToWebView(view);
    }

    private void injectWebAppConnectorToWebView(WebView portalWebView) {
        final NewRequestWebAppConnector webAppConnector = new NewRequestWebAppConnector(
                new RequestResultHandler(), requestDescription);
        portalWebView.addJavascriptInterface(webAppConnector, "AndroidConnectorNewRequest");
    }

    public class RequestResultHandler{
        void onSuccess(String requestId){
            returnRequestId(requestId);
        }

        void onError(String error){
            Log.e(TAG, "Failed to submit request: " + error);
            finishWebView(RESULT_ERROR, null);
        }
    }

    private void returnRequestId(String requestId) {
        Bundle conData = new Bundle();
        conData.putString(CREATED_REQUEST_ID, requestId);
        Intent intent = new Intent();
        intent.putExtras(conData);
        finishWebView(RESULT_OK, intent);
    }

}

