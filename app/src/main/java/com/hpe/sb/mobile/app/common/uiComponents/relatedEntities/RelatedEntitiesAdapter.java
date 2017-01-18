package com.hpe.sb.mobile.app.common.uiComponents.relatedEntities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.common.dataClients.entityDisplay.EntityBadgeService;
import com.hpe.sb.mobile.app.serverModel.search.EntityItem;

import java.util.List;

import javax.inject.Inject;

/**
 * Created by malikdav on 17/04/2016.
 */
public class RelatedEntitiesAdapter extends RecyclerView.Adapter<RelatedEntitiesListViewHolder> {

    private Context context;
    private List<EntityItem> itemList;
    private int selectedPosition = -1;
    private String selectedEntityId = null;

    private OnEntityItemSelectedListener itemSelectedListener;
    private RelatedEntitiesListViewHolder previouslySelectedHolder = null;
    private boolean isHightlightable;

    @Inject
    public EntityBadgeService entityBadgeService;

    public RelatedEntitiesAdapter(Context context) {
        ((ServiceBrokerApplication) context.getApplicationContext()).getServiceBrokerComponent()
                .inject(this);
        this.context = context;
    }

    @Override
    public RelatedEntitiesListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.entities_list_row, parent, false);
        return new RelatedEntitiesListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RelatedEntitiesListViewHolder holder, final int position) {
        EntityItem entityItem = itemList.get(position);
        markSelectedItem(holder, position);
        holder.bind(entityItem, context, entityBadgeService.getBadgeProperties(entityItem.getType()));
        onItemClick(holder, position, entityItem);
    }

    @Override
    public void onViewRecycled(RelatedEntitiesListViewHolder holder) {
        super.onViewRecycled(holder);
    }

    @Override
    public void onViewAttachedToWindow(RelatedEntitiesListViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    private void markSelectedItem(RelatedEntitiesListViewHolder holder, int position) {
        if(isHightlightable) {
            EntityItem entityItem = itemList.get(position);
            if(selectedPosition == position){
                //we don't want to select the offering only according to index, cause the category may be changed by the user
                //and in such case we select other offering by mistake
                //in order to verify it is the same offering, we check in addition the entity id
                //(19530 - [Sep 16] Mobile The Offerings are selected when user did not select it- scenario when using device back or scroll)
                if(entityItem.getId().equals(selectedEntityId)){
                    holder.setSelected(true);
                }else{
                    selectedPosition = -1;
                    holder.setSelected(false);
                }
            }else{
                holder.setSelected(false);
            }
        }
    }

    private void onItemClick(final RelatedEntitiesListViewHolder holder, final int position, final EntityItem entityItem) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle selection decoration
                if(isHightlightable) {
                    if (previouslySelectedHolder != null) {
                        previouslySelectedHolder.setSelected(false);
                    }
                    notifyItemChanged(selectedPosition);
                    selectedPosition = position;
                    selectedEntityId = entityItem.getId();
                    holder.setSelected(true);
                    notifyItemChanged(selectedPosition);
                    previouslySelectedHolder = holder;
                }
                // Perform the selection action
                itemSelectedListener.call(entityItem);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public void setItemList(List<EntityItem> itemList) {
        this.itemList = itemList;
    }

    public void addToRelatedEntitiesToList(List<EntityItem> relatedEntitiesList) {
        this.itemList.addAll(relatedEntitiesList);
        notifyDataSetChanged();
    }

    public void setItemSelectedListener(OnEntityItemSelectedListener itemSelectedListener) {
        this.itemSelectedListener = itemSelectedListener;
    }

    public void setIsHightlightable(boolean isHightlightable) {
        this.isHightlightable = isHightlightable;
    }
}
