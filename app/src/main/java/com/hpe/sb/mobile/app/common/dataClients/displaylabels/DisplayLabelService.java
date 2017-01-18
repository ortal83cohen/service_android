package com.hpe.sb.mobile.app.common.dataClients.displaylabels;


import com.hpe.sb.mobile.app.serverModel.displayLabels.DisplayLabelsWrapper;
import com.hpe.sb.mobile.app.serverModel.displayLabels.OfferingLabels;

import rx.Observable;

/**
 * Created by salemo on 23/03/2016.
 *
 */
public interface DisplayLabelService {

    Observable<Void> setLabels(DisplayLabelsWrapper labels);
    OfferingLabels getOfferingLabels();
}