package com.munchado.orderprocess.network.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by Gautam on 4/1/2015.
 */
public class NetworkError extends Exception {
    public static final int NO_NETWORK_ERROR = 0;
    private final String ERROR_MESSAGE = "Not able to connect to VConnect. Please check your network connection and try again.";
    public int code = -1;
    private String message;
    private String localizedMessage = "Server Error";

    public NetworkError(String s) {
        super(s);
        localizedMessage = s;
    }

    public NetworkError(VolleyError volleyError) {
        if (volleyError instanceof TimeoutError) {
            localizedMessage = ERROR_MESSAGE;
            return;
        }
        if (volleyError instanceof ServerError) {
            localizedMessage = ERROR_MESSAGE;
            return;
        }
        if (volleyError instanceof ParseError) {
            localizedMessage = "Something went wrong. Please check internet connection.";
            //code = NetworkError.NO_NETWORK_ERROR;
            return;
        }
        if (volleyError instanceof NoConnectionError) {
            localizedMessage = ERROR_MESSAGE;
            code = NetworkError.NO_NETWORK_ERROR;
            return;
        }
        if (volleyError instanceof com.android.volley.NetworkError) {
            localizedMessage = ERROR_MESSAGE;
            return;
        }
        if (volleyError instanceof AuthFailureError) {
            localizedMessage = "Auth Failure Error";
            return;
        }
        localizedMessage = ERROR_MESSAGE;

    }


    public NetworkError(JSONObject jo) {
        if (jo == null) return;
        if (jo.has("ResponseCode")) code = jo.optInt("ResponseCode", -1);
        if (code != -1) {
            localizedMessage = getDescriptionFromCode(code, jo.optString("message"));
        }
        //if (jo.has("reason")) reason = jo.optString("reason", null);
        //if (jo.has("message")) message = jo.optString("message", null);
        if (jo.has("ResponseMessage")) localizedMessage = jo.optString("error_description");
    }

    @Override
    public String getLocalizedMessage() {
        return "" + localizedMessage;
    }

    private String getDescriptionFromCode(int code, String defaultMessage) {
        switch (code) {
            case 401:
                return "Account not active.";
            case 405:
                return "Facebook account already linked to another account. Try logging out of this account," +
                        " then log in with Facebook, then unlink that account from the settings page.";
            case 406:
                return "Twitter account already linked to another account.";
            case 409:
                return "You've already reported this earlier";
            case 410:
                return "You've already liked this earlier.";
            case 411:
                return "Invalid User information entered";
            case 412:
                return "Email already in use";
            case 413:
                return "Username already in use";
            default:
                return defaultMessage == null ? "Server error " + code : defaultMessage;
        }
    }

    @Override
    public String getMessage() {
        return message == null ? super.getMessage() : message;
    }
}