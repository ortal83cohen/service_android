package com.hpe.sb.mobile.app.serverModel;

import com.hpe.sb.mobile.app.infra.dataClients.Persistable;

/**
 * A data model for a TenantSettings.
 */
public class TenantSettings implements Persistable {

    public enum OfferingInRequestPolicy {
        MANDATORY,
        OPTIONAL,
        IGNORE,
        SKIP
    }

    private OfferingInRequestPolicy offeringInRequestPolicy;

    private boolean isMobileSupportedOnTenant;
	
    /**
     * Empty constractor needs for POJO tests
     */
    public TenantSettings() {
    }

    @Override
    public String getId() {
        return "TenantSettings";
    }

    @Override
    public int getChecksum() {
        return 0;
    }

    public OfferingInRequestPolicy getOfferingInRequestPolicy() {
        return offeringInRequestPolicy;
    }

    public void setOfferingInRequestPolicy(OfferingInRequestPolicy offeringInRequestPolicy) {
        this.offeringInRequestPolicy = offeringInRequestPolicy;
    }

    public boolean isMobileSupportedOnTenant() {
        return isMobileSupportedOnTenant;
    }

    public void setMobileSupportedOnTenant(boolean mobileSupportedOnTenant) {
        isMobileSupportedOnTenant = mobileSupportedOnTenant;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TenantSettings that = (TenantSettings) o;

        if (isMobileSupportedOnTenant != that.isMobileSupportedOnTenant) {
            return false;
        }
        return offeringInRequestPolicy != null ? offeringInRequestPolicy == that.offeringInRequestPolicy : that.offeringInRequestPolicy == null;

    }

    @Override
    public int hashCode() {
        int result = offeringInRequestPolicy != null ? offeringInRequestPolicy.hashCode() : 0;
        result = 31 * result + (isMobileSupportedOnTenant ? 1 : 0);
        return result;
    }
}
