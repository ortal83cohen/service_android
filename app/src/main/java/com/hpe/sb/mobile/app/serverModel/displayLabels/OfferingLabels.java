package com.hpe.sb.mobile.app.serverModel.displayLabels;

import com.hpe.sb.mobile.app.serverModel.search.EntityType;

import java.util.Map;

/**
 * Created by salemo on 14/06/2016.
 *
 */
public class OfferingLabels {

    private Map<EntityType, String> offeringTypeToDisplayLabel;

    public Map<EntityType, String> getOfferingTypeToDisplayLabel() {
        return offeringTypeToDisplayLabel;
    }

    public void setOfferingTypeToDisplayLabel(Map<EntityType, String> offeringTypeToDisplayLabel) {
        this.offeringTypeToDisplayLabel = offeringTypeToDisplayLabel;
    }
}
