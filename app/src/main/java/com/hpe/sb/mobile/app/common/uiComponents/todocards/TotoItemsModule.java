package com.hpe.sb.mobile.app.common.uiComponents.todocards;


import com.hpe.sb.mobile.app.common.uiComponents.todocards.utils.TodoCardsViewActionsLogicHandlerFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class TotoItemsModule {

    @Provides
    @Singleton
    TodoCardsViewActionsLogicHandlerFactory provideTodoCardsViewActionsLogicHandlerFactory() {
        return new TodoCardsViewActionsLogicHandlerFactory();
    }
}
