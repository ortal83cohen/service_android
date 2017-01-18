package com.hpe.sb.mobile.app.features.home;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.StringRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.*;
import android.widget.*;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.common.dataClients.catalog.CatalogClient;
import com.hpe.sb.mobile.app.common.dataClients.catalog.restClient.CatalogRestClient;
import com.hpe.sb.mobile.app.common.dataClients.search.SearchClient;
import com.hpe.sb.mobile.app.common.dataClients.themeSettings.ThemeSettingsService;
import com.hpe.sb.mobile.app.common.dataClients.user.UserClient;
import com.hpe.sb.mobile.app.common.dataClients.userContext.UserContextService;
import com.hpe.sb.mobile.app.common.services.dateTime.DateTimeService;
import com.hpe.sb.mobile.app.common.services.dateTime.PartOfDay;
import com.hpe.sb.mobile.app.common.uiComponents.categories.BaseOnCategorySelectListener;
import com.hpe.sb.mobile.app.common.uiComponents.categories.CategoryPageRecyclerViewHelper;
import com.hpe.sb.mobile.app.common.uiComponents.categories.view.BrowseCategoriesCallback;
import com.hpe.sb.mobile.app.common.uiComponents.commonLayout.SwipeToRefreshNonHorizontal;
import com.hpe.sb.mobile.app.common.uiComponents.smartFeed.SmartFeedView;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.TodoCardsView;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.utils.TodoCardsViewActionsLogicHandler;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.utils.TodoCardsViewActionsLogicHandlerFactory;
import com.hpe.sb.mobile.app.features.catalog.CategoryPageActivity;
import com.hpe.sb.mobile.app.features.login.services.LogoutService;
import com.hpe.sb.mobile.app.features.request.NewRequestActivity;
import com.hpe.sb.mobile.app.features.search.SearchResultsActivity;
import com.hpe.sb.mobile.app.infra.baseActivities.BaseActivity;
import com.hpe.sb.mobile.app.infra.image.ImageService;
import com.hpe.sb.mobile.app.infra.rx.BaseSubscriber;
import com.hpe.sb.mobile.app.serverModel.ThemeSettings;
import com.hpe.sb.mobile.app.serverModel.catalog.CatalogGroup;
import com.hpe.sb.mobile.app.serverModel.user.Feed;
import com.hpe.sb.mobile.app.serverModel.user.User;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.List;

