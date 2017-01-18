package com.hpe.sb.mobile.app.infra.image;

import android.support.annotation.Nullable;
import android.widget.ImageView;
import com.hpe.sb.mobile.app.infra.image.transformations.CircleMaskTransformation;


/**
 * Created by chovel on 19/04/2016.
 *
 */
public class ImageServiceUtil {

    private ImageService imageService;

    public ImageServiceUtil(ImageService imageService) {
        this.imageService = imageService;
    }

    public void loadImageWithCircleMask(String imageUri, ImageView view, @Nullable Integer width, @Nullable Integer height) {
        imageService.loadImageWithTransformations(imageUri, view, width, height, new CircleMaskTransformation());
    }

    public void loadImage(String imageUri, ImageView view, @Nullable Integer width, @Nullable Integer height) {
        imageService.loadImage(imageUri, view, width, height);
    }

}
