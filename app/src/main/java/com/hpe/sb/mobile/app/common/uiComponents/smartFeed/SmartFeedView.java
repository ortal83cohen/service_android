package com.hpe.sb.mobile.app.common.uiComponents.smartFeed;

import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.common.dataClients.entityDisplay.EntityBadgeService;
import com.hpe.sb.mobile.app.serverModel.search.EntityItem;
import com.hpe.sb.mobile.app.serverModel.user.Feed;
import com.hpe.sb.mobile.app.serverModel.user.FeedGroup;
import com.hpe.sb.mobile.app.common.uiComponents.relatedEntities.RelatedEntitiesListViewHolder;
import com.hpe.sb.mobile.app.common.uiComponents.relatedEntities.RelatedEntitiesRecyclerViewHelper;
import com.hpe.sb.mobile.app.common.uiComponents.relatedEntities.RelatedEntitySelectionHandler;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import javax.inject.Inject;

/**
 * Created by chovel on 11/04/2016.
 */
public class SmartFeedView extends LinearLayout {

    @Inject
    public EntityBadgeService entityBadgeService;

    public SmartFeedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        ((ServiceBrokerApplication) context.getApplicationContext()).getServiceBrokerComponent()
                .inject(this);
        setOrientation(LinearLayout.VERTICAL);
    }

    public void refreshFeed(Feed feed) {
        removeAllViewsInLayout();

        for (FeedGroup feedGroups : feed.getFeedGroups()) {
            if (feedGroups.getEntityItems().size() != 0) {
                View view = LayoutInflater
                        .from(getContext()).inflate(R.layout.feed_card, this, false);
                RelatedEntitiesRecyclerViewHelper recyclerViewUtil
                        = new RelatedEntitiesRecyclerViewHelper(getContext());
                LinearLayout entityContainer = (LinearLayout) view
                        .findViewById(R.id.entity_container);
                entityContainer.setContentDescription(feedGroups.getTitle());
                View divider = null;
                for (final EntityItem entityItem : feedGroups.getEntityItems()) {

                    View entitiesListRow = LayoutInflater.from(getContext())
                            .inflate(R.layout.entities_list_row, entityContainer, false);
                    RelatedEntitiesListViewHolder viewHolder = new RelatedEntitiesListViewHolder(
                            entitiesListRow);
                    viewHolder.bind(entityItem, getContext(), entityBadgeService.getBadgeProperties(entityItem.getType()));
                    final RelatedEntitySelectionHandler relatedEntitySelectionHandler
                            = new RelatedEntitySelectionHandler(getContext());
                    entitiesListRow.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            relatedEntitySelectionHandler.call(entityItem);
                        }
                    });

                    entityContainer.addView(entitiesListRow);

                    divider = LayoutInflater.from(getContext())
                            .inflate(R.layout.divider, entityContainer, false);
                    entityContainer.addView(divider);
                }
                if (divider != null) {
                    entityContainer.removeView(divider);
                }
                TextView title = (TextView) view.findViewById(android.R.id.title);

                title.setText(feedGroups.getTitle());

                addView(view);
            }
        }
    }

    public int getItemCount() {
        return getChildCount();
    }
}
