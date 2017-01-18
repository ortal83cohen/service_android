package com.hpe.sb.mobile.app.common.utils;

import com.hpe.sb.mobile.app.features.detailsActivity.RequestForFormUtils;
import com.hpe.sb.mobile.app.serverModel.form.FieldType;
import com.hpe.sb.mobile.app.serverModel.form.FormEnum;
import com.hpe.sb.mobile.app.serverModel.form.FormEnumDescriptor;
import com.hpe.sb.mobile.app.serverModel.form.FormField;

import org.junit.Test;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by cohenort on 02/05/2016.
 */
public class RequestForFormUtilsTest {

    FormField mGFormField = new FormField();

    Object modelAttribute;

    @Test
    public void testRichText() {
        mGFormField.setFieldType(FieldType.RICH_TEXT);
        modelAttribute = "string 2";
        String result = RequestForFormUtils
                .getFieldAsString(mGFormField, modelAttribute, null);
        assertEquals("string 2", result);
    }

    @Test
    public void testSmallText() {
        mGFormField.setFieldType(FieldType.SMALL_TEXT);
        modelAttribute = "string 3";
        String result = RequestForFormUtils
                .getFieldAsString(mGFormField, modelAttribute, null);
        assertEquals("string 3", result);
    }

    @Test
    public void testDateTime() {
        mGFormField.setFieldType(FieldType.DATE_TIME);
        modelAttribute = 1.462203361131E12;
        String result = RequestForFormUtils
                .getFieldAsString(mGFormField, modelAttribute, null);
        assertEquals(DateFormat.getDateTimeInstance()
                .format(new Date(((Double) modelAttribute).longValue())), result);
    }

    @Test
    public void testEnum() {
        mGFormField.setFieldType(FieldType.ENUM);
        modelAttribute = "value1";
        List<FormEnumDescriptor> enumDescriptors = new ArrayList<>();
        FormEnumDescriptor descriptor = new FormEnumDescriptor();
        descriptor.setId("id1");
        mGFormField.setReferenceName("id1");
        List<FormEnum> enums = new ArrayList<>();
        FormEnum mGenum = new FormEnum();
        mGenum.setValue("value1");
        mGenum.setDisplayLabel("DisplayLabel1");
        enums.add(mGenum);
        descriptor.setEnums(enums);
        enumDescriptors.add(descriptor);
        String result = RequestForFormUtils
                .getFieldAsString(mGFormField, modelAttribute, enumDescriptors);
        assertEquals("DisplayLabel1", result);
    }
}
