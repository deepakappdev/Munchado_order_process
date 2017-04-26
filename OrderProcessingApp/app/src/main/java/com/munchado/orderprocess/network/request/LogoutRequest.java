package com.munchado.orderprocess.network.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.munchado.orderprocess.model.login.StatusResponse;
import com.munchado.orderprocess.network.volley.GsonRequest;
import com.munchado.orderprocess.network.volley.NetworkConstants;
import com.munchado.orderprocess.utils.PrefUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by user on 6/23/2015.
 */
public class LogoutRequest extends BaseRequest {

    private final String password;
    private final String username;

    public LogoutRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getServiceUrl() {
        return NetworkConstants.LOGOUT_URL;
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
            object.put("token", PrefUtil.getToken());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object;
    }

    public GsonRequest createServerRequest(Response.ErrorListener errorListener, Response.Listener listener) {
        GsonRequest<StatusResponse> gsonRequest = new GsonRequest<>(
                Request.Method.GET, getServiceUrl(),
                StatusResponse.class, null, listener, errorListener, getJsonRequest());
        gsonRequest.setShouldCache(false);
        gsonRequest.setHeader(getHeaders());
        return gsonRequest;
    }
}
