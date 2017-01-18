package com.hpe.sb.mobile.app.features.googlepushnotification;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.hpe.sb.mobile.app.common.dataClients.user.UserClient;
import com.hpe.sb.mobile.app.common.dataClients.userContext.ApplicationType;
import com.hpe.sb.mobile.app.common.dataClients.userContext.UserContextService;
import com.hpe.sb.mobile.app.features.googlepushnotification.model.GcmRegistrationPerSender;
import com.hpe.sb.mobile.app.features.googlepushnotification.model.MGGcmMessageData;
import com.hpe.sb.mobile.app.features.googlepushnotification.model.MGGcmSender;
import com.hpe.sb.mobile.app.features.googlepushnotification.model.MGGooglePushNotificationData;
import com.hpe.sb.mobile.app.features.googlepushnotification.model.MGGooglePushNotificationIdentifier;
import com.hpe.sb.mobile.app.features.googlepushnotification.notificationPresenters.GooglePushNotificationPresenter;
import com.hpe.sb.mobile.app.features.googlepushnotification.restClient.GooglePushNotificationRestClient;
import com.hpe.sb.mobile.app.features.googlepushnotification.sharedPreferences.GooglePushNotificationSharedPrefClient;
import com.hpe.sb.mobile.app.features.login.services.ConnectionContextService;
import com.hpe.sb.mobile.app.infra.dataClients.DataBlob;
import com.hpe.sb.mobile.app.infra.dataClients.SingletonDbClient;
import com.hpe.sb.mobile.app.infra.db.MissingMandatoryDBItemException;
import com.hpe.sb.mobile.app.infra.exception.AuthorizationException;
import com.hpe.sb.mobile.app.serverModel.user.User;
import com.hpe.sb.mobile.app.serverModel.user.useritems.ApprovalUserItem;
import com.hpe.sb.mobile.app.serverModel.user.useritems.RequestActiveUserItem;
import com.hpe.sb.mobile.app.serverModel.user.useritems.RequestFeedbackUserItem;
import com.hpe.sb.mobile.app.serverModel.user.useritems.RequestResolveUserItem;
import com.hpe.sb.mobile.app.serverModel.user.useritems.UserItem;
import com.hpe.sb.mobile.app.serverModel.user.useritems.UserItems;

import java.io.IOException;
import java.util.Map;

import rx.Observable;
import rx.exceptions.Exceptions;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;
import rx.functions.Func4;
import rx.schedulers.Schedulers;


public class GooglePushNotificationClientImpl implements GooglePushNotificationClient {

    public static final String TAG = "GooglePushNotifClient";

    private final ConnectionContextService connectionContextService;

    private final UserContextService userContextService;

    private GooglePushNotificationRestClient restClient;

    private GooglePushNotificationSharedPrefClient googlePushNotificationSharedPrefClient;

    private final UserClient userClient;

    private final SingletonDbClient<User> userDbClient;

    private Map<String, GooglePushNotificationPresenter> notificationPresentersMap;

    public GooglePushNotificationClientImpl(GooglePushNotificationRestClient googlePushNotificationRestClient,
            GooglePushNotificationSharedPrefClient googlePushNotificationSharedPrefClient,
            ConnectionContextService connectionContextService,
            UserContextService userContextService,
            UserClient userClient, SingletonDbClient<User> userDbClient,
            Map<String, GooglePushNotificationPresenter> notificationPresentersMap) {
        this.restClient = googlePushNotificationRestClient;
        this.googlePushNotificationSharedPrefClient = googlePushNotificationSharedPrefClient;
        this.connectionContextService = connectionContextService;
        this.userContextService = userContextService;
        this.userClient = userClient;
        this.notificationPresentersMap = notificationPresentersMap;
        this.userDbClient = userDbClient;
    }

    @Override
    public Observable<Long> getLastSuccessfulRegistrationTime(Context context) {
        return googlePushNotificationSharedPrefClient.getLastSuccessfulRegistrationTime();
    }

