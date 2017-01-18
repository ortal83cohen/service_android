package com.hpe.sb.mobile.app.features.request.recycleview;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.common.uiComponents.categories.BaseOnCategorySelectListener;
import com.hpe.sb.mobile.app.features.request.NewRequestFlowState;
import com.hpe.sb.mobile.app.features.request.event.NewRequestFormEvent;
import com.hpe.sb.mobile.app.features.request.recycleview.type.NewRequestViewType;
import com.hpe.sb.mobile.app.features.request.recycleview.viewholder.*;
import com.hpe.sb.mobile.app.infra.baseActivities.BaseActivity;
import com.hpe.sb.mobile.app.infra.eventbus.EventBus;
import com.hpe.sb.mobile.app.serverModel.catalog.CatalogGroup;

import java.util.List;

/**
 * Created by malikdav on 25/04/2016.
 */
public class NewRequestViewsAdapter extends RecyclerView.Adapter<NewRequestViewHolder> {

    private final ViewTypeMapping viewTypeMapping;
    private BaseActivity activity;
    private List<NewRequestViewType> viewTypes;
    private NewRequestFlowState newRequestFlowState;
    private EventBus<NewRequestFormEvent> eventBus;

    public NewRequestViewsAdapter(BaseActivity activity, List<NewRequestViewType> viewType, NewRequestFlowState newRequestFlowState) {
        this.activity = activity;
        this.viewTypes = viewType;
        this.newRequestFlowState = newRequestFlowState;
        this.viewTypeMapping = new ViewTypeMapping();
    }

    @Override
    public NewRequestViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ViewTypeMapping.DESCRIBE_REQUEST_VIEW_TYPE: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_request_form_rich_text, parent, false);
                return new DescribeNewRequestViewHolder(view, eventBus);
            }
            case ViewTypeMapping.CHOOSE_RELATED_ENTITY_VIEW_TYPE: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_request_choose_related_entity, parent, false);
                return new ChooseRelatedEntitiesViewHolder(view, eventBus);
            }
            case ViewTypeMapping.CATEGORIES_VIEW_TYPE: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_request_categories_pane, parent, false);
                return new CategoriesViewHolder(view, eventBus, new BaseOnCategorySelectListener(activity) {
                    @Override
                    public boolean handleCategorySelected(CatalogGroup catalogGroup) {
                        if(super.handleCategorySelected(catalogGroup)) {
                            return true;
                        }

                        final NewRequestFormEvent event = new NewRequestFormEvent(NewRequestFormEvent.CHOSE_CATEGORY, catalogGroup.getId());
                        event.setData2(catalogGroup.getTitle());
                        eventBus.send(event);
                        return true;
                    }
                });
            }
            case ViewTypeMapping.CHOOSE_RELATED_ENTITY_IN_CATEGORY_VIEW_TYPE: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_request_choose_related_entity_in_category, parent, false);
                return new ChooseRelatedEntityInCategoryViewHolder(view, eventBus);
            }
            case ViewTypeMapping.REVIEW_CHOSEN_RELATED_ENTITY_VIEW_TYPE: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_request_selected_entity_review, parent, false);
                return new ReviewChosenRelatedEntityViewHolder(view, eventBus);
            }
            case ViewTypeMapping.RICH_TEXT_VIEW_TYPE: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_request_form_rich_text, parent, false);
                return new RichTextViewHolder(view, eventBus);
            }
            case ViewTypeMapping.ENUM_VIEW_TYPE: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_request_form_enum, parent, false);
                return new EnumViewHolder(view, eventBus);
            }
            case ViewTypeMapping.ALMOST_THERE_VIEW_TYPE: {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_request_almost_there, parent, false);
                return new AlmostThereViewHolder(view, eventBus);
            }
            default: {
                String errorMessage = String.format("No matching viewholder for view type: %s", viewType);
                Log.d(getClass().getName(), errorMessage);

                throw new RuntimeException(errorMessage);
            }
        }
    }

    @Override
    public void onBindViewHolder(NewRequestViewHolder holder, int position) {
        holder.bind(viewTypes.get(position), activity, newRequestFlowState);
    }

    @Override
    public int getItemViewType(int position) {
        NewRequestViewType newRequestViewType = viewTypes.get(position);
        return viewTypeMapping.getViewType(newRequestViewType.getClass());
    }

    @Override
    public int getItemCount() {
        return viewTypes.size();
    }

    public void setEventBus(EventBus<NewRequestFormEvent> eventBus) {
        this.eventBus = eventBus;
    }
}
