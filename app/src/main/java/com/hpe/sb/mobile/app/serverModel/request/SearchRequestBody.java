package com.hpe.sb.mobile.app.serverModel.request;


import com.hpe.sb.mobile.app.serverModel.search.EntityType;

import java.util.Set;

/**
 * Created by malikdav on 10/04/2016.
 */
public class SearchRequestBody {

    private String description;
    private Set<EntityType> types;
    private int maxResults;

    public SearchRequestBody() {
    }

    public SearchRequestBody(String description, Set<EntityType> types, int maxResults) {
        this.description = description;
        this.types = types;
        this.maxResults = maxResults;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<EntityType> getTypes() {
        return types;
    }

    public void setTypes(Set<EntityType> types) {
        this.types = types;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }
}
