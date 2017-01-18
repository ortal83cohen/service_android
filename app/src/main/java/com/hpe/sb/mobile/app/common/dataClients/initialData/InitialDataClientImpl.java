package com.hpe.sb.mobile.app.common.dataClients.initialData;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hpe.sb.mobile.app.common.dataClients.userContext.UserContextService;
import com.hpe.sb.mobile.app.common.dataClients.displaylabels.DisplayLabelService;
import com.hpe.sb.mobile.app.common.services.version.VersionService;
import com.hpe.sb.mobile.app.infra.dataClients.ListDbClient;
import com.hpe.sb.mobile.app.infra.exception.AppUpgradeRequiredException;
import com.hpe.sb.mobile.app.infra.exception.TenantUnsupportedOnMobileException;
import com.hpe.sb.mobile.app.features.googlepushnotification.gcm.GcmRegistrationIntentService;
import com.hpe.sb.mobile.app.features.googlepushnotification.scheduler.GcmSchedulerService;
import com.hpe.sb.mobile.app.infra.image.ImageService;
import com.hpe.sb.mobile.app.features.login.services.ConnectionContextService;
import com.hpe.sb.mobile.app.serverModel.InitialDataQuery;
import com.hpe.sb.mobile.app.serverModel.InitialData;
import com.hpe.sb.mobile.app.serverModel.catalog.CatalogGroup;
import com.hpe.sb.mobile.app.serverModel.catalog.CatalogGroupDiffResult;
import com.hpe.sb.mobile.app.serverModel.diff.DiffQuery;
import com.hpe.sb.mobile.app.infra.dataClients.DataBlob;
import com.hpe.sb.mobile.app.infra.restclient.RestService;
import com.hpe.sb.mobile.app.common.dataClients.tenantSettings.TenantSettingsService;
import com.hpe.sb.mobile.app.common.dataClients.themeSettings.ThemeSettingsService;
import com.hpe.sb.mobile.app.common.utils.ClientDataFlowUtils;
import com.hpe.sb.mobile.app.common.utils.DiffQueryUtils;
import com.hpe.sb.mobile.app.common.utils.time.TimeAgeUtils;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func4;
import rx.functions.Func6;
import rx.schedulers.Schedulers;

public class InitialDataClientImpl implements InitialDataClient {

    public static final int INITIAL_DATA_MAX_AGE = 3;


    private RestService restService;
    private ListDbClient<CatalogGroup> catalogDbClient;
    private UserContextService userContextService;
    private DisplayLabelService displayLabelService;
    private ThemeSettingsService themeSettingsService;
    private TenantSettingsService tenantSettingsService;
    private ImageService imageService;
    private ConnectionContextService connectionContextService;
    private final GcmSchedulerService gcmSchedulerService;
    private VersionService versionService;
    private static final String TAG = InitialDataClientImpl.class.getName();

    public InitialDataClientImpl(RestService restService, ListDbClient<CatalogGroup> catalogDbClient,
                                 UserContextService userContextService, ThemeSettingsService themeSettingsService,
                                 TenantSettingsService tenantSettingsService, ImageService imageService,
                                 ConnectionContextService connectionContextService, DisplayLabelService displayLabelService,
                                 GcmSchedulerService gcmSchedulerService, VersionService versionService) {
        this.restService = restService;
        this.catalogDbClient = catalogDbClient;
        this.userContextService = userContextService;
        this.displayLabelService = displayLabelService;
        this.themeSettingsService = themeSettingsService;
        this.tenantSettingsService = tenantSettingsService;
        this.imageService = imageService;
        this.connectionContextService = connectionContextService;
        this.gcmSchedulerService = gcmSchedulerService;
        this.versionService = versionService;
    }

    public String getClientPrefix() {
        return "initialData";
    }

