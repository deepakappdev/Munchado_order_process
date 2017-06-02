package com.munchado.orderprocess.model.orderdetail;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by android on 24/2/17.
 */
public class UserActivity implements Parcelable {
    public int total_user_order;
    public int total_user_review;
    public int total_user_checkin;
    public int total_user_reservation;

    protected UserActivity(Parcel in) {
        total_user_order = in.readInt();
        total_user_review = in.readInt();
        total_user_checkin = in.readInt();
        total_user_reservation = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(total_user_order);
        dest.writeInt(total_user_review);
        dest.writeInt(total_user_checkin);
        dest.writeInt(total_user_reservation);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<UserActivity> CREATOR = new Parcelable.Creator<UserActivity>() {
        @Override
        public UserActivity createFromParcel(Parcel in) {
            return new UserActivity(in);
        }

        @Override
        public UserActivity[] newArray(int size) {
            return new UserActivity[size];
        }
    };
}