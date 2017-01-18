package com.hpe.sb.mobile.app.serverModel.form;

public class FormEnum {

    private String displayLabel;
    private String value;
    private String iconImageId;

    public FormEnum() {
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public void setDisplayLabel(String displayLabel) {
        this.displayLabel = displayLabel;
    }

    public String getIconImageId() {
        return iconImageId;
    }

    public void setIconImageId(String iconImageId) {
        this.iconImageId = iconImageId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FormEnum formEnum = (FormEnum) o;

        if (displayLabel != null ? !displayLabel.equals(formEnum.displayLabel) : formEnum.displayLabel != null)
            return false;
        if (value != null ? !value.equals(formEnum.value) : formEnum.value != null) return false;
        return iconImageId != null ? iconImageId.equals(formEnum.iconImageId) : formEnum.iconImageId == null;

    }

    @Override
    public int hashCode() {
        int result = displayLabel != null ? displayLabel.hashCode() : 0;
        result = 31 * result + (value != null ? value.hashCode() : 0);
        result = 31 * result + (iconImageId != null ? iconImageId.hashCode() : 0);
        return result;
    }
}
