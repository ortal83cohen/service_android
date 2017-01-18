package com.hpe.sb.mobile.app.common.uiComponents.todocards.utils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.common.dataClients.user.UserClient;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.TodoCardData;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.TodoCardsView;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.animations.TodoCardsAnimationUtil;
import com.hpe.sb.mobile.app.common.utils.LogTagConstants;
import com.hpe.sb.mobile.app.features.home.DisplayMessage;
import com.hpe.sb.mobile.app.features.request.RequestClient;
import com.hpe.sb.mobile.app.infra.baseActivities.BaseActivity;
import com.hpe.sb.mobile.app.infra.eventbus.EventBus;
import com.hpe.sb.mobile.app.infra.rx.BaseSubscriber;
import com.hpe.sb.mobile.app.serverModel.user.useritems.*;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by salemo on 04/05/2016.
 * handle the cards logic, events and the state holding(the cards displayed)
 */
public class TodoCardsViewActionsLogicHandler {

    public static final String LOADING_USER_ITEMS_ERROR_MESSAGE = "Loading user items failed.";

    public static final String ENTITY_DATA_ID = "entityDataId";

    public static final String SHOULD_GO_BACK = "shouldGoBack";

    public static final String COMMENT_DATA = "commentData";

    public static final String CARD_VIEW = "cardView";

    public static final String BUTTON_PANEL = "buttonPanel";

    //the activity that holds the cards
    private final BaseActivity activity;

    @Inject
    protected EventBus<TodoCardsEvent> eventBus;

    @Inject
    public UserClient userClient;

    @Inject
    public RequestClient requestClient;

    private TodoCardsView cardsView;

    private OnCompletedLoadingListener mOnCompletedLoadingListener;

    TodoCardsViewActionsLogicHandler(BaseActivity activity, TodoCardsView cardsView,
                                     OnCompletedLoadingListener onCompletedLoadingListener) {
        //dagger injection
        ((ServiceBrokerApplication) activity.getApplicationContext()).getServiceBrokerComponent()
                .inject(this);
        this.activity = activity;
        this.mOnCompletedLoadingListener = onCompletedLoadingListener;
        this.cardsView = cardsView;
    }

    public void refreshCardsView(boolean force) {
        Observable<UserItems> userItems = userClient.getUserItems(activity, force /* !forceRefresh */);
        setUserItemsByObservable(userItems, force);
        cardsView.measure(cardsView.getMeasuredWidth(), cardsView.getMeasuredHeight());
    }

    void initCardsView() {
        if (cardsView != null) {
            //set card list to initialize view and set the correct layout params
            cardsView.setCardsList(cardsView.getCardsList() != null ? cardsView.getCardsList() : new ArrayList<TodoCardData>());
            cardsView.setEventBus(eventBus);
        }
        initEventBusHandling();
    }

    private void initEventBusHandling() {
        eventBus.toObserverable()
                .subscribe(getCardsEventsHandling());
    }

    /**
     * handle the cards events
     **/
    private BaseSubscriber<TodoCardsEvent> getCardsEventsHandling() {
        return new BaseSubscriber<TodoCardsEvent>(activity, activity.getSbExceptionsHandler()) {
            @Override
            public void onNext(TodoCardsEvent event) {
                Observable<UserItems> mgUserItemsObservable;
                Map<String, Object> cardData;
                switch (event.getEventType()) {
                    case TodoCardsEvent.TODO_CARDS_ACCEPT_SOLUTION_EVENT:
                        Log.d(LogTagConstants.USER_ITEMS,
                                "TODO_CARDS_ACCEPT_SOLUTION_EVENT RECEIVED");
                        cardData = event.getData();
                        mgUserItemsObservable = markRequestAsSolved(cardData, R.string.failed_to_accept);
                        eventPostActions(cardData, mgUserItemsObservable);
                        break;
                    case TodoCardsEvent.TODO_CARDS_REJECT_SOLUTION_EVENT:
                        Log.d(LogTagConstants.USER_ITEMS,
                                "TODO_CARDS_REJECT_SOLUTION_EVENT RECEIVED");
                        cardData = event.getData();
                        mgUserItemsObservable = rejectSolutionByRequestId(cardData);
                        eventPostActions(cardData, mgUserItemsObservable);
                        break;
                    case TodoCardsEvent.TODO_CARDS_APPROVE_TASK_EVENT:
                        Log.d(LogTagConstants.USER_ITEMS, "TODO_CARDS_APPROVE_TASK_EVENT RECEIVED");
                        cardData = event.getData();
                        mgUserItemsObservable = approveTaskByTaskId(cardData);
                        eventPostActions(cardData, mgUserItemsObservable);
                        break;
                    case TodoCardsEvent.TODO_CARDS_DENY_TASK_EVENT_START:
                        Log.d(LogTagConstants.USER_ITEMS,
                                "TODO_CARDS_DENY_TASK_EVENT_START RECEIVED");
                        DenyTaskDialog denyTaskDialog = new DenyTaskDialog(activity, eventBus, event);
                        denyTaskDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                        denyTaskDialog.getWindow().setGravity(Gravity.TOP);
                        denyTaskDialog.show();
                        break;
                    case TodoCardsEvent.TODO_CARDS_DENY_TASK_EVENT_END:
                        Log.d(LogTagConstants.USER_ITEMS,
                                "TODO_CARDS_DENY_TASK_EVENT_END RECEIVED");
                        cardData = event.getData();
                        mgUserItemsObservable = denyTaskByTaskId(cardData);
                        eventPostActions(cardData, mgUserItemsObservable);
                        break;
                    case TodoCardsEvent.TODO_CARDS_MARK_AS_SOLVED_EVENT:
                        Log.d(LogTagConstants.USER_ITEMS,
                                "TODO_CARDS_MARK_AS_SOLVED_EVENT RECEIVED");
                        cardData = event.getData();
                        mgUserItemsObservable = markRequestAsSolved(cardData, R.string.failed_to_mark_resolved);
                        eventPostActions(cardData, mgUserItemsObservable);
                        break;
                    default:
                        Log.e(LogTagConstants.USER_ITEMS,
                                "ERROR: Unknown todoCardsEvent received, todoCardsEvent type: "
                                        + event.getEventType() + ", todoCardsEvent data: " + event
                                        .getData());
                        break;
                }
            }
        };
    }

