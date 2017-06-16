package com.munchado.orderprocess.ui.activity;

import android.app.DownloadManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.model.token.TokenResponse;
import com.munchado.orderprocess.model.update.UpgradeData;
import com.munchado.orderprocess.network.RequestController;
import com.munchado.orderprocess.network.volley.NetworkError;
import com.munchado.orderprocess.network.volley.RequestCallback;
import com.munchado.orderprocess.ui.fragment.CustomErrorDialogFragment;
import com.munchado.orderprocess.utils.LogUtils;
import com.munchado.orderprocess.utils.MunchadoUtils;
import com.munchado.orderprocess.utils.PrefUtil;
import com.munchado.orderprocess.utils.StringUtils;
import com.munchado.orderprocess.utils.Utils;

public class SplashActivity extends AppCompatActivity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1500;
    UpgradeData upgradeData;
    private DownloadManager dm;
    int versionCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        int savedVersionCode = PrefUtil.getVersionCode();
        if (versionCode >= savedVersionCode)
            doWorkAfterUpdate();
//        if (!StringUtils.isNullOrEmpty(PrefUtil.getToken()))
//            checkUpdate(versionCode + "");
//        else
        if (MunchadoUtils.isNetworkAvailable(this)) {
            getNewToken();
        } else {
            CustomErrorDialogFragment errorDialogFragment = CustomErrorDialogFragment.newInstance(getResources().getString(R.string.network_error));
            errorDialogFragment.show(getSupportFragmentManager(), "Error");
        }

    }

    private void getNewToken() {
        RequestController.createNewAccessToken(new RequestCallback() {
            @Override
            public void error(NetworkError networkError) {
                LogUtils.d("============= token error");
                Utils.showLogin(SplashActivity.this);
//                if (networkError != null && !StringUtils.isNullOrEmpty(networkError.getLocalizedMessage())) {
//                    CustomErrorDialogFragment errorDialogFragment = CustomErrorDialogFragment.newInstance(networkError.getLocalizedMessage());
//                    errorDialogFragment.show(getSupportFragmentManager(), "Error");
//                }
//                getNewToken();
            }

            @Override
            public void success(Object obj) {
                LogUtils.d("============= token success");
                TokenResponse mTokenResponse = (TokenResponse) obj;
                PrefUtil.putToken(mTokenResponse.data.token);
                checkUpdate(versionCode + "");
            }
        });
    }

    private void checkUpdate(String version) {
        RequestController.getUpdateApp(version, new RequestCallback() {
            @Override
            public void error(NetworkError volleyError) {

            }

            @Override
            public void success(Object obj) {
                LogUtils.d("============= checkUpdate success");
                upgradeData = (UpgradeData) obj;
                if (upgradeData != null && upgradeData.data != null && !StringUtils.isNullOrEmpty(upgradeData.data.upgrade_type)) {
                    if (upgradeData.data.upgrade_type.equalsIgnoreCase("hard") || upgradeData.data.upgrade_type.equalsIgnoreCase("soft")) {
                        if (PrefUtil.getUpgradeType().isEmpty()) {
                            PrefUtil.setUpgradeDisplayCount(upgradeData.data.counter);
                        }
                        PrefUtil.setUpgradeClearData(upgradeData.data.clear_data);
                        PrefUtil.setUpgradeType(upgradeData.data.upgrade_type);
                        PrefUtil.setUpgradeMessage(upgradeData.data.message);
                        if (upgradeData.data.upgrade_type.equalsIgnoreCase("hard")) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                            builder.setMessage(getResources().getString(R.string.app_name) + " Update Available.").setPositiveButton("Update", dialogClickListener).setCancelable(false).show();
                        } else if ((upgradeData.data.upgrade_type.equalsIgnoreCase("soft") && PrefUtil.getUpgradeDisplayCount() == 3)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                            builder.setMessage(getResources().getString(R.string.app_name) + " Update Available.").setPositiveButton("Update", dialogClickListener)
                                    .setNegativeButton("Cancel", dialogClickListener).setCancelable(false).show();
                        } else
                            gotoHome();
                    } else {
                        PrefUtil.setUpgradeDisplayCount(0);
                        PrefUtil.setUpgradeType("");
                        gotoHome();
                    }
                }
            }
        });
    }

    private void gotoHome() {
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
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    if (upgradeData != null) {
                        dialog.dismiss();
                        gotoHome();
//                        if (PrefUtil.isLogin()) {
//                            Intent i = new Intent(SplashActivity.this, HomeActivity.class);
//                            startActivity(i);
//                        } else {
//                            Intent i = new Intent(SplashActivity.this, LoginActivity.class);
//                            startActivity(i);
//                        }
//                        Intent webIntent = new Intent(Intent.ACTION_VIEW,
//                                Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
//                        startActivity(webIntent);
//                        finish();
                        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                        DownloadManager.Request request = new DownloadManager.Request(
                                Uri.parse(upgradeData.data.apk_link));
                        long enqueue = dm.enqueue(request);
                        if (upgradeData.data.upgrade_type.equalsIgnoreCase("hard")) {
                            PrefUtil.setUpgradeDisplayCount(0);
                            PrefUtil.setUpgradeType("");
                        }
                    }
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    dialog.dismiss();
                    gotoHome();
                    //No button clicked
                    break;
            }


        }
    };


    private void doWorkAfterUpdate() {
        if (PrefUtil.getUpgradeCleanData()) {
            PrefUtil.clearAllData();
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
