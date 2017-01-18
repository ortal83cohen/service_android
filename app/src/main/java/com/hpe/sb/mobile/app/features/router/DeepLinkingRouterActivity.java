package com.hpe.sb.mobile.app.features.router;

import com.hpe.sb.mobile.app.common.utils.BrowserUtils;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import java.util.List;


public class DeepLinkingRouterActivity extends Activity {


    private static final String TAG = DeepLinkingRouterActivity.class.getSimpleName();

    @Override
    public void onStart() {
        try {
            super.onStart();
            Uri uriData = getIntent().getData();
            if (uriData == null) {
                startActivity(RouterActivity.createIntent(DeepLinkingRouterActivity.this));
                finish();
                return;
            }
            deepLinkingRoute(uriData);
        } catch (Exception e) {
            Log.e(TAG, "", e);

        }
        finish();
    }

    private void deepLinkingRoute(Uri uriData) {
        final List<String> pathSegments = uriData.getPathSegments();
        String firstPathSegment = pathSegments.get(0);
        if (firstPathSegment.equals("saw") || firstPathSegment.equals("main")) {
            switch (pathSegments.get(1)) {
                case "ess":
                    switch (pathSegments.get(2)) {
                        case "requestTracking":
                        case "offeringPage":
                        case "categoryPage":
                        case "viewResult":
                            startRouterActivity();
                            break;
                        default:
                            sendToWebBrowser(uriData);
                    }
                    break;
                case "Request":
                case "Offering":
                case "Category":
                case "News":
                case "Article":
                  startRouterActivity();
                    break;
                default:
                    sendToWebBrowser(uriData);
            }
        }
        else {
            sendToWebBrowser(uriData);
        }
    }

    private void startRouterActivity() {
        startActivity(RouterActivity.createIntent(DeepLinkingRouterActivity.this).setData( getIntent().getData()));
        finish();
    }

    private void sendToWebBrowser(Uri data) {
        getIntent().setData(null);
        BrowserUtils.sendToWebBrowser(data, this);
    }
}
