package com.hpe.sb.mobile.app.features.request.helper;

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.view.View;
import android.view.ViewGroup;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.common.dataClients.user.UserClient;
import com.hpe.sb.mobile.app.common.uiComponents.metricFont.MetricFontCustomButton;
import com.hpe.sb.mobile.app.common.uiComponents.metricFont.MetricFontCustomTextView;
import com.hpe.sb.mobile.app.features.detailsActivity.DetailsActivity;
import com.hpe.sb.mobile.app.features.home.MainActivity;
import com.hpe.sb.mobile.app.features.request.RequestClient;
import com.hpe.sb.mobile.app.features.request.animation.SubmitRequestAnimationUtil;
import com.hpe.sb.mobile.app.infra.baseActivities.BaseActivity;
import com.hpe.sb.mobile.app.infra.rx.BaseSubscriber;
import com.hpe.sb.mobile.app.serverModel.request.FullRequest;
import com.hpe.sb.mobile.app.serverModel.request.RequestForTrackingPage;
import com.hpe.sb.mobile.app.serverModel.request.RequestType;
import com.hpe.sb.mobile.app.serverModel.search.EntityType;
import com.hpe.sb.mobile.app.serverModel.user.useritems.RequestActiveUserItem;
import com.hpe.sb.mobile.app.serverModel.user.useritems.UserItems;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by malikdav on 08/05/2016.
 *
 */
public class RequestSubmittedPageHelper {

    public static final int FROM_SUBMIT_REQUEST_PAGE = 321;

    public static void startPage(BaseActivity activity, String requestId, RequestClient requestClient, UserClient userClient) {
        ViewGroup requestSubmitted = (ViewGroup) activity.findViewById(R.id.request_submitted);
        MetricFontCustomTextView requestNumberId = (MetricFontCustomTextView) activity.findViewById(R.id.request_number_id);
        MetricFontCustomButton trackingButton = (MetricFontCustomButton) activity.findViewById(R.id.new_request_tracking_button);
        MetricFontCustomButton homeButton = (MetricFontCustomButton) activity.findViewById(R.id.new_request_home_button);

        requestSubmitted.setVisibility(View.VISIBLE);

        initRequestIdText(activity, requestId, requestNumberId);
        compareButtonsWidth(trackingButton, homeButton);

        initHomeButton(activity, homeButton);
        initTrackRequestButton(activity, trackingButton, requestId, requestClient);
        Animator submitRequestAnimation = SubmitRequestAnimationUtil.createRequestAnimation(activity);
        submitRequestAnimation.start();

        // Asynchronous update user items to have the newly created request
        updateCardsPersistantData(activity, userClient);
    }

    private static void updateCardsPersistantData(final BaseActivity activity, UserClient userClient) {
        Observable<UserItems> userItems = userClient.getUserItems(activity, true /* !forceRefresh */);
        userItems.subscribe(new BaseSubscriber<UserItems>(activity, activity.getSbExceptionsHandler()) {
            @Override
            public void onNext(UserItems userItems) {

            }
        });
    }

    private static void initHomeButton(final Activity activity, final MetricFontCustomButton homeButton) {
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                homeButton.setEnabled(false);
                Intent intent = MainActivity.createIntentToExistingInstance(activity);
                activity.startActivity(intent);
            }
        });

    }

    private static void initTrackRequestButton(final BaseActivity activity, final MetricFontCustomButton trackRequestButton, final String requestId, final RequestClient requestClient) {
        trackRequestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                trackRequestButton.setEnabled(false);
                requestClient.getRequestForTrackingPage(activity, requestId).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new BaseSubscriber<RequestForTrackingPage>(activity, activity.getSbExceptionsHandler()) {
                            @Override
                            public void onNext(RequestForTrackingPage requestForTrackingPage) {
                                FullRequest request = requestForTrackingPage.getRequestForForm().getRequest();
                                RequestActiveUserItem requestActiveItem = new RequestActiveUserItem(requestForTrackingPage.getId(),
                                        requestForTrackingPage.getTitle(),
                                        convertEntityTypeToRequestType(request.getOfferingType()),
                                        requestForTrackingPage.getPhase(),
                                        requestForTrackingPage.getMetaPhase(),
                                        requestForTrackingPage.getRequestedForName(),
                                        requestForTrackingPage.getDescription(),
                                        requestForTrackingPage.getCreationTime());
                                Intent intent = DetailsActivity.createIntent(activity, requestActiveItem, false, false, false);
                                activity.startActivityForResult(intent, FROM_SUBMIT_REQUEST_PAGE);
                            }
                        });

            }
        });

    }

    public static RequestType convertEntityTypeToRequestType(EntityType entityType) {
        if (entityType == EntityType.SERVICE_REQUEST || entityType == EntityType.SERVICE_OFFERING) {
            return RequestType.SERVICE;
        }

        if (entityType == EntityType.SUPPORT_REQUEST || entityType == EntityType.SUPPORT_OFFERING) {
            return RequestType.SUPPORT;
        }

        if (entityType == EntityType.HR_REQUEST || entityType == EntityType.HUMAN_RESOURCE_OFFERING) {
            return RequestType.HR;
        }

        if (entityType == EntityType.CART_REQUEST) {
            return RequestType.CART;
        }

        return null;
    }

    private static void compareButtonsWidth(MetricFontCustomButton trackingButton, MetricFontCustomButton homeButton) {
        int trackingButtonWidth = trackingButton.getWidth();
        int homeButtonWidth = homeButton.getWidth();
        if(homeButtonWidth == trackingButtonWidth) {
            return;
        }

        if(trackingButtonWidth > homeButtonWidth) {
            ViewGroup.LayoutParams params = homeButton.getLayoutParams();
            params.width = trackingButtonWidth;
            homeButton.setLayoutParams(params);
        } else {
            ViewGroup.LayoutParams params = trackingButton.getLayoutParams();
            params.width = homeButtonWidth;
            trackingButton.setLayoutParams(params);
        }
    }

    private static void initRequestIdText(Activity activity, String requestId, MetricFontCustomTextView requestNumberId) {
        Resources res = activity.getResources();
        String text = String.format(res.getString(R.string.created_request_id), requestId);
        requestNumberId.setText(text);
    }
}
