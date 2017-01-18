package com.hpe.sb.mobile.app.common.uiComponents.todocards.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.infra.eventbus.EventBus;
import com.hpe.sb.mobile.app.serverModel.user.useritems.ApprovalUserItem;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.animations.TodoCardsAnimationUtil;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.utils.TodoCardsEvent;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.utils.TodoCardsViewActionsLogicHandler;
import com.hpe.sb.mobile.app.common.services.dateTime.DateTimeService;

import javax.inject.Inject;

/**
 * Created by chovel on 07/04/2016.
 *
 */
public class ApprovalCardViewHolder implements CardViewHolder<ApprovalUserItem> {

    @Inject
    public DateTimeService dateTimeService;

    public static final String COST_TEXT_FORMAT = "%s + %s";

    public TextView title;
    public TextView price;
    public TextView entityDetails;
    public TextView requestedBy;
    public TextView approvalTaskTitle;
    public TextView creationDate;
    Button approveButton;
    Button denyButton;
    private EventBus<TodoCardsEvent> eventBus;
    /**
     * Id is used for events, animations, etc..
     * not necessarily used when displaying the view.
     * holds the id of the task that had been bound last
     */
    private String taskId;

    public ApprovalCardViewHolder(View approvalCardView, final Context context, EventBus<TodoCardsEvent> eventBus) {
        ((ServiceBrokerApplication)context.getApplicationContext()).getServiceBrokerComponent().inject(this);
        this.eventBus = eventBus;
        View innerView = approvalCardView.findViewById(R.id.outerLayout).findViewById(R.id.approval_card).findViewById(R.id.innerLayout);
        title = (TextView) innerView.findViewById(R.id.title);
        price = (TextView) innerView.findViewById(R.id.price);
        entityDetails = (TextView) innerView.findViewById(R.id.entity_details);
        requestedBy = (TextView) innerView.findViewById(R.id.requested_by_value);
        approvalTaskTitle = (TextView) innerView.findViewById(R.id.approval_task_title_value);
        creationDate = (TextView) innerView.findViewById(R.id.date);
        approveButton = (Button) approvalCardView.findViewById(R.id.outerLayout).findViewById(R.id.approval_card).
                findViewById(R.id.innerLayout).findViewById(R.id.card_button_approve);
        denyButton = (Button) approvalCardView.findViewById(R.id.outerLayout).findViewById(R.id.approval_card).
                findViewById(R.id.innerLayout).findViewById(R.id.card_button_deny);
        initApproveButton(approvalCardView, context);
        initDenyButton(approvalCardView);
    }

    private void initApproveButton(final View ApprovalView, final Context context) {
        approveButton.setEnabled(true);
        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                approveButton.setEnabled(false);
                denyButton.setEnabled(false);
                TodoCardsAnimationUtil cardsAnimationUtil = new TodoCardsAnimationUtil();
                TodoCardsEvent eventData = TodoCardsViewActionsLogicHandler.createEvent(taskId, TodoCardsEvent.TODO_CARDS_APPROVE_TASK_EVENT, ApprovalView,approveButton);
                cardsAnimationUtil.executePositiveActionAnimationsAndSendEvent(ApprovalView, context, eventBus, eventData);
            }
        });
    }

    private void initDenyButton(final View ApprovalView) {
        denyButton.setEnabled(true);
        denyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TodoCardsEvent eventData = TodoCardsViewActionsLogicHandler.createEvent(taskId, TodoCardsEvent.TODO_CARDS_DENY_TASK_EVENT_START, ApprovalView,approveButton);
                eventBus.send(eventData);
            }
        });
    }

    public void bind(ApprovalUserItem approvalItem) {
        taskId = approvalItem.getTaskId();
        title.setText(approvalItem.getTitle());
        entityDetails.setText(approvalItem.getEntityType() + " " + approvalItem.getEntityId());
        requestedBy.setText(approvalItem.getApproveForName());
        approvalTaskTitle.setText(approvalItem.getTaskTitle());
        if (approvalItem.getCost() != null || approvalItem.getRecurringCost() != null) {
            String priceText;
            if (approvalItem.getCost() != null) {
                priceText = approvalItem.getRecurringCost() != null ?
                        String.format(COST_TEXT_FORMAT, approvalItem.getCost(), approvalItem.getRecurringCost())
                        : approvalItem.getCost();
            } else {
                priceText = approvalItem.getRecurringCost();
            }
            price.setVisibility(View.VISIBLE);
            price.setText(priceText);
        }else {
            price.setVisibility(View.GONE);
        }
        creationDate.setText(dateTimeService.getDate(approvalItem.getCreationTime()));
    }

    @Override
    public void onCommentClick(View view) {

    }

}
