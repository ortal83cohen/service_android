package com.hpe.sb.mobile.app.infra.rx;

import android.util.Log;
import com.hpe.sb.mobile.app.infra.baseActivities.BaseActivity;
import com.hpe.sb.mobile.app.infra.baseActivities.SbExceptionsHandler;
import rx.Subscriber;

/**
 * Created by mufler on 15/08/2016.
 */
public abstract class BaseSubscriber<T> extends Subscriber<T>{
    protected static String TAG = "BaseSubscriber";
    private final BaseActivity context;
    protected SbExceptionsHandler sbExceptionsHandler;

    public BaseSubscriber(BaseActivity context, SbExceptionsHandler sbExceptionsHandler) {
        super();
        this.context = context;
        this.sbExceptionsHandler = sbExceptionsHandler;
    }

    @Override
    public void onError(Throwable e) {
        Log.e(TAG, "Default error handling ", e);
        sbExceptionsHandler.handleException(context, Thread.currentThread(), e);
    }
    @Override
    public void onCompleted() {

    }

    @Override
    public void onStart() {
        super.onStart();
        context.dismissSnackbarIfNeeded();
    }

    @Override
    public void onNext(T t) {

    }
}
