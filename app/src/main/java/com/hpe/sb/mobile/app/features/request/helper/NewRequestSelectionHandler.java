package com.hpe.sb.mobile.app.features.request.helper;

import com.hpe.sb.mobile.app.infra.eventbus.EventBus;
import com.hpe.sb.mobile.app.serverModel.search.EntityItem;
import com.hpe.sb.mobile.app.features.request.event.NewRequestFormEvent;
import com.hpe.sb.mobile.app.common.uiComponents.relatedEntities.OnEntityItemSelectedListener;

/**
 * Created by gabaysh on 18/05/2016.
 */
public class NewRequestSelectionHandler implements OnEntityItemSelectedListener {

    private EventBus<NewRequestFormEvent> eventBus;

    public NewRequestSelectionHandler(EventBus<NewRequestFormEvent> eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void call(EntityItem entityItem) {
        eventBus.send(new NewRequestFormEvent(NewRequestFormEvent.CHOSE_RELATED_ENTITY, entityItem.getId()));
    }

    public void setEventBus(EventBus<NewRequestFormEvent> eventBus) {
        this.eventBus = eventBus;
    }
}
