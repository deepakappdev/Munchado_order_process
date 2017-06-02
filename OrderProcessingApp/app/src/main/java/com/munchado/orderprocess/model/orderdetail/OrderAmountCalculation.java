package com.munchado.orderprocess.model.orderdetail;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by android on 22/2/17.
 */
public class OrderAmountCalculation implements Parcelable {
    public String subtotal;
    public String tax_amount;
    public String tip_amount;
    public String delivery_charge;
    public String discount;
    public String promocode_discount;
    public String redeem_point;
    public String pay_via_point;
    public String pay_via_card;
    public String pay_via_cash;
    public String total_order_price;

    protected OrderAmountCalculation(Parcel in) {
        subtotal = in.readString();
        tax_amount = in.readString();
        tip_amount = in.readString();
        delivery_charge = in.readString();
        discount = in.readString();
        promocode_discount = in.readString();
        redeem_point = in.readString();
        pay_via_point = in.readString();
        pay_via_card = in.readString();
        pay_via_cash = in.readString();
        total_order_price = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(subtotal);
        dest.writeString(tax_amount);
        dest.writeString(tip_amount);
        dest.writeString(delivery_charge);
        dest.writeString(discount);
        dest.writeString(promocode_discount);
        dest.writeString(redeem_point);
        dest.writeString(pay_via_point);
        dest.writeString(pay_via_card);
        dest.writeString(pay_via_cash);
        dest.writeString(total_order_price);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<OrderAmountCalculation> CREATOR = new Parcelable.Creator<OrderAmountCalculation>() {
        @Override
        public OrderAmountCalculation createFromParcel(Parcel in) {
            return new OrderAmountCalculation(in);
        }

        @Override
        public OrderAmountCalculation[] newArray(int size) {
            return new OrderAmountCalculation[size];
        }
    };
}