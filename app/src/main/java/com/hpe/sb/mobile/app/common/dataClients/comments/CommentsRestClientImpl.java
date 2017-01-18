package com.hpe.sb.mobile.app.common.dataClients.comments;

import android.content.Context;
import com.hpe.sb.mobile.app.infra.restclient.RestService;
import rx.Observable;

/**
 * Created by mufler on 05/05/2016.
 */
public class CommentsRestClientImpl  implements CommentsRestClient {

    private final RestService restService;
    private String relativePath = "/comments";

    public CommentsRestClientImpl(RestService restService){
        this.restService = restService;
    }

    @Override
    public Observable<Void> addComment(Context context,
                                       String entityType, String entityId, String commentBody) {
        String requestPath = "/addComment";
        String URL = relativePath + requestPath + "/" + entityType + "/" + entityId;
        final Observable<Void> postRequest = restService.createPostRequest(URL, commentBody, Void.class, context);
        return postRequest;
    }
}
