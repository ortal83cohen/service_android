package com.hpe.sb.mobile.app.infra.baseActivities;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.webkit.*;
import android.widget.Toast;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.features.login.services.ConnectionContextService;
import com.hpe.sb.mobile.app.features.login.services.SessionCookieService;
import com.hpe.sb.mobile.app.infra.exception.AuthenticationException;
import com.hpe.sb.mobile.app.infra.exception.AuthorizationException;
import com.hpe.sb.mobile.app.infra.exception.NoConnectionException;
import com.hpe.sb.mobile.app.infra.exception.NotFoundException;
import com.hpe.sb.mobile.app.infra.restclient.HttpLookupUtils;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import javax.inject.Inject;
import java.net.HttpURLConnection;

/**
 * Created by salemo on 24/05/2016.
 */
public abstract class AbstractUnsupportedActivity extends BaseActivity {
    private static final String TAG = AbstractUnsupportedActivity.class.getSimpleName();
    public static final String ESS_SUPPORTED_USER_AGENT_STRING = "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.76 Mobile Safari/537.36";
    private static final String ABOUT_BLANK = "about:blank";

    @Inject
    HttpLookupUtils httpLookupUtils;

    @Inject
    SessionCookieService sessionCookieService;

    @Inject
    ConnectionContextService connectionContextService;

    public static final int RESULT_ERROR = RESULT_FIRST_USER + 1;

