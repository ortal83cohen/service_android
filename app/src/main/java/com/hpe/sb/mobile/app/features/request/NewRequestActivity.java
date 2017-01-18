package com.hpe.sb.mobile.app.features.request;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.common.dataClients.catalog.CatalogClient;
import com.hpe.sb.mobile.app.common.dataClients.catalog.restClient.CatalogRestClient;
import com.hpe.sb.mobile.app.common.dataClients.entityDisplay.EntityBadgeService;
import com.hpe.sb.mobile.app.common.dataClients.search.SearchClient;
import com.hpe.sb.mobile.app.common.dataClients.tenantSettings.TenantSettingsService;
import com.hpe.sb.mobile.app.common.dataClients.user.UserClient;
import com.hpe.sb.mobile.app.common.uiComponents.dialogs.GeneralErrorDialogFragment;
import com.hpe.sb.mobile.app.common.uiComponents.metricFont.MetricFontCustomButton;
import com.hpe.sb.mobile.app.common.utils.LogTagConstants;
import com.hpe.sb.mobile.app.features.home.MainActivity;
import com.hpe.sb.mobile.app.features.request.dialog.UnsavedChangesDialog;
import com.hpe.sb.mobile.app.features.request.event.NewRequestFormEvent;
import com.hpe.sb.mobile.app.features.request.factory.ViewTypeFactory;
import com.hpe.sb.mobile.app.features.request.helper.NewRequestHeadersHelper;
import com.hpe.sb.mobile.app.features.request.helper.RequestSubmittedPageHelper;
import com.hpe.sb.mobile.app.features.request.recycleview.CurrentPositionHolder;
import com.hpe.sb.mobile.app.features.request.recycleview.NewRequestLayoutManager;
import com.hpe.sb.mobile.app.features.request.recycleview.NewRequestViewsAdapter;
import com.hpe.sb.mobile.app.features.request.recycleview.SnappyRecyclerView;
import com.hpe.sb.mobile.app.features.request.recycleview.type.AlmostThereViewType;
import com.hpe.sb.mobile.app.features.request.recycleview.type.FormSpecificFieldViewType;
import com.hpe.sb.mobile.app.features.request.recycleview.type.NewRequestViewType;
import com.hpe.sb.mobile.app.features.request.scroll.NewRequestScroller;
import com.hpe.sb.mobile.app.infra.baseActivities.BaseActivity;
import com.hpe.sb.mobile.app.infra.eventbus.EventBus;
import com.hpe.sb.mobile.app.infra.rx.BaseSubscriber;
import com.hpe.sb.mobile.app.serverModel.CompletionStatus;
import com.hpe.sb.mobile.app.serverModel.EntityCreationResult;
import com.hpe.sb.mobile.app.serverModel.TenantSettings.OfferingInRequestPolicy;
import com.hpe.sb.mobile.app.serverModel.catalog.CatalogGroup;
import com.hpe.sb.mobile.app.serverModel.catalog.Offering;
import com.hpe.sb.mobile.app.serverModel.request.RequestForForm;
import com.hpe.sb.mobile.app.serverModel.request.SearchRequestBody;
import com.hpe.sb.mobile.app.serverModel.search.EntityItem;
import com.hpe.sb.mobile.app.serverModel.search.EntityType;
import com.hpe.sb.mobile.app.serverModel.search.SearchResultsWrapper;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class NewRequestActivity extends BaseActivity implements FinishWithDirtyWarning, GeneralErrorDialogFragment.DialogListener {

    private static final String OFFERING_ID = "OFFERING_ID";
    private static final int RESULT_CODE_NEW_REQUEST_FROM_OFFERING = 365;
    public static final int MAX_RELATED_ENTITIES_TO_DISPLAY = 3;
    private final NewRequestFlowState newRequestFlowState;
    private FormFieldDisplayManager formFieldDisplayManager;
    private SoftKeyboardStateHolder softKeyboardStateHolder;
    private boolean requestSubmitted = false;


    private NewRequestScroller scroller;
    private static final int SEARCH_MAX_RESULT = 50;
    private Set<EntityType> relatedEntitiesTypes = new HashSet<EntityType>() {{
        add(EntityType.SUPPORT_OFFERING);
        add(EntityType.SERVICE_OFFERING);
        add(EntityType.HUMAN_RESOURCE_OFFERING);
    }};

    private View loadingGif;
    private CurrentPositionHolder currentPositionHolder;

    public NewRequestActivity() {
        newRequestFlowState = new NewRequestFlowState();
    }

    @Inject
    protected RequestClient requestClient;

    @Inject
    public UserClient userClient;

    @Inject
    protected SearchClient searchClient;

    @Inject
    public CatalogClient catalogClient;

    @Inject
    protected CatalogRestClient catalogRestClient;

    @Inject
    public TenantSettingsService tenantSettingsService;

    @Inject
    protected EventBus<NewRequestFormEvent> eventBus;

    @Inject
    public EntityBadgeService entityBadgeService;

    private NewRequestViewsAdapter newRequestViewsAdapter;
    private SnappyRecyclerView newRequestFlowItemsRecycleView;
    private List<NewRequestViewType> viewTypes = new ArrayList<>();
    private ViewTypeFactory viewTypeFactory;
    private NewRequestHelper newRequestHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((ServiceBrokerApplication) getApplicationContext()).getServiceBrokerComponent().inject(this);

        initDefaultOffering();

        viewTypeFactory = new ViewTypeFactory();
        setContentView(R.layout.activity_new_request);
        loadingGif = findViewById(R.id.loading_gif);
        hideRequestSubmittedScreen();
        NewRequestHeadersHelper.initHeaders(this);
        initNewRequestFlowItemRecycleView();
        subscribeEventBusListeners();
        softKeyboardStateHolder = new SoftKeyboardStateHolder();
        scroller = new NewRequestScroller(newRequestFlowItemsRecycleView, viewTypes, softKeyboardStateHolder, currentPositionHolder);
        newRequestHelper = new NewRequestHelper();

        Intent intent = getIntent();
        final Bundle intentExtras = intent.getExtras();
        if (intentExtras != null) {
            String offeringId = intentExtras.getString(OFFERING_ID);
            if (offeringId != null) {
                loadingGif.setVisibility(View.VISIBLE);
                eventBus.send(new NewRequestFormEvent(NewRequestFormEvent.CHOSE_RELATED_ENTITY, offeringId));
            }
        } else {
            // add first item
            if(OfferingInRequestPolicy.SKIP == tenantSettingsService.getTenantSettings().getOfferingInRequestPolicy()) {
                getDefaultOffering(new BaseSubscriber<Offering>(this, getSbExceptionsHandler()) {
                    @Override
                    public void onNext(Offering offering) {
                        if(offering != null) {
                            newRequestFlowState.setSelectedOffering(offering);
                        }
                        startDisplayingRequestForm();
                    }
                });
            } else {
                viewTypes.add(viewTypeFactory.describeNewRequestViewType());
            }
        }

        final View activityRootView = findViewById(R.id.activity_root);
        activityRootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = activityRootView.getRootView().getHeight() - activityRootView.getHeight();

                // if more than 100 pixels, its probably a keyboard...
                int threshold = activityRootView.getRootView().getHeight() / 4;
                boolean isKeyboardOpen = heightDiff > threshold;
                softKeyboardStateHolder.setIsKeyboardOpen(isKeyboardOpen);
            }
        });
    }

    @Override
    public void onActionModeFinished(ActionMode mode) {
        super.onActionModeFinished(mode);
    }

    /**
     * Alerting the user if he is about the quit and lose any unsave changes
     */
    @Override
    public void finishWithDirtyWarning() {
        if(isFormDisplayed()) {
            UnsavedChangesDialog unsavedChangesDialog = new UnsavedChangesDialog();
            unsavedChangesDialog.show(getFragmentManager(), NewRequestActivity.class.getName());
        } else {
            super.finish();
        }
    }

    @Override
    public void onBackPressed() {
        if(requestSubmitted){
            finish();
        }else if(scroller.isBackAvailable()) {
            scroller.scrollToPrevious();
        } else {
            finishWithDirtyWarning();
        }
    }

    private boolean isFormDisplayed() {
        NewRequestViewType firstViewType = null;
        if(viewTypes != null && !viewTypes.isEmpty()){
            firstViewType = viewTypes.get(0);
        }
        return firstViewType != null && firstViewType instanceof FormSpecificFieldViewType;
    }

    /**
     * Set the request submitted screen to be transparent,
     * It will only be shown when the request will be submitted
     * We use this approach for the fade in animation
     */
    private void hideRequestSubmittedScreen() {
        ViewGroup requestSubmitted = (ViewGroup) findViewById(R.id.request_submitted);
        requestSubmitted.setVisibility(View.INVISIBLE);
        requestSubmitted.setAlpha(0);
    }

    /**
     * Receiving speech input
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case RESULT_CODE_NEW_REQUEST_FROM_OFFERING:{
                if (resultCode == RESULT_OK) {
                    final String createdRequestId = data.getStringExtra(UnsupportedOfferingActivity.CREATED_REQUEST_ID);
                    if (createdRequestId != null) {
                        requestSubmitted =true;
                        RequestSubmittedPageHelper.startPage(NewRequestActivity.this, createdRequestId, requestClient, userClient);
                    } else {
                        enableNextButton();
                        sbExceptionsHandler.handleException(this, Thread.currentThread(), new RuntimeException("Unexpected error: got null as created request id"));
                    }
                } else if (resultCode == UnsupportedOfferingActivity.RESULT_ERROR) {
                    enableNextButton();
                    sbExceptionsHandler.handleException(this, Thread.currentThread(), new RuntimeException("Unexpected error: failed to create request in webview"));
                } else if (resultCode == RESULT_CANCELED) {
                    enableNextButton();
                }
            }break;
            case RequestSubmittedPageHelper.FROM_SUBMIT_REQUEST_PAGE: {
                Intent intent = MainActivity.createIntentToExistingInstance(this);
                startActivity(intent);
            }break;
            default: {
                Log.e(LogTagConstants.NEW_REQUEST, String.format("Return to %s with unknown result code: %d", this.getClass().getSimpleName(), requestCode));
            }
        }
    }

    private SearchRequestBody getRelatedEntitiesRequestBody(String requestDescription) {
        return new SearchRequestBody(requestDescription,
                relatedEntitiesTypes,
                SEARCH_MAX_RESULT);
    }

    private void subscribeEventBusListeners() {
        eventBus.toObserverable()
                .subscribe((new Action1<NewRequestFormEvent>() {
                    @Override
                    public void call(NewRequestFormEvent event) {

                        switch (event.getEventType()) {
                            case NewRequestFormEvent.DESCRIBED_YOUR_REQUEST: {
                                String requestDescription = event.getData();
                                searchForRelatedEntities(requestDescription,event.getData2());
                            }
                            break;
                            case NewRequestFormEvent.CHOSE_CATEGORY: {
                                String catalogGroupId = event.getData();
                                String catalogGroupTitle = event.getData2();
                                showEntitiesOfCategory(catalogGroupId, catalogGroupTitle);
                            }
                            break;
                            case NewRequestFormEvent.CHOSE_RELATED_ENTITY: {
                                String entityId = event.getData();
                                reviewSelectedRelatedEntity(entityId);
                            }
                            break;
                            case NewRequestFormEvent.REVIEWED_SELECTED_ENTITY: {
                                startDisplayingRequestForm();
                            }
                            break;
                            case NewRequestFormEvent.NEXT_FORM_FIELD: {
                                boolean hasNext = formFieldDisplayManager.hasNext();
                                if (hasNext) {
                                    addAndDisplayViewType(formFieldDisplayManager.nextFormField());
                                } else {
                                    View activityRoot = findViewById(R.id.activity_root);
                                    activityRoot.invalidate();
                                    activityRoot.requestLayout();
                                    if (!(viewTypes.get(viewTypes.size() - 1) instanceof AlmostThereViewType)) {
                                        viewTypes.add(viewTypeFactory.almostThereViewType());
                                        newRequestViewsAdapter.notifyItemInserted(viewTypes.size() - 1);
                                    }
                                    scroller.scrollToNext();
                                }
                            }
                            break;
                            case NewRequestFormEvent.SUBMIT_REQUEST: {
                                submitRequest();
                            }
                            break;
                        }
                    }

                    private void showEntitiesOfCategory(final String catalogGroupId, final String catalogGroupTitle) {
                        NewRequestHeadersHelper.showCategoryHeader(NewRequestActivity.this, getString(R.string.new_request_category_offerings_header, catalogGroupTitle));
                        addAndDisplayViewType(viewTypeFactory.chooseRelatedEntityInCategoryViewType(catalogGroupId));
                    }

                    private void submitRequest() {
                        requestClient.createRequest(NewRequestActivity.this, newRequestFlowState.getFullRequest()).observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new BaseSubscriber<EntityCreationResult>(NewRequestActivity.this, sbExceptionsHandler) {

                                    @Override
                                    public void onNext(EntityCreationResult entityCreationResult) {
                                        if (entityCreationResult.getCompletionStatus() == CompletionStatus.OK) {
                                            requestSubmitted = true;

                                            //TODO:Dudi- should i move this to a framgent or to a custom view(not resuable)
                                            RequestSubmittedPageHelper.startPage(NewRequestActivity.this, entityCreationResult.getEntityId(), requestClient, userClient);
                                        } else {
                                            //TODO:
                                            Log.e(LogTagConstants.NEW_REQUEST, "Failed to create request, return status code is: " + entityCreationResult.getCompletionStatus());
                                            showGeneralDialogError();
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        MetricFontCustomButton submitButton = (MetricFontCustomButton) findViewById(R.id.submit_button);
                                        if(submitButton != null){
                                            submitButton.setEnabled(true);
                                            submitButton.setText(R.string.submit_request);
                                        }
                                        View loadingGif = findViewById(R.id.loading_gif);
                                        if(loadingGif != null){
                                            loadingGif.setVisibility(View.INVISIBLE);
                                        }
                                        super.onError(e);
                                    }
                                });
                    }

                    private void searchForRelatedEntities(String requestDescription, final String data2) {
                        newRequestFlowState.setRequestDescriptionCandidate(requestDescription);
                        searchClient.search(NewRequestActivity.this, getRelatedEntitiesRequestBody(requestDescription))
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new BaseSubscriber<SearchResultsWrapper>(NewRequestActivity.this, sbExceptionsHandler) {
                                    @Override
                                    public void onNext(SearchResultsWrapper searchResultsWrapper) {
                                        List<EntityItem> searchResults = searchResultsWrapper.getResults();
                                        if (searchResults.isEmpty()) {
                                            notFoundRightOfferingInQuery();
                                        } else {
                                            showEntities(searchResults);
                                        }
                                        eventBus.send(
                                                new NewRequestFormEvent(NewRequestFormEvent.FINISH_LOADING,
                                                        data2));
                                    }
                                    @Override
                                    public void onError(Throwable e) {
                                        Log.e(LogTagConstants.NEW_REQUEST, "Failed to get related entities");
                                        enableNextButton();
                                        super.onError(e);
                                    }
                                });
                    }

                    private void reviewSelectedRelatedEntity(String entityId) {
                        //TODO: Add support for different entity type (i.e Article)
                        NewRequestHeadersHelper.hideCategoryHeader(NewRequestActivity.this);
                        catalogRestClient.getOffering(NewRequestActivity.this, entityId)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribeOn(Schedulers.io())
                                .subscribe(new BaseSubscriber<Offering>(NewRequestActivity.this, sbExceptionsHandler) {
                                    @Override
                                    public void onNext(Offering offering) {
                                        loadingGif.setVisibility(View.GONE);
                                        addAndDisplayViewType(viewTypeFactory.reviewChosenRelatedEntityViewType(offering, entityBadgeService));
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        loadingGif.setVisibility(View.GONE);
                                        enableNextButton();
                                        super.onError(e);
                                    }
                                });
                    }
                }));
    }

    private void notFoundRightOfferingInQuery() {
        if(OfferingInRequestPolicy.IGNORE == tenantSettingsService.getTenantSettings().getOfferingInRequestPolicy()) {
            showDefaultOffering();
        } else {
            showCategoriesToFindEntity();
        }
    }

    private void startDisplayingRequestForm() {
        final Offering selectedOffering = newRequestFlowState.getSelectedOffering();
        if(selectedOffering == null) {
            //  no offering was chose
            startDisplayingRequestFormOnMobile(null/*offeringId*/);
        } else if(selectedOffering.isSupportableOnMobile()) {
            startDisplayingRequestFormOnMobile(selectedOffering.getId());
        } else {
            //go to the web page
            final View newRequestHeader = findViewById(R.id.header);
            Pair<View, String> p1 = Pair.create(newRequestHeader, "new_request_header_shared");
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, p1);
            Intent intent = new Intent(NewRequestActivity.this, UnsupportedOfferingActivity.class);
            intent.putExtra(UnsupportedOfferingActivity.OFFERING_ID, selectedOffering.getId());
            intent.putExtra(UnsupportedOfferingActivity.REQUEST_DESCRIPTION, newRequestFlowState.getRequestDescriptionCandidate());
            startActivityForResult(intent, RESULT_CODE_NEW_REQUEST_FROM_OFFERING, options.toBundle());
        }
    }

    private void startDisplayingRequestFormOnMobile(String offeringId) {
        requestClient.getNewRequestForm(NewRequestActivity.this, offeringId).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new BaseSubscriber<RequestForForm>(this, getSbExceptionsHandler()) {
                    @Override
                    public void onNext(RequestForForm requestForForm) {
                        //TODO:Dudi- validate the form is valid(not empty)
                        NewRequestHeadersHelper.showOfferingHeader(NewRequestActivity.this, newRequestFlowState);

                        formFieldDisplayManager = new FormFieldDisplayManager(requestForForm.getForm(), viewTypes, viewTypeFactory, newRequestViewsAdapter, scroller);
                        newRequestFlowState.setFullRequest(requestForForm.getRequest());
                        if (formFieldDisplayManager.hasNext()) {
                            addAndDisplayViewType(formFieldDisplayManager.nextFormField());
                        }

                        //TODO: the scrolling animation still doest work
                        // We need to perform the removal of the none form fields after the form field has been added, so the scrolling animation will occur.
                        removeAllFieldsExceptLast();
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(LogTagConstants.NEW_REQUEST, "Failed to get New request form");
                        enableNextButton();
                        super.onError(e);
                    }
                });
    }

    private void enableNextButton() {
        final Button next = (Button) findViewById(R.id.next_button);
        if(next != null){
            next.setEnabled(true);
        }
    }

    private void addAndDisplayViewType(NewRequestViewType newRequestViewType) {
        int indexOf = newRequestHelper.indexOfViewType(newRequestViewType, viewTypes);
        if (indexOf != -1) {
            removeAllFieldsFromPosition(indexOf);
        }

        viewTypes.add(newRequestViewType);
        newRequestViewsAdapter.notifyItemInserted(viewTypes.size() - 1);
        scroller.scrollToLast();
    }

    private void removeAllFieldsFromPosition(int position) {
        int viewTypesToRemoveCount = viewTypes.size() - position;
        for (int i = 0; i < viewTypesToRemoveCount; i++) {
            viewTypes.remove(position);
        }

        newRequestViewsAdapter.notifyItemRangeRemoved(position, viewTypesToRemoveCount);
    }

    private void removeAllFieldsExceptLast() {
        int viewTypesToRemoveCount = viewTypes.size() - 1;
        for (int i = 0; i < viewTypesToRemoveCount; i++) {
            viewTypes.remove(0);
        }

        currentPositionHolder.updatePosition(0, viewTypes.size());
        newRequestViewsAdapter.notifyItemRangeRemoved(0, viewTypesToRemoveCount);
    }

    private void showGeneralDialogError() {
        runOnUiThread(new Runnable() {
            public void run() {
                new GeneralErrorDialogFragment().show(getSupportFragmentManager(), NewRequestActivity.class.getName());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_new_request, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void initNewRequestFlowItemRecycleView() {
        currentPositionHolder = new CurrentPositionHolder();
        newRequestFlowItemsRecycleView = (SnappyRecyclerView) findViewById(R.id.new_request_recycleview);
        viewTypes = new ArrayList<>();
        NewRequestLayoutManager layoutManager = new NewRequestLayoutManager(this, currentPositionHolder);
//        layoutManager = new SnappyLinearLayoutManager(this);
        layoutManager.setAutoMeasureEnabled(true);
        newRequestViewsAdapter = new NewRequestViewsAdapter(this, viewTypes, newRequestFlowState);
        newRequestViewsAdapter.setEventBus(eventBus);
        newRequestFlowItemsRecycleView.setHasFixedSize(false);
        newRequestFlowItemsRecycleView.setLayoutManager(layoutManager);
        newRequestFlowItemsRecycleView.setAdapter(newRequestViewsAdapter);
        newRequestFlowItemsRecycleView.setSnapEnabled(true);
        newRequestFlowItemsRecycleView.setCurrentPositionHolder(currentPositionHolder);

//        newRequestFlowItemsRecycleView.addOnScrollListener(new NewRequestScrollListener(this));
    }

    public static Intent createIntentForOffering(Context context, String entityId) {
        Intent intent = new Intent(context, NewRequestActivity.class);
        intent.putExtra(OFFERING_ID, entityId);
        return intent;
    }

    private void showCategoriesToFindEntity() {
        Observable<List<CatalogGroup>> categories = catalogClient.getCategories(NewRequestActivity.this, false /* !forceRefresh */);
        Observable<Offering> defaultOffering = catalogClient.getDefaultOffering(NewRequestActivity.this);
        Observable.zip(categories, defaultOffering, new Func2<List<CatalogGroup>, Offering, CategoriesAndDefaultOffering>() {
            @Override
            public CategoriesAndDefaultOffering call(List<CatalogGroup> categories, Offering defaultOffering) {
                return new CategoriesAndDefaultOffering(categories, defaultOffering);
            }
        })
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseSubscriber<CategoriesAndDefaultOffering>(NewRequestActivity.this, NewRequestActivity.this.sbExceptionsHandler) {
            @Override
            public void onNext(CategoriesAndDefaultOffering categoriesAndDefaultOffering) {
                boolean isOfferingMandatory = OfferingInRequestPolicy.MANDATORY == tenantSettingsService.getTenantSettings().getOfferingInRequestPolicy();
                boolean isShowStillNotFoundMessage = true;
                if(isOfferingMandatory && categoriesAndDefaultOffering.defaultOffering == null) {
                    isShowStillNotFoundMessage = false;
                }
                addAndDisplayViewType(viewTypeFactory.chooseCategoriesViewType(
                        categoriesAndDefaultOffering.categories, isShowStillNotFoundMessage));
            }
        })
        ;
    }

    private void showEntities(List<EntityItem> searchResults) {
        List<EntityItem> searchResultToDisplay = trimSearchResults(searchResults);
        addAndDisplayViewType(viewTypeFactory.chooseRelatedEntitiesViewType(searchResultToDisplay));
    }

    private List<EntityItem> trimSearchResults(List<EntityItem> searchResults) {
        int numberOfResultToDisplay = Math.min(MAX_RELATED_ENTITIES_TO_DISPLAY, searchResults.size());
        return searchResults.subList(0, numberOfResultToDisplay);
    }

    public void onCantFindEntityInSearchResult(View view) {
        NewRequestHeadersHelper.hideCategoryHeader(NewRequestActivity.this);
        notFoundRightOfferingInQuery();
    }

    public void onCantFindEntityInCategories(View view) {
        NewRequestHeadersHelper.hideCategoryHeader(NewRequestActivity.this);
        showDefaultOffering();
    }

    private void showDefaultOffering() {
        //TODO: Add support for different entity type (i.e Article)
        NewRequestHeadersHelper.hideCategoryHeader(NewRequestActivity.this);
        getDefaultOffering(
                new BaseSubscriber<Offering>(this, getSbExceptionsHandler()) {
                    @Override
                    public void onNext(Offering offering) {
                        if(offering != null) {
                            addAndDisplayViewType(viewTypeFactory.reviewChosenRelatedEntityViewType(offering, entityBadgeService));
                        } else {
                            startDisplayingRequestForm();
                        }
                    }
                });
    }

    private void initDefaultOffering() {
        // just to call 'getDefaultOffering'. The result will be cached in catalogClient
        getDefaultOffering(new BaseSubscriber<Offering>(this, getSbExceptionsHandler()){}) ;
    }

    private void getDefaultOffering(final BaseSubscriber<Offering> baseSubscriber) {
        //TODO: Add support for different entity type (i.e Article)
        catalogClient.getDefaultOffering(NewRequestActivity.this)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(baseSubscriber);
    }

    class CategoriesAndDefaultOffering {
        List<CatalogGroup> categories;
        Offering defaultOffering;

        public CategoriesAndDefaultOffering(List<CatalogGroup> categories, Offering defaultOffering) {
            this.categories = categories;
            this.defaultOffering = defaultOffering;
        }
    }


    @Override
    public void onDialogPositiveClick(DialogFragment dialog) {
        Intent intent = new Intent(dialog.getActivity(), MainActivity.class);
        startActivity (intent);
        dialog.dismiss();
    }


    @Override
    public void onDialogNegativeClick(DialogFragment dialog) {
        Intent intent = new Intent(dialog.getActivity(), MainActivity.class);
        startActivity (intent);
        dialog.dismiss();
    }
}
