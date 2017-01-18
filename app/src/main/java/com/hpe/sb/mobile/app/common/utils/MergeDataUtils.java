package com.hpe.sb.mobile.app.common.utils;

import android.util.Log;
import com.hpe.sb.mobile.app.infra.dataClients.Identifiable;
import com.hpe.sb.mobile.app.serverModel.diff.DiffResult;

import java.util.*;

import rx.functions.Func2;

public class MergeDataUtils {

    public static <T extends Identifiable> Func2<List<T>, DiffResult<T>, List<T>> createMergeFunction(final String tag, Class<T> clazz) {
        return new Func2<List<T>, DiffResult<T>, List<T>>() {
            @Override
            public List<T> call(List<T> fromDb, DiffResult<T> fromRest) {
                Log.d(tag, "Merging results from rest and db");
                List<T> merge = merge(tag, fromRest.getOrderedIds(), fromRest.getAddedOrUpdated(), fromDb);
                Log.d(tag, merge.size() + " items after merge");
                return merge;
            }
        };
    }

    public static <T extends Identifiable> List<T> merge(final String tag, Collection<String> ids, List<T> priority1, List<T> priority2) {
        Log.d(tag, "Merging results from with ids: " + ids + ", priority1 : " + priority1 +" and priority2 : " + priority2);
        return merge(ids, toIdMap(priority1), toIdMap(priority2));
    }

    private static <T> List<T> merge(Collection<String> ids, Map<String, T> priority1, Map<String, T> priority2) {
        List<T> result = new ArrayList<>();
        for (String id : ids) {
            if (priority1.containsKey(id)) {
                result.add(priority1.get(id));
            } else if (priority2.containsKey(id)){
                result.add(priority2.get(id));
            }
        }

        return result;
    }

    private static <T extends Identifiable> Map<String, T> toIdMap(List<T> categories) {
        Map<String, T> idToCategoryMap = new HashMap<>();
        for (T category : categories) {
            idToCategoryMap.put(category.getId(), category);
        }

        return idToCategoryMap;
    }
}
