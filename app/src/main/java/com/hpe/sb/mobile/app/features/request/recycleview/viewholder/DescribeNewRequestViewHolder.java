package com.hpe.sb.mobile.app.features.request.recycleview.viewholder;

import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.infra.baseActivities.BaseActivity;
import com.hpe.sb.mobile.app.infra.eventbus.EventBus;
import com.hpe.sb.mobile.app.common.uiComponents.metricFont.MetricFontCustomActionButton;
import com.hpe.sb.mobile.app.common.uiComponents.metricFont.MetricFontCustomTextView;
import com.hpe.sb.mobile.app.features.request.NewRequestFlowState;
import com.hpe.sb.mobile.app.features.request.event.NewRequestFormEvent;
import com.hpe.sb.mobile.app.features.request.recycleview.type.DescribeNewRequestViewType;
import com.hpe.sb.mobile.app.common.utils.ViewUtil;
import com.hpe.sb.mobile.app.common.utils.keyboard.KeyboardUtils;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import rx.functions.Action1;

/**
 * Created by malikdav on 25/04/2016.
 */
public class DescribeNewRequestViewHolder extends NewRequestViewHolder<DescribeNewRequestViewType> {

    private static final String HOLDER_IDENTIFIER = "DescribeNewRequestViewHolder";

//    private static final int REQ_CODE_SPEECH_INPUT_REPLY = 101;
//    private static final int REQ_CODE_SPEECH_INPUT = 100;

    private MetricFontCustomTextView header;

    private MetricFontCustomActionButton nextButton;

    //    private ImageButton micButton;
    private EditText description;

    private String descriptionText = null;
    //    private TextToSpeech textToSpeech;


    public DescribeNewRequestViewHolder(View itemView, EventBus<NewRequestFormEvent> eventBus) {
        super(itemView, eventBus);

        header = (MetricFontCustomTextView) itemView.findViewById(R.id.title1);
        description = (EditText) itemView.findViewById(R.id.edit_text);
//        micButton = (ImageButton) itemView.findViewById(R.id.btnSpeak);
        nextButton = (MetricFontCustomActionButton) itemView.findViewById(R.id.next_button);
        subscribeEventBusListeners();
    }

    @Override
    public void bind(DescribeNewRequestViewType viewType, BaseActivity activity,
                     NewRequestFlowState flowState) {
        header.setText(activity.getString(R.string.describe_new_request));
        initDescriptionEditText(activity);
        initNextButton(activity);
        nextButton.setEnabled(false);
//        initSpeechButton(activity);
//        initTextToSpeech(activity);
    }

    //    private void initSpeechButton(final Activity activity) {
//        micButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                promptSpeechInput(activity);
//            }
//        });
//    }

    private void initDescriptionEditText(Activity activity) {
        KeyboardUtils.showKeyboard(activity, description);
        description.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                nextButton.setEnabled(
                        !ViewUtil.isEmptyText(description) && !description.getText().toString()
                                .equals(descriptionText));
            }
        });
    }

//    private void initTextToSpeech(final Activity activity) {
//        textToSpeech = new TextToSpeech(activity, new TextToSpeech.OnInitListener() {
//            @Override
//            public void onInit(int status) {
//                if (status != TextToSpeech.ERROR) {
//                    textToSpeech.setLanguage(Locale.US);
//                    textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
//                        @Override
//                        public void onStart(String utteranceId) {
//
//                         }
//
//                        @Override
//                        public void onDone(String utteranceId) {
//                            promptSpeechReply(activity);
//                        }
//
//                        @Override
//                        public void onError(String utteranceId) {
//
//                        }
//                    });
//                }
//            }
//        });
//    }


    private void initNextButton(final BaseActivity activity) {
        nextButton.setEnabled(false);
        nextButton.setOnProcessState(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ViewUtil.isEmptyText(description)) {
                    if (handleNoNetwork(activity)) {
                        return;
                    }
                    nextButton.setEnabled(false);
                    KeyboardUtils.hideKeyboard(activity);
                    descriptionText = description.getText().toString();
                    eventBus.send(
                            new NewRequestFormEvent(NewRequestFormEvent.DESCRIBED_YOUR_REQUEST,
                                    descriptionText));
                }
            }
        });
        nextButton.setOnClickNormalState(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check connectivity
                if (handleNoNetwork(activity)) {
                    return;
                }
                nextButton.setProgress(20);
                nextButton.setEnabled(false);
                KeyboardUtils.hideKeyboard(activity);
                descriptionText = description.getText().toString();
                NewRequestFormEvent newRequestFormEvent = new NewRequestFormEvent(
                        NewRequestFormEvent.DESCRIBED_YOUR_REQUEST,
                        descriptionText);
                newRequestFormEvent.setData2(HOLDER_IDENTIFIER);
                eventBus.send(newRequestFormEvent);
            }
        }).build();

    }

    private boolean handleNoNetwork(BaseActivity activity) {
        final boolean networkConnectionAvailable = activity.isNetworkConnectionAvailable();
        if(!networkConnectionAvailable){
            nextButton.setProgress(0);
            nextButton.setEnabled(true);
            KeyboardUtils.hideKeyboard(activity);
            activity.showNoConnectionSnackbar();
            return true;
        }
        return false;
    }

    private void subscribeEventBusListeners() {
        eventBus.toObserverable()
                .subscribe((new Action1<NewRequestFormEvent>() {
                    @Override
                    public void call(NewRequestFormEvent event) {

                        switch (event.getEventType()) {
                            case NewRequestFormEvent.FINISH_LOADING: {
                                String requestDescription = event.getData();
                                if (requestDescription != null && requestDescription.equals(HOLDER_IDENTIFIER)) {
                                    nextButton.setProgress(0);
                                }
                            }
                        }
                    }
                }));
    }
//    /**
//     * Showing google speech input dialog
//     */
//    private void promptSpeechInput(Activity activity) {
//        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
//                activity.getString(R.string.speech_prompt));
//        try {
//            activity.startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
//        } catch (ActivityNotFoundException a) {
//            Toast.makeText(activity,
//                    activity.getString(R.string.speech_not_supported),
//                    Toast.LENGTH_SHORT).show();
//        }
//    }

//    /**
//     * Showing google speech input dialog
//     */
//    private void promptSpeechReply(Activity activity) {
//        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
//                activity.getString(R.string.yes_or_no));
//        try {
//            activity.startActivityForResult(intent, REQ_CODE_SPEECH_INPUT_REPLY);
//        } catch (ActivityNotFoundException a) {
//            Toast.makeText(activity,
//                    activity.getString(R.string.speech_not_supported),
//                    Toast.LENGTH_SHORT).show();
//        }
//    }

}
