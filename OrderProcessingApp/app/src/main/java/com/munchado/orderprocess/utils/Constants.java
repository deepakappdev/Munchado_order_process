package com.munchado.orderprocess.utils;

/**
 * Created by android on 22/2/17.
 */

public class Constants {
    public static final String PREF_IS_LOGGED_IN = "pref_is_login";
    public static final String PREF_TOKEN = "pref_token";
    public static final String PREF_USERNAME = "pref_username";
    public static final String PREF_PASSWORD = "pref_password";
    public static final String PREF_IP_ADDRESS = "pref_ip";
    public static final String PREF_BLUETOOTH_MODEL = "pref_bluetooth_model";
    public static final String PREF_BLUETOOTH_MODEL_CODE = "pref_bluetooth_model_code";
    public static final String PREF_MANUAL_PRINT = "pref_manual_print";
    public static final String PREF_SCREEN_ON = "pref_screen_on";
    public static final String PREF_USER_ID = "pref_user_id";
    public static final String PREF_VERSION_CODE = "pref_version_code";
    public static final String PREF_UPGRADE_TYPE = "pref_upgrade_type";
    public static final String PREF_DISPLAY_COUNT = "pref_display_count";
    public static final String PREF_UPGRADE_CLEAR_DATA = "pref_upgrade_clear_data";
    public static final String PREF_UPGRADE_MESSAGE = "pref_upgrade_message";

    public static final String FRAGMENT_ARGS = "frag_args";
    public static final String FRAGMENT_PRINT_SETTING = "frag_PrintSettingFragment";
    public static final String FRAGMENT_ACTIVE_ORDER = "frag_ActiveOrderFragment";

    public static final String PARAM_PUBNUB_ACTION = "param_pubnub_action";
    public static final String PARAM_PUBNUB_SUBSCRIBE = "param_pubnub_subscribe";
    public static final String PARAM_PUBNUB_UNSUBSCRIBE = "param_pubnub_unsubscribe";

    public static final String PUBNUB_PUBLISHER_KEY = "pub-c-3372adca-2851-4be2-9402-5ecbf17f35a0";
    public static final String PUBNUB_SUBSCRIBER_KEY = "sub-c-9481452a-049c-11e7-afb0-0619f8945a4f";
    public static final String PUBNUB_SECRET_KEY = "sec-c-ZjExYmM4NDItNWUzYy00NWFiLTk2ZWEtMTI4NjRhMjVkN2Ri";
//    public static final String PUBNUB_PUBLISHER_KEY = "pub-c-914cfe24-f521-4ba7-bd44-1b75762222aa";
//    public static final String PUBNUB_SUBSCRIBER_KEY = "sub-c-c934ce06-e827-11e4-a30c-0619f8945a4f";
//    public static final String PUBNUB_SECRET_KEY = "sec-c-NmViODZiNzAtYmI4MC00NThiLTg5NjUtOTU3MjVmNzMzM2I1";

    public static String PUBNUB_CHANNEL = "dashboard_%s";

    //0=new,1=confirm,2=reject,3=alternate time,4=not respond,5=cancel,6=user confirm,7=archive
    public static final String NEW_ORDER = "0";
    public static final String CONFIRM = "1";
    public static final String REJECT = "2";
    public static final String ALTERNATE_TIME = "3";
    public static final String NOT_RESPOND = "4";
    public static final String CANCEL = "5";
    public static final String USER_CONFIRM = "6";
    public static final String ARCHIVE = "7";

}
