package com.munchado.orderprocess.network.volley;

import com.android.volley.VolleyError;
import com.google.gson.Gson;
import com.munchado.orderprocess.model.BaseResponse;

import org.json.JSONObject;

/**
 * Created by Gautam on 4/1/2015.
 */
public class NetworkError extends Exception {
    public static final int NO_NETWORK_ERROR = 0;
    private final String ERROR_MESSAGE = "Not able to connect to Munchado. Please check your network connection and try again.";
    public int code = -1;
    private String message;
    private String localizedMessage = "Server Error";

    public NetworkError(String s) {
        super(s);
        localizedMessage = s;
    }

    public NetworkError(VolleyError volleyError) {
        if(volleyError!=null && volleyError.networkResponse!=null && volleyError.networkResponse.data!=null){
            String response =new String(volleyError.networkResponse.data);
            BaseResponse model= new Gson().fromJson(response,BaseResponse.class);
            localizedMessage = model.message;
        }
        else{
            BaseResponse model= new BaseResponse();
            model.message="Something went wrong";
            model.result=false;
            localizedMessage = model.message;
        }
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