    @Override
    public Observable<GcmRegistrationPerSender> getGcmRegistrationForCurrentSender(final Context context) {
        return googlePushNotificationSharedPrefClient.getSender().cache().flatMap(new Func1<MGGcmSender, Observable<MGGcmSender>>() {
            @Override
            public Observable<MGGcmSender> call(MGGcmSender mgGcmSender) {
                if (mgGcmSender != null && mgGcmSender.getSenderId() != null) {
                    return Observable.just(mgGcmSender);
                }
                return restClient.getSender(context);
            }
        }).switchMap(new Func1<MGGcmSender, Observable<GcmRegistrationPerSender>>() {
            @Override
            public Observable<GcmRegistrationPerSender> call(MGGcmSender mgGcmSender) {
                return googlePushNotificationSharedPrefClient.getGcmRegistration(mgGcmSender.getSenderId());
            }
        });

    }

    @Override
    public Observable<Void> register(final Context context) {
        ApplicationType applicationType = connectionContextService.getConnectionContext().getApplicationType();
        // TODO: Mor: need to use capabilities model when we have time to change
        if (applicationType == ApplicationType.DEMO || applicationType == ApplicationType.PROPEL) {
            return Observable.just(null);
        }

        final String userId = userContextService.getUserModel().getId();

        // get the GCM sender id from the server
        Observable<MGGcmSender> getSenderFromServer = restClient.getSender(context).cache();

        // store the sender id in db
        Observable<Void> storeSenderInDb = getSenderFromServer
                .flatMap(new Func1<MGGcmSender, Observable<Void>>() {
                    @Override
                    public Observable<Void> call(MGGcmSender gcmSender) {
                        return googlePushNotificationSharedPrefClient.setSender(gcmSender);
                    }
                }).cache();

        // get from db the GCM registration for the sender we got
        Observable<GcmRegistrationPerSender> getGcmRegistrationFromDb = getSenderFromServer
                .flatMap(new Func1<MGGcmSender, Observable<GcmRegistrationPerSender>>() {
                    @Override
                    public Observable<GcmRegistrationPerSender> call(MGGcmSender gcmSender) {
                        return googlePushNotificationSharedPrefClient.getGcmRegistration(gcmSender.getSenderId());
                    }
                }).cache();

        // if the GCM registrations in db doesn't contain a registration id for the sender, perform registration to GCM for that sender
        Observable<GcmRegistrationPerSender> registerIfNoGcmRegistrationForSender = Observable.zip(
                getSenderFromServer, getGcmRegistrationFromDb,
                new Func2<MGGcmSender, GcmRegistrationPerSender, GcmRegistrationPerSender>() {
                    @Override
                    public GcmRegistrationPerSender call(MGGcmSender gcmSender, GcmRegistrationPerSender gcmRegistrationPerSender) {
                        if (gcmRegistrationPerSender != null && gcmRegistrationPerSender.getRegistrationId() != null) {
                            return gcmRegistrationPerSender;
                        } else {
                            try {
                                GcmRegistrationPerSender newGcmRegistrationPerSender = performRegistrationToGcm(gcmSender, context);
                                return newGcmRegistrationPerSender;
                            } catch (Throwable t) {
                                throw Exceptions.propagate(t);
                            }
                        }
                    }
                }).cache();

        // if we have just now performed registration to GCM for the sender, store the new registration id in db
        Observable<Void> storeRegistrationIdInDb = Observable.switchOnNext(Observable.zip(
                getSenderFromServer, getGcmRegistrationFromDb, registerIfNoGcmRegistrationForSender,
                new Func3<MGGcmSender, GcmRegistrationPerSender, GcmRegistrationPerSender, Observable<Void>>() {

                    @Override
                    public Observable<Void> call(MGGcmSender gcmSender, GcmRegistrationPerSender oldGcmRegistrationPerSender,
                            GcmRegistrationPerSender newGcmRegistrationPerSender) {
                        if (oldGcmRegistrationPerSender != null && oldGcmRegistrationPerSender.getRegistrationId() != null) {
                            return Observable.just(null);
                        } else {
                            return googlePushNotificationSharedPrefClient.setGcmRegistration(gcmSender.getSenderId(), newGcmRegistrationPerSender);
                        }
                    }
                })).cache();

        // if the registration id was never sent to server (because we have just now registered or because a problem occurred last time), send the registration id to the server
        Observable<Void> sendRegistrationIdToServer = Observable.switchOnNext(Observable.zip(
                getSenderFromServer, registerIfNoGcmRegistrationForSender,
                new Func2<MGGcmSender, GcmRegistrationPerSender, Observable<Void>>() {
                    @Override
                    public Observable<Void> call(MGGcmSender gcmSender, GcmRegistrationPerSender gcmRegistrationPerSender) {
                        if (gcmRegistrationPerSender.isSentToServer()) {
                            return Observable.just(null);
                        }
                        String registrationId = gcmRegistrationPerSender.getRegistrationId();
                        String senderId = gcmSender.getSenderId();
                        return restClient.addRegistrationId(context, userId, registrationId, senderId);
                    }
                })).cache();

        // if the registration id has been set to the server successfully, store in db that the registration was sent
        Observable<Void> storeIsSentToServerInDb = Observable.switchOnNext(Observable.zip(
                getSenderFromServer, registerIfNoGcmRegistrationForSender, sendRegistrationIdToServer, storeRegistrationIdInDb,
                new Func4<MGGcmSender, GcmRegistrationPerSender, Void, Void, Observable<Void>>() {
                    @Override
                    public Observable<Void> call(MGGcmSender gcmSender, GcmRegistrationPerSender gcmRegistrationPerSender, Void void1, Void void2) {
                        if (gcmRegistrationPerSender.isSentToServer()) {
                            return Observable.just(null);
                        }
                        gcmRegistrationPerSender.setIsSentToServer(true);
                        return googlePushNotificationSharedPrefClient.setGcmRegistration(gcmSender.getSenderId(), gcmRegistrationPerSender);
                    }
                })).cache();

        // return an observable that has dependencies on all actions in the registration process
        return Observable.zip(storeSenderInDb, storeIsSentToServerInDb, new Func2<Void, Void, Void>() {
            @Override
            public Void call(Void void1, Void void2) {
                return null;
            }
        }).flatMap(new Func1<Void, Observable<Void>>() {
            @Override
            public Observable<Void> call(Void aVoid) {
                return googlePushNotificationSharedPrefClient.setLastSuccessfulRegistrationTime();
            }
        }).subscribeOn(Schedulers.io());
    }

