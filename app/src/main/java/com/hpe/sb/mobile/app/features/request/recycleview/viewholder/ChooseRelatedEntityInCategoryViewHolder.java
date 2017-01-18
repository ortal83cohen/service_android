package com.hpe.sb.mobile.app.features.request.recycleview.viewholder;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.common.dataClients.catalog.CatalogClient;
import com.hpe.sb.mobile.app.infra.baseActivities.BaseActivity;
import com.hpe.sb.mobile.app.infra.eventbus.EventBus;
import com.hpe.sb.mobile.app.common.utils.ModelsConverter;
import com.hpe.sb.mobile.app.infra.rx.BaseSubscriber;
import com.hpe.sb.mobile.app.serverModel.catalog.Offering;
import com.hpe.sb.mobile.app.serverModel.catalog.OfferingsByCatalogGroupResult;
import com.hpe.sb.mobile.app.serverModel.search.EntityItem;
import com.hpe.sb.mobile.app.features.request.NewRequestFlowState;
import com.hpe.sb.mobile.app.features.request.event.NewRequestFormEvent;
import com.hpe.sb.mobile.app.features.request.helper.NewRequestSelectionHandler;
import com.hpe.sb.mobile.app.features.request.recycleview.type.ChooseRelatedEntityInCategoryViewType;
import com.hpe.sb.mobile.app.common.uiComponents.relatedEntities.RelatedEntitiesRecyclerViewHelper;
import com.hpe.sb.mobile.app.common.utils.LogTagConstants;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by malikdav on 25/04/2016.
 */
public class ChooseRelatedEntityInCategoryViewHolder extends NewRequestViewHolder<ChooseRelatedEntityInCategoryViewType> {

    @Inject
    public CatalogClient catalogClient;

    private RecyclerView recycleView;
    private RelatedEntitiesRecyclerViewHelper helper;
    private View title;
    private View loadingGif;


    public ChooseRelatedEntityInCategoryViewHolder(View itemView, EventBus<NewRequestFormEvent> eventBus) {
        super(itemView, eventBus);

        ((ServiceBrokerApplication)itemView.getContext().getApplicationContext()).getServiceBrokerComponent().inject(this);

        recycleView = (RecyclerView) itemView.findViewById(R.id.related_entities);
        recycleView.setNestedScrollingEnabled(false);
        title = itemView.findViewById(android.R.id.title);
        loadingGif = itemView.findViewById(R.id.loading_gif);

        helper = new RelatedEntitiesRecyclerViewHelper(itemView.getContext());
        helper.initRecyclerView(recycleView, true, null, new NewRequestSelectionHandler(eventBus));
    }

    @Override
    public void bind(ChooseRelatedEntityInCategoryViewType viewType, BaseActivity activity, NewRequestFlowState flowState) {
        helper.updateItems(recycleView, new ArrayList<EntityItem>(0));

        title.setVisibility(View.GONE);
        loadingGif.setVisibility(View.VISIBLE);
        catalogClient.getOfferingsByCatalogGroup(recycleView.getContext(), false, 0, CatalogClient.MAX_NUMBER_OF_OFFERINGS,
                viewType.getCatalogGroupId(), CatalogClient.CATALOG_GROUP_CATEGORY_LEVEL)
                .delay(500, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new BaseSubscriber<OfferingsByCatalogGroupResult>(activity, activity.getSbExceptionsHandler()) {
            @Override
            public void onCompleted() {
                loadingGif.setVisibility(View.GONE);
            }
            @Override
            public void onError(Throwable e) {
                loadingGif.setVisibility(View.GONE);
                String msg = "Failed to get Offerings By Category, error: " + e.getMessage();
                Log.e(LogTagConstants.NEW_REQUEST, msg);

                super.onError(e);
            }

            @Override
            public void onNext(OfferingsByCatalogGroupResult offeringsByCatalogGroupResult) {
                List<Offering> offerings = offeringsByCatalogGroupResult.getOfferings();
                if(!offerings.isEmpty()) {
                    title.setVisibility(View.VISIBLE);
                    List<EntityItem> entityItems = new ModelsConverter().convertOfferingsToEntityItems(offerings);
                    helper.updateItems(recycleView, entityItems);
                }
            }
        });
    }
}
