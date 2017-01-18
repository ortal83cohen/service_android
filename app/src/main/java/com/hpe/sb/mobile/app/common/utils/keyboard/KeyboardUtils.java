package com.hpe.sb.mobile.app.common.utils.keyboard;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public final class KeyboardUtils {
    
    /**
     * Supresses instantiation 
     */
    private KeyboardUtils() {
        throw new AssertionError();
    }


    /**
     * Show keyboard and focus to given EditText
     *
     * @param context Context
     * @param target  EditText to focus
     */
    public static void showKeyboard(Context context, EditText target) {
        if (context == null || target == null) {
            return;
        }
        target.requestFocus();
        InputMethodManager imm = getInputMethodManager(context);

        imm.showSoftInput(target, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * Show keyboard and focus to given EditText.
     * uses different parameter from when the activity is not created
     *
     * @param activity Context
     * @param target  EditText to focus
     * @param visibleFromBack  flag indicating if the keyboard should open when returning to the activity with the back button
     */
    public static void showKeyboardWhenActivityCreated(Activity activity, EditText target, boolean visibleFromBack) {
        if (activity == null || target == null) {
            return;
        }

        activity.getWindow().setSoftInputMode(visibleFromBack ? WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE : WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        target.requestFocus();
    }

    /**
     * Show keyboard and focus to given EditText.
     * Use this method if target EditText is in Dialog.
     *
     * @param dialog Dialog
     * @param target EditText to focus
     */
    public static void showKeyboardInDialog(Dialog dialog, EditText target) {
        if (dialog == null || target == null) {
            return;
        }

        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        target.requestFocus();
    }

    /**
     * hide keyboard
     *
     * @param context Context
     * @param target  View that currently has focus
     */
    public static void hideKeyboard(Context context, View target) {
        if (context == null || target == null) {
            return;
        }

        InputMethodManager imm = getInputMethodManager(context);
        imm.hideSoftInputFromWindow(target.getWindowToken(), 0);
        target.clearFocus();
    }

    /**
     * hide keyboard
     *
     * @param activity Activity
     */
    public static void hideKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();

        if (view != null) {
            hideKeyboard(activity, view);
        }
    }

    private static InputMethodManager getInputMethodManager(Context context) {
        return (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }
}
