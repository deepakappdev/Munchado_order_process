package com.munchado.orderprocess.notification;

import android.app.IntentService;
import android.content.Intent;

//import com.pubnub.api.PNConfiguration;
//import com.pubnub.api.PubNub;
//import com.pubnub.api.callbacks.PNCallback;
//import com.pubnub.api.enums.PNPushType;
//import com.pubnub.api.models.consumer.PNStatus;
//import com.pubnub.api.models.consumer.push.PNPushAddChannelResult;

/**
 * Created by munchado on 12/12/16.
 */
public class SendTokenToPubnubIntentService extends IntentService {

    private static final String TAG = "SendTokenToPubnubIntentService";
//    private String token = null;
//    private Pubnub mPubnub;

    public SendTokenToPubnubIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//        MyLogs.d("================onHandleIntent SendTokenToPubnubIntentService");
//        token = intent.getStringExtra(Constants.PARAM_TOKEN);

//        punnub configuration
//        PNConfiguration pnConfiguration = new PNConfiguration();
//        pnConfiguration.setSubscribeKey(Constants.PUBNUB_SUBSCRIBER_KEY);
//        pnConfiguration.setPublishKey(Constants.PUBNUB_PUBLISHER_KEY);
//        pnConfiguration.setSecure(true);
//        mPubnub = new PubNub(pnConfiguration);
//
//        this.mPubnub = new Pubnub(Constants.PUBNUB_PUBLISHER_KEY, Constants.PUBNUB_SUBSCRIBER_KEY);
//        this.mPubnub.setUUID(PrefUtil.getString(this, Constants.PREF_USER_ID, ""));
//
//
//        MyLogs.d("================onHandleIntent token" + token);
//        subscribeWithChannel();
//        enablePushOnChannel(token,String.format(Constants.PUBNUB_CHANNEL, PrefUtil.getString(this,Constants.PREF_USER_ID,"")));
    }

//    private void enablePushOnChannel(String regId,String channelName) {
//        //adding regId to pubnub channel
//        mPubnub.addPushNotificationsOnChannels()
//                .pushType(PNPushType.GCM)
//                .channels(Arrays.asList(channelName))
//                .deviceId(regId)
//                .async(new PNCallback<PNPushAddChannelResult>() {
//                    @Override
//                    public void onResponse(PNPushAddChannelResult result, PNStatus status) {
//                        if (status.isError()) {
//                            MyLogs.d("================Error on push notification" + status.getErrorData());
//                        } else {
//                            MyLogs.d("===============Push notification added ");
//                        }
//                        stopSelf();
//                    }
//                });
//
//
//
//    }

//    public void subscribeWithChannel() {
//        Callback subscribeCallback = new Callback() {
//            @Override
//            public void successCallback(String channel, Object message) {
//
//                Log.d("PUBNUB", "=================Channel: " + channel + " Msg: " + message.toString());
//            }
//
//            @Override
//            public void connectCallback(String channel, Object message) {
//                Log.d("Subscribe", "============Connected! " + message.toString());
//
//            }
//        };
//        try {
//            mPubnub.subscribe(String.format(Constants.PUBNUB_CHANNEL, PrefUtil.getString(this, Constants.PREF_USER_ID, "")), subscribeCallback);
//
//        } catch (PubnubException e) {
//            e.printStackTrace();
//        }
//    }
}
