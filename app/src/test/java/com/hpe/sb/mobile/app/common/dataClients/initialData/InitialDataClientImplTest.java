package com.hpe.sb.mobile.app.common.dataClients.initialData;

import android.content.Context;

import com.hpe.sb.mobile.app.common.dataClients.userContext.UserContextService;
import com.hpe.sb.mobile.app.common.dataClients.displaylabels.DisplayLabelService;
import com.hpe.sb.mobile.app.common.services.version.VersionService;
import com.hpe.sb.mobile.app.features.googlepushnotification.scheduler.GcmSchedulerService;
import com.hpe.sb.mobile.app.infra.dataClients.ListDbClient;
import com.hpe.sb.mobile.app.infra.exception.AppUpgradeRequiredException;
import com.hpe.sb.mobile.app.infra.image.ImageService;
import com.hpe.sb.mobile.app.features.login.services.ConnectionContextService;
import com.hpe.sb.mobile.app.serverModel.InitialData;
import com.hpe.sb.mobile.app.serverModel.TenantSettings;
import com.hpe.sb.mobile.app.serverModel.ThemeSettings;
import com.hpe.sb.mobile.app.serverModel.VersionContainer;
import com.hpe.sb.mobile.app.serverModel.catalog.CatalogGroupDiffResult;
import com.hpe.sb.mobile.app.serverModel.catalog.CatalogGroup;
import com.hpe.sb.mobile.app.serverModel.displayLabels.DisplayLabelsWrapper;
import com.hpe.sb.mobile.app.serverModel.user.User;
import com.hpe.sb.mobile.app.infra.dataClients.DataBlob;
import com.hpe.sb.mobile.app.infra.restclient.RestService;
import com.hpe.sb.mobile.app.common.dataClients.tenantSettings.TenantSettingsService;
import com.hpe.sb.mobile.app.common.dataClients.themeSettings.ThemeSettingsService;
import com.squareup.picasso.Picasso;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.observers.TestSubscriber;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class InitialDataClientImplTest {

    private RestService restServiceMock;
    private UserContextService userContextServiceMock;
    private ThemeSettingsService themeSettingsServiceMock;
    private TenantSettingsService tenantSettingsService;
    private ListDbClient<CatalogGroup> catalogPersistenceClientMock;
    private ImageService imageServiceMock;
    private DisplayLabelService displayLabelService;
    private ConnectionContextService connectionContextService;
    private VersionService versionService;

    private InitialDataClient initialDataClient;

    private GcmSchedulerService gcmSchedulerService;

    @Before
    public void setup() {
        restServiceMock = mock(RestService.class);
        userContextServiceMock = mock(UserContextService.class);
        themeSettingsServiceMock = mock(ThemeSettingsService.class);
        tenantSettingsService = mock(TenantSettingsService.class);
        catalogPersistenceClientMock = mock(ListDbClient.class);
        imageServiceMock = mock(ImageService.class);
        displayLabelService = mock(DisplayLabelService.class);
        connectionContextService = mock(ConnectionContextService.class);
        gcmSchedulerService = mock(GcmSchedulerService.class);
        versionService = mock(VersionService.class);

        initialDataClient = new InitialDataClientImpl(restServiceMock, catalogPersistenceClientMock,
                userContextServiceMock, themeSettingsServiceMock, tenantSettingsService, imageServiceMock, connectionContextService, displayLabelService,
                gcmSchedulerService, versionService);
    }

    @Test
    public void testSuccess() {
        InitialData initialData = prepareInitialData();
        Observable<List<DataBlob<CatalogGroup>>> categoriesFromPersistence = Observable.just(((List<DataBlob<CatalogGroup>>) new ArrayList<DataBlob<CatalogGroup>>()));
        when(restServiceMock.createPostRequest(anyString(), any(), any(Class.class), any(Context.class)))
                .thenReturn(Observable.just(initialData));
        when(catalogPersistenceClientMock.getAllByType()).thenReturn(categoriesFromPersistence);
        when(imageServiceMock.prefetchImage(anyString(), any(Picasso.Priority.class), anyInt(), anyInt())).thenReturn(Observable.<Void>just(null));
        when(userContextServiceMock.setUserModel(any(User.class))).thenReturn(Observable.<Void>just(null));
        when(displayLabelService.setLabels(any(DisplayLabelsWrapper.class))).thenReturn(Observable.<Void>just(null));
        when(themeSettingsServiceMock.setThemeSettings(any(ThemeSettings.class))).thenReturn(Observable.<Void>just(null));
        when(tenantSettingsService.setTenantSettings(any(TenantSettings.class))).thenReturn(Observable.<Void>just(null));
		when(versionService.getAppCurrentVersion( any(Context.class))).thenReturn(1);

        Observable<InitialData> initialDataObservable = initialDataClient.initializeData(mock(Context.class));
        TestSubscriber<InitialData> testSubscriber = new TestSubscriber<>();
        initialDataObservable.subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();

        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertValueCount(1);
        verify(gcmSchedulerService).scheduleGCMJob(any(Context.class));
    }

    @Test
    public void testErrorUpgradeRequired() {
        InitialData initialData = prepareInitialData();
        Observable<List<DataBlob<CatalogGroup>>> categoriesFromPersistence = Observable.just(((List<DataBlob<CatalogGroup>>) new ArrayList<DataBlob<CatalogGroup>>()));
        when(restServiceMock.createPostRequest(anyString(), any(), any(Class.class), any(Context.class)))
                .thenReturn(Observable.just(initialData));
        when(catalogPersistenceClientMock.getAllByType()).thenReturn(categoriesFromPersistence);
        when(imageServiceMock.prefetchImage(anyString(), any(Picasso.Priority.class), anyInt(), anyInt())).thenReturn(Observable.<Void>just(null));
        when(userContextServiceMock.setUserModel(any(User.class))).thenReturn(Observable.<Void>just(null));
        when(versionService.getAppCurrentVersion( any(Context.class))).thenReturn(0);

        Observable<InitialData> initialDataObservable = initialDataClient.initializeData(mock(Context.class));
        TestSubscriber<InitialData> testSubscriber = new TestSubscriber<>();
        initialDataObservable.subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();

        testSubscriber.assertError(AppUpgradeRequiredException.class);
        verify(gcmSchedulerService, times(0)).scheduleGCMJob(any(Context.class));
    }



    @Test
    public void testErrorInCriticalPart() {
        Observable<List<DataBlob<CatalogGroup>>> categoriesFromPersistence = Observable.just(((List<DataBlob<CatalogGroup>>) new ArrayList<DataBlob<CatalogGroup>>()));
        RuntimeException error = new RuntimeException("Fail me? Fail you!");
        when(restServiceMock.createPostRequest(anyString(), any(), any(Class.class), any(Context.class)))
                .thenReturn(Observable.error(error));
        when(catalogPersistenceClientMock.getAllByType()).thenReturn(categoriesFromPersistence);
        when(versionService.getAppCurrentVersion( any(Context.class))).thenReturn(1);

        Observable<InitialData> initialDataObservable = initialDataClient.initializeData(mock(Context.class));
        TestSubscriber<InitialData> testSubscriber = new TestSubscriber<>();
        initialDataObservable.subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();

        testSubscriber.assertError(error);
        verify(gcmSchedulerService, times(0)).scheduleGCMJob(any(Context.class));
    }

    @Test
    public void testErrorInNonCriticalPart() {
        InitialData initialData = prepareInitialData();
        Observable<List<DataBlob<CatalogGroup>>> categoriesFromPersistence = Observable.just(((List<DataBlob<CatalogGroup>>) new ArrayList<DataBlob<CatalogGroup>>()));
        when(restServiceMock.createPostRequest(anyString(), any(), any(Class.class), any(Context.class)))
                .thenReturn(Observable.just(initialData));
        when(catalogPersistenceClientMock.getAllByType()).thenReturn(categoriesFromPersistence);
        when(imageServiceMock.prefetchImage(anyString(), any(Picasso.Priority.class), anyInt(), anyInt()))
                .thenReturn(Observable.<Void>error(new RuntimeException("I won't fail you, I promise..")));
        when(userContextServiceMock.setUserModel(any(User.class))).thenReturn(Observable.<Void>just(null));
        when(displayLabelService.setLabels(any(DisplayLabelsWrapper.class))).thenReturn(Observable.<Void>just(null));
        when(themeSettingsServiceMock.setThemeSettings(any(ThemeSettings.class))).thenReturn(Observable.<Void>just(null));
        when(tenantSettingsService.setTenantSettings(any(TenantSettings.class))).thenReturn(Observable.<Void>just(null));
		when(versionService.getAppCurrentVersion( any(Context.class))).thenReturn(1);

        Observable<InitialData> initialDataObservable = initialDataClient.initializeData(mock(Context.class));
        TestSubscriber<InitialData> testSubscriber = new TestSubscriber<>();
        initialDataObservable.subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();

        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertValueCount(1);
        verify(gcmSchedulerService, times(1)).scheduleGCMJob(any(Context.class));
    }

    private InitialData prepareInitialData() {
        InitialData initialData = new InitialData();
        initialData.setCategoriesDiff(new CatalogGroupDiffResult());
        initialData.setThemeSettings(new ThemeSettings());
        VersionContainer versionContainer = new VersionContainer();
        versionContainer.setAndroidAppMinVersion(1);
        versionContainer.setIosAppMinVersion(1);
        initialData.setMinSupportedVersion(versionContainer);

        return initialData;
    }
}
