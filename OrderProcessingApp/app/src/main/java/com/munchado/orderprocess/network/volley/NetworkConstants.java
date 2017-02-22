package com.munchado.orderprocess.network.volley;


/**
 * Created by Gautam on 4/2/2015.
 */
public class NetworkConstants {

    public static final int STATUS_CODE_OK = 200;
//    public static final String HTTP_BASE_URL = "http://frycartandroidapp.cfapps.io";
    public static final String HTTP_BASE_URL = "http://cloudinsider.in/FryCartApp";
    public static final String PHONE_VERIFICATION_URL = HTTP_BASE_URL+"/sms";
    public static final String REPORT_URL = HTTP_BASE_URL + "/Report";
    public static final String LAST_ORDER_URL = HTTP_BASE_URL + "/Report";
    public static final String DEALS_UL = HTTP_BASE_URL + "/deals?type=DEAL";
    public static final String UPDATE_PRICE_URL = HTTP_BASE_URL + "/UpdatePrice";
    public static final String BANNERS_URL = HTTP_BASE_URL + "/deals?type=category&keyword=all";
    public static final String USER_VALIDATE_URL = HTTP_BASE_URL + "/ValidateUser";


    //public static String httpBase = "http://app.vconnect.com";
    public static String httpBase = "http://103.18.72.50/mappservice_stageing";
    public static String baseUserGetURL = httpBase + "/VConnect.svc/Rest/";
    public static String loginUserURL = baseUserGetURL + "LoginUser";
    public static String httpBaseNoV = "";
    public static String baseAccessTokenPostURL;
    public static String baseGetDefaultURL;
    public static String baseChangePWURL;
    public static String sourceString = "MobileApp_Android_Store";

    private NetworkConstants() {

    }

    public static void changeUrls() {
        baseUserGetURL = httpBase + "/VConnect.svc/Rest/";
        loginUserURL = baseUserGetURL + "LoginUser";
    }
}
