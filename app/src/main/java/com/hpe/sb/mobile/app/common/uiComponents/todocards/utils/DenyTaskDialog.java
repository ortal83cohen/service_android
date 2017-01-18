package com.hpe.sb.mobile.app.common.uiComponents.todocards.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.infra.eventbus.EventBus;
import com.hpe.sb.mobile.app.common.uiComponents.todocards.animations.TodoCardsAnimationUtil;
import com.hpe.sb.mobile.app.common.utils.ViewUtil;


public class DenyTaskDialog extends Dialog implements android.view.View.OnClickListener {

    private final EventBus<TodoCardsEvent> eventBus;
    private final Activity activity;
    private TodoCardsEvent todoCardsEvent;
    public Button cancelButton;
    public Button doneButton;

    public DenyTaskDialog(Activity activity, EventBus<TodoCardsEvent> eventBus, TodoCardsEvent todoCardsEvent) {
        super(activity);
        this.activity = activity;
        this.todoCardsEvent = todoCardsEvent;
        this.eventBus = eventBus;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.deny_task_dialog);
        cancelButton = (Button) findViewById(R.id.cancel_button);
        doneButton = (Button) findViewById(R.id.done_button);
        cancelButton.setOnClickListener(this);
        doneButton.setOnClickListener(this);
        initCommentField();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cancel_button:
                closeKeyboard();
                dismiss();
                break;
            case R.id.done_button:
                addCommentToTodoCardsEvent();
                closeKeyboard();
                dismiss();
                executeDenyTaskAnimationsAndSendEvent();
                break;
            default:
                break;
        }
        dismiss();
    }

    private void initCommentField() {
        final EditText comment = (EditText) findViewById(R.id.edit_text);
        comment.addTextChangedListener(getDoneButtonListener(comment));
        comment.requestFocus();
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
    }

    private void addCommentToTodoCardsEvent() {
        EditText comment = (EditText) findViewById(R.id.edit_text);
        TodoCardsViewActionsLogicHandler.addAdditionalDataToEvent(todoCardsEvent, TodoCardsViewActionsLogicHandler.COMMENT_DATA, comment.getText().toString());
    }

    private void executeDenyTaskAnimationsAndSendEvent() {
        View view = TodoCardsViewActionsLogicHandler.getEventViewData(todoCardsEvent.getData());
        TodoCardsAnimationUtil cardsAnimationUtil = new TodoCardsAnimationUtil();
        todoCardsEvent.setEventType(TodoCardsEvent.TODO_CARDS_DENY_TASK_EVENT_END);
        cardsAnimationUtil.executeNegativeActionAnimationsAndSendEvent(view, activity.getApplicationContext(), eventBus,  todoCardsEvent);
    }

    private void closeKeyboard() {
        // Check if no view has focus:
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm.isAcceptingText()) {
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
            }
        }
    }

    /**
     * enable DONE button when there is text as comment
     * **/
    private TextWatcher getDoneButtonListener(final EditText comment) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                doneButton.setEnabled(!ViewUtil.isEmptyText(comment));
            }
        };
    }
}