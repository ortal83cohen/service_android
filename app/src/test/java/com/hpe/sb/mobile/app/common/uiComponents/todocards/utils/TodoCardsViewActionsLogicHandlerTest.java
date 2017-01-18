package com.hpe.sb.mobile.app.common.uiComponents.todocards.utils;

import android.content.Context;
import android.view.View;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.ServiceBrokerComponent;
import com.hpe.sb.mobile.app.common.dataClients.user.UserClient;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.TodoCardsView;
import com.hpe.sb.mobile.app.features.request.RequestClient;
import com.hpe.sb.mobile.app.infra.baseActivities.BaseActivity;
import com.hpe.sb.mobile.app.infra.eventbus.EventBus;
import com.hpe.sb.mobile.app.serverModel.user.useritems.*;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import rx.Observable;
import rx.Observer;
import rx.observers.TestSubscriber;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by oded on 14/07/2016.
 *
 */
public class TodoCardsViewActionsLogicHandlerTest {


    private TodoCardsViewActionsLogicHandler todoCardsViewActionsLogicHandler;
    private RequestClient requestClient;
    private UserClient userClient;
    private EventBus eventBus;
    private BaseActivity activity;
    private TodoCardsView cardsView;


    @Before
    public void setUp() {
        activity = mock(BaseActivity.class);
        ServiceBrokerApplication applicationMock = mock(ServiceBrokerApplication.class);
        when(activity.getApplicationContext()).thenReturn(applicationMock);
        ServiceBrokerComponent serviceBrokerComponentMock = mock(ServiceBrokerComponent.class);
        when(applicationMock.getServiceBrokerComponent()).thenReturn(serviceBrokerComponentMock);
        cardsView = mock(TodoCardsView.class);
        todoCardsViewActionsLogicHandler = new TodoCardsViewActionsLogicHandler(activity, cardsView, null);
        requestClient = mock(RequestClient.class);
        userClient = mock(UserClient.class);
        eventBus = new EventBus();
        todoCardsViewActionsLogicHandler.setRequestClient(requestClient);
        todoCardsViewActionsLogicHandler.setUserClient(userClient);
        todoCardsViewActionsLogicHandler.setEventBus(eventBus);
        todoCardsViewActionsLogicHandler.initCardsView();
    }

    @Test
    public void testAcceptSolutionEvent() {
        UserItems userItems = mock(UserItems.class);
        Observable<UserItems> userItemsObservable = Observable.just(userItems);
        when(requestClient.acceptRequest(any(Context.class), anyString())).thenReturn(userItemsObservable);
        testCardEvent(TodoCardsEvent.TODO_CARDS_ACCEPT_SOLUTION_EVENT, userItemsObservable, userItems);
        verify(requestClient).acceptRequest(activity, "entityId");
        verify(requestClient, never()).rejectRequest(activity, "entityId");
        verify(cardsView).setCardsList(any(List.class));
    }

    @Test
    public void testRejectSolutionEvent() {
        UserItems userItems = mock(UserItems.class);
        Observable<UserItems> userItemsObservable = Observable.just(userItems);
        when(requestClient.rejectRequest(any(Context.class), anyString())).thenReturn(userItemsObservable);
        testCardEvent(TodoCardsEvent.TODO_CARDS_REJECT_SOLUTION_EVENT, userItemsObservable, userItems);
        verify(requestClient).rejectRequest(activity, "entityId");
        verify(requestClient, never()).acceptRequest(activity, "entityId");
        verify(cardsView).setCardsList(any(List.class));
    }

    @Test
    public void testApproveEvent() {
        UserItems userItems = mock(UserItems.class);
        Observable<UserItems> userItemsObservable = Observable.just(userItems);
        when(userClient.approveTask(any(Context.class), anyString())).thenReturn(userItemsObservable);
        testCardEvent(TodoCardsEvent.TODO_CARDS_APPROVE_TASK_EVENT, userItemsObservable, userItems);
        verify(userClient).approveTask(activity, "entityId");
        verify(userClient, never()).denyTask(activity, "entityId", "comment");
        verify(cardsView).setCardsList(any(List.class));
    }

    @Test
    public void testDenyEvent() {
        UserItems userItems = mock(UserItems.class);
        Observable<UserItems> userItemsObservable = Observable.just(userItems);
        when(userClient.denyTask(any(Context.class), anyString(), anyString())).thenReturn(userItemsObservable);
        testCardEvent(TodoCardsEvent.TODO_CARDS_DENY_TASK_EVENT_END, userItemsObservable, userItems);
        verify(userClient).denyTask(activity, "entityId", "comment");
        verify(userClient, never()).approveTask(activity, "entityId");
        verify(cardsView).setCardsList(any(List.class));
    }

    @Test
    public void testMarkAsSolvedEvent() {
        UserItems userItems = mock(UserItems.class);
        Observable<UserItems> userItemsObservable = Observable.just(userItems);
        when(requestClient.acceptRequest(any(Context.class), anyString())).thenReturn(userItemsObservable);
        testCardEvent(TodoCardsEvent.TODO_CARDS_MARK_AS_SOLVED_EVENT, userItemsObservable, userItems);
        verify(requestClient).acceptRequest(activity, "entityId");
        verify(requestClient, never()).rejectRequest(activity, "entityId");
        verify(cardsView).setCardsList(any(List.class));
    }

