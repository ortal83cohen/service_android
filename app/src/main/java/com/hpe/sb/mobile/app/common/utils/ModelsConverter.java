package com.hpe.sb.mobile.app.common.utils;

import com.hpe.sb.mobile.app.serverModel.catalog.Offering;
import com.hpe.sb.mobile.app.serverModel.search.EntityItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by salemo on 28/04/2016.
 */
public class ModelsConverter {

    public List<EntityItem> convertOfferingsToEntityItems(List<Offering> offerings) {
        List<EntityItem> entityItems = new ArrayList<>();
        if (offerings == null || offerings.isEmpty()) {
            return entityItems;
        }
        for (Offering offering : offerings) {
            entityItems.add(new EntityItem(offering.getId(), offering.getOfferingType(), offering.getTitle(),
                    offering.getDescription(), offering.getImageId(), offering.isPopular(), offering.getCost(),
                    offering.getCostCurrency(), offering.isSupportableOnMobile()));
        }
        return entityItems;
    }
}
