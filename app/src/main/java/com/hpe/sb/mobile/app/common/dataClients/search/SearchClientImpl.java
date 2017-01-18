package com.hpe.sb.mobile.app.common.dataClients.search;

import android.content.Context;
import android.util.Log;
import com.hpe.sb.mobile.app.serverModel.request.SearchRequestBody;
import com.hpe.sb.mobile.app.serverModel.search.SearchResultsWrapper;
import com.hpe.sb.mobile.app.infra.restclient.RestService;
import rx.Observable;

/**
 * Created by gabaysh on 03/05/2016.
 */
public class SearchClientImpl implements SearchClient {

    private final RestService restService;

    public SearchClientImpl(RestService restService) {
        this.restService = restService;
    }

    private String getPathPrefix() {
        return "search/";
    }

    @Override
    public Observable<SearchResultsWrapper> search(Context context, SearchRequestBody searchRequestBody) {
        Log.d(SearchClient.SEARCH_TAG, "Creating rest request with " + searchRequestBody.getDescription());
        return restService.createPostRequest(getPathPrefix(), searchRequestBody, SearchResultsWrapper.class, context);
    }
}