package com.munchado.orderprocess.model.orderdetail;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by android on 22/2/17.
 */
public class MyPaymentDetail implements Parcelable {
    public String card_name;
    public String card_number;
    public String card_type;
    public String expiry_year;
    public String expiry_month;
    public String billing_zip;

    protected MyPaymentDetail(Parcel in) {
        card_name = in.readString();
        card_number = in.readString();
        card_type = in.readString();
        expiry_year = in.readString();
        expiry_month = in.readString();
        billing_zip = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(card_name);
        dest.writeString(card_number);
        dest.writeString(card_type);
        dest.writeString(expiry_year);
        dest.writeString(expiry_month);
        dest.writeString(billing_zip);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MyPaymentDetail> CREATOR = new Parcelable.Creator<MyPaymentDetail>() {
        @Override
        public MyPaymentDetail createFromParcel(Parcel in) {
            return new MyPaymentDetail(in);
        }

        @Override
        public MyPaymentDetail[] newArray(int size) {
            return new MyPaymentDetail[size];
        }
    };
}