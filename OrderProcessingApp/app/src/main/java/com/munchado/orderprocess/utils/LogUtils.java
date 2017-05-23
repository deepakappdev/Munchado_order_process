package com.munchado.orderprocess.utils;

import android.util.Log;

import com.munchado.orderprocess.BuildConfig;

/**
 * Created by android on 22/2/17.
 */
public class LogUtils {

    public static void d(String s) {
        if (BuildConfig.DEBUG)
            Log.d("", s);
    }

    public static void e(String s) {
//        if (BuildConfig.DEBUG)
            Log.e("", s);
    }

    public static void e(String tag, String message) {
        if (BuildConfig.DEBUG && !StringUtils.isNullOrEmpty(tag) && !StringUtils.isNullOrEmpty(message))
            Log.e(tag, message);
    }

    public static void d(String tag, String message) {
        if (BuildConfig.DEBUG && !StringUtils.isNullOrEmpty(tag) && !StringUtils.isNullOrEmpty(message))
            Log.d(tag, message);
    }

}
