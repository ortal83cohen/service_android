package com.hpe.sb.mobile.app.common.uiComponents.categories;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.hpe.sb.mobile.app.serverModel.catalog.CatalogGroup;

/**
 * Created by mufler on 14/06/2016.
 */
public abstract class BaseOnCategorySelectListener implements OnCategorySelectListener {
    protected Activity hostActivity;

    public BaseOnCategorySelectListener(Activity hostActivity) {
        this.hostActivity = hostActivity;
    }

    @Override
    public boolean handleCategorySelected(CatalogGroup catalogGroup) {
        Log.i(getClass().getName(), "open CategoryPageActivity for category id: " + catalogGroup.getId());
        String targetUrl = catalogGroup.getTargetUrl();
        if(targetUrl != null) {
            if (!targetUrl.startsWith("http://") && !targetUrl.startsWith("https://")) {
                targetUrl = "http://" + targetUrl;
            }
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl));
            hostActivity.startActivity(browserIntent);
            return true;
        }
        return false;
    }
}
