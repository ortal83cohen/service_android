package com.hpe.sb.mobile.app.features.router;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.hpe.sb.mobile.app.R;

import java.util.List;


public class BaseDialog extends Dialog implements View.OnClickListener {

    private final Activity activity;
    private int layoutId;
    private List<ActionButton> actionButtons;

    public BaseDialog(Activity activity, int layoutId, List<ActionButton> actionButtons) {
        super(activity);
        this.activity = activity;
        this.layoutId = layoutId;
        this.actionButtons = actionButtons;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false); //modal dialog
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(layoutId);
        for(ActionButton actionButton : actionButtons){
            final ActionButton actionButtonFinal = actionButton;
            Button button = (Button) findViewById(actionButton.buttonLayoutId);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    actionButtonFinal.onClickListener.onClick(v);
                    dismiss();
                }
            });
        }

    }

    @Override
    public void onClick(View v) {
        activity.finish();
    }

    public static class ActionButton{
        private int buttonLayoutId;
        private View.OnClickListener onClickListener;
        public ActionButton(int buttonLayoutId , View.OnClickListener onClickListener) {
            this.buttonLayoutId = buttonLayoutId;
            this.onClickListener = onClickListener;
        }

        public void setOnClickListener(View.OnClickListener onClickListener) {
            this.onClickListener = onClickListener;
        }
    }
}