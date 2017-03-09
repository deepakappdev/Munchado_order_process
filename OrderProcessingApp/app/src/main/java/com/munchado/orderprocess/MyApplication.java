package com.munchado.orderprocess;

import android.app.Application;
import android.content.Context;

import com.epson.easyselect.EasySelectDeviceType;
import com.munchado.orderprocess.utils.Foreground;

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

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        Foreground.init(this);
    }
}
