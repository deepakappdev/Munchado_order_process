package com.munchado.orderprocess.network.volley;


/**
 * Created by Gautam on 22/2/2017.
 */
public class NetworkConstants {
    public static final String HTTP_BASE_URL = "https://api.munchado.com/";
    //    public static final String HTTP_BASE_URL = "https://demoapi.munchado.com/";
    public static final String TOKEN_URL = HTTP_BASE_URL + "api/dashboard/token?mob=true";
    public static final String LOGIN_URL = HTTP_BASE_URL + "api/dashboard/login?mob=true";
    public static final String LOGOUT_URL = HTTP_BASE_URL + "api/dashboard/logout?mob=true";
    public static final String GET_ORDER_URL = HTTP_BASE_URL + "api/dashboard/order?mob=true";
    public static final String GET_ORDER_DETAIL_URL = HTTP_BASE_URL + "api/dashboard/order/";
    public static final String GET_RESTAURANT_PROFILE_URL = HTTP_BASE_URL + "api/dashboard/restaurant?mob=true";
    public static final String GET_UPDATE_APP_URL = HTTP_BASE_URL + "api/dashboard/updateapp?mob=true";
}
