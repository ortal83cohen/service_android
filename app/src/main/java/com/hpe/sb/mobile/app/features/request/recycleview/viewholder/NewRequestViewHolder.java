package com.hpe.sb.mobile.app.features.request.recycleview.viewholder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import com.hpe.sb.mobile.app.features.request.NewRequestFlowState;
import com.hpe.sb.mobile.app.features.request.event.NewRequestFormEvent;
import com.hpe.sb.mobile.app.features.request.recycleview.type.NewRequestViewType;
import com.hpe.sb.mobile.app.infra.baseActivities.BaseActivity;
import com.hpe.sb.mobile.app.infra.eventbus.EventBus;

/**
 * Created by malikdav on 25/04/2016.
 */
public abstract class NewRequestViewHolder<T extends NewRequestViewType> extends RecyclerView.ViewHolder {
    protected EventBus<NewRequestFormEvent> eventBus;
    public NewRequestViewHolder(View itemView, EventBus<NewRequestFormEvent> eventBus) {
        super(itemView);

        this.eventBus = eventBus;
    }

    public abstract void bind(T newRequestViewType, BaseActivity activity, NewRequestFlowState newRequestFlowState);
}
