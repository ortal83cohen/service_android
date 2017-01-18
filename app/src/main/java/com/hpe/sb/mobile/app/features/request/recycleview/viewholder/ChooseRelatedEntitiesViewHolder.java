package com.hpe.sb.mobile.app.features.request.recycleview.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.infra.baseActivities.BaseActivity;
import com.hpe.sb.mobile.app.infra.eventbus.EventBus;
import com.hpe.sb.mobile.app.features.request.NewRequestFlowState;
import com.hpe.sb.mobile.app.features.request.event.NewRequestFormEvent;
import com.hpe.sb.mobile.app.features.request.helper.NewRequestSelectionHandler;
import com.hpe.sb.mobile.app.features.request.recycleview.type.ChooseRelatedEntityViewType;
import com.hpe.sb.mobile.app.common.uiComponents.relatedEntities.RelatedEntitiesRecyclerViewHelper;

/**
 * Created by malikdav on 25/04/2016.
 */
public class ChooseRelatedEntitiesViewHolder extends NewRequestViewHolder<ChooseRelatedEntityViewType> {

    private RecyclerView recycleView;
    private RelatedEntitiesRecyclerViewHelper helper;

    public ChooseRelatedEntitiesViewHolder(View itemView, EventBus<NewRequestFormEvent> eventBus) {
        super(itemView, eventBus);

        recycleView = (RecyclerView) itemView.findViewById(R.id.related_entities);

        helper = new RelatedEntitiesRecyclerViewHelper(itemView.getContext());
        helper.initRecyclerView(recycleView, true, null, new NewRequestSelectionHandler(eventBus));
    }

    @Override
    public void bind(ChooseRelatedEntityViewType viewType, BaseActivity activity, NewRequestFlowState flowState) {
        helper.updateItems(recycleView, viewType.getRelatedEntities());
    }
}
