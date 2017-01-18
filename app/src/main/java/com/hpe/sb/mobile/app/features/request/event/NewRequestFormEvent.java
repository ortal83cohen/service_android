package com.hpe.sb.mobile.app.features.request.event;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by malikdav on 26/04/2016.
 */
public class NewRequestFormEvent {

    public static final int DESCRIBED_YOUR_REQUEST = 0;
    public static final int CHOSE_CATEGORY = 1;
    public static final int CHOSE_RELATED_ENTITY = 2;
    public static final int REVIEWED_SELECTED_ENTITY = 3;
    public static final int NEXT_FORM_FIELD = 4;
    public static final int SUBMIT_REQUEST = 5;
    public static final int FINISH_LOADING = 6;

    @IntDef({DESCRIBED_YOUR_REQUEST, CHOSE_RELATED_ENTITY, REVIEWED_SELECTED_ENTITY, NEXT_FORM_FIELD, SUBMIT_REQUEST, CHOSE_CATEGORY, FINISH_LOADING})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EventType {}

    private @NewRequestFormEvent.EventType int eventType;
    private String data;
    private String data2;

    public NewRequestFormEvent(@EventType int eventType, String data) {
        this.eventType = eventType;
        this.data = data;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData2() {
        return data2;
    }

    public void setData2(String data2) {
        this.data2 = data2;
    }
}
