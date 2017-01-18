package com.hpe.sb.mobile.app.features.googlepushnotification.restClient;

import android.content.Context;
import com.hpe.sb.mobile.app.features.googlepushnotification.model.MGGcmSender;
import com.hpe.sb.mobile.app.features.googlepushnotification.model.MGGooglePushNotificationData;
import com.hpe.sb.mobile.app.features.googlepushnotification.model.MGGooglePushNotificationIdentifier;
import com.hpe.sb.mobile.app.infra.restclient.RestService;
import rx.Observable;

public class GooglePushNotificationRestClientImpl implements GooglePushNotificationRestClient {

    private final RestService restService;

    public GooglePushNotificationRestClientImpl(RestService restService) {
        this.restService = restService;
    }

    private String getPathPrefix() {
        return "GooglePushNotification/";
    }

    @Override
    public Observable<MGGcmSender> getSender(Context context) {
        return restService.createGetRequest(getPathPrefix() + "GCM/sender", MGGcmSender.class, context);
    }

    @Override
    public Observable<Void> addRegistrationId(Context context, String userId, String registrationId, String gcmSenderId) {
        String url = getPathPrefix() + "GCM/" + gcmSenderId + "/" + userId + "/addRegistrationId";
        return restService.createPutRequest(url, registrationId, Void.class, context);
    }

    @Override
    public Observable<MGGooglePushNotificationData> getNotificationData(Context context, MGGooglePushNotificationIdentifier googlePushNotificationIdentifier, String userId, String registrationId) {
        String url = getPathPrefix() + "GCM/notificationData/" +
                googlePushNotificationIdentifier.getPushNotificationType() + "/" +
                googlePushNotificationIdentifier.getPushNotificationId() + "/" +
                googlePushNotificationIdentifier.getEntityType() + "/" +
                googlePushNotificationIdentifier.getEntityId() + "/" +
                userId + "/" + registrationId;
        return restService.createGetRequest(url, MGGooglePushNotificationData.class, context);
    }
}
