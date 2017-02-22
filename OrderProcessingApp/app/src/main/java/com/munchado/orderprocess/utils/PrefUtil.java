package com.munchado.orderprocess.utils;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.munchado.orderprocess.MyApplication;


public final class PrefUtil {
    private PrefUtil() {
    }

    public static int getInt(final String key, int defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.mContext);
        return prefs.getInt(key, defaultValue);
    }

    public static void putInt(String key, int value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.mContext);
        prefs.edit().putInt(key, value).apply();
    }


    public static String getString(final String key, final String defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.mContext);
        return prefs.getString(key, defaultValue);
    }

    public static void putString(String key, String value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.mContext);
        prefs.edit().putString(key, value).apply();
    }


    public static double getDouble(final String key, double defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.mContext);
        String value = prefs.getString(key, defaultValue + "");
        try {
            return Double.parseDouble(value);
        } catch (Exception x) {
            return defaultValue;
        }
    }

    public static void putDouble(String key, double value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.mContext);
        prefs.edit().putString(key, value + "").apply();
    }

    private static boolean getBoolean(String key, final boolean defaultValue) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.mContext);
        return prefs.getBoolean(key, defaultValue);
    }

    private static void putBoolean(String key, boolean value) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MyApplication.mContext);
        prefs.edit().putBoolean(key, value).apply();
    }

    public static void clearAllData() {
        PreferenceManager.getDefaultSharedPreferences(MyApplication.mContext).edit().clear().apply();
    }


    public static boolean isLogin() {
        return getBoolean(Constants.PREF_IS_LOGGED_IN, false);
    }

    public static void setLogin(boolean login) {
        putBoolean(Constants.PREF_IS_LOGGED_IN, login);
    }

    public static String getToken() {
        return getString(Constants.PREF_TOKEN, "");
    }

    public static void putToken(String token) {
        PrefUtil.putString(Constants.PREF_TOKEN, token);
    }


    public static String getIPAddress() {
        return getString(Constants.PREF_IP_ADDRESS, "");
    }

    public static void putIPAddress(String token) {
        PrefUtil.putString(Constants.PREF_IP_ADDRESS, token);
    }

    public static boolean isManualPrint() {
        return getBoolean(Constants.PREF_MANUAL_PRINT, false);
    }

    public static void setManualPrint(boolean login) {
        putBoolean(Constants.PREF_MANUAL_PRINT, login);
    }
}