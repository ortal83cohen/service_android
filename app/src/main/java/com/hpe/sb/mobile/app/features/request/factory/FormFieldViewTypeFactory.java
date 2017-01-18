package com.hpe.sb.mobile.app.features.request.factory;

import android.util.Log;
import com.hpe.sb.mobile.app.serverModel.form.FieldType;
import com.hpe.sb.mobile.app.serverModel.form.FormEnumDescriptor;
import com.hpe.sb.mobile.app.serverModel.form.FormField;
import com.hpe.sb.mobile.app.features.request.recycleview.type.EnumViewType;
import com.hpe.sb.mobile.app.features.request.recycleview.type.FormSpecificFieldViewType;
import com.hpe.sb.mobile.app.features.request.recycleview.type.RichTextViewType;

import java.util.List;

/**
 * Created by malikdav on 28/04/2016.
 */
public class FormFieldViewTypeFactory {

    public FormSpecificFieldViewType createViewType(FormField formField, List<FormEnumDescriptor> enumDescriptors) {
        FieldType fieldType = formField.getFieldType();
        switch (fieldType) {
            case RICH_TEXT:
                return new RichTextViewType(formField);
            case ENUM:
                return getEnumViewType(formField, enumDescriptors);
            case SMALL_TEXT:
            case MEDIUM_TEXT:
            case LARGE_TEXT:
            case EMAIL:
            case URL:
            case IMAGE:
            case INTEGER:
            case DOUBLE:
            case PERCENTAGE:
            case BOOLEAN:
            case DATE:
            case DATE_TIME:
                default: {
                    Log.e(this.getClass().getName(), String.format("No Form implementation for form field of type %s", fieldType.name()));
                    return null;
                }
        }
    }

    private EnumViewType getEnumViewType(FormField formField, List<FormEnumDescriptor> enumDescriptors) {
        if(enumDescriptors == null) {
            Log.e(this.getClass().getName(), String.format("Enum descriptors cant be null for form field of type: %s", formField.getFieldType().name()));
            return null;
        }

        for (FormEnumDescriptor enumDescriptor : enumDescriptors) {
            if (enumDescriptor.getId().equals(formField.getReferenceName())) {
                return new EnumViewType(formField, enumDescriptors);
            }
        }

        Log.e(this.getClass().getName(), String.format("Missing enum descriptor for reference name: %s", formField.getReferenceName()));
        return null;
    }
}
