package com.munchado.orderprocess.network.volley;


/**
 * Created by piyush on 22/2/2017.
 */
public class NetworkConstants {
    public static final String HTTP_BASE_URL = "http://api.munchado.in/";// QC
//    public static final String HTTP_BASE_URL = "https://api.munchado.com/"; // LIVE
//    public static final String HTTP_BASE_URL = "https://demoapi.munchado.com/";// DEMO
//    public static final String HTTP_BASE_URL = "http://192.168.29.21/";// SUDHANSHU
    public static final String TOKEN_URL = HTTP_BASE_URL + "api/dashboard/token?mob=true";
    public static final String LOGIN_URL = HTTP_BASE_URL + "api/dashboard/login?mob=true";
    public static final String LOGOUT_URL = HTTP_BASE_URL + "api/dashboard/logout?mob=true";
    public static final String GET_ORDER_URL = HTTP_BASE_URL + "api/dashboard/order?mob=true";
    public static final String GET_ORDER_DETAIL_URL = HTTP_BASE_URL + "api/dashboard/order/";
    public static final String GET_RESTAURANT_PROFILE_URL = HTTP_BASE_URL + "api/dashboard/restaurant?mob=true";
    public static final String GET_UPDATE_APP_URL = HTTP_BASE_URL + "api/dashboard/updateapp?mob=true";
    public static final String GET_DINEIN_URL = HTTP_BASE_URL + "api/dashboard/restaurant/holdatable?mob=true&token=";
    public static final String GET_DINEIN_DETAIL_URL = HTTP_BASE_URL + "api/dashboard/restaurant/holdatable/%s?mob=true&token=";
    public static final String GET_ORDER_LIST_DATE_URL = HTTP_BASE_URL + "api/dashboard/order?mob=true&token=";
    public static final String GET_RESERVATION_LIST_URL = HTTP_BASE_URL + "api/dashboard/reservations/today?mob=true&token=";

}
