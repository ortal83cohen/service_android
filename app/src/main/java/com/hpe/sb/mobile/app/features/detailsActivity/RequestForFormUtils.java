package com.hpe.sb.mobile.app.features.detailsActivity;

import com.hpe.sb.mobile.app.serverModel.form.FormEnum;
import com.hpe.sb.mobile.app.serverModel.form.FormEnumDescriptor;
import com.hpe.sb.mobile.app.serverModel.form.FormField;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by cohenort on 02/05/2016.
 */
public class RequestForFormUtils {

    public static String getFieldAsString(FormField mGFormField, Object modelAttribute,
                                          List<FormEnumDescriptor> enumDescriptors) {

        switch (mGFormField.getFieldType()) {
            case RICH_TEXT:
            case SMALL_TEXT:
                return (String) modelAttribute;
            case DATE_TIME:
                return DateFormat.getDateTimeInstance()
                        .format(new Date(((Double) modelAttribute).longValue()));
            case ENUM:
                for (FormEnumDescriptor enumDescriptor : enumDescriptors
                        ) {
                    if (enumDescriptor.getId().equals(mGFormField.getReferenceName())) {
                        for (FormEnum mgenum : enumDescriptor.getEnums()
                                ) {
                            if (mgenum.getValue().equals(modelAttribute.toString())) {
                                return mgenum.getDisplayLabel();
                            }
                        }
                    }
                }
            default:
                return "";
        }

    }


}
