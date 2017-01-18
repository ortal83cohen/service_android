package com.hpe.sb.mobile.app.serverModel.request;

public class RequestForTrackingPage {

    private String id;
    private String title;
    private String description;
    private String status;
    private RequestForForm requestForForm;
    private String phase;
    private String metaPhase;
    private String requestedForName;
    private long creationTime;

    public RequestForTrackingPage() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public RequestForForm getRequestForForm() {
        return requestForForm;
    }

    public void setPhase(String phase) {
        this.phase = phase;
    }

    public void setMetaPhase(String metaPhase) {
        this.metaPhase = metaPhase;
    }

    public void setRequestedForName(String requestedForName) {
        this.requestedForName = requestedForName;
    }

    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    public String getPhase() {
        return phase;
    }


    public String getMetaPhase() {
        return metaPhase;
    }

    public String getRequestedForName() {
        return requestedForName;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public void setRequestForForm(RequestForForm requestForForm) {
        this.requestForForm = requestForForm;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RequestForTrackingPage that = (RequestForTrackingPage) o;

        if (creationTime != that.creationTime) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (description != null ? !description.equals(that.description) : that.description != null) return false;
        if (status != null ? !status.equals(that.status) : that.status != null) return false;
        if (requestForForm != null ? !requestForForm.equals(that.requestForForm) : that.requestForForm != null)
            return false;
        if (phase != null ? !phase.equals(that.phase) : that.phase != null) return false;
        if (metaPhase != null ? !metaPhase.equals(that.metaPhase) : that.metaPhase != null) return false;
        return requestedForName != null ? requestedForName.equals(that.requestedForName) : that.requestedForName == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (description != null ? description.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (requestForForm != null ? requestForForm.hashCode() : 0);
        result = 31 * result + (phase != null ? phase.hashCode() : 0);
        result = 31 * result + (metaPhase != null ? metaPhase.hashCode() : 0);
        result = 31 * result + (requestedForName != null ? requestedForName.hashCode() : 0);
        result = 31 * result + (int) (creationTime ^ (creationTime >>> 32));
        return result;
    }
}
