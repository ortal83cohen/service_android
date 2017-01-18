package com.hpe.sb.mobile.app.infra.image;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.infra.restclient.HttpHeadersUtil;
import com.hpe.sb.mobile.app.infra.restclient.HttpLookupUtils;
import com.squareup.picasso.*;
import rx.Observable;
import rx.Subscriber;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mufler on 05/04/2016.
 */
public class ImageServiceImpl implements ImageService {
    private static final String AND = "&";
    private static Pattern RELATIVE_JS_PATTERN = Pattern.compile(".*js/modules/.*");
    private static final String PARAM = "/?";
    private static final String WIDTH = "width=";
    private static final String HEIGHT = "height=";
    private final HttpLookupUtils httpLookupUtils;

    private static final int FAILED_TO_LOAD_RES_ID = R.drawable.categoriesnopic;

    private Picasso picasso;

    public ImageServiceImpl(Context context, HttpLookupUtils httpLookupUtils, final HttpHeadersUtil httpHeadersUtil) {
        this.httpLookupUtils = httpLookupUtils;
        Picasso.Builder builder = new Picasso.Builder(context);
        this.picasso = builder.downloader(new UrlConnectionDownloader(context) {
            @Override
            protected HttpURLConnection openConnection(Uri path) throws IOException {
                final HttpURLConnection httpURLConnection = super.openConnection(path);
                final Map<String, String> httpHeaders = httpHeadersUtil.getHttpHeaders();
                for (Map.Entry<String, String> headerToValuePair : httpHeaders.entrySet()) {
                    httpURLConnection.setRequestProperty(headerToValuePair.getKey(), headerToValuePair.getValue());
                }
                return httpURLConnection;
            }
        }).listener(new Picasso.Listener() {
            @Override
            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                Log.e(getClass().getName(), "Failed to load image from URL " + uri, exception);
            }
        }).build();
        Picasso.setSingletonInstance(this.picasso);
    }

    @Override
    public void loadImage(String imageUri, ImageView view, @Nullable Integer width, @Nullable Integer height){
        String imageUrl = getUrl(imageUri, width, height);
        if(imageUrl == null){
            picasso.load(FAILED_TO_LOAD_RES_ID).into(view);
            return;
        }
        picasso.load(imageUrl).error(FAILED_TO_LOAD_RES_ID).into(view);
    }

    @Override
    public void loadImageWithTransformations(String imageUri, ImageView view, @Nullable Integer width, @Nullable Integer height, Transformation... transformations) {
        String imageUrl = getUrl(imageUri, width, height);
        if (imageUrl == null) {
            picasso.load(FAILED_TO_LOAD_RES_ID).into(view);
            return;
        }
        picasso.load(imageUrl).error(FAILED_TO_LOAD_RES_ID).transform(Arrays.asList(transformations)).into(view);
    }

    @Override
    public void loadImage(String imageUri, Target target, @Nullable Integer width, @Nullable Integer height){
        String imageUrl = getUrl(imageUri, width, height);
        if(imageUrl == null){
            picasso.load(FAILED_TO_LOAD_RES_ID).into(target);
            return;
        }
        picasso.load(imageUrl).error(FAILED_TO_LOAD_RES_ID).into(target);
    }

    @Override
    public void loadImageWithPlaceHolder(String imageUri, ImageView view,
                                         @Nullable Integer width, @Nullable Integer height, int placeHolderImageId){
        String imageUrl = getUrl(imageUri, width, height);
        if(imageUrl == null){
            picasso.load(FAILED_TO_LOAD_RES_ID).into(view);
            return;
        }
        picasso.load(imageUrl).error(FAILED_TO_LOAD_RES_ID).placeholder(placeHolderImageId).into(view);
    }

    @Override
    public Observable<Void> prefetchImage(final String imageUri, final Picasso.Priority priority, @Nullable final Integer width, @Nullable final Integer height) {
        return Observable.create(new Observable.OnSubscribe<Void>() {
            @Override
            public void call(final Subscriber<? super Void> subscriber) {
                String imageUrl = getUrl(imageUri, width, height);
                if (imageUrl == null) {
                    subscriber.onError(new IllegalArgumentException("Url invalid"));
                    return;
                }

                picasso.load(imageUrl).priority(priority).fetch(new Callback() {
                    @Override
                    public void onSuccess() {
                        subscriber.onNext(null);
                        subscriber.onCompleted();
                    }

                    @Override
                    public void onError() {
                        subscriber.onError(new RuntimeException("Failed pre-fetching image " + imageUri));
                    }
                });
            }
        });
    }

    private String getUrl(String imageUri, Integer width, Integer height) {
        String imageUrl;
        final Matcher matcher = RELATIVE_JS_PATTERN.matcher(imageUri);
        if (matcher.matches()){//ngnix (/js/modules/)
            imageUrl = httpLookupUtils.getBaseUrl() + "/" + imageUri;
        } else {//REST
            imageUrl = httpLookupUtils.getBaseRestUrl(getClientPrefix());
            StringBuilder sb = new StringBuilder().append(imageUrl).append("/");
            sb.append(imageUri);
            if (width != null) {
                sb.append(PARAM);
                sb.append(WIDTH).append(width);
            }
            if(height != null){
                if(width == null){
                    Log.e(getClass().getName(), "Width not set");
                    return null;
                }
                sb.append(AND).append(HEIGHT).append(height);
            }
            imageUrl = sb.toString();
        }

        return imageUrl;
    }

    public String getClientPrefix() {
        return "file-service/image";
    }


    //for testing
    @SuppressWarnings("unused")
    ImageServiceImpl(HttpLookupUtils httpLookupUtils, Picasso picasso){
        this.picasso = picasso;
        this.httpLookupUtils = httpLookupUtils;
    }
}
