package com.hpe.sb.mobile.app.common.uiComponents.todocards;

import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.utils.TodoCardsEvent;
import com.hpe.sb.mobile.app.infra.eventbus.EventBus;
import com.hpe.sb.mobile.app.serverModel.user.useritems.UserItem;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.adapter.SwipeFlingAdapterView;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.adapter.TodoCardsCircularArrayAdapter;
import com.hpe.sb.mobile.app.features.detailsActivity.DetailsActivity;
import com.hpe.sb.mobile.app.common.utils.RuntimeViewIdGenerator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chovel on 11/04/2016.
 *
 */
public class TodoCardsView extends RelativeLayout {

    private final int DEFAULT_DOTS_INDICATOR_SIZE = 7;

    private int next;

    private int curr;

    private int maxDots;

    private ArrayList<TodoCardData> activeCards;

    private List<TodoCardData> cardsList;

    private TodoCardsCircularArrayAdapter arrayAdapter;

    private SwipeFlingAdapterView swipeFlingAdapterView;

    private DotsIndexIndicatorView dotsIndexIndicatorView;

    private EventBus<TodoCardsEvent> eventBus;

    public TodoCardsView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.TodoCardsView,
                0, 0);

        try {
            maxDots = a.getInteger(R.styleable.TodoCardsView_maxDots, DEFAULT_DOTS_INDICATOR_SIZE);
        } finally {
            a.recycle();
        }

        cardsList = new ArrayList<>();
        activeCards = new ArrayList<>();
        if (!isInEditMode()) {
            //used to avoid problems in design view in ide because of the casting when injecting image service in the array adapter
            arrayAdapter = new TodoCardsCircularArrayAdapter(context, activeCards.size(),
                    activeCards, eventBus);
        }
        initFlingAdapterView(context);
        initActiveCardsAndCount(cardsList);
        initDotsIndexIndicator(context, cardsList.size());
    }

    public void setEventBus(EventBus<TodoCardsEvent> eventBus) {
        this.eventBus = eventBus;
    }

    private void initFlingAdapterView(final Context context) {
        swipeFlingAdapterView = new SwipeFlingAdapterView(context);
        swipeFlingAdapterView.setAdapter(arrayAdapter);
        swipeFlingAdapterView.setFlingListener(new CardsFlingListener());
        swipeFlingAdapterView.setId(RuntimeViewIdGenerator.generateViewId());
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                (int) getResources().getDimension(R.dimen.todo_cards_fling_height));
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, TRUE);
        addView(swipeFlingAdapterView, layoutParams);
        swipeFlingAdapterView.setOnItemClickListener(
                new SwipeFlingAdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClicked(int itemPosition, Object dataObject) {

                        Intent intent = DetailsActivity.createIntent(context,
                                ((TodoCardData) dataObject).getData(UserItem.class), false, false, false);

                        View view = swipeFlingAdapterView
                                .getChildAt(arrayAdapter.getCount() == 1 ? 0 : 1);

                        View todoBarIcon = view.findViewById(R.id.barIcon);

                        View header = view.findViewById(R.id.header);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            header.setTransitionName("header");
                        }
                        Pair<View, String> p2 = Pair.create(header, "header");
                        ActivityOptionsCompat options;
                        if (todoBarIcon != null) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                todoBarIcon.setTransitionName("info_image");
                            }
                            Pair<View, String> p1 = Pair.create(todoBarIcon, "info_image");
                            options = ActivityOptionsCompat.
                                    makeSceneTransitionAnimation((Activity) context, p1, p2);
                        } else {
                            options = ActivityOptionsCompat.
                                    makeSceneTransitionAnimation((Activity) context, p2);
                        }
                        ((Activity) context).startActivity(intent, options.toBundle());
                    }
                });
    }

    private void initDotsIndexIndicator(Context context, int cardAmount) {
        dotsIndexIndicatorView = new DotsIndexIndicatorView(context, cardAmount, maxDots);
        dotsIndexIndicatorView.setId(RuntimeViewIdGenerator.generateViewId());
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.BELOW, swipeFlingAdapterView.getId());
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, TRUE);
        int dotsIndicatorMarginTop = getContext().getResources().getDimensionPixelSize(R.dimen.dots_indicator_top_margin);
        layoutParams.setMargins(0, dotsIndicatorMarginTop, 0, 0);
        addView(dotsIndexIndicatorView, layoutParams);
    }

    private void initActiveCardsAndCount(List<TodoCardData> cardsList) {
        activeCards = new ArrayList<>();
        TodoCardData frontCardData = cardsList.isEmpty() ? null : cardsList.get(0);
        TodoCardData backCardData = cardsList.size() >= 2 ? cardsList.get(1) : null;
        if (frontCardData != null) {
            activeCards.add(frontCardData);
        }
        if (backCardData != null) {
            activeCards.add(backCardData);
        }
        this.curr = 0;
        this.next = 1;
    }

    public void setCardsList(List<TodoCardData> cardsList) {
        this.cardsList = cardsList;
        dotsIndexIndicatorView.setCardAmount(cardsList.size());
        LayoutParams layoutParams;
        if (!cardsList.isEmpty()) {
            dotsIndexIndicatorView.onCardSelected(0);
            layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    (int) getResources().getDimension(R.dimen.todo_cards_fling_height));
        }else{
            layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    0);
        }
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, TRUE);
        swipeFlingAdapterView.setLayoutParams(layoutParams);
        initActiveCardsAndCount(cardsList);
        arrayAdapter = new TodoCardsCircularArrayAdapter(this.getContext(), 0, activeCards, eventBus);
        swipeFlingAdapterView.setAdapter(arrayAdapter);
        swipeFlingAdapterView.resetLayout();
    }

    public List<TodoCardData> getCardsList() {
        return cardsList;
    }

    private class CardsFlingListener implements SwipeFlingAdapterView.onFlingListener {

        @Override
        public void removeFirstObjectInAdapter() {
            activeCards.set(0, cardsList.get(next));
            curr = next;
            next = (curr + 1) % cardsList.size();
            activeCards.set(1, cardsList.get(next));
            arrayAdapter.notifyDataSetChanged();

            if (dotsIndexIndicatorView != null) {
                dotsIndexIndicatorView.onCardSelected(curr);
            }
        }

        @Override
        public void onLeftCardExit(Object dataObject) {

        }

        @Override
        public void onRightCardExit(Object dataObject) {

        }

        @Override
        public void onAdapterAboutToEmpty(int itemsInAdapter) {

        }

        @Override
        public void onScroll(float scrollProgressPercent) {

        }

        @Override
        public void prepareNextCard(Boolean isNext) {
            if (isNext) {
                //going forward
                int realNextIndex = (curr + 1) % cardsList.size();
                next = realNextIndex;
                activeCards.set(1, cardsList.get(realNextIndex));
            } else {
                //going backwards
                int realPrevIndex = curr - 1 == -1 ? cardsList.size() - 1 : curr - 1;
                next = realPrevIndex;
                activeCards.set(1, cardsList.get(realPrevIndex));
            }
            arrayAdapter.notifyDataSetChanged();
        }

    }

}
