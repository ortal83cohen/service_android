package com.hpe.sb.mobile.app.common.uiComponents.entityIcon;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.infra.image.ImageServiceUtil;
import com.hpe.sb.mobile.app.serverModel.search.EntityType;

import javax.inject.Inject;

/**
 * Created by malikdav on 24/04/2016.
 *
 */
public class EntityIconImageView extends ImageView {

    @Inject
    ImageServiceUtil imageUtil;

    public EntityIconImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if(isInEditMode()) {
            loadDefaultEntityIcon(EntityType.ARTICLE);
        } else {
            ((ServiceBrokerApplication) context.getApplicationContext()).getServiceBrokerComponent().inject(this);
        }
    }

    /**
     * Set the icon image for a given image id, in the image id is empty or null, then the icon will be set to the default entity type icon
     *
     * @param imageId This path may be a remote URL, file resource (prefixed with {@code file:}), content resource
     * (prefixed with {@code content:}), or android resource (prefixed with {@code
     * android.resource:}. Can also be null
     * @param entityType in case the image id is null or empty string the default entityType icon will be used
     */
    public void setImage(String imageId, EntityType entityType) {
        if (imageId == null || imageId.equals("")) {
            loadDefaultEntityIcon(entityType);
        }
        else {
            loadEntityIcon(imageId);
        }
    }

    private void modifyViewChange() {
        invalidate(); /*inform a need to redraw*/
        requestLayout(); /*inform the layout should be recalculated due to a view size change*/
    }

    private void loadDefaultEntityIcon(EntityType entityType) {
        this.setImageResource(getDefaultIconResource(entityType));
    }

    private void loadEntityIcon(String imageId) {
            imageUtil.loadImage(imageId, this, null, null);
        // TODO:Dudi- Do we need to invalidate and requestLayout after picasso - or it is already doing so?

    }

    private int getDefaultIconResource(EntityType type) {
        switch (type) {
            case ARTICLE:
                return R.drawable.articles_default_icon;
            case HUMAN_RESOURCE_OFFERING:
            case SUPPORT_OFFERING:
            case SERVICE_OFFERING:
                return R.drawable.offerings_default_icon;
            case NEWS:
                return R.drawable.icon_news;
            /*
            * not implement yet, who ever needs to use needs to implement
            * */
            case SUPPORT_REQUEST:
            case SERVICE_REQUEST:
            case HR_REQUEST:
            case CART_REQUEST:
            case QUESTION:
            default: {
                Log.e(getClass().getName(), String.format("Missing default icon for type :%s", type.name()));

                return 0;
            }
        }
    }
}
