package com.hpe.sb.mobile.app.common.dataClients.displaylabels;


import android.util.Log;

import com.hpe.sb.mobile.app.infra.dataClients.SingletonDbClient;
import com.hpe.sb.mobile.app.serverModel.displayLabels.DisplayLabelsWrapper;
import com.hpe.sb.mobile.app.serverModel.displayLabels.OfferingLabels;
import com.hpe.sb.mobile.app.infra.dataClients.DataBlob;
import com.hpe.sb.mobile.app.common.utils.LogTagConstants;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;

/**
 * Created by salemo on 30/03/2016.
 * service that holds the dynamic DisplayLabels, used for a synchronize flow in the app.
 * we retrieve the data once at the initial data flow of the app
 */
public class DisplayLabelServiceImpl implements DisplayLabelService {
    private static final String TAG = DisplayLabelServiceImpl.class.getSimpleName();
    private DisplayLabelsWrapper labels;

    private final SingletonDbClient<DisplayLabelsWrapper> displayLabelsDbClient;

    public DisplayLabelServiceImpl(SingletonDbClient<DisplayLabelsWrapper> displayLabelsDbClient) {
        this.displayLabelsDbClient = displayLabelsDbClient;
    }

    public void initialize() {
        DataBlob<DisplayLabelsWrapper> userDataBlob = displayLabelsDbClient.getItemSync();
        if (userDataBlob != null) {
            labels = userDataBlob.getData();
            Log.e(TAG, "found display labels in db: " + labels);
        } else {
            Log.e(TAG, "did not find display labels in db");
        }
    }

    @Override
    public OfferingLabels getOfferingLabels() {
        return labels != null? labels.getOfferingTypeLabels() : null;
    }

    @Override
    public Observable<Void> setLabels(final DisplayLabelsWrapper labels) {
        return Observable.defer(new Func0<Observable<Void>>() {
            @Override
            public Observable<Void> call() {
                DisplayLabelServiceImpl.this.labels = labels;
                return displayLabelsDbClient.set(labels);
            }
        });
    }
}