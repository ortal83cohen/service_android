package com.hpe.sb.mobile.app.common.uiComponents.relatedEntities;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.common.dataClients.displaylabels.BadgeProperties;
import com.hpe.sb.mobile.app.serverModel.search.EntityItem;
import com.hpe.sb.mobile.app.common.uiComponents.entityBadge.EntityBadgeView;
import com.hpe.sb.mobile.app.common.uiComponents.entityIcon.EntityIconImageView;

import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by malikdav on 19/04/2016.
 * holds the row view of related entities list
 */
public class RelatedEntitiesListViewHolder extends RecyclerView.ViewHolder {
    private TextView title;
    private TextView description;
    private TextView cost;
    private EntityBadgeView badge;
    private EntityIconImageView entityIcon;
    private ImageView popularImage;
    private ImageView selectedImage;

    public RelatedEntitiesListViewHolder(View itemView) {
        super(itemView);
        this.title = (TextView) itemView.findViewById(R.id.entity_title);
        this.description = (TextView) itemView.findViewById(R.id.description);
        this.cost = (TextView) itemView.findViewById(R.id.cost);
        this.badge = (EntityBadgeView) itemView.findViewById(R.id.badge);
        this.entityIcon = (EntityIconImageView) itemView.findViewById(R.id.entity_icon);
        this.popularImage = (ImageView) itemView.findViewById(R.id.popular_icon);
        this.selectedImage = (ImageView) itemView.findViewById(R.id.selected_image);
    }

    public void bind(EntityItem entityItem, Context context, BadgeProperties badgeProperties) {
        cleanHolderOptionalFields();
        this.title.setText(entityItem.getTitle());
        this.itemView.setContentDescription(entityItem.getTitle());
        this.description.setText(entityItem.getPreview());
        this.badge.setBadge(badgeProperties);

        if (entityItem.isPopularity()) {
            this.popularImage.setVisibility(View.VISIBLE);
        }

        this.entityIcon.setImage(entityItem.getImageId(), entityItem.getType());

        if (entityItem.getCost() > 0) {
            String currencySymbol = entityItem.getCostCurrency() != null ? convertToCurrencySymbol(entityItem.getCostCurrency()) : "$";
            try {
                this.cost.setText(String.valueOf(NumberFormat.getNumberInstance(Locale.US).format(entityItem.getCost()) + " " + currencySymbol));
            } catch (Exception e) {
                Log.e(getClass().getName(), String.format("could not set cost of entity with id: %s, cost is: %s, currencySymbol is: %s msg: %s",
                        entityItem.getId(), entityItem.getCost(), currencySymbol, e.getMessage()));
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

    //clean optional fields because the item is recycling an old item!
    private void cleanHolderOptionalFields() {
        this.badge.setText("");
        this.cost.setText("");
        this.popularImage.setVisibility(View.GONE);
    }

    private String convertToCurrencySymbol(String currency) {
        switch (currency) {
            case "USD":
                return "$";
            case "EUR":
                return "€";
            case "GDP":
                return "£";
            case "BRL":
                return "R$";
            case "CAD":
                return "$";
            case "CNY":
                return "¥";
            case "INR":
                return "र";
            case "ILS":
                return "₪";
            case "JPY":
                return "¥";
            case "KRW":
                return "₩";
            case "MXN":
                return "$";
            case "RUB":
                return "руб";
            default:
                return "$";
        }
    }
}