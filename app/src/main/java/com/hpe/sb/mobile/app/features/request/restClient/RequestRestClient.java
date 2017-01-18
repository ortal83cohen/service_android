package com.hpe.sb.mobile.app.features.request.restClient;

import android.content.Context;
import com.hpe.sb.mobile.app.serverModel.EntityCreationResult;
import com.hpe.sb.mobile.app.serverModel.request.FullRequest;
import com.hpe.sb.mobile.app.serverModel.request.RequestForForm;
import com.hpe.sb.mobile.app.serverModel.request.RequestForTrackingPage;
import com.hpe.sb.mobile.app.serverModel.request.SearchRequestBody;
import com.hpe.sb.mobile.app.serverModel.search.SearchResultsWrapper;
import rx.Observable;

/**
 * Created by salemo on 04/05/2016.
 *
 */
public interface RequestRestClient {
    Observable<RequestForTrackingPage> getRequestForTrackingPage(Context context, String requestId);

    Observable<SearchResultsWrapper> getRelatedEntities(Context context, SearchRequestBody searchRequestBody);

    Observable<RequestForForm> getNewRequestForm(Context context,
                                                 String offeringId);


    Observable<EntityCreationResult> createRequest(Context context, FullRequest fullRequest);

    Observable<Void> acceptRequest(Context context, String requestId);

    Observable<Void> rejectRequest(Context context, String requestId);
}
