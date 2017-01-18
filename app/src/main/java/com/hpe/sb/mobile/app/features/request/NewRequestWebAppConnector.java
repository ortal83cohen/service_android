package com.hpe.sb.mobile.app.features.request;

import android.webkit.JavascriptInterface;

/**
 * Created by mufler on 10/05/2016.
 *
 */
public class NewRequestWebAppConnector {
    private static final String SUCCESS = "SUCCESS";
    private UnsupportedOfferingActivity.RequestResultHandler requestResultHandler;
    private String requestDescription;

    public NewRequestWebAppConnector(UnsupportedOfferingActivity.RequestResultHandler requestResultHandler, String requestDescription) {
        this.requestResultHandler = requestResultHandler;
        this.requestDescription = requestDescription;
    }

    @JavascriptInterface
    public void sendRequestCreationResult(String status, String newRequestId, Object additionalData) {
        if(SUCCESS.equals(status)){
            requestResultHandler.onSuccess(newRequestId);
        }else{
            requestResultHandler.onError(additionalData != null? additionalData.toString(): "");
        }
    }

    @JavascriptInterface
    public String getRequestDescriptionFromMobile() {
        return requestDescription;
    }
}
