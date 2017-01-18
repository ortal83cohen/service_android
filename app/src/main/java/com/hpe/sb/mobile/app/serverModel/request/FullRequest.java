package com.hpe.sb.mobile.app.serverModel.request;

import com.hpe.sb.mobile.app.serverModel.comment.Comment;
import com.hpe.sb.mobile.app.serverModel.search.EntityType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FullRequest {

    private Map<String, Object> properties;
    private List<Comment> comments;
    private String offeringId;
    private EntityType offeringType;

    public FullRequest() {
        properties = new HashMap<>();
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }

    public void setProperty(String property, Object value) {
        this.properties.put(property, value);
    }

    public Object getProperty(String property) {
        return this.properties.get(property);
    }

    public String getOfferingId() {
        return offeringId;
    }

    public EntityType getOfferingType() {
        return offeringType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FullRequest that = (FullRequest) o;

        if (properties != null ? !properties.equals(that.properties) : that.properties != null) return false;
        if (offeringId != null ? !offeringId.equals(that.offeringId) : that.offeringId != null) return false;
        if (offeringType != null ? !offeringType.equals(that.offeringType) : that.offeringType != null) return false;
        return comments != null ? comments.equals(that.comments) : that.comments == null;

    }

    @Override
    public int hashCode() {
        int result = properties != null ? properties.hashCode() : 0;
        result = 31 * result + (comments != null ? comments.hashCode() : 0);
        result = 31 * result + (offeringId != null ? offeringId.hashCode() : 0);
        result = 31 * result + (offeringType != null ? offeringType.hashCode() : 0);
        return result;
    }

    public void setOfferingId(String offeringId) {
        this.offeringId = offeringId;
    }

    public void setOfferingType(EntityType offeringType) {
        this.offeringType = offeringType;
    }
}
