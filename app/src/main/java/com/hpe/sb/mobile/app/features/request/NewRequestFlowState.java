package com.hpe.sb.mobile.app.features.request;

import com.hpe.sb.mobile.app.serverModel.catalog.Offering;
import com.hpe.sb.mobile.app.serverModel.request.FullRequest;

/**
 * Created by malikdav on 28/04/2016.
 */
public class NewRequestFlowState {

    private FullRequest fullRequest;
    private String requestDescriptionCandidate;
    private Offering selectedOffering = null;

    public NewRequestFlowState() {
    }

    public FullRequest getFullRequest() {
        return fullRequest;
    }

    public void setFullRequest(FullRequest fullRequest) {
        this.fullRequest = fullRequest;
    }

    public String getRequestDescriptionCandidate() {
        return requestDescriptionCandidate;
    }

    public void setRequestDescriptionCandidate(String requestDescriptionCandidate) {
        this.requestDescriptionCandidate = requestDescriptionCandidate;
    }

    public Offering getSelectedOffering() {
        return selectedOffering;
    }

    public void setSelectedOffering(Offering selectedOffering) {
        this.selectedOffering = selectedOffering;
    }
}
