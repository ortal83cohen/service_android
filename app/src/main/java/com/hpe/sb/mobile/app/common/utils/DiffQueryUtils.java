package com.hpe.sb.mobile.app.common.utils;

import com.hpe.sb.mobile.app.serverModel.diff.DiffQuery;
import com.hpe.sb.mobile.app.serverModel.diff.DiffQueryItem;
import com.hpe.sb.mobile.app.infra.dataClients.Persistable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DiffQueryUtils {

    public static DiffQuery createDiffQuery(Collection<? extends Persistable> collection) {
        List<DiffQueryItem> items = new ArrayList<>();
        for (Persistable item : collection) {
            items.add(new DiffQueryItem(item.getId(), item.getChecksum()));
        }
        DiffQuery diffQuery = new DiffQuery();
        diffQuery.setItems(items);

        return diffQuery;
    }

}
