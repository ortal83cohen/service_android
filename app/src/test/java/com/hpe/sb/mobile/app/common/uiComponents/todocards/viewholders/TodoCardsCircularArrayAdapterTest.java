package com.hpe.sb.mobile.app.common.uiComponents.todocards.viewholders;

import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.ServiceBrokerComponent;
import com.hpe.sb.mobile.app.common.services.dateTime.DateTimeService;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.adapter.TodoCardsCircularArrayAdapter;
import com.hpe.sb.mobile.app.infra.image.ImageServiceUtil;
import com.hpe.sb.mobile.app.infra.eventbus.EventBus;
import com.hpe.sb.mobile.app.serverModel.request.RequestType;
import com.hpe.sb.mobile.app.serverModel.user.useritems.ApprovalUserItem;
import com.hpe.sb.mobile.app.serverModel.user.useritems.RequestActiveUserItem;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.TodoCardData;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.utils.TodoCardsEvent;

import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by chovel on 24/04/2016.
 */
public class TodoCardsCircularArrayAdapterTest {

    private TodoCardsCircularArrayAdapter todoCardsArrayAdapter;

    private LayoutInflater layoutInflaterMock;


    @Before
    public void setUp() {
        Context contextMock = mock(Context.class);
        ServiceBrokerApplication applicationMock = mock(ServiceBrokerApplication.class);
        when(contextMock.getApplicationContext()).thenReturn(applicationMock);
        ServiceBrokerComponent serviceBrokerComponentMock = mock(ServiceBrokerComponent.class);
        when(applicationMock.getServiceBrokerComponent()).thenReturn(serviceBrokerComponentMock);
        todoCardsArrayAdapter = new TodoCardsCircularArrayAdapter(contextMock, 0,
                new ArrayList<TodoCardData>(), new EventBus<TodoCardsEvent>());
        layoutInflaterMock = mock(LayoutInflater.class);
        todoCardsArrayAdapter.setLayoutInflater(layoutInflaterMock);
        ImageServiceUtil imageServiceUtilMock = mock(ImageServiceUtil.class);
        todoCardsArrayAdapter.setImageServiceUtil(imageServiceUtilMock);
    }

    @Test
    public void testGetCount() {
        //item list is empty
        assertEquals(0, todoCardsArrayAdapter.getCount());

        //one item
        List<TodoCardData> items = new ArrayList<>();
        items.add(getTestActiveRequestTodoData());
        todoCardsArrayAdapter.setTodoCardItems(items);
        assertEquals(1, todoCardsArrayAdapter.getCount());

        //more than one item
        items.add(getTestActiveRequestTodoData());
        todoCardsArrayAdapter.setTodoCardItems(items);
        assertEquals(Integer.MAX_VALUE, todoCardsArrayAdapter.getCount());
    }

    @Test
    public void testGetItem() {
        //item list is empty
        assertNull(todoCardsArrayAdapter.getItem(0));

        //position is not bigger than items size
        List<TodoCardData> items = new ArrayList<>();
        items.add(getTestActiveRequestTodoData());
        todoCardsArrayAdapter.setTodoCardItems(items);

        //when position is bigger, circular behavior
        //5%1 = 0
        assertEquals(items.get(0), todoCardsArrayAdapter.getItem(5));

        //5%2 = 1
        items.add(getTestActiveRequestTodoData());
        todoCardsArrayAdapter.setTodoCardItems(items);
        assertEquals(items.get(1), todoCardsArrayAdapter.getItem(5));
    }

    @Test
    public void testTypeCount() {
        assertEquals(todoCardsArrayAdapter.getViewTypeCount(), TodoCardData.getTypeCount());
    }

