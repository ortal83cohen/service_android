package com.hpe.sb.mobile.app.serverModel.form;

import java.util.List;


public class Form {

    private List<FormSection> formSections;
    private List<FormEnumDescriptor> enumDescriptors;

    public Form() {
    }

    public List<FormEnumDescriptor> getEnumDescriptors() {
        return enumDescriptors;
    }

    public void setEnumDescriptors(List<FormEnumDescriptor> enumDescriptors) {
        this.enumDescriptors = enumDescriptors;
    }

    public List<FormSection> getFormSections() {
        return formSections;
    }

    public void setFormSections(List<FormSection> formSections) {
        this.formSections = formSections;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Form form = (Form) o;

        if (formSections != null ? !formSections.equals(form.formSections) : form.formSections != null)
            return false;
        return enumDescriptors != null ? enumDescriptors.equals(form.enumDescriptors) : form.enumDescriptors == null;

    }

    @Override
    public int hashCode() {
        int result = formSections != null ? formSections.hashCode() : 0;
        result = 31 * result + (enumDescriptors != null ? enumDescriptors.hashCode() : 0);
        return result;
    }
}
