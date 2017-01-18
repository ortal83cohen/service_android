package com.hpe.sb.mobile.app.features.detailsActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.widget.*;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.common.dataClients.comments.CommentsRestClient;
import com.hpe.sb.mobile.app.common.dataClients.user.UserClient;
import com.hpe.sb.mobile.app.common.services.dateTime.DateTimeService;
import com.hpe.sb.mobile.app.common.uiComponents.commonLayout.EntityDescriptionView;
import com.hpe.sb.mobile.app.common.uiComponents.metricFont.MetricFontCustomButton;
import com.hpe.sb.mobile.app.common.uiComponents.metricFont.MetricFontCustomTextView;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.animations.TodoCardsAnimationUtil;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.utils.TodoCardsEvent;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.utils.TodoCardsViewActionsLogicHandler;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.utils.TodoCardsViewActionsLogicHandlerFactory;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.viewholders.ActiveRequestCardViewHolder;
import com.hpe.sb.mobile.app.common.utils.LogTagConstants;
import com.hpe.sb.mobile.app.common.utils.keyboard.KeyboardUtils;
import com.hpe.sb.mobile.app.common.utils.keyboard.KeyboardVisibilityEvent;
import com.hpe.sb.mobile.app.common.utils.keyboard.KeyboardVisibilityEventListener;
import com.hpe.sb.mobile.app.features.login.services.ConnectionContextService;
import com.hpe.sb.mobile.app.features.request.RequestClient;
import com.hpe.sb.mobile.app.infra.baseActivities.BaseHttpActivity;
import com.hpe.sb.mobile.app.infra.rx.BaseSubscriber;
import com.hpe.sb.mobile.app.serverModel.comment.Comment;
import com.hpe.sb.mobile.app.serverModel.form.FormField;
import com.hpe.sb.mobile.app.serverModel.form.FormSection;
import com.hpe.sb.mobile.app.serverModel.request.FullRequest;
import com.hpe.sb.mobile.app.serverModel.request.RequestForTrackingPage;
import com.hpe.sb.mobile.app.serverModel.request.RequestType;
import com.hpe.sb.mobile.app.serverModel.user.useritems.*;

import android.graphics.drawable.ColorDrawable;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import javax.inject.Inject;

public class DetailsActivity extends BaseHttpActivity implements View.OnClickListener {


    private static final String EXTRA_DATA = "extra_data";

    private static final String COMMENT_FLAG = "comment_flag";

    public static final String SEND_DENY_EVENT_FLAG = "send_deny_event_flag";

    private static final String COMMENT_TEXT = "comment_text";

    private static final String DISABLE_BUTTON_PANEL = "disable_button_panel";

    private static final String DONE = "Done";

    @Inject
    public RequestClient requestClient;

    @Inject
    public ConnectionContextService connectionContextService;

    @Inject
    public CommentsRestClient commentsRestClient;

    @Inject
    public UserClient userClient;

    @Inject
    public TodoCardsViewActionsLogicHandlerFactory todoCardsViewActionsLogicHandlerFactory;

    @Inject
    public DateTimeService dateTimeService;

    com.hpe.sb.mobile.app.common.uiComponents.metricFont.MetricFontCustomTextView detailsButton;

    private UserItem userItem;

    private boolean commentFlag;

    private boolean sendDenyEvent;

    private boolean disableButtonPanel;

    private EntityDescriptionView mEntityDescriptionView;

    private com.hpe.sb.mobile.app.common.uiComponents.discussion.DiscussionView discussionView;

    private LinearLayout itemsContainer;

    private CardView detailsCard;

    private ScrollView additionalDetailsContainer;

    private FrameLayout buttonPanel;

    private EditText commentEditText;

    private ScrollView scrollView;

    private FrameLayout commentEditTextFrame;

    private ImageView commentEditImage;

    private MetricFontCustomButton buttonOnCard;

    private View buttonOnCardDivider;

    private MetricFontCustomTextView cardTitle;

    private RequestForTrackingPage detailsResponse;

    private CardView chatCard;

    private MetricFontCustomTextView chatHistory;

