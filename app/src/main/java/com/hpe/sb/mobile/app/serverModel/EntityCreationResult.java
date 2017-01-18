package com.hpe.sb.mobile.app.serverModel;


/**
 * Created by malikdav on 05/05/2016.
 */
public class EntityCreationResult {

    private CompletionStatus completionStatus;
    private String entityId;


    public EntityCreationResult(CompletionStatus completionStatus, String entityId) {
        this.completionStatus = completionStatus;
        this.entityId = entityId;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public CompletionStatus getCompletionStatus() {
        return completionStatus;
    }

    public void setCompletionStatus(CompletionStatus completionStatus) {
        this.completionStatus = completionStatus;
    }
}
