package com.hpe.sb.mobile.app.common.uiComponents.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import com.hpe.sb.mobile.app.R;
import com.hpe.sb.mobile.app.features.router.RouteBundleFactory;
import com.hpe.sb.mobile.app.features.router.RouterActivity;
import com.hpe.sb.mobile.app.features.router.Routes;


public class UnexpectedErrorDialogFragment extends DialogFragment {
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Open error dialog leading to router activity
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.unexpected_error_header).setMessage(R.string.unexpected_error)
                .setCancelable(false)
                .setPositiveButton(R.string.unexpected_error_restart, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(RouteBundleFactory.ROUTE_CODE_EXTRA, Routes.DEFAULT);
                        Intent intent = RouterActivity.createIntent(getActivity(), bundle);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                        startActivity (intent);
                    }
                }).setNegativeButton(R.string.unexpected_error_dismiss, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dismiss();
            }
        });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
