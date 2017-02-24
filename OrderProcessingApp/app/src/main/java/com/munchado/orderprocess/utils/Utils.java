package com.munchado.orderprocess.utils;

/**
 * Created by android on 23/2/17.
 */
public class Utils {
    public static double parseDouble(String delivery_charge) {
        double value = 0;
        try {
            value = Double.parseDouble(delivery_charge);
        } catch(Exception x){}
        return value;
    }
}
