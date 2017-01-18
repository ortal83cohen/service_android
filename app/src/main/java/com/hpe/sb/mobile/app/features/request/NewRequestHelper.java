package com.hpe.sb.mobile.app.features.request;

import com.hpe.sb.mobile.app.features.request.recycleview.type.NewRequestViewType;

import java.util.List;

/**
 * Created by malikdav on 18/05/2016.
 */
public class NewRequestHelper {
    public int indexOfViewType(NewRequestViewType desiredViewType, List<NewRequestViewType> viewTypes) {
        for(int i = 0; i < viewTypes.size(); i++) {
            if(viewTypes.get(i).getClass() == desiredViewType.getClass()) {
                return i;
            }
        }

        return -1;
    }
}
