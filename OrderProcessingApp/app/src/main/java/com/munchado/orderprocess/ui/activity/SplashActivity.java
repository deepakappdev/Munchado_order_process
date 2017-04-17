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
import com.munchado.orderprocess.model.update.UpgradeData;
import com.munchado.orderprocess.network.RequestController;
import com.munchado.orderprocess.network.volley.NetworkError;
import com.munchado.orderprocess.network.volley.RequestCallback;
import com.munchado.orderprocess.utils.PrefUtil;
import com.munchado.orderprocess.utils.StringUtils;

public class SplashActivity extends AppCompatActivity {
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1500;
    UpgradeData upgradeData;
    private long enqueue;
    private DownloadManager dm;

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

//        registerReceiver(receiver, new IntentFilter(
//                DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void checkUpdate(String version) {
        RequestController.getUpdateApp(version, new RequestCallback() {
            @Override
            public void error(NetworkError volleyError) {

            }

            @Override
            public void success(Object obj) {
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
                            builder.setMessage("MA App Update Available.").setPositiveButton("Install", dialogClickListener).setCancelable(false).show();
                        } else if ((upgradeData.data.upgrade_type.equalsIgnoreCase("soft") && PrefUtil.getUpgradeDisplayCount() == 3)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
                            builder.setMessage("MA App Update Available.").setPositiveButton("Install", dialogClickListener)
                                    .setNegativeButton("Cancel", dialogClickListener).show();
                        }

                    } else {
                        PrefUtil.setUpgradeDisplayCount(0);
                        PrefUtil.setUpgradeType("");
                    }


                }
            }


        });
    }

    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked
                    if (upgradeData != null) {
//                        Intent i = new Intent(Intent.ACTION_VIEW);
//                        i.setData(Uri.parse(upgradeData.data.apk_link));
//                        startActivity(i);
                        dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                        DownloadManager.Request request = new DownloadManager.Request(
                                Uri.parse(upgradeData.data.apk_link));
                        enqueue = dm.enqueue(request);
                    }
                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
            dialog.dismiss();
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
    };

//    BroadcastReceiver receiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
//                long downloadId = intent.getLongExtra(
//                        DownloadManager.EXTRA_DOWNLOAD_ID, 0);
//                DownloadManager.Query query = new DownloadManager.Query();
//                query.setFilterById(enqueue);
//                Cursor c = dm.query(query);
//                if (c.moveToFirst()) {
//                    int columnIndex = c
//                            .getColumnIndex(DownloadManager.COLUMN_STATUS);
//                    if (DownloadManager.STATUS_SUCCESSFUL == c
//                            .getInt(columnIndex)) {
//
//                        Toast.makeText(SplashActivity.this, "Updated apk has been downloaded successfully.", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        }
//    };

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
