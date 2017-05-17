package com.munchado.orderprocess;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.epson.easyselect.EasySelectDeviceType;
import com.munchado.orderprocess.utils.Foreground;

import io.fabric.sdk.android.Fabric;

/**
 * Created by android on 22/2/17.
 */

public class MyApplication extends Application {
    public static Context mContext;

    public static String mTarget = null;
    public static String mInterfaceType = null;
    public static String mAddress = null;
    public static String printerName = null;
    public static String printData = null;
    public static int mDeviceType = EasySelectDeviceType.TCP;

    public static boolean isDialogShown=false;
    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        Fabric.with(this, new Crashlytics());
        Foreground.init(this);
    }
}
