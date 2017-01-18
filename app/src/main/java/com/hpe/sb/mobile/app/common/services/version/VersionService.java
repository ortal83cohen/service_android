package com.hpe.sb.mobile.app.common.services.version;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Created by alterl on 09/08/2016.
 */
public class VersionService {

    private final String TAG = this.getClass().getSimpleName();

    public VersionService() {
    }

    public Integer getAppCurrentVersion(Context context){
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionCode;

        } catch (PackageManager.NameNotFoundException e) {
           throw new RuntimeException("failed to get package info", e);
        }
    }

}
