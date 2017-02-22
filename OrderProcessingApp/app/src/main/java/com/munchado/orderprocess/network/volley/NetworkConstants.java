package com.munchado.orderprocess.network.volley;


/**
 * Created by Gautam on 4/2/2015.
 */
public class NetworkConstants {
    public static final String HTTP_BASE_URL = "http://demoapi.munchado.com/";
    public static final String TOKEN_URL = HTTP_BASE_URL + "api/dashboard/token?mob=true";
    public static final String LOGIN_URL = HTTP_BASE_URL + "api/dashboard/login?mob=true";
    public static final String LOGOUT_URL = HTTP_BASE_URL + "api/dashboard/logout?mob=true";

    public static final String GET_ARCHIVE_ORDER_URL = HTTP_BASE_URL + "archive";

}
