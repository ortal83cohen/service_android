package com.hpe.sb.mobile.app.common.uiComponents.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import com.hpe.sb.mobile.app.R;

/**
 * Created by malikdav on 26/04/2016.
 * NOTE: in order to use it the calling activity must implement GeneralErrorDialogFragment.DialogListener
 * interface, otherwise ClassCast exception will be thrown at runtime
 */
public class GeneralErrorDialogFragment extends DialogFragment {
    public static final String TAG = GeneralErrorDialogFragment.class.getSimpleName();
    public static final String ERROR_TITLE_KEY = "ERROR_TITLE_KEY";
    public static final String ERROR_DESC_KEY = "ERROR_DESC_KEY";
    public static final String ERROR_DESC = "ERROR_DESC";
    public static final String POSITIVE_ACTION_KEY= "POSITIVE_ACTION_KEY";

    private int errorTitleMsgId;
    private int messageId;
    private String message;
    private int positiveActionMsgId;

    public interface DialogListener {
        public void onDialogPositiveClick(DialogFragment dialog);
        public void onDialogNegativeClick(DialogFragment dialog);
    }

    DialogListener dialogListener;

    // Override the Fragment.onAttach() method to instantiate the DialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            dialogListener = (DialogListener) activity;
        } catch (ClassCastException e) {
            Log.e(TAG, activity.toString()
                    + " must implement DialogListener");
            throw new ClassCastException(activity.toString()
                    + " must implement DialogListener");
        }
        this.setCancelable(false);
    }

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
        extractArgs(args);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(ERROR_TITLE_KEY, errorTitleMsgId);
        outState.putInt(ERROR_DESC_KEY, messageId);
        outState.putString(ERROR_DESC, message);
        outState.putInt(POSITIVE_ACTION_KEY, positiveActionMsgId);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if(savedInstanceState != null) {
            extractArgs(savedInstanceState);
        }else{
            messageId = messageId == 0 ? R.string.general_error: messageId;
            positiveActionMsgId = positiveActionMsgId == 0? R.string.ok: positiveActionMsgId;
        }
        // Open error dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                .setCancelable(false)
                .setPositiveButton(positiveActionMsgId, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialogListener.onDialogPositiveClick(GeneralErrorDialogFragment.this);
                    }
                });
        if(errorTitleMsgId != 0){
            builder.setTitle(errorTitleMsgId);
        }
        if(message != null){
            builder.setMessage(message);
        }else{
            builder.setMessage(messageId);
        }
        // Create the AlertDialog object and return it
        return builder.create();
    }


    private void extractArgs(Bundle args) {
        errorTitleMsgId = args.getInt(ERROR_TITLE_KEY);
        messageId = args.getInt(ERROR_DESC_KEY, R.string.general_error);
        message = args.getString(ERROR_DESC);
        positiveActionMsgId = args.getInt(POSITIVE_ACTION_KEY, R.string.ok);
    }
}
