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

    public static void putPrinterType(String token) {
        PrefUtil.putString(Constants.PREF_PRINTER_TYPE, token);
    }

    public static String getPrinterType() {
        return getString(Constants.PREF_PRINTER_TYPE, Constants.BLUETOOTH);
    }

    public static void putIPAddress(String token) {
        PrefUtil.putString(Constants.PREF_IP_ADDRESS, token);
    }

    public static String getBluetoothModel() {
        return getString(Constants.PREF_BLUETOOTH_MODEL, "");
    }

    public static void putBluetoothModel(String token) {
        PrefUtil.putString(Constants.PREF_BLUETOOTH_MODEL, token);
    }

    public static int getBluetoothModelCode() {
        return getInt(Constants.PREF_BLUETOOTH_MODEL_CODE, -1);
    }

    public static void putBluetoothModelCode(int model) {
        PrefUtil.putInt(Constants.PREF_BLUETOOTH_MODEL_CODE, model);
    }

    public static boolean isManualPrint() {
        return getBoolean(Constants.PREF_MANUAL_PRINT, false);
    }

    public static void setManualPrint(boolean login) {
        putBoolean(Constants.PREF_MANUAL_PRINT, login);
    }

    public static boolean isScreenOn() {
        return getBoolean(Constants.PREF_SCREEN_ON, true);
    }

    public static void setScreenON(boolean login) {
        putBoolean(Constants.PREF_SCREEN_ON, login);
    }


    public static int getVersionCode() {
        return getInt(Constants.PREF_VERSION_CODE, 0);
    }

    public static void setVersionCode(int versionCode) {
        putInt(Constants.PREF_VERSION_CODE, versionCode);


    }

    public static void setUpgradeClearData(boolean clearData) {
        putBoolean(Constants.PREF_UPGRADE_CLEAR_DATA, clearData);
    }

    public static boolean getUpgradeCleanData() {
        return getBoolean(Constants.PREF_UPGRADE_CLEAR_DATA, false);
    }

    public static void setUpgradeDisplayCount(int count) {
        putInt(Constants.PREF_DISPLAY_COUNT, count);
    }

    public static int getUpgradeDisplayCount() {
        return getInt(Constants.PREF_DISPLAY_COUNT, 0);
    }

    public static void setUpgradeType(String upgradeType) {
        putString(Constants.PREF_UPGRADE_TYPE, upgradeType);
    }

    public static String getUpgradeType() {
        return getString(Constants.PREF_UPGRADE_TYPE, "");
    }

    public static void setUpgradeMessage(String message) {
        putString(Constants.PREF_UPGRADE_MESSAGE, message);
    }

    public static String getUpgradeMessage() {
        return getString(Constants.PREF_UPGRADE_MESSAGE, "");
    }

    public static void putUsername(String username) {
        putString(Constants.PREF_USERNAME, username);
    }

    public static void putPassword(String pwd) {
        putString(Constants.PREF_PASSWORD, pwd);
    }

    public static String getUsername() {
        return getString(Constants.PREF_USERNAME, "");
    }

    public static String getPassword() {
        return getString(Constants.PREF_PASSWORD, "");
    }
}