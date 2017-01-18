package com.hpe.sb.mobile.app.common.uiComponents.relatedEntities;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.hpe.sb.mobile.app.features.article.ArticleActivity;
import com.hpe.sb.mobile.app.infra.baseActivities.BaseActivity;
import com.hpe.sb.mobile.app.serverModel.search.EntityType;
import com.hpe.sb.mobile.app.serverModel.search.EntityItem;
import com.hpe.sb.mobile.app.features.request.NewRequestActivity;

/**
 * Created by gabaysh on 18/05/2016.
 */
public class RelatedEntitySelectionHandler implements OnEntityItemSelectedListener {

    private Context context;

    public RelatedEntitySelectionHandler(Context context) {
        this.context = context;
    }

    @Override
    public void call(EntityItem entityItem) {
        EntityType type = entityItem.getType();
        switch (type) {
            case HUMAN_RESOURCE_OFFERING:
            case SUPPORT_OFFERING:
            case SERVICE_OFFERING:
                openOfferingPage(entityItem);
                break;
            case ARTICLE:
                openArticlePage(entityItem);
                break;
            default:
                Log.e(getClass().getName(), String.format("Type is not supported: %s", type.name()));
        }
    }

    private void openArticlePage(EntityItem entityItem) {
        Intent intent = ArticleActivity.createIntent(context, entityItem.getId(),  entityItem.getType().toString());
        context.startActivity(intent);
    }

    private void openOfferingPage(EntityItem entityItem) {
        final Intent intentForOffering = NewRequestActivity.createIntentForOffering(context, entityItem.getId());
        intentForOffering.putExtra(BaseActivity.FLAG_REMOVE_FROM_BACK, true);
        context.startActivity(intentForOffering);
    }
}
