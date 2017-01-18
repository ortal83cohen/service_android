package com.hpe.sb.mobile.app.features.request.recycleview.viewholder;

import android.content.res.Resources;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.common.uiComponents.commonLayout.EntityDescriptionView;
import com.hpe.sb.mobile.app.common.uiComponents.entityBadge.EntityBadgeView;
import com.hpe.sb.mobile.app.common.uiComponents.entityIcon.EntityIconImageView;
import com.hpe.sb.mobile.app.common.uiComponents.metricFont.MetricFontCustomTextView;
import com.hpe.sb.mobile.app.features.login.services.ConnectionContextService;
import com.hpe.sb.mobile.app.features.request.NewRequestFlowState;
import com.hpe.sb.mobile.app.features.request.event.NewRequestFormEvent;
import com.hpe.sb.mobile.app.features.request.recycleview.type.ReviewChosenRelatedEntityViewType;
import com.hpe.sb.mobile.app.infra.baseActivities.BaseActivity;
import com.hpe.sb.mobile.app.infra.eventbus.EventBus;
import com.hpe.sb.mobile.app.serverModel.catalog.Offering;

import javax.inject.Inject;

/**
 * Created by malikdav on 26/04/2016.
 *
 */
public class ReviewChosenRelatedEntityViewHolder
        extends NewRequestViewHolder<ReviewChosenRelatedEntityViewType> {

    @Inject
    public ConnectionContextService connectionContextService;

    private final EntityIconImageView entityIcon;

    private final MetricFontCustomTextView title;

    private final ImageView popularIcon;

    private final EntityBadgeView badge;

    private final EntityDescriptionView offeringDescription;

    private final Button nextButton;

    /**
     * the layout that limit the size of the scroll and decides if there is scroll or not
     */
    private final LinearLayout nestedScrollParent;

    public ReviewChosenRelatedEntityViewHolder(View itemView, EventBus<NewRequestFormEvent> eventBus) {
        super(itemView, eventBus);
        ((ServiceBrokerApplication)itemView.getContext().getApplicationContext()).getServiceBrokerComponent().inject(this);

        entityIcon = (EntityIconImageView) itemView.findViewById(R.id.entity_icon);
        title = (MetricFontCustomTextView) itemView.findViewById(R.id.entity_title);
        popularIcon = (ImageView) itemView.findViewById(R.id.popular_icon);
        badge = (EntityBadgeView) itemView.findViewById(R.id.badge);
        offeringDescription = (EntityDescriptionView) itemView.findViewById(R.id.entity_description);
        nextButton = (Button) itemView.findViewById(R.id.next_button);
        nestedScrollParent = (LinearLayout) itemView.findViewById(R.id.nested_scroll_parent);
    }

    @Override
    public void bind(ReviewChosenRelatedEntityViewType viewType, BaseActivity activity, NewRequestFlowState flowState) {
        Offering offering = viewType.getOffering();

        entityIcon.setImage(offering.getImageId(), offering.getOfferingType());
        title.setText(offering.getTitle());
        badge.setBadge(viewType.getBadgeProperties());
        if (offering.isPopular()) {
            popularIcon.setVisibility(View.VISIBLE);
        } else {
            popularIcon.setVisibility(View.GONE);
        }
        offeringDescription.setDescription(offering.getDescription(), connectionContextService);

        if (nestedScrollParent != null) {
            final LinearLayout nextButtonContentView = (LinearLayout) itemView.findViewById(R.id.next_button_parent);
            nextButtonContentView.post(new Runnable() {
                @Override
                public void run() {
                    final LinearLayout scrollContentView = (LinearLayout) itemView.findViewById(R.id.scroll_content_view);
                    int scrollContentViewHeight = scrollContentView.getHeight();

                    int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;
                    int headerHeight = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.new_request_header);
                    int marginsHeight = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.new_request_chosen_related_entity_margin_sum);
                    int statusBarHeight = itemView.getContext().getResources().getDimensionPixelSize(R.dimen.status_bar_height);
                    int nextButtonHeight = nextButtonContentView.getHeight();
                    int validScrollHeight = screenHeight - headerHeight - statusBarHeight - nextButtonHeight - marginsHeight;

                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) nestedScrollParent.getLayoutParams();
                    if (scrollContentViewHeight > validScrollHeight) {
                        params.height = validScrollHeight;
                    } else {
                        itemView.findViewById(R.id.next_button_divider).setVisibility(View.GONE);
                    }
                    nestedScrollParent.setLayoutParams(params);
                }
            });
        }

        initNextButton(offering, flowState, activity);
    }

    private void initNextButton(final Offering offering, final NewRequestFlowState newRequestFlowState, final BaseActivity activity) {
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // update new request state
                newRequestFlowState.setSelectedOffering(offering);

                //check connectivity
                final boolean networkConnectionAvailable = activity.isNetworkConnectionAvailable();
                if(!networkConnectionAvailable){
                    nextButton.setEnabled(true);
                    activity.showNoConnectionSnackbar();
                    return;
                }

                nextButton.setEnabled(false);
                eventBus.send(new NewRequestFormEvent(NewRequestFormEvent.REVIEWED_SELECTED_ENTITY, offering.getId()));
            }
        });
    }
}
