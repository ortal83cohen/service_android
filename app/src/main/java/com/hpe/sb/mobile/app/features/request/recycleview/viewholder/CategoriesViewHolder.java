package com.hpe.sb.mobile.app.features.request.recycleview.viewholder;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import com.hpe.sb.mobile.app.common.uiComponents.categories.OnCategorySelectListener;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.infra.baseActivities.BaseActivity;
import com.hpe.sb.mobile.app.infra.eventbus.EventBus;
import com.hpe.sb.mobile.app.features.request.NewRequestFlowState;
import com.hpe.sb.mobile.app.features.request.event.NewRequestFormEvent;
import com.hpe.sb.mobile.app.features.request.recycleview.type.CategoriesViewType;
import com.hpe.sb.mobile.app.common.uiComponents.categories.CategoryPageRecyclerViewHelper;

/**
 * Created by malikdav on 25/04/2016.
 */
public class CategoriesViewHolder extends NewRequestViewHolder<CategoriesViewType> {

    private final CategoryPageRecyclerViewHelper helper;
    private RecyclerView recycleView;
    private TextView stillCantFindMsg;

    public CategoriesViewHolder(View itemView, final EventBus<NewRequestFormEvent> eventBus, OnCategorySelectListener onCategorySelectListener) {
        super(itemView, eventBus);
        Context context = itemView.getContext();
        ((ServiceBrokerApplication)context.getApplicationContext()).getServiceBrokerComponent().inject(this);

        recycleView = (RecyclerView) itemView.findViewById(R.id.categories_grid);
        recycleView.setNestedScrollingEnabled(false);
        helper = new CategoryPageRecyclerViewHelper(context);
        helper.initRecyclerView(recycleView, true/*isHightlightable*/, onCategorySelectListener);

        stillCantFindMsg = (TextView) itemView.findViewById(R.id.cant_find_entity_in_categories);
    }

    @Override
    public void bind(CategoriesViewType viewType, BaseActivity activity, NewRequestFlowState flowState) {
        helper.updateItems(recycleView, viewType.getCategories());
        if(viewType.isShowStillNotFoundMessage()) {
            stillCantFindMsg.setVisibility(View.VISIBLE);
        } else {
            stillCantFindMsg.setVisibility(View.GONE);
        }
    }
}
