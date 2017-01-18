package com.hpe.sb.mobile.app.features.request.recycleview.type;

import com.hpe.sb.mobile.app.serverModel.form.FormField;

/**
 * Created by malikdav on 28/04/2016.
 */
public class RichTextViewType implements FormSpecificFieldViewType {

    private FormField formField;

    public RichTextViewType() {
    }

    public RichTextViewType(FormField formField) {
        this.formField = formField;
    }


    public FormField getFormField() {
        return formField;
    }

    public void setFormField(FormField formField) {
        this.formField = formField;
    }
}
