package com.hpe.sb.mobile.app.common.uiComponents.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.features.home.MainActivity;


public class AuthErrorDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Open error dialog leading to main activity
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.auth_error_header).setMessage(R.string.auth_error)
                .setCancelable(false)
                .setPositiveButton(R.string.auth_error_login, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = MainActivity.createIntentForLogout(getActivity());
                        startActivity (intent);
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
