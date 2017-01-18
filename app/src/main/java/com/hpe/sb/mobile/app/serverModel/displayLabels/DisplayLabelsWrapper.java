package com.hpe.sb.mobile.app.serverModel.displayLabels;

import com.hpe.sb.mobile.app.infra.dataClients.Persistable;

/**
 * Created by salemo on 14/06/2016.
 *
 */
public class DisplayLabelsWrapper implements Persistable {


    public static final String DISPLAY_LABELS_WRAPPER_STATIC_ID = "displayLabelsWrapper";
    OfferingLabels offeringTypeLabels;

    public OfferingLabels getOfferingTypeLabels() {
        return offeringTypeLabels;
    }

    public void setOfferingTypeLabels(OfferingLabels offeringTypeLabels) {
        this.offeringTypeLabels = offeringTypeLabels;
    }

    @Override
    public String getId() {
        return DISPLAY_LABELS_WRAPPER_STATIC_ID;
    }

    @Override
    public int getChecksum() {
        return hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        DisplayLabelsWrapper that = (DisplayLabelsWrapper) o;

        return offeringTypeLabels != null ? offeringTypeLabels.equals(that.offeringTypeLabels) : that.offeringTypeLabels == null;

    }

    @Override
    public int hashCode() {
        return offeringTypeLabels != null ? offeringTypeLabels.hashCode() : 0;
    }
}
