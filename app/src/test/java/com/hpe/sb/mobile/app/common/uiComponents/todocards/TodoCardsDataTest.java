package com.hpe.sb.mobile.app.common.uiComponents.todocards;

import com.hpe.sb.mobile.app.serverModel.user.useritems.ApprovalUserItem;
import com.hpe.sb.mobile.app.serverModel.user.useritems.RequestActiveUserItem;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

/**
 * Created by chovel on 24/04/2016.
 *
 */
public class TodoCardsDataTest {

    @Test
    public void testGetTypeCount() {
        assertEquals(TodoCardData.classList.size(), TodoCardData.getTypeCount());
    }

    @Test
    public void testGetViewTypeIndex() {
        for (Class clazz : TodoCardData.classList) {
            assertTrue(TodoCardData.getViewTypeIndex(clazz) != -1);
        }
    }

    @Test
    public void testGetData() {
        //unsupported class
        TodoCardData todoCardData = new TodoCardData("test", String.class);
        assertNull(todoCardData.getData(RequestActiveUserItem.class));
        assertNull(todoCardData.getData(String.class));

        //supported class
        RequestActiveUserItem mockItem = mock(RequestActiveUserItem.class);
        todoCardData = new TodoCardData(mockItem, RequestActiveUserItem.class);
            //correct class
        assertEquals(mockItem, todoCardData.getData(RequestActiveUserItem.class));
            //not supported
        assertNull(todoCardData.getData(String.class));
            //not assignable
        assertNull(todoCardData.getData(ApprovalUserItem.class));
    }

}
