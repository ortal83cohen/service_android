package com.hpe.sb.mobile.app.features.request.recycleview;

/**
 * Created by malikdav on 17/05/2016.
 */
public class CurrentPositionHolder {
    private int position;

    public void setPosition(int position) {
        this.position = position;
    }

    public int getCurrentPosition() {
        return position;
    }

    public void decrement() {
        if (position > 0) {
            position--;
        }
    }

    public void increment(int childCount) {
        if (position < childCount - 1) {
            position++;
        }
    }

    public void updatePositionByDirection(int direction, int childCount) {

        if (direction < 0) {
            decrement();
        } else {
            increment(childCount);
        }
    }

    public int updatePosition(int position, int itemCount) {
        if (position >= 0 && position <= itemCount - 1) {
            setPosition(position);
        }

        return this.position;
    }
}