    private GcmRegistrationPerSender performRegistrationToGcm(MGGcmSender gcmSender, Context context) throws IOException {
        String senderId = gcmSender.getSenderId();
        InstanceID instanceID = InstanceID.getInstance(context);
        String registrationId = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
        GcmRegistrationPerSender gcmRegistrationPerSender = new GcmRegistrationPerSender(registrationId, false);
        return gcmRegistrationPerSender;
    }

    @Override
    public Observable<Void> displayNotification(final Context context, final String fromSender,
            final MGGcmMessageData gcmMessageData) {

        final MGGooglePushNotificationIdentifier googlePushNotificationIdentifier = gcmMessageData.getGooglePushNotificationIdentifier();
        final GooglePushNotificationPresenter googlePushNotificationPresenter = notificationPresentersMap
                .get(googlePushNotificationIdentifier.getPushNotificationType());
        if (googlePushNotificationPresenter == null) {
            Log.e(TAG, "No presenter found for notification type " + googlePushNotificationIdentifier.getPushNotificationType());
            return Observable.just(null);
        }

        Observable<User> userObservable;
        if (userContextService.getUserModel() != null) {
            userObservable = Observable.just(userContextService.getUserModel());
        } else {
            Log.e(TAG, "Trying to display notification after the user clear app data, there is no user data in memory");
            userObservable = userDbClient.getItem().map(new Func1<DataBlob<User>, User>() {
                @Override
                public User call(DataBlob<User> userDataBlob) {
                    if (userDataBlob != null) {
                        return userDataBlob.getData();
                    }
                    Log.e(TAG, "Trying to get User Model from Db after the user clear app data, there is no user data in the database");
                    throw Exceptions.propagate(new MissingMandatoryDBItemException());
                }
            });
        }

        Observable<MGGooglePushNotificationData> getNotificationDataFromServer = Observable
                .switchOnNext(Observable.zip(userObservable, googlePushNotificationSharedPrefClient.getGcmRegistration(fromSender),
                        new Func2<User, GcmRegistrationPerSender, Observable<MGGooglePushNotificationData>>() {
                            @Override
                            public Observable<MGGooglePushNotificationData> call(final User user, final GcmRegistrationPerSender gcmRegistrationPerSender) {
                                String registrationId = null;
                                if (gcmRegistrationPerSender != null) {
                                    registrationId = gcmRegistrationPerSender.getRegistrationId();
                                }
                                return restClient
                                        .getNotificationData(context, googlePushNotificationIdentifier, user.getId(), registrationId);
                            }
                        }
                ));

        Observable<UserItem> getMGItemFromServer = userClient.getUserItems(context, true)
                .map(new Func1<UserItems, UserItem>() {
                    @Override
                    public UserItem call(UserItems mgUserItems) {
                        return getMGItemRelevantForNotification(mgUserItems, googlePushNotificationIdentifier);
                    }
                }).cache();

        return Observable.zip(getNotificationDataFromServer, getMGItemFromServer, new Func2<MGGooglePushNotificationData, UserItem, Void>() {
            @Override
            public Void call(MGGooglePushNotificationData googlePushNotificationData, UserItem userItem) {
                googlePushNotificationPresenter.displayNotification(context, gcmMessageData, googlePushNotificationData, userItem);
                return null;
            }
        }).onErrorResumeNext(new Func1<Throwable, Observable<? extends Void>>() {
            @Override
            public Observable<? extends Void> call(Throwable throwable) {
                Log.e(TAG, "An error has occurred when trying to display the full notification", throwable);
                if (throwable instanceof AuthorizationException) {
                    return Observable.error(throwable);
                } else {
                    googlePushNotificationPresenter.displayOfflineNotification(context, gcmMessageData);
                    return null;
                }
            }
        }).subscribeOn(Schedulers.io());
    }

