package com.munchado.orderprocess.network.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.munchado.orderprocess.model.login.LoginResponse;
import com.munchado.orderprocess.network.volley.GsonRequest;
import com.munchado.orderprocess.network.volley.NetworkConstants;
import com.munchado.orderprocess.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by user on 6/23/2015.
 */
public class LoginRequest extends BaseRequest {

    private final String password;
    private final String username;

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getServiceUrl() {
        return NetworkConstants.LOGIN_URL;
    }

    public HashMap<String, String> getParameters() {
        HashMap parameter = new HashMap();
        return parameter;
    }

    public HashMap<String, String> getHeaders() {
        HashMap<String, String> header = new HashMap<String, String>();
        return header;
    }

    public JSONObject getJsonRequest() {
        JSONObject object = new JSONObject();
        try {
            object.put("dashboard_username", username);
            object.put("dashboard_password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LogUtils.e("==== json parameter : "+object.toString());
        return object;
    }

    public GsonRequest createServerRequest(Response.ErrorListener errorListener, Response.Listener listener) {
        GsonRequest<LoginResponse> itemListRequest = new GsonRequest<>(
                Request.Method.POST, getServiceUrl(),
                LoginResponse.class, null, listener, errorListener, getJsonRequest());
        itemListRequest.setShouldCache(false);
        itemListRequest.setHeader(getHeaders());
        return itemListRequest;
    }
}