    @Test
    public void testGetItemViewType() {
        //no item at position - when items are empty
        assertEquals(todoCardsArrayAdapter.getItemViewType(0),
                TodoCardsCircularArrayAdapter.ITEM_TYPE_DEFAULT);

        //normal behavior
        List<TodoCardData> items = new ArrayList<>();
        items.add(getTestActiveRequestTodoData());
        items.add(getTestApprovalItemTodoData());
        todoCardsArrayAdapter.setTodoCardItems(items);
        assertEquals(TodoCardData.getViewTypeIndex(items.get(0).getModelClass()),
                todoCardsArrayAdapter.getItemViewType(0));
        //5%2 == 1
        assertEquals(TodoCardData.getViewTypeIndex(items.get(1).getModelClass()),
                todoCardsArrayAdapter.getItemViewType(5));
    }

    @Test
    public void testGetView() {
        //testing view holder pattern
        List<TodoCardData> items = new ArrayList<>();
        items.add(getTestActiveRequestTodoData());
        items.add(getTestApprovalItemTodoData());
        todoCardsArrayAdapter.setTodoCardItems(items);

        ViewGroup viewGroupMock = mock(ViewGroup.class);
        View mockActiveRequestCardView = mock(RelativeLayout.class);
        when(layoutInflaterMock.inflate(R.layout.active_request_card, viewGroupMock, false))
                .thenReturn(mockActiveRequestCardView);
        TextView textViewMock = mock(TextView.class);
        //mock ActiveRequestCardViewHolder constructor
        when(mockActiveRequestCardView.findViewById(anyInt())).
                thenReturn(mockActiveRequestCardView).
                thenReturn(mockActiveRequestCardView).
                thenReturn(textViewMock).
                thenReturn(textViewMock).
                thenReturn(textViewMock).
                thenReturn(null).
                thenReturn(mock(LinearLayout.class)).
                thenReturn(textViewMock).
                thenReturn(textViewMock).
                thenReturn(mock(FrameLayout.class)).
                thenReturn(textViewMock);
        Context contextMock = mock(Context.class);
        ServiceBrokerApplication serviceBrokerApplicationMock = mock(ServiceBrokerApplication.class);
        when(mockActiveRequestCardView.getContext()).thenReturn(contextMock);
        when(contextMock.getApplicationContext()).thenReturn(serviceBrokerApplicationMock);

        ServiceBrokerComponent serviceBrokerComponentMock = mock(ServiceBrokerComponent.class);
        doAnswer(new Answer<Void>() {
            public Void answer(InvocationOnMock invocation) {
                Object[] arguments = invocation.getArguments();
                ActiveRequestCardViewHolder activeRequestCardViewHolder = (ActiveRequestCardViewHolder) arguments[0];
                activeRequestCardViewHolder.setDateTimeService(mock(DateTimeService.class));
                return null;
            }
        }).when(serviceBrokerComponentMock).inject(any(ActiveRequestCardViewHolder.class));

        when(serviceBrokerApplicationMock.getServiceBrokerComponent()).thenReturn(serviceBrokerComponentMock);

        //create new view holder
        View initialView = todoCardsArrayAdapter.getView(0, null, viewGroupMock);
        verify(layoutInflaterMock).inflate(R.layout.active_request_card, viewGroupMock, false);
        verify(initialView).setTag(any(CardViewHolder.class));
        assertEquals(mockActiveRequestCardView, initialView);

        //recycle
        CardViewHolder mockViewHolder = mock(CardViewHolder.class);
        when(initialView.getTag()).thenReturn(mockViewHolder);
        todoCardsArrayAdapter.getView(0, initialView, viewGroupMock);
        verify(initialView).getTag();
        verify(mockViewHolder).bind(items.get(0).getData(RequestActiveUserItem.class));
    }

    private TodoCardData getTestApprovalItemTodoData() {
        return new TodoCardData(
                new ApprovalUserItem("desc", "id", "type", "name", "avatar", "task", "title", "title",
                        System.currentTimeMillis()), ApprovalUserItem.class);
    }

    private TodoCardData getTestActiveRequestTodoData() {
        return new TodoCardData(
                new RequestActiveUserItem("id", "title", RequestType.SERVICE, "phase", "Validation", "name", "desc",
                        System.currentTimeMillis()), RequestActiveUserItem.class);
    }

}
