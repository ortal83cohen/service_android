package com.hpe.sb.mobile.app.common.dataClients.search;

import android.content.Context;
import com.hpe.sb.mobile.app.serverModel.request.SearchRequestBody;
import com.hpe.sb.mobile.app.serverModel.search.SearchResultsWrapper;
import rx.Observable;


/**
 * Created by gabaysh on 03/05/2016.
 */
public interface SearchClient {

    public static final String SEARCH_TAG = "search";

    Observable<SearchResultsWrapper> search(Context context, SearchRequestBody searchRequestBody);
}