public class MainActivity extends BaseActivity
        implements TodoCardsViewActionsLogicHandler.OnCompletedLoadingListener, DisplayMessage {
    private static final String DO_LOGOUT = "DO_LOGOUT";

    @Inject
    public UserClient userClient;

    @Inject
    public CatalogRestClient catalogRestClient;

    @Inject
    public SearchClient searchClient;

    @Inject
    public ThemeSettingsService themeSettingsService;

    @Inject
    public ImageService imageService;

    @Inject
    public CatalogClient catalogClient;

    @Inject
    public UserContextService userContextService;

    @Inject
    public LogoutService logoutService;

    @Inject
    public TodoCardsViewActionsLogicHandlerFactory todoCardsViewActionsLogicHandlerFactory;

    @Inject
    public DateTimeService dateTimeService;


    //browse categories
    private BrowseCategoriesCallback bottomSheetCallback;

    private FloatingActionButton newRequestButton;

    private TodoCardsViewActionsLogicHandler todoCardsViewActionsLogicHandler;

    private CoordinatorLayout coordinatorLayout;

    private CoordinatorLayout snackbarCoordinatorLayout;

    private SwipeRefreshLayout pullToRefresh;

    private Button searchButton;

    private EditText searchBox;

    private ScrollView scrollView;

    private SmartFeedView smartFeedView;

    private CategoryPageRecyclerViewHelper categoryPageRecyclerViewHelper;

    private boolean pullToRefreshRefreshable = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((ServiceBrokerApplication) getApplicationContext()).getServiceBrokerComponent()
                .inject(this);
        setContentView(R.layout.activity_main);
        setBackground();
        updateGreetingMessage();
        setupToolbar();
        getToolbar().getBackground().setAlpha(0);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        snackbarCoordinatorLayout = (CoordinatorLayout) findViewById(
                R.id.snackbarCoordinatorLayout);

        initCardsViewHandler();
        newRequestButton = (FloatingActionButton) findViewById(R.id.new_request);
        searchButton = (Button) findViewById(R.id.search_btn);
        searchBox = (EditText) findViewById(R.id.search);
        if (searchBox != null) {
            searchBox.setText("");
        }

        setButtonHandler();
        ViewGroup bottomSheet = (ViewGroup) coordinatorLayout
                .findViewById(R.id.categories_slide_pane);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        this.bottomSheetCallback = new BrowseCategoriesCallback(this, bottomSheet, behavior,
                newRequestButton);
        smartFeedView = ((SmartFeedView) findViewById(R.id.smart_feed));
        pullToRefresh = (SwipeToRefreshNonHorizontal) findViewById(R.id.pullToRefresh);

        initCategories();

        pullToRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh(true);
            }
        });
        pullToRefresh.setColorSchemeResources(R.color.offering_service_type_color);

        pullToRefresh.setHorizontalFadingEdgeEnabled(false);

        scrollView.getViewTreeObserver()
                .addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
                    int lastScroll = 0;

                    @Override
                    public void onScrollChanged() {
                        int scrollY = scrollView.getScrollY();
                        if (lastScroll != scrollY) {
                            lastScroll = scrollY;
                            pullToRefreshRefreshable = scrollY == 0;
                            setRefreshable(scrollY == 0);
                            getToolbar().getBackground().setAlpha((int) (Math.min((float) scrollY / 80, 1) * 255));
                        }
                    }
                });

        Intent intent = getIntent();
        boolean doLogout = intent.getBooleanExtra(MainActivity.DO_LOGOUT, false);
        if(doLogout){
            logoutService.localLogout(this);
        }
    }

    private void initCategories() {
        final RecyclerView categoriesGrid = (RecyclerView) findViewById(R.id.categories_grid);
        categoryPageRecyclerViewHelper = new CategoryPageRecyclerViewHelper(MainActivity.this);
        categoryPageRecyclerViewHelper.initRecyclerView(categoriesGrid, false/*isHightlightable*/,
                new BaseOnCategorySelectListener(this) {
                    @Override
                    public boolean handleCategorySelected(final CatalogGroup catalogGroup) {
                        if (super.handleCategorySelected(catalogGroup)) {
                            return true;
                        }

                        Intent intent = CategoryPageActivity.createIntent(getApplicationContext(), catalogGroup);
                        startActivity(intent);
                        return true;
                    }
                });
    }

    @Override
    public void showNoConnectionSnackbar() {
        snackbar = showSnackbar(R.string.no_internet_connection, Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }

    private Snackbar showSnackbar(@StringRes int resId, @Snackbar.Duration int duration) {
        Snackbar snackbar = Snackbar
                .make(snackbarCoordinatorLayout, resId, duration)
                .setAction("1", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        refresh(true);
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(getResources().getColor(R.color.offering_service_type_color));

        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_action);
        textView.setText("");
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.refresh, 0);
        return snackbar;
    }

    @Override
    public void dismissSnackbarIfNeeded() {
        //do nothing otherwise categories page has a hole at the bottom
    }

    private void initCardsViewHandler() {
        TodoCardsView cardsView = ((TodoCardsView) findViewById(R.id.todo_card));
        if (cardsView != null) {
            this.todoCardsViewActionsLogicHandler = todoCardsViewActionsLogicHandlerFactory
                    .createTodoCardsViewActionsLogicHandler(this, cardsView, this);
        }
    }

    private void updateGreetingMessage() {
        TextView greetingPlaceholder = (TextView) findViewById(R.id.greeting);
        String greetingMessage = getGreetingMessage();
        if (greetingPlaceholder != null && greetingMessage != null) {
            greetingPlaceholder.setText(greetingMessage);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refresh(false);
        updateGreetingMessage();
        searchBox = (EditText) findViewById(R.id.search);
        searchBox.setText("");
    }

    private String getGreetingMessage() {
        User userModel = userContextService.getUserModel();
        if (userModel != null) {
            @PartOfDay int partOfDay = dateTimeService.getPartOfDay(Calendar.getInstance().get(Calendar.HOUR_OF_DAY));
            switch (partOfDay) {
                case PartOfDay.MORNING:
                    return getString(R.string.greeting_morning, userModel.getName());
                case PartOfDay.AFTERNOON:
                    return getString(R.string.greeting_afternoon, userModel.getName());
                case PartOfDay.EVENING:
                    return getString(R.string.greeting_evening, userModel.getName());
                case PartOfDay.NIGHT:
                    return getString(R.string.greeting_night, userModel.getName());
            }
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            logoutService.logout(this);
        }

        return super.onOptionsItemSelected(item);
    }

    private void setButtonHandler() {
        newRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NewRequestActivity.class));
            }
        });

        searchBox.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean gotFocus) {
                if (gotFocus) {
                    searchBox.clearFocus();
                    startSearchActivity();
                }
            }
        });
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startSearchActivity();
            }
        });
    }

    private void startSearchActivity() {
        Intent intent = SearchResultsActivity.createIntent(getApplicationContext(), "");
        startActivity(intent);
    }

    private void refresh(final boolean force) {
        todoCardsViewActionsLogicHandler.refreshCardsView(force);

        Observable<Feed> userItems = userClient.getSmartFeed(this, force /* !forceRefresh */);
        userItems.observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<Feed>(this, sbExceptionsHandler) {
                    @Override
                    public void onNext(Feed feed) {
                        smartFeedView.refreshFeed(feed);
                    }
                });
        refreshCategories(true);
    }

    private void refreshCategories(boolean forceRefresh) {
        final RecyclerView categoriesGrid = (RecyclerView) findViewById(R.id.categories_grid);
        catalogClient.getCategories(MainActivity.this, forceRefresh /* !forceRefresh */)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<List<CatalogGroup>>(this, sbExceptionsHandler) {
                    @Override
                    public void onNext(List<CatalogGroup> categories) {
                        categoryPageRecyclerViewHelper.updateItems(categoriesGrid, categories);
                    }
                });
    }

    private void setBackground() {
        ImageView backgroundImageView = (ImageView) findViewById(R.id.backgroundImage);
        ThemeSettings themeSettings = themeSettingsService.getThemeSettings();
        if (themeSettings != null) {
            String backgroundImageId = themeSettings.getBackgroundImageId();
            if (backgroundImageId != null && !backgroundImageId.isEmpty()) {
                imageService.loadImage(backgroundImageId, backgroundImageView, null/*width*/, null/*height*/);
            }
        }
    }

    @Override
    public void onBackPressed() {
        final boolean handled = this.bottomSheetCallback.handleHardwareBackButton();
        if (!handled) {
            this.moveTaskToBack(true);
//            super.onBackPressed();
        }
    }

    @Override
    public void stopRefreshing() {
        pullToRefresh.setRefreshing(false);
    }


    public void setRefreshable(boolean isRefreshable) {
        pullToRefresh.setEnabled(isRefreshable);
    }

    public void setRefreshable() {
        pullToRefresh.setEnabled(pullToRefreshRefreshable);
    }

    @Override
    public void show(int resId, int length) {
        int showSnackbarLength;
        switch (length) {
            case LENGTH_INDEFINITE:
                showSnackbarLength = Snackbar.LENGTH_INDEFINITE;
                break;
            case LENGTH_LONG:
                showSnackbarLength = Snackbar.LENGTH_LONG;
                break;
            case LENGTH_SHORT:
            default:
                showSnackbarLength = Snackbar.LENGTH_SHORT;
        }
        showSnackbar(resId, showSnackbarLength);
    }

    public static Intent createIntentForLogout(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        // return to main activity instance, do not create a new one.
        // in addition kill all activities on top of the main activity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(DO_LOGOUT, true);
        return intent;
    }

    public static Intent createIntentToExistingInstance(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        // return to main activity instance, do not create a new one.
        // in addition kill all activities on top of the main activity
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        return intent;
    }
}
