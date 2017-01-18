package com.hpe.sb.mobile.app.features.request.recycleview.viewholder;

import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.infra.baseActivities.BaseActivity;
import com.hpe.sb.mobile.app.infra.eventbus.EventBus;
import com.hpe.sb.mobile.app.common.uiComponents.metricFont.MetricFontCustomButton;
import com.hpe.sb.mobile.app.features.request.NewRequestFlowState;
import com.hpe.sb.mobile.app.features.request.event.NewRequestFormEvent;
import com.hpe.sb.mobile.app.features.request.recycleview.type.AlmostThereViewType;

import android.view.View;

/**
 * Created by malikdav on 29/04/2016.
 */
public class AlmostThereViewHolder extends NewRequestViewHolder<AlmostThereViewType> {

    private final View loadingGif;

    private MetricFontCustomButton submitButton;
    public AlmostThereViewHolder(View view, EventBus<NewRequestFormEvent> eventBus) {
        super(view, eventBus);
        submitButton = (MetricFontCustomButton) view.findViewById(R.id.submit_button);
        loadingGif = view.findViewById(R.id.loading_gif);
    }

    @Override
    public void bind(AlmostThereViewType viewType, final BaseActivity activity, NewRequestFlowState flowState) {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!activity.isNetworkConnectionAvailable()){
                    submitButton.setEnabled(true);
                    activity.showNoConnectionSnackbar();
                    return;
                }
                submitButton.setText(R.string.submitting);
                submitButton.setEnabled(false);
                eventBus.send(new NewRequestFormEvent(NewRequestFormEvent.SUBMIT_REQUEST, null));
                loadingGif.setVisibility(View.VISIBLE);
            }
        });
    }
}
