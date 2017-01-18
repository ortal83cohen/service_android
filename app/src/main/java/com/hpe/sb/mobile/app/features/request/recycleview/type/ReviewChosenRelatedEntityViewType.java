package com.hpe.sb.mobile.app.features.request.recycleview.type;

import com.hpe.sb.mobile.app.common.dataClients.displaylabels.BadgeProperties;
import com.hpe.sb.mobile.app.common.dataClients.entityDisplay.EntityBadgeService;
import com.hpe.sb.mobile.app.serverModel.catalog.Offering;

/**
 * Created by malikdav on 26/04/2016.
 *
 */
public class ReviewChosenRelatedEntityViewType implements NewRequestViewType {
    private Offering offering;
    private EntityBadgeService entityBadgeService;

    public ReviewChosenRelatedEntityViewType(Offering offering, EntityBadgeService entityBadgeService) {
        this.offering = offering;
        this.entityBadgeService = entityBadgeService;
    }

    public Offering getOffering() {
        return offering;
    }

    public void setOffering(Offering offering) {
        this.offering = offering;
    }

    public BadgeProperties getBadgeProperties() {
        return entityBadgeService.getBadgeProperties(offering.getOfferingType());
    }
}
