package com.munchado.orderprocess.model.orderdetail;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by android on 22/2/17.
 */
public class MyItemList implements Parcelable {
    public String order_item_id;
    public String item_name;
    public String item_qty;
    public String unit_price;
    public String item_special_instruction;
    public int item_status;
    public ArrayList<AddonsList> addons_list = new ArrayList<>();


    protected MyItemList(Parcel in) {
        order_item_id = in.readString();
        item_name = in.readString();
        item_qty = in.readString();
        unit_price = in.readString();
        item_special_instruction = in.readString();
        item_status = in.readInt();
        if (in.readByte() == 0x01) {
            addons_list = new ArrayList<AddonsList>();
            in.readList(addons_list, AddonsList.class.getClassLoader());
        } else {
            addons_list = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(order_item_id);
        dest.writeString(item_name);
        dest.writeString(item_qty);
        dest.writeString(unit_price);
        dest.writeString(item_special_instruction);
        dest.writeInt(item_status);
        if (addons_list == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(addons_list);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<MyItemList> CREATOR = new Parcelable.Creator<MyItemList>() {
        @Override
        public MyItemList createFromParcel(Parcel in) {
            return new MyItemList(in);
        }

        @Override
        public MyItemList[] newArray(int size) {
            return new MyItemList[size];
        }
    };
}