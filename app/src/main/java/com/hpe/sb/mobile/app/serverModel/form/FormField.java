package com.hpe.sb.mobile.app.serverModel.form;

public class FormField {

    public String displayLabel;
    private String modelAttribute;
    private String placeholder;
    private boolean readOnly;

    /**
     * Is the required field
     */
    private boolean required;

    private FieldType fieldType;
    private String referenceName;

    public FormField() {
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public void setDisplayLabel(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public FieldType getFieldType() {
        return fieldType;
    }

    public void setFieldType(FieldType fieldType) {
        this.fieldType = fieldType;
    }

    public String getModelAttribute() {
        return modelAttribute;
    }

    public void setModelAttribute(String modelAttribute) {
        this.modelAttribute = modelAttribute;
    }

    public String getPlaceholder() {
        return placeholder;
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FormField that = (FormField) o;

        if (readOnly != that.readOnly) return false;
        if (displayLabel != null ? !displayLabel.equals(that.displayLabel) : that.displayLabel != null) return false;
        if (modelAttribute != null ? !modelAttribute.equals(that.modelAttribute) : that.modelAttribute != null)
            return false;
        if (placeholder != null ? !placeholder.equals(that.placeholder) : that.placeholder != null) return false;
        if (fieldType != that.fieldType) return false;
        return referenceName != null ? referenceName.equals(that.referenceName) : that.referenceName == null;

    }

    @Override
    public int hashCode() {
        int result = displayLabel != null ? displayLabel.hashCode() : 0;
        result = 31 * result + (modelAttribute != null ? modelAttribute.hashCode() : 0);
        result = 31 * result + (placeholder != null ? placeholder.hashCode() : 0);
        result = 31 * result + (readOnly ? 1 : 0);
        result = 31 * result + (fieldType != null ? fieldType.hashCode() : 0);
        result = 31 * result + (referenceName != null ? referenceName.hashCode() : 0);
        return result;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }
}
