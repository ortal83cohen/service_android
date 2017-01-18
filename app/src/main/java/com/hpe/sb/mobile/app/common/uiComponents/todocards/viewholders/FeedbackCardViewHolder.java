package com.hpe.sb.mobile.app.common.uiComponents.todocards.viewholders;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.common.utils.HtmlUtil;
import com.hpe.sb.mobile.app.infra.image.ImageServiceUtil;
import com.hpe.sb.mobile.app.infra.eventbus.EventBus;
import com.hpe.sb.mobile.app.common.uiComponents.metricFont.MetricFontCustomButton;
import com.hpe.sb.mobile.app.serverModel.comment.Comment;
import com.hpe.sb.mobile.app.serverModel.request.RequestType;
import com.hpe.sb.mobile.app.serverModel.user.useritems.RequestFeedbackUserItem;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.animations.TodoCardsAnimationUtil;
import com.hpe.sb.mobile.app.features.detailsActivity.DetailsActivity;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.utils.TodoCardsEvent;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.utils.TodoCardsViewActionsLogicHandler;
import com.hpe.sb.mobile.app.common.services.dateTime.DateTimeService;
import com.hpe.sb.mobile.app.common.utils.EmptyImageGetter;

import javax.inject.Inject;

/**
 * Created by chovel on 10/04/2016.
 *
 */
public class FeedbackCardViewHolder implements CardViewHolder<RequestFeedbackUserItem> {

    @Inject
    public DateTimeService dateTimeService;

    private final EventBus<TodoCardsEvent> eventBus;
    private final View feedbackCardView;
    private LayoutInflater layoutInflater;
    private ImageServiceUtil imageServiceUtil;
    private FrameLayout buttonPanel;
    public TextView title;
    public TextView entityId;
    public TextView openedFor;
    public TextView elapsedTime;
    public TextView commentName;
    public TextView commentText;
    public ImageView commentImage;
    private MetricFontCustomButton commentButton;
    RequestFeedbackUserItem feedbackItem;
    private final LinearLayout header;
    private final ImageView barIcon;

    public FeedbackCardViewHolder(View feedbackCardView, ImageServiceUtil imageServiceUtil, LayoutInflater layoutInflater, EventBus<TodoCardsEvent> eventBus) {
        ((ServiceBrokerApplication)feedbackCardView.getContext().getApplicationContext()).getServiceBrokerComponent().inject(this);
        this.eventBus = eventBus;
        this.feedbackCardView = feedbackCardView;
        this.imageServiceUtil = imageServiceUtil;
        this.layoutInflater = layoutInflater;
        View innerView = feedbackCardView.findViewById(R.id.outerLayout).findViewById(R.id.feedback_card).findViewById(R.id.innerLayout);
        title = (TextView)innerView.findViewById(R.id.title);
        entityId = (TextView)innerView.findViewById(R.id.entityId);
        openedFor = (TextView)innerView.findViewById(R.id.openedForValue);
        elapsedTime = (TextView)innerView.findViewById(R.id.date);
        commentImage = (ImageView)innerView.findViewById(R.id.commentImage);
        View commentBubble = innerView.findViewById(R.id.commentBubble);
        commentName = (TextView)commentBubble.findViewById(R.id.name);
        commentText = (TextView)commentBubble.findViewById(R.id.comment);
        buttonPanel = (FrameLayout)innerView.findViewById(R.id.buttonPanel);
        header = (LinearLayout) innerView.findViewById(R.id.header);
        barIcon = (ImageView) innerView.findViewById(R.id.barIcon);
    }

    public void bind(RequestFeedbackUserItem feedbackItem) {
        this.feedbackItem = feedbackItem;
        title.setText(feedbackItem.getTitle());
        entityId.setText(feedbackItem.getId());
        Comment lastComment = feedbackItem.getLastComment();
        if (lastComment != null) {
            commentName.setText(lastComment.getAuthorName());
            commentText.setText(
                    HtmlUtil.fromHtml(lastComment.getContent(),new EmptyImageGetter()));
            String authorAvatarImageId = lastComment.getAuthorAvatarImageId();
            if (authorAvatarImageId != null && !authorAvatarImageId.isEmpty()) {
                imageServiceUtil.loadImageWithCircleMask(authorAvatarImageId, commentImage, null, null);
            }
        }
        openedFor.setText(feedbackItem.getRequestedForName());
        elapsedTime.setText(dateTimeService.getElapsedTimeInTextForm(feedbackItem.getCreationTime()));
        View newButtons;
        if (RequestType.SUPPORT.equals(feedbackItem.getRequestType()) && feedbackItem.getMetaPhase() != null && !feedbackItem.getMetaPhase().equals(ActiveRequestCardViewHolder.VALIDATION_META_PHASE) &&
                !feedbackItem.getMetaPhase().equals(ActiveRequestCardViewHolder.DONE_META_PHASE)) {
            newButtons = layoutInflater.inflate(R.layout.buttons_comment_solved, null);
            MetricFontCustomButton markAsSolvedButton = (MetricFontCustomButton) newButtons.findViewById(R.id.card_button_solved);
            initMarkAsSolvedButton(markAsSolvedButton, feedbackItem);
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

    private void initMarkAsSolvedButton(final MetricFontCustomButton markAsSolvedButton, final RequestFeedbackUserItem feedbackItem) {
        markAsSolvedButton.setEnabled(true);
        markAsSolvedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                markAsSolvedButton.setEnabled(false);
                TodoCardsAnimationUtil todoCardsAnimationUtil = new TodoCardsAnimationUtil();
                TodoCardsEvent eventData = TodoCardsViewActionsLogicHandler.createEvent(feedbackItem.getId(),
                        TodoCardsEvent.TODO_CARDS_MARK_AS_SOLVED_EVENT, feedbackCardView,buttonPanel);
                todoCardsAnimationUtil.executePositiveActionAnimationsAndSendEvent(feedbackCardView, feedbackCardView.getContext(), eventBus, eventData);
            }
        });
    }

    public void onCommentClick(View v) {
        Intent intent = DetailsActivity.createIntent(feedbackCardView.getContext(),
                feedbackItem, false, true, false);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && header != null) {
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
                    makeSceneTransitionAnimation((Activity) feedbackCardView.getContext(), p1, p2);
        } else {
            options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation((Activity) feedbackCardView.getContext(), p2);
        }
        feedbackCardView.getContext().startActivity(intent, options.toBundle());
    }
}
