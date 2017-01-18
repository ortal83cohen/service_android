package com.hpe.sb.mobile.app.common.utils;

import com.hpe.sb.mobile.app.infra.dataClients.DataBlob;
import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observers.TestSubscriber;

@RunWith(MockitoJUnitRunner.class)
public class ClientDataFlowUtilsTest {

    @Test
    public void mergeDifferentFromPersistence_dataEmittedTwice() {
        String tag = "testTag";
        final DataBlob<PersistableTestDataType> answerFromPersistence = new DataBlob<>("from persistence", new PersistableTestDataType("from persistence", 0), PersistableTestDataType.class, System.currentTimeMillis(), System.currentTimeMillis());  ;
        final PersistableTestDataType answerFromRest = new PersistableTestDataType("from rest", 0);
        final PersistableTestDataType mergedAnswer = new PersistableTestDataType("merged", 0);
        final boolean[] persistenceUpdated = {false};

        Observable<List<DataBlob<PersistableTestDataType>>> fromPersistence = Observable.just(Collections.singletonList(answerFromPersistence));
        Func1<List<PersistableTestDataType>, Observable<List<PersistableTestDataType>>> sendToRest = prepareSendToRestFunction(answerFromPersistence, answerFromRest);
        Func2<List<PersistableTestDataType>, List<PersistableTestDataType>, List<PersistableTestDataType>> merge = prepareMergeFunction(answerFromPersistence, answerFromRest, mergedAnswer);
        Action1<List<PersistableTestDataType>> updatePersistence = preparePersistenceUpdateFunction(persistenceUpdated);

        Observable<List<PersistableTestDataType>> fromCacheSendDiffAndMerge = ClientDataFlowUtils.getFromCacheSendDiffAndMerge(tag, fromPersistence, sendToRest, merge, updatePersistence);
        TestSubscriber<List<PersistableTestDataType>> testSubscriber = new TestSubscriber<>();
        fromCacheSendDiffAndMerge.subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();

        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertValueCount(2);
        testSubscriber.assertValues(Collections.singletonList(answerFromPersistence.getData()),
                Collections.singletonList(mergedAnswer));
        Assert.assertTrue(persistenceUpdated[0]);
    }

