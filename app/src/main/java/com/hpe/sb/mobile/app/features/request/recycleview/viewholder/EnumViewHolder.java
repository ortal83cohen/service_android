package com.hpe.sb.mobile.app.features.request.recycleview.viewholder;

import android.app.Activity;
import android.view.View;
import android.widget.RadioGroup;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.infra.baseActivities.BaseActivity;
import com.hpe.sb.mobile.app.infra.eventbus.EventBus;
import com.hpe.sb.mobile.app.common.uiComponents.metricFont.MetricFontCustomButton;
import com.hpe.sb.mobile.app.common.uiComponents.metricFont.MetricFontCustomRadioButton;
import com.hpe.sb.mobile.app.common.uiComponents.metricFont.MetricFontCustomTextView;
import com.hpe.sb.mobile.app.serverModel.form.FormEnum;
import com.hpe.sb.mobile.app.serverModel.form.FormEnumDescriptor;
import com.hpe.sb.mobile.app.serverModel.form.FormField;
import com.hpe.sb.mobile.app.serverModel.request.FullRequest;
import com.hpe.sb.mobile.app.features.request.NewRequestFlowState;
import com.hpe.sb.mobile.app.features.request.event.NewRequestFormEvent;
import com.hpe.sb.mobile.app.features.request.recycleview.type.EnumViewType;

import java.util.List;

/**
 * Created by malikdav on 12/05/2016.
 */
public class EnumViewHolder extends NewRequestViewHolder<EnumViewType> {
    private final MetricFontCustomTextView title;
    private final RadioGroup radioGroup;
    private final MetricFontCustomButton nextButton;
    private boolean radioButtonAdded;

    public EnumViewHolder(View view, EventBus<NewRequestFormEvent> eventBus) {
        super(view, eventBus);

        title = (MetricFontCustomTextView) itemView.findViewById(R.id.title1);
        radioGroup = (RadioGroup) itemView.findViewById(R.id.radio_group);
        nextButton = (MetricFontCustomButton) itemView.findViewById(R.id.next_button);
    }

    @Override
    public void bind(EnumViewType viewType, BaseActivity activity, NewRequestFlowState flowState) {
        FormField formField = viewType.getFormField();
        List<FormEnumDescriptor> enumDescriptors = viewType.getEnumDescriptors();
        FormEnumDescriptor enumDescriptor = getEnumDescriptor(formField.getReferenceName(), enumDescriptors);
        List<FormEnum> enums = enumDescriptor.getEnums();
        initEnums(enums, activity);
        initNextButton(formField.getModelAttribute(), enums, flowState.getFullRequest(), activity);
        title.setText(enumDescriptor.getDisplayLabel());
    }

    private void initNextButton(final String modelAttribute, final List<FormEnum> enums, final FullRequest fullRequest, final BaseActivity activity) {
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //check connectivity
                final boolean networkConnectionAvailable = activity.isNetworkConnectionAvailable();
                if(!networkConnectionAvailable){
                    nextButton.setEnabled(true);
                    activity.showNoConnectionSnackbar();
                    return;
                }

                nextButton.setEnabled(false);
                int enumIndex = radioGroup.getCheckedRadioButtonId();
                String enumValue = enums.get(enumIndex).getValue();
                fullRequest.setProperty(modelAttribute, enumValue);

                eventBus.send(new NewRequestFormEvent(NewRequestFormEvent.NEXT_FORM_FIELD, null));
            }
        });

        // enable next button when selection is changed
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                nextButton.setEnabled(true);
            }
        });
    }

    private void initEnums(List<FormEnum> enums, Activity activity) {
        if(!radioButtonAdded) {
            radioButtonAdded = true;
            for (int i = 0; i < enums.size(); i++) {
                FormEnum anEnum = enums.get(i);
                MetricFontCustomRadioButton radioButton = new MetricFontCustomRadioButton(activity);
                radioButton.setId(i);
                radioButton.setText(anEnum.getDisplayLabel());
                radioButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextButton.setEnabled(true);
                    }
                });
                radioGroup.addView(radioButton);
            }
        }
    }

    private FormEnumDescriptor getEnumDescriptor(String referenceName, List<FormEnumDescriptor> enumDescriptors) {
        for (FormEnumDescriptor enumDescriptor : enumDescriptors) {
            if (enumDescriptor.getId().equals(referenceName)) {
                return enumDescriptor;
            }
        }

        // can be reached because we validate the descriptor is there before we add an EnumViewHolder
        return null;
    }
}
