package com.hpe.sb.mobile.app.common.uiComponents.todocards.viewholders;

import android.view.View;

/**
 * Created by chovel on 14/04/2016.
 */
public interface CardViewHolder<T> {

    void bind(T item);

    void onCommentClick(View view);


}
