package com.hpe.sb.mobile.app.features.catalog;

import com.hpe.sb.mobile.app.serverModel.catalog.CatalogGroupsByCategory;
import com.hpe.sb.mobile.app.serverModel.catalog.OfferingsByCatalogGroupResult;
import com.hpe.sb.mobile.app.serverModel.catalog.OfferingsByCatalogGroupResultDiffResult;
import com.hpe.sb.mobile.app.serverModel.catalog.OfferingsByCategory;
import com.hpe.sb.mobile.app.infra.dataClients.DataBlob;

import junit.framework.Assert;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * Created by salemo on 23/05/2016.
 *
 */
public class CatalogDataFlowClientTestUtil {


    public static Action1<OfferingsByCatalogGroupResultDiffResult> getUpdatePersistenceAction(final boolean isRestCallExpected, final boolean[] persistenceUpdatedOffering) {
        return new Action1<OfferingsByCatalogGroupResultDiffResult>() {
            @Override
            public void call(OfferingsByCatalogGroupResultDiffResult s) {
                if (!isRestCallExpected) {
                    Assert.fail("Should not have updated persistence because there is no response from rest due to max age!");
                }
                persistenceUpdatedOffering[0] = true;
            }
        };
    }

    public static Action1<OfferingsByCatalogGroupResultDiffResult> preparePersistenceUpdateFunction(final boolean[] persistenceUpdated) {
        return new Action1<OfferingsByCatalogGroupResultDiffResult>() {
            @Override
            public void call(OfferingsByCatalogGroupResultDiffResult s) {
                persistenceUpdated[0] = true;
            }
        };
    }

    public static Func2<OfferingsByCatalogGroupResult, OfferingsByCatalogGroupResultDiffResult, OfferingsByCatalogGroupResult> prepareMergeFunction(final DataBlob<CatalogGroupsByCategory> answerFromPersistence, final OfferingsByCatalogGroupResultDiffResult answerFromRest, final OfferingsByCatalogGroupResult mergedAnswer) {
        return new Func2<OfferingsByCatalogGroupResult, OfferingsByCatalogGroupResultDiffResult, OfferingsByCatalogGroupResult>() {
            @Override
            public OfferingsByCatalogGroupResult call(OfferingsByCatalogGroupResult s, OfferingsByCatalogGroupResultDiffResult s2) {
                return mergedAnswer;
            }
        };
    }

    public static Func1<OfferingsByCatalogGroupResult, Observable<OfferingsByCatalogGroupResultDiffResult>> prepareSendToRestFunction(final DataBlob<OfferingsByCategory> answerFromPersistence, final OfferingsByCatalogGroupResultDiffResult answerFromRest) {
        return new Func1<OfferingsByCatalogGroupResult, Observable<OfferingsByCatalogGroupResultDiffResult>>() {
            @Override
            public Observable<OfferingsByCatalogGroupResultDiffResult> call(OfferingsByCatalogGroupResult s) {
                return Observable.just(answerFromRest);
            }
        };
    }
}
