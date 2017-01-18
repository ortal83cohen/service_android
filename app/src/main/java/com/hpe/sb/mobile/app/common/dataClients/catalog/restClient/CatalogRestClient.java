package com.hpe.sb.mobile.app.common.dataClients.catalog.restClient;

import android.content.Context;
import com.hpe.sb.mobile.app.serverModel.catalog.CatalogGroup;
import com.hpe.sb.mobile.app.serverModel.catalog.Offering;
import com.hpe.sb.mobile.app.serverModel.catalog.OfferingsByCatalogGroupResult;
import com.hpe.sb.mobile.app.serverModel.catalog.OfferingsByCatalogGroupResultDiffResult;
import com.hpe.sb.mobile.app.serverModel.diff.DiffResult;
import rx.Observable;

import java.util.List;

/**
 * Created by malikdav on 14/03/2016.
 *
 */
public interface CatalogRestClient {

    Observable<OfferingsByCatalogGroupResultDiffResult> getOfferingsByCatalogGroup(Context context, int offset, int numberOfOfferingsToReturn,
                                                                                   String catalogGroupId, int level,
                                                                                   OfferingsByCatalogGroupResult dbOfferingsByCatalogGroupResult);

    Observable<Offering> getOffering(Context context, String offeringId);

    Observable<Offering> getDefaultOffering(Context context);

    Observable<DiffResult<CatalogGroup>> getCategories(Context context, List<CatalogGroup> categories);
}