    private TodoCardsViewActionsLogicHandler todoCardsViewActionsLogicHandler;

    private TextView title;

    private View loadingGif;

    private LinearLayout chatCardContainer;

    private LinearLayout container;

    private String requestId;

    private int buttonPanelDefoaltState;

    private String commentText;

    public static Intent createIntent(Context context, UserItem userItem, boolean disableButtonPanel, boolean comment, boolean showDenyComment) {
        Intent bookIntent = new Intent(context, DetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(EXTRA_DATA, userItem);
        bundle.putBoolean(COMMENT_FLAG, comment);
        bundle.putBoolean(SEND_DENY_EVENT_FLAG, showDenyComment);
        bundle.putBoolean(DISABLE_BUTTON_PANEL, disableButtonPanel);
        bookIntent.putExtras(bundle);
        return bookIntent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_cards_details);
        ((ServiceBrokerApplication) getApplicationContext()).getServiceBrokerComponent()
                .inject(this);
        if (savedInstanceState != null) {
            userItem = savedInstanceState.getParcelable(EXTRA_DATA);
            commentFlag = savedInstanceState.getBoolean(COMMENT_FLAG);
            sendDenyEvent = savedInstanceState.getBoolean(SEND_DENY_EVENT_FLAG, false);
            disableButtonPanel = savedInstanceState.getBoolean(DISABLE_BUTTON_PANEL, false);
            commentText = savedInstanceState.getString(COMMENT_TEXT, "");
        } else {
            userItem = getIntent().getParcelableExtra(EXTRA_DATA);
            commentFlag = getIntent().getBooleanExtra(COMMENT_FLAG, false);
            sendDenyEvent = getIntent().getBooleanExtra(SEND_DENY_EVENT_FLAG, false);
            disableButtonPanel = getIntent().getBooleanExtra(DISABLE_BUTTON_PANEL, false);
        }
        container = (LinearLayout) findViewById(R.id.main_container);
        init();
        initListeners();
        loadingGif = findViewById(R.id.loading_gif);
        container.post(new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.back);
        getSupportActionBar().setElevation(10);
        this.todoCardsViewActionsLogicHandler = todoCardsViewActionsLogicHandlerFactory
                .createTodoCardsViewActionsLogicHandler(this, null, null);
        if (sendDenyEvent) {
            executeDenyButtonAction();
        }
    }

    private void refresh() {
        if (userItem instanceof ApprovalUserItem) {
            userClient.getApproval(this, /*approval id in this case*/userItem.getId())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getRequestForTrackingPageObserver());
        } else {
            requestClient
                    .getRequestForTrackingPage(this, requestId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(getRequestForTrackingPageObserver());
        }
    }

