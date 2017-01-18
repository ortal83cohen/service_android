package com.hpe.sb.mobile.app.common.utils;

import com.hpe.sb.mobile.app.infra.dataClients.Identifiable;
import com.hpe.sb.mobile.app.serverModel.diff.DiffResult;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import rx.functions.Func2;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by steflin on 20/07/2016.
 */
public class MergeDataUtilsTest {
    private TestObject testObject1a = new TestObject("1", "a");
    private TestObject testObject3a = new TestObject("3", "a");
    private TestObject testObject1b = new TestObject("1", "b");
    private TestObject testObject2b = new TestObject("2", "b");
    private TestObject testObject3b = new TestObject("3", "b");

    private List<String> ids = Arrays.asList("1", "2", "3");
    private List<TestObject> priority1 = Arrays.asList(testObject1a, testObject3a);
    private List<TestObject> priority2 = Arrays.asList(testObject1b, testObject2b, testObject3b);

    @Test
    public void testCreateMergeFunction() {
        Func2<List<TestObject>, DiffResult<TestObject>, List<TestObject>> mergeFunction =
                MergeDataUtils.createMergeFunction("tag1", TestObject.class);

        DiffResult<TestObject> diffResult = new DiffResult<>(priority1, Collections.<String>emptyList(), ids);

        validateMerged(mergeFunction.call(priority2, diffResult));
    }

    @Test
    public void testMerge() {
        validateMerged(MergeDataUtils.merge("tag1", ids, priority1, priority2));
    }

    private void validateMerged(List<TestObject> merged) {
        assertEquals("merged list size", 3, merged.size());
        assertTrue(merged.contains(testObject1a));
        assertTrue(merged.contains(testObject2b));
        assertTrue(merged.contains(testObject3a));
    }
}

class TestObject implements Identifiable {
    private String id;
    private String data;

    public TestObject(String id, String data) {
        this.id = id;
        this.data = data;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        TestObject that = (TestObject) o;

        if(id != null ? !id.equals(that.id) : that.id != null) return false;
        return data != null ? data.equals(that.data) : that.data == null;

    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }
}
