package com.hpe.sb.mobile.app.serverModel.form;

import java.util.List;

public class FormSection {

    private String displayLabel;
    private List<FormField> fields;

    public FormSection() {
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public void setDisplayLabel(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public List<FormField> getFields() {
        return fields;
    }

    public void setFields(List<FormField> fields) {
        this.fields = fields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FormSection that = (FormSection) o;

        if (displayLabel != null ? !displayLabel.equals(that.displayLabel) : that.displayLabel != null) return false;
        return fields != null ? fields.equals(that.fields) : that.fields == null;

    }

    @Override
    public int hashCode() {
        int result = displayLabel != null ? displayLabel.hashCode() : 0;
        result = 31 * result + (fields != null ? fields.hashCode() : 0);
        return result;
    }
}
