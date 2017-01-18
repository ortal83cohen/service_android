package com.hpe.sb.mobile.app.common.uiComponents.todocards.utils;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

/**
 * Created by salemo on 02/05/2016.
 * TodoCardsEvent that is send on Event Bus
 */
public class TodoCardsEvent {

    /**
     * ALL TodoCardsEvent Types should be define here.
     * add TodoCardsEvent Type by increasing the last TodoCardsEvent Type by 1.
     * **/
    public static final int TODO_CARDS_ACCEPT_SOLUTION_EVENT = 5;
    public static final int TODO_CARDS_REJECT_SOLUTION_EVENT = 6;
    public static final int TODO_CARDS_APPROVE_TASK_EVENT = 7;
    public static final int TODO_CARDS_DENY_TASK_EVENT_START = 8;
    public static final int TODO_CARDS_DENY_TASK_EVENT_END = 9;
    public static final int TODO_CARDS_MARK_AS_SOLVED_EVENT = 10;

    @IntDef({TODO_CARDS_ACCEPT_SOLUTION_EVENT, TODO_CARDS_REJECT_SOLUTION_EVENT,
            TODO_CARDS_APPROVE_TASK_EVENT, TODO_CARDS_DENY_TASK_EVENT_START, TODO_CARDS_DENY_TASK_EVENT_END,
            TODO_CARDS_MARK_AS_SOLVED_EVENT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EventType {}

    private @TodoCardsEvent.EventType int eventType;
    private Map<String,Object> data;

    public TodoCardsEvent(@EventType int eventType, Map<String,Object> data) {
        this.eventType = eventType;
        this.data = data;
    }

    public int getEventType() {
        return eventType;
    }

    public void setEventType(int eventType) {
        this.eventType = eventType;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }
}
