package com.hpe.sb.mobile.app.infra.image;

import com.hpe.sb.mobile.app.infra.restclient.HttpLookupUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyListOf;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


public class ImageServiceTest {

    private HttpLookupUtils httpLookupUtils;

    private ImageView imageViewMock;

    private Picasso picassoMock;

    private ImageServiceImpl imageService;

    private ArgumentCaptor<String> finalUrlCaptor;

    private RequestCreator requestCreatorMock;

    @Before
    public void setup() {
        httpLookupUtils = mock(HttpLookupUtils.class);
        finalUrlCaptor = ArgumentCaptor.forClass(String.class);
        picassoMock =
                mockPicasso(finalUrlCaptor);
        imageService = new ImageServiceImpl(httpLookupUtils, picassoMock);
        imageViewMock = mock(ImageView.class);
    }

    @Test
    public void testLoadFrsImage() {
        when(httpLookupUtils.getBaseRestUrl(imageService.getClientPrefix()))
                .thenReturn("my_test_url");
        imageService.loadImage("my_frs_resource", imageViewMock, null, null);
        Assert.assertEquals("my_test_url/my_frs_resource", finalUrlCaptor.getValue());
        verify(httpLookupUtils).getBaseRestUrl(imageService.getClientPrefix());
        verify(requestCreatorMock).into(imageViewMock);
    }

    @Test
    public void testLoadStaticImage() {
        when(httpLookupUtils.getBaseUrl()).thenReturn("my_test_url");
        imageService.loadImage("/js/modules/image.png", imageViewMock, null, null);
        Assert.assertEquals("my_test_url//js/modules/image.png", finalUrlCaptor.getValue());
        verify(httpLookupUtils).getBaseUrl();
        verify(requestCreatorMock).into(imageViewMock);
    }

    @Test
    public void testLoadStaticWithSize() {
        when(httpLookupUtils.getBaseUrl()).thenReturn("my_test_url");
        imageService.loadImage("/js/modules/image.png", imageViewMock, 20, 20);
        Assert.assertEquals("my_test_url//js/modules/image.png", finalUrlCaptor.getValue());
        verify(httpLookupUtils).getBaseUrl();
        verify(requestCreatorMock).into(imageViewMock);
    }

    @Test
    public void testLoadFrsImageWithSize() {
        when(httpLookupUtils.getBaseRestUrl(imageService.getClientPrefix()))
                .thenReturn("my_test_url");
        imageService.loadImage("my_frs_resource", imageViewMock, 300, null);
        Assert.assertEquals("my_test_url/my_frs_resource/?width=300", finalUrlCaptor.getValue());
        verify(httpLookupUtils).getBaseRestUrl(imageService.getClientPrefix());
        verify(requestCreatorMock).into(imageViewMock);
    }

    @Test
    public void testLoadFrsImageWithWidth() {
        when(httpLookupUtils.getBaseRestUrl(imageService.getClientPrefix()))
                .thenReturn("my_test_url");
        imageService.loadImage("my_frs_resource", imageViewMock, 300, 100);
        Assert.assertEquals("my_test_url/my_frs_resource/?width=300&height=100",
                finalUrlCaptor.getValue());
        verify(httpLookupUtils).getBaseRestUrl(imageService.getClientPrefix());
        verify(requestCreatorMock).into(imageViewMock);
    }

    @Test
    public void testLoadFrsImageIntoTarget() {
        when(httpLookupUtils.getBaseRestUrl(imageService.getClientPrefix()))
                .thenReturn("my_test_url");
        final Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };
        imageService.loadImage("my_frs_resource", target, 300, 100);
        Assert.assertEquals("my_test_url/my_frs_resource/?width=300&height=100",
                finalUrlCaptor.getValue());
        verify(httpLookupUtils).getBaseRestUrl(imageService.getClientPrefix());
        verify(requestCreatorMock).into(target);
    }

    @Test
    public void testLoadFrsImageWithHeightOnly() {
        when(httpLookupUtils.getBaseRestUrl(imageService.getClientPrefix()))
                .thenReturn("my_test_url");
        when(picassoMock.load(anyInt())).thenReturn(requestCreatorMock);
        imageService.loadImage("my_frs_resource", imageViewMock, null, 100);
        verify(httpLookupUtils).getBaseRestUrl(imageService.getClientPrefix());
        verify(requestCreatorMock).into(imageViewMock);
        verify(picassoMock).load(anyInt());
    }

    @Test
    public void testLoadImageWithTransformations() {
        when(httpLookupUtils.getBaseRestUrl(imageService.getClientPrefix()))
                .thenReturn("my_test_url");
        when(picassoMock.load(anyInt())).thenReturn(requestCreatorMock);
        when(requestCreatorMock.transform(anyListOf(Transformation.class)))
                .thenReturn(requestCreatorMock);
        imageService.loadImageWithTransformations("my_frs_resource", imageViewMock, null, null,
                mock(Transformation.class), mock(Transformation.class));
        verify(requestCreatorMock).transform(anyListOf(Transformation.class));
    }

    Picasso mockPicasso(ArgumentCaptor<String> captor) {
        picassoMock = mock(Picasso.class);
        requestCreatorMock = mock(RequestCreator.class);
        when(requestCreatorMock.placeholder(anyInt())).thenReturn(requestCreatorMock);
        when(requestCreatorMock.error(anyInt())).thenReturn(requestCreatorMock);
        when(picassoMock.load(captor.capture())).thenReturn(requestCreatorMock);
        doNothing().when(requestCreatorMock).into(any(ImageView.class));
        return picassoMock;
    }
}