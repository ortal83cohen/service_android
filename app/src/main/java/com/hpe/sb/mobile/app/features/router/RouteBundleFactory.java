package com.hpe.sb.mobile.app.features.router;

import android.os.Bundle;

import com.hpe.sb.mobile.app.serverModel.user.useritems.UserItem;

public class RouteBundleFactory {

    public static final String MG_ITEM_EXTRA = "Item";
    public static final String ROUTE_CODE_EXTRA = "RouteCode";

    public Bundle createDefaultRouteBundle() {
        Bundle bundle = new Bundle();
        bundle.putInt(ROUTE_CODE_EXTRA, Routes.DEFAULT);
        return bundle;
    }

    public Bundle createRequestRouteBundle(UserItem userItem) {
        Bundle bundle = new Bundle();
        bundle.putInt(ROUTE_CODE_EXTRA, Routes.REQUEST);
        bundle.putParcelable(MG_ITEM_EXTRA, userItem);
        return bundle;
    }

    public Bundle createCommentOnRequestRouteBundle(UserItem userItem) {
        Bundle bundle = new Bundle();
        bundle.putInt(ROUTE_CODE_EXTRA, Routes.COMMENT_ON_REQUEST);
        bundle.putParcelable(MG_ITEM_EXTRA, userItem);
        return bundle;
    }

    public Bundle createRequestWithDenyDialogRouteBundle(UserItem userItem) {
        Bundle bundle = new Bundle();
        bundle.putInt(ROUTE_CODE_EXTRA, Routes.REQUEST_WITH_DENY_DIALOG);
        bundle.putParcelable(MG_ITEM_EXTRA, userItem);
        return bundle;
    }
}
