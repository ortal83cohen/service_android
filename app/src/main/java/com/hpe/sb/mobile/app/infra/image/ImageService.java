package com.hpe.sb.mobile.app.infra.image;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import rx.Observable;

/**
 * Service for downloading images from the server side -
 * ngnix server or file repository service in the case of SAW.
 *
 */
public interface ImageService {
    /**
     * @param imageUri this methods accepts URI of 2 types:
     *                 - image guid in FRS
     *                 - url of static resource in ngnix according to pattern /js/module
     * @param view     the view which will contain the image after downloading
     * @param width
     * @param height   width and height parameters can be null, so the server will not resize and will return original image
     *                 as it was stored in image service.
     */
    void loadImage(String imageUri, ImageView view, @Nullable Integer width, @Nullable Integer height);

    /**
     * Has the same function as loadImage(String imageUri, ImageView view, @Nullable Integer width, @Nullable Integer height)
     * but applies the given transformations (in the order they are given) on the loaded image before setting it on the image view
     *
     * @param imageUri see loadImage
     * @param view see loadImage
     * @param width see loadImage
     * @param height see loadImage
     * @param transformations the transformations to apply on the loaded image in order
     */
    void loadImageWithTransformations(String imageUri, ImageView view, @Nullable Integer width, @Nullable Integer height, Transformation... transformations);

    void loadImage(String imageUri, Target target, @Nullable Integer width, @Nullable Integer height);

    /**
     * Same as #loadImage(android.content.Context, java.lang.String, android.widget.ImageView)
     * which in addition accepts resource id as placeholder for the image till its loaded.
     * @param imageUri
     * @param view
     * @param width
     * @param height
     * @param placeHolderImageId
     */
    void loadImageWithPlaceHolder(String imageUri, ImageView view, @Nullable Integer width, @Nullable Integer height, int placeHolderImageId);

    /**
     * Loads an image and does nothing. This will save the image on the device and possibly on memory.
     */
    Observable<Void> prefetchImage(String imageUri, Picasso.Priority priority, @Nullable Integer width, @Nullable Integer height);

}
