package com.munchado.orderprocess.network.request;

import com.android.volley.Request;
import com.android.volley.Response;
import com.munchado.orderprocess.model.token.TokenResponse;
import com.munchado.orderprocess.network.volley.GsonRequest;
import com.munchado.orderprocess.network.volley.NetworkConstants;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by user on 6/23/2015.
 */
public class NewTokenRequest extends BaseRequest{

    public String getServiceUrl() {
        return NetworkConstants.TOKEN_URL;
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
        return object;
    }

    public GsonRequest createServerRequest(Response.ErrorListener errorListener, Response.Listener listener) {
        GsonRequest<TokenResponse> itemListRequest = new GsonRequest<>(
                Request.Method.POST, getServiceUrl(),
                TokenResponse.class, null, listener, errorListener, getJsonRequest());
        itemListRequest.setShouldCache(false);
        itemListRequest.setHeader(getHeaders());
        return itemListRequest;
    }
}
