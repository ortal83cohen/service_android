package com.hpe.sb.mobile.app.serverModel.user.useritems;

import com.hpe.sb.mobile.app.infra.dataClients.Identifiable;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cohenort on 21/04/2016.
 */
public abstract class UserItem implements Parcelable, Identifiable {

    protected UserItem() {
    }

    protected UserItem(Parcel in) {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    abstract public String getTitle();

    abstract public String getId();

    abstract public String getDescription();

    abstract public String getEntityType();

    @Override
    public String toString() {
        return "id = " + getId();
    }

}
