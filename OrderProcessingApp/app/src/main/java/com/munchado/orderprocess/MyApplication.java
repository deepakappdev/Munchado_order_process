package com.munchado.orderprocess;

import android.app.Application;
import android.content.Context;

/**
 * Created by android on 22/2/17.
 */

public class MyApplication extends Application {
    public static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
    }
}
