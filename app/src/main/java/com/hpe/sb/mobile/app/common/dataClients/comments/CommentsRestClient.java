package com.hpe.sb.mobile.app.common.dataClients.comments;

import android.content.Context;
import rx.Observable;

/**
 * Created by mufler on 05/05/2016.
 */
public interface CommentsRestClient {
    Observable<Void> addComment(Context context,
                                String entityType, String entityId, String commentBody
    );
}
