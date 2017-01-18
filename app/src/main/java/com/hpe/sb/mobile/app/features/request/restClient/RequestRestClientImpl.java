package com.hpe.sb.mobile.app.features.request.restClient;

import android.content.Context;
import android.util.Log;
import com.hpe.sb.mobile.app.common.utils.LogTagConstants;
import com.hpe.sb.mobile.app.infra.restclient.BaseRestClient;
import com.hpe.sb.mobile.app.infra.restclient.HttpLookupUtils;
import com.hpe.sb.mobile.app.infra.restclient.RestClientQueue;
import com.hpe.sb.mobile.app.infra.restclient.RestService;
import com.hpe.sb.mobile.app.infra.restclient.customrequest.RequestFactory;
import com.hpe.sb.mobile.app.serverModel.EntityCreationResult;
import com.hpe.sb.mobile.app.serverModel.request.FullRequest;
import com.hpe.sb.mobile.app.serverModel.request.RequestForForm;
import com.hpe.sb.mobile.app.serverModel.request.RequestForTrackingPage;
import com.hpe.sb.mobile.app.serverModel.request.SearchRequestBody;
import com.hpe.sb.mobile.app.serverModel.search.SearchResultsWrapper;
import rx.Observable;
import rx.functions.Action1;

/**
 * Created by salemo on 04/05/2016.
 *
 */
public class RequestRestClientImpl extends BaseRestClient implements RequestRestClient {

    private final RestService restService;
    private final RequestFactory requestFactory;

    public RequestRestClientImpl(RestClientQueue restClientQueue, RequestFactory requestFactory, HttpLookupUtils httpLookupUtils, RestService restService) {
        super(restClientQueue, httpLookupUtils);
        this.requestFactory = requestFactory;
        this.restService = restService;
    }

    @Override
    public String getClientPrefix() {
        return "request/";
    }


    @Override
    public Observable<RequestForTrackingPage> getRequestForTrackingPage(Context context, final String requestId) {
        return restService.createGetRequest(getClientPrefix() + requestId, RequestForTrackingPage.class, context).doOnError(new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e(LogTagConstants.REQUEST_CLIENT, "unable to get request with id:" + requestId, throwable);
            }
        }).cache();
    }

    @Override
    public Observable<SearchResultsWrapper> getRelatedEntities(Context context, SearchRequestBody searchRequestBody) {
        return restService.createPostRequest(getClientPrefix() + "relatedEntities",
                searchRequestBody,
                SearchResultsWrapper.class,
                context);
    }

    @Override
    public Observable<RequestForForm> getNewRequestForm(Context context,
                                                        String offeringId) {
        return restService.createGetRequest(getClientPrefix() + "newRequestForm/" + offeringId,
                RequestForForm.class, context);
    }

    @Override
    public Observable<EntityCreationResult> createRequest(Context context, FullRequest fullRequest) {
        return restService.createPostRequest(getClientPrefix() + "createRequest", fullRequest, EntityCreationResult.class, context);
    }

    @Override
    public Observable<Void> acceptRequest(Context context, final String requestId) {
        return restService.createPutRequest(getClientPrefix() + "acceptSolution/" + requestId, null, Void.class, context).doOnError(new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e(LogTagConstants.REQUEST_CLIENT, "unable to accept request with id:" + requestId, throwable);
            }
        }).cache();
    }

    @Override
    public Observable<Void> rejectRequest(Context context, final String requestId) {
        return restService.createPutRequest(getClientPrefix() + "rejectSolution/" + requestId, null, Void.class, context).doOnError(new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                Log.e(LogTagConstants.REQUEST_CLIENT, "unable to reject request with id:" + requestId, throwable);
            }
        }).cache();
    }
}
