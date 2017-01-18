package com.hpe.sb.mobile.app.common.uiComponents.todocards.utils;

import com.hpe.sb.mobile.app.common.uiComponents.todocards.TodoCardsView;
import com.hpe.sb.mobile.app.infra.baseActivities.BaseActivity;

/**
 * Created by oded on 14/07/2016.
 *
 */
public class TodoCardsViewActionsLogicHandlerFactory {

    public TodoCardsViewActionsLogicHandler createTodoCardsViewActionsLogicHandler(BaseActivity activity, TodoCardsView cardsView,
                                                                                   TodoCardsViewActionsLogicHandler.OnCompletedLoadingListener onCompletedLoadingListener){
        TodoCardsViewActionsLogicHandler todoCardsViewActionsLogicHandler = new TodoCardsViewActionsLogicHandler(activity, cardsView, onCompletedLoadingListener);
        todoCardsViewActionsLogicHandler.initCardsView();
        return todoCardsViewActionsLogicHandler;
    }
}
