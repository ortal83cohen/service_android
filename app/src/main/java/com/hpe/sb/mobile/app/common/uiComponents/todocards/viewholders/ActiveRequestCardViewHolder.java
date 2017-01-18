package com.hpe.sb.mobile.app.common.uiComponents.todocards.viewholders;

import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.common.dataClients.userContext.UserContextService;
import com.hpe.sb.mobile.app.common.services.dateTime.DateTimeService;
import com.hpe.sb.mobile.app.common.utils.HtmlUtil;
import com.hpe.sb.mobile.app.infra.eventbus.EventBus;
import com.hpe.sb.mobile.app.common.uiComponents.metricFont.MetricFontCustomButton;
import com.hpe.sb.mobile.app.serverModel.request.RequestType;
import com.hpe.sb.mobile.app.serverModel.user.useritems.RequestActiveUserItem;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.animations.TodoCardsAnimationUtil;
import com.hpe.sb.mobile.app.features.detailsActivity.DetailsActivity;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.utils.TodoCardsEvent;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.utils.TodoCardsViewActionsLogicHandler;
import com.hpe.sb.mobile.app.common.utils.EmptyImageGetter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import javax.inject.Inject;

/**
 * Created by chovel on 10/04/2016.
 *
 */
public class ActiveRequestCardViewHolder implements CardViewHolder<RequestActiveUserItem> {

    @Inject
    public UserContextService userContextService;
    @Inject
    public DateTimeService dateTimeService;

    public static final String VALIDATION_META_PHASE = "Validation";
    public static final String DONE_META_PHASE = "Done";

    private static final String TAG = "ActiveRequestCard";

    private final Context context;

    private final ImageView barIcon;

    private final LinearLayout header;

    private MetricFontCustomButton commentButton;

    private FrameLayout buttonPanel;

    private LayoutInflater layoutInflater;

    public TextView title;

    public TextView entityId;

    public TextView openedFor;

    public TextView elapsedTime;

    public TextView description;

    private RequestActiveUserItem mGRequestActiveItem;

    private final View activeCardView;
    private final EventBus<TodoCardsEvent> eventBus;

    public ActiveRequestCardViewHolder(View activeCardView, LayoutInflater layoutInflater, EventBus<TodoCardsEvent> eventBus) {
        this.context = activeCardView.getContext();
        ((ServiceBrokerApplication) context.getApplicationContext()).getServiceBrokerComponent().inject(this);
        this.eventBus = eventBus;
        this.activeCardView = activeCardView;
        this.layoutInflater = layoutInflater;
        RelativeLayout innerView = (RelativeLayout) activeCardView
                .findViewById(R.id.active_request_card).findViewById(R.id.innerLayout);
        title = (TextView) innerView.findViewById(R.id.title);
        entityId = (TextView) innerView.findViewById(R.id.entityId);
        openedFor = (TextView) innerView.findViewById(R.id.openedForValue);
        barIcon = (ImageView) innerView.findViewById(R.id.barIcon);
        header = (LinearLayout) innerView.findViewById(R.id.header);
        elapsedTime = (TextView) innerView.findViewById(R.id.date);
        description = (TextView) innerView.findViewById(R.id.description);
        buttonPanel = (FrameLayout) innerView.findViewById(R.id.buttonPanel);
    }

    @Override
    public void bind(RequestActiveUserItem activeRequest) {
        mGRequestActiveItem = activeRequest;
        title.setText(activeRequest.getTitle());
        entityId.setText(activeRequest.getId());
        openedFor.setText(activeRequest.getRequestedForName());
        elapsedTime
                .setText(dateTimeService.getElapsedTimeInTextForm(activeRequest.getCreationTime()));
        if(activeRequest.getDescription()!=null) {
            description.setText(
                    HtmlUtil.fromHtml(activeRequest.getDescription(), new EmptyImageGetter()));
        }else {
            ((ServiceBrokerApplication) context.getApplicationContext()).getServiceBrokerComponent()
                    .inject(this);
            Log.e(TAG,"activeRequest description should never be null. entityId "+activeRequest.getId()+
                    ", tenantId "+((userContextService!=null && userContextService.getUserModel()!=null )?
                    userContextService.getUserModel().getTenantId():"unknown"));
        }
        View newButtons;
        if (RequestType.SUPPORT.equals(activeRequest.getRequestType()) && activeRequest.getMetaPhase() != null && !activeRequest.getMetaPhase().equals(VALIDATION_META_PHASE) && !activeRequest.getMetaPhase().equals(DONE_META_PHASE)) {
            newButtons = layoutInflater.inflate(R.layout.buttons_comment_solved, null);
            MetricFontCustomButton markAsSolvedButton = (MetricFontCustomButton) newButtons.findViewById(R.id.card_button_solved);
            initMarkAsSolvedButton(markAsSolvedButton, activeRequest);
        } else {
            newButtons = layoutInflater.inflate(R.layout.buttons_comment, null);
        }
        buttonPanel.removeAllViews();
        buttonPanel.addView(newButtons);
        commentButton = (MetricFontCustomButton) buttonPanel.findViewById(R.id.card_button_comment);
        if (commentButton != null) {
            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCommentClick(v);
                }
            });
        }
    }

    private void initMarkAsSolvedButton(final MetricFontCustomButton markAsSolvedButton, final RequestActiveUserItem activeRequest) {
        markAsSolvedButton.setEnabled(true);
        markAsSolvedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markAsSolvedButton.setEnabled(false);
                TodoCardsAnimationUtil todoCardsAnimationUtil = new TodoCardsAnimationUtil();
                TodoCardsEvent eventData = TodoCardsViewActionsLogicHandler.createEvent(activeRequest.getId(),
                        TodoCardsEvent.TODO_CARDS_MARK_AS_SOLVED_EVENT, activeCardView,buttonPanel);
                todoCardsAnimationUtil.executePositiveActionAnimationsAndSendEvent(activeCardView, context, eventBus, eventData);
            }
        });
    }

    public void onCommentClick(View v) {
        Intent intent = DetailsActivity.createIntent(context,
                mGRequestActiveItem, false, true, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            header.setTransitionName("header");
        }
        Pair<View, String> p2 = Pair.create((View) header, "header");
        ActivityOptionsCompat options;
        if (barIcon != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                barIcon.setTransitionName("info_image");
            }
            Pair<View, String> p1 = Pair.create((View) barIcon, "info_image");
            options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation((Activity) context, p1, p2);
        } else {
            options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation((Activity) context, p2);
        }
        context.startActivity(intent, options.toBundle());
    }

    /*for tests*/
    void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}