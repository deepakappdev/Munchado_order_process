package com.munchado.orderprocess.utils;

import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.text.Html;

import com.munchado.orderprocess.MyApplication;
import com.munchado.orderprocess.ui.fragment.CustomErrorDialogFragment;

/**
 * Created by android on 23/2/17.
 */
public class Utils {
    public static double parseDouble(String delivery_charge) {
        double value = 0;
        try {
            value = Double.parseDouble(delivery_charge);
        } catch (Exception x) {
        }
        return value;
    }

    public static String decodeHtml(String htmlValue) {
        if (Build.VERSION.SDK_INT >= 24) {
            return Html.fromHtml(htmlValue, Html.FROM_HTML_MODE_LEGACY).toString();
        } else
            return Html.fromHtml(htmlValue).toString();

    }

    public static void showLogin(FragmentActivity activity) {
//        Intent i = new Intent(activity, LoginActivity.class);
//        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//        PrefUtil.clearAllData();
//        activity.startActivity(i);
//        activity.finish();
        if (!MyApplication.isDialogShown) {
            CustomErrorDialogFragment errorDialogFragment = CustomErrorDialogFragment.newInstance(activity, "Session Timeout. Please login.", true);
            errorDialogFragment.show(activity.getSupportFragmentManager(), "Error");
            MyApplication.isDialogShown = true;
        }

    }
}
