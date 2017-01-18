package com.hpe.sb.mobile.app.serverModel.form;

/**
 * An enumeration of supported field types.
 * Marker for {@link com.hpe.sb.mobile.app.serverModel.form.FieldType}.
 */
public enum FieldType {

    /**
     * Means the field logically represents a small text value.
     */
    SMALL_TEXT(String.class, 140),

    /**
     * Means the field logically represents a medium text value.
     */
    MEDIUM_TEXT(String.class, 500),

    /**
     * Means the field logically represents a large text value.
     */
    LARGE_TEXT(String.class, 1000000),

    /**
     * Means the field logically represents a rich text value.
     */
    RICH_TEXT(String.class, 1000000),

    /**
     * Means the field logically represents email address.
     */
    EMAIL(String.class, 254),

    /**
     * Means the field logically represents a url address.
     */
    URL(String.class, 2048),

    /**
     * Means the field logically represents a image uri.
     */
    IMAGE(String.class, 2048),

    /**
     * Means the field logically represents an integer value.
     */
    INTEGER(Integer.class),

    /**
     * Means the field logically represents a double value.
     */
    DOUBLE(Double.class),

    /**
     * Means the field logically represents a percentage (double) value.
     */
    PERCENTAGE(Double.class),

    /**
     * Means the field logically represents a boolean value.
     */
    BOOLEAN(Boolean.class),

    /**
     * Means the field logically represents a date value.
     */
    DATE(Long.class),

    /**
     * Means the field logically represents some date & time value.
     */
    DATE_TIME(Long.class),

    /**
     * Means the field represents a value of some enumeration.
     * Actual value will be the value of some {@link com.hpe.mobile.gateway.model.form.MGEnumDescriptor}.
     */
    ENUM(String.class);

    private final Class javaApiType;
    private final Integer maximumLength;

    FieldType(Class javaApiType) {
        this(javaApiType, null);
    }

    FieldType(Class javaApiType, Integer maximumLength) {
        this.javaApiType = javaApiType;
        this.maximumLength = maximumLength;
    }

    public Class getJavaApiType() {
        return javaApiType;
    }

    public Integer getMaximumLength() {
        return maximumLength;
    }

}
