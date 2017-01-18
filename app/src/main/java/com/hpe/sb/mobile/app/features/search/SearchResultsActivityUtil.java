package com.hpe.sb.mobile.app.features.search;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.common.dataClients.search.SearchClient;
import com.hpe.sb.mobile.app.common.uiComponents.relatedEntities.RelatedEntitiesRecyclerViewHelper;
import com.hpe.sb.mobile.app.common.uiComponents.relatedEntities.RelatedEntitySelectionHandler;
import com.hpe.sb.mobile.app.infra.baseActivities.BaseActivity;
import com.hpe.sb.mobile.app.infra.rx.BaseSubscriber;
import com.hpe.sb.mobile.app.serverModel.request.SearchRequestBody;
import com.hpe.sb.mobile.app.serverModel.search.EntityType;
import com.hpe.sb.mobile.app.serverModel.search.SearchResultsWrapper;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import java.util.Set;

/**
 * Created by salemo on 29/05/2016.
 *
 */
public class SearchResultsActivityUtil {

    public static void sendSearchQuery(final BaseActivity activity, SearchClient searchClient, final String query, Set<EntityType> relatedEntitiesTypes) {
        if (!query.isEmpty()){
            SearchRequestBody searchRequestBody = new SearchRequestBody(query, relatedEntitiesTypes,20);
            searchClient.search(activity, searchRequestBody).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseSubscriber<SearchResultsWrapper>(activity, activity.getSbExceptionsHandler()) {
                @Override
                public void onNext(SearchResultsWrapper searchResultsWrapper) {
                    SearchResultsActivityUtil.stopLoadingGif(activity.findViewById(R.id.loading_gif));
                    if (searchResultsWrapper != null && searchResultsWrapper.getResults() != null && searchResultsWrapper.getResults().size() > 0){
                        SearchResultsActivityUtil.showResultsList(activity, searchResultsWrapper);
                    } else {
                        SearchResultsActivityUtil.showNoResultsIcon(activity);
                    }
                }

                @Override
                public void onError(Throwable e) {
                    String msg = "Failed to get search results, error: " + e.getMessage();
                    Log.e(getClass().getName(), msg);
                    SearchResultsActivityUtil.stopLoadingGif(activity.findViewById(R.id.loading_gif));
                    super.onError(e);
                }
            });

        }

    }

    public static void initLoadingGif(Activity activity) {
        final View loadingGif = activity.findViewById(R.id.loading_gif);
        if (loadingGif != null) {
            displayView(loadingGif, activity.findViewById(R.id.no_search), activity.findViewById(R.id.search_results_list), activity.findViewById(R.id.search_no_results));
            loadingGif.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    public static void showNoSearchIcon(Activity activity, EditText searchBox) {
        View noSearchIcon = activity.findViewById(R.id.no_search);
        displayView(noSearchIcon, activity.findViewById(R.id.search_results_list), activity.findViewById(R.id.search_no_results));
    }

    private static void displayView(View viewToDisplay, View... viewsToRemove) {
        if (viewsToRemove != null) {
            for (View view: viewsToRemove) {
                removeView(view);
            }
        }
        if (viewToDisplay != null) {
            viewToDisplay.setVisibility(View.VISIBLE);
        }
    }

    private static void showResultsList(Activity activity, SearchResultsWrapper searchResultsWrapper) {
        View searchResultsList = activity.findViewById(R.id.search_results_list);
        displayView(searchResultsList, activity.findViewById(R.id.search_no_results), activity.findViewById(R.id.no_search));
        initResultsList(searchResultsWrapper, activity);
    }

    private static void showNoResultsIcon(Activity activity) {
        View noResultsIcon = activity.findViewById(R.id.search_no_results);
        displayView(noResultsIcon, activity.findViewById(R.id.search_results_list), activity.findViewById(R.id.no_search));
    }

    private static void initResultsList(SearchResultsWrapper searchResultsWrapper, Activity activity) {
        if (searchResultsWrapper != null) {
            RecyclerView searchResultsListView = (RecyclerView) activity.findViewById(R.id.search_results_list);
            final RelatedEntitiesRecyclerViewHelper helper = new RelatedEntitiesRecyclerViewHelper(activity);
            helper.initRecyclerView(searchResultsListView, false, null, new RelatedEntitySelectionHandler(activity));
            helper.updateItems(searchResultsListView, searchResultsWrapper.getResults());
        }
    }

    private static void stopLoadingGif(View loadingGif) {
        removeView(loadingGif);
    }

    private static void removeView(View view) {
        if (view != null) {
            view.setVisibility(View.GONE);
        }
    }
}
