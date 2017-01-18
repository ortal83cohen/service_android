package com.hpe.sb.mobile.app.features.request;

import android.content.Context;
import com.hpe.sb.mobile.app.serverModel.EntityCreationResult;
import com.hpe.sb.mobile.app.serverModel.request.FullRequest;
import com.hpe.sb.mobile.app.serverModel.request.RequestForForm;
import com.hpe.sb.mobile.app.serverModel.request.RequestForTrackingPage;
import com.hpe.sb.mobile.app.serverModel.request.SearchRequestBody;
import com.hpe.sb.mobile.app.serverModel.search.SearchResultsWrapper;
import com.hpe.sb.mobile.app.serverModel.user.useritems.UserItems;
import rx.Observable;

/**
 * Created by malikdav on 14/03/2016.
 *
 */
public interface RequestClient {

    Observable<RequestForTrackingPage> getRequestForTrackingPage(Context context, String requestId);


    Observable<SearchResultsWrapper> getRelatedEntities(Context context, SearchRequestBody relatedEntitiesRequestBody);


    Observable<RequestForForm> getNewRequestForm(Context context,
                                                 String offeringId);


    Observable<EntityCreationResult> createRequest(Context context, FullRequest fullRequest);

    Observable<UserItems> acceptRequest(Context context, String requestId);

    Observable<UserItems> rejectRequest(Context context, String requestId);
}
