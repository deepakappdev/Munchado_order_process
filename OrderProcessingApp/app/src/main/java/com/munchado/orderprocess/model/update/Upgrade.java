package com.munchado.orderprocess.model.update;

import android.os.Parcel;
import android.os.Parcelable;

public class Upgrade implements Parcelable {
    public String upgrade_type;
    public int counter;
    public String message;
    public String apk_link;
    public boolean clear_data;

    protected Upgrade(Parcel in) {
        upgrade_type = in.readString();
        counter = in.readInt();
        message = in.readString();
        apk_link = in.readString();
        clear_data = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(upgrade_type);
        dest.writeInt(counter);
        dest.writeString(message);
        dest.writeString(apk_link);
        dest.writeByte((byte) (clear_data ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Upgrade> CREATOR = new Parcelable.Creator<Upgrade>() {
        @Override
        public Upgrade createFromParcel(Parcel in) {
            return new Upgrade(in);
        }

        @Override
        public Upgrade[] newArray(int size) {
            return new Upgrade[size];
        }
    };
}