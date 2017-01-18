package com.hpe.sb.mobile.app.features.request.recycleview.type;

import android.util.Log;
import com.hpe.sb.mobile.app.serverModel.form.FormEnumDescriptor;
import com.hpe.sb.mobile.app.serverModel.form.FormField;

import java.util.List;

/**
 * Created by malikdav on 12/05/2016.
 */
public class EnumViewType implements FormSpecificFieldViewType {
    private FormField formField;
    private List<FormEnumDescriptor> enumDescriptors;

    public EnumViewType(FormField formField, List<FormEnumDescriptor> enumDescriptors) {
        if(formField.getReferenceName() == null) {
            Log.e(this.getClass().getName(), "Enum form field cant have null reference type");
        }

        this.formField = formField;
        this.enumDescriptors = enumDescriptors;
    }

    public FormField getFormField() {
        return formField;
    }

    public List<FormEnumDescriptor> getEnumDescriptors() {
        return enumDescriptors;
    }
}
