package com.hpe.sb.mobile.app.common.uiComponents.categories.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.hpe.sb.mobile.app.common.uiComponents.categories.OnCategorySelectListener;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.common.dataClients.catalog.restClient.CatalogRestClient;
import com.hpe.sb.mobile.app.common.uiComponents.categories.view.CategoryCardView;
import com.hpe.sb.mobile.app.infra.image.ImageService;
import com.hpe.sb.mobile.app.serverModel.catalog.CatalogGroup;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by mufler on 27/04/2016.
 */
public class CategoriesViewAdapter extends RecyclerView.Adapter<CategoriesViewAdapter.CategoryViewHolder>{
    private static final String TAG = CategoriesViewAdapter.class.getSimpleName();

    @Inject
    public CatalogRestClient catalogRestClient;

    @Inject
    public ImageService imageService;

    private List<CatalogGroup> itemList;
    private int selectedPosition = -1;
    private OnCategorySelectListener itemSelectedListener;
    private boolean isHightlightable;


    public CategoriesViewAdapter(Context context) {
        ((ServiceBrokerApplication) context.getApplicationContext()).getServiceBrokerComponent().inject(this);
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        CategoryCardView v = (CategoryCardView) LayoutInflater.from(parent.getContext()).inflate(R.layout.category_card, parent, false);
        final CategoryViewHolder categoryViewHolder = new CategoryViewHolder(v);
        v.setOnClickListener(categoryViewHolder);
        return categoryViewHolder;
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        CatalogGroup entityItem = itemList.get(position);
        markSelectedItem(holder, position);
        holder.bind(entityItem);
        onItemClick(holder, position, entityItem);
    }

    private void markSelectedItem(CategoryViewHolder holder, int position) {
        if(isHightlightable) {
            holder.setSelected(selectedPosition == position);
        }
    }

    private void onItemClick(final CategoryViewHolder holder, final int position, final CatalogGroup entityItem) {
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle selection decoration
                if(isHightlightable) {
                    if(position != selectedPosition) {
                        notifyItemChanged(selectedPosition);
                        notifyItemChanged(position);
                        selectedPosition = position;
                    }
                }
                // Perform the selection action
                itemSelectedListener.handleCategorySelected(entityItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(itemList == null) {
            return 0;
        }
        return itemList.size();
    }

    public void setItemList(List<CatalogGroup> itemList) {
        this.itemList = itemList;
    }

    public void setIsHightlightable(boolean isHightlightable) {
        this.isHightlightable = isHightlightable;
    }

    public void setItemSelectedListener(OnCategorySelectListener itemSelectedListener) {
        this.itemSelectedListener = itemSelectedListener;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private CategoryCardView categoryCard;
        private ImageView icon;
        private TextView title;
        private ImageView selectedImage;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            this.categoryCard = (CategoryCardView) itemView.findViewById(R.id.category_card);
            this.icon = (ImageView) itemView.findViewById(R.id.category_image);
            this.title = (TextView) itemView.findViewById(R.id.category_title);
            this.selectedImage = (ImageView) itemView.findViewById(R.id.selected_image);
        }

        void bind(CatalogGroup categoryData){
            this.categoryCard.setContentDescription(categoryData.getTitle());
            this.title.setText(categoryData.getTitle());

            final String backgroundColor = categoryData.getBackgroundColor();
            int color = Color.BLACK;
            if (backgroundColor != null) {
                try {
                    color = Color.parseColor(backgroundColor);
                }catch(Exception e){
                    Log.e(TAG, "Failed to parse color " + backgroundColor + "; set black");
                }
            }
            this.categoryCard.setCardBackgroundColor(color);
            if (categoryData.getIconImageId() != null) {
                try {
                    imageService.loadImage(categoryData.getIconImageId(), this.icon, null, null);//todo:mufler set size
                } catch (Exception | Error e) {
                    Log.e(TAG, "Failed to set category icon", e);
                }
            }
        }

        public void setSelected(boolean isSelected) {
            itemView.setSelected(isSelected);
            if(isSelected) {
                selectedImage.setVisibility(View.VISIBLE);
            } else {
                selectedImage.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            final int layoutPosition = getLayoutPosition();
            final CatalogGroup catalogGroup = itemList.get(layoutPosition);

            if(itemSelectedListener != null){
                itemSelectedListener.handleCategorySelected(catalogGroup);
            }
        }
    }
}
