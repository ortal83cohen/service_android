package com.hpe.sb.mobile.app.common.uiComponents.todocards.viewholders;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.utils.TodoCardsEvent;
import com.hpe.sb.mobile.app.common.utils.HtmlUtil;
import com.hpe.sb.mobile.app.infra.eventbus.EventBus;
import com.hpe.sb.mobile.app.serverModel.comment.Comment;
import com.hpe.sb.mobile.app.serverModel.user.useritems.RequestResolveUserItem;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.animations.TodoCardsAnimationUtil;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.utils.TodoCardsViewActionsLogicHandler;
import com.hpe.sb.mobile.app.common.services.dateTime.DateTimeService;
import com.hpe.sb.mobile.app.common.utils.EmptyImageGetter;

import javax.inject.Inject;

/**
 * Created by chovel on 10/04/2016.
 *
 */
public class RequestResolvedCardViewHolder implements CardViewHolder<RequestResolveUserItem> {

    @Inject
    public DateTimeService dateTimeService;

    public TextView title;
    public TextView entityId;
    public TextView openedFor;
    public TextView elapsedTime;
    public TextView solutionText;
    Button acceptButton;
    Button rejectButton;
    private EventBus<TodoCardsEvent> eventBus;

    public RequestResolvedCardViewHolder(final View requestResolvedView, final Context context, EventBus<TodoCardsEvent> eventBus) {
        ((ServiceBrokerApplication)context.getApplicationContext()).getServiceBrokerComponent().inject(this);
        this.eventBus = eventBus;
        View innerView = requestResolvedView.findViewById(R.id.outerLayout).findViewById(R.id.request_resolved_card).findViewById(R.id.innerLayout);
        title = (TextView)innerView.findViewById(R.id.title);
        entityId = (TextView)innerView.findViewById(R.id.entityId);
        openedFor = (TextView)innerView.findViewById(R.id.openedForValue);
        elapsedTime = (TextView)innerView.findViewById(R.id.date);
        View commentBubble = innerView.findViewById(R.id.solutionBubble);
        solutionText = (TextView)commentBubble.findViewById(R.id.comment);
        acceptButton = (Button) requestResolvedView.findViewById(R.id.outerLayout).findViewById(R.id.request_resolved_card).
                findViewById(R.id.innerLayout).findViewById(R.id.card_button_accept);
        rejectButton = (Button) requestResolvedView.findViewById(R.id.outerLayout).findViewById(R.id.request_resolved_card).
                findViewById(R.id.innerLayout).findViewById(R.id.card_button_reject);
        initAcceptButton(requestResolvedView, context);
        initRejectButton(requestResolvedView, context);
    }

    private void initAcceptButton(final View requestResolvedView, final Context context) {
        acceptButton.setEnabled(true);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptButton.setEnabled(false);
                rejectButton.setEnabled(false);
                TodoCardsAnimationUtil cardsAnimationUtil = new TodoCardsAnimationUtil();
                TodoCardsEvent eventData = TodoCardsViewActionsLogicHandler.createEvent(entityId.getText().toString(),
                        TodoCardsEvent.TODO_CARDS_ACCEPT_SOLUTION_EVENT, requestResolvedView,acceptButton);
                cardsAnimationUtil.executePositiveActionAnimationsAndSendEvent(requestResolvedView, context, eventBus, eventData);
            }
        });
    }

    private void initRejectButton(final View requestResolvedView, final Context context) {
        rejectButton.setEnabled(true);
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectButton.setEnabled(false);
                acceptButton.setEnabled(false);
                TodoCardsAnimationUtil cardsAnimationUtil = new TodoCardsAnimationUtil();
                TodoCardsEvent eventData = TodoCardsViewActionsLogicHandler.createEvent(entityId.getText().toString(),
                        TodoCardsEvent.TODO_CARDS_REJECT_SOLUTION_EVENT, requestResolvedView,rejectButton);
                cardsAnimationUtil.executeNegativeActionAnimationsAndSendEvent(requestResolvedView, context, eventBus, eventData);
            }
        });
    }

    public void bind(RequestResolveUserItem requestResolveItem) {
        title.setText(requestResolveItem.getTitle());
        entityId.setText(requestResolveItem.getId());
        solutionText.setText(
                HtmlUtil.fromHtml(requestResolveItem.getSolution(),new EmptyImageGetter()));
        openedFor.setText(requestResolveItem.getRequestedForName());
        Comment solutionComment = requestResolveItem.getSolutionComment();
        if (solutionComment != null && solutionComment.getCreationTime() != 0) {
            elapsedTime.setText(dateTimeService.getElapsedTimeInTextForm(solutionComment.getCreationTime())); //solution time
        }
    }

    @Override
    public void onCommentClick(View view) {

    }

}
