package com.hpe.sb.mobile.app.features.login.services;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.features.googlepushnotification.GooglePushNotificationClient;
import com.hpe.sb.mobile.app.features.googlepushnotification.model.GcmRegistrationPerSender;
import com.hpe.sb.mobile.app.features.login.restClient.AuthenticationClient;
import com.hpe.sb.mobile.app.features.router.RouterActivity;
import com.hpe.sb.mobile.app.infra.dataClients.GeneralDbClient;
import com.hpe.sb.mobile.app.serverModel.user.LogoutData;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func3;
import rx.schedulers.Schedulers;

public class LogoutServiceImpl implements LogoutService {

    public AuthenticationClient authenticationClient;

    public GooglePushNotificationClient googlePushNotificationClient;

    public GeneralDbClient generalDbClient;

    public ConnectionContextService connectionContextService;

    private SessionCookieService sessionCookieService;

    public LogoutServiceImpl(AuthenticationClient authenticationClient,
                             GooglePushNotificationClient googlePushNotificationClient, GeneralDbClient generalDbClient,
                             ConnectionContextService connectionContextService, SessionCookieService sessionCookieService) {
        this.authenticationClient = authenticationClient;
        this.googlePushNotificationClient = googlePushNotificationClient;
        this.generalDbClient = generalDbClient;
        this.connectionContextService = connectionContextService;
        this.sessionCookieService = sessionCookieService;
    }

    @Override
    public void logout(final Activity currentActivity) {

        googlePushNotificationClient.getGcmRegistrationForCurrentSender(currentActivity).subscribe(new Action1<GcmRegistrationPerSender>() {
            @Override
            public void call(GcmRegistrationPerSender gcmRegistrationPerSender) {
                Observable<Integer> deleteDbObservable = generalDbClient.deleteUserData().subscribeOn(Schedulers.io());

                Observable<Void> authenticationObservable = authenticationClient.logout(currentActivity, new LogoutData(connectionContextService.getConnectionContext().getLongToken()
                        ,gcmRegistrationPerSender != null ? gcmRegistrationPerSender.getRegistrationId() : null)).subscribeOn(Schedulers.io());

                Observable<Void> clearWebViewCookiesObservable = sessionCookieService.clearWebViewCookies();

                Observable.zip(deleteDbObservable, authenticationObservable, clearWebViewCookiesObservable, new Func3<Integer, Void, Void, Object>() {
                    @Override
                    public Object call(Integer integer, Void aVoid, Void aVoid2) {
                        return null;
                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {
                        doLocalLogout(currentActivity);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(getClass().getName(), "error logout from the server", e);
                        Toast.makeText(currentActivity, R.string.unable_to_log_out, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(Object o) {

                    }
                });
            }
        });
    }

    private void doLocalLogout(Activity currentActivity) {
        NotificationManager notificationManager = (NotificationManager) currentActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        connectionContextService.clearHeadersAndCookies();
        Intent intent = new Intent(currentActivity, RouterActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setData(currentActivity.getIntent().getData());
        currentActivity.startActivity(intent);
        currentActivity.finish();
    }

    ;

    @Override
    public void localLogout(Activity currentActivity) {
        doLocalLogout(currentActivity);
    }
}


