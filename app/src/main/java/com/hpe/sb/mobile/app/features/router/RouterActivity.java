package com.hpe.sb.mobile.app.features.router;

import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.common.dataClients.article.restClient.ArticleRestClient;
import com.hpe.sb.mobile.app.common.dataClients.catalog.CatalogClient;
import com.hpe.sb.mobile.app.common.dataClients.initialData.InitialDataClient;
import com.hpe.sb.mobile.app.common.dataClients.user.UserClient;
import com.hpe.sb.mobile.app.common.dataClients.userContext.ApplicationType;
import com.hpe.sb.mobile.app.common.dataClients.userContext.UserContextService;
import com.hpe.sb.mobile.app.common.utils.BrowserUtils;
import com.hpe.sb.mobile.app.features.article.ArticleActivity;
import com.hpe.sb.mobile.app.features.catalog.CategoryPageActivity;
import com.hpe.sb.mobile.app.features.detailsActivity.DetailsActivity;
import com.hpe.sb.mobile.app.features.home.MainActivity;
import com.hpe.sb.mobile.app.features.login.activities.ChangingDestinationActivity;
import com.hpe.sb.mobile.app.features.login.activities.PreLoginActivity;
import com.hpe.sb.mobile.app.features.login.activities.WebViewLoginActivity;
import com.hpe.sb.mobile.app.features.login.services.ActivationUrlService;
import com.hpe.sb.mobile.app.features.login.services.ConnectionContextService;
import com.hpe.sb.mobile.app.features.login.services.PostLoginService;
import com.hpe.sb.mobile.app.features.request.NewRequestActivity;
import com.hpe.sb.mobile.app.features.request.RequestClient;
import com.hpe.sb.mobile.app.features.request.helper.RequestSubmittedPageHelper;
import com.hpe.sb.mobile.app.infra.baseActivities.BaseActivity;
import com.hpe.sb.mobile.app.infra.exception.AppUpgradeRequiredException;
import com.hpe.sb.mobile.app.infra.exception.NotFoundException;
import com.hpe.sb.mobile.app.infra.exception.RequestTimeoutException;
import com.hpe.sb.mobile.app.infra.exception.TenantUnsupportedOnMobileException;
import com.hpe.sb.mobile.app.infra.rx.BaseSubscriber;
import com.hpe.sb.mobile.app.serverModel.InitialData;
import com.hpe.sb.mobile.app.serverModel.article.Article;
import com.hpe.sb.mobile.app.serverModel.catalog.CatalogGroup;
import com.hpe.sb.mobile.app.serverModel.request.FullRequest;
import com.hpe.sb.mobile.app.serverModel.request.RequestForTrackingPage;
import com.hpe.sb.mobile.app.serverModel.user.useritems.ApprovalUserItem;
import com.hpe.sb.mobile.app.serverModel.user.useritems.RequestActiveUserItem;
import com.hpe.sb.mobile.app.serverModel.user.useritems.UserItem;
import com.hpe.sb.mobile.app.serverModel.user.useritems.UserItems;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;


public class RouterActivity extends BaseActivity {

    private static final String HTTP = "http";
    public static final String SHORT_HOST_QUERY_PARAM_NAME = "shortHost";

    @Inject
    UserContextService userContext;

    @Inject
    protected ConnectionContextService connectionContextService;

    @Inject
    public InitialDataClient initialDataClient;

    @Inject
    public PostLoginService postLoginService;

    @Inject
    public UserClient userClient;

    @Inject
    public CatalogClient catalogClient;

    @Inject
    public RequestClient requestClient;

    @Inject
    public ArticleRestClient articleRestClient;

    @Inject
    ActivationUrlService activationUrlService;

    public static final int LOGIN_REQUEST_CODE = 369;

    public static final String URL_QUERY_PARAMETER = "url";

    private static final String TAG = RouterActivity.class.getSimpleName();

    private boolean isBackFromAnotherActivity = false;

    private Bundle extras;

    private Observable<Void> daggerInitObservable;

