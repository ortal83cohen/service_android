package com.hpe.sb.mobile.app.features.request;

import android.content.Context;
import com.hpe.sb.mobile.app.common.dataClients.user.dbClient.UserItemsDbClient;
import com.hpe.sb.mobile.app.features.request.restClient.RequestRestClient;
import com.hpe.sb.mobile.app.infra.restclient.BaseRestClient;
import com.hpe.sb.mobile.app.infra.restclient.HttpLookupUtils;
import com.hpe.sb.mobile.app.infra.restclient.RestClientQueue;
import com.hpe.sb.mobile.app.serverModel.EntityCreationResult;
import com.hpe.sb.mobile.app.serverModel.request.FullRequest;
import com.hpe.sb.mobile.app.serverModel.request.RequestForForm;
import com.hpe.sb.mobile.app.serverModel.request.RequestForTrackingPage;
import com.hpe.sb.mobile.app.serverModel.request.SearchRequestBody;
import com.hpe.sb.mobile.app.serverModel.search.SearchResultsWrapper;
import com.hpe.sb.mobile.app.serverModel.user.useritems.UserItems;
import rx.Observable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by malikdav on 14/03/2016.
 *
 */
public class RequestClientImpl extends BaseRestClient implements RequestClient {

    private RequestRestClient requestRestClient;
    private UserItemsDbClient userItemsDbClient;

    public RequestClientImpl(RestClientQueue restClientQueue, HttpLookupUtils httpLookupUtils,
                             RequestRestClient requestRestClient, UserItemsDbClient userItemsDbClient) {
        super(restClientQueue, httpLookupUtils);
        this.userItemsDbClient = userItemsDbClient;
        this.requestRestClient = requestRestClient;
    }

    @Override
    public String getClientPrefix() {
        return "request/";
    }

	@Override
    public Observable<SearchResultsWrapper> getRelatedEntities(Context context, SearchRequestBody searchRequestBody) {
        return requestRestClient.getRelatedEntities(context, searchRequestBody);
    }


    @Override
    public Observable<RequestForTrackingPage> getRequestForTrackingPage(Context context,
                                                                     String requestId) {
        return requestRestClient.getRequestForTrackingPage(context, requestId);
    }

    @Override
    public Observable<RequestForForm> getNewRequestForm(Context context,
                                                     String offeringId) {
        return requestRestClient.getNewRequestForm(context, offeringId);
    }

    @Override
    public Observable<EntityCreationResult> createRequest(Context context, FullRequest fullRequest) {
													   

        return requestRestClient.createRequest(context, fullRequest);
    }

    @Override
    public Observable<UserItems> acceptRequest(Context context, final String requestId) {
        return requestRestClient.acceptRequest(context, requestId)
                .flatMap(new Func1<Void, Observable<UserItems>>() {
                    @Override
                    public Observable<UserItems> call(Void aVoid) {
                        return userItemsDbClient.removeUserItemFromDb(requestId);
                    }
                }).subscribeOn(Schedulers.io());
    }

    @Override
    public Observable<UserItems> rejectRequest(Context context, final String requestId) {
        return requestRestClient.rejectRequest(context, requestId)
                .flatMap(new Func1<Void, Observable<UserItems>>() {
                    @Override
                    public Observable<UserItems> call(Void aVoid) {
                        return userItemsDbClient.removeUserItemFromDb(requestId);
                    }
                }).subscribeOn(Schedulers.io());
    }

}
