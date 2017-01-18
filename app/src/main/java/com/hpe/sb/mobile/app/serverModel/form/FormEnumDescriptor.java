package com.hpe.sb.mobile.app.serverModel.form;

import java.util.List;

public class FormEnumDescriptor {

    private String id;
    private String displayLabel;
    private List<FormEnum> enums;

    public FormEnumDescriptor() {
    }

    public List<FormEnum> getEnums() {
        return enums;
    }

    public void setEnums(List<FormEnum> enums) {
        this.enums = enums;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public void setDisplayLabel(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FormEnumDescriptor that = (FormEnumDescriptor) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (displayLabel != null ? !displayLabel.equals(that.displayLabel) : that.displayLabel != null) return false;
        return enums != null ? enums.equals(that.enums) : that.enums == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (displayLabel != null ? displayLabel.hashCode() : 0);
        result = 31 * result + (enums != null ? enums.hashCode() : 0);
        return result;
    }
}
