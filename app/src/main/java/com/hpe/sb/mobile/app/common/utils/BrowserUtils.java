package com.hpe.sb.mobile.app.common.utils;

import com.hpe.sb.mobile.app.R;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;


public class BrowserUtils {

    private static final String SCHEME = "http";

    private static final String SITE_FOR_EXAMPLE = "www.google.com";
    // We cant ask who can open our site because if the user choose our app to be the only app for this site we wont see any browsers in the options chooser.
    // that way we ask the android who can open some site, in this case, Google.

    public static void sendToWebBrowser(final Uri uri, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW,new Uri.Builder().scheme(SCHEME).authority(SITE_FOR_EXAMPLE).build());
        PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(intent, 0); // ask the android which app can open this intent.
        String packageNameOfAppToHide = context.getPackageName();
        List<Intent> targetIntents = new ArrayList<>();
        for (ResolveInfo currentInfo : activities) {// remove our app from the list
            String packageName = currentInfo.activityInfo.packageName;
            if (!packageNameOfAppToHide.equals(packageName)) {
                Intent targetIntent = new Intent(Intent.ACTION_VIEW);
                targetIntent.setData(uri);
                targetIntent.setPackage(packageName);
                targetIntents.add(targetIntent);
            }
        }
        if (!targetIntents.isEmpty()) {
            if(targetIntents.size() == 1){ //  if only ine app, just open it, else open app chooser and let the user open his favorite browser.
                context.startActivity(targetIntents.get(0));
            }else {
                Intent chooserIntent = Intent.createChooser(targetIntents.remove(0), context.getString(R.string.open_with));
                chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, targetIntents.toArray(new Parcelable[]{}));
                context.startActivity(chooserIntent);
            }
        }
    }
}
