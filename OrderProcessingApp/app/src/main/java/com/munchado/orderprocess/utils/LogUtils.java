package com.munchado.orderprocess.utils;

import android.util.Log;

import com.munchado.orderprocess.BuildConfig;

/**
 * Created by android on 22/2/17.
 */
public class LogUtils {
    public static void d(String tag, String s) {
        if (BuildConfig.DEBUG)
            Log.d(tag, s);
    }

    public static void d(String s) {
        if (BuildConfig.DEBUG)
            Log.d("", s);
    }
    public static void e(String s) {
        if (BuildConfig.DEBUG)
            Log.e("", s);
    }
    public static void e(String tag, String s) {
        if (BuildConfig.DEBUG)
            Log.e(tag, s);
    }
}
