package com.hpe.sb.mobile.app.common.uiComponents.todocards.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.ServiceBrokerApplication;
import com.hpe.sb.mobile.app.infra.image.ImageServiceUtil;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.utils.TodoCardsEvent;
import com.hpe.sb.mobile.app.infra.eventbus.EventBus;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.TodoCardData;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.viewholders.*;
import com.hpe.sb.mobile.app.common.utils.LogTagConstants;

import javax.inject.Inject;
import java.util.List;

/**
 * Created by chovel on 07/04/2016.
 *
 */
public class TodoCardsCircularArrayAdapter extends ArrayAdapter<TodoCardData> {

    public static final String NO_ITEM_TYPE_ERROR = "Item view type was queried but no such item at position %s. This should not be called when item list in circular adapter is empty. Returning default value.";
    public static final int ITEM_TYPE_DEFAULT = 0;

    private LayoutInflater layoutInflater;
    private List<TodoCardData> todoCardItems;
    private EventBus<TodoCardsEvent> eventBus;

    @Inject
    public ImageServiceUtil imageServiceUtil;

    public TodoCardsCircularArrayAdapter(Context context, int resource, List<TodoCardData> todoCardItems, EventBus<TodoCardsEvent> eventBus) {
        super(context, resource, todoCardItems);
        this.todoCardItems = todoCardItems;
        this.eventBus = eventBus;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //dagger injection
        ((ServiceBrokerApplication)context.getApplicationContext()).getServiceBrokerComponent().inject(this);
    }

    @Override
    public int getCount() {
        return todoCardItems.isEmpty() ? 0 : todoCardItems.size() == 1 ? 1 : Integer.MAX_VALUE;
    }

    @Override
    public TodoCardData getItem(int position) {
        return todoCardItems.isEmpty() ? null : todoCardItems.get(position % todoCardItems.size());
    }

    @Override
    public int getViewTypeCount() {
        return TodoCardData.getTypeCount();
    }

    @Override
    public int getItemViewType(int position) {
        TodoCardData item = getItem(position);
        if (item == null) {
            Log.e(LogTagConstants.USER_ITEMS, String.format(NO_ITEM_TYPE_ERROR, position));
        }
        return item == null ? ITEM_TYPE_DEFAULT : TodoCardData.getViewTypeIndex(item.getModelClass());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        TodoCardData item = getItem(position);
        CardViewHolder cardViewHolder = null;
        if (convertView == null) {
            switch (type) {
                case TodoCardData.ACTIVE_REQUEST:
                    convertView = layoutInflater.inflate(R.layout.active_request_card, parent, false);
                    cardViewHolder = new ActiveRequestCardViewHolder(convertView, layoutInflater, eventBus);
                    break;

                case TodoCardData.APPROVAL:
                    convertView = layoutInflater.inflate(R.layout.approval_card, parent, false);
                    cardViewHolder = new ApprovalCardViewHolder(convertView, getContext(), eventBus);
                    break;

                case TodoCardData.FEEDBACK_REQUEST:
                    convertView = layoutInflater.inflate(R.layout.feedback_card, parent, false);
                    cardViewHolder = new FeedbackCardViewHolder(convertView, imageServiceUtil, layoutInflater, eventBus);
                    break;

                case TodoCardData.REQUEST_RESOLVED:
                    convertView = layoutInflater.inflate(R.layout.request_resolved_card, parent, false);
                    cardViewHolder = new RequestResolvedCardViewHolder(convertView, getContext(), eventBus);
                    break;
            }
            convertView.setTag(cardViewHolder);
        }
        else {
            cardViewHolder = (CardViewHolder) convertView.getTag();
        }

        if (cardViewHolder != null) {
            cardViewHolder.bind(item.getData(item.getModelClass()));
        }
        return convertView;
    }


    //use for tests
    public void setTodoCardItems(List<TodoCardData> todoCardItems) {
        this.todoCardItems = todoCardItems;
    }

    //use for tests
    public void setImageServiceUtil(ImageServiceUtil imageServiceUtil) {
        this.imageServiceUtil = imageServiceUtil;
    }

    //use for tests
    public void setLayoutInflater(LayoutInflater layoutInflater) {
        this.layoutInflater = layoutInflater;
    }

}
