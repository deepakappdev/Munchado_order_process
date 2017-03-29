package com.munchado.orderprocess.ui.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.model.update.UpgradeData;
import com.munchado.orderprocess.network.RequestController;
import com.munchado.orderprocess.network.volley.NetworkError;
import com.munchado.orderprocess.network.volley.RequestCallback;
import com.munchado.orderprocess.utils.PrefUtil;
import com.munchado.orderprocess.utils.StringUtils;

public class SplashActivity extends AppCompatActivity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        int versionCode = 1;
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int savedVersionCode = PrefUtil.getVersionCode();
        if (versionCode >= savedVersionCode)
            doWorkAfterUpdate();

        checkUpdate(versionCode + "");


    }

    private void checkUpdate(String version) {
        RequestController.getUpdateApp(version, new RequestCallback() {
            @Override
            public void error(NetworkError volleyError) {

            }

            @Override
            public void success(Object obj) {
                UpgradeData upgradeData = (UpgradeData) obj;
                if (upgradeData != null && upgradeData.data != null && !StringUtils.isNullOrEmpty(upgradeData.data.upgrade_type)) {
                    if (upgradeData.data.upgrade_type.equalsIgnoreCase("hard") || upgradeData.data.upgrade_type.equalsIgnoreCase("soft")) {
                        if (PrefUtil.getUpgradeType().isEmpty()) {
                            PrefUtil.setUpgradeDisplayCount(upgradeData.data.counter);
                        }
                        PrefUtil.setUpgradeClearData(upgradeData.data.clear_data);
                        PrefUtil.setUpgradeType(upgradeData.data.upgrade_type);
                        PrefUtil.setUpgradeMessage(upgradeData.data.message);

                        if (upgradeData.data.upgrade_type.equalsIgnoreCase("hard") || (upgradeData.data.upgrade_type.equalsIgnoreCase("soft") && PrefUtil.getUpgradeDisplayCount() == 3)) {

//                            Intent i = new Intent(Intent.ACTION_VIEW);
//                            i.setData(Uri.parse(upgradeData.data.apk_link));
//                            startActivity(i);
                        }

                    } else {
                        PrefUtil.setUpgradeDisplayCount(0);
                        PrefUtil.setUpgradeType("");
                    }

                    new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                        @Override
                        public void run() {
                            // This method will be executed once the timer is over
                            // Start your app main activity
                            if (PrefUtil.isLogin()) {
                                Intent i = new Intent(SplashActivity.this, HomeActivity.class);
                                startActivity(i);
                            } else {
                                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                                startActivity(i);
                            }


                            // close this activity
                            finish();
                        }
                    }, SPLASH_TIME_OUT);
                }
            }


        });
    }

    private void doWorkAfterUpdate() {
        if (PrefUtil.getUpgradeCleanData()) {
//            PrefUtil.clearAllData();
        }
        PrefUtil.setUpgradeDisplayCount(0);
        PrefUtil.setUpgradeType("");
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            PrefUtil.setVersionCode(pInfo.versionCode);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