    public static Intent createIntent(Context context, Bundle routeBundle) {
        Intent intent = new Intent(context, RouterActivity.class);
        intent.putExtras(routeBundle);
        return intent;
    }

    public static Intent createIntent(Context context) {
        return new Intent(context, RouterActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        daggerInitObservable = Observable.fromCallable(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                ((ServiceBrokerApplication) getApplicationContext()).getServiceBrokerComponent().inject(RouterActivity.this);
                return null;
            }
        }).cache()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
        daggerInitObservable.subscribe(new Subscriber<Void>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(RouterActivity.this, R.string.application_start_error, Toast.LENGTH_LONG)
                        .show();
                finish();
            }

            @Override
            public void onNext(Void aVoid) {

            }
        });
        setContentView(R.layout.activity_splash_screen);
        this.extras = savedInstanceState != null ? savedInstanceState : getIntent().getExtras();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (extras != null) {
            outState.putInt(RouteBundleFactory.ROUTE_CODE_EXTRA, extras.getInt(RouteBundleFactory.ROUTE_CODE_EXTRA));
            outState.putParcelable(RouteBundleFactory.MG_ITEM_EXTRA, extras.getParcelable(RouteBundleFactory.MG_ITEM_EXTRA));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isBackFromAnotherActivity) {
            return;
        }

        daggerInitObservable.observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseSubscriber<Void>(this, getSbExceptionsHandler()) {
            @Override
            public void onNext(Void aVoid) {
                boolean isLoginNeeded = loginIfNeeded();
                if (!isLoginNeeded) {
                    doInitialDataAndRoute();
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "Dagger initialization failed", e);
                super.onError(e);
            }
        });
    }

    private void doInitialDataAndRoute() {
        initialDataClient.initializeData(this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<InitialData>(RouterActivity.this, getSbExceptionsHandler()) {
                    @Override
                    public void onCompleted() {
                        doRouting();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Init data failed", e);
                        if (e instanceof RequestTimeoutException) {
                            doRouting();
                        } else if (e instanceof TenantUnsupportedOnMobileException) {
                            handleTenantUnsupportedOnMobileError();
                        } else if (e instanceof AppUpgradeRequiredException) {
                            Log.e(TAG, "app upgrade required, ,min supported version is " + ((AppUpgradeRequiredException) e).getAndroidAppMinVersion(), e);
                            handleUpgradeRequiredError();
                        }
                        else {
                           super.onError(e);
                        }
                    }

                    @Override
                    public void onNext(InitialData initialData) {

                    }
                });
    }

    private void doRouting() {
        Uri uriData = getIntent().getData();
        if (uriData != null && !isUniqueAppScheme(uriData.getScheme())) {
            Log.i(TAG, "Received deepLinking data: " + uriData);
                deepLinkingRoute(uriData);
            return;
        }
        Integer routerRequestCode = getRouterRequestCode();
        if (routerRequestCode != null) {
            Log.i(TAG, "Received notification data: " + routerRequestCode);
            notificationRoute(routerRequestCode);
            return;
        }
        startMainActivity();
    }

    private void startMainActivity() {
        Intent i = new Intent(RouterActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void startDetailsActivityWithBackStack(boolean disableButtonPanel, boolean shouldOpenToComment, boolean shouldOpenDenyDialog, UserItem userItem) {
        Intent detailsActivityIntent = DetailsActivity.createIntent(this, userItem, disableButtonPanel, shouldOpenToComment, shouldOpenDenyDialog);
        startActivityWithBackStack(detailsActivityIntent);
    }

    private void startActivityWithBackStack(Intent intent) {
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(DetailsActivity.class);
        stackBuilder.addNextIntent(intent);
        stackBuilder.startActivities();
        finish();
    }

    private Integer getRouterRequestCode() {
        Integer routerRequestCode;
        if (extras == null) {
            routerRequestCode = null;
        } else {
            routerRequestCode = extras.getInt(RouteBundleFactory.ROUTE_CODE_EXTRA, Routes.DEFAULT);
        }
        return routerRequestCode;
    }

    private void notificationRoute(int routerRequestCode) {
        switch (routerRequestCode) {
            case Routes.DEFAULT: {
                Intent i = new Intent(RouterActivity.this, MainActivity.class);
                startActivity(i);
                finish();
                break;
            }
            case Routes.REQUEST: {
                startDetailsActivityWithBackStack(false, false, false, (UserItem) extras.getParcelable(RouteBundleFactory.MG_ITEM_EXTRA));
                break;
            }
            case Routes.COMMENT_ON_REQUEST: {
                startDetailsActivityWithBackStack(false, true, false, (UserItem) extras.getParcelable(RouteBundleFactory.MG_ITEM_EXTRA));
                break;
            }
            case Routes.REQUEST_WITH_DENY_DIALOG: {
                startDetailsActivityWithBackStack(false, false, true, (UserItem) extras.getParcelable(RouteBundleFactory.MG_ITEM_EXTRA));
                break;
            }
            case Routes.RELOGIN:{
                doLogin();
            }
            default: {
                Log.e(TAG, "Unidentified request code for router");
                finish();
            }
        }
    }

    private void deepLinkingRoute(Uri uriData) {
        final List<String> pathSegments = uriData.getPathSegments();
        String currentUrl = activationUrlService.getActivationUrl();
        Uri currentUri = Uri.parse(currentUrl);
        boolean isHostNotEqual = currentUri.getHost() != null && !currentUri.getHost().equals(uriData.getHost());
        boolean isTenantIdNotEqual = currentUri.getQueryParameter("TENANTID") != null && uriData.getQueryParameter("TENANTID") != null &&
                !currentUri.getQueryParameter("TENANTID").equals(uriData.getQueryParameter("TENANTID"));
        if ( isHostNotEqual || isTenantIdNotEqual) {
            startChangingDestinationActivity();
        } else {
            String firstPathSegment = pathSegments.get(0);
            if (firstPathSegment.equals("saw") || firstPathSegment.equals("main")) {
                switch (pathSegments.get(1)) {
                    case "ess":
                        switch (pathSegments.get(2)) {
                            case "requestTracking":
                                startRequestActivity(pathSegments.get(3));
                                break;
                            case "offeringPage":
                                startActivityWithBackStack(NewRequestActivity.createIntentForOffering(getBaseContext(), pathSegments.get(3)));
                                break;
                            case "categoryPage":
                                startCategoryActivity(pathSegments.get(3));
                                break;
                            case "viewResult":
                                startArticleActivity(pathSegments.get(3));
                                break;
                            default:
                                BrowserUtils.sendToWebBrowser(uriData, this);
                        }
                        break;
                    case "Request":
                        startRequestActivity(pathSegments.get(2));
                        break;
                    case "Offering":
                        startActivityWithBackStack(NewRequestActivity.createIntentForOffering(getBaseContext(), pathSegments.get(2)));
                        break;
                    case "Category":
                        startCategoryActivity(pathSegments.get(2));
                        break;
                    case "Article":
                        startActivityWithBackStack(ArticleActivity.createIntent(this, pathSegments.get(2), "Article"));
                        break;
                    case "News":
                        startActivityWithBackStack(ArticleActivity.createIntent(this, pathSegments.get(2), "News"));
                        break;
                    default:
                }
            } else {
                startMainActivity();
            }
        }
    }

    private void startChangingDestinationActivity() {
        Intent i = new Intent(RouterActivity.this, ChangingDestinationActivity.class);
        i.setData(getIntent().getData());
        startActivity(i);
        finish();
    }

    private void startCategoryActivity(final String categoryId) {
        catalogClient.getCategories(this, false /* !forceRefresh */)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<List<CatalogGroup>>(this, sbExceptionsHandler) {
                    @Override
                    public void onNext(List<CatalogGroup> categories) {
                        CatalogGroup category = getRelevantCategory(categories, categoryId);
                        if (category != null) {
                            Intent intent = CategoryPageActivity.createIntent(getBaseContext(), category);
                            startActivityWithBackStack(intent);
                        } else {
                            Log.e(TAG, getString(R.string.unknown_category_id));
                            super.onError(new NotFoundException());
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(getClass().getName(), "Failed to get Categories from link", e);
                        super.onError(e);
                    }
                });
    }

    private void startArticleActivity(final String requestId) {
        Observable<Article> articleObservable = articleRestClient.getArticle(this, requestId);
        articleObservable.subscribeOn(Schedulers.io()).subscribe(new BaseSubscriber<Article>(this, sbExceptionsHandler) {
            @Override
            public void onNext(Article article) {
                startActivityWithBackStack(ArticleActivity.createIntent(getBaseContext(), requestId, article.getSubType()));
            }

            @Override
            public void onError(Throwable e) {
                Log.e(getClass().getName(), "failed to open article from link", e);
                super.onError(e);
            }
        });
    }

    private void startRequestActivity(final String requestId) {
        userClient.getUserItems(this, true).observeOn(Schedulers.io()).subscribe(new BaseSubscriber<UserItems>(this, sbExceptionsHandler) {
            @Override
            public void onNext(UserItems userItems) {
                UserItem item = getRelevantMGItem(userItems, requestId);
                if (item != null) {
                    startDetailsActivityWithBackStack(false, false, false, item);
                } else {
                    requestClient.getRequestForTrackingPage(getBaseContext(), requestId).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread()).subscribe(
                            new BaseSubscriber<RequestForTrackingPage>(RouterActivity.this, sbExceptionsHandler) {
                                @Override
                                public void onNext(RequestForTrackingPage requestForTrackingPage) {
                                    FullRequest request = requestForTrackingPage.getRequestForForm().getRequest();
                                    RequestActiveUserItem requestActiveItem = new RequestActiveUserItem(requestForTrackingPage.getId(),
                                            requestForTrackingPage.getTitle(),
                                            RequestSubmittedPageHelper.convertEntityTypeToRequestType(request.getOfferingType()),
                                            requestForTrackingPage.getPhase(),
                                            requestForTrackingPage.getMetaPhase(),
                                            requestForTrackingPage.getRequestedForName(),
                                            requestForTrackingPage.getDescription(),
                                            requestForTrackingPage.getCreationTime());
                                    startDetailsActivityWithBackStack(true, false, false, requestActiveItem); //
                                }

                                @Override
                                public void onError(Throwable e) {
                                    Log.e(getClass().getName(), "failed to open request from link", e);
                                    super.onError(e);
                                }
                            });
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(getClass().getName(), "failed to get user items for opening request from link", e);
                super.onError(e);
            }
        });
    }

    private CatalogGroup getRelevantCategory(List<CatalogGroup> CatalogList, String categoryId) {
        for (CatalogGroup category : CatalogList) {
            if (categoryId.equals(category.getId())) {
                return category;
            }
        }
        return null;
    }

    private UserItem getRelevantMGItem(UserItems userItems, String entityId) {
        for (UserItem userItem : userItems.getAllUserItems()) {
            String id = userItem.getId();
            if (userItem instanceof ApprovalUserItem) {
                id = ((ApprovalUserItem)userItem).getEntityId();
            }
            if (entityId.equals(id)) {
                return userItem;
            }
        }
        return null;
    }

    private boolean loginIfNeeded() {
        boolean isLoginNeeded = !connectionContextService.isConnectionContextValid();
        if (isLoginNeeded) {
            doLogin();
        }
        return isLoginNeeded;
    }

    private void doLogin() {
        String urlString = null;
        boolean preLoginRequired = true;
        Uri uriData = getIntent().getData();
        if (uriData != null) {
            if (isUniqueAppScheme(uriData.getScheme())) {
                urlString = getUrlFromQueryParamIfExists();
                preLoginRequired = (urlString == null);
            }else{
                Uri.Builder builder = new Uri.Builder();
                if (uriData.getQueryParameter(PreLoginActivity.TENANT_ID_QUERY_PARAM_NAME) != null) {
                    builder.appendQueryParameter(PreLoginActivity.TENANT_ID_QUERY_PARAM_NAME,
                            uriData.getQueryParameter(PreLoginActivity.TENANT_ID_QUERY_PARAM_NAME));
                    preLoginRequired = false; //tenant id exists, so we can skip pre-login
                }else if(uriData.getQueryParameter(SHORT_HOST_QUERY_PARAM_NAME) != null){
                    preLoginRequired = false; // shortHost parameter exists, so we can skip pre-login
                }
                Uri uri = builder.scheme(HTTP).encodedAuthority(uriData.getAuthority())
                        .appendQueryParameter("APP", "SAW").build();
                urlString = uri.toString();
            }
        }
        if (preLoginRequired){
            Intent intent = new Intent(this, PreLoginActivity.class);
            startActivityForResult(intent, LOGIN_REQUEST_CODE);
        } else {
            connectionContextService.updateApplicationType(ApplicationType.SAW);
            activationUrlService.setActivationUrl(urlString);
            Intent intent = WebViewLoginActivity.createIntent(this, urlString);
            startActivityForResult(intent, LOGIN_REQUEST_CODE);
        }
    }
    private boolean isUniqueAppScheme(String scheme){
        return scheme.equals("propel") || scheme.equals("com.saw.mobile.portal");
    }

    @Nullable
    private String getUrlFromQueryParamIfExists() {
        Uri data = getIntent().getData();
        String url = null;
        if (data != null) {
            url = data.getQueryParameter(URL_QUERY_PARAMETER);
        }
        return url;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isBackFromAnotherActivity = true;
        if (requestCode == LOGIN_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                daggerInitObservable
                        .flatMap(new Func1<Void, Observable<Void>>() {
                            @Override
                            public Observable<Void> call(Void aVoid) {
                                return postLoginService.initializePostLogin(RouterActivity.this);
                            }
                        })
                        .flatMap(new Func1<Void, Observable<InitialData>>() {
                            @Override
                            public Observable<InitialData> call(Void aVoid) {
                                return initialDataClient.initializeData(RouterActivity.this);
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BaseSubscriber<InitialData>(RouterActivity.this, getSbExceptionsHandler()) {
                            @Override
                            public void onCompleted() {
                                doRouting();
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (e instanceof TenantUnsupportedOnMobileException) {
                                    Log.e(TAG, "tenant not supported", e);
                                    handleTenantUnsupportedOnMobileError();
                                } else if (e instanceof AppUpgradeRequiredException) {
                                    Log.e(TAG, "app upgrade required, ,min supported version is " + ((AppUpgradeRequiredException) e).getAndroidAppMinVersion(),
                                            e);
                                    handleUpgradeRequiredError();
                                } else {
                                    Log.e(TAG, "post login or initialize data failed", e);
                                    super.onError(e);
                                }
                            }

                            @Override
                            public void onNext(InitialData initialData) {

                            }
                        });
            } else {
                Log.e(TAG, "Error: failed to login");
                finish();
            }
        }
    }

    private void handleTenantUnsupportedOnMobileError() {
        List<BaseDialog.ActionButton> buttons = new ArrayList<>();
        buttons.add(new BaseDialog.ActionButton(R.id.done_button, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }));
        BaseDialog baseDialog = new BaseDialog(RouterActivity.this, R.layout.tenant_unsupported_on_mobile_dialog_layout, buttons);
        baseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        baseDialog.show();
    }

    private void handleUpgradeRequiredError() {
        List<BaseDialog.ActionButton> buttons = new ArrayList<>();
        buttons.add(new BaseDialog.ActionButton(R.id.done_button, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                final String appPackageName = getPackageName();
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        }));
        BaseDialog baseDialog = new BaseDialog(RouterActivity.this, R.layout.upgrade_required_dialog_layout, buttons);
        baseDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        baseDialog.show();
    }

}
