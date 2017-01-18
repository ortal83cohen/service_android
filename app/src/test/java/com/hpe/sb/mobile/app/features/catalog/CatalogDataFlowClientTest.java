package com.hpe.sb.mobile.app.features.catalog;

import com.hpe.sb.mobile.app.common.dataClients.catalog.CatalogDataFlowClient;
import com.hpe.sb.mobile.app.serverModel.catalog.CatalogGroup;
import com.hpe.sb.mobile.app.serverModel.catalog.CatalogGroupsByCategory;
import com.hpe.sb.mobile.app.serverModel.catalog.Offering;
import com.hpe.sb.mobile.app.serverModel.catalog.OfferingsByCatalogGroupResult;
import com.hpe.sb.mobile.app.serverModel.catalog.OfferingsByCatalogGroupResultDiffResult;
import com.hpe.sb.mobile.app.serverModel.catalog.OfferingsByCategory;
import com.hpe.sb.mobile.app.serverModel.diff.DiffResult;
import com.hpe.sb.mobile.app.infra.dataClients.DataBlob;
import junit.framework.Assert;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observers.TestSubscriber;


/**
 * Created by salemo on 22/05/2016.
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class CatalogDataFlowClientTest {

    CatalogDataFlowClient catalogDataFlowClient = new CatalogDataFlowClient();
    @Test
    public void mergeRestWithPersistence_dataEmittedTwice() {
        String tag = "testTag";
        final DataBlob<OfferingsByCategory> answerFromPersistenceOfferingByCategory = new DataBlob<>("from persistence", new OfferingsByCategory(new ArrayList<String>(), "categoryId"), OfferingsByCategory.class, System.currentTimeMillis(), System.currentTimeMillis());
        final DataBlob<CatalogGroupsByCategory> answerFromPersistenceCatalogGroupByCategory = new DataBlob<>("from persistence", new CatalogGroupsByCategory(new ArrayList<String>(), "categoryId"), CatalogGroupsByCategory.class, System.currentTimeMillis(), System.currentTimeMillis());
        final OfferingsByCatalogGroupResultDiffResult answerFromRest = new OfferingsByCatalogGroupResultDiffResult(new DiffResult<Offering>(), new DiffResult<CatalogGroup>());
        ArrayList<Offering> offerings1 = new ArrayList<>();
        offerings1.add(new Offering());
        ArrayList<CatalogGroup> mgParentCategories1 = new ArrayList<>();
        mgParentCategories1.add(new CatalogGroup());
        final OfferingsByCatalogGroupResult mergedAnswer = new OfferingsByCatalogGroupResult(offerings1, mgParentCategories1);

        ArrayList<Offering> offerings2 = new ArrayList<>();
        ArrayList<CatalogGroup> mgParentCategories2 = new ArrayList<>();
        mgParentCategories2.add(new CatalogGroup());
        final OfferingsByCatalogGroupResult fromPersistenceAnswer = new OfferingsByCatalogGroupResult(offerings2, mgParentCategories2);
        final boolean[] persistenceUpdatedOffering = {false};

        Observable<DataBlob<OfferingsByCategory>> fromPersistenceOfferingByCategory =  Observable.just(answerFromPersistenceOfferingByCategory);
        Observable<DataBlob<CatalogGroupsByCategory>> fromPersistenceCatalogGroupByCategory = Observable.just(answerFromPersistenceCatalogGroupByCategory);
        Observable<OfferingsByCatalogGroupResult> fromPersistence = Observable.just(fromPersistenceAnswer);


        Func1<OfferingsByCatalogGroupResult, Observable<OfferingsByCatalogGroupResultDiffResult>> sendToRest = CatalogDataFlowClientTestUtil.prepareSendToRestFunction(answerFromPersistenceOfferingByCategory, answerFromRest);
        Func2<OfferingsByCatalogGroupResult, OfferingsByCatalogGroupResultDiffResult, OfferingsByCatalogGroupResult> merge = CatalogDataFlowClientTestUtil.prepareMergeFunction(answerFromPersistenceCatalogGroupByCategory, answerFromRest, mergedAnswer);

        Action1<OfferingsByCatalogGroupResultDiffResult> updatePersistenceOffering = CatalogDataFlowClientTestUtil.preparePersistenceUpdateFunction(persistenceUpdatedOffering);
        Observable<OfferingsByCatalogGroupResult> fromCacheSendDiffAndMerge = catalogDataFlowClient.getFromCacheSendDiffAndMergeMultipleItems(tag, fromPersistence, sendToRest, merge,
                updatePersistenceOffering, fromPersistenceOfferingByCategory, fromPersistenceCatalogGroupByCategory);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        fromCacheSendDiffAndMerge.toBlocking().subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();

        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertValueCount(2);
        testSubscriber.assertValues(fromPersistenceAnswer, mergedAnswer);
        Assert.assertTrue(persistenceUpdatedOffering[0]);
    }

    @Test
    public void testMaxAge_restCallMade() {
        testMaxAge(30, 20, 2);
    }

    @Test
    public void testMaxAge_noRestCallMade() {
        testMaxAge(20, 30, 1);
    }

    public void testMaxAge(int persistenceTimeToDeduct, int maxAgeTime, int valueCountToAssert) {
        String tag = "testTag";
        long persistence_time = System.currentTimeMillis() - TimeUnit.MILLISECONDS.convert(persistenceTimeToDeduct, TimeUnit.MINUTES);
        final boolean isRestCallExpected = maxAgeTime < persistenceTimeToDeduct;
        final DataBlob<OfferingsByCategory> answerFromPersistenceOfferingByCategory = new DataBlob<>("from persistence", new OfferingsByCategory(new ArrayList<String>(), "categoryId"), OfferingsByCategory.class, persistence_time, persistence_time);
        final DataBlob<CatalogGroupsByCategory> answerFromPersistenceCatalogGroupByCategory = new DataBlob<>("from persistence", new CatalogGroupsByCategory(new ArrayList<String>(), "categoryId"), CatalogGroupsByCategory.class, persistence_time, persistence_time);
        final OfferingsByCatalogGroupResultDiffResult answerFromRest = new OfferingsByCatalogGroupResultDiffResult(new DiffResult<Offering>(), new DiffResult<CatalogGroup>());
        ArrayList<Offering> offerings1 = new ArrayList<>();
        ArrayList<CatalogGroup> mgParentCategories1 = new ArrayList<>();
        mgParentCategories1.add(new CatalogGroup());
        final OfferingsByCatalogGroupResult mergedAnswer = new OfferingsByCatalogGroupResult(offerings1, mgParentCategories1);

        ArrayList<Offering> offerings2 = new ArrayList<>();
        offerings2.add(new Offering());
        ArrayList<CatalogGroup> mgParentCategories2 = new ArrayList<>();
        mgParentCategories2.add(new CatalogGroup());
        final OfferingsByCatalogGroupResult fromPersistenceAnswer = new OfferingsByCatalogGroupResult(offerings2, mgParentCategories2);
        final boolean[] persistenceUpdatedOffering = {false};

        Observable<DataBlob<OfferingsByCategory>> fromPersistenceOfferingByCategory =  Observable.just(answerFromPersistenceOfferingByCategory);
        Observable<DataBlob<CatalogGroupsByCategory>> fromPersistenceCatalogGroupByCategory = Observable.just(answerFromPersistenceCatalogGroupByCategory);
        Observable<OfferingsByCatalogGroupResult> fromPersistence = Observable.just(fromPersistenceAnswer);


        Func1<OfferingsByCatalogGroupResult, Observable<OfferingsByCatalogGroupResultDiffResult>> sendToRest = new Func1<OfferingsByCatalogGroupResult, Observable<OfferingsByCatalogGroupResultDiffResult>>() {
            @Override
            public Observable<OfferingsByCatalogGroupResultDiffResult> call(OfferingsByCatalogGroupResult s) {
                if (!isRestCallExpected) {
                    Assert.fail("Should not have called rest because of max age!");
                }
                return Observable.just(answerFromRest);
            }
        };
        Func2<OfferingsByCatalogGroupResult, OfferingsByCatalogGroupResultDiffResult, OfferingsByCatalogGroupResult> merge = new Func2<OfferingsByCatalogGroupResult, OfferingsByCatalogGroupResultDiffResult, OfferingsByCatalogGroupResult>() {
            @Override
            public OfferingsByCatalogGroupResult call(OfferingsByCatalogGroupResult s, OfferingsByCatalogGroupResultDiffResult s2) {
                if (!isRestCallExpected) {
                    Assert.fail("Should not have called rest because there is no response from rest due to max age!");
                }
                return mergedAnswer;
            }
        };

        Action1<OfferingsByCatalogGroupResultDiffResult> updatePersistenceOffering = CatalogDataFlowClientTestUtil.getUpdatePersistenceAction(isRestCallExpected, persistenceUpdatedOffering);

        Observable<OfferingsByCatalogGroupResult> fromCacheSendDiffAndMerge = catalogDataFlowClient.getFromCacheSendDiffAndMergeMultipleItems(tag, fromPersistence, sendToRest, merge,
                updatePersistenceOffering, 30, TimeUnit.MINUTES, fromPersistenceOfferingByCategory, fromPersistenceCatalogGroupByCategory);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        fromCacheSendDiffAndMerge.toBlocking().subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();

        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertValueCount(valueCountToAssert);
        if (isRestCallExpected) {
            testSubscriber.assertValues(fromPersistenceAnswer, mergedAnswer);
        } else {
            testSubscriber.assertValues(fromPersistenceAnswer);
        }
        Assert.assertEquals(persistenceUpdatedOffering[0], isRestCallExpected);
    }

    @Test
    @Ignore
    public void mergeEqualsToPersistence_dataEmittedOnce() {
        String tag = "testTag";
        final DataBlob<OfferingsByCategory> answerFromPersistenceOfferingByCategory = new DataBlob<>("from persistence", new OfferingsByCategory(new ArrayList<String>(), "categoryId"), OfferingsByCategory.class, System.currentTimeMillis(), System.currentTimeMillis());
        final DataBlob<CatalogGroupsByCategory> answerFromPersistenceCatalogGroupByCategory = new DataBlob<>("from persistence", new CatalogGroupsByCategory(new ArrayList<String>(), "categoryId"), CatalogGroupsByCategory.class, System.currentTimeMillis(), System.currentTimeMillis());
        final OfferingsByCatalogGroupResultDiffResult answerFromRest = new OfferingsByCatalogGroupResultDiffResult(new DiffResult<Offering>(), new DiffResult<CatalogGroup>());
        ArrayList<Offering> offerings1 = new ArrayList<>();
        offerings1.add(new Offering());
        ArrayList<CatalogGroup> mgParentCategories1 = new ArrayList<>();
        mgParentCategories1.add(new CatalogGroup());
        final OfferingsByCatalogGroupResult mergedAnswer = new OfferingsByCatalogGroupResult(offerings1, mgParentCategories1);

        ArrayList<Offering> offerings2 = new ArrayList<>();
        offerings2.add(new Offering());
        ArrayList<CatalogGroup> mgParentCategories2 = new ArrayList<>();
        mgParentCategories2.add(new CatalogGroup());
        final OfferingsByCatalogGroupResult fromPersistenceAnswer = new OfferingsByCatalogGroupResult(offerings2, mgParentCategories2);
        final boolean[] persistenceUpdated = {false};

        Observable<DataBlob<OfferingsByCategory>> fromPersistenceOfferingByCategory =  Observable.just(answerFromPersistenceOfferingByCategory);
        Observable<DataBlob<CatalogGroupsByCategory>> fromPersistenceCatalogGroupByCategory = Observable.just(answerFromPersistenceCatalogGroupByCategory);
        Observable<OfferingsByCatalogGroupResult> fromPersistence = Observable.just(fromPersistenceAnswer);


        Func1<OfferingsByCatalogGroupResult, Observable<OfferingsByCatalogGroupResultDiffResult>> sendToRest = CatalogDataFlowClientTestUtil.prepareSendToRestFunction(answerFromPersistenceOfferingByCategory, answerFromRest);
        Func2<OfferingsByCatalogGroupResult, OfferingsByCatalogGroupResultDiffResult, OfferingsByCatalogGroupResult> merge = CatalogDataFlowClientTestUtil.prepareMergeFunction(answerFromPersistenceCatalogGroupByCategory, answerFromRest, mergedAnswer);

        Action1<OfferingsByCatalogGroupResultDiffResult> updatePersistenceOffering = CatalogDataFlowClientTestUtil.preparePersistenceUpdateFunction(persistenceUpdated);
        Observable<OfferingsByCatalogGroupResult> fromCacheSendDiffAndMerge = catalogDataFlowClient.getFromCacheSendDiffAndMergeMultipleItems(tag, fromPersistence, sendToRest, merge,
                updatePersistenceOffering, fromPersistenceOfferingByCategory, fromPersistenceCatalogGroupByCategory);

        TestSubscriber<Object> testSubscriber = new TestSubscriber<>();
        fromCacheSendDiffAndMerge.toBlocking().subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();

        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertValues(fromPersistenceAnswer);
    }
}
