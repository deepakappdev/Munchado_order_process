package com.munchado.orderprocess.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * Created by manish.verma on 02-09-2016.
 */
public class MunchadoUtils {


    public static final String DEFAULT_CITY = "New York";
    public static final String DEFAULT_STATE = "NY";
    public static final String DEFAULT_COUNTRY = "US";
    public static final String DEFAULT_CITY_ID = "18848";



    /**
     * method for check the internet connection
     *
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);

            if (connectivity != null) {
                NetworkInfo[] info = connectivity.getAllNetworkInfo();

                if (info != null) {
                    for (int i = 0; i < info.length; i++) {
                        LogUtils.d("===== in loop "+info[i].getState().toString());
                        if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                           LogUtils.d("===== connected");
                            return true;
                        }
                    }
                }
            }
            LogUtils.d("===== not connected");
            return false;
        } catch (Exception e) {
            return false;
        }
    }


}
