package com.hpe.sb.mobile.app.infra.image;

import android.widget.ImageView;
import com.hpe.sb.mobile.app.infra.image.transformations.CircleMaskTransformation;
import com.squareup.picasso.Transformation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.internal.matchers.VarargMatcher;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Created by chovel on 24/04/2016.
 *
 */
public class ImageServiceUtilTest {

    private ImageServiceUtil imageServiceUtil;
    private ImageService imageServiceMock;

    @Before
    public void setup() {
        imageServiceMock = mock(ImageService.class);
        imageServiceUtil = new ImageServiceUtil(imageServiceMock);
    }

    @Test
    public void testCircularTransform() {
        ImageView imageViewMock = mock(ImageView.class);
        imageServiceUtil.loadImageWithCircleMask("testUri", imageViewMock, null, null);
        verify(imageServiceMock).loadImageWithTransformations(eq("testUri"), eq(imageViewMock), isNull(Integer.class), isNull(Integer.class), argThat(new TransformationKeyMatcher(new CircleMaskTransformation())));
    }

    class TransformationKeyMatcher extends ArgumentMatcher<Transformation>  implements VarargMatcher{

        List<String> transformationKeys;

        public TransformationKeyMatcher(Transformation... transformations) {
            transformationKeys = new ArrayList<>();
            for (Transformation transformation : transformations) {
                transformationKeys.add(transformation.key());
            }
        }

        @Override
        public boolean matches(Object argument) {
            if (argument instanceof Transformation[]) {
                Transformation[] transformations = (Transformation[]) argument;
                if (transformations.length != transformationKeys.size()) {
                    return false;
                }
                for (int i = 0; i < transformations.length; i++) {
                    if (!transformations[i].key().equals(transformationKeys.get(i))) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
    }

}