    private BaseSubscriber<RequestForTrackingPage> getRequestForTrackingPageObserver() {
        return new BaseSubscriber<RequestForTrackingPage>(this, sbExceptionsHandler) {
            @Override
            public void onNext(RequestForTrackingPage requestForTrackingPage) {
                detailsResponse = requestForTrackingPage;
                updateDetails();
            }

        };
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_DATA, userItem);
        outState.putBoolean(COMMENT_FLAG, commentFlag);
        outState.putString(COMMENT_TEXT, commentText);
    }

    private void init() {

        detailsCard
                = (CardView) findViewById(R.id.details_card);
        mEntityDescriptionView = (EntityDescriptionView) findViewById(R.id.request_description);
        buttonPanel = (FrameLayout) findViewById(R.id.buttonPanel);
        buttonOnCard = (MetricFontCustomButton) findViewById(R.id.comment_button_on_card);
        buttonOnCardDivider = findViewById(R.id.buttonDivider);
        commentEditImage = (ImageView) findViewById(R.id.comment_edit_image);
        commentEditText = (EditText) findViewById(R.id.comment_edit_text);
        commentEditTextFrame = (FrameLayout) findViewById(R.id.comment_edit_text_frame);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        itemsContainer = (LinearLayout) findViewById(R.id.items_container);
        additionalDetailsContainer = (ScrollView) findViewById(R.id.additional_details_container);
        detailsButton = (com.hpe.sb.mobile.app.common.uiComponents.metricFont.MetricFontCustomTextView) findViewById(
                R.id.details_button);
        chatCard = (CardView) findViewById(R.id.chat_card);
        chatCardContainer = (LinearLayout) findViewById(R.id.chat_card_container);
        chatHistory = (com.hpe.sb.mobile.app.common.uiComponents.metricFont.MetricFontCustomTextView) findViewById(
                R.id.chat_history);
        cardTitle = (com.hpe.sb.mobile.app.common.uiComponents.metricFont.MetricFontCustomTextView) findViewById(
                R.id.card_title);
        discussionView = (com.hpe.sb.mobile.app.common.uiComponents.discussion.DiscussionView) findViewById(R.id.discussion_view);
        title = (TextView) findViewById(android.R.id.title);
        TextView barTitle = (TextView) findViewById(R.id.barTitle);
        TextView entityId = (TextView) findViewById(R.id.entityId);
        LinearLayout followingLayout = (LinearLayout) findViewById(R.id.following_layout);
        TextView openedFor = (TextView) findViewById(R.id.openedFor);
        TextView status = (TextView) findViewById(R.id.status);
        TextView elapsedTime = (TextView) findViewById(R.id.date);
        TextView openedForText = (TextView) findViewById(R.id.openedForText);
        FrameLayout header = (FrameLayout) findViewById(R.id.header);
        FrameLayout subHeader = (FrameLayout) findViewById(R.id.subHeader);
        RelativeLayout requestFrame = (RelativeLayout) findViewById(R.id.requestFrame);
        ImageView barIcon = (ImageView) findViewById(R.id.barIcon);
        title.setText((userItem).getTitle());
        View newButtons = null;

        mEntityDescriptionView.setDescription(
                userItem.getDescription(), connectionContextService);
        LayoutInflater layoutInflater = getLayoutInflater();

        if (userItem instanceof RequestFeedbackUserItem) {
            requestId = userItem.getId();
            barIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon_info_blue));
            subHeader.setBackgroundColor(getResources().getColor(R.color.feedbackCardSecondary));
            header.setBackgroundColor(getResources().getColor(R.color.feedbackCard));
            openedForText.setVisibility(View.VISIBLE);
            status.setText(R.string.new_comment);
            status.setTextColor(getResources().getColor(R.color.feedbackCard));
            barTitle.setText(R.string.provideMoreInformation);
            entityId.setText(userItem.getId());
            openedFor.setText(((RequestFeedbackUserItem) userItem).getRequestedForName());
            elapsedTime.setText(dateTimeService
                    .getElapsedTimeInTextForm(((RequestFeedbackUserItem) userItem).getCreationTime()));
            if (RequestType.SUPPORT.equals(((RequestFeedbackUserItem) userItem).getRequestType())
                    && ((RequestFeedbackUserItem) userItem).getMetaPhase() != null &&
                    !((RequestFeedbackUserItem) userItem).getMetaPhase()
                            .equals(ActiveRequestCardViewHolder.VALIDATION_META_PHASE) &&
                    !((RequestFeedbackUserItem) userItem).getMetaPhase()
                            .equals(ActiveRequestCardViewHolder.DONE_META_PHASE)) {
                newButtons = initWithMarkAsSolvedButton(R.layout.buttons_solved_big, layoutInflater);
            } else {
                newButtons = null;
            }
        } else if (userItem instanceof FollowedActiveRequestItem) {

            openedForText.setVisibility(View.VISIBLE);
            requestId = userItem.getId();
            barTitle.setVisibility(View.GONE);
            followingLayout.setVisibility(View.VISIBLE);
            entityId.setText(userItem.getId());
            openedFor.setText(((FollowedActiveRequestItem) userItem).getRequestedForName());
            elapsedTime.setText(dateTimeService
                    .getElapsedTimeInTextForm(((FollowedActiveRequestItem) userItem).getCreationTime()));
            status.setTextColor(getResources().getColor(R.color.activeRequestCard));
            if (((FollowedActiveRequestItem) userItem).getMetaPhase().equals(DONE)) {
                status.setText(R.string.closed);
                subHeader.setBackgroundColor(
                        getResources().getColor(R.color.activeRequestCard));
                header.setBackgroundColor(getResources().getColor(R.color.activeRequestCard_closed));
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.activeRequestCard_closed)));
                elapsedTime.setVisibility(View.GONE);

            } else {
                status.setText(R.string.submitted);
                subHeader.setBackgroundColor(
                        getResources().getColor(R.color.activeRequestCardSecondary));
                header.setBackgroundColor(getResources().getColor(R.color.activeRequestCard));
            }
            newButtons = null;
        } else if (userItem instanceof RequestActiveUserItem) {
            openedForText.setVisibility(View.VISIBLE);
            requestId = userItem.getId();
            barTitle.setVisibility(View.GONE);
            entityId.setText(userItem.getId());
            openedFor.setText(((RequestActiveUserItem) userItem).getRequestedForName());
            elapsedTime.setText(dateTimeService
                    .getElapsedTimeInTextForm(((RequestActiveUserItem) userItem).getCreationTime()));
            status.setTextColor(getResources().getColor(R.color.activeRequestCard));
            if (RequestType.SUPPORT.equals(((RequestActiveUserItem) userItem).getRequestType())
                    && ((RequestActiveUserItem) userItem).getMetaPhase() != null &&
                    !((RequestActiveUserItem) userItem).getMetaPhase()
                            .equals(ActiveRequestCardViewHolder.VALIDATION_META_PHASE) &&
                    !((RequestActiveUserItem) userItem).getMetaPhase()
                            .equals(ActiveRequestCardViewHolder.DONE_META_PHASE)) {
                if (!disableButtonPanel) {
                    newButtons = initWithMarkAsSolvedButton(R.layout.buttons_solved_big, layoutInflater);
                }
            } else {
                newButtons = null;
            }
            if (((RequestActiveUserItem) userItem).getMetaPhase().equals(DONE)) {
                status.setText(R.string.closed);
                subHeader.setBackgroundColor(
                        getResources().getColor(R.color.activeRequestCard_closed));
                header.setBackgroundColor(getResources().getColor(R.color.activeRequestCardSecondary_closed));
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.activeRequestCardSecondary_closed)));
                elapsedTime.setVisibility(View.GONE);
                buttonOnCard.setVisibility(View.GONE);
                buttonOnCardDivider.setVisibility(View.GONE);
                newButtons = null;
            } else {
                status.setText(R.string.submitted);
                subHeader.setBackgroundColor(
                        getResources().getColor(R.color.activeRequestCardSecondary));
                header.setBackgroundColor(getResources().getColor(R.color.activeRequestCard));
            }


        } else if (userItem instanceof FollowedResolvedRequestItem) {
            requestId = userItem.getId();
            followingLayout.setVisibility(View.VISIBLE);
            barIcon.setImageDrawable(getResources().getDrawable(R.drawable.request_approved));
            subHeader.setBackgroundColor(getResources().getColor(R.color.resolvedCardSecondary));
            header.setBackgroundColor(getResources()
                    .getColor(R.color.request_resolved_card_accept_text_visible_color));
            openedForText.setVisibility(View.VISIBLE);
            status.setText(R.string.solution_provided);
            status.setTextColor(getResources()
                    .getColor(R.color.request_resolved_card_accept_text_visible_color));
            barTitle.setText(R.string.requestResolved);
            entityId.setText(userItem.getId());
            openedFor.setText(((FollowedResolvedRequestItem) userItem).getRequestedForName());
            elapsedTime.setText(dateTimeService
                    .getElapsedTimeInTextForm(((FollowedResolvedRequestItem) userItem).getSolutionComment().getCreationTime()));
            newButtons = null;
        } else if (userItem instanceof RequestResolveUserItem) {
            requestId = userItem.getId();
            barIcon.setImageDrawable(getResources().getDrawable(R.drawable.request_approved));
            subHeader.setBackgroundColor(getResources().getColor(R.color.resolvedCardSecondary));
            header.setBackgroundColor(getResources()
                    .getColor(R.color.request_resolved_card_accept_text_visible_color));
            openedForText.setVisibility(View.VISIBLE);
            status.setText(R.string.solution_provided);
            status.setTextColor(getResources()
                    .getColor(R.color.request_resolved_card_accept_text_visible_color));
            barTitle.setText(R.string.requestResolved);
            entityId.setText(userItem.getId());
            openedFor.setText(((RequestResolveUserItem) userItem).getRequestedForName());
            elapsedTime.setText(dateTimeService
                    .getElapsedTimeInTextForm(((RequestResolveUserItem) userItem).getSolutionComment().getCreationTime()));
            newButtons = layoutInflater.inflate(R.layout.buttons_accept_reject_big, null);
            Button acceptButton = (Button) newButtons.findViewById(R.id.card_button_right);
            Button rejectButton = (Button) newButtons.findViewById(R.id.card_button_left);
            initAcceptButton(acceptButton, rejectButton);
            initRejectButton(rejectButton, acceptButton);
        } else if (userItem instanceof ApprovalUserItem) {
            chatCard.setVisibility(View.GONE);
            requestId = ((ApprovalUserItem) userItem).getEntityId();
            TextView requestedBy = (TextView) findViewById(R.id.requested_by_value);
            TextView approvalType = (TextView) findViewById(R.id.approval_type_value);
            barIcon.setImageDrawable(getResources().getDrawable(R.drawable.icon_info));
            subHeader.setBackgroundColor(getResources().getColor(R.color.approvalCard));
            header.setBackgroundColor(getResources().getColor(R.color.approvalCardSecondary));
            openedForText.setVisibility(View.GONE);
            status.setText(R.string.pendingApproval);
            status.setTextColor(getResources().getColor(R.color.approvalCard));
            barTitle.setText(R.string.pendingYourApproval);
            requestFrame.setVisibility(View.VISIBLE);
            requestedBy.setText(((ApprovalUserItem) userItem).getApproveForName());
            entityId.setText(((ApprovalUserItem) userItem).getCost());
            approvalType.setText(userItem.getEntityType());
            elapsedTime.setText(dateTimeService
                    .getDate(((ApprovalUserItem) userItem).getCreationTime()));
            newButtons = layoutInflater.inflate(R.layout.buttons_approve_deny_big, null);
            Button approveButton = (Button) newButtons.findViewById(R.id.card_button_right);
            Button denyButton = (Button) newButtons.findViewById(R.id.card_button_left);
            initApproveButton(approveButton, denyButton);
            initDenyButton(denyButton);
        }
        buttonPanel.removeAllViews();
        if (newButtons != null) {
            buttonPanel.addView(newButtons);
            buttonPanelDefoaltState = View.VISIBLE;
        } else {
            buttonPanelDefoaltState = View.GONE;
            buttonPanel.setVisibility(View.GONE);
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 0);
            scrollView.setLayoutParams(params);
        }
        if (commentFlag) {
            chatCard.setVisibility(View.VISIBLE);
            onCommentClick(null);
        }
    }

    private void initApproveButton(final Button approveButton, final Button denyButton) {
        approveButton.setEnabled(true);
        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPanel.setVisibility(View.GONE);
                MetricFontCustomTextView animationText = (MetricFontCustomTextView) findViewById(
                        R.id.todo_card_animation_text);
                if (animationText != null) {
                    animationText.setText(R.string.request_approved);
                }
                TodoCardsEvent eventData = TodoCardsViewActionsLogicHandler.createEvent(userItem.getId(),
                        TodoCardsEvent.TODO_CARDS_APPROVE_TASK_EVENT, container, buttonPanel);
                TodoCardsViewActionsLogicHandler.addAdditionalDataToEvent(eventData,
                        TodoCardsViewActionsLogicHandler.SHOULD_GO_BACK, String.valueOf(true));
                TodoCardsAnimationUtil cardsAnimationUtil = new TodoCardsAnimationUtil();
                cardsAnimationUtil.executePositiveActionAnimationsAndSendEvent(container,
                        getApplicationContext(), todoCardsViewActionsLogicHandler.getEventBus(),
                        eventData);
            }
        });
    }

    private void initDenyButton(Button denyButton) {
        denyButton.setEnabled(true);
        denyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                executeDenyButtonAction();
            }
        });
    }

    private void executeDenyButtonAction() {
        MetricFontCustomTextView animationText = (MetricFontCustomTextView) findViewById(
                R.id.negative_todo_card_animation_text);
        if (animationText != null) {
            animationText.setText(R.string.request_denied);
        }

        TodoCardsEvent eventData = TodoCardsViewActionsLogicHandler
                .createEvent(userItem.getId(),
                        TodoCardsEvent.TODO_CARDS_DENY_TASK_EVENT_START, container, buttonPanel);
        TodoCardsViewActionsLogicHandler.addAdditionalDataToEvent(eventData,
                TodoCardsViewActionsLogicHandler.SHOULD_GO_BACK, String.valueOf(true));
        todoCardsViewActionsLogicHandler.getEventBus().send(eventData);
    }

    private void initAcceptButton(final Button acceptButton, final Button rejectButton) {
        acceptButton.setEnabled(true);
        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPanel.setVisibility(View.GONE);
                MetricFontCustomTextView animationText = (MetricFontCustomTextView) findViewById(
                        R.id.todo_card_animation_text);
                if (animationText != null) {
                    animationText.setText(R.string.request_accepted);
                }

                TodoCardsEvent eventData = TodoCardsViewActionsLogicHandler.createEvent(userItem.getId(),
                        TodoCardsEvent.TODO_CARDS_ACCEPT_SOLUTION_EVENT, container, buttonPanel);
                TodoCardsViewActionsLogicHandler.addAdditionalDataToEvent(eventData,
                        TodoCardsViewActionsLogicHandler.SHOULD_GO_BACK, String.valueOf(true));
                TodoCardsAnimationUtil cardsAnimationUtil = new TodoCardsAnimationUtil();
                cardsAnimationUtil.executePositiveActionAnimationsAndSendEvent(container,
                        getApplicationContext(), todoCardsViewActionsLogicHandler.getEventBus(),
                        eventData);
            }
        });
    }

    private void initRejectButton(final Button rejectButton, final Button acceptButton) {
        rejectButton.setEnabled(true);
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPanel.setVisibility(View.GONE);
                MetricFontCustomTextView animationText = (MetricFontCustomTextView) findViewById(
                        R.id.negative_todo_card_animation_text);
                if (animationText != null) {
                    animationText.setText(R.string.request_rejected);
                }
                TodoCardsEvent eventData = TodoCardsViewActionsLogicHandler.createEvent(userItem.getId(),
                        TodoCardsEvent.TODO_CARDS_REJECT_SOLUTION_EVENT, container, buttonPanel);
                TodoCardsViewActionsLogicHandler.addAdditionalDataToEvent(eventData,
                        TodoCardsViewActionsLogicHandler.SHOULD_GO_BACK, String.valueOf(true));
                TodoCardsAnimationUtil cardsAnimationUtil = new TodoCardsAnimationUtil();
                cardsAnimationUtil.executeNegativeActionAnimationsAndSendEvent(container,
                        getApplicationContext(), todoCardsViewActionsLogicHandler.getEventBus(),
                        eventData);
            }
        });
    }

    private View initWithMarkAsSolvedButton(int id, LayoutInflater layoutInflater) {
        View newButtons;
        newButtons = layoutInflater.inflate(id, null);
        final MetricFontCustomButton markAsSolvedButton = (MetricFontCustomButton) newButtons
                .findViewById(R.id.card_button_right);
        markAsSolvedButton.setEnabled(true);
        markAsSolvedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPanel.setVisibility(View.GONE);
                MetricFontCustomTextView animationText = (MetricFontCustomTextView) findViewById(
                        R.id.todo_card_animation_text);
                if (animationText != null) {
                    animationText.setText(R.string.request_accepted);
                }

                TodoCardsEvent eventData = TodoCardsViewActionsLogicHandler.createEvent(userItem.getId(),
                        TodoCardsEvent.TODO_CARDS_MARK_AS_SOLVED_EVENT, container, buttonPanel);
                TodoCardsViewActionsLogicHandler.addAdditionalDataToEvent(eventData,
                        TodoCardsViewActionsLogicHandler.SHOULD_GO_BACK, String.valueOf(true));
                TodoCardsAnimationUtil todoCardsAnimationUtil = new TodoCardsAnimationUtil();
                todoCardsAnimationUtil.executePositiveActionAnimationsAndSendEvent(container,
                        getApplicationContext(), todoCardsViewActionsLogicHandler.getEventBus(),
                        eventData);
            }
        });
        return newButtons;
    }

    @Override
    public void showNoConnectionSnackbar() {
        snackbar = showSnackbar(R.string.no_internet_connection, Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }

    private Snackbar showSnackbar(@StringRes int resId, @Snackbar.Duration int duration) {
        Snackbar snackbar = Snackbar
                .make(container, resId,
                        duration)
                .setAction("1", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        refresh();
                    }
                });

        // Changing message text color
        snackbar.setActionTextColor(getResources().getColor(R.color.offering_service_type_color));

        // Changing action button text color
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView
                .findViewById(android.support.design.R.id.snackbar_action);
        textView.setText("");
        textView.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.refresh, 0);
        return snackbar;
    }

    private void updateDetails() {
        if (detailsResponse == null) {
            return;
        }
        FullRequest request = detailsResponse.getRequestForForm()
                .getRequest();
        loadingGif.setVisibility(View.GONE);
        chatCardContainer.setVisibility(View.VISIBLE);
        discussionView.setItems(request.getComments());
        chatHistory.setText(
                getString(R.string.interaction_history, String.valueOf(discussionView.getItemsCount())));
        detailsButton.setVisibility(View.VISIBLE);

        if (commentFlag) {
            detailsButton.setVisibility(View.GONE);
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    scrollView.scrollTo(0, chatCard.getBottom());
                }
            });
        }

        title.setText(detailsResponse.getTitle());
        mEntityDescriptionView.setDescription(
                detailsResponse.getDescription(), connectionContextService);
    }

    void initListeners() {
        MetricFontCustomButton buttonComment = (MetricFontCustomButton) buttonPanel
                .findViewById(R.id.card_button_comment);
        if (buttonComment != null) {
            buttonComment.setOnClickListener(this);
        }

        commentEditText.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (commentEditText.length() == 0) {
                            commentEditImage.setVisibility(View.GONE);
                            commentEditImage
                                    .setImageDrawable(
                                            getResources().getDrawable(R.drawable.picture));
                        } else {
                            commentEditImage.setVisibility(View.VISIBLE);
                            commentEditImage
                                    .setImageDrawable(getResources().getDrawable(R.drawable.send));
                        }
                        commentText = commentEditText.getText().toString();
                    }
                }
        );
        detailsButton.setOnClickListener(this);

        KeyboardVisibilityEvent.setEventListener(
                this,
                new KeyboardVisibilityEventListener() {
                    @Override
                    public void onVisibilityChanged(boolean isOpen) {
                        commentFlag = isOpen;
                        if (isOpen) {
                            detailsButton.setVisibility(View.GONE);
                            buttonPanel.setVisibility(View.GONE);
                            buttonOnCard.setVisibility(View.GONE);
                            buttonOnCardDivider.setVisibility(View.GONE);
                            commentEditTextFrame.setVisibility(View.VISIBLE);
                        } else {
                            detailsButton.setVisibility(View.VISIBLE);
                            buttonPanel.setVisibility(buttonPanelDefoaltState);
                            buttonOnCard.setVisibility(View.VISIBLE);
                            buttonOnCardDivider.setVisibility(View.VISIBLE);
                            commentEditTextFrame.setVisibility(View.GONE);
                        }
                        scrollView.fullScroll(View.FOCUS_DOWN);
                    }
                });
    }

    @Override
    public void onBackPressed() {
        if (additionalDetailsContainer.getVisibility() == View.VISIBLE) {
            additionalDetailsContainer.setVisibility(View.GONE);
            getSupportActionBar().setElevation(10);
            setTitle(getString(R.string.title_activity_details));
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
//        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void onCommentSubmit(View v) {
        if (commentEditImage.getDrawable().getConstantState()
                .equals(getResources().getDrawable(R.drawable.picture).getConstantState())) {
            final Toast toast = Toast.makeText(getBaseContext(), "attachment here", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else if (commentEditImage.getDrawable()
                .getConstantState()
                .equals(getResources().getDrawable(R.drawable.send).getConstantState())) {
            Comment comment = new Comment();
            comment.setContent(commentEditText.getText().toString());
            Observable<Void> commentObservable = commentsRestClient
                    .addComment(this, userItem.getEntityType(), requestId,
                            commentEditText.getText().toString());
            commentObservable
                    .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseSubscriber<Void>(this, getSbExceptionsHandler()) {
                public static final java.lang.String COMMENT_SUCCESS_MESSAGE = "SUCCESS";

                public static final java.lang.String COMMENT_ERROR_MESSAGE = "error";
                @Override
                public void onNext(Void aVoid) {
                    if (userItem instanceof RequestFeedbackUserItem) {
                        userClient.deleteItemAndUpdateUserItems(getBaseContext(), userItem);
                    }
                    Log.e(LogTagConstants.COMMENT, COMMENT_SUCCESS_MESSAGE);
                    commentEditText.setText("");
                    commentText = "";
                }

                @Override
                public void onError(Throwable e) {
                    final Toast toast = Toast.makeText(getBaseContext(), R.string.failed_to_add_comment, Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    removeLastCommentFromView();
                    Log.e(LogTagConstants.COMMENT, COMMENT_ERROR_MESSAGE, e);
                    super.onError(e);
                }
            });
            addCommentToView(comment);
        }
        KeyboardUtils.hideKeyboard(this);
    }

    private void addCommentToView(Comment comment) {
        discussionView.addItem(comment);
        chatHistory.setText(
                getString(R.string.interaction_history,
                        String.valueOf(discussionView.getItemsCount())));
    }

    private void removeLastCommentFromView() {
        discussionView.deleteItem();
        chatHistory.setText(getString(R.string.interaction_history, String.valueOf(discussionView.getItemsCount())));
    }

    public void onCommentClick(View v) {
        container.post(new Runnable() {
            @Override
            public void run() {
                commentEditText.requestFocus();
                commentEditText.setText(commentText);
                KeyboardUtils.showKeyboard(getBaseContext(), commentEditText);
            }
        });
    }

    public void onDetailsClick(View v) {
        additionalDetailsContainer.setVisibility(View.VISIBLE);
        getSupportActionBar().setElevation(0);
        setTitle(getString(R.string.title_additional_details));
        View view;
        if (itemsContainer.getChildCount() == 0) {
            for (FormSection section : detailsResponse
                    .getRequestForForm().getForm().getFormSections()
                    ) {
                if (section.getDisplayLabel() != null && !section.getDisplayLabel().equals("")) {
                    cardTitle.setText(section.getDisplayLabel());
                } else {
                    cardTitle.setText(getString(R.string.title_additional_details));
                }
                for (FormField mGFormField : section.getFields()) {

                    view = View.inflate(getBaseContext(),
                            R.layout.additional_details_item, null);
                    TextView test1 = (TextView) view
                            .findViewById(android.R.id.text1);
                    TextView test2 = (TextView) view
                            .findViewById(android.R.id.text2);
                    test1.setText(mGFormField.getDisplayLabel());
                    String detailsText = RequestForFormUtils.getFieldAsString(mGFormField,
                            detailsResponse.getRequestForForm().getRequest().getProperties()
                                    .get(mGFormField.getModelAttribute()),
                            detailsResponse.getRequestForForm().getForm().getEnumDescriptors());
                    test2.setText(detailsText);
                    test2.setContentDescription(mGFormField.getDisplayLabel());
                    if (detailsText != null && !detailsText.equals("")) {
                        itemsContainer.addView(view);
                    }
                }
            }
            additionalDetailsContainer.invalidate();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.card_button_comment:
            case R.id.comment_button_on_card:
                onCommentClick(v);
                break;
            case R.id.details_button:
                onDetailsClick(v);
                break;
        }

    }
}
