package com.hpe.sb.mobile.app.common.services.assetReader;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Callable;

import rx.Observable;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class AssetReaderService {

    public static final String READING_CSS_FILE_TAG = "Asset Reader Helper";
    private final Context context;

    public AssetReaderService(Context context) {
        this.context = context;
    }

    public Observable<String> getAssetAsString(final String assetURI) {
        return Observable.fromCallable(new Callable<String>() {
            @Override
            public String call() throws Exception {
                BufferedReader cssReader = null;
                try {
                    String css = "";
                    cssReader = new BufferedReader(
                            new InputStreamReader(context.getAssets().open(assetURI)));
                    String line;
                    while ((line = cssReader.readLine()) != null) {
                        css += line + "\n";
                    }
                    return css;
                } catch (IOException e) {
                    Log.e(READING_CSS_FILE_TAG, "unable to read asset file", e);
                    return "";
                } finally {
                    if (cssReader != null) {
                        try {
                            cssReader.close();
                        } catch (IOException e) {
                            Log.e(READING_CSS_FILE_TAG, "unable to close asset file", e);
                        }
                    }
                }
            }
        }).doOnError(new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e(READING_CSS_FILE_TAG, "unable to read asset file", throwable);
            }
        }).subscribeOn(Schedulers.io());
    }

}
