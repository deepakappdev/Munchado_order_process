package com.munchado.orderprocess.utils;

import android.os.Build;
import android.text.Html;

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
    public static String decodeHtml(String htmlValue) {
        if (Build.VERSION.SDK_INT >= 24) {
            return Html.fromHtml(htmlValue, Html.FROM_HTML_MODE_LEGACY).toString();
        } else
            return Html.fromHtml(htmlValue).toString();

    }
}