    private WebView webView;
    private String mainUrl;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.unsupported_entity_web_view);
        ((ServiceBrokerApplication) getApplicationContext()).getServiceBrokerComponent().inject(this);

        webView = (WebView) findViewById(R.id.appWebView);
        WebSettings webSettings = null;
        if (webView == null) {
            Log.e(TAG, "Failed to Start WebView, cannot find webView by id");
            finishWebView(RESULT_ERROR, null);
        }
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCachePath(getCacheDir().getPath());
        webSettings.setDomStorageEnabled(true);
        // Impersonate user agent of a supported device.
        webSettings.setUserAgentString(ESS_SUPPORTED_USER_AGENT_STRING);
        webView.setWebViewClient(new WebViewClient() {
            boolean isInErrorFlow = false;

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                isInErrorFlow = false;
                super.onPageStarted(view, url, favicon);
                AbstractUnsupportedActivity.this.onPagePostStarted(view);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if(isInErrorFlow){
                    super.onPageFinished(view, url);
                    return;
                }
                if(url.contains("not-found")){
                    Log.e(TAG, "Got not found error");
                    showBlankPage();
                    Throwable throwable = new NotFoundException();
                    sbExceptionsHandler.handleException(AbstractUnsupportedActivity.this, Thread.currentThread(), throwable);
                }else if( url.contains("saw/ess/error")){
                    Log.e(TAG, "Got saw error page");
                    showBlankPage();
                    Throwable throwable = new NoConnectionException();
                    sbExceptionsHandler.handleException(AbstractUnsupportedActivity.this, Thread.currentThread(), throwable);
                }else if(!url.contains(ABOUT_BLANK) && !url.contains("saw/ess/")){//hoping to catch redirect to portal's login
                    Log.e(TAG, "Got not saw url <" + url + ">");
                    showBlankPage();
                    Throwable throwable = new AuthenticationException();
                    sbExceptionsHandler.handleException(AbstractUnsupportedActivity.this, Thread.currentThread(), throwable);
                }
                else {
                    injectCss(view);
                    fixKeyboardOnDropdownsIssue(view);
                }
                super.onPageFinished(view, url);
            }

            /*
            This callback will be called on sdks >= 23 and NOT only if the main page failed to load BUT
            for any element
             */
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                if(!request.isForMainFrame()){
                    return;
                }
                Log.e(TAG, "Got error " + error);
                if (recordError()) {
                    return;
                }
                final int errorCode = error.getErrorCode();
                handleReceivedError(errorCode);
                super.onReceivedError(view, request, error);
            }

            /*
            This callback will be called on sdks < 23 and only if the main page failed to load
             */
            @Override
            @SuppressWarnings("deprecation")
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                if(!failingUrl.toString().equals(mainUrl)){//to be on the safe side lets check again that the error page indeed the main page
                    return;
                }
                if (recordError()) {
                    return;
                }
                Log.e(TAG, "Got error code " + errorCode);
                handleReceivedError(errorCode);
                super.onReceivedError(view, errorCode, description, failingUrl);
            }


            /*
                I didn't see how we get can here, but lets handle it as well.
             */
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                if(!request.isForMainFrame()){
                    return;
                }
                if (recordError()) {
                    return;
                }
                Log.e(TAG, "Got HTTP error " + errorResponse);
                showBlankPage();
                Throwable throwable = null;
                final int errorCode = errorResponse.getStatusCode();
                switch (errorCode) {
                    case HttpURLConnection.HTTP_UNAUTHORIZED: {
                        throwable = new AuthenticationException();
                        break;
                    }
                    case HttpURLConnection.HTTP_NOT_FOUND: {
                        throwable = new NotFoundException();
                        break;
                    }
                    case HttpURLConnection.HTTP_FORBIDDEN: {
                        throwable = new AuthorizationException();
                        break;
                    }
                    default: {
                        throwable = new NoConnectionException();
                    }
                }
                sbExceptionsHandler.handleException(AbstractUnsupportedActivity.this, Thread.currentThread(), throwable);
                super.onReceivedHttpError(view, request, errorResponse);
            }

            // Fix 18798: keyboard covers dropdown
            private void fixKeyboardOnDropdownsIssue(WebView view) {
                try {
                    view.loadUrl("javascript:(function() {" +
                            "$(document).on('touchend', function(){" +
                            "$('[ng-controller=\"dropDownListEditorCtrl\"] .select2-search,[ng-controller=\"dropDownListEditorCtrl\"] .select2-focusser').remove();" +
                            "})" +
                            "})()");
                } catch (Exception e) {
                    Log.e(TAG, "Faied to handle CSS", e);
                }
            }

            // Very ugly hack to hide unwanted components from displaying in webview.
            // If this activity survives more than one version, this code should be moved to UI
            // and controlled by a flag that the webview should pass.
            private void injectCss(WebView view) {
                try {
                    String css = ".ess-header-wrapper { display: none !important; } " +
                            ".ess-main-page-content-wrapper { padding-top: 0px !important;} " +
                            ".ess-widgets-container {display: none !important;} " +
                            ".ess-main-page-content-header { display: none !important;} " +
                            ".ess-main-container { padding-top: 0px !important; } " +
                            ".ess-top-panel-wrapper {display: none !important;} " +
                            getAdditionalCss();
                    String encoded = Base64.encodeToString(css.getBytes(), Base64.NO_WRAP);
                    view.loadUrl("javascript:(function() {" +
                            "var parent = document.getElementsByTagName('head').item(0);" +
                            "var style = document.createElement('style');" +
                            "style.type = 'text/css';" +
                            // Tell the browser to BASE64-decode the string into your script !!!
                            "style.innerHTML = window.atob('" + encoded + "');" +
                            "parent.appendChild(style)" +
                            "})()");
                } catch (Exception e) {
                    Log.e(TAG, "Faied to handle CSS", e);
                }
            }

            private boolean recordError() {
                if(isInErrorFlow){
                    return true;
                }
                isInErrorFlow = true;
                return false;
            }

        });

        Intent intent = getIntent();
        String relativeUrl = getRelativeUrl(intent);
        if (relativeUrl == null) {
            Log.e(TAG, "Failed to Start WebView, no data to show");
            finishWebView(RESULT_CANCELED, null);
            return;
        }

        //add the short host query param to avoid redirection in deep linking filter in gateway in cases of custom URLs
        String shortHostQueryParam = (relativeUrl.contains("?") ? "&" : "?") + "shortHost=" +
                connectionContextService.getConnectionContext().getHostName();
        mainUrl = httpLookupUtils.getBaseUrl() + "/saw/ess/" + relativeUrl + shortHostQueryParam;

        Log.i(TAG, "loading url: " + mainUrl);
        sessionCookieService.initWebViewSession()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Void>() {
                    @Override
                    public void call(Void aVoid) {
                        webView.loadUrl(mainUrl);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.getStackTraceString(throwable);
                        Toast.makeText(AbstractUnsupportedActivity.this,
                                "Failed to initialize web view session", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                });
    }

    private void showBlankPage() {
        try {
            webView.stopLoading();
        } catch (Exception e) {
            Log.w(TAG, "Failed to stop loading ", e);
        }
        try {
            webView.loadUrl(ABOUT_BLANK);
        } catch (Exception e) {
            Log.w(TAG, "Failed load blank page ", e);
        }
    }



    private void handleReceivedError(int errorCode) {
        showBlankPage();
        Throwable throwable = null;
        switch (errorCode) {
            case WebViewClient.ERROR_AUTHENTICATION: {//not gonna happen - we get redirect to login page here
                throwable = new AuthenticationException();
                break;
            }
            case WebViewClient.ERROR_FILE_NOT_FOUND: {
                throwable = new NotFoundException();
                break;
            }
            default: {
                throwable = new NoConnectionException();
            }
        }
        sbExceptionsHandler.handleException(AbstractUnsupportedActivity.this, Thread.currentThread(), throwable);
    }


    protected abstract String getRelativeUrl(Intent intent);

    protected String getAdditionalCss() {
        return "";
    }

    protected void onPagePostStarted(WebView view) {
    }

    @Override
    public void onBackPressed() {
        if (webView.getUrl().equals(ABOUT_BLANK)) {//we are here becaue of error
            finishWebView(RESULT_CANCELED, null);
        }else if (webView.canGoBack()) {
            webView.goBack();
        } else {
            finishWebView(RESULT_CANCELED, null);
        }
    }

    protected void finishWebView(int result, Intent intent) {
        if (intent != null) {
            setResult(result, intent);
        } else {
            setResult(result);
        }
        finish();
    }

}
