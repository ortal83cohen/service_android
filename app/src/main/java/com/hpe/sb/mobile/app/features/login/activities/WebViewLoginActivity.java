package com.hpe.sb.mobile.app.features.login.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.hpe.sb.mobile.app.features.login.services.ActivationUrlService;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.features.login.services.ConnectionContextService;
import com.hpe.sb.mobile.app.features.login.services.SessionCookieService;
import com.hpe.sb.mobile.app.features.login.services.WebAppConnector;
import com.hpe.sb.mobile.app.features.login.utils.UrlParser;
import com.hpe.sb.mobile.app.infra.baseActivities.BaseActivity;

import javax.inject.Inject;

import com.hpe.sb.mobile.app.infra.baseActivities.BaseActivity;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import javax.inject.Inject;

public class WebViewLoginActivity extends BaseActivity {
    // url parameter for indicating mobile application
    private static final String URL_PARAM_FOR_MOBILE_APP = "forMobileApp";

    @Inject
    ConnectionContextService connectionContextService;

    @Inject
    SessionCookieService sessionCookieService;

    @Inject
    ActivationUrlService activationUrlService;

    @Inject
    UrlParser urlParser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view_login);
        ((ServiceBrokerApplication) getApplicationContext()).getServiceBrokerComponent().inject(this);

        setupToolbar();
        toolbar.setTitle(R.string.log_in);
        if (!activationUrlService.isSuccessfulLoginWithActivationUrl()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        }
        final WebView portalWebView = (WebView) findViewById(R.id.portalWebView);
        portalWebView.getSettings().setJavaScriptEnabled(true);
        injectWebAppConnectorToWebView(portalWebView);
        portalWebView.clearCache(true);
        portalWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                injectWebAppConnectorToWebView(view);
                updateConnectionContextWithCookie(url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                updateConnectionContextWithCookie(url);
            }

            private void updateConnectionContextWithCookie(String url) {
                String cookie = CookieManager.getInstance().getCookie(url);
                connectionContextService.updateCookie(cookie);
                validateConnectionContextAndFinishIfValid();
            }
        });

        final String url = getUrl();
        if (url != null) {
            sessionCookieService.clearWebViewCookies()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Action1<Void>() {
                        @Override
                        public void call(Void aVoid) {
                            portalWebView.loadUrl(url);
                        }
                    }, new Action1<Throwable>() {
                        @Override
                        public void call(Throwable throwable) {
                            Log.getStackTraceString(throwable);
                            Toast.makeText(WebViewLoginActivity.this,
                                    R.string.session_failed_error, Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
        } else {
            Toast.makeText(WebViewLoginActivity.this, R.string.missing_mandatory_error, Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
            finish();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (activationUrlService.isSuccessfulLoginWithActivationUrl()) {
            getMenuInflater().inflate(R.menu.menu_web_view_login, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_switch_tenant) {
            activationUrlService.updateSuccessfulLoginWithActivationUrl(false);
            finish();
        }
        if (id == android.R.id.home) {
            activationUrlService.updateSuccessfulLoginWithActivationUrl(false);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private String getUrl() {
        String url = getIntent().getStringExtra(PreLoginActivity.URL_INTENT_EXTRA_NAME);
        if (url != null && !url.isEmpty()) {
            String forMobileAppValue = urlParser.getQueryParam(url, URL_PARAM_FOR_MOBILE_APP);
            if(forMobileAppValue != null && (forMobileAppValue.isEmpty() || forMobileAppValue.equalsIgnoreCase("true"))) {
                return url;
            } else {
                // need to add mobile mark for go to different jsp in server(TenantVersionFilter)
                return urlParser.addQueryParam(url, URL_PARAM_FOR_MOBILE_APP, "true");
            }
        }
        return null;
    }

    private void validateConnectionContextAndFinishIfValid() {
        if (connectionContextService.isConnectionContextValidForLogin()) {
            connectionContextService.storeConnectionContext();
            setResult(RESULT_OK);
            finish();
        }
    }

    private void injectWebAppConnectorToWebView(WebView portalWebView) {
        WebAppConnector webAppConnector = new WebAppConnector(connectionContextService, new Runnable() {
            @Override
            public void run() {
                validateConnectionContextAndFinishIfValid();
            }
        });
        portalWebView.addJavascriptInterface(webAppConnector, "MobileAppConnector");
    }

    public static Intent createIntent(Context context, String url) {
        Intent intent = new Intent(context, WebViewLoginActivity.class);
        intent.putExtra(PreLoginActivity.URL_INTENT_EXTRA_NAME, url);
        return intent;
    }
}
