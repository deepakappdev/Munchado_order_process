package com.munchado.orderprocess.model.orderdetail;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by android on 22/2/17.
 */
public class AddonsList implements Parcelable {
    public String addon_price;

    public String addon_quantity;

    public String addons_total_price;

    public String addon_name;

    public String addons_id;

    protected AddonsList(Parcel in) {
        addon_price = in.readString();
        addon_quantity = in.readString();
        addons_total_price = in.readString();
        addon_name = in.readString();
        addons_id = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(addon_price);
        dest.writeString(addon_quantity);
        dest.writeString(addons_total_price);
        dest.writeString(addon_name);
        dest.writeString(addons_id);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<AddonsList> CREATOR = new Parcelable.Creator<AddonsList>() {
        @Override
        public AddonsList createFromParcel(Parcel in) {
            return new AddonsList(in);
        }

        @Override
        public AddonsList[] newArray(int size) {
            return new AddonsList[size];
        }
    };
}