    @Override
    public Observable<InitialData> initializeData(final Context context) {
        final Integer appCurrentVersion = versionService.getAppCurrentVersion(context);

        final Observable<InitialData> initialDataObservable = catalogDbClient.getAllByType()
                .filter(new Func1<List<DataBlob<CatalogGroup>>, Boolean>() {
                    @Override
                    public Boolean call(List<DataBlob<CatalogGroup>> dataBlobs) {
                        Boolean result = true;
                        if (dataBlobs != null && !dataBlobs.isEmpty()) {
                            long lastTimeFetched = dataBlobs.get(0).getLastFetched();
                            result = TimeAgeUtils.isTimeOlderThanXHours(lastTimeFetched, INITIAL_DATA_MAX_AGE);
                        }

                        return result;
                    }
                })
                .map(ClientDataFlowUtils.<CatalogGroup>listBlobToDataFunction())
                .map(new Func1<List<CatalogGroup>, DiffQuery>() {
                    @Override
                    public DiffQuery call(List<CatalogGroup> categories) {
                        return DiffQueryUtils.createDiffQuery(categories);
                    }
                }).flatMap(new Func1<DiffQuery, Observable<InitialData>>() {
                    @Override
                    public Observable<InitialData> call(DiffQuery categoriesDiffQuery) {
                        InitialDataQuery initialDataQuery = new InitialDataQuery();
                        initialDataQuery.setCategoriesDiffQuery(categoriesDiffQuery);
                        return restService.createPostRequest(getClientPrefix(), initialDataQuery, InitialData.class, context);
                    }
                }).cache();
        final Observable<Void> setCurrentUser = initialDataObservable.flatMap(new Func1<InitialData, Observable<Void>>() {
            @Override
            public Observable<Void> call(InitialData initialData) {
                return userContextService.setUserModel(initialData.getCurrentUser());
            }
        });
        final Observable<Void> setLabels = initialDataObservable.flatMap(new Func1<InitialData, Observable<Void>>() {
            @Override
            public Observable<Void> call(InitialData initialData) {
                return displayLabelService.setLabels(initialData.getDisplayLabelsWrapper());
            }
        });
        final Observable<Void> setThemeSettings = initialDataObservable.flatMap(new Func1<InitialData, Observable<Void>>() {
            @Override
            public Observable<Void> call(InitialData initialData) {
                return themeSettingsService.setThemeSettings(initialData.getThemeSettings());
            }
        });
        final Observable<Void> setTenantSettings = initialDataObservable.flatMap(new Func1<InitialData, Observable<Void>>() {
            @Override
            public Observable<Void> call(InitialData initialData) {
                return tenantSettingsService.setTenantSettings(initialData.getTenantSettings());
            }
        });

        final Observable<InitialData> updateDb = initialDataObservable.doOnNext(new Action1<InitialData>() {
            @Override
            public void call(InitialData initialData) {
                CatalogGroupDiffResult categoriesDiff = initialData.getCategoriesDiff();
                if (categoriesDiff != null) {
                    catalogDbClient.update(categoriesDiff.getAddedOrUpdated(), categoriesDiff.getDeletedIds(), categoriesDiff.getOrderedIds());
                } else {
                    Log.e(TAG, "Categories diff result from initial data are null, cannot update db");
                }
            }
        });

        final Observable<Void> prefetchBackgroundImages = initialDataObservable.flatMap(new Func1<InitialData, Observable<Void>>() {
            @Override
            public Observable<Void> call(InitialData initialData) {
                return imageService.prefetchImage(initialData.getThemeSettings().getBackgroundImageId(), Picasso.Priority.HIGH, null, null);
            }
        }).timeout(5, TimeUnit.SECONDS)
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(this.getClass().getName(), "Pre-fetching background image failed");
                    }
                })
                .onErrorResumeNext(new Func1<Throwable, Observable<? extends Void>>() {
                    @Override
                    public Observable<? extends Void> call(Throwable throwable) {
                        return Observable.just(null); // Avoid failing initial data if prefetching images failed
                    }
                });

        return initialDataObservable.doOnNext(new Action1<InitialData>() {
            @Override
            public void call(InitialData initialData) {
                if (initialData != null && initialData.getTenantSettings() != null &&
                        !initialData.getTenantSettings().isMobileSupportedOnTenant()) {
                    throw new TenantUnsupportedOnMobileException();
                }
            }
        }).doOnNext(new Action1<InitialData>() {
            @Override
            public void call(InitialData initialData) {
                if (initialData != null && initialData.getMinSupportedVersion().getAndroidAppMinVersion() > appCurrentVersion){
                    throw new AppUpgradeRequiredException(initialData.getMinSupportedVersion().getAndroidAppMinVersion());
                }
            }
        }).doOnError(new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    // Clear previous login details to enable logging in again
                    if (throwable instanceof TenantUnsupportedOnMobileException) {
                        connectionContextService.clearHeadersAndCookies();
                    }
                }
            }).flatMap(new Func1<InitialData, Observable<InitialData>>() {
                @Override
                public Observable<InitialData> call(final InitialData initialData) {
                return Observable.zip(setCurrentUser, setLabels, setThemeSettings, setTenantSettings, updateDb, prefetchBackgroundImages, new Func6<Void, Void, Void, Void, InitialData, Void, InitialData>() {
                        @Override
                    public InitialData call(Void aVoid1, Void aVoid2, Void aVoid3, Void aVoid4, InitialData initialData, Void aVoid5) {
                            return initialData;
                        }
                    }).doOnNext(new Action1<InitialData>() {
                        @Override
                        public void call(InitialData initialData) {
                            // this depends on user model being filled
                            Intent intent = new Intent(context, GcmRegistrationIntentService.class);
                            context.startService(intent);
                        }
                    }).doOnNext(new Action1<InitialData>() {
                        @Override
                        public void call(InitialData initialData) {
                            gcmSchedulerService.scheduleGCMJob(context);
                        }
                    });
                }
            }).subscribeOn(Schedulers.io());
        }

}