    private UserItem getMGItemRelevantForNotification(UserItems userItems, MGGooglePushNotificationIdentifier googlePushNotificationIdentifier) {
        String notificationEntityId = googlePushNotificationIdentifier.getEntityId();
        for (ApprovalUserItem approval : userItems.getApprovals()) {
            if (notificationEntityId.equals(approval.getId())) {
                return approval;
            }
        }
        for (RequestFeedbackUserItem feedbackItem : userItems.getRequestFeedbackItems()) {
            if (notificationEntityId.equals(feedbackItem.getId())) {
                return feedbackItem;
            }
        }
        for (RequestResolveUserItem requestResolveItem : userItems.getRequestResolveItems()) {
            if (notificationEntityId.equals(requestResolveItem.getId())) {
                return requestResolveItem;
            }
        }
        for (RequestActiveUserItem request : userItems.getActiveRequests()) {
            if (notificationEntityId.equals(request.getId())) {
                return request;
            }
        }
        return null;
    }

    @Override
    public Observable<Void> clearAllRegistration() {
        return googlePushNotificationSharedPrefClient.clearAllRegistrations().subscribeOn(Schedulers.computation());
    }

    @Override
    public Observable<Void> initializeRegistrationsAfterLogin() {
        return googlePushNotificationSharedPrefClient.resetIsSentToServerForAllRegistrations().subscribeOn(Schedulers.computation());
    }

}
