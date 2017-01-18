package com.hpe.sb.mobile.app.features.request.recycleview.viewholder;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.infra.baseActivities.BaseActivity;
import com.hpe.sb.mobile.app.infra.eventbus.EventBus;
import com.hpe.sb.mobile.app.common.uiComponents.metricFont.MetricFontCustomActionButton;
import com.hpe.sb.mobile.app.common.uiComponents.metricFont.MetricFontCustomTextView;
import com.hpe.sb.mobile.app.serverModel.form.FormField;
import com.hpe.sb.mobile.app.serverModel.request.FullRequest;
import com.hpe.sb.mobile.app.features.request.NewRequestFlowState;
import com.hpe.sb.mobile.app.features.request.event.NewRequestFormEvent;
import com.hpe.sb.mobile.app.features.request.recycleview.type.RichTextViewType;
import com.hpe.sb.mobile.app.common.utils.ViewUtil;
import com.hpe.sb.mobile.app.common.utils.keyboard.KeyboardUtils;

/**
 * Created by malikdav on 28/04/2016.
 */
public class RichTextViewHolder extends NewRequestViewHolder<RichTextViewType> {

    private MetricFontCustomTextView header;
    private MetricFontCustomActionButton nextButton;
//    private ImageButton micButton;
    private EditText richText;
//    private TextToSpeech textToSpeech;

    public RichTextViewHolder(View view, EventBus<NewRequestFormEvent> eventBus) {
        super(view, eventBus);

        header = (MetricFontCustomTextView) itemView.findViewById(R.id.title1);
        richText = (EditText) itemView.findViewById(R.id.edit_text);
//        micButton = (ImageButton) itemView.findViewById(R.id.btnSpeak);
        nextButton = (MetricFontCustomActionButton) itemView.findViewById(R.id.next_button);
    }

    @Override
    public void bind(RichTextViewType viewType, BaseActivity activity, NewRequestFlowState flowState) {
        FormField formField = viewType.getFormField();
        header.setText(formField.getDisplayLabel());
//        initSpeechButton(activity);
//        initTextToSpeech(activity);
        initEditText(formField, flowState.getRequestDescriptionCandidate(), activity);
        initNextButton(flowState.getFullRequest(), formField, activity);

    }

    private void initNextButton(final FullRequest fullRequest, final FormField formField, final BaseActivity activity) {
        nextButton.setOnClickNormalState(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ViewUtil.isEmptyText(richText)) {

                    //check connectivity
                    final boolean networkConnectionAvailable = activity.isNetworkConnectionAvailable();
                    if(!networkConnectionAvailable){
                        nextButton.setEnabled(true);
                        KeyboardUtils.hideKeyboard(activity);
                        activity.showNoConnectionSnackbar();
                        return;
                    }

                    nextButton.setEnabled(false);
                    KeyboardUtils.hideKeyboard(activity);
                    fullRequest.setProperty(formField.getModelAttribute(), richText.getText().toString());

                    eventBus.send(new NewRequestFormEvent(NewRequestFormEvent.NEXT_FORM_FIELD, null));
                }
            }
        }).build();
        if(ViewUtil.isEmptyText(richText)){
            nextButton.setEnabled(false);
        }
    }

    private void initEditText(FormField formField, String requestDescriptionCandidate, final Activity activity) {
        richText.setHint(formField.getPlaceholder());
        richText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                nextButton.setEnabled(!ViewUtil.isEmptyText(richText));
            }
        });

        //TODO:Dudi- Ugly solution - in case of description i need to set the rich text with the candidate description
        // i have 3 options to overcome this.
        // 1. expose api to get propel and saw request description model attribute so i could check it here.
        // 2. in the new request activity i can set a flag for the first form field as a request, after all we know that the fitst one is the request - also ugly.
        // 3. We can assume the model attribute contains description, as i just implementet here:
        if(isDescription(formField)) {
            richText.setText(requestDescriptionCandidate);
        }

        richText.postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyboardUtils.showKeyboard(activity, richText);
            }
        }, 100);
    }

    private boolean isDescription(FormField formField) {
        return formField.getModelAttribute().toLowerCase().contains("description");
    }
}
