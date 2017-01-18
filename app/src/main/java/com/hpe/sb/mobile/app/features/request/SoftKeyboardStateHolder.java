package com.hpe.sb.mobile.app.features.request;

/**
 * Created by malikdav on 30/05/2016.
 */
public class SoftKeyboardStateHolder {
    private boolean isKeyboardOpen;
    private OnHideKeyboardListener onHideKeyboardListener;

    public void setIsKeyboardOpen(boolean isKeyboardOpen) {

        if(this.isKeyboardOpen != isKeyboardOpen) {
            this.isKeyboardOpen = isKeyboardOpen;

            if(!isKeyboardOpen) {
                if(onHideKeyboardListener != null) {
                    onHideKeyboardListener.invoke();
                }
            }
        }
    }

    public boolean isKeyboardOpen() {
        return isKeyboardOpen;
    }

    public void setOnHideKeyboardListener(OnHideKeyboardListener onHideKeyboardListener) {
        this.onHideKeyboardListener = onHideKeyboardListener;
    }

    public interface OnHideKeyboardListener {
        void invoke();
    }
}
