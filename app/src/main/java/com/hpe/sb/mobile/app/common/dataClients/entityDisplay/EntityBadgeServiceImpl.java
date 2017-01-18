package com.hpe.sb.mobile.app.common.dataClients.entityDisplay;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.common.dataClients.displaylabels.BadgeProperties;
import com.hpe.sb.mobile.app.common.dataClients.displaylabels.DisplayLabelService;
import com.hpe.sb.mobile.app.serverModel.search.EntityType;

/**
 * Created by salemo on 30/03/2016.
 * service that holds the metadata localized key, used for a synchronize flow in the app.
 * we retrieve the data once at the initial data flow of the app
 */
public class EntityBadgeServiceImpl implements EntityBadgeService {

    private final Context context;
    private DisplayLabelService displayLabelService;

    public EntityBadgeServiceImpl(DisplayLabelService displayLabelService, Context context) {
        this.displayLabelService = displayLabelService;
        this.context = context;
    }

    @Override
    public BadgeProperties getBadgeProperties(EntityType entityType) {
        BadgeProperties badgeProperties = new BadgeProperties();
        switch (entityType) {
            case HUMAN_RESOURCE_OFFERING:
                setHROfferingBadge(badgeProperties);
                break;
            case SUPPORT_OFFERING:
                setSupportOfferingBadge(badgeProperties);
                break;
            case SERVICE_OFFERING:
                setServiceOfferingBadge(badgeProperties);
                break;
            case ARTICLE:
                setArticleBadge(badgeProperties);
                break;
            /*
            * not implement yet, who ever needs to use needs to implement
            * */
            case SUPPORT_REQUEST:
            case SERVICE_REQUEST:
            case HR_REQUEST:
            case CART_REQUEST:
            case QUESTION:
            default:
                Log.e(getClass().getName(), String.format("Badge for type %s is not implemented yet", entityType.name()));
                setSupportOfferingBadge(badgeProperties);
        }
        return badgeProperties;
    }


    private void setSupportOfferingBadge(BadgeProperties badgeProperties) {
        badgeProperties.setDrawable(ContextCompat.getDrawable(context, R.drawable.support_badge));
        badgeProperties.setBackgroundColor(ContextCompat.getColor(context, R.color.offering_support_type_color));
        badgeProperties.setText(displayLabelService.getOfferingLabels().getOfferingTypeToDisplayLabel().get(EntityType.SUPPORT_OFFERING).toUpperCase());
    }

    private void setHROfferingBadge(BadgeProperties badgeProperties) {
        badgeProperties.setDrawable(ContextCompat.getDrawable(context, R.drawable.support_badge));
        badgeProperties.setBackgroundColor(ContextCompat.getColor(context, R.color.offering_support_type_color));
        badgeProperties.setText(displayLabelService.getOfferingLabels().getOfferingTypeToDisplayLabel().get(EntityType.HUMAN_RESOURCE_OFFERING).toUpperCase());
    }

    private void setServiceOfferingBadge(BadgeProperties badgeProperties) {
        badgeProperties.setDrawable(ContextCompat.getDrawable(context, R.drawable.service_badge));
        badgeProperties.setBackgroundColor(ContextCompat.getColor(context, R.color.offering_service_type_color));
        badgeProperties.setText(displayLabelService.getOfferingLabels().getOfferingTypeToDisplayLabel().get(EntityType.SERVICE_OFFERING).toUpperCase());

    }

    private void setArticleBadge(BadgeProperties badgeProperties) {
        badgeProperties.setDrawable(ContextCompat.getDrawable(context, R.drawable.article_badge));
        badgeProperties.setBackgroundColor(ContextCompat.getColor(context, R.color.related_entities_article_badge_color));
        badgeProperties.setText((String) context.getResources().getText(R.string.article_badge_text));
    }
}