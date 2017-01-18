package com.hpe.sb.mobile.app;

import android.app.Instrumentation;
import android.content.ContentResolver;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;

import com.hpe.sb.mobile.app.infra.db.DbHelper;
import com.hpe.sb.mobile.app.infra.dataClients.DataBlob;
import com.hpe.sb.mobile.app.infra.dataClients.ListDbClient;
import com.hpe.sb.mobile.app.infra.dataClients.Persistable;
import com.hpe.sb.mobile.app.infra.db.GeneralBlobModel;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import rx.Observable;
import rx.Observer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by chovel on 01/05/2016.
 * 
 */
@RunWith(AndroidJUnit4.class)
public class ListDbClientTest extends InstrumentationTestCase {

    Instrumentation instrumentation;
    ListDbClient<TestData> testClient;

    @Before
    public void setUp() throws Exception {
        injectInstrumentation(InstrumentationRegistry.getInstrumentation());
        instrumentation = getInstrumentation();
        ContentResolver contentResolver = instrumentation.getContext().getContentResolver();
        testClient = new ListDbClient<>(TestData.class, contentResolver, "TEST_PERSISTENCE_CLIENT", GeneralBlobModel.TYPE_TEST,
                new DbHelper(contentResolver));
        super.setUp();
    }

    @Test
    public void testUpdateGetAllClear() {

        //insert data to persistence
        final List<TestData> dataToInsert = Arrays.asList(new TestData("1", 0), new TestData("2", 0));
        testClient.update(dataToInsert, new ArrayList<String>(), Arrays.asList("1","2"));

        //assert data was inserted and returns in query
        Observable<List<DataBlob<TestData>>> fromPersistence = testClient.getAllByType();
        fromPersistence.subscribe(new Observer<List<DataBlob<TestData>>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                fail("Error thrown!");
            }

            @Override
            public void onNext(List<DataBlob<TestData>> dataBlobs) {
                for (DataBlob<TestData> dataBlob : dataBlobs) {
                    assertTrue(dataToInsert.contains(dataBlob.getData()));
                }
            }
        });

        //clear all test data
        testClient.clear();

        //assert data is cleared
        fromPersistence = testClient.getAllByType();
        fromPersistence.subscribe(new Observer<List<DataBlob<TestData>>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {
                fail("Error thrown!");
            }

            @Override
            public void onNext(List<DataBlob<TestData>> dataBlobs) {
                assertTrue(dataBlobs.isEmpty());
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
