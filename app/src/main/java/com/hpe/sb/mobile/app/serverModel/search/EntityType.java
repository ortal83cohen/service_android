package com.hpe.sb.mobile.app.serverModel.search;

/**
 * An enum representing a {@link EntityItem} type of searchable entities..
 */
public enum EntityType {

    ARTICLE,

    NEWS,

    SUPPORT_OFFERING,

    SERVICE_OFFERING,

    SUPPORT_REQUEST,

    HUMAN_RESOURCE_OFFERING,

    HR_REQUEST,

    SERVICE_REQUEST,

    /*
    * IMPORTANT: this field should not be used because cart request is not searchable.
    * currently in use only for backward compatibility.
    * */
    CART_REQUEST,

    QUESTION
}
