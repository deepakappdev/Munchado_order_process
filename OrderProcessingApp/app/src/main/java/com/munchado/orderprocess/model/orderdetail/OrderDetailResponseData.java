package com.munchado.orderprocess.model.orderdetail;

import android.os.Parcel;
import android.os.Parcelable;

import com.munchado.orderprocess.model.update.Upgrade;

import java.util.ArrayList;

/**
 * Created by android on 22/2/17.
 */
public class OrderDetailResponseData implements Parcelable {
    public String id;
    public String customer_first_name;
    public String customer_last_name;
    public String email;
    public String order_date;
    public String status;
    public String order_type1;
    public String order_type2;
    public String delivery_date;
    public String order_type;
    public String special_instruction;
    public String restaurant_id;
    public String tip_percent;
    public String redeem_point;
    public String pay_via_point;
    public String pay_via_card;
    public String total_amount;
    public String payment_receipt;
    public int is_reviewed;
    public int review_id;
    public int cod;
    public int pay_via_cash;
    public String restaurant_name;
    public String restaurant_address;
    public String rest_code;
    public String restaurant_image_name;
    public MyPaymentDetail my_payment_details;
    public MyDeliveryDetail my_delivery_detail;
    public OrderAmountCalculation order_amount_calculation;
    public ArrayList<MyItemList> item_list = new ArrayList<>();
    public UserActivity user_activity;
    public String user_image;
    public Upgrade fource_update;

    protected OrderDetailResponseData(Parcel in) {
        id = in.readString();
        customer_first_name = in.readString();
        customer_last_name = in.readString();
        email = in.readString();
        order_date = in.readString();
        status = in.readString();
        order_type1 = in.readString();
        order_type2 = in.readString();
        delivery_date = in.readString();
        order_type = in.readString();
        special_instruction = in.readString();
        restaurant_id = in.readString();
        tip_percent = in.readString();
        redeem_point = in.readString();
        pay_via_point = in.readString();
        pay_via_card = in.readString();
        total_amount = in.readString();
        payment_receipt = in.readString();
        is_reviewed = in.readInt();
        review_id = in.readInt();
        cod = in.readInt();
        pay_via_cash = in.readInt();
        restaurant_name = in.readString();
        restaurant_address = in.readString();
        rest_code = in.readString();
        restaurant_image_name = in.readString();
        my_payment_details = (MyPaymentDetail) in.readValue(MyPaymentDetail.class.getClassLoader());
        my_delivery_detail = (MyDeliveryDetail) in.readValue(MyDeliveryDetail.class.getClassLoader());
        order_amount_calculation = (OrderAmountCalculation) in.readValue(OrderAmountCalculation.class.getClassLoader());
        if (in.readByte() == 0x01) {
            item_list = new ArrayList<MyItemList>();
            in.readList(item_list, MyItemList.class.getClassLoader());
        } else {
            item_list = null;
        }
        user_activity = (UserActivity) in.readValue(UserActivity.class.getClassLoader());
        user_image = in.readString();
        fource_update = (Upgrade) in.readValue(Upgrade.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(customer_first_name);
        dest.writeString(customer_last_name);
        dest.writeString(email);
        dest.writeString(order_date);
        dest.writeString(status);
        dest.writeString(order_type1);
        dest.writeString(order_type2);
        dest.writeString(delivery_date);
        dest.writeString(order_type);
        dest.writeString(special_instruction);
        dest.writeString(restaurant_id);
        dest.writeString(tip_percent);
        dest.writeString(redeem_point);
        dest.writeString(pay_via_point);
        dest.writeString(pay_via_card);
        dest.writeString(total_amount);
        dest.writeString(payment_receipt);
        dest.writeInt(is_reviewed);
        dest.writeInt(review_id);
        dest.writeInt(cod);
        dest.writeInt(pay_via_cash);
        dest.writeString(restaurant_name);
        dest.writeString(restaurant_address);
        dest.writeString(rest_code);
        dest.writeString(restaurant_image_name);
        dest.writeValue(my_payment_details);
        dest.writeValue(my_delivery_detail);
        dest.writeValue(order_amount_calculation);
        if (item_list == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(item_list);
        }
        dest.writeValue(user_activity);
        dest.writeString(user_image);
        dest.writeValue(fource_update);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<OrderDetailResponseData> CREATOR = new Parcelable.Creator<OrderDetailResponseData>() {
        @Override
        public OrderDetailResponseData createFromParcel(Parcel in) {
            return new OrderDetailResponseData(in);
        }

        @Override
        public OrderDetailResponseData[] newArray(int size) {
            return new OrderDetailResponseData[size];
        }
    };
}