    private void testCardEvent(int todoCardsAcceptSolutionEvent, Observable<UserItems> userItemsObservable, UserItems userItems) {
        ArrayList<RequestActiveUserItem> requestActiveItems = new ArrayList<>();
        requestActiveItems.add(new RequestActiveUserItem());
        TodoCardsEvent todoCardsEvent = mock(TodoCardsEvent.class);
        when(todoCardsEvent.getEventType()).thenReturn(todoCardsAcceptSolutionEvent);
        Map<String, Object> cardData = new HashMap<>();
        cardData.put(TodoCardsViewActionsLogicHandler.ENTITY_DATA_ID, "entityId");
        cardData.put(TodoCardsViewActionsLogicHandler.COMMENT_DATA, "comment");
        when(todoCardsEvent.getData()).thenReturn(cardData);
        eventBus.send(todoCardsEvent);

        TestSubscriber<UserItems> testSubscriber = new TestSubscriber<>();
        userItemsObservable.subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertValueCount(1);
        List<UserItems> onNextEventsUserItems = testSubscriber.getOnNextEvents();
        UserItems expectedUserItems = onNextEventsUserItems.get(0);
        Assert.assertEquals(expectedUserItems, userItems);
    }

    @Test
    public void testRefreshCardsView() {
        UserItems userItems = mock(UserItems.class);
        Observable<UserItems> userItemsObservable = Observable.just(userItems);
        when(userClient.getUserItems(activity, false)).thenReturn(userItemsObservable);
        todoCardsViewActionsLogicHandler.refreshCardsView(false);
        TestSubscriber<UserItems> testSubscriber = new TestSubscriber<>();
        userItemsObservable.subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertValueCount(1);
        List<UserItems> onNextEventsUserItems = testSubscriber.getOnNextEvents();
        UserItems expectedUserItems = onNextEventsUserItems.get(0);
        Assert.assertEquals(expectedUserItems, userItems);
        verify(userClient).getUserItems(activity, false);
        cardsView = todoCardsViewActionsLogicHandler.getCardsView();
        verify(cardsView).measure(anyInt(), anyInt());
        verify(cardsView).setCardsList(any(List.class));
    }

    @Test
    public void testCreateEventAndAddData() {
        View view = mock(View.class);
        View buttonPanel = mock(View.class);
        String entityId = "entityId";
        TodoCardsEvent todoCardsEvent = TodoCardsViewActionsLogicHandler.createEvent(entityId, TodoCardsEvent.TODO_CARDS_ACCEPT_SOLUTION_EVENT,
                view, buttonPanel);
        Assert.assertEquals(todoCardsEvent.getEventType(), TodoCardsEvent.TODO_CARDS_ACCEPT_SOLUTION_EVENT);
        Map<String, Object> data = todoCardsEvent.getData();
        Assert.assertEquals(data.get(TodoCardsViewActionsLogicHandler.ENTITY_DATA_ID), entityId);
        Assert.assertEquals(TodoCardsViewActionsLogicHandler.getEventViewData(data), view);
        Assert.assertEquals(TodoCardsViewActionsLogicHandler.getButtonPanelData(data), buttonPanel);
        Assert.assertEquals(data.get(TodoCardsViewActionsLogicHandler.COMMENT_DATA), null);
        String comment = "comment";
        TodoCardsViewActionsLogicHandler.addAdditionalDataToEvent(todoCardsEvent, TodoCardsViewActionsLogicHandler.COMMENT_DATA, comment);
        Assert.assertEquals(data.get(TodoCardsViewActionsLogicHandler.COMMENT_DATA), comment);

    }

    @Test
    public void testUserItemsHandler() {
        cardsView = mock(TodoCardsView.class);
        todoCardsViewActionsLogicHandler.setCardsView(cardsView);
        UserItems userItems = mock(UserItems.class);
        List<RequestActiveUserItem> activeRequests = new ArrayList<>();
        RequestActiveUserItem activeRequest = mock(RequestActiveUserItem.class);
        activeRequests.add(activeRequest);
        when(userItems.getActiveRequests()).thenReturn(activeRequests);
        List<RequestResolveUserItem> requestResolveItems = new ArrayList<>();
        RequestResolveUserItem requestResolved = mock(RequestResolveUserItem.class);
        requestResolveItems.add(requestResolved);
        when(userItems.getRequestResolveItems()).thenReturn(requestResolveItems);
        List<ApprovalUserItem> approvals = new ArrayList<>();
        ApprovalUserItem approvalItem = mock(ApprovalUserItem.class);
        approvals.add(approvalItem);
        when(userItems.getApprovals()).thenReturn(approvals);
        List<RequestFeedbackUserItem> feedbackItems = new ArrayList<>();
        RequestFeedbackUserItem requestFeedbackItem = mock(RequestFeedbackUserItem.class);
        feedbackItems.add(requestFeedbackItem);
        when(userItems.getRequestFeedbackItems()).thenReturn(feedbackItems);
        Observable<UserItems> userItemsObservable = Observable.just(userItems);
        Observer<UserItems> userItemsHandler = todoCardsViewActionsLogicHandler.getUserItemsHandler(false);
        userItemsObservable.subscribe(userItemsHandler);

        TestSubscriber<UserItems> testSubscriber = new TestSubscriber<>();
        userItemsObservable.subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();
        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertValueCount(1);
        List<UserItems> onNextEventsUserItems = testSubscriber.getOnNextEvents();
        UserItems expectedUserItems = onNextEventsUserItems.get(0);
        Assert.assertEquals(expectedUserItems, userItems);
        verify(expectedUserItems).getApprovals();
        verify(expectedUserItems).getActiveRequests();
        verify(expectedUserItems).getRequestFeedbackItems();
        verify(expectedUserItems).getRequestResolveItems();
        verify(cardsView).setCardsList(any(List.class));
    }
}