    @Test
    public void mergeEqualsToPersistence_dataEmittedOnce() {
        String tag = "testTag";
        final DataBlob<PersistableTestDataType> answerFromPersistence = new DataBlob<>("from persistence", new PersistableTestDataType("from persistence", 0), PersistableTestDataType.class, System.currentTimeMillis(), System.currentTimeMillis());  ;
        final PersistableTestDataType answerFromRest = new PersistableTestDataType("from rest", 0);
        final PersistableTestDataType mergedAnswer = answerFromPersistence.getData();
        final boolean[] persistenceUpdated = {false};

        Observable<List<DataBlob<PersistableTestDataType>>> fromPersistence = Observable.just(Collections.singletonList(answerFromPersistence));
        Func1<List<PersistableTestDataType>, Observable<List<PersistableTestDataType>>> sendToRest = prepareSendToRestFunction(answerFromPersistence, answerFromRest);
        Func2<List<PersistableTestDataType>, List<PersistableTestDataType>, List<PersistableTestDataType>> merge = prepareMergeFunction(answerFromPersistence, answerFromRest, mergedAnswer);
        Action1<List<PersistableTestDataType>> updatePersistence = preparePersistenceUpdateFunction(persistenceUpdated);

        Observable<List<PersistableTestDataType>> fromCacheSendDiffAndMerge = ClientDataFlowUtils.getFromCacheSendDiffAndMerge(tag, fromPersistence, sendToRest, merge, updatePersistence);
        TestSubscriber<List<PersistableTestDataType>> testSubscriber = new TestSubscriber<>();
        fromCacheSendDiffAndMerge.subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();

        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertValueCount(1);
        testSubscriber.assertValues(Collections.singletonList(answerFromPersistence.getData()));
        Assert.assertTrue(persistenceUpdated[0]);
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
        final List<DataBlob<PersistableTestDataType>> answerFromPersistence = Arrays.asList(new DataBlob<>("from persistence", new PersistableTestDataType("from persistence", 0), PersistableTestDataType.class, persistence_time, persistence_time),
                new DataBlob<>("from persistence 2", new PersistableTestDataType("from persistence 2", 0), PersistableTestDataType.class, persistence_time, persistence_time));
        final List<PersistableTestDataType> answerFromRest = Arrays.asList(new PersistableTestDataType("from rest 1", 0), new PersistableTestDataType("from rest 2", 0));
        final List<PersistableTestDataType> mergedAnswer = Arrays.asList(new PersistableTestDataType("merged", 0));
        Observable<List<DataBlob<PersistableTestDataType>>> fromPersistence = Observable.just(answerFromPersistence);
        Func1<List<PersistableTestDataType>, Observable<List<PersistableTestDataType>>> sendToRest = new Func1<List<PersistableTestDataType>, Observable<List<PersistableTestDataType>>>() {
            @Override
            public Observable<List<PersistableTestDataType>> call(List<PersistableTestDataType> s) {
                if (!isRestCallExpected) {
                    Assert.fail("Should not have called rest because of max age!");
                }
                return Observable.just(answerFromRest);
            }
        };
        Func2<List<PersistableTestDataType>, List<PersistableTestDataType>, List<PersistableTestDataType>> merge = new Func2<List<PersistableTestDataType>, List<PersistableTestDataType>, List<PersistableTestDataType>>() {
            @Override
            public List<PersistableTestDataType> call(List<PersistableTestDataType> s, List<PersistableTestDataType> s2) {
                if (!isRestCallExpected) {
                    Assert.fail("Should not have called rest because there is no response from rest due to max age!");
                }
                return mergedAnswer;
            }
        };
        final boolean[] persistenceUpdated = {false};
        Action1<List<PersistableTestDataType>> updatePersistence = new Action1<List<PersistableTestDataType>>() {
            @Override
            public void call(List<PersistableTestDataType> s) {
                if (!isRestCallExpected) {
                    Assert.fail("Should not have updated persistence because there is no response from rest due to max age!");
                }
                persistenceUpdated[0] = true;
            }
        };

        Observable<List<PersistableTestDataType>> fromCacheSendDiffAndMerge = ClientDataFlowUtils.getFromCacheSendDiffAndMerge(tag, fromPersistence, sendToRest, merge, updatePersistence, 30, TimeUnit.MINUTES);
        TestSubscriber<List<PersistableTestDataType>> testSubscriber = new TestSubscriber<>();
        fromCacheSendDiffAndMerge.subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();

        testSubscriber.assertNoErrors();
        testSubscriber.assertCompleted();
        testSubscriber.assertValueCount(valueCountToAssert);
        List<PersistableTestDataType> fromPersistenceInnerData = Arrays.asList(answerFromPersistence.get(0).getData(), answerFromPersistence.get(1).getData());
        if (isRestCallExpected) {
            testSubscriber.assertValues(fromPersistenceInnerData, mergedAnswer);
        }
        else {
            testSubscriber.assertValues(fromPersistenceInnerData);
        }
        Assert.assertEquals(persistenceUpdated[0], isRestCallExpected);
    }

    private Action1<List<PersistableTestDataType>> preparePersistenceUpdateFunction(final boolean[] persistenceUpdated) {
        return new Action1<List<PersistableTestDataType>>() {
            @Override
            public void call(List<PersistableTestDataType> s) {
                persistenceUpdated[0] = true;
            }
        };
    }

    private Func2<List<PersistableTestDataType>, List<PersistableTestDataType>, List<PersistableTestDataType>> prepareMergeFunction(final DataBlob<PersistableTestDataType> answerFromPersistence, final PersistableTestDataType answerFromRest, final PersistableTestDataType mergedAnswer) {
        return new Func2<List<PersistableTestDataType>, List<PersistableTestDataType>, List<PersistableTestDataType>>() {
            @Override
            public List<PersistableTestDataType> call(List<PersistableTestDataType> s, List<PersistableTestDataType> s2) {
                Assert.assertEquals(answerFromPersistence.getData(), s.get(0));
                Assert.assertEquals(answerFromRest, s2.get(0));
                return Collections.singletonList(mergedAnswer);
            }
        };
    }

    private Func1<List<PersistableTestDataType>, Observable<List<PersistableTestDataType>>> prepareSendToRestFunction(final DataBlob<PersistableTestDataType> answerFromPersistence, final PersistableTestDataType answerFromRest) {
        return new Func1<List<PersistableTestDataType>, Observable<List<PersistableTestDataType>>>() {
            @Override
            public Observable<List<PersistableTestDataType>> call(List<PersistableTestDataType> s) {
                Assert.assertEquals(answerFromPersistence.getData(), s.get(0));
                return Observable.just(Collections.singletonList(answerFromRest));
            }
        };
    }


}
