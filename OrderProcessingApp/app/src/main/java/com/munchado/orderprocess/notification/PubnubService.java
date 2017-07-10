package com.munchado.orderprocess.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.ui.activity.HomeActivity;
import com.munchado.orderprocess.utils.Constants;
import com.munchado.orderprocess.utils.Foreground;
import com.munchado.orderprocess.utils.LogUtils;
import com.munchado.orderprocess.utils.PrefUtil;
import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubException;

import java.util.Random;

public class PubnubService extends Service {

    private Pubnub mPubnub;

    public PubnubService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null && intent.hasExtra(Constants.PARAM_PUBNUB_ACTION)) {
            if (intent.getStringExtra(Constants.PARAM_PUBNUB_ACTION).equalsIgnoreCase(Constants.PARAM_PUBNUB_SUBSCRIBE)) {
                initPub();
            } else if (intent.getStringExtra(Constants.PARAM_PUBNUB_ACTION).equalsIgnoreCase(Constants.PARAM_PUBNUB_UNSUBSCRIBE)) {
                mPubnub.unsubscribeAll();
            }
        }

        return super.onStartCommand(intent, flags, startId);


    }

    private void initPub() {
        this.mPubnub = new Pubnub(Constants.PUBNUB_PUBLISHER_KEY, Constants.PUBNUB_SUBSCRIBER_KEY);
        this.mPubnub.setUUID(PrefUtil.getString(Constants.PREF_USER_ID, ""));
        Callback subscribeCallback = new Callback() {
            @Override
            public void successCallback(String channel, final Object message) {
                Foreground.get().setSubscribed(true);
                LogUtils.d("PUBNUB", "=================Channel: " + channel + " Msg: " + message.toString());
//                LogUtils.d("PUBNUB", "=================Channel: Foreground.get().isBackground() : " + Foreground.get().isBackground());
//                LogUtils.d("PUBNUB", "=================Channel: Foreground.get().isForeground() : " + Foreground.get().isForeground());
//                final NotificationModel mNotificationModel = new Gson().fromJson(message.toString(), NotificationModel.class);
//
//                if (!Foreground.get().isForeground() && mNotificationModel != null && !StringUtils.isNullOrEmpty(mNotificationModel.msg)) {
//                    redirectToTargetScreen(mNotificationModel);
//                }
//                sendMessage(message.toString());
//                redirectToTargetScreen(message.toString());
            }

            @Override
            public void connectCallback(String channel, Object message) {
                Log.d("Subscribe", "============Connected! " + message.toString() + "==== Channel : " + channel);

            }
        };
        try {
            mPubnub.subscribe(String.format(Constants.PUBNUB_CHANNEL, PrefUtil.getString(Constants.PREF_USER_ID, "")), subscribeCallback);

        } catch (PubnubException e) {
            e.printStackTrace();
        }
    }

    // Send an Intent with an action named "my-event".
    private void sendMessage(String message) {
        Intent intent = new Intent("com.munchado.orderprocess.notification.refresh");
        // add data
        intent.putExtra("message", message);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void redirectToTargetScreen(String message) {

        Intent intent = new Intent(this, HomeActivity.class);
        Bundle bundle = new Bundle();
//        bundle.putParcelable(Constants.PARAM_NOTIFICATION, mNotificationModel);
        intent.putExtras(bundle);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .setSmallIcon(R.mipmap.app_small_icon)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(new Random().nextInt(10000) /* ID of notification */, notificationBuilder.build());
    }


}