    private Observable<UserItems> markRequestAsSolved(final Map<String, Object> cardData, final int msgId) {
        String requestId = (String) cardData.get(ENTITY_DATA_ID);
        Observable<UserItems> mgUserItemsObservable = requestClient.acceptRequest(activity, requestId).observeOn(AndroidSchedulers.mainThread()).doOnError(new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                removePositiveAnimation(cardData);
                Toast.makeText(activity, msgId, Toast.LENGTH_LONG).show();
            }
        });
        setUserItemsByObservable(mgUserItemsObservable, true);
        return mgUserItemsObservable;
    }

    private void eventPostActions(Map cardData, Observable<UserItems> mgUserItemsObservable) {
        boolean shouldGoBack = cardData.get(SHOULD_GO_BACK) != null ? Boolean.valueOf((String) cardData.get(SHOULD_GO_BACK)) : false;
        if (shouldGoBack && mgUserItemsObservable != null) {
            mgUserItemsObservable.observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseSubscriber<UserItems>(activity, activity.getSbExceptionsHandler()) {
                @Override
                public void onNext(UserItems userItems) {
                    activity.onBackPressed();
                }
            });
        }
    }

    private Observable<UserItems> denyTaskByTaskId(final Map<String, Object> cardData) {
        String taskId = (String) cardData.get(ENTITY_DATA_ID);
        String comment = (String) cardData.get(COMMENT_DATA);
        Observable<UserItems> mgUserItemsObservable = userClient.denyTask(activity, taskId, comment).observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        removeNegativeAnimation(cardData);
                        Toast.makeText(activity, R.string.failed_to_deny, Toast.LENGTH_LONG).show();
                    }
                });
        setUserItemsByObservable(mgUserItemsObservable, true);
        return mgUserItemsObservable;
    }

    private Observable<UserItems> approveTaskByTaskId(final Map<String, Object> cardData) {
        String taskId = (String) cardData.get(ENTITY_DATA_ID);
        Observable<UserItems> mgUserItemsObservable = userClient.approveTask(activity, taskId).observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        removePositiveAnimation(cardData);
                        Toast.makeText(activity, R.string.failed_to_approve, Toast.LENGTH_LONG).show();
                    }
                });
        setUserItemsByObservable(mgUserItemsObservable, true);
        return mgUserItemsObservable;
    }

    private Observable<UserItems> rejectSolutionByRequestId(final Map<String, Object> cardData) {
        String requestId = (String) cardData.get(ENTITY_DATA_ID);
        Observable<UserItems> mgUserItemsObservable = requestClient.rejectRequest(activity, requestId).observeOn(AndroidSchedulers.mainThread()).doOnError(new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                removeNegativeAnimation(cardData);
                Toast.makeText(activity, R.string.failed_to_reject, Toast.LENGTH_LONG).show();
            }
        });
        setUserItemsByObservable(mgUserItemsObservable, true);
        return mgUserItemsObservable;
    }

    private void removeNegativeAnimation(Map<String, Object> cardData) {
        View view = TodoCardsViewActionsLogicHandler.getEventViewData(cardData);
        View buttonPanel = TodoCardsViewActionsLogicHandler.getButtonPanelData(cardData);
        TodoCardsAnimationUtil todoCardsAnimationUtil = new TodoCardsAnimationUtil();
        todoCardsAnimationUtil.removeNegativeAnimation(view, activity);
        buttonPanel.setVisibility(View.VISIBLE);
        buttonPanel.setEnabled(true);
    }

    private void removePositiveAnimation(Map<String, Object> cardData) {
        View view = TodoCardsViewActionsLogicHandler.getEventViewData(cardData);
        View buttonPanel = TodoCardsViewActionsLogicHandler.getButtonPanelData(cardData);
        TodoCardsAnimationUtil todoCardsAnimationUtil = new TodoCardsAnimationUtil();
        todoCardsAnimationUtil.removePositiveAnimation(view, activity);
        buttonPanel.setVisibility(View.VISIBLE);
        buttonPanel.setEnabled(true);
    }

    private void setUserItemsByObservable(Observable<UserItems> userItems, boolean force) {
        userItems.observeOn(AndroidSchedulers.mainThread())
                .subscribe(getUserItemsHandler(force));
    }

    BaseSubscriber<UserItems> getUserItemsHandler(final boolean force) {
        return new BaseSubscriber<UserItems>(activity, activity.getSbExceptionsHandler()) {
            boolean firstResult = true;
            @Override
            public void onCompleted() {
                if (mOnCompletedLoadingListener != null) {
                    mOnCompletedLoadingListener.stopRefreshing();
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(LogTagConstants.USER_ITEMS, LOADING_USER_ITEMS_ERROR_MESSAGE, e);
                if (mOnCompletedLoadingListener != null) {
                    mOnCompletedLoadingListener.stopRefreshing();
                }
                super.onError(e);
            }

            @Override
            public void onNext(UserItems userItems) {
                if (firstResult || force) {
                    firstResult = false;
                    List<TodoCardData> cards = new ArrayList<>();
                    for (ApprovalUserItem approval : userItems.getApprovals()) {
                        cards.add(new TodoCardData(approval, ApprovalUserItem.class));
                    }
                    for (RequestFeedbackUserItem feedbackItem : userItems
                            .getRequestFeedbackItems()) {
                        cards.add(new TodoCardData(feedbackItem, RequestFeedbackUserItem.class));
                    }
                    for (RequestResolveUserItem requestResolveItem : userItems
                            .getRequestResolveItems()) {
                        cards.add(new TodoCardData(requestResolveItem,
                                RequestResolveUserItem.class));
                    }
                    for (RequestActiveUserItem request : userItems.getActiveRequests()) {
                        cards.add(new TodoCardData(request, RequestActiveUserItem.class));
                    }
                    if (cardsView != null && isCardListChanged(cards)) {
                        cardsView.setCardsList(cards);
                    }
                } else {
                    ((DisplayMessage) activity).show(R.string.new_updates_available, DisplayMessage.LENGTH_LONG);
                }

            }
        };
    }

    private boolean isCardListChanged(List<TodoCardData> cards) {
        return cardsView.getCardsList() == null || !cardsView.getCardsList().equals(cards);
    }

    public static TodoCardsEvent addAdditionalDataToEvent(TodoCardsEvent cardData,
            String key, String value) {
        if (cardData != null) {
            @TodoCardsEvent.EventType int eventType = cardData.getEventType();
            Map<String, Object> data = cardData.getData();
            data.put(key, value);
            cardData = new TodoCardsEvent(eventType, data);
        }
        return cardData;
    }

    public static TodoCardsEvent createEvent(String entityId,
            @TodoCardsEvent.EventType int todoCardsEventType, View view, View buttonPanel) {
        Map<String, Object> cardData = new HashMap<>();
        cardData.put(ENTITY_DATA_ID, entityId);
        cardData.put(CARD_VIEW, view);
        cardData.put(BUTTON_PANEL, buttonPanel);
        return new TodoCardsEvent(todoCardsEventType, cardData);
    }

    public static View getEventViewData(Map<String, Object> todoCardsEventData) {
        return (View) todoCardsEventData.get(CARD_VIEW);
    }

    static View getButtonPanelData(Map<String, Object> todoCardsEventData) {
        return (View) todoCardsEventData.get(BUTTON_PANEL);
    }

    public EventBus<TodoCardsEvent> getEventBus() {
        return eventBus;
    }

    public interface OnCompletedLoadingListener {

        public void stopRefreshing();
    }

    /*for tests*/
    public void setEventBus(EventBus<TodoCardsEvent> eventBus) {
        this.eventBus = eventBus;
    }

    /*for tests*/
    public void setRequestClient(RequestClient requestClient) {
        this.requestClient = requestClient;
    }

    /*for tests*/
    public void setUserClient(UserClient userClient) {
        this.userClient = userClient;
    }

    /*for tests*/
    public TodoCardsView getCardsView() {
        return cardsView;
    }

    /*for tests*/
    public void setCardsView(TodoCardsView cardsView) {
        this.cardsView = cardsView;
    }
}
