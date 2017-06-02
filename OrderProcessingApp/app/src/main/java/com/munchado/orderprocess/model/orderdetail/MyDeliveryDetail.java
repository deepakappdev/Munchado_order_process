package com.munchado.orderprocess.model.orderdetail;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by android on 22/2/17.
 */
public class MyDeliveryDetail implements Parcelable {
    public String first_name;
    public String last_Name;
    public String city;
    public String apt_suite;
    public String state;
    public String phone;
    public String address;
    public String zipcode;

    protected MyDeliveryDetail(Parcel in) {
        first_name = in.readString();
        last_Name = in.readString();
        city = in.readString();
        apt_suite = in.readString();
        state = in.readString();
        phone = in.readString();
        address = in.readString();
        zipcode = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(first_name);
        dest.writeString(last_Name);
        dest.writeString(city);
        dest.writeString(apt_suite);
        dest.writeString(state);
        dest.writeString(phone);
        dest.writeString(address);
        dest.writeString(zipcode);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MyDeliveryDetail> CREATOR = new Parcelable.Creator<MyDeliveryDetail>() {
        @Override
        public MyDeliveryDetail createFromParcel(Parcel in) {
            return new MyDeliveryDetail(in);
        }

        @Override
        public MyDeliveryDetail[] newArray(int size) {
            return new MyDeliveryDetail[size];
        }
    };
}