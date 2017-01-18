package com.hpe.sb.mobile.app.common.uiComponents.todocards;


import com.hpe.sb.mobile.app.serverModel.user.useritems.ApprovalUserItem;
import com.hpe.sb.mobile.app.serverModel.user.useritems.RequestActiveUserItem;
import com.hpe.sb.mobile.app.serverModel.user.useritems.RequestFeedbackUserItem;
import com.hpe.sb.mobile.app.serverModel.user.useritems.UserItem;
import com.hpe.sb.mobile.app.serverModel.user.useritems.RequestResolveUserItem;

import java.util.Arrays;
import java.util.List;

/**
 * Created by chovel on 07/04/2016.
 *
 */
public class TodoCardData {

    Class modelClazz;
    Object item;
    public static List<Class> classList = Arrays.asList(new Class[]{RequestActiveUserItem.class, ApprovalUserItem.class, RequestFeedbackUserItem.class, RequestResolveUserItem.class});
    public static final int ACTIVE_REQUEST = 0;
    public static final int APPROVAL = 1;
    public static final int FEEDBACK_REQUEST = 2;
    public static final int REQUEST_RESOLVED = 3;

    public <T> TodoCardData(T item, Class<T> clazz) {
        this.item = item;
        this.modelClazz = clazz;
    }

    public static int getTypeCount() {
        return classList.size();
    }

    public Class getModelClass() {
        return modelClazz;
    }

    public static int getViewTypeIndex(Class clazz) {
        return classList.indexOf(clazz);
    }

    public <T> T getData(Class<T> clazz) {
        if ((classList.contains(clazz) || clazz.equals(UserItem.class)) && clazz.isAssignableFrom(modelClazz)) {
            return clazz.cast(item);
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        TodoCardData that = (TodoCardData) o;

        if (modelClazz != null ? !modelClazz.equals(that.modelClazz) : that.modelClazz != null) {
            return false;
        }
        return item != null ? item.equals(that.item) : that.item == null;

    }

    @Override
    public int hashCode() {
        int result = modelClazz != null ? modelClazz.hashCode() : 0;
        result = 31 * result + (item != null ? item.hashCode() : 0);
        return result;
    }

}
