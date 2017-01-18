package com.hpe.sb.mobile.app.infra.baseActivities;

import android.content.Intent;
import android.util.Log;
import com.hpe.sb.mobile.app.common.uiComponents.dialogs.AuthErrorDialogFragment;
import com.hpe.sb.mobile.app.common.uiComponents.dialogs.UnexpectedErrorDialogFragment;
import com.hpe.sb.mobile.app.features.error.PageNotFoundActivity;
import com.hpe.sb.mobile.app.features.error.PageNotPermittedActivity;
import com.hpe.sb.mobile.app.infra.exception.*;
import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subjects.PublishSubject;

import java.util.concurrent.TimeUnit;

/**
 * Created by mufler on 16/08/2016.
 */
public class SbExceptionsHandler {
    private static final String TAG = SbExceptionsHandler.class.getSimpleName();
    private PublishSubject<ExceptionData> errorSubject;

    public SbExceptionsHandler() {
        errorSubject = PublishSubject.create();
    }

    public void init() {
        final Observable<ExceptionData> errorObservable = errorSubject.asObservable();
        final Observable<ExceptionData> connectionErrorObservable = errorObservable.filter(new Func1<ExceptionData, Boolean>() {
            @Override
            public Boolean call(ExceptionData exceptionData) {
                return isConnectionThrowable(exceptionData);
            }
        });
        final Observable<ExceptionData> throttledErrorObservable = errorObservable.filter(new Func1<ExceptionData, Boolean>() {
            @Override
            public Boolean call(ExceptionData exceptionData) {
                return !isConnectionThrowable(exceptionData);
            }
        }).throttleFirst(3, TimeUnit.SECONDS, Schedulers.computation());

        final Observable<ExceptionData> mergedErrorObservable = Observable.merge(connectionErrorObservable, throttledErrorObservable);
        mergedErrorObservable
                .subscribe(new Subscriber<ExceptionData>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(ExceptionData exceptionData) {
                        Throwable e = exceptionData.getThrowable();
                        BaseActivity context = exceptionData.getContext();

                        try {
                            if (e instanceof RequestTimeoutException || e instanceof NoConnectionException) {
                                context.showNoConnectionSnackbar();
                            } else if (e instanceof AuthenticationException) {
                                AuthErrorDialogFragment authErrorDialogFragment = new AuthErrorDialogFragment();
                                authErrorDialogFragment.setCancelable(false);
                                authErrorDialogFragment.show(context.getSupportFragmentManager(), "Auth exception");
                            } else if (e instanceof NotFoundException) {
                                Intent intent = new Intent(context, PageNotFoundActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION );
                                context.startActivity(intent);
                                handleBack(context);
                            } else if (e instanceof AuthorizationException) {
                                Intent intent = new Intent(context, PageNotPermittedActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION );
                                context.startActivity(intent);
                                handleBack(context);
                            } else {
                                final UnexpectedErrorDialogFragment errorDialogFragment = new UnexpectedErrorDialogFragment();
                                errorDialogFragment.setCancelable(false);
                                errorDialogFragment.show(context.getSupportFragmentManager(), "Unexpected exception");
                            }
                        }catch(Exception e1){
                            Log.w(TAG, "Failed to handle exceptions", e1);
                        }
                    }
                });
    }

    private void handleBack(BaseActivity context) {
        if(context.getIntent() != null ){//in order to go back to the
            final Intent initialIntent = context.getIntent();
            if(initialIntent.getBooleanExtra(BaseActivity.FLAG_REMOVE_FROM_BACK, false)){
                context.finish();
            }
        }
    }

    public void handleException(final BaseActivity context, Thread thread, Throwable e) {
        e.printStackTrace(); // not all Android versions will print the stack trace automatically
        Log.e(getClass().getName(), String.format("Caught unhandled exception, error message: %s", e.getMessage()));
        errorSubject.onNext(new ExceptionData(e, thread, context));
    }


    private Boolean isConnectionThrowable(ExceptionData exceptionData) {
        final Throwable throwable = exceptionData.getThrowable();
        return throwable instanceof RequestTimeoutException || throwable instanceof NoConnectionException;
    }

    public class ExceptionData {

        private Throwable throwable;
        private Thread thread;
        private BaseActivity context;

        public ExceptionData(Throwable e, Thread thread, BaseActivity context) {
            this.throwable = e;
            this.thread = thread;
            this.context = context;
        }

        public Throwable getThrowable() {
            return throwable;
        }

        public void setThrowable(Throwable e) {
            this.throwable = e;
        }

        public Thread getThread() {
            return thread;
        }

        public void setThread(Thread thread) {
            this.thread = thread;
        }

        public BaseActivity getContext() {
            return context;
        }

        public void setContext(BaseActivity context) {
            this.context = context;
        }
    }


}

