package com.hpe.sb.mobile.app.common.dataClients.displaylabels;

import android.content.ContentResolver;

import com.hpe.sb.mobile.app.infra.db.DbHelper;
import com.hpe.sb.mobile.app.infra.dataClients.SingletonDbClient;
import com.hpe.sb.mobile.app.serverModel.displayLabels.DisplayLabelsWrapper;
import com.hpe.sb.mobile.app.infra.db.GeneralBlobModel;
import com.hpe.sb.mobile.app.common.utils.LogTagConstants;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by salemo on 30/03/2016.
 *entitydisplaymodule
 */
@Module
public class DisplayLabelDataModule {

    @Provides
    @Singleton
    public DisplayLabelService provideDisplayLabelService(SingletonDbClient<DisplayLabelsWrapper> userDbClient) {
        DisplayLabelServiceImpl displayLabelServiceImpl = new DisplayLabelServiceImpl(userDbClient);
        displayLabelServiceImpl.initialize();
        return displayLabelServiceImpl;
    }

    @Provides
    @Singleton
    public SingletonDbClient<DisplayLabelsWrapper> provideDisplayLabelsDbClient(ContentResolver contentResolver, DbHelper dbHelper) {
        return new SingletonDbClient<>(DisplayLabelsWrapper.class, contentResolver,
                LogTagConstants.DISPLAY_LABELS_CLIENT, "displayLabelId", GeneralBlobModel.TYPE_DISPLAY_LABELS, dbHelper);
    }
}
