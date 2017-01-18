package com.hpe.sb.mobile.app.features.request;

import com.hpe.sb.mobile.app.serverModel.form.Form;
import com.hpe.sb.mobile.app.serverModel.form.FormField;
import com.hpe.sb.mobile.app.serverModel.form.FormSection;
import com.hpe.sb.mobile.app.features.request.factory.ViewTypeFactory;
import com.hpe.sb.mobile.app.features.request.recycleview.NewRequestViewsAdapter;
import com.hpe.sb.mobile.app.features.request.recycleview.type.FormSpecificFieldViewType;
import com.hpe.sb.mobile.app.features.request.recycleview.type.NewRequestViewType;
import com.hpe.sb.mobile.app.features.request.scroll.NewRequestScroller;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by malikdav on 15/05/2016.
 */
public class FormFieldDisplayManager {
    private List<FormField> formFields;
    private Form form;
    private List<NewRequestViewType> viewTypes;
    private ViewTypeFactory viewTypeFactory;
    private NewRequestViewsAdapter newRequestViewsAdapter;
    private NewRequestScroller scroller;
    private int formFieldIndex = -1;

    public FormFieldDisplayManager(Form form, List<NewRequestViewType> viewTypes, ViewTypeFactory viewTypeFactory, NewRequestViewsAdapter newRequestViewsAdapter, NewRequestScroller scroller) {
        this.form = form;
        this.viewTypes = viewTypes;
        this.viewTypeFactory = viewTypeFactory;
        this.newRequestViewsAdapter = newRequestViewsAdapter;
        this.scroller = scroller;
        this.formFields = new ArrayList<>();

        populateFormFieldList(form);
    }

    private void populateFormFieldList(Form form) {
        List<FormSection> formSections = form.getFormSections();
        for (FormSection formSection : formSections) {
            for (FormField formField : formSection.getFields()) {
                formFields.add(formField);
            }
        }
    }

    /**
     *
     * @return true if there are more form fields to display
     */
    public boolean hasNext() {
        return !formFields.isEmpty() && (formFields.size() > (formFieldIndex + 1));
    }

    public FormSpecificFieldViewType nextFormField() {
        if(!hasNext()) {
            return null;
        }
        formFieldIndex++;

        FormField formField = formFields.get(formFieldIndex);
        FormSpecificFieldViewType formSpecificFieldViewType = viewTypeFactory.createFormFieldViewType(formField, form.getEnumDescriptors());
        if (formSpecificFieldViewType != null) {
            return formSpecificFieldViewType;
        } else {
            return null;
        }
    }
}
