package com.hpe.sb.mobile.app.common.dataClients.comments;

import android.content.Context;
import com.hpe.sb.mobile.app.infra.restclient.RestService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by mufler on 08/05/2016.
 */
@RunWith(MockitoJUnitRunner.class)
public class CommentsRestClientImplTest {
    @Mock
    private RestService restServiceMock;

    @InjectMocks
    private CommentsRestClientImpl testedObject;
    @Before
    public void setUp() throws Exception {

    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testAddComment() throws Exception {
        final ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        //when(restServiceMock.createPostRequest(captor.capture(), anyObject(), Void.class, any(Context.class))).thenReturn(mock(Observable.class));
        final Context contextMock = mock(Context.class);
        testedObject.addComment(contextMock, "Offering", "12345", "Its a comment");
        final String expectedUrl = "/comments/addComment/Offering/12345";
        verify(restServiceMock).createPostRequest(captor.capture(),  eq("Its a comment"), eq(Void.class), eq(contextMock));
        final String capturedUrl = captor.getValue();
        assertEquals(expectedUrl, capturedUrl);
    }
}