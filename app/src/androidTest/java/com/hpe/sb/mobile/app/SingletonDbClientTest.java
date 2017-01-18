package com.hpe.sb.mobile.app;

import android.app.Instrumentation;
import android.content.ContentResolver;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import com.hpe.sb.mobile.app.infra.db.DbHelper;
import com.hpe.sb.mobile.app.infra.dataClients.DataBlob;
import com.hpe.sb.mobile.app.infra.dataClients.Persistable;
import com.hpe.sb.mobile.app.infra.dataClients.SingletonDbClient;
import com.hpe.sb.mobile.app.infra.db.GeneralBlobModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import rx.Observable;
import rx.Observer;
import rx.observers.TestSubscriber;

/**
 * Created by salemo on 04/05/2016.
 *
 */
@RunWith(AndroidJUnit4.class)
public class SingletonDbClientTest extends InstrumentationTestCase {

    Instrumentation instrumentation;
    SingletonDbClient<TestData> testClient;

    @Before
    public void setUp() throws Exception {
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        instrumentation = getInstrumentation();
        ContentResolver contentResolver = instrumentation.getContext().getContentResolver();
        DbHelper dbHelper = new DbHelper(contentResolver);
        testClient = new SingletonDbClient<>(TestData.class, contentResolver,
                "TEST_PERSISTENCE_CLIENT", "itemId", GeneralBlobModel.TYPE_TEST, dbHelper);
        super.setUp();
        TestSubscriber<Void> testSubscriber = new TestSubscriber<>();
        testClient.clear().subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();
    }

    @Test
    public void testSetAndGetAll() {
        testNoDataExists();

        //insert data to persistence
        final TestData dataToInsert = new TestData("1", 0);
        testClient.set(dataToInsert);

        //assert data was inserted and returns in query
        Observable<DataBlob<TestData>> itemByType = testClient.getItem();
        itemByType.subscribe(new Observer<DataBlob<TestData>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                fail("Error thrown!" + e);
            }

            @Override
            public void onNext(DataBlob<TestData> dataBlob) {
                assertTrue(dataToInsert.equals(dataBlob.getData()));
            }
        });
        TestSubscriber<Void> testSubscriber = new TestSubscriber<>();
        testClient.clear().subscribe(testSubscriber);
        testSubscriber.awaitTerminalEvent();
        testNoDataExists();
    }

    private void testNoDataExists() {
        //assert no data
        Observable<DataBlob<TestData>> noItemByType = testClient.getItem();
        noItemByType.subscribe(new Observer<DataBlob<TestData>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                fail("Error thrown!");
            }

            @Override
            public void onNext(DataBlob<TestData> dataBlobs) {
                assertNull(dataBlobs);
            }
        });
    }

    private class TestData implements Persistable {

        private String id;
        private int checksum;

        public TestData(String id, int checksum) {
            this.id = id;
            this.checksum = checksum;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public int getChecksum() {
            return checksum;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            TestData testData = (TestData) o;

            if (checksum != testData.checksum) {
                return false;
            }
            return id != null ? id.equals(testData.id) : testData.id == null;

        }

        @Override
        public int hashCode() {
            int result = id != null ? id.hashCode() : 0;
            result = 31 * result + checksum;
            return result;
        }
    }